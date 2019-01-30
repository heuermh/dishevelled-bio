/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import com.google.common.io.Files;

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
import org.dishevelled.commandline.argument.LongArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Split FASTA files.
 *
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class SplitFasta extends AbstractSplit {
    private final int lineWidth;
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
        super(inputFile, bytes, records, prefix, suffix);
        this.lineWidth = lineWidth;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputFile);

            long r = 0L;
            int files = 0;
            CountingWriter writer = null;

            for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
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
        }
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

    static final String getBaseName(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        // trim trailing .fa or .fasta if present after trimming compression extension
        if (baseName.endsWith(".fa")) {
            return baseName.substring(0, baseName.length() - 3);
        }
        else if (baseName.endsWith(".fasta")) {
            return baseName.substring(0, baseName.length() - 6);
        }
        return baseName;
    }

    static final String getFileExtensions(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        String extension = Files.getFileExtension(file.getName());
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
        FileArgument inputFile = new FileArgument("i", "input-file", "input FASTA file, default stdin", false);
        StringArgument bytes = new StringArgument("b", "bytes", "split input file at next record after each n bytes", false);
        LongArgument records = new LongArgument("r", "records", "split input file after each n records", false);
        StringArgument prefix = new StringArgument("p", "prefix", "output file prefix", false);
        StringArgument suffix = new StringArgument("s", "suffix", "output file suffix, e.g. .fa.gz", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);
        ArgumentList arguments = new ArgumentList(about, help, inputFile, bytes, records, prefix, suffix, lineWidth);
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
                if (inputFile.wasFound()) {
                    p = getBaseName(inputFile.getValue());
                }
                else {
                    p = "x";
                }
            }

            String s = suffix.getValue();
            if (!suffix.wasFound()) {
                if (inputFile.wasFound()) {
                    s = getFileExtensions(inputFile.getValue());
                }
                else {
                    // if (Compress.isBgzfInputStream(...)) {
                    //   s = ".fa.bgz";
                    // else if (Compress.isGzipInputStream(...)) { // method does not yet exist
                    //   s = ".fa.gz";
                    // else if (Compress.isBzip2InputStream(...)) { // method does not yet exist
                    //   s = ".fa.bz2"
                    // }
                    s = ".fa";
                }
            }

            splitFasta = new SplitFasta(inputFile.getValue(), b, records.getValue(), p, s, lineWidth.getValue(DEFAULT_LINE_WIDTH));
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
