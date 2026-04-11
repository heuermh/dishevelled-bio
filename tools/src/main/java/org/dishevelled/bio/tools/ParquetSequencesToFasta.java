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

import static org.dishevelled.compress.Writers.writer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.concurrent.Callable;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Convert DNA or protein sequences in Parquet format to FASTA format.
 *
 * @since 3.1
 * @author  Michael Heuer
 */
public final class ParquetSequencesToFasta implements Callable<Integer> {
    private final String parquetPath;
    private final File fastaFile;
    private final int lineWidth;
    static final int DEFAULT_LINE_WIDTH = 70;
    private static final String READ_SQL = "SELECT description, sequence FROM read_parquet('%s')";
    private static final String USAGE = "dsh-parquet-sequences-to-fasta [args]";


    /**
     * Convert DNA or protein sequences in Parquet format to FASTA format.
     *
     * @param parquetPath input Parquet path, must not be null
     * @param fastaFile output FASTA file, if any
     * @param lineWidth line width
     */
    public ParquetSequencesToFasta(final String parquetPath, final File fastaFile, final int lineWidth) {
        checkNotNull(parquetPath);
        this.parquetPath = parquetPath;
        this.fastaFile = fastaFile;
        this.lineWidth = lineWidth;
    }

    @Override
    public Integer call() throws Exception {
        try (PrintWriter writer = writer(fastaFile)) {
            Class.forName("org.duckdb.DuckDBDriver");
            try (Connection connection = DriverManager.getConnection("jdbc:duckdb:")) {
                try (Statement statement = connection.createStatement()) {
                    // todo: if parquetPath starts with s3 load httpfs and credentials
                    try (ResultSet resultSet = statement.executeQuery(String.format(READ_SQL, parquetPath))) {
                        while (resultSet.next()) {
                            writeSequence(resultSet.getString(1), resultSet.getString(2), lineWidth, writer);
                        }
                    }
                }
            }
        }
        return 0;
    }

    static void writeSequence(final String description, final String sequence, final int lineWidth, final PrintWriter writer) {
        writer.print(">");
        writer.println(description);
        for (int i = 0, length = sequence.length(); i <= length; i += lineWidth) {
            writer.println(sequence.substring(i, Math.min(i + lineWidth, length)));
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
        StringArgument parquetPath = new StringArgument("i", "input-parquet-path", "input Parquet path", true);
        FileArgument fastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);
        ArgumentList arguments = new ArgumentList(about, help, parquetPath, fastaFile, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        ParquetSequencesToFasta parquetSequencesToFasta = null;
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
            parquetSequencesToFasta = new ParquetSequencesToFasta(parquetPath.getValue(), fastaFile.getValue(), lineWidth.getValue(DEFAULT_LINE_WIDTH));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(parquetSequencesToFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
