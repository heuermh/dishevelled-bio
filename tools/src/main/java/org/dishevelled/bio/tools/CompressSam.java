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

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.dishevelled.bio.alignment.sam.SamHeader;
import org.dishevelled.bio.alignment.sam.SamReader;
import org.dishevelled.bio.alignment.sam.SamRecord;
import org.dishevelled.bio.alignment.sam.SamWriter;
import org.dishevelled.bio.alignment.sam.SamListener;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Compress alignments in SAM format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class CompressSam implements Callable<Integer> {
    private final Path inputSamPath;
    private final File outputSamFile;
    private static final String USAGE = "dsh-compress-sam [args]";

    /**
     * Compress alignments in SAM format to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputSamFile input SAM file, if any
     * @param outputSamFile output SAM file, if any
     */
    public CompressSam(final File inputSamFile, final File outputSamFile) {
        this(inputSamFile == null ? null : inputSamFile.toPath(), outputSamFile);
    }

    /**
     * Compress alignments in SAM format to splittable bgzf or bzip2 compression codecs.
     *
     * @since 2.1
     * @param inputSamPath input SAM path, if any
     * @param outputSamFile output SAM file, if any
     */
    public CompressSam(final Path inputSamPath, final File outputSamFile) {
        this.inputSamPath = inputSamPath;
        this.outputSamFile = outputSamFile;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputSamPath);
            writer = writer(outputSamFile);
            
            final PrintWriter w = writer;
            SamReader.stream(reader, new SamListener() {
                    @Override
                    public boolean header(final SamHeader header) {
                        SamWriter.writeHeader(header, w);
                        return true;
                    }

                    @Override
                    public boolean record(final SamRecord record) {
                        SamWriter.writeRecord(record, w);
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
        PathArgument inputSamPath = new PathArgument("i", "input-sam-path", "input SAM path, default stdin", false);
        FileArgument outputSamFile = new FileArgument("o", "output-sam-file", "output SAM file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputSamPath, outputSamFile);
        CommandLine commandLine = new CommandLine(args);

        CompressSam compressSam = null;
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
            compressSam = new CompressSam(inputSamPath.getValue(), outputSamFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressSam.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
