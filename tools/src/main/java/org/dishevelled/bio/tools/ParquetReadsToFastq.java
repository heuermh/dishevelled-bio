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

import static org.dishevelled.compress.Writers.writer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.concurrent.Callable;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;
import org.biojava.bio.program.fastq.FastqVariant;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Convert reads in Parquet format to FASTQ format.
 *
 * @since 3.1
 * @author  Michael Heuer
 */
public final class ParquetReadsToFastq implements Callable<Integer> {
    private final String parquetPath;
    private final File fastqFile;
    // todo: flag for sorting first?
    private static final String READ_SQL = "SELECT name, sequence, quality FROM read_parquet('%s')";
    private static final String USAGE = "dsh-parquet-reads-to-fastq [args]";

    /**
     * Convert reads in Parquet format to FASTQ format.
     *
     * @param parquetPath input Parquet path, must not be null
     * @param fastqFile output FASTQ file, if any
     */
    public ParquetReadsToFastq(final String parquetPath, final File fastqFile) {
        checkNotNull(parquetPath);
        this.parquetPath = parquetPath;
        this.fastqFile = fastqFile;
    }

    @Override
    public Integer call() throws Exception {
        final FastqBuilder fastqBuilder = new FastqBuilder();
        final FastqWriter fastqWriter = new SangerFastqWriter();
        try (PrintWriter writer = writer(fastqFile)) {
            Class.forName("org.duckdb.DuckDBDriver");
            try (Connection connection = DriverManager.getConnection("jdbc:duckdb:")) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet resultSet = statement.executeQuery(String.format(READ_SQL, parquetPath))) {
                        while (resultSet.next()) {
                            Fastq fastq = fastqBuilder
                                .withVariant(FastqVariant.FASTQ_SANGER)
                                .withDescription(resultSet.getString(1))
                                .withSequence(resultSet.getString(2))
                                .withQuality(resultSet.getString(3))
                                .build();

                            fastqWriter.append(writer, fastq);
                        }
                    }
                }
            }
        }
        return 0;
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        StringArgument parquetPath = new StringArgument("i", "input-parquet-path", "input Parquet path", true);
        FileArgument fastqFile = new FileArgument("o", "output-fastq-file", "output FASTQ file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, parquetPath, fastqFile);
        CommandLine commandLine = new CommandLine(args);

        ParquetReadsToFastq parquetReadsToFastq = null;
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
            parquetReadsToFastq = new ParquetReadsToFastq(parquetPath.getValue(), fastqFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(parquetReadsToFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
