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
import org.dishevelled.commandline.argument.PathArgument;

import org.duckdb.DuckDBAppender;
import org.duckdb.DuckDBConnection;

/**
 * Convert DNA sequences in FASTQ format to Parquet format.
 *
 * @since 3.1
 * @author  Michael Heuer
 */
public final class FastqToParquet implements Callable<Integer> {
    private final Path fastqPath;
    private final File parquetFile;
    private final int rowGroupSize;
    private final FastqReader fastqReader = new SangerFastqReader();
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE reads (name VARCHAR, sequence VARCHAR, quality VARCHAR)";
    private static final String COPY_SQL = "COPY reads TO '%s' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d, PER_THREAD_OUTPUT)";
    private static final String USAGE = "dsh-fastq-to-parquet [args]";


    /**
     * Convert DNA sequences in FASTQ format to Parquet format.
     *
     * @param fastqPath input FASTQ path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param rowGroupSize row group size, must be greater than zero
     */
    public FastqToParquet(final Path fastqPath, final File parquetFile, final int rowGroupSize) {
        checkNotNull(parquetFile);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        this.fastqPath = fastqPath;
        this.parquetFile = parquetFile;
        this.rowGroupSize = rowGroupSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        DuckDBConnection connection = null;
        Statement statement = null;
        try {
            reader = reader(fastqPath);

            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
            statement = connection.createStatement();

            statement.execute(CREATE_TABLE_SQL);
            DuckDBAppender appender = null;
            try {
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "reads");

                final DuckDBAppender a = appender;
                fastqReader.stream(reader, new StreamListener() {
                        @Override
                        public void fastq(final Fastq fastq) {
                            try {
                                a.beginRow();
                                a.append(fastq.getDescription());
                                a.append(fastq.getSequence());
                                a.append(fastq.getQuality());
                                a.endRow();
                            }
                            catch (SQLException e) {
                                throw new RuntimeException("could not append " + fastq.getDescription(), e);
                            }
                        }
                    });
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


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {

        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument fastqPath = new PathArgument("i", "input-fastq-path", "input FASTQ path, default stdin", false);
        FileArgument parquetFile = new FileArgument("o", "output-parquet-file", "output Parquet file", true);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, fastqPath, parquetFile, rowGroupSize);
        CommandLine commandLine = new CommandLine(args);

        FastqToParquet fastqToParquet = null;
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
            fastqToParquet = new FastqToParquet(fastqPath.getValue(), parquetFile.getValue(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE));
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
