/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2025 held jointly by the individual authors.

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.fsf.org/licensing/licenses/lgpl.html
    > http://www.opensource.org/licenses/lgpl-license.php

*/
package org.dishevelled.bio.tools;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.Statement;

import java.util.concurrent.Callable;

import com.google.common.annotations.Beta;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.LongArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.duckdb.DuckDBAppender;
import org.duckdb.DuckDBConnection;

/**
 * Convert DNA or protein sequences in FASTA format to Parquet format.
 *
 * Beta implementation to be merged into FastaToParquet after performance benchmarking.
 *
 * @since 2.4
 * @author  Michael Heuer
 */
@Beta
@SuppressWarnings("deprecation")
public final class FastaToParquet3 implements Callable<Integer> {
    private final Path fastaPath;
    private final File parquetFile;
    private final String alphabet;
    private final int rowGroupSize;
    private final long partitionSize;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    static final long DEFAULT_PARTITION_SIZE = DEFAULT_ROW_GROUP_SIZE * 10L;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE s%d (name VARCHAR, seq VARCHAR)";
    private static final String CREATE_VIEW_SQL = "CREATE OR REPLACE VIEW sequences AS SELECT name, upper(seq) AS sequence, length(sequence) AS length, '%s' AS alphabet FROM s%d";
    private static final String DROP_TABLE_SQL = "DROP TABLE s%d";
    private static final String COPY_SQL = "COPY sequences TO '%s/part-%d-%d.parquet' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d)";
    private static final String USAGE = "dsh-fasta-to-parquet3 [args]";


    /**
     * Convert DNA or protein sequences in FASTA format to Parquet format.
     *
     * @param fastaPath input FASTA path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param alphabet input FASTA file alphabet { dna, protein }, if any
     * @param rowGroupSize row group size, must be greater than zero
     * @param partitionSize partition size, in number of rows per partitioned Parquet file, must be greater than zero
     */
    public FastaToParquet3(final Path fastaPath, final File parquetFile, final String alphabet, final int rowGroupSize, final long partitionSize) {
        checkNotNull(parquetFile);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        checkArgument(partitionSize > 0, "partition size must be greater than zero");
        this.fastaPath = fastaPath;
        this.parquetFile = parquetFile;
        this.alphabet = alphabet;
        this.rowGroupSize = rowGroupSize;
        this.partitionSize = partitionSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        DuckDBConnection connection = null;
        Statement statement = null;
        long rows = 0L;
        long firstRow = 0L;
        try {
            reader = reader(fastaPath);
            parquetFile.mkdirs();

            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
            statement = connection.createStatement();

            DuckDBAppender appender = null;
            try {

                statement.execute(String.format(CREATE_TABLE_SQL, firstRow));
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("s%d", firstRow));

                for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                    Sequence sequence = sequences.nextSequence();

                    appender.beginRow();
                    appender.append(sequence.getName());
                    appender.append(sequence.seqString());
                    appender.endRow();

                    rows++;
                    if ((rows % partitionSize) == 0) {
                        try {
                            if (appender != null) {
                                appender.close();
                            }
                        }
                        catch (Exception e) {
                            // ignore
                        }
                        statement.execute(String.format(CREATE_VIEW_SQL, isProteinAlphabet() ? "protein" : "dna", firstRow));
                        statement.execute(String.format(COPY_SQL, parquetFile, firstRow, rows, rowGroupSize));
                        statement.execute(String.format(DROP_TABLE_SQL, firstRow));

                        firstRow = rows + 1;
                        statement.execute(String.format(CREATE_TABLE_SQL, firstRow));
                        appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("s%d", firstRow));
                    }
                }
            }
            catch (Exception e) {
                throw e;
            }
            finally {
                try {
                    if (appender != null) {
                        appender.close();
                    }
                }
                catch (Exception e) {
                    // ignore
                }
            }

            statement.execute(String.format(CREATE_VIEW_SQL, isProteinAlphabet() ? "protein" : "dna", firstRow));
            statement.execute(String.format(COPY_SQL, parquetFile, firstRow, rows, rowGroupSize));

            return 0;
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (Exception e) {
                // ignore
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    boolean isProteinAlphabet() {
        return alphabet != null && (alphabet.equalsIgnoreCase("protein") || alphabet.equalsIgnoreCase("aa"));
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {

        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument fastaPath = new PathArgument("i", "input-fasta-path", "input FASTA path, default stdin", false);
        FileArgument parquetFile = new FileArgument("o", "output-parquet-file", "output Parquet file, will be created as a directory, overwriting if necessary", true);
        StringArgument alphabet = new StringArgument("e", "alphabet", "input FASTA alphabet { dna, protein }, default dna", false);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);
        LongArgument partitionSize = new LongArgument("p", "partition-size", "partition size, default " + DEFAULT_PARTITION_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, fastaPath, parquetFile, alphabet, rowGroupSize, partitionSize);
        CommandLine commandLine = new CommandLine(args);

        FastaToParquet3 fastaToParquet = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            fastaToParquet = new FastaToParquet3(fastaPath.getValue(), parquetFile.getValue(), alphabet.getValue(DEFAULT_ALPHABET), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE), partitionSize.getValue(DEFAULT_PARTITION_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(fastaToParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
