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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

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
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.StringListArgument;

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
    private final boolean multiallelic;
    private final List<String> infoFields;
    private final String infoPrefix;
    private final List<String> samples;
    private final String samplePrefix;
    private final List<String> formatFields;
    private final boolean lowercase;
    private final int rowGroupSize;
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    static final String DEFAULT_INFO_PREFIX = "";
    static final String DEFAULT_SAMPLE_PREFIX = "";
    static final List<String> EMPTY_LIST = Collections.emptyList();
    private static final String CREATE_TABLE_SQL_PREFIX = "CREATE TABLE variants (chrom VARCHAR, pos LONG, ref VARCHAR, alt VARCHAR, qual DOUBLE, filters_applied BOOLEAN, filters_passed BOOLEAN, filters_failed VARCHAR[]";
    private static final String CREATE_TABLE_SQL_PREFIX_MULTIALLELIC = "CREATE TABLE variants (chrom VARCHAR, pos LONG, ref VARCHAR, alt VARCHAR[], qual DOUBLE, filters_applied BOOLEAN, filters_passed BOOLEAN, filters_failed VARCHAR[]";
    private static final String CREATE_TABLE_SQL_SUFFIX = ")";
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
        this(vcfPath, parquetFile, false, EMPTY_LIST, DEFAULT_INFO_PREFIX, EMPTY_LIST, DEFAULT_SAMPLE_PREFIX, EMPTY_LIST, true, rowGroupSize);
    }

    /**
     * Convert variants in VCF format to Parquet format.
     *
     * @since 3.2
     * @param vcfPath input VCF path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param multiallelic true to allow multiallelic records
     * @param infoFields list of INFO fields, may be empty but must not be null
     * @param infoPrefix info prefix, may be empty but must not be null
     * @param samples list of samples, may be empty but must not be null
     * @param samplePrefix sample prefix, may be empty but must not be null
     * @param formatFields list of FORMAT fields, may be empty but must not be null
     * @param lowercase true to lowercase fields and samples for column names
     * @param rowGroupSize row group size, must be greater than zero
     */
    public VcfToParquet(final Path vcfPath,
                        final File parquetFile,
                        final boolean multiallelic,
                        final List<String> infoFields,
                        final String infoPrefix,
                        final List<String> samples,
                        final String samplePrefix,
                        final List<String> formatFields,
                        final boolean lowercase,
                        final int rowGroupSize) {
        checkNotNull(parquetFile);
        checkNotNull(infoFields);
        checkNotNull(infoPrefix);
        checkNotNull(samples);
        checkNotNull(samplePrefix);
        checkNotNull(formatFields);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        this.vcfPath = vcfPath;
        this.parquetFile = parquetFile;
        this.multiallelic = multiallelic;
        this.infoFields = infoFields;
        this.infoPrefix = infoPrefix;
        this.samples = samples;
        this.samplePrefix = samplePrefix;
        this.formatFields = formatFields;
        this.lowercase = lowercase;
        this.rowGroupSize = rowGroupSize;
    }


    /**
     * Create and return the CREATE TABLE SQL.
     *
     * @return the CREATE TABLE SQL
     */
    private String createTableSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(multiallelic ? CREATE_TABLE_SQL_PREFIX_MULTIALLELIC : CREATE_TABLE_SQL_PREFIX);
        for (String infoField : infoFields) {
            sb.append(", ");
            sb.append(infoPrefix);
            sb.append(lowercase ? infoField.toLowerCase() : infoField);
            sb.append(" VARCHAR[]");
        }
        for (String sample : samples) {
            for (String formatField : formatFields) {
                sb.append(", ");
                sb.append(samplePrefix);
                sb.append(lowercase ? sample.toLowerCase() : sample);
                sb.append("_");
                sb.append(lowercase ? formatField.toLowerCase() : formatField);
                sb.append(" VARCHAR[]");
            }
        }
        sb.append(CREATE_TABLE_SQL_SUFFIX);
        return sb.toString();
    }

    /**
     * Create and return the COPY SQL.
     *
     * @return the COPY SQL
     */
    private String copySql() {
        return String.format(COPY_SQL, parquetFile, rowGroupSize);
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

            statement.execute(createTableSql());
            DuckDBAppender appender = null;
            try {
                appender = connection.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "variants");

                final DuckDBAppender a = appender;
                VcfParser.parse(reader, new VcfParseAdapter() {
                        private Map<String, String[]> infoValues = new HashMap<String, String[]>();
                        private Table<String, String, String[]> formatValues = HashBasedTable.create();

                        @Override
                        public void lineNumber(final long lineNumber) throws IOException {
                            try {
                                infoValues.clear();
                                formatValues.clear();
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
                                if (multiallelic) {
                                    a.append(Arrays.asList(alt));
                                }
                                else {
                                    if (alt.length == 0) {
                                        a.append("");
                                    }
                                    else if (alt.length == 1) {
                                        a.append(alt[0]);
                                    }
                                    else {
                                        throw new IOException("multiallelic variants not supported, found alternate alleles " + alt);
                                    }
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
                                    a.append((Double) null);
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
                        public void filter(final String... filter) throws IOException {
                            try {
                                if (filter.length == 0) {
                                    a.append(false);
                                    a.append(false);
                                    a.append(EMPTY_LIST);
                                }
                                else if (filter.length == 1) {
                                    if ("PASS".equals(filter[0])) {
                                        a.append(true);
                                        a.append(true);
                                        a.append(EMPTY_LIST);
                                    }
                                    else {
                                        a.append(true);
                                        a.append(false);
                                        a.append(Arrays.asList(filter));
                                    }
                                }
                                else {
                                    a.append(true);
                                    a.append(false);
                                    a.append(Arrays.asList(filter));
                                }
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void info(final String infoId, final String... values) throws IOException {
                            if (infoFields.contains(infoId)) {
                                infoValues.put(infoId, values);
                            }
                        }

                        @Override
                        public void genotype(final String sampleId, final String formatId, final String... values) {
                            if (samples.contains(sampleId) && formatFields.contains(formatId)) {
                                formatValues.put(sampleId, formatId, values);
                            }
                        }

                        @Override
                        public boolean complete() throws IOException {
                            try {
                                for (String infoField : infoFields) {
                                    if (infoValues.containsKey(infoField)) {
                                        a.append(Arrays.asList(infoValues.get(infoField)));
                                    }
                                    else {
                                        a.append(EMPTY_LIST);
                                    }
                                }
                                for (String sample : samples) {
                                    for (String formatField : formatFields) {
                                        if (formatValues.contains(sample, formatField)) {
                                            a.append(Arrays.asList(formatValues.get(sample, formatField)));
                                        }
                                        else {
                                            a.append(EMPTY_LIST);
                                        }
                                    }
                                }
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
            statement.execute(copySql());

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
        Switch multiallelic = new Switch("m", "multiallelic", "allow multiallelic records");
        StringListArgument infoFields = new StringListArgument("n", "info-fields", "list of INFO fields to include", false);
        StringArgument infoPrefix = new StringArgument("p", "info-prefix", "info prefix, default \"\"", false);
        StringListArgument samples = new StringListArgument("s", "samples", "list of samples to include", false);
        StringArgument samplePrefix = new StringArgument("x", "sample-prefix", "sample prefix, default \"\"", false);
        StringListArgument formatFields = new StringListArgument("f", "format-fields", "list of FORMAT fields to include", false);
        Switch lowercase = new Switch("w", "lowercase", "lowercase fields and samples for column names");
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, vcfPath, parquetFile, multiallelic, infoFields, infoPrefix, samples, samplePrefix, formatFields, lowercase, rowGroupSize);
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
            vcfToParquet = new VcfToParquet(vcfPath.getValue(), parquetFile.getValue(), multiallelic.wasFound(), infoFields.getValue(EMPTY_LIST), infoPrefix.getValue(DEFAULT_INFO_PREFIX), samples.getValue(EMPTY_LIST), samplePrefix.getValue(DEFAULT_SAMPLE_PREFIX), formatFields.getValue(EMPTY_LIST), lowercase.wasFound(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE));
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
