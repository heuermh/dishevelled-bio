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

import static org.dishevelled.bio.protein.uniprot.UniprotEntryFeatureReader.stream;

import static org.dishevelled.compress.Readers.reader;

import java.io.BufferedReader;
import java.io.File;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.concurrent.Callable;

import org.dishevelled.bio.protein.uniprot.EntryFeature;
import org.dishevelled.bio.protein.uniprot.EntryFeatureListener;

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
 * Extract protein features from UniProt entries in XML format to Parquet format.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public final class ExtractUniprotFeaturesToParquet implements Callable<Integer> {
    private final Path uniprotXmlPath;
    private final File featureParquetFile;
    private final int rowGroupSize;
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE features (accession VARCHAR, description VARCHAR, evidence VARCHAR, ref VARCHAR, \"type\" VARCHAR, original VARCHAR, variations VARCHAR, begin_status CHAR, begin INT, end_status CHAR, \"end\" INT, position_status CHAR, position INT, location_sequence VARCHAR, ligand VARCHAR, ligand_part VARCHAR)";
    private static final String COPY_SQL = "COPY features TO '%s' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d, PER_THREAD_OUTPUT)";;
    private static final String USAGE = "dsh-extract-uniprot-features-to-parquet [args]";


    /**
     * Extract protein features from UniProt entries in XML format to Parquet format.
     *
     * @param uniprotXmlPath UniProt XML path, if any
     * @param featureParquetFile feature Parquet file, must not be null
     * @param rowGroupSize row group size, must be greater than zero
     */
    public ExtractUniprotFeaturesToParquet(final Path uniprotXmlPath, final File featureParquetFile, final int rowGroupSize) {
        checkNotNull(featureParquetFile);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        this.uniprotXmlPath = uniprotXmlPath;
        this.featureParquetFile = featureParquetFile;
        this.rowGroupSize = rowGroupSize;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        DuckDBConnection connection = null;
        Statement statement = null;
        try {
            reader = reader(uniprotXmlPath);
            featureParquetFile.mkdirs();

            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
            statement = connection.createStatement();

            statement.execute(CREATE_TABLE_SQL);
            DuckDBAppender appender = null;
            try {
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "features");

                final DuckDBAppender a = appender;
                EntryFeatureListener callback = new EntryFeatureListener() {
                        @Override
                        public boolean entryFeature(final EntryFeature f) {
                            try {
                                a.beginRow();
                                a.append(f.getAccession());
                                a.append(f.getDescription());
                                a.append(f.getEvidence());
                                a.append(f.getRef());
                                a.append(f.getType());
                                a.append(f.getOriginal());
                                a.append(f.getVariations());
                                a.append((f.getLocation().getBegin() == null) ? null : f.getLocation().getBegin().getStatus().getSymbol());

                                // ick.
                                Integer begin = (f.getLocation().getBegin() == null) ? null : f.getLocation().getBegin().getPosition();
                                if (begin != null) {
                                    a.append(begin);
                                }
                                else {
                                    a.append((String) null);
                                }
                                a.append((f.getLocation().getEnd() == null) ? null : f.getLocation().getEnd().getStatus().getSymbol());

                                Integer end = (f.getLocation().getEnd() == null) ? null : f.getLocation().getEnd().getPosition();
                                if (end != null) {
                                    a.append(end);
                                }
                                else {
                                    a.append((String) null);
                                }
                                a.append((f.getLocation().getPosition() == null) ? null : f.getLocation().getPosition().getStatus().getSymbol());

                                Integer position = (f.getLocation().getPosition() == null) ? null : f.getLocation().getPosition().getPosition();
                                if (position != null) {
                                    a.append(position);
                                }
                                else {
                                    a.append((String) null);
                                }

                                a.append(f.getLocation().getSequence());
                                a.append(f.getLigand());
                                a.append(f.getLigandPart());
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
            statement.execute(String.format(COPY_SQL, featureParquetFile, rowGroupSize));

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
        FileArgument featureParquetFile = new FileArgument("o", "output-feature-file", "output feature Parquet file", true);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, uniprotXmlPath, featureParquetFile, rowGroupSize);
        CommandLine commandLine = new CommandLine(args);

        ExtractUniprotFeaturesToParquet extractUniprotFeaturesToParquet = null;
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
            extractUniprotFeaturesToParquet = new ExtractUniprotFeaturesToParquet(uniprotXmlPath.getValue(), featureParquetFile.getValue(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractUniprotFeaturesToParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
