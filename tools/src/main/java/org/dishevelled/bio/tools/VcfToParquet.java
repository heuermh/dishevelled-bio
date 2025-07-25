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
import java.io.IOException;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;

import org.dishevelled.bio.variant.vcf.VcfParseAdapter;
import org.dishevelled.bio.variant.vcf.VcfParser;

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
 * Convert variants in VCF format to Parquet format.
 *
 * @since 3.1
 * @author  Michael Heuer
 */
public final class VcfToParquet implements Callable<Integer> {
    private final Path vcfPath;
    private final File parquetFile;
    private final int rowGroupSize;
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE variants (chrom VARCHAR, pos LONG, ref VARCHAR, alt VARCHAR, qual DOUBLE)";
    private static final String COPY_SQL = "COPY variants TO '%s' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d, PER_THREAD_OUTPUT)";
    private static final String USAGE = "dsh-vcf-to-parquet [args]";


    /**
     * Convert variants in VCF format to Parquet format.
     *
     * @param vcfPath input VCF path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param rowGroupSize row group size, must be greater than zero
     */
    public VcfToParquet(final Path vcfPath, final File parquetFile, final int rowGroupSize) {
        checkNotNull(parquetFile);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        this.vcfPath = vcfPath;
        this.parquetFile = parquetFile;
        this.rowGroupSize = rowGroupSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        DuckDBConnection connection = null;
        Statement statement = null;
        try {
            reader = reader(vcfPath);

            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
            statement = connection.createStatement();

            statement.execute(CREATE_TABLE_SQL);
            DuckDBAppender appender = null;
            try {
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "variants");

                final DuckDBAppender a = appender;
                VcfParser.parse(reader, new VcfParseAdapter() {
                        @Override
                        public void lineNumber(final long lineNumber) throws IOException {
                            try {
                                a.beginRow();
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void chrom(final String chrom) throws IOException {
                            try {
                                a.append(chrom);
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void pos(final long pos) throws IOException {
                            try {
                                a.append(pos);
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void ref(final String ref) throws IOException {
                            try {
                                a.append(ref);
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }

                        }

                        @Override
                        public void alt(final String... alt) throws IOException {
                            try {
                                if (alt.length == 0) {
                                    a.append("");
                                }
                                else if (alt.length == 1) {
                                    a.append(alt[0]);
                                }
                                else {
                                    a.append(Joiner.on(",").join(alt));
                                }
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                                
                        }

                        @Override
                        public void qual(final Double qual) throws IOException {
                            try {
                                if (qual == null) {
                                    a.append((String) null);
                                }
                                else {
                                    a.append(qual);
                                }
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }

                        }

                        @Override
                        public boolean complete() throws IOException {
                            try {
                                a.endRow();
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                            return true;
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
        PathArgument vcfPath = new PathArgument("i", "input-vcf-path", "input VCF path, default stdin", false);
        FileArgument parquetFile = new FileArgument("o", "output-parquet-file", "output Parquet file", true);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, vcfPath, parquetFile, rowGroupSize);
        CommandLine commandLine = new CommandLine(args);

        VcfToParquet vcfToParquet = null;
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
            vcfToParquet = new VcfToParquet(vcfPath.getValue(), parquetFile.getValue(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(vcfToParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
