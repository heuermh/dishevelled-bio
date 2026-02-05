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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
import org.dishevelled.commandline.argument.LongArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.StringListArgument;

import org.duckdb.DuckDBAppender;
import org.duckdb.DuckDBConnection;

/**
 * Convert variants in VCF format to partitioned Parquet format.
 *
 * @since 3.1
 * @author  Michael Heuer
 */
public final class VcfToPartitionedParquet implements Callable<Integer> {
    private final Path vcfPath;
    private final File parquetFile;
    private final List<String> infoFields;
    private final String infoPrefix;
    private final List<String> samples;
    private final String samplePrefix;
    private final List<String> formatFields;
    private final boolean lowercase;
    private final boolean multiallelic;
    private final int rowGroupSize;
    private final long partitionSize;
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    static final String DEFAULT_INFO_PREFIX = "";
    static final String DEFAULT_SAMPLE_PREFIX = "";
    static final long DEFAULT_PARTITION_SIZE = DEFAULT_ROW_GROUP_SIZE * 10L;
    static final List<String> EMPTY_LIST = Collections.emptyList();
    private static final String CREATE_TABLE_SQL_PREFIX = "CREATE TABLE v%d (chrom VARCHAR, pos LONG, ref VARCHAR, alt VARCHAR, qual DOUBLE, filters_applied BOOLEAN, filters_passed BOOLEAN, filters_failed VARCHAR[]";
    private static final String CREATE_TABLE_SQL_PREFIX_MULTIALLELIC = "CREATE TABLE v%d (chrom VARCHAR, pos LONG, ref VARCHAR, alt VARCHAR[], qual DOUBLE, filters_applied BOOLEAN, filters_passed BOOLEAN, filters_failed VARCHAR[]";
    private static final String CREATE_TABLE_SQL_SUFFIX = ")";
    private static final String DROP_TABLE_SQL = "DROP TABLE v%d";
    private static final String COPY_SQL = "COPY v%d TO '%s/part-%d-%d.parquet' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d)";
    private static final String USAGE = "dsh-vcf-to-partitioned-parquet [args]";


    /**
     * Convert variants in VCF format to partitioned Parquet format.
     *
     * @param vcfPath input VCF path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param rowGroupSize row group size, must be greater than zero
     * @param partitionSize partition size, in number of rows per partitioned Parquet file, must be greater than zero
     */
    public VcfToPartitionedParquet(final Path vcfPath, final File parquetFile, final int rowGroupSize, final long partitionSize) {
        this(vcfPath, parquetFile, EMPTY_LIST, DEFAULT_INFO_PREFIX, EMPTY_LIST, DEFAULT_SAMPLE_PREFIX, EMPTY_LIST, true, false, rowGroupSize, partitionSize);
    }

    /**
     * Convert variants in VCF format to partitioned Parquet format.
     *
     * @since 3.2
     * @param vcfPath input VCF path, if any
     * @param parquetFile output Parquet file, will be created as a directory, overwriting if necessary
     * @param infoFields list of INFO fields, may be empty but must not be null
     * @param infoPrefix info prefix, may be empty but must not be null
     * @param samples list of samples, may be empty but must not be null
     * @param samplePrefix sample prefix, may be empty but must not be null
     * @param formatFields list of FORMAT fields, may be empty but must not be null
     * @param lowercase true to lowercase fields and samples for column names
     * @param multiallelic true to allow multiallelic records
     * @param rowGroupSize row group size, must be greater than zero
     * @param partitionSize partition size, in number of rows per partitioned Parquet file, must be greater than zero
     */
    public VcfToPartitionedParquet(final Path vcfPath,
                                   final File parquetFile,
                                   final List<String> infoFields,
                                   final String infoPrefix,
                                   final List<String> samples,
                                   final String samplePrefix,
                                   final List<String> formatFields,
                                   final boolean lowercase,
                                   final boolean multiallelic,
                                   final int rowGroupSize,
                                   final long partitionSize) {
        checkNotNull(parquetFile);
        checkNotNull(infoFields);
        checkNotNull(infoPrefix);
        checkNotNull(samples);
        checkNotNull(samplePrefix);
        checkNotNull(formatFields);
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        checkArgument(partitionSize > 0, "partition size must be greater than zero");
        this.vcfPath = vcfPath;
        this.parquetFile = parquetFile;
        this.infoFields = infoFields;
        this.infoPrefix = infoPrefix;
        this.samples = samples;
        this.samplePrefix = samplePrefix;
        this.formatFields = formatFields;
        this.lowercase = lowercase;
        this.multiallelic = multiallelic;
        this.rowGroupSize = rowGroupSize;
        this.partitionSize = partitionSize;
    }


    /**
     * Create and return the CREATE TABLE SQL.
     *
     * @param firstRow first row
     * @return the CREATE TABLE SQL
     */
    private String createTableSql(final long firstRow) {
        StringBuilder sb = new StringBuilder();
        sb.append(multiallelic ? String.format(CREATE_TABLE_SQL_PREFIX_MULTIALLELIC, firstRow) : String.format(CREATE_TABLE_SQL_PREFIX, firstRow));
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

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        final AtomicLong rows = new AtomicLong();
        final AtomicLong firstRow = new AtomicLong();
        final AtomicReference<Statement> statement = new AtomicReference<Statement>();
        final AtomicReference<DuckDBAppender> appender = new AtomicReference<DuckDBAppender>();
        final AtomicReference<DuckDBConnection> connection = new AtomicReference<DuckDBConnection>();
        try {
            reader = reader(vcfPath);
            parquetFile.mkdirs();

            connection.set((DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:"));
            statement.set(connection.get().createStatement());

            try {
                statement.get().execute(createTableSql(firstRow.get()));
                appender.set(connection.get().createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("v%d", firstRow.get())));

                VcfParser.parse(reader, new VcfParseAdapter() {
                        private Map<String, String[]> infoValues = new HashMap<String, String[]>();
                        private Table<String, String, String[]> formatValues = HashBasedTable.create();

                        @Override
                        public void lineNumber(final long lineNumber) throws IOException {
                            try {
                                infoValues.clear();
                                formatValues.clear();
                                appender.get().beginRow();
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void chrom(final String chrom) throws IOException {
                            try {
                                appender.get().append(chrom);
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void pos(final long pos) throws IOException {
                            try {
                                appender.get().append(pos);
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }
                        }

                        @Override
                        public void ref(final String ref) throws IOException {
                            try {
                                appender.get().append(ref);
                            }
                            catch (SQLException e) {
                                throw new IOException(e);
                            }

                        }

                        @Override
                        public void alt(final String... alt) throws IOException {
                            try {
                                if (multiallelic) {
                                    appender.get().append(Arrays.asList(alt));
                                }
                                else {
                                    if (alt.length == 0) {
                                        appender.get().append("");
                                    }
                                    else if (alt.length == 1) {
                                        appender.get().append(alt[0]);
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
                                    appender.get().append((Double) null);
                                }
                                else {
                                    appender.get().append(qual);
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
                                    appender.get().append(false);
                                    appender.get().append(false);
                                    appender.get().append(EMPTY_LIST);
                                }
                                else if (filter.length == 1) {
                                    if ("PASS".equals(filter[0])) {
                                        appender.get().append(true);
                                        appender.get().append(true);
                                        appender.get().append(EMPTY_LIST);
                                    }
                                    else {
                                        appender.get().append(true);
                                        appender.get().append(false);
                                        appender.get().append(Arrays.asList(filter));
                                    }
                                }
                                else {
                                    appender.get().append(true);
                                    appender.get().append(false);
                                    appender.get().append(Arrays.asList(filter));
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
                                        appender.get().append(Arrays.asList(infoValues.get(infoField)));
                                    }
                                    else {
                                        appender.get().append(EMPTY_LIST);
                                    }
                                }
                                for (String sample : samples) {
                                    for (String formatField : formatFields) {
                                        if (formatValues.contains(sample, formatField)) {
                                            appender.get().append(Arrays.asList(formatValues.get(sample, formatField)));
                                        }
                                        else {
                                            appender.get().append(EMPTY_LIST);
                                        }
                                    }
                                }
                                appender.get().endRow();

                                rows.incrementAndGet();
                                if ((rows.get() % partitionSize) == 0) {
                                    try {
                                        appender.get().close();
                                    }
                                    catch (Exception e) {
                                        // ignore
                                    }
                                    statement.get().execute(String.format(COPY_SQL, firstRow.get(), parquetFile, firstRow.get(), rows.get(), rowGroupSize));
                                    statement.get().execute(String.format(DROP_TABLE_SQL, firstRow.get()));

                                    firstRow.set(rows.get() + 1);
                                    statement.get().execute(createTableSql(firstRow.get()));
                                    appender.set(connection.get().createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("v%d", firstRow.get())));
                                }
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
                    appender.get().close();
                }
                catch (Exception e) {
                    // ignore
                }
            }
            statement.get().execute(String.format(COPY_SQL, firstRow.get(), parquetFile, firstRow.get(), rows.get(), rowGroupSize));

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
        PathArgument vcfPath = new PathArgument("i", "input-vcf-path", "input VCF path, default stdin", false);
        FileArgument parquetFile = new FileArgument("o", "output-parquet-file", "output Parquet file, will be created as a directory, overwriting if necessary", true);
        StringListArgument infoFields = new StringListArgument("n", "info-fields", "list of INFO fields to include", false);
        StringArgument infoPrefix = new StringArgument("r", "info-prefix", "info prefix, default \"\"", false);
        StringListArgument samples = new StringListArgument("s", "samples", "list of samples to include", false);
        StringArgument samplePrefix = new StringArgument("x", "sample-prefix", "sample prefix, default \"\"", false);
        StringListArgument formatFields = new StringListArgument("f", "format-fields", "list of FORMAT fields to include", false);
        Switch lowercase = new Switch("w", "lowercase", "lowercase fields and samples for column names");
        Switch multiallelic = new Switch("m", "multiallelic", "allow multiallelic records");
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);
        LongArgument partitionSize = new LongArgument("p", "partition-size", "partition size, default " + DEFAULT_PARTITION_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, vcfPath, parquetFile, infoFields, infoPrefix, samples, samplePrefix, formatFields, lowercase, multiallelic, rowGroupSize, partitionSize);
        CommandLine commandLine = new CommandLine(args);

        VcfToPartitionedParquet vcfToPartitionedParquet = null;
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
            vcfToPartitionedParquet = new VcfToPartitionedParquet(vcfPath.getValue(), parquetFile.getValue(), infoFields.getValue(EMPTY_LIST), infoPrefix.getValue(DEFAULT_INFO_PREFIX), samples.getValue(EMPTY_LIST), samplePrefix.getValue(DEFAULT_SAMPLE_PREFIX), formatFields.getValue(EMPTY_LIST), lowercase.wasFound(), multiallelic.wasFound(), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE), partitionSize.getValue(DEFAULT_PARTITION_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(vcfToPartitionedParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
