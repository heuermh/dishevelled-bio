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
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import java.util.regex.Pattern;

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

/**
 * Rename references in BED files.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class RenameBedReferences extends AbstractRenameReferences {
    private static final String USAGE = "dsh-rename-bed-references [--chr] -i input.bed.gz -o output.bed.gz";


    /**
     * Rename references in BED files.
     *
     * @param chr true to add "chr" to chromosome names
     * @param inputBedFile input BED file, if any
     * @param outputBedFile output BED file, if any
     */
    public RenameBedReferences(final boolean chr, final File inputBedFile, final File outputBedFile) {
        super(chr, inputBedFile, outputBedFile);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFile);
            writer = writer(outputFile);

            final PrintWriter w = writer;
            BedReader.stream(reader, new BedListener() {
                    @Override
                    public boolean record(final BedRecord record) {
                        BedRecord renamed = null;
                        switch (record.getFormat()) {
                        case BED3:
                            renamed = new BedRecord(rename(record.getChrom()),
                                                    record.getStart(),
                                                    record.getEnd());
                            break;
                        case BED4:
                            renamed = new BedRecord(rename(record.getChrom()),
                                                    record.getStart(),
                                                    record.getEnd(),
                                                    record.getName());
                            break;
                        case BED5:
                            renamed = new BedRecord(rename(record.getChrom()),
                                                    record.getStart(),
                                                    record.getEnd(),
                                                    record.getName(),
                                                    record.getScore());
                            break;
                        case BED6:
                            renamed = new BedRecord(rename(record.getChrom()),
                                                    record.getStart(),
                                                    record.getEnd(),
                                                    record.getName(),
                                                    record.getScore(),
                                                    record.getStrand());
                            break;
                        case BED12:
                            renamed = new BedRecord(rename(record.getChrom()),
                                                    record.getStart(),
                                                    record.getEnd(),
                                                    record.getName(),
                                                    record.getScore(),
                                                    record.getStrand(),
                                                    record.getThickStart(),
                                                    record.getThickEnd(),
                                                    record.getItemRgb(),
                                                    record.getBlockCount(),
                                                    record.getBlockSizes(),
                                                    record.getBlockStarts());
                            break;
                        default:
                            break;
                        }
                        if (renamed != null) {
                            BedWriter.write(renamed, w);
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
                // empty
            }
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
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
        Switch chr = new Switch("c", "chr", "add \"chr\" to chromosome reference names");
        FileArgument inputBedFile = new FileArgument("i", "input-bed-file", "input BED file, default stdin", false);
        FileArgument outputBedFile = new FileArgument("o", "output-bed-file", "output BED file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, chr, inputBedFile, outputBedFile);
        CommandLine commandLine = new CommandLine(args);

        RenameBedReferences renameBedReferences = null;
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            renameBedReferences = new RenameBedReferences(chr.wasFound(), inputBedFile.getValue(), outputBedFile.getValue());
        }
        catch (CommandLineParseException e) {
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
        catch (NullPointerException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(renameBedReferences.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
