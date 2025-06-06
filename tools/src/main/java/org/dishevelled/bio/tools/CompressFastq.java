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
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.biojava.bio.program.fastq.Fastq;

import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.SangerFastqWriter;
import org.biojava.bio.program.fastq.StreamListener;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class CompressFastq implements Callable<Integer> {
    private final Path inputFastqPath;
    private final File outputFastqFile;
    private static final String USAGE = "dsh-compress-fastq [args]";

    /**
     * Compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs.
     *
     * @deprecated will be removed in version 3.0
     * @param inputFastqFile input FASTQ file, if any
     * @param outputFastqFile output FASTQ file, if any
     */
    public CompressFastq(final File inputFastqFile, final File outputFastqFile) {
        this(inputFastqFile == null ? null : inputFastqFile.toPath(), outputFastqFile);
    }

    /**
     * Compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs.
     *
     * @since 2.1
     * @param inputFastqPath input FASTQ path, if any
     * @param outputFastqFile output FASTQ file, if any
     */
    public CompressFastq(final Path inputFastqPath, final File outputFastqFile) {
        this.inputFastqPath = inputFastqPath;
        this.outputFastqFile = outputFastqFile;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastqPath);
            writer = writer(outputFastqFile);

            final PrintWriter w = writer;
            final FastqReader fastqReader = new SangerFastqReader();
            final FastqWriter fastqWriter = new SangerFastqWriter();
            fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        try {
                            fastqWriter.append(w, fastq);
                        }
                        catch (IOException e) {
                            // ignore
                        }
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
        PathArgument inputFastqPath = new PathArgument("i", "input-fastq-path", "input FASTQ path, default stdin", false);
        FileArgument outputFastqFile = new FileArgument("o", "output-fastq-file", "output FASTQ file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputFastqPath, outputFastqFile);
        CommandLine commandLine = new CommandLine(args);

        CompressFastq compressFastq = null;
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
            compressFastq = new CompressFastq(inputFastqPath.getValue(), outputFastqFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
