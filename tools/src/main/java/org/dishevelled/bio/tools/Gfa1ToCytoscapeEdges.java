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

import java.io.File;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Callable;

import org.dishevelled.bio.annotation.Annotation;

import org.dishevelled.bio.assembly.gfa1.Containment;
import org.dishevelled.bio.assembly.gfa1.Gfa1Adapter;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Header;
import org.dishevelled.bio.assembly.gfa1.Link;
import org.dishevelled.bio.assembly.gfa1.Path;
import org.dishevelled.bio.assembly.gfa1.Segment;
import org.dishevelled.bio.assembly.gfa1.Traversal;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Convert GFA 1.0 format to edges.txt format for Cytoscape.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class Gfa1ToCytoscapeEdges implements Callable<Integer> {
    private final File inputGfa1File;
    private final File outputEdgesFile;
    private static final String HEADER = "source\ttarget\tinteraction\t" +
        "id\tsource_orientation\ttarget_orientation\toverlap\tmapping_quality\t" +
        "mismatch_count\tread_count\tfragment_count\tkmer_count\tposition\t" +
        "path_name\tordinal";
    private static final String USAGE = "dsh-gfa1-to-cytoscape-edges -i input.gfa.gz -n edges.txt.gz";

    /**
     * Convert GFA 1.0 format to edges.txt format for Cytoscape.
     *
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputEdgesFile output edges.txt file, if any
     */
    public Gfa1ToCytoscapeEdges(final File inputGfa1File,
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
                        sb.append(link.getSource().getId());
                        sb.append("\t");
                        sb.append(link.getTarget().getId());
                        sb.append("\t");
                        sb.append("link");
                        sb.append("\t");
                        sb.append(link.getIdOpt().orElse(""));
                        sb.append("\t");
                        sb.append(link.getSource().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(link.getTarget().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(link.getOverlapOpt().orElse(""));
                        sb.append("\t");
                        sb.append(link.containsMappingQuality() ? link.getMappingQuality() : "");
                        sb.append("\t");
                        sb.append(link.containsMismatchCount() ? link.getMismatchCount() : "");
                        sb.append("\t");
                        sb.append(link.containsReadCount() ? link.getReadCount() : "");
                        sb.append("\t");
                        sb.append(link.containsFragmentCount() ? link.getFragmentCount() : "");
                        sb.append("\t");
                        sb.append(link.containsKmerCount() ? link.getKmerCount() : "");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        ew.println(sb.toString());
                        return true;
                    }

                    @Override
                    public boolean containment(final Containment containment) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(containment.getContainer().getId());
                        sb.append("\t");
                        sb.append(containment.getContained().getId());
                        sb.append("\t");
                        sb.append("containment");
                        sb.append("\t");
                        sb.append(containment.getIdOpt().orElse(""));
                        sb.append("\t");
                        sb.append(containment.getContainer().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(containment.getContained().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(containment.getOverlapOpt().orElse(""));
                        sb.append("\t");
                        sb.append("\t");
                        sb.append(containment.containsMismatchCount() ? containment.getMismatchCount() : "");
                        sb.append("\t");
                        sb.append(containment.containsReadCount() ? containment.getReadCount() : "");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append(containment.getPosition());
                        sb.append("\t");
                        sb.append("\t");
                        ew.println(sb.toString());
                        return true;
                    }

                    @Override
                    public boolean traversal(final Traversal traversal) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(traversal.getSource().getId());
                        sb.append("\t");
                        sb.append(traversal.getTarget().getId());
                        sb.append("\t");
                        sb.append("traversal");
                        sb.append("\t");
                        sb.append(traversal.getIdOpt().orElse(""));
                        sb.append("\t");
                        sb.append(traversal.getSource().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(traversal.getTarget().getOrientation().getSymbol());
                        sb.append("\t");
                        sb.append(traversal.getOverlapOpt().orElse(""));
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append("\t");
                        sb.append(traversal.getPathName());
                        sb.append("\t");
                        sb.append(traversal.getOrdinal());
                        ew.println(sb.toString());
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

        Gfa1ToCytoscapeEdges gfa1ToCytoscapeEdges = null;
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
            gfa1ToCytoscapeEdges = new Gfa1ToCytoscapeEdges(inputGfa1File.getValue(), outputEdgesFile.getValue());
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
            System.exit(gfa1ToCytoscapeEdges.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
