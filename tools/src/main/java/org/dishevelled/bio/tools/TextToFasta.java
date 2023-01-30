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
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Convert DNA or protein sequences in tab-separated values (tsv) text format to FASTA format.
 *
 * @since 2.2
 * @author  Michael Heuer
 */
public final class TextToFasta implements Callable<Integer> {
    private final Path textPath;
    private final File fastaFile;
    private final String alphabet;
    private final int lineWidth;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_LINE_WIDTH = 70;
    private static final String USAGE = "dsh-text-to-fasta [args]";


    /**
     * Convert DNA or protein sequences in tab-separated values (tsv) text format to FASTA format.
     *
     * @param textPath input text path, if any
     * @param fastaFile output FASTA file, if any
     * @param alphabet output FASTA file alphabet { dna, protein }, if any
     * @param lineWidth output line width
     */
    public TextToFasta(final Path textPath, final File fastaFile, final String alphabet, final int lineWidth) {
        this.textPath = textPath;
        this.fastaFile = fastaFile;
        this.alphabet = alphabet;
        this.lineWidth = lineWidth;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(textPath);
            writer = writer(fastaFile);

            long lineNumber = 0;
            while (reader.ready()) {
                lineNumber++;
                String[] tokens = reader.readLine().split("\t");
                if (tokens.length != 2) {
                    throw new IOException("expected 2 tokens, found " + tokens.length + " at line number " + lineNumber);
                }

                // todo: if strict, use biojava to validate input and write?
                String description = tokens[0];
                String sequence = tokens[1];
                writer.println(">" + description);
                for (int i = 0, length = sequence.length(); i <= length; i += lineWidth) {
                    writer.println(sequence.substring(i, Math.min(i + lineWidth, length)));
                }
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
        PathArgument textPath = new PathArgument("i", "input-text-path", "input text path, default stdin", false);
        FileArgument fastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);
        StringArgument alphabet = new StringArgument("e", "alphabet", "output FASTA alphabet { dna, protein }, default dna", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "output line width, default " + DEFAULT_LINE_WIDTH, false);

        ArgumentList arguments = new ArgumentList(about, help, textPath, fastaFile, alphabet, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        TextToFasta textToFasta = null;
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
            textToFasta = new TextToFasta(textPath.getValue(), fastaFile.getValue(), alphabet.getValue(DEFAULT_ALPHABET), lineWidth.getValue(DEFAULT_LINE_WIDTH));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(textToFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
