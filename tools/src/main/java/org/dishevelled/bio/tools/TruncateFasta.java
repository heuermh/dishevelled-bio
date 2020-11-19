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

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.seq.SequenceTools;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Truncate sequences in FASTA format.
 *
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class TruncateFasta implements Callable<Integer> {
    private final File inputFastaFile;
    private final File outputFastaFile;
    private final int length;
    private final String alphabet;
    private final int lineWidth;
    static final int DEFAULT_LENGTH = 10000;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_LINE_WIDTH = 70;
    static final String DESCRIPTION_LINE = "description_line";
    private static final String USAGE = "dsh-truncate-fasta -l 1000 [args]";

    /**
     * Truncate sequences in FASTA format.
     *
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param length length, must be at least 0
     * @param lineWidth line width
     */
    public TruncateFasta(final File inputFastaFile, final File outputFastaFile, final int length, final int lineWidth) {
        this(inputFastaFile, outputFastaFile, length, DEFAULT_ALPHABET, lineWidth);
    }

    /**
     * Truncate sequences in FASTA format.
     *
     * @since 2.0
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param length length, must be at least 0
     * @param alphabet input FASTA file alphabet { dna, protein }, if any
     * @param lineWidth line width
     */
    public TruncateFasta(final File inputFastaFile,
                         final File outputFastaFile,
                         final int length,
                         final String alphabet,
                         final int lineWidth) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be at least zero");
        }
        this.inputFastaFile = inputFastaFile;
        this.outputFastaFile = outputFastaFile;
        this.length = length;
        this.alphabet = alphabet;
        this.lineWidth = lineWidth;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastaFile);
            writer = writer(outputFastaFile);

            for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();
                Sequence subSequence = SequenceTools.subSequence(sequence, 1, Math.min(length, sequence.length()));
                writeSequence(subSequence, lineWidth, writer);
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

    boolean isProteinAlphabet() {
        return alphabet != null && (alphabet.equalsIgnoreCase("protein") || alphabet.equalsIgnoreCase("aa"));
    }

    // copied with mods from biojava-legacy FastaFormat, as it uses PrintStream not PrintWriter
    static String describeSequence(final Sequence sequence) {
        return sequence.getAnnotation().containsProperty(DESCRIPTION_LINE) ?
            (String) sequence.getAnnotation().getProperty(DESCRIPTION_LINE) : (String) sequence.getName();
    }

    static void writeSequence(final Sequence sequence, final int lineWidth, final PrintWriter writer) throws IOException {
        writer.print(">");
        writer.println(describeSequence(sequence));
        for (int i = 1, length = sequence.length(); i <= length; i += lineWidth) {
            writer.println(sequence.subStr(i, Math.min(i + lineWidth - 1, length)));
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
        FileArgument inputFastaFile = new FileArgument("i", "input-fasta-file", "input FASTA file, default stdin", false);
        FileArgument outputFastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);
        IntegerArgument length = new IntegerArgument("l", "length", "length, default " + DEFAULT_LENGTH, false);
        StringArgument alphabet = new StringArgument("e", "alphabet", "input FASTA alphabet { dna, protein }, default dna", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);

        ArgumentList arguments = new ArgumentList(about, help, inputFastaFile, outputFastaFile, length, alphabet, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        TruncateFasta truncateFasta = null;
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
            truncateFasta = new TruncateFasta(inputFastaFile.getValue(), outputFastaFile.getValue(), length.getValue(DEFAULT_LENGTH), alphabet.getValue(DEFAULT_ALPHABET), lineWidth.getValue(DEFAULT_LINE_WIDTH));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(truncateFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
