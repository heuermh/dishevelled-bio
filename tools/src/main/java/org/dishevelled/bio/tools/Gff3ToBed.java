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

import java.util.List;

import java.util.concurrent.Callable;

import org.dishevelled.bio.feature.gff3.Gff3Listener;
import org.dishevelled.bio.feature.gff3.Gff3Reader;
import org.dishevelled.bio.feature.gff3.Gff3Record;

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
 * Convert transcript features in GFF3 format to BED format.
 *
 * @since 2.4
 * @author  Michael Heuer
 */
public final class Gff3ToBed implements Callable<Integer> {
    private final Path inputGff3Path;
    private final File outputBedFile;
    private static final String USAGE = "dsh-gff3-to-bed [args]";

    public Gff3ToBed(final Path inputGff3Path, final File outputBedFile) {
        this.inputGff3Path = inputGff3Path;
        this.outputBedFile = outputBedFile;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGff3Path);
            writer = writer(outputBedFile);

            final PrintWriter w = writer;
            Gff3Reader.stream(reader, new Gff3Listener() {
                    @Override
                    public boolean record(final Gff3Record gff3Record) {
                        if (accept(gff3Record)) {
                            BedRecord bedRecord = convert(gff3Record);
                            BedWriter.write(bedRecord, w);
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
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    static boolean accept(final Gff3Record gff3Record) {
        return gff3Record.getAttributes().containsKey("transcript_id");
    }

    static BedRecord convert(final Gff3Record gff3Record) {
        String chrom = gff3Record.getSeqid();
        long chromStart = gff3Record.getStart();
        long chromEnd = gff3Record.getEnd();

        // transcript_id is present, but might be multi-valued
        List<String> transcriptIds = gff3Record.getAttributes().get("transcript_id");
        String transcriptId = transcriptIds.get(0);

        // score may not be present
        int score = gff3Record.getScore() == null ? 0 : Math.max(0, Math.min(1000, (int) Math.round(gff3Record.getScore())));

        String strand = gff3Record.getStrand() == null ? "." : gff3Record.getStrand();
        long thickStart = chromStart;
        long thickEnd = chromEnd;
        String itemRgb = "0";
        
        // at least one block must be present
        int blockCount = 1;
        long[] blockSizes = new long[] { (chromEnd - chromStart) };
        long[] blockStarts = new long[] { 0L };

        return new BedRecord(chrom, chromStart, chromEnd, transcriptId, score, strand, thickStart, thickEnd, itemRgb, blockCount, blockSizes, blockStarts);
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument inputGff3Path = new PathArgument("i", "input-gff3-path", "input GFF3 path, default stdin", false);
        FileArgument outputBedFile = new FileArgument("o", "output-bed-file", "output BED file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGff3Path, outputBedFile);
        CommandLine commandLine = new CommandLine(args);

        Gff3ToBed gff3ToBed = null;
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
            gff3ToBed = new Gff3ToBed(inputGff3Path.getValue(), outputBedFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(gff3ToBed.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
