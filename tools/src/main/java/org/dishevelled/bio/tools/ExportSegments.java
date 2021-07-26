/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;

import org.dishevelled.bio.assembly.gfa1.Gfa1Adapter;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Segment;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;

/**
 * Export assembly segment sequences in GFA 1.0 format to FASTA format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class ExportSegments implements Callable<Integer> {
    private final File inputGfa1File;
    private final File outputFastaFile;
    private final int lineWidth;
    static final int DEFAULT_LINE_WIDTH = 70;
    private static final String USAGE = "dsh-export-segments [args]";

    /**
     * Export assembly segment sequences in GFA 1.0 format to FASTA format.
     *
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param lineWidth line width
     */
    public ExportSegments(final File inputGfa1File, final File outputFastaFile, final int lineWidth) {
        this.inputGfa1File = inputGfa1File;
        this.outputFastaFile = outputFastaFile;
        this.lineWidth = lineWidth;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGfa1File);
            writer = writer(outputFastaFile);

            final PrintWriter w = writer;
            Gfa1Reader.stream(reader(inputGfa1File), new Gfa1Adapter() {
                    @Override
                    public boolean segment(final Segment segment) {
                        if (segment.hasSequence()) {
                            String description = describeSegment(segment);
                            String sequence = segment.getSequence();

                            w.print(">");
                            w.println(describeSegment(segment));
                            for (int i = 0, length = sequence.length(); i <= length; i += lineWidth) {
                                w.println(sequence.substring(i, Math.min(i + lineWidth, length)));
                            }
                        }
                        return true;
                    }
                });

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
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    static String describeSegment(final Segment segment) {
        StringBuilder sb = new StringBuilder();
        sb.append(segment.getName());
        if (!segment.getAnnotations().isEmpty()) {
            sb.append(" ");
            Joiner joiner = Joiner.on("\t");
            joiner.appendTo(sb, segment.getAnnotations().values());
        }
        return sb.toString();
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument inputGfa1File = new FileArgument("i", "input-gfa1-file", "input GFA 1.0 file, default stdin", false);
        FileArgument outputFastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1File, outputFastaFile, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        ExportSegments exportSegments = null;
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
            exportSegments = new ExportSegments(inputGfa1File.getValue(), outputFastaFile.getValue(), lineWidth.getValue(DEFAULT_LINE_WIDTH));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(exportSegments.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
