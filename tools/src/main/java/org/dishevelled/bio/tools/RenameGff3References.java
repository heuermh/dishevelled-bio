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
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

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

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Rename references in GFF3 files.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class RenameGff3References extends AbstractRenameReferences {
    private static final String USAGE = "dsh-rename-references [--chr] -i input.gff3.gz -o output.gff3.gz";


    /**
     * Rename references in GFF3 files.
     *
     * @param chr true to add "chr" to chromosome names
     * @param inputGff3File input GFF3 file, if any
     * @param outputGff3File output GFF3 file, if any
     */
    public RenameGff3References(final boolean chr, final File inputGff3File, final File outputGff3File) {
        super(chr, inputGff3File, outputGff3File);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFile);
            writer = writer(outputFile);

            final PrintWriter w = writer;
            Gff3Reader.stream(reader, new Gff3Listener() {
                    @Override
                    public boolean record(final Gff3Record record) {
                        Gff3Record renamed = new Gff3Record(rename(record.getSeqid()),
                                                            record.getSource(),
                                                            record.getFeatureType(),
                                                            record.getStart(),
                                                            record.getEnd(),
                                                            record.getScore(),
                                                            record.getStrand(),
                                                            record.getPhase(),
                                                            record.getAttributes());
                        Gff3Writer.write(renamed, w);
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
        FileArgument inputGff3File = new FileArgument("i", "input-gff3-file", "input GFF3 file, default stdin", false);
        FileArgument outputGff3File = new FileArgument("o", "output-gff3-file", "output GFF3 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, chr, inputGff3File, outputGff3File);
        CommandLine commandLine = new CommandLine(args);

        RenameGff3References renameGff3References = null;
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
            renameGff3References = new RenameGff3References(chr.wasFound(), inputGff3File.getValue(), outputGff3File.getValue());
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
            System.exit(renameGff3References.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
