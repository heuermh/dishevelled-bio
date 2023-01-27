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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;
import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.FastqVariant;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;
import org.biojava.bio.program.fastq.SangerFastqReader;
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
 * Bin quality scores from files in FASTQ format.
 *
 * @since 2.2
 * @author  Michael Heuer
 */
public final class BinFastqQualityScores implements Callable<Integer> {
    private final Path inputFastqPath;
    private final File outputFastqFile;
    private final FastqReader fastqReader = new SangerFastqReader();
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "dsh-bin-fastq-quality-scores [args]";


    /**
     * Bin quality scores from files in FASTQ format.
     *
     * @param inputFastqFile input FASTQ file, if any
     * @param outputFastqFile output FASTQ file, if any
     */
    public BinFastqQualityScores(final Path inputFastqPath, final File outputFastqFile) {
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
            final FastqBuilder fastqBuilder = new FastqBuilder();
            fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        try {
                            Fastq f = fastqBuilder
                                .withDescription(fastq.getDescription())
                                .withSequence(fastq.getSequence())
                                .withQuality(bin(fastq.getQuality()))
                                .build();
                            fastqWriter.append(w, f);
                        }
                        catch (IOException e) {
                            throw new RuntimeException("could not write FASTQ", e);
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
     * Bin the specified quality scores per Illumina technical note, &quot;Reducing
     * Whole-Genome Data Storage Footprint.&quot;
     *
     * @param quality quality scores to bin
     * @return the specified quality scores binned per Illumina technical note
     */
    static final String bin(final String quality) {
        int size = quality.length();
        int[] qualityScores = new int[size];
        for (int i = 0; i < size; i++)
        {
            char c = quality.charAt(i);
            qualityScores[i] = binQualityScore(FastqVariant.FASTQ_SANGER.qualityScore(c));
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++)
        {
            sb.append(FastqVariant.FASTQ_SANGER.quality(qualityScores[i]));
        }
        return sb.toString();
    }

    /**
     * Bin the specified quality score per Illumina technical note, &quot;Reducing
     * Whole-Genome Data Storage Footprint.&quot;
     *
     * @param qualityScore quality score to bin
     * @return the specified quality score binned per Illumina technical note
     */
    static int binQualityScore(final int qualityScore) {
        if (qualityScore < 2) {
            return qualityScore;
        }
        else if (qualityScore < 10) {
            return 6;
        }
        else if (qualityScore < 20) {
            return 15;
        }
        else if (qualityScore < 25) {
            return 22;
        }
        else if (qualityScore < 30) {
            return 27;
        }
        else if (qualityScore < 35) {
            return 33;
        }
        else if (qualityScore < 40) {
            return 37;
        }
        return 40;
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

        BinFastqQualityScores binFastqQualityScores = null;
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

            binFastqQualityScores = new BinFastqQualityScores(inputFastqPath.getValue(), outputFastqFile.getValue());
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
        catch (IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(binFastqQualityScores.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
