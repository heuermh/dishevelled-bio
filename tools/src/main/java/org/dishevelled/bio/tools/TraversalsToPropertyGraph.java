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
import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.dishevelled.bio.assembly.gfa1.Gfa1Adapter;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Traversal;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Convert traversals in GFA 1.0 format to property graph CSV format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class TraversalsToPropertyGraph implements Callable<Integer> {
    private final Path inputGfa1Path;
    private final File outputEdgesFile;
    private static final String HEADER = "~id,~source,~target,sourceOrientation:String,targetOrientation:String,interaction:String,pathName:String,ordinal:Int,overlap:String";
    private static final String USAGE = "dsh-traversals-to-property-graph -i input.gfa.gz -o traversal-edges.csv.gz";


    /**
     * Convert traversals in GFA 1.0 format to property graph CSV format.
     *
     * @deprecated will be removed in version 3.0
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputEdgesFile output traversal-edges.csv file, if any
     */
    public TraversalsToPropertyGraph(final File inputGfa1File,
                                     final File outputEdgesFile) {
        this(inputGfa1File == null ? null : inputGfa1File.toPath(), outputEdgesFile);
    }

    /**
     * Convert traversals in GFA 1.0 format to property graph CSV format.
     *
     * @since 2.1
     * @param inputGfa1Path input GFA 1.0 path, if any
     * @param outputEdgesFile output traversal-edges.csv file, if any
     */
    public TraversalsToPropertyGraph(final Path inputGfa1Path,
                                     final File outputEdgesFile) {
        this.inputGfa1Path = inputGfa1Path;
        this.outputEdgesFile = outputEdgesFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter edgesWriter = null;
        try {
            edgesWriter = writer(outputEdgesFile);
            edgesWriter.println(HEADER);

            final PrintWriter ew = edgesWriter;
            Gfa1Reader.stream(reader(inputGfa1Path), new Gfa1Adapter() {

                    @Override
                    public boolean traversal(final Traversal traversal) {
                        if (!traversal.containsId()) {
                            throw new IllegalArgumentException("traversal identifiers are required for property graph CSV format");
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(traversal.getId());
                        sb.append(",");
                        sb.append(traversal.getSource().getName());
                        sb.append(",");
                        sb.append(traversal.getTarget().getName());
                        sb.append(",");
                        sb.append(traversal.getSource().getOrientation().getSymbol());
                        sb.append(",");
                        sb.append(traversal.getTarget().getOrientation().getSymbol());
                        sb.append(",t,");
                        sb.append(traversal.getPathName());
                        sb.append(",");
                        sb.append(traversal.getOrdinal());
                        sb.append(",");
                        sb.append(traversal.getOverlapOpt().orElse(""));
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
        PathArgument inputGfa1Path = new PathArgument("i", "input-gfa1-path", "input GFA 1.0 path, default stdin", false);
        FileArgument outputEdgesFile = new FileArgument("o", "output-edges-file", "output property graph CSV format file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1Path, outputEdgesFile);
        CommandLine commandLine = new CommandLine(args);

        TraversalsToPropertyGraph traversalsToPropertyGraph = null;
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
            traversalsToPropertyGraph = new TraversalsToPropertyGraph(inputGfa1Path.getValue(), outputEdgesFile.getValue());
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
            System.exit(traversalsToPropertyGraph.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
