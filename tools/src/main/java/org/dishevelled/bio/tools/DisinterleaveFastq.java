/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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

/**
 * Convert interleaved FASTQ format into first and second sequence files in FASTQ format.
 *
 * @author  Michael Heuer
 */
public final class DisinterleaveFastq implements Callable<Integer> {
    private final File pairedFile;
    private final File unpairedFile;
    private final File firstFastqFile;
    private final File secondFastqFile;
    private static final String USAGE = "dsh-disinterleave-fastq -p foo.paired.fq.gz [-u foo.unpaired.fq.gz] -1 foo_1.fq.gz -2 foo_2.fq.gz";


    /**
     * Convert interleaved FASTQ format into first and second sequence files in FASTQ format.
     *
     * @param pairedFile output interleaved paired FASTQ file, must not be null
     * @param unpairedFile output unpaired FASTQ file, if any
     * @param firstFastqFile first FASTQ input file, must not be null
     * @param secondFastqFile second FASTQ input file, must not be null
     */
    public DisinterleaveFastq(final File pairedFile, final File unpairedFile, final File firstFastqFile, final File secondFastqFile) {
        checkNotNull(pairedFile);
        checkNotNull(firstFastqFile);
        checkNotNull(secondFastqFile);
        this.pairedFile = pairedFile;
        this.unpairedFile = unpairedFile;
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
            pairedReader = reader(pairedFile);
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

            if (unpairedFile != null) {
                unpairedReader = reader(unpairedFile);

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
        FileArgument pairedFile = new FileArgument("p", "paired-file", "interleaved paired FASTQ input file", true);
        FileArgument unpairedFile = new FileArgument("u", "unpaired-file", "unpaired FASTQ input file", false);
        FileArgument firstFastqFile = new FileArgument("1", "first-fastq-file", "first FASTQ output file", true);
        FileArgument secondFastqFile = new FileArgument("2", "second-fastq-file", "second FASTQ output file", true);

        ArgumentList arguments = new ArgumentList(about, help, pairedFile, unpairedFile, firstFastqFile, secondFastqFile);
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
            disinterleaveFastq = new DisinterleaveFastq(pairedFile.getValue(), unpairedFile.getValue(), firstFastqFile.getValue(), secondFastqFile.getValue());
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
