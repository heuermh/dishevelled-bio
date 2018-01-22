/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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

import com.google.common.io.Files;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.dishevelled.bio.read.PairedEndAdapter;
import org.dishevelled.bio.read.PairedEndFastqReader;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.LongArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Split interleaved FASTQ files.
 *
 * @author  Michael Heuer
 */
public final class SplitInterleavedFastq extends AbstractSplit {
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "dsh-split-interleaved-fastq -r 100 -i foo.ifq.gz";

    /**
     * Split interleaved FASTQ files.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next pair of records after each n bytes, if any
     * @param records split the input file after each n records, respecting pairs, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     */
    public SplitInterleavedFastq(final File inputFile, final Long bytes, final Long records, final String prefix, final String suffix) {
        super(inputFile, bytes, records, prefix, suffix);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputFile);

            PairedEndFastqReader.streamInterleaved(reader, new PairedEndAdapter() {
                    private long r = 0L;
                    private int files = 0;
                    private CountingWriter writer;

                    @Override
                    public void paired(final Fastq left, final Fastq right) {
                        if (writer == null) {
                            writer = createCountingWriter(files);
                        }
                        try {
                            fastqWriter.append(writer, left);
                            fastqWriter.append(writer, right);
                            writer.flush();
                        }
                        catch (IOException e) {
                            // ignore
                        }
                        r += 2;

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
        }
    }

    static final String getBaseName(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        // trim trailing .ifq if present
        return baseName.endsWith(".ifq") ? baseName.substring(baseName.length() - 4) : baseName;
    }

    static final String getFileExtensions(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        String extension = Files.getFileExtension(file.getName());
        // add .ifq to extension if present
        return baseName.endsWith(".ifq") ? ".ifq." + extension : "." + extension;
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch help = new Switch("h", "help", "display help message");
        FileArgument inputFile = new FileArgument("i", "input-file", "input interleaved FASTQ file, default stdin", false);
        StringArgument bytes = new StringArgument("b", "bytes", "split input file at next pair of records after each n bytes", false);
        LongArgument records = new LongArgument("r", "records", "split input file after each n records, respecting pairs", false);
        StringArgument prefix = new StringArgument("p", "prefix", "output file prefix", false);
        StringArgument suffix = new StringArgument("s", "suffix", "output file suffix, e.g. .ifq.gz", false);
        ArgumentList arguments = new ArgumentList(help, inputFile, bytes, records, prefix, suffix);
        CommandLine commandLine = new CommandLine(args);

        SplitInterleavedFastq splitInterleavedFastq = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
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
                    //   s = ".ifq.bgz";
                    // else if (Compress.isGzipInputStream(...)) { // method does not yet exist
                    //   s = ".ifq.gz";
                    // else if (Compress.isBzip2InputStream(...)) { // method does not yet exist
                    //   s = ".ifq.bz2"
                    // }
                    s = ".ifq";
                }
            }

            splitInterleavedFastq = new SplitInterleavedFastq(inputFile.getValue(), b, records.getValue(), p, s);
        }
        catch (CommandLineParseException | NullPointerException e) {
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(splitInterleavedFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
