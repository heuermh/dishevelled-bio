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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.concurrent.Callable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.annotations.Beta;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.StreamListener;

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

import org.duckdb.DuckDBAppender;
import org.duckdb.DuckDBConnection;

/**
 * Convert DNA sequences in FASTQ format to Parquet format.
 *
 * Beta implementation to be merged into FastqToParquet after performance benchmarking.
 *
 * @since 2.4
 * @author  Michael Heuer
 */
@Beta
public final class FastqToParquet3 implements Callable<Integer> {
    private final Path fastqPath;
    private final File parquetFile;
    private final int rowGroupSize;
    private final long partitionSize;
    private final FastqReader fastqReader = new SangerFastqReader();
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    static final long DEFAULT_PARTITION_SIZE = DEFAULT_ROW_GROUP_SIZE * 10L;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE s%d (name VARCHAR, seq VARCHAR, qual VARCHAR)";
    private static final String CREATE_VIEW_SQL = "CREATE OR REPLACE VIEW reads AS SELECT name, upper(seq) AS sequence, qual AS quality, length(sequence) AS length FROM s%d";
    private static final String DROP_TABLE_SQL = "DROP TABLE s%d";
    private static final String COPY_SQL = "COPY reads TO '%s/part-%d-%d.parquet' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d)";
    private static final String USAGE = "dsh-fastq-to-parquet3 [args]";


    /**
     * Convert DNA sequences in FASTQ format to Parquet format.
     *
     * @param fastqPath input FASTQ path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param rowGroupSize row group size, must be greater than zero
     * @param partitionSize partition size, in number of rows per partitioned Parquet file, must be greater than zero
     */
    public FastqToParquet3(final Path fastqPath, final File parquetFile, final int rowGroupSize, final long partitionSize) {
        checkNotNull(parquetFile);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        checkArgument(partitionSize > 0, "partition size must be greater than zero");
        this.fastqPath = fastqPath;
        this.parquetFile = parquetFile;
        this.rowGroupSize = rowGroupSize;
        this.partitionSize = partitionSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        final AtomicLong rows = new AtomicLong();
        final AtomicLong firstRow = new AtomicLong();
        final AtomicReference<Statement> statement = new AtomicReference<Statement>();
        final AtomicReference<DuckDBAppender> appender = new AtomicReference<DuckDBAppender>();
        final AtomicReference<DuckDBConnection> connection = new AtomicReference<DuckDBConnection>();
        try {
            reader = reader(fastqPath);
            parquetFile.mkdirs();

            connection.set((DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:"));
            statement.set(connection.get().createStatement());

            try {
                statement.get().execute(String.format(CREATE_TABLE_SQL, firstRow.get()));
                appender.set(connection.get().createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("s%d", firstRow.get())));

                fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        try {
                            appender.get().beginRow();
                            appender.get().append(fastq.getDescription());
                            appender.get().append(fastq.getSequence());
                            appender.get().append(fastq.getQuality());
                            appender.get().endRow();

                            rows.incrementAndGet();
                            if ((rows.get() % partitionSize) == 0) {
                                try {
                                    appender.get().close();
                                }
                                catch (Exception e) {
                                    // ignore
                                }
                                statement.get().execute(String.format(CREATE_VIEW_SQL, firstRow.get()));
                                statement.get().execute(String.format(COPY_SQL, parquetFile, firstRow.get(), rows.get(), rowGroupSize));
                                statement.get().execute(String.format(DROP_TABLE_SQL, firstRow.get()));

                                firstRow.set(rows.get() + 1);
                                statement.get().execute(String.format(CREATE_TABLE_SQL, firstRow.get()));
                                appender.set(connection.get().createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("s%d", firstRow.get())));
                            }
                        }
                        catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            catch (Exception e) {
                throw e;
            }
            finally {
                try {
                    appender.get().close();
                }
                catch (Exception e) {
                    // ignore
                }
            }

            statement.get().execute(String.format(CREATE_VIEW_SQL, firstRow.get()));
            statement.get().execute(String.format(COPY_SQL, parquetFile, firstRow.get(), rows.get(), rowGroupSize));

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
                statement.get().close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                connection.get().close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {

        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument fastqPath = new PathArgument("i", "input-fastq-path", "input FASTQ path, default stdin", false);
        FileArgument parquetFile = new FileArgument("o", "output-parquet-file", "output Parquet file, will be created as a directory, overwriting if necessary", true);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);
        LongArgument partitionSize = new LongArgument("p", "partition-size", "partition size, default " + DEFAULT_PARTITION_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, fastqPath, parquetFile, rowGroupSize, partitionSize);
        CommandLine commandLine = new CommandLine(args);

        FastqToParquet3 fastqToParquet = null;
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
            fastqToParquet = new FastqToParquet3(fastqPath.getValue(), parquetFile.getValue(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE), partitionSize.getValue(DEFAULT_PARTITION_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(fastqToParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
