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
 * Output description lines from DNA sequences in FASTQ format.
 *
 * @author  Michael Heuer
 */
public final class FastqDescription implements Callable<Integer> {
    private final Path fastqPath;
    private final File descriptionFile;
    private final FastqReader fastqReader = new SangerFastqReader();
    private static final String USAGE = "dsh-fastq-description [args]";


    /**
     * Output description lines from DNA sequences in FASTQ format.
     *
     * @deprecated will be removed in version 3.0
     * @param fastqFile input FASTQ file, if any
     * @param descriptionFile output file of description lines, if any
     */
    public FastqDescription(final File fastqFile, final File descriptionFile) {
        this(fastqFile == null ? null : fastqFile.toPath(), descriptionFile);
    }

    /**
     * Output description lines from DNA sequences in FASTQ format.
     *
     * @since 2.1
     * @param fastqPath input FASTQ path, if any
     * @param descriptionFile output file of description lines, if any
     */
    public FastqDescription(final Path fastqPath, final File descriptionFile) {
        this.fastqPath = fastqPath;
        this.descriptionFile = descriptionFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(fastqPath);
            writer = writer(descriptionFile);

            final PrintWriter w = writer;
            fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq)
                    {
                        w.println(fastq.getDescription());
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
        PathArgument fastqPath = new PathArgument("i", "fastq-path", "input FASTQ path, default stdin", false);
        FileArgument descriptionFile = new FileArgument("o", "description-file", "output file of description lines, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, fastqPath, descriptionFile);
        CommandLine commandLine = new CommandLine(args);

        FastqDescription fastqDescription = null;
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
            fastqDescription = new FastqDescription(fastqPath.getValue(), descriptionFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(fastqDescription.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
