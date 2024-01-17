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

import java.net.URL;

import java.util.concurrent.Callable;

import org.apache.commons.codec.digest.DigestUtils;

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
import org.dishevelled.commandline.argument.URLArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Create a SequenceDictionary from DNA sequences in FASTA format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
@SuppressWarnings("deprecation")
public final class CreateSequenceDictionary implements Callable<Integer> {
    private final String url;
    private final Path inputFastaPath;
    private final File outputSequenceDictionaryFile;
    private static final String USAGE = "dsh-create-sequence-dictionary [args]";

    /**
     * Create a SequenceDictionary from DNA sequences in FASTA format.
     *
     * @param inputFastaFile input FASTA file, if any
     * @param outputSequenceDictionaryFile output SequenceDictionary .dict file, if any
     */
    public CreateSequenceDictionary(final File inputFastaFile, final File outputSequenceDictionaryFile) {
        this(null, inputFastaFile == null ? null : inputFastaFile.toPath(), outputSequenceDictionaryFile);
    }

    /**
     * Create a SequenceDictionary from DNA sequences in FASTA format.
     *
     * @since 2.1
     * @param inputFastaPath input FASTA path, if any
     * @param outputSequenceDictionaryFile output SequenceDictionary .dict file, if any
     */
    public CreateSequenceDictionary(final Path inputFastaPath, final File outputSequenceDictionaryFile) {
        this(null, inputFastaPath, outputSequenceDictionaryFile);
    }

    /**
     * Create a SequenceDictionary from DNA sequences in FASTA format with the specified URL.
     *
     * @since 2.1
     * @param url URL, if any
     * @param inputFastaFile input FASTA file, if any
     * @param outputSequenceDictionaryFile output SequenceDictionary .dict file, if any
     */
    public CreateSequenceDictionary(final URL url,
                                    final File inputFastaFile,
                                    final File outputSequenceDictionaryFile) {

        this(url,
             inputFastaFile == null ? null : inputFastaFile.toPath(),
             outputSequenceDictionaryFile);
    }

    /**
     * Create a SequenceDictionary from DNA sequences in FASTA format with the specified URL.
     *
     * @since 2.1
     * @param url URL, if any
     * @param inputFastaPath input FASTA path, if any
     * @param outputSequenceDictionaryFile output SequenceDictionary .dict file, if any
     */
    public CreateSequenceDictionary(final URL url,
                                    final Path inputFastaPath,
                                    final File outputSequenceDictionaryFile) {
        this.inputFastaPath = inputFastaPath;
        this.outputSequenceDictionaryFile = outputSequenceDictionaryFile;
        if (url != null) {
            this.url = url.toString();
        }
        else {
            this.url = inputFastaPath == null ? null : inputFastaPath.toUri().toString();
        }
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastaPath);
            writer = writer(outputSequenceDictionaryFile);

            writer.println("@HD\tVN:1.6");
            for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();

                String name = sequence.getName();
                int length = sequence.length();
                String md5Hex = DigestUtils.md5Hex(sequence.seqString().toUpperCase());

                StringBuilder sb = new StringBuilder();
                sb.append("@SQ\tSN:");
                sb.append(name);
                sb.append("\tLN:");
                sb.append(length);
                sb.append("\tM5:");
                sb.append(md5Hex);
                if (url != null) {
                    sb.append("\tUR:");
                    sb.append(url);
                }
                writer.println(sb.toString());
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
        URLArgument url = new URLArgument("u", "url", "URL, default input FASTA path", false);
        PathArgument inputFastaPath = new PathArgument("i", "input-fasta-path", "input FASTA path, default stdin", false);
        FileArgument outputSequenceDictionaryFile = new FileArgument("o", "output-sequence-dictionary-file", "output SequenceDictionary .dict file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, url, inputFastaPath, outputSequenceDictionaryFile);
        CommandLine commandLine = new CommandLine(args);

        CreateSequenceDictionary createSequenceDictionary = null;
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
            createSequenceDictionary = new CreateSequenceDictionary(url.getValue(), inputFastaPath.getValue(), outputSequenceDictionaryFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(createSequenceDictionary.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
