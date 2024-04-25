/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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

import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

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
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Convert segments in GFA 1.0 format to property graph CSV format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class SegmentsToPropertyGraph implements Callable<Integer> {
    private final Path inputGfa1Path;
    private final File outputNodesFile;
    private static final String HEADER = "~id,sequence:String,length:Long,readCount:Int,fragmentCount:Int,kmerCount:Int,sequenceChecksum:String,sequenceUri:String";
    private static final String USAGE = "dsh-segments-to-property-graph -i input.gfa.gz -o nodes.csv.gz";

    /**
     * Convert segments in GFA 1.0 format to property graph CSV format.
     *
     * @deprecated will be removed in version 3.0
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputNodesFile output nodes.csv file, if any
     */
    public SegmentsToPropertyGraph(final File inputGfa1File,
                                   final File outputNodesFile) {
        this(inputGfa1File == null ? null : inputGfa1File.toPath(), outputNodesFile);
    }

    /**
     * Convert segments in GFA 1.0 format to property graph CSV format.
     *
     * @since 2.1
     * @param inputGfa1Path input GFA 1.0 path, if any
     * @param outputNodesFile output nodes.csv file, if any
     */
    public SegmentsToPropertyGraph(final Path inputGfa1Path,
                                   final File outputNodesFile) {
        this.inputGfa1Path = inputGfa1Path;
        this.outputNodesFile = outputNodesFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter nodesWriter = null;
        try {
            nodesWriter = writer(outputNodesFile);
            nodesWriter.println(HEADER);

            final PrintWriter nw = nodesWriter;
            Gfa1Reader.stream(reader(inputGfa1Path), new Gfa1Adapter() {

                    @Override
                    public boolean segment(final Segment segment) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(segment.getName());
                        sb.append(",");
                        sb.append(segment.getSequenceOpt().orElse(""));
                        sb.append(",");
                        sb.append(segment.containsLength() ? segment.getLength() : "");
                        sb.append(",");
                        sb.append(segment.containsReadCount() ? segment.getReadCount() : "");
                        sb.append(",");
                        sb.append(segment.containsFragmentCount() ? segment.getFragmentCount() : "");
                        sb.append(",");
                        sb.append(segment.containsKmerCount() ? segment.getKmerCount() : "");
                        sb.append(",");
                        sb.append(segment.containsSequenceChecksum() ? String.valueOf(segment.getSequenceChecksum()) : "");
                        sb.append(",");
                        sb.append(segment.getSequenceUriOpt().orElse(""));
                        nw.println(sb);
                        return true;
                    }
                });

            return 0;
        }
        finally {
            try {
                nodesWriter.close();
            }
            catch (Exception e) {
                // empty
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
        PathArgument inputGfa1Path = new PathArgument("i", "input-gfa1-path", "input GFA 1.0 path, default stdin", false);
        FileArgument outputNodesFile = new FileArgument("o", "output-nodes-file", "output property graph CSV format file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1Path, outputNodesFile);
        CommandLine commandLine = new CommandLine(args);

        SegmentsToPropertyGraph segmentsToPropertyGraph = null;
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            segmentsToPropertyGraph = new SegmentsToPropertyGraph(inputGfa1Path.getValue(), outputNodesFile.getValue());
        }
        catch (CommandLineParseException e) {
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        catch (NullPointerException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(segmentsToPropertyGraph.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
