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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Path;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.LongArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.dishevelled.compress.Compress;

/**
 * Split FASTA files.
 *
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class SplitFasta extends AbstractSplit {
    private final String alphabet;
    private final int lineWidth;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_LINE_WIDTH = 70;
    static final String DESCRIPTION_LINE = "description_line";
    private static final String USAGE = "dsh-split-fasta -r 100 -i foo.fa.gz";


    /**
     * Split FASTA files.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     * @param lineWidth line width
     */
    public SplitFasta(final File inputFile,
                      final Long bytes,
                      final Long records,
                      final String prefix,
                      final String suffix,
                      final int lineWidth) {
        this(inputFile == null ? null : inputFile.toPath(),
             bytes,
             records,
             prefix,
             suffix,
             lineWidth);
    }

    /**
     * Split FASTA files.
     *
     * @since 2.1
     * @param inputPath input path, if any
     * @param bytes split the input path at next record after each n bytes, if any
     * @param records split the input path after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     * @param lineWidth line width
     */
    public SplitFasta(final Path inputPath,
                      final Long bytes,
                      final Long records,
                      final String prefix,
                      final String suffix,
                      final int lineWidth) {
        this(inputPath, DEFAULT_ALPHABET, bytes, records, prefix, -1, suffix, lineWidth);
    }

    /**
     * Split FASTA files.
     *
     * @since 1.3.2
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     * @param lineWidth line width
     */
    public SplitFasta(final File inputFile,
                      final Long bytes,
                      final Long records,
                      final String prefix,
                      final int leftPad,
                      final String suffix,
                      final int lineWidth) {
        this(inputFile == null ? null : inputFile.toPath(),
             bytes,
             records,
             prefix,
             leftPad,
             suffix,
             lineWidth);
    }

    /**
     * Split FASTA files.
     *
     * @since 2.1
     * @param inputPath input path, if any
     * @param bytes split the input path at next record after each n bytes, if any
     * @param records split the input path after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     * @param lineWidth line width
     */
    public SplitFasta(final Path inputPath,
                      final Long bytes,
                      final Long records,
                      final String prefix,
                      final int leftPad,
                      final String suffix,
                      final int lineWidth) {
        this(inputPath, DEFAULT_ALPHABET, bytes, records, prefix, leftPad, suffix, lineWidth);
    }

    /**
     * Split FASTA files.
     *
     * @since 2.0
     * @param inputFile input file, if any
     * @param alphabet input file alphabet { dna, protein }, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     * @param lineWidth line width
     */
    public SplitFasta(final File inputFile,
                      final String alphabet,
                      final Long bytes,
                      final Long records,
                      final String prefix,
                      final int leftPad,
                      final String suffix,
                      final int lineWidth) {
        this(inputFile == null ? null : inputFile.toPath(),
             alphabet,
             bytes,
             records,
             prefix,
             leftPad,
             suffix,
             lineWidth);
    }

    /**
     * Split FASTA files.
     *
     * @since 2.1
     * @param inputPath input path, if any
     * @param alphabet input path alphabet { dna, protein }, if any
     * @param bytes split the input path at next record after each n bytes, if any
     * @param records split the input path after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     * @param lineWidth line width
     */
    public SplitFasta(final Path inputPath,
                      final String alphabet,
                      final Long bytes,
                      final Long records,
                      final String prefix,
                      final int leftPad,
                      final String suffix,
                      final int lineWidth) {
        super(inputPath, bytes, records, prefix, leftPad, suffix);
        this.lineWidth = lineWidth;
        this.alphabet = alphabet;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputPath);

            long r = 0L;
            int files = 0;
            CountingWriter writer = null;

            for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();

                if (writer == null) {
                    writer = createCountingWriter(files);
                }
                try {
                    writeSequence(sequence, lineWidth, writer.asPrintWriter());
                    writer.flush();
                }
                catch (IOException e) {
                    // ignore
                }
                r++;

                if (r >= records || writer.getCount() >= bytes) {
                    r = 0L;
                    files++;

                    try {
                        writer.close();
                    }
                    catch (Exception e) {
                        // ignore
                    }
                    finally {
                        writer = null;
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
            closeWriters();
        }
    }

    boolean isProteinAlphabet() {
        return alphabet != null && (alphabet.equalsIgnoreCase("protein") || alphabet.equalsIgnoreCase("aa"));
    }

    // copied with mods from biojava-legacy FastaFormat, as it uses PrintStream not PrintWriter
    static String describeSequence(final Sequence sequence) {
        return sequence.getAnnotation().containsProperty(DESCRIPTION_LINE) ?
            (String) sequence.getAnnotation().getProperty(DESCRIPTION_LINE) : sequence.getName();
    }

    static void writeSequence(final Sequence sequence, final int lineWidth, final PrintWriter writer) {
        writer.print(">");
        writer.println(describeSequence(sequence));
        for (int i = 1, length = sequence.length(); i <= length; i += lineWidth) {
            writer.println(sequence.subStr(i, Math.min(i + lineWidth - 1, length)));
        }
    }

    static final String getBaseName(final Path path) {
        String baseName = getNameWithoutExtension(path);
        // trim trailing .fa or .fasta if present after trimming compression extension
        if (baseName.endsWith(".fa")) {
            return baseName.substring(0, baseName.length() - 3);
        }
        else if (baseName.endsWith(".fasta")) {
            return baseName.substring(0, baseName.length() - 6);
        }
        return baseName;
    }

    static final String getFileExtensions(final Path path) {
        String baseName = getNameWithoutExtension(path);
        String extension = getFileExtension(path);
        // add .fa or .fasta to extension if present
        if (baseName.endsWith(".fa")) {
            return ".fa." + extension;
        }
        else if (baseName.endsWith(".fasta")) {
            return ".fasta." + extension;
        }
        return extension;
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument inputPath = new PathArgument("i", "input-path", "input FASTA path, default stdin", false);
        StringArgument alphabet = new StringArgument("e", "alphabet", "input FASTA alphabet { dna, protein }, default dna", false);
        StringArgument bytes = new StringArgument("b", "bytes", "split input path at next record after each n bytes", false);
        LongArgument records = new LongArgument("r", "records", "split input path after each n records", false);
        StringArgument prefix = new StringArgument("p", "prefix", "output file prefix", false);
        IntegerArgument leftPad = new IntegerArgument("d", "left-pad", "left pad split index in output file name", false);
        StringArgument suffix = new StringArgument("s", "suffix", "output file suffix, e.g. .fa.gz", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);

        ArgumentList arguments = new ArgumentList(about, help, inputPath, alphabet, bytes, records, prefix, leftPad, suffix, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        SplitFasta splitFasta = null;
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

            Long b = bytes.wasFound() ? toBytes(bytes.getValue()) : null;

            String p = prefix.getValue();
            if (!prefix.wasFound()) {
                if (inputPath.wasFound()) {
                    p = getBaseName(inputPath.getValue());
                }
                else {
                    p = "x";
                }
            }

            String s = suffix.getValue();
            if (!suffix.wasFound()) {
                if (inputPath.wasFound()) {
                    s = getFileExtensions(inputPath.getValue());
                }
                else {
                    if (Compress.isBgzfInputStream(System.in)) {
                        s = ".fa.bgz";
                    }
                    else if (Compress.isGzipInputStream(System.in)) {
                        s = ".fa.gz";
                    }
                    else if (Compress.isBzip2InputStream(System.in)) {
                        s = ".fa.bz2";
                    }
                    else {
                        s = ".fa";
                    }
                }
            }

            splitFasta = new SplitFasta(inputPath.getValue(), alphabet.getValue(DEFAULT_ALPHABET), b, records.getValue(), p, leftPad.getValue(-1), s, lineWidth.getValue(DEFAULT_LINE_WIDTH));
        }
        catch (CommandLineParseException | NullPointerException e) {
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
            System.exit(splitFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
