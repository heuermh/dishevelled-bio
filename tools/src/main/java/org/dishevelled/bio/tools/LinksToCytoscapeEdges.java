/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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

import java.util.concurrent.Callable;

import org.dishevelled.bio.assembly.gfa1.Gfa1Adapter;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Link;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Convert links in GFA 1.0 format to edges.txt format for Cytoscape.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class LinksToCytoscapeEdges implements Callable<Integer> {
    private final File inputGfa1File;
    private final File outputEdgesFile;
    private static final String HEADER = "source\tsourceOrientation\ttarget\ttargetOrientation\tinteraction\tid\toverlap\tmappingQuality\tmismatchCount";
    private static final String USAGE = "dsh-links-to-cytoscape-edges -i input.gfa.gz -n edges.txt.gz";

    /**
     * Convert links in GFA 1.0 format to edges.txt format for Cytoscape.
     *
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputEdgesFile output edges.txt file, if any
     */
    public LinksToCytoscapeEdges(final File inputGfa1File,
                                 final File outputEdgesFile) {
        this.inputGfa1File = inputGfa1File;
        this.outputEdgesFile = outputEdgesFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter edgesWriter = null;
        try {
            edgesWriter = writer(outputEdgesFile);
            edgesWriter.println(HEADER);

            final PrintWriter ew = edgesWriter;
            Gfa1Reader.stream(reader(inputGfa1File), new Gfa1Adapter() {

                    @Override
                    public boolean link(final Link link) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(link.getSource().getName());
                        sb.append("\t");
                        sb.append(link.getSource().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(link.getTarget().getName());
                        sb.append("\t");
                        sb.append(link.getTarget().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append("link");
                        sb.append("\t");
                        sb.append(link.getIdOpt().orElse(""));
                        sb.append("\t");
                        sb.append(link.getOverlapOpt().orElse(""));
                        sb.append("\t");
                        sb.append(link.containsMappingQuality() ? link.getMappingQuality() : "");
                        sb.append("\t");
                        sb.append(link.containsMismatchCount() ? link.getMismatchCount() : "");
                        sb.append("\t");
                        ew.println(sb);
                        return true;
                    }
                });

            return 0;
        }
        finally {
            try {
                edgesWriter.close();
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
        FileArgument inputGfa1File = new FileArgument("i", "input-gfa1-file", "input GFA 1.0 file, default stdin", false);
        FileArgument outputEdgesFile = new FileArgument("o", "output-edges-file", "output Cytoscape edges.txt format file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1File, outputEdgesFile);
        CommandLine commandLine = new CommandLine(args);

        LinksToCytoscapeEdges linksToCytoscapeEdges = null;
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
            linksToCytoscapeEdges = new LinksToCytoscapeEdges(inputGfa1File.getValue(), outputEdgesFile.getValue());
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
            System.exit(linksToCytoscapeEdges.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
