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

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.dishevelled.bio.assembly.gfa1.Gfa1Listener;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Gfa1Record;
import org.dishevelled.bio.assembly.gfa1.Gfa1Writer;
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
 * Compress assembly in rGFA format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class CompressRgfa implements Callable<Integer> {
    private final Path inputRgfaPath;
    private final File outputRgfaFile;
    private static final String USAGE = "dsh-compress-rgfa [args]";

    /**
     * Compress assembly in rGFA format to splittable bgzf or bzip2 compression codecs.
     *
     * @deprecated will be removed in version 3.0
     * @param inputRgfaFile input rGFA file, if any
     * @param outputRgfaFile output rGFA file, if any
     */
    public CompressRgfa(final File inputRgfaFile, final File outputRgfaFile) {
        this(inputRgfaFile == null ? null : inputRgfaFile.toPath(), outputRgfaFile);
    }

    /**
     * Compress assembly in rGFA format to splittable bgzf or bzip2 compression codecs.
     *
     * @since 2.1
     * @param inputRgfaPath input rGFA path, if any
     * @param outputRgfaFile output rGFA file, if any
     */
    public CompressRgfa(final Path inputRgfaPath, final File outputRgfaFile) {
        this.inputRgfaPath = inputRgfaPath;
        this.outputRgfaFile = outputRgfaFile;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputRgfaPath);
            writer = writer(outputRgfaFile);
            
            final PrintWriter w = writer;
            Gfa1Reader.stream(reader, new Gfa1Listener() {
                    @Override
                    public boolean record(final Gfa1Record rgfaRecord) {
                        if (rgfaRecord instanceof Segment) {
                            Segment segment = (Segment) rgfaRecord;

                            // SN, SO, SR are required for rGFA segments
                            if (!segment.containsStableName()) {
                                throw new IllegalArgumentException("rGFA segment " + segment.getName() + " must contain SN tag");
                            }
                            if (!segment.containsStableOffset()) {
                                throw new IllegalArgumentException("rGFA segment " + segment.getName() + " must contain SO tag");
                            }
                            if (!segment.containsStableRank()) {
                                throw new IllegalArgumentException("rGFA segment " + segment.getName() + " must contain SR tag");
                            }
                        }
                        Gfa1Writer.write(rgfaRecord, w);
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
        PathArgument inputRgfaPath = new PathArgument("i", "input-rgfa-path", "input rGFA path, default stdin", false);
        FileArgument outputRgfaFile = new FileArgument("o", "output-rgfa-file", "output rGFA file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputRgfaPath, outputRgfaFile);
        CommandLine commandLine = new CommandLine(args);

        CompressRgfa compressRgfa = null;
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
            compressRgfa = new CompressRgfa(inputRgfaPath.getValue(), outputRgfaFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressRgfa.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
