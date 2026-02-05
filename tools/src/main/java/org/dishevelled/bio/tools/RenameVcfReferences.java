/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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

import java.util.ArrayList;
import java.util.List;

import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfSample;
import org.dishevelled.bio.variant.vcf.VcfWriter;
import org.dishevelled.bio.variant.vcf.VcfStreamAdapter;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLines;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLine;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Rename references in VCF files.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class RenameVcfReferences extends AbstractRenameReferences {
    private static final String USAGE = "dsh-rename-vcf-references [--chr] -i input.vcf.gz -o output.vcf.gz";


    /**
     * Rename references in VCF files.
     *
     * @deprecated will be removed in version 3.0
     * @param chr true to add "chr" to chromosome names
     * @param inputVcfFile input VCF file, if any
     * @param outputVcfFile output VCF file, if any
     */
    public RenameVcfReferences(final boolean chr, final File inputVcfFile, final File outputVcfFile) {
        this(chr, inputVcfFile == null ? null : inputVcfFile.toPath(), outputVcfFile);
    }

    /**
     * Rename references in VCF files.
     *
     * @since 2.1
     * @param chr true to add "chr" to chromosome names
     * @param inputVcfPath input VCF path, if any
     * @param outputVcfFile output VCF file, if any
     */
    public RenameVcfReferences(final boolean chr, final Path inputVcfPath, final File outputVcfFile) {
        super(chr, inputVcfPath, outputVcfFile);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            writer = writer(outputFile);

            final PrintWriter w = writer;
            VcfReader.stream(reader(inputPath), new VcfStreamAdapter() {
                    private boolean wroteSamples = false;
                    private List<VcfSample> samples = new ArrayList<VcfSample>();

                    @Override
                    public void header(final VcfHeader header) {
                        VcfHeaderLines headerLines = VcfHeaderLines.fromHeader(header);
                        // error if ##contig header lines present
                        if (!headerLines.getContigHeaderLines().isEmpty()) {
                            throw new RuntimeException("renaming references may cause inconsistencies when ##contig header lines present, e.g.:\n" + headerLines.getContigHeaderLines().values().iterator().next());
                        }
                        // error if ##reference header line present
                        for (VcfHeaderLine line : headerLines.getHeaderLines()) {
                            if ("reference".equals(line.getKey())) {
                                throw new RuntimeException("renaming references may cause inconsistencies when ##reference header line present:\n" + line.toString());
                            }
                        }
                        VcfWriter.writeHeader(header, w);
                    }

                    @Override
                    public void sample(final VcfSample sample) {
                        samples.add(sample);
                    }

                    @Override
                    public void record(final VcfRecord record) {
                        // write out samples
                        if (!wroteSamples) {
                            VcfWriter.writeColumnHeader(samples, w);
                            wroteSamples = true;
                        }

                        // write out record, renaming chrom
                        VcfRecord renamed = VcfRecord.builder(record)
                            .withChrom(rename(record.getChrom()))
                            .build();

                        VcfWriter.writeRecord(samples, renamed, w);
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
        PathArgument inputVcfPath = new PathArgument("i", "input-vcf-path", "input VCF path, default stdin", false);
        FileArgument outputVcfFile = new FileArgument("o", "output-vcf-file", "output VCF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, chr, inputVcfPath, outputVcfFile);
        CommandLine commandLine = new CommandLine(args);

        RenameVcfReferences renameVcfReferences = null;
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
            renameVcfReferences = new RenameVcfReferences(chr.wasFound(), inputVcfPath.getValue(), outputVcfFile.getValue());
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
            System.exit(renameVcfReferences.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
