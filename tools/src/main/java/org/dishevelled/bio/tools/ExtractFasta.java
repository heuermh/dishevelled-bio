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
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

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
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Extract matching DNA or protein sequences in FASTA format.
 *
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class ExtractFasta implements Callable<Integer> {
    private final Path inputFastaPath;
    private final File outputFastaFile;
    private final String alphabet;
    private final String name;
    private final Pattern pattern;
    private final int lineWidth;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_LINE_WIDTH = 70;
    static final String DESCRIPTION_LINE = "description_line";
    private static final String USAGE = "dsh-extract-fasta [args]";

    /**
     * Extract matching DNA or protein sequences in FASTA format.
     *
     * @deprecated will be removed in version 3.0
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param name exact sequence name to match, if any
     * @param description FASTA description line regex pattern to match, if any
     * @param lineWidth line width
     */
    public ExtractFasta(final File inputFastaFile,
                        final File outputFastaFile,
                        final String name,
                        final String description,
                        final int lineWidth) {

        this(inputFastaFile == null ? null : inputFastaFile.toPath(),
             outputFastaFile,
             name,
             description,
             DEFAULT_ALPHABET,
             lineWidth);
    }

    /**
     * Extract matching DNA or protein sequences in FASTA format.
     *
     * @since 2.1
     * @param inputFastaPath input FASTA path, if any
     * @param outputFastaFile output FASTA file, if any
     * @param name exact sequence name to match, if any
     * @param description FASTA description line regex pattern to match, if any
     * @param lineWidth line width
     */
    public ExtractFasta(final Path inputFastaPath,
                        final File outputFastaFile,
                        final String name,
                        final String description,
                        final int lineWidth) {

        this(inputFastaPath, outputFastaFile, name, description, DEFAULT_ALPHABET, lineWidth);
    }

    /**
     * Extract matching DNA or protein sequences in FASTA format.
     *
     * @since 2.0
     * @deprecated will be removed in version 3.0
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param name exact sequence name to match, if any
     * @param description FASTA description line regex pattern to match, if any
     * @param alphabet input FASTA file alphabet { dna, protein }, if any
     * @param lineWidth line width
     */
    public ExtractFasta(final File inputFastaFile,
                        final File outputFastaFile,
                        final String name,
                        final String description,
                        final String alphabet,
                        final int lineWidth) {

        this(inputFastaFile == null ? null : inputFastaFile.toPath(),
             outputFastaFile,
             name,
             description,
             alphabet,
             lineWidth);
    }

    /**
     * Extract matching DNA or protein sequences in FASTA format.
     *
     * @since 2.1
     * @param inputFastaPath input FASTA path, if any
     * @param outputFastaFile output FASTA file, if any
     * @param name exact sequence name to match, if any
     * @param description FASTA description line regex pattern to match, if any
     * @param alphabet input FASTA file alphabet { dna, protein }, if any
     * @param lineWidth line width
     */
    public ExtractFasta(final Path inputFastaPath,
                        final File outputFastaFile,
                        final String name,
                        final String description,
                        final String alphabet,
                        final int lineWidth) {

        this.inputFastaPath = inputFastaPath;
        this.outputFastaFile = outputFastaFile;
        this.name = name;
        try {
            this.pattern = (description == null) ? null : Pattern.compile(description);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("illegal regex pattern, caught " + e.getMessage());
        }
        this.alphabet = alphabet;
        this.lineWidth = lineWidth;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastaPath);
            writer = writer(outputFastaFile);

            for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();
                if (sequence.getName().equals(name)) {
                    writeSequence(sequence, lineWidth, writer);
                }
                else if (pattern != null) {
                    Matcher matcher = pattern.matcher(describeSequence(sequence));
                    if (matcher.matches()) {
                        writeSequence(sequence, lineWidth, writer);
                    }
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
        PathArgument inputFastaPath = new PathArgument("i", "input-fasta-path", "input FASTA path, default stdin", false);
        FileArgument outputFastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);
        StringArgument name = new StringArgument("n", "name", "exact sequence name to match", false);
        StringArgument description = new StringArgument("d", "description", "FASTA description line regex pattern to match", false);
        StringArgument alphabet = new StringArgument("e", "alphabet", "input FASTA alphabet { dna, protein }, default dna", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);

        ArgumentList arguments = new ArgumentList(about, help, inputFastaPath, outputFastaFile, name, description, alphabet, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        ExtractFasta extractFasta = null;
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
            extractFasta = new ExtractFasta(inputFastaPath.getValue(), outputFastaFile.getValue(), name.getValue(), description.getValue(), alphabet.getValue(DEFAULT_ALPHABET), lineWidth.getValue(DEFAULT_LINE_WIDTH));
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
