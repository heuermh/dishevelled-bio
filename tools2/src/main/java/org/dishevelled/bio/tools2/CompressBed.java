/*

    dsh-bio-tools2  Command line tools.
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
package org.dishevelled.bio.tools2;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;

import org.dishevelled.bio.feature.bed.BedReader;
import org.dishevelled.bio.feature.bed.BedWriter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Compress features in BED format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@Command(
  name = "compress-bed",
  mixinStandardHelpOptions = true,
  sortOptions = false,
  usageHelpAutoWidth = true,
  resourceBundle = "org.dishevelled.bio.tools2.Messages",
  versionProvider = org.dishevelled.bio.tools2.About.class
)
public final class CompressBed implements Callable<Integer> {

    @Parameters(arity = "0..*")
    private List<File> inputBedFiles = new ArrayList<File>();

    @Option(names = { "-o", "--output-bed-file" })
    private File outputBedFile = null;


    /**
     * Compress features in BED format to splittable bgzf or bzip2 compression codecs.
     *
     * @since 3.0
     */
    public CompressBed() {
        this(null, null);
    }

    /**
     * Compress features in BED format to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputBedFile input BED file, if any
     * @param outputBedFile output BED file, if any
     */
    public CompressBed(final File inputBedFile, final File outputBedFile) {
        inputBedFiles.add(inputBedFile);
        this.outputBedFile = outputBedFile;
    }

    @Override
    public Integer call() throws Exception {
        if (inputBedFiles.isEmpty()) {
            inputBedFiles.add(null);
        }
        try (final PrintWriter writer = writer(outputBedFile)) {
            for (File inputBedFile : inputBedFiles) {
                try (BufferedReader reader = reader(inputBedFile)) {
                     BedReader.stream(reader, bedRecord -> {
                             BedWriter.write(bedRecord, writer);
                             return true;
                         });
                     }
            }
        }
        return 0;
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        // todo: warn if inputBedFiles is empty and interactive tty
        CommandLine commandLine = new CommandLine(new CompressBed());
        commandLine.setUsageHelpLongOptionsMaxWidth(42);
        System.exit(commandLine.execute(args));
    }
}
