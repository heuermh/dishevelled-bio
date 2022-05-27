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

import java.nio.file.Path;

import java.util.Collections;
import java.util.List;

import java.util.concurrent.Callable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfSample;
import org.dishevelled.bio.variant.vcf.VcfWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Convert Ensembl variant table to VCF format.
 *
 * @author  Michael Heuer
 */
public class EnsemblVariantTableToVcf implements Callable<Integer>  {
    private final Path inputEnsemblVariantTablePath;
    private final File outputVcfFile;
    private static final String USAGE = "dsh-variant-table-to-vcf [args]";

    /**
     * Convert Ensembl variant table to VCF format
     *
     * @param inputEnsemblVariantTableFile input Ensembl variant table file, if any
     * @param outputVcfFile output VCF file, if any
     */
    public EnsemblVariantTableToVcf(final File inputEnsemblVariantTableFile, final File outputVcfFile) {
        this(inputEnsemblVariantTableFile == null ? null : inputEnsemblVariantTableFile.toPath(), outputVcfFile);
    }

    /**
     * Convert Ensembl variant table to VCF format
     *
     * @since 2.1
     * @param inputEnsemblVariantTablePath input Ensembl variant table path, if any
     * @param outputVcfFile output VCF file, if any
     */
    public EnsemblVariantTableToVcf(final Path inputEnsemblVariantTablePath, final File outputVcfFile) {
        this.inputEnsemblVariantTablePath = inputEnsemblVariantTablePath;
        this.outputVcfFile = outputVcfFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputEnsemblVariantTablePath);
            writer = writer(outputVcfFile);

            VcfHeader header = VcfHeader.builder()
                    .withFileFormat("VCFv4.2")
                    .withMeta("##fileformat=VCFv4.2")
                    .build();
            VcfWriter.writeHeader(header, writer);

            List<VcfSample> samples = Collections.emptyList();
            VcfWriter.writeColumnHeader(samples, writer);

            VcfRecord.Builder builder = VcfRecord.builder()
                    .withFilter("PASS")
                    .withFormat("");

            for (CSVRecord record : CSVFormat.DEFAULT.withHeader().parse(reader)) {
                String variantId = record.get("Variant ID");
                String[] location = record.get("Location").split(":");
                String[] alleles = record.get("Alleles").replace("-", ".").split("/");

                if (alleles.length != 2) {
                    // log warning, found e.g. COSMIC_MUTATION, HGMD_MUTATION, or CCC/-/CC, G/A/C/T
                    continue;
                }
                VcfRecord vcfRecord = builder.withChrom(location[0])
                        .withPos(Long.parseLong(location[1]))
                        .withRef(alleles[0])
                        .withAlt(alleles[1])
                        .withId(variantId)
                        .build();

                VcfWriter.writeRecord(samples, vcfRecord, writer);
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
        PathArgument inputEnsemblVariantTablePath = new PathArgument("i", "input-variant-table-path", "input Ensembl variant table path, default stdin", false);
        FileArgument outputVcfFile = new FileArgument("o", "output-vcf-file", "output VCF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputEnsemblVariantTablePath, outputVcfFile);
        CommandLine commandLine = new CommandLine(args);

        EnsemblVariantTableToVcf ensemblVariantTableToVcf = null;
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
            ensemblVariantTableToVcf = new EnsemblVariantTableToVcf(inputEnsemblVariantTablePath.getValue(), outputVcfFile.getValue());
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
        catch (IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(ensemblVariantTableToVcf.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
