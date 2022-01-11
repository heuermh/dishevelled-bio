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

import static com.google.common.base.Preconditions.checkArgument;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import static org.dishevelled.bio.sequence.Sequences.encode;
import static org.dishevelled.bio.sequence.Sequences.encodeWithNs;
import static org.dishevelled.bio.sequence.Sequences.encodeWithAmbiguity;

import java.io.File;
import java.io.PrintWriter;

import java.nio.ByteBuffer;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Callable;

import org.dishevelled.bio.annotation.Annotation;

import org.dishevelled.bio.assembly.gfa1.Gfa1Listener;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Gfa1Record;
import org.dishevelled.bio.assembly.gfa1.Segment;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Encode segment sequences as Base64 byte arrays in GFA 1.0 format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class Base64EncodeSegments implements Callable<Integer> {
    private final File inputGfa1File;
    private final File outputGfa1File;
    private final boolean withNs;
    private final boolean withAmbiguity;
    private static final String USAGE = "dsh-base64-encode-segments -i input.gfa.gz -o output.gfa.gz";

    /**
     * Encode segment sequences as Base64 byte arrays in GFA 1.0 format.
     *
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param withNs encode with Ns
     * @param withAmbiguity encode with ambiguity
     * @param outputGfa1File output GFA 1.0 file, if any
     */
    public Base64EncodeSegments(final File inputGfa1File,
                                final boolean withNs,
                                final boolean withAmbiguity,
                                final File outputGfa1File) {

        checkArgument(!(withNs && withAmbiguity), "withNs and withAmbiguity are mutually exclusive");
        this.inputGfa1File = inputGfa1File;
        this.withNs = withNs;
        this.withAmbiguity = withAmbiguity;
        this.outputGfa1File = outputGfa1File;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputGfa1File);

            final PrintWriter w = writer;
            Gfa1Reader.stream(reader(inputGfa1File), new Gfa1Listener() {
                    @Override
                    public final boolean record(final Gfa1Record record) {
                        if (record instanceof Segment) {
                            Segment segment = (Segment) record;
                            if (segment.hasSequence()) {
                                String name = segment.getName();
                                String sequence = segment.getSequence();
                                ByteBuffer encodedSequence = null;
                                if (withNs) {
                                    encodedSequence = encodeWithNs(sequence);
                                }
                                else if (withAmbiguity) {
                                    encodedSequence = encodeWithAmbiguity(sequence);
                                }
                                else {
                                    encodedSequence = encode(sequence);
                                }

                                Map<String, Annotation> annotations = new HashMap<String, Annotation>(segment.getAnnotations());
                                annotations.put("bs", new Annotation("bs", "Z", base64(encodedSequence)));
                                // add length annotation if missing
                                if (!annotations.containsKey("LN")) {
                                    annotations.put("LN", new Annotation("LN", "i", String.valueOf(sequence.length())));
                                }
                                w.println(new Segment(name, null, annotations).toString());

                            }
                            else {
                                w.println(segment.toString());
                            }
                        }
                        else {
                            w.println(record.toString());
                        }
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

    static String base64(final ByteBuffer byteBuffer) {
        ByteBuffer readOnly = byteBuffer.asReadOnlyBuffer();
        readOnly.position(0);
        byte[] b = new byte[readOnly.limit()];
        readOnly.get(b, 0, b.length);
        return Base64.getEncoder().withoutPadding().encodeToString(b);
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
        FileArgument outputGfa1File = new FileArgument("o", "output-gfa1-file", "output GFA 1.0 file, default stdout", false);

        Switch withNs = new Switch("n", "with-ns", "encode sequence with Ns e.g. {a,c,g,t,n}");
        Switch withAmbiguity = new Switch("g", "with-ambiguity", "encode sequence with ambiguity e.g. {a,c,g,t,m,r,t,...}");
        ArgumentList arguments = new ArgumentList(about, help, inputGfa1File, withNs, withAmbiguity, outputGfa1File);
        CommandLine commandLine = new CommandLine(args);

        Base64EncodeSegments base64EncodeSegments = null;
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
            base64EncodeSegments = new Base64EncodeSegments(inputGfa1File.getValue(), withNs.getValue(), withAmbiguity.getValue(), outputGfa1File.getValue());
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
            System.exit(base64EncodeSegments.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
