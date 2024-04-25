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

import java.util.concurrent.Callable;

import org.dishevelled.bio.feature.bed.BedListener;
import org.dishevelled.bio.feature.bed.BedReader;
import org.dishevelled.bio.feature.bed.BedRecord;
import org.dishevelled.bio.feature.bed.BedWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Compress features in BED format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class CompressBed implements Callable<Integer> {
    private final Path inputBedPath;
    private final File outputBedFile;
    private static final String USAGE = "dsh-compress-bed [args]";

    /**
     * Compress features in BED format to splittable bgzf or bzip2 compression codecs.
     *
     * @deprecated will be removed in version 3.0
     * @param inputBedFile input BED file, if any
     * @param outputBedFile output BED file, if any
     */
    public CompressBed(final File inputBedFile, final File outputBedFile) {
        this(inputBedFile == null ? null : inputBedFile.toPath(), outputBedFile);
    }

    /**
     * Compress features in BED format to splittable bgzf or bzip2 compression codecs.
     *
     * @since 2.1
     * @param inputBedPath input BED path, if any
     * @param outputBedFile output BED file, if any
     */
    public CompressBed(final Path inputBedPath, final File outputBedFile) {
        this.inputBedPath = inputBedPath;
        this.outputBedFile = outputBedFile;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputBedPath);
            writer = writer(outputBedFile);

            final PrintWriter w = writer;
            BedReader.stream(reader, new BedListener() {
                    @Override
                    public boolean record(final BedRecord bedRecord) {
                        BedWriter.write(bedRecord, w);
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
        PathArgument inputBedPath = new PathArgument("i", "input-bed-path", "input BED path, default stdin", false);
        FileArgument outputBedFile = new FileArgument("o", "output-bed-file", "output BED file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputBedPath, outputBedFile);
        CommandLine commandLine = new CommandLine(args);

        CompressBed compressBed = null;
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
            compressBed = new CompressBed(inputBedPath.getValue(), outputBedFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressBed.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
