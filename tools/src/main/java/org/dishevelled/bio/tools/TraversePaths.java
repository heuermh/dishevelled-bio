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
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.Map;

import java.util.concurrent.Callable;

import org.dishevelled.bio.annotation.Annotation;

import org.dishevelled.bio.assembly.gfa1.Gfa1Listener;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Gfa1Record;
import org.dishevelled.bio.assembly.gfa1.Gfa1Writer;
import org.dishevelled.bio.assembly.gfa1.Path;
import org.dishevelled.bio.assembly.gfa1.Reference;
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
 * Traverse paths in GFA 1.0 format, creating new traversal records.
 *
 * @since 1.3
 * @author  Michael Heuer
 */
public final class TraversePaths implements Callable<Integer> {
    private final java.nio.file.Path inputGfa1Path;
    private final File outputGfa1File;
    private static final Map<String, Annotation> EMPTY_ANNOTATIONS = Collections.emptyMap();
    private static final String USAGE = "dsh-traverse-paths [args]";


    /**
     * Traverse paths in GFA 1.0 format, creating new traversal records.
     *
     * @deprecated will be removed in version 3.0
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputGfa1File output GFA 1.0 file, if any
     */
    public TraversePaths(final File inputGfa1File, final File outputGfa1File) {
        this(inputGfa1File == null ? null : inputGfa1File.toPath(), outputGfa1File);
    }

    /**
     * Traverse paths in GFA 1.0 format, creating new traversal records.
     *
     * @param inputGfa1Path input GFA 1.0 path, if any
     * @param outputGfa1File output GFA 1.0 file, if any
     */
    public TraversePaths(final java.nio.file.Path inputGfa1Path, final File outputGfa1File) {
        this.inputGfa1Path = inputGfa1Path;
        this.outputGfa1File = outputGfa1File;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGfa1Path);
            writer = writer(outputGfa1File);
            
            final PrintWriter w = writer;
            Gfa1Reader.stream(reader, new Gfa1Listener() {
                    @Override
                    public boolean record(final Gfa1Record gfa1Record) {
                        Gfa1Writer.write(gfa1Record, w);

                        if (gfa1Record instanceof Path) {
                            Path path = (Path) gfa1Record;
                            int size = path.getSegments().size();

                            Reference source = null;
                            Reference target = null;
                            String overlap = null;

                            for (int i = 0; i < size; i++) {
                                target = path.getSegments().get(i);
                                if (i > 0) {
                                    overlap = (path.getOverlaps() != null && path.getOverlaps().size() > i) ? path.getOverlaps().get(i - 1) : null;
                                }
                                if (source != null) {
                                    Traversal traversal = new Traversal(path.getName(), i - 1, source, target, overlap, EMPTY_ANNOTATIONS);
                                    Gfa1Writer.write(traversal, w);
                                }
                                source = target;
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


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument inputGfa1Path = new PathArgument("i", "input-gfa1-path", "input GFA 1.0 path, default stdin", false);
        FileArgument outputGfa1File = new FileArgument("o", "output-gfa1-file", "output GFA 1.0 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1Path, outputGfa1File);
        CommandLine commandLine = new CommandLine(args);

        TraversePaths traversePaths = null;
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
            traversePaths = new TraversePaths(inputGfa1Path.getValue(), outputGfa1File.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(traversePaths.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
