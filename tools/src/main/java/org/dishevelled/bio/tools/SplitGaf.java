/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import org.dishevelled.bio.alignment.gaf.GafListener;
import org.dishevelled.bio.alignment.gaf.GafReader;
import org.dishevelled.bio.alignment.gaf.GafRecord;
import org.dishevelled.bio.alignment.gaf.GafWriter;

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

import org.dishevelled.compress.Compress;

/**
 * Split GAF files.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
public final class SplitGaf extends AbstractSplit {
    private static final String USAGE = "dsh-split-gaf -r 100 -i foo.gaf.bgz";

    /**
     * Split GAF files.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     */
    public SplitGaf(final File inputFile, final Long bytes, final Long records, final String prefix, final String suffix) {
        this(inputFile, bytes, records, prefix, -1, suffix);
    }

    /**
     * Split GAF files.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     */
    public SplitGaf(final File inputFile, final Long bytes, final Long records, final String prefix, final int leftPad, final String suffix) {
        super(inputFile, bytes, records, prefix, leftPad, suffix);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputFile);

            GafReader.stream(reader, new GafListener() {
                    private long r = 0L;
                    private int files = 0;
                    private CountingWriter writer;

                    @Override
                    public boolean record(final GafRecord record) {
                        if (writer == null) {
                            writer = createCountingWriter(files);
                        }
                        GafWriter.write(record, writer.asPrintWriter());
                        try {
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
                        return true;
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
            closeWriters();
        }
    }

    static final String getBaseName(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        // trim trailing .gaf if present
        return baseName.endsWith(".gaf") ? baseName.substring(0, baseName.length() - 4) : baseName;
    }

    static final String getFileExtensions(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        String extension = Files.getFileExtension(file.getName());
        // add .gaf to extension if present
        return baseName.endsWith(".gaf") ? ".gaf." + extension : "." + extension;
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument inputFile = new FileArgument("i", "input-file", "input GAF file, default stdin", false);
        StringArgument bytes = new StringArgument("b", "bytes", "split input file at next record after each n bytes", false);
        LongArgument records = new LongArgument("r", "records", "split input file after each n records", false);
        StringArgument prefix = new StringArgument("p", "prefix", "output file prefix", false);
        IntegerArgument leftPad = new IntegerArgument("d", "left-pad", "left pad split index in output file name", false);
        StringArgument suffix = new StringArgument("s", "suffix", "output file suffix, e.g. .gaf.bgz", false);
        ArgumentList arguments = new ArgumentList(about, help, inputFile, bytes, records, prefix, leftPad, suffix);
        CommandLine commandLine = new CommandLine(args);

        SplitGaf splitGaf = null;
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
                    if (Compress.isBgzfInputStream(System.in)) {
                        s = ".gaf.bgz";
                    }
                    else if (Compress.isGzipInputStream(System.in)) {
                        s = ".gaf.gz";
                    }
                    else if (Compress.isBzip2InputStream(System.in)) {
                        s = ".gaf.bz2";
                    }
                    else {
                        s = ".gaf";
                    }
                }
            }

            splitGaf = new SplitGaf(inputFile.getValue(), b, records.getValue(), p, leftPad.getValue(-1), s);
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
            System.exit(splitGaf.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
