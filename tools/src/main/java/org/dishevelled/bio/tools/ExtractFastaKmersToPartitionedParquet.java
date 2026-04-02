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

import static org.dishevelled.compress.Readers.reader;

import java.io.BufferedReader;
import java.io.File;

import java.nio.file.Path;

import java.sql.DriverManager;
import java.sql.Statement;

import java.util.concurrent.Callable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.PathArgument;

import org.duckdb.DuckDBAppender;
import org.duckdb.DuckDBConnection;

/**
 * Extract kmers from DNA or protein sequences in FASTA format to partitioned Parquet format.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class ExtractFastaKmersToPartitionedParquet implements Callable<Integer> {
    private final Path fastaPath;
    private final File parquetFile;
    private final String alphabet;
    private final int kmerLength;
    private final boolean includeNs;
    private final int upstreamLength;
    private final int downstreamLength;
    private final int rowGroupSize;
    private final long partitionSize;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_ROW_GROUP_SIZE = 122880;
    static final long DEFAULT_PARTITION_SIZE = DEFAULT_ROW_GROUP_SIZE * 100L;
    private static final String CREATE_TABLE_SQL = "CREATE TABLE kmers%d (kmer VARCHAR, reference VARCHAR, start LONG, upstream VARCHAR, downstream VARCHAR, length INTEGER, alphabet VARCHAR)";
    private static final String DROP_TABLE_SQL = "DROP TABLE kmers%d";
    private static final String COPY_SQL = "COPY kmers%d TO '%s/part-%d-%d.parquet' (FORMAT 'parquet', COMPRESSION 'zstd', OVERWRITE_OR_IGNORE 1, ROW_GROUP_SIZE %d)";
    private static final String USAGE = "dsh-extract-fasta-kmers-to-partitioned-parquet [args]";


    /**
     * Extract kmers from DNA or protein sequences in FASTA format to partitioned Parquet format.
     *
     * @param fastaPath input FASTA path, if any
     * @param parquetFile output Parquet file, must not be null; created as a directory, overwriting if necessary
     * @param alphabet input FASTA file alphabet { dna, protein }, must not be null
     * @param kmerLength kmer length, must be at least 1
     * @param includeNs for DNA sequences, include kmers containing Ns
     * @param upstreamLength upstream reference sequence length to include, default 0
     * @param downstreamLength downstream reference sequence length to include, default 0
     * @param rowGroupSize row group size, must be greater than zero
     * @param partitionSize partition size, in number of rows per partitioned Parquet file, must be greater than zero
     */
    public ExtractFastaKmersToPartitionedParquet(final Path fastaPath,
                                                 final File parquetFile,
                                                 final String alphabet,
                                                 final int kmerLength,
                                                 final boolean includeNs,
                                                 final int upstreamLength,
                                                 final int downstreamLength,
                                                 final int rowGroupSize,
                                                 final long partitionSize) {

        checkNotNull(parquetFile);
        checkNotNull(alphabet);
        checkArgument(kmerLength >= 1, "kmer length must be at least 1");
        checkArgument(upstreamLength >= 0, "upstream length must be at least 0");
        checkArgument(downstreamLength >= 0, "downstream length must be at least 0");
        checkArgument(rowGroupSize > 0, "row group size must be greater than zero");
        checkArgument(partitionSize > 0, "partition size must be greater than zero");
        this.fastaPath = fastaPath;
        this.parquetFile = parquetFile;
        this.alphabet = alphabet;
        this.kmerLength = kmerLength;
        this.includeNs = includeNs;
        this.upstreamLength = upstreamLength;
        this.downstreamLength = downstreamLength;
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
            reader = reader(fastaPath);
            parquetFile.mkdirs();

            connection.set((DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:"));
            statement.set(connection.get().createStatement());

            try {
                statement.get().execute(String.format(CREATE_TABLE_SQL, firstRow.get()));
                appender.set(connection.get().createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("kmers%d", firstRow.get())));

                for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                    Sequence sequence = sequences.nextSequence();

                    if (sequence.length() > kmerLength) {
                        String name = sequence.getName();

                        // 0-based half-open coordinates
                        int lastStart = Math.max(0, sequence.length() - kmerLength);
                        for (int start = 0; start <= lastStart; start++) {

                            // 1-based fully closed coordinates
                            int kmerStart = start + 1;
                            int kmerEnd = start + kmerLength;
                            String kmer = sequence.subStr(kmerStart, kmerEnd).toUpperCase();

                            if (includeNs || !kmer.contains("N")) {
                                appender.get().beginRow();
                                appender.get().append(kmer);
                                appender.get().append(name);
                                // output 0-based half-open coordinates
                                appender.get().append((long) start);

                                if (upstreamLength > 0) {
                                    // 1-based fully closed coordinates
                                    int upstreamStart = Math.max(1, start + 1 - upstreamLength);
                                    int upstreamEnd = start;
                                    if (upstreamEnd >= upstreamStart) {
                                        appender.get().append(sequence.subStr(upstreamStart, upstreamEnd).toUpperCase());
                                    }
                                    else {
                                        appender.get().append("");
                                    }
                                }
                                if (downstreamLength > 0) {
                                    // 1-based fully closed coordinates
                                    int downstreamStart = Math.min(kmerEnd + 1, sequence.length() + 1);
                                    int downstreamEnd = Math.min(kmerEnd + downstreamLength, sequence.length());
                                    if (downstreamEnd >= downstreamStart) {
                                        appender.get().append(sequence.subStr(downstreamStart, downstreamEnd).toUpperCase());
                                    }
                                    else {
                                        appender.get().append("");
                                    }
                                }
                                appender.get().append(kmer.length());
                                appender.get().append(alphabet);
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
                                    statement.get().execute(String.format(CREATE_TABLE_SQL, firstRow.get()));
                                    appender.set(connection.get().createAppender(DuckDBConnection.DEFAULT_SCHEMA, String.format("kmers%d", firstRow.get())));
                                }
                            }
                        }
                    }
                }
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

    boolean isProteinAlphabet() {
        return alphabet != null && (alphabet.equalsIgnoreCase("protein") || alphabet.equalsIgnoreCase("aa"));
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {

        // install a signal handler to exit on SIGPIPE
        sun.misc.Signal.handle(new sun.misc.Signal("PIPE"), new sun.misc.SignalHandler() {
                @Override
                public void handle(final sun.misc.Signal signal) {
                    System.exit(0);
                }
            });

        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument fastaPath = new PathArgument("i", "input-fasta-path", "input FASTA path, default stdin", false);
        FileArgument parquetFile = new FileArgument("o", "output-parquet-file", "output Parquet file, will be created as a directory, overwriting if necessary", true);
        StringArgument alphabet = new StringArgument("e", "alphabet", "input FASTA alphabet { dna, protein }, default dna", false);
        IntegerArgument kmerLength = new IntegerArgument("k", "kmer-length", "kmer length", true);
        Switch includeNs = new Switch("n", "include-ns", "for DNA sequences, include kmers containing Ns");
        IntegerArgument upstreamLength = new IntegerArgument("u", "upstream-length", "upstream length, default 0", false);
        IntegerArgument downstreamLength = new IntegerArgument("d", "downstream-length", "downstream length, default 0", false);
        IntegerArgument rowGroupSize = new IntegerArgument("g", "row-group-size", "row group size, default " + DEFAULT_ROW_GROUP_SIZE, false);
        LongArgument partitionSize = new LongArgument("p", "partition-size", "partition size, default " + DEFAULT_PARTITION_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, fastaPath, parquetFile, alphabet, kmerLength, includeNs, upstreamLength, downstreamLength, rowGroupSize, partitionSize);
        CommandLine commandLine = new CommandLine(args);

        ExtractFastaKmersToPartitionedParquet extractFastaKmersToPartitionedParquet = null;
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
            extractFastaKmersToPartitionedParquet = new ExtractFastaKmersToPartitionedParquet(fastaPath.getValue(), parquetFile.getValue(), alphabet.getValue(DEFAULT_ALPHABET), kmerLength.getValue(), includeNs.wasFound(), upstreamLength.getValue(0), downstreamLength.getValue(0), rowGroupSize.getValue(DEFAULT_ROW_GROUP_SIZE), partitionSize.getValue(DEFAULT_PARTITION_SIZE));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractFastaKmersToPartitionedParquet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
