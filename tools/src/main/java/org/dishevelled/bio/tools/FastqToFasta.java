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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqReader;
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
 * Convert DNA sequences in FASTQ format to FASTA format.
 *
 * @author  Michael Heuer
 */
public final class FastqToFasta implements Callable<Integer> {
    private final Path fastqPath;
    private final File fastaFile;
    private final FastqReader fastqReader = new SangerFastqReader();
    private static final String USAGE = "dsh-fastq-to-fasta [args]";

    /**
     * Convert DNA sequences in FASTQ format to FASTA format.
     *
     * @param fastqFile input FASTQ file, if any
     * @param fastaFile output FASTA file, if any
     */
    public FastqToFasta(final File fastqFile, final File fastaFile) {
        this(fastqFile == null ? null : fastqFile.toPath(), fastaFile);
    }

    /**
     * Convert DNA sequences in FASTQ format to FASTA format.
     *
     * @since 2.1
     * @param fastqPath input FASTQ path, if any
     * @param fastaFile output FASTA file, if any
     */
    public FastqToFasta(final Path fastqPath, final File fastaFile) {
        this.fastqPath = fastqPath;
        this.fastaFile = fastaFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(fastqPath);
            writer = writer(fastaFile);

            final PrintWriter w = writer;
            fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq)
                    {
                        StringBuilder sb = new StringBuilder(1200);
                        sb.append(">");
                        sb.append(fastq.getDescription());
                        sb.append("\n");
                        sb.append(fastq.getSequence());
                        w.println(sb.toString());
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
        PathArgument fastqPath = new PathArgument("i", "input-fastq-path", "input FASTQ path, default stdin", false);
        FileArgument fastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, fastqPath, fastaFile);
        CommandLine commandLine = new CommandLine(args);

        FastqToFasta fastqToFasta = null;
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
            fastqToFasta = new FastqToFasta(fastqPath.getValue(), fastaFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(fastqToFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
