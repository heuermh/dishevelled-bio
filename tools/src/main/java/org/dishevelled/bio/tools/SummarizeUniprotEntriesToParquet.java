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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.protein.uniprot.UniprotEntrySummaryReader.stream;

import static org.dishevelled.compress.Readers.reader;

import java.io.BufferedReader;
import java.io.File;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.concurrent.Callable;

import org.dishevelled.bio.protein.uniprot.EntrySummary;
import org.dishevelled.bio.protein.uniprot.EntrySummaryListener;

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
 * Summarize UniProt entries in XML format to Parquet format.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public final class SummarizeUniprotEntriesToParquet implements Callable<Integer> {
    private final Path uniprotXmlPath;
    private final File summaryParquetFile;
    private final int rowGroupSize;
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE summary (organism VARCHAR, organism_id VARCHAR, lineage VARCHAR, \"type\" VARCHAR, reviewed BOOLEAN, unreviewed BOOLEAN, has_structure BOOLEAN)";
    private static final String COPY_SQL = "COPY summary TO '%s' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d, PER_THREAD_OUTPUT)";
    private static final String USAGE = "dsh-summarize-uniprot-entries-to-parquet [args]";


    /**
     * Summarize UniProt entries in XML format to Parquet format.
     *
     * @param uniprotXmlPath UniProt XML path, if any
     * @param summaryParquetFile summary Parquet file, must not be null
     * @param rowGroupSize row group size, must be greater than zero
     */
    public SummarizeUniprotEntriesToParquet(final Path uniprotXmlPath, final File summaryParquetFile, final int rowGroupSize) {
        checkNotNull(summaryParquetFile);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        this.uniprotXmlPath = uniprotXmlPath;
        this.summaryParquetFile = summaryParquetFile;
        this.rowGroupSize = rowGroupSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        DuckDBConnection connection = null;
        Statement statement = null;
        try {
            reader = reader(uniprotXmlPath);
            summaryParquetFile.mkdirs();

            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
            statement = connection.createStatement();

            statement.execute(CREATE_TABLE_SQL);
            DuckDBAppender appender = null;
            try {
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "summary");

                final DuckDBAppender a = appender;
                EntrySummaryListener callback = new EntrySummaryListener() {
                        @Override
                        public boolean entrySummary(final EntrySummary e) {
                            try {
                                a.beginRow();
                                a.append(e.getOrganism());
                                a.append(e.getOrganismId());
                                a.append(e.getLineage());
                                a.append(e.getType());
                                a.append(e.isReviewed());
                                a.append(e.isUnreviewed());
                                a.append(e.hasStructure());
                                a.endRow();

                                return true;
                            }
                            catch (SQLException ex) {
                                throw new RuntimeException("could not append row", ex);
                            }
                        }
                    };

                stream(reader, callback);
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
            statement.execute(String.format(COPY_SQL, summaryParquetFile, rowGroupSize));

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

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument uniprotXmlPath = new PathArgument("i", "input-uniprot-xml-path", "input UniProt XML path, default stdin", false);
        FileArgument summaryParquetFile = new FileArgument("o", "output-summary-parquet-file", "output summary Parquet file", true);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, uniprotXmlPath, summaryParquetFile, rowGroupSize);
        CommandLine commandLine = new CommandLine(args);

        SummarizeUniprotEntriesToParquet summarizeUniprotEntriesToParquet = null;
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
            summarizeUniprotEntriesToParquet = new SummarizeUniprotEntriesToParquet(uniprotXmlPath.getValue(), summaryParquetFile.getValue(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(summarizeUniprotEntriesToParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
