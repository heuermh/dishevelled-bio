/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;

import java.util.concurrent.Callable;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;
import org.biojava.bio.program.fastq.FastqVariant;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Convert sequences in tab-separated values (tsv) text format to FASTQ format.
 *
 * @author  Michael Heuer
 */
public final class TextToFastq implements Callable<Integer> {
    private final File textFile;
    private final File fastqFile;
    private static final String USAGE = "dsh-text-to-fastq [args]";


    /**
     * Convert sequences in tab-separated values (tsv) text format to FASTQ format.
     *
     * @param textFile input text file, if any
     * @param fastqFile output FASTQ file, if any
     */
    public TextToFastq(final File textFile, final File fastqFile) {
        this.textFile = textFile;
        this.fastqFile = fastqFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(textFile);
            writer = writer(fastqFile);
            FastqWriter fastqWriter = new SangerFastqWriter();

            long lineNumber = 0;
            while (reader.ready()) {
                lineNumber++;
                String[] tokens = reader.readLine().split("\t");
                if (tokens.length != 3) {
                    throw new IOException("expected 3 tokens, found " + tokens.length + " at line number " + lineNumber);
                }
                Fastq fastq = new FastqBuilder()
                    .withVariant(FastqVariant.FASTQ_SANGER)
                    .withDescription(tokens[0])
                    .withSequence(tokens[1])
                    .withQuality(tokens[2])
                    .build();

                fastqWriter.append(writer, fastq);
            }
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
        FileArgument textFile = new FileArgument("i", "input-text-file", "input text file, default stdin", false);
        FileArgument fastqFile = new FileArgument("o", "output-fastq-file", "output FASTQ file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, textFile, fastqFile);
        CommandLine commandLine = new CommandLine(args);

        TextToFastq textToFastq = null;
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
            textToFastq = new TextToFastq(textFile.getValue(), fastqFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(textToFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}