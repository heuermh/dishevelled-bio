/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.Statement;

import java.util.concurrent.Callable;

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
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.duckdb.DuckDBAppender;
import org.duckdb.DuckDBConnection;

/**
 * Convert DNA or protein sequences in FASTA format to Parquet format.
 *
 * @since 2.4
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class FastaToParquet implements Callable<Integer> {
    private final Path fastaPath;
    private final File parquetFile;
    private final String alphabet;
    private final int rowGroupSize;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    static final String DESCRIPTION_LINE = "description_line";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE sequences (name VARCHAR, description VARCHAR, sequence VARCHAR, length INTEGER, alphabet VARCHAR)";
    private static final String COPY_SQL = "COPY sequences TO '%s' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d, PER_THREAD_OUTPUT)";
    private static final String USAGE = "dsh-fasta-to-parquet [args]";


    /**
     * Convert DNA or protein sequences in FASTA format to Parquet format.
     *
     * @param fastaPath input FASTA path, if any
     * @param parquetFile output Parquet file, must not be null; created as a directory, overwriting if necessary
     * @param alphabet input FASTA path alphabet { dna, protein }, must not be null
     * @param rowGroupSize row group size, must be greater than zero
     */
    public FastaToParquet(final Path fastaPath, final File parquetFile, final String alphabet, final int rowGroupSize) {
        checkNotNull(parquetFile);
        checkNotNull(alphabet);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        this.fastaPath = fastaPath;
        this.parquetFile = parquetFile;
        this.alphabet = alphabet;
        this.rowGroupSize = rowGroupSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        DuckDBConnection connection = null;
        Statement statement = null;
        try {
            reader = reader(fastaPath);
            parquetFile.mkdirs();

            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
            statement = connection.createStatement();

            statement.execute(CREATE_TABLE_SQL);
            DuckDBAppender appender = null;
            try {
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "sequences");

                for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                    Sequence sequence = sequences.nextSequence();

                    appender.beginRow();
                    appender.append(sequence.getName());
                    appender.append(describeSequence(sequence));
                    appender.append(sequence.seqString().toUpperCase());
                    appender.append(sequence.length());
                    appender.append(isProteinAlphabet() ? "protein" : "dna");
                    appender.endRow();
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
            statement.execute(String.format(COPY_SQL, parquetFile, rowGroupSize));

            return 0;
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
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

    static String describeSequence(final Sequence sequence) {
        return sequence.getAnnotation().containsProperty(DESCRIPTION_LINE) ?
            (String) sequence.getAnnotation().getProperty(DESCRIPTION_LINE) : (String) sequence.getName();
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

        ArgumentList arguments = new ArgumentList(about, help, fastaPath, parquetFile, alphabet, rowGroupSize);
        CommandLine commandLine = new CommandLine(args);

        FastaToParquet fastaToParquet = null;
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
            fastaToParquet = new FastaToParquet(fastaPath.getValue(), parquetFile.getValue(), alphabet.getValue(DEFAULT_ALPHABET), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE));
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
