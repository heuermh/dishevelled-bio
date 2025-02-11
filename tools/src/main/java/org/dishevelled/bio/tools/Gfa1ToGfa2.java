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

import org.dishevelled.bio.assembly.gfa2.Alignment;
import org.dishevelled.bio.assembly.gfa2.Gfa2Writer;
import org.dishevelled.bio.assembly.gfa2.Position;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Convert GFA 1.0 format to GFA 2.0 format.
 *
 * @author  Michael Heuer
 */
public final class Gfa1ToGfa2 implements Callable<Integer> {
    private final java.nio.file.Path inputGfa1Path;
    private final File outputGfa2File;
    private static final String USAGE = "dsh-gfa1-to-gfa2 -i input.gfa1.gz -o output.gfa2.gz";


    /**
     * Convert GFA 1.0 format to GFA 2.0 format.
     *
     * @deprecated will be removed in version 3.0
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputGfa2File output GFA 2.0 file, if any
     */
    public Gfa1ToGfa2(final File inputGfa1File, final File outputGfa2File) {
        this(inputGfa1File == null ? null : inputGfa1File.toPath(), outputGfa2File);
    }

    /**
     * Convert GFA 1.0 format to GFA 2.0 format.
     *
     * @since 2.1
     * @param inputGfa1Path input GFA 1.0 path, if any
     * @param outputGfa2File output GFA 2.0 file, if any
     */
    public Gfa1ToGfa2(final java.nio.file.Path inputGfa1Path, final File outputGfa2File) {
        this.inputGfa1Path = inputGfa1Path;
        this.outputGfa2File = outputGfa2File;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputGfa2File);

            final PrintWriter w = writer;
            Gfa1Reader.stream(reader(inputGfa1Path), new Gfa1Adapter() {
                    @Override
                    public boolean header(final Header header) {
                        // convert VN:Z:1.0 to VN:Z:2.0 annotation if present
                        if (header.getAnnotations().containsKey("VN")) {
                            if (!"1.0".equals(header.getAnnotations().get("VN").getValue())) {
                                throw new RuntimeException("cannot convert input as GFA 1.0, was " + header.getAnnotations().get("VN").getValue());
                            }
                            Map<String, Annotation> annotations = new HashMap<String, Annotation>();
                            annotations.put("VN", new Annotation("VN", "Z", "2.0"));
                            for (Annotation annotation : header.getAnnotations().values()) {
                                if (!"VN".equals(annotation.getName())) {
                                    annotations.put(annotation.getName(), annotation);
                                }
                            }
                            Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Header(annotations), w);
                        }
                        else {
                            Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Header(header.getAnnotations()), w);
                        }
                        return true;
                    }

                    @Override
                    public boolean segment(final Segment segment) {
                        if (segment.getSequence() != null) {
                            Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Segment(segment.getId(), segment.getSequence().length(), segment.getSequence(), segment.getAnnotations()), w);
                        }
                        else if (segment.getAnnotations().containsKey("LN")) {
                            Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Segment(segment.getId(), Integer.parseInt(segment.getAnnotations().get("LN").getValue()), segment.getSequence(), segment.getAnnotations()), w);
                        }
                        else {
                            Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Segment(segment.getId(), 0, segment.getSequence(), segment.getAnnotations()), w);
                        }
                        return true;
                    }

                    @Override
                    public boolean link(final Link link) {
                        Position unknown = new Position(0, false);
                        Alignment alignment = link.getOverlap() == null ? null : Alignment.valueOf(link.getOverlap());
                        Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Edge(null, toGfa2Reference(link.getSource()), toGfa2Reference(link.getTarget()), unknown, unknown, unknown, unknown, alignment, link.getAnnotations()), w);
                        return true;
                    }

                    @Override
                    public boolean containment(final Containment containment) {
                        Position unknown = new Position(0, false);
                        Position targetStart = new Position(containment.getPosition(), false);
                        Alignment alignment = containment.getOverlap() == null ? null : Alignment.valueOf(containment.getOverlap());
                        Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Edge(null, toGfa2Reference(containment.getContainer()), toGfa2Reference(containment.getContained()), unknown, unknown, targetStart, unknown, alignment, containment.getAnnotations()), w);
                        return true;
                    }

                    @Override
                    public boolean path(final Path path) {
                        Gfa2Writer.write(new org.dishevelled.bio.assembly.gfa2.Path(path.getName(), toGfa2References(path.getSegments()), path.getAnnotations()), w);
                        return true;
                    }
                });

            return 0;
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
            }
        }
    }

    static List<org.dishevelled.bio.assembly.gfa2.Reference> toGfa2References(List<org.dishevelled.bio.assembly.gfa1.Reference> references) {
        List<org.dishevelled.bio.assembly.gfa2.Reference> gfa2References = new ArrayList<org.dishevelled.bio.assembly.gfa2.Reference>(references.size());
        for (org.dishevelled.bio.assembly.gfa1.Reference reference : references) {
            gfa2References.add(toGfa2Reference(reference));
        }
        return gfa2References;
    }

    static org.dishevelled.bio.assembly.gfa2.Reference toGfa2Reference(org.dishevelled.bio.assembly.gfa1.Reference reference) {
        return new org.dishevelled.bio.assembly.gfa2.Reference(reference.getId(), toGfa2Orientation(reference.getOrientation()));
    }

    static org.dishevelled.bio.assembly.gfa2.Orientation toGfa2Orientation(org.dishevelled.bio.assembly.gfa1.Orientation orientation) {
        return orientation.isForward() ? org.dishevelled.bio.assembly.gfa2.Orientation.FORWARD : org.dishevelled.bio.assembly.gfa2.Orientation.REVERSE;
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
        FileArgument outputGfa2File = new FileArgument("o", "output-gfa2-file", "output GFA 2.0 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1Path, outputGfa2File);
        CommandLine commandLine = new CommandLine(args);

        Gfa1ToGfa2 gfa1ToGfa2 = null;
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
            gfa1ToGfa2 = new Gfa1ToGfa2(inputGfa1Path.getValue(), outputGfa2File.getValue());
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
            System.exit(gfa1ToGfa2.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
