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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import org.dishevelled.bio.feature.gff3.Gff3Listener;
import org.dishevelled.bio.feature.gff3.Gff3Reader;
import org.dishevelled.bio.feature.gff3.Gff3Record;
import org.dishevelled.bio.feature.gff3.Gff3Writer;

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
 * Split GFF3 files.
 *
 * @author  Michael Heuer
 */
public final class SplitGff3 extends AbstractSplit {
    private static final String USAGE = "dsh-split-gff3 -r 100 -i foo.gff3.gz";

    /**
     * Split GFF3 files.
     *
     * @deprecated will be removed in version 3.0
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     */
    public SplitGff3(final File inputFile, final Long bytes, final Long records, final String prefix, final String suffix) {
        this(inputFile == null ? null : inputFile.toPath(),
             bytes,
             records,
             prefix,
             suffix);
    }

    /**
     * Split GFF3 files.
     *
     * @since 2.1
     * @param inputPath input path, if any
     * @param bytes split the input path at next record after each n bytes, if any
     * @param records split the input path after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     */
    public SplitGff3(final Path inputPath, final Long bytes, final Long records, final String prefix, final String suffix) {
        this(inputPath, bytes, records, prefix, -1, suffix);
    }

    /**
     * Split GFF3 files.
     *
     * @since 1.3.2
     * @deprecated will be removed in version 3.0
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     */
    public SplitGff3(final File inputFile, final Long bytes, final Long records, final String prefix, final int leftPad, final String suffix) {
        this(inputFile == null ? null : inputFile.toPath(),
             bytes,
             records,
             prefix,
             leftPad,
             suffix);
    }

    /**
     * Split GFF3 files.
     *
     * @since 2.1
     * @param inputPath input path, if any
     * @param bytes split the input path at next record after each n bytes, if any
     * @param records split the input path after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     */
    public SplitGff3(final Path inputPath, final Long bytes, final Long records, final String prefix, final int leftPad, final String suffix) {
        super(inputPath, bytes, records, prefix, leftPad, suffix);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputPath);

            Gff3Reader.stream(reader, new Gff3Listener() {
                    private long r = 0L;
                    private int files = 0;
                    private CountingWriter writer;

                    @Override
                    public boolean record(final Gff3Record rec) {
                        if (writer == null) {
                            writer = createCountingWriter(files);
                        }
                        Gff3Writer.write(rec, writer.asPrintWriter());
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

    static String getBaseName(final Path path) {
        String baseName = getNameWithoutExtension(path);
        // trim trailing .gff3 if present
        return baseName.endsWith(".gff3") ? baseName.substring(0, baseName.length() - 5) : baseName;
    }

    static String getFileExtensions(final Path path) {
        String baseName = getNameWithoutExtension(path);
        String extension = getFileExtension(path);
        // add .gff3 to extension if present
        return baseName.endsWith(".gff3") ? ".gff3." + extension : "." + extension;
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument inputPath = new PathArgument("i", "input-path", "input GFF3 path, default stdin", false);
        StringArgument bytes = new StringArgument("b", "bytes", "split input path at next record after each n bytes", false);
        LongArgument records = new LongArgument("r", "records", "split input path after each n records", false);
        StringArgument prefix = new StringArgument("p", "prefix", "output file prefix", false);
        IntegerArgument leftPad = new IntegerArgument("d", "left-pad", "left pad split index in output file name", false);
        StringArgument suffix = new StringArgument("s", "suffix", "output file suffix, e.g. .gff3.gz", false);

        ArgumentList arguments = new ArgumentList(about, help, inputPath, bytes, records, prefix, leftPad, suffix);
        CommandLine commandLine = new CommandLine(args);

        SplitGff3 splitGff3 = null;
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
                        s = ".gff3.bgz";
                    }
                    else if (Compress.isGzipInputStream(System.in)) {
                        s = ".gff3.gz";
                    }
                    else if (Compress.isBzip2InputStream(System.in)) {
                        s = ".gff3.bz2";
                    }
                    else {
                        s = ".gff3";
                    }
                }
            }

            splitGff3 = new SplitGff3(inputPath.getValue(), b, records.getValue(), p, leftPad.getValue(-1), s);
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
            System.exit(splitGff3.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
