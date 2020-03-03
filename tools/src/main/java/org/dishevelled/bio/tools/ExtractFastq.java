/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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

import java.util.concurrent.Callable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Extract matching sequences in FASTQ format.
 *
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class ExtractFastq implements Callable<Integer> {
    private final File inputFastqFile;
    private final File outputFastqFile;
    private final String name;
    private final Pattern pattern;
    private final FastqReader fastqReader = new SangerFastqReader();
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "dsh-extract-fastq [args]";

    /**
     * Extract matching sequences in FASTQ format.
     *
     * @param inputFastqFile input FASTQ file, if any
     * @param outputFastqFile output FASTQ file, if any
     * @param name exact sequence name to match, if any
     * @param description FASTQ description regex pattern to match, if any
     */
    public ExtractFastq(final File inputFastqFile, final File outputFastqFile, final String name, final String description) {
        this.inputFastqFile = inputFastqFile;
        this.outputFastqFile = outputFastqFile;
        this.name = name;
        try {
            this.pattern = (description == null) ? null : Pattern.compile(description);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("illegal regex pattern, caught " + e.getMessage());
        }
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastqFile);
            writer = writer(outputFastqFile);

            final PrintWriter w = writer;
            fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        try {
                            if (fastq.getDescription().equals(name)) {
                                fastqWriter.append(w, fastq);
                            }
                            else if (pattern != null) {
                                Matcher matcher = pattern.matcher(fastq.getDescription());
                                if (matcher.matches()) {
                                    fastqWriter.append(w, fastq);
                                }
                            }
                        }
                        catch (IOException e) {
                            throw new RuntimeException("could not write fastq", e);
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
        FileArgument inputFastqFile = new FileArgument("i", "input-fastq-file", "input FASTQ file, default stdin", false);
        FileArgument outputFastqFile = new FileArgument("o", "output-fastq-file", "output FASTQ file, default stdout", false);
        StringArgument name = new StringArgument("n", "name", "exact sequence name to match", false);
        StringArgument description = new StringArgument("d", "description", "FASTQ description regex pattern to match", false);

        ArgumentList arguments = new ArgumentList(about, help, inputFastqFile, outputFastqFile, name, description);
        CommandLine commandLine = new CommandLine(args);

        ExtractFastq extractFastq = null;
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
            extractFastq = new ExtractFastq(inputFastqFile.getValue(), outputFastqFile.getValue(), name.getValue(), description.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
