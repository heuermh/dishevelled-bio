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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.read.PairedEndFastqReader.isLeft;
import static org.dishevelled.bio.read.PairedEndFastqReader.isRight;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.SangerFastqWriter;
import org.biojava.bio.program.fastq.StreamListener;

import org.dishevelled.bio.read.PairedEndAdapter;
import org.dishevelled.bio.read.PairedEndFastqReader;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Convert interleaved FASTQ format into first and second DNA sequence files in FASTQ format.
 *
 * @author  Michael Heuer
 */
public final class DisinterleaveFastq implements Callable<Integer> {
    private final Path pairedPath;
    private final Path unpairedPath;
    private final File firstFastqFile;
    private final File secondFastqFile;
    private static final String USAGE = "dsh-disinterleave-fastq -p foo.paired.fq.gz [-u foo.unpaired.fq.gz] -1 foo_1.fq.gz -2 foo_2.fq.gz";


    /**
     * Convert interleaved FASTQ format into first and second DNA sequence files in FASTQ format.
     *
     * @param pairedFile input interleaved paired FASTQ file, must not be null
     * @param unpairedFile input unpaired FASTQ file, if any
     * @param firstFastqFile first FASTQ output file, must not be null
     * @param secondFastqFile second FASTQ output file, must not be null
     */
    public DisinterleaveFastq(final File pairedFile, final File unpairedFile, final File firstFastqFile, final File secondFastqFile) {
        this(pairedFile == null ? null : pairedFile.toPath(),
             unpairedFile == null ? null : unpairedFile.toPath(),
             firstFastqFile,
             secondFastqFile);
    }

    /**
     * Convert interleaved FASTQ format into first and second DNA sequence files in FASTQ format.
     *
     * @since 2.1
     * @param pairedPath input interleaved paired FASTQ path, must not be null
     * @param unpairedPath input unpaired FASTQ file, if any
     * @param firstFastqFile first FASTQ output file, must not be null
     * @param secondFastqFile second FASTQ output file, must not be null
     */
    public DisinterleaveFastq(final Path pairedPath, final Path unpairedPath, final File firstFastqFile, final File secondFastqFile) {
        checkNotNull(pairedPath);
        checkNotNull(firstFastqFile);
        checkNotNull(secondFastqFile);
        this.pairedPath = pairedPath;
        this.unpairedPath = unpairedPath;
        this.firstFastqFile = firstFastqFile;
        this.secondFastqFile = secondFastqFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader pairedReader = null;
        BufferedReader unpairedReader = null;
        PrintWriter firstWriter = null;
        PrintWriter secondWriter = null;
        try {
            pairedReader = reader(pairedPath);
            firstWriter = writer(firstFastqFile);
            secondWriter = writer(secondFastqFile);

            final PrintWriter fw = firstWriter;
            final PrintWriter sw = secondWriter;

            final FastqWriter firstFastqWriter = new SangerFastqWriter();
            final FastqWriter secondFastqWriter = new SangerFastqWriter();

            PairedEndFastqReader.streamInterleaved(pairedReader, new PairedEndAdapter() {
                    @Override
                    public void paired(final Fastq left, final Fastq right) {
                        try {
                            firstFastqWriter.append(fw, left);
                            secondFastqWriter.append(sw, right);
                        }
                        catch (IOException e) {
                            throw new RuntimeException("could not write paired end reads", e);
                       } 
                    }

                    @Override
                    public void unpaired(final Fastq unpaired) {
                        throw new IllegalArgumentException("interleaved paired file contained unpaired read " + unpaired.getDescription());
                    }
                });

            if (unpairedPath != null) {
                unpairedReader = reader(unpairedPath);

                new SangerFastqReader().stream(unpairedReader, new StreamListener() {
                        @Override
                        public void fastq(final Fastq fastq) {
                            try {
                                if (isLeft(fastq)) {
                                    firstFastqWriter.append(fw, fastq);
                                }
                                else if (isRight(fastq)) {
                                    secondFastqWriter.append(sw, fastq);
                                }
                            }
                            catch (IOException e) {
                                throw new RuntimeException("could not write unpaired read", e);
                            }
                        }
                    });
            }

            return 0;
        }
        finally {
            try {
                pairedReader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                unpairedReader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                firstWriter.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                secondWriter.close();
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
        PathArgument pairedPath = new PathArgument("p", "paired-path", "interleaved paired FASTQ input path", true);
        PathArgument unpairedPath = new PathArgument("u", "unpaired-path", "unpaired FASTQ input path", false);
        FileArgument firstFastqFile = new FileArgument("1", "first-fastq-file", "first FASTQ output file", true);
        FileArgument secondFastqFile = new FileArgument("2", "second-fastq-file", "second FASTQ output file", true);

        ArgumentList arguments = new ArgumentList(about, help, pairedPath, unpairedPath, firstFastqFile, secondFastqFile);
        CommandLine commandLine = new CommandLine(args);

        DisinterleaveFastq disinterleaveFastq = null;
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
            disinterleaveFastq = new DisinterleaveFastq(pairedPath.getValue(), unpairedPath.getValue(), firstFastqFile.getValue(), secondFastqFile.getValue());
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
        try {
            System.exit(disinterleaveFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
