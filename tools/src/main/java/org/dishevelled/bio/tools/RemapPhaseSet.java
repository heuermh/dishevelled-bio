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
import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Callable;

import org.dishevelled.bio.variant.vcf.VcfGenotype;
import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfSample;
import org.dishevelled.bio.variant.vcf.VcfWriter;
import org.dishevelled.bio.variant.vcf.VcfStreamAdapter;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLines;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Remap PS Type=String phase set ids in VCF format to PS Type=Integer.
 *
 * @author  Michael Heuer
 */
public final class RemapPhaseSet implements Callable<Integer> {
    private final Path inputVcfPath;
    private final File outputVcfFile;
    private static final String USAGE = "dsh-remap-phase-set -i input.vcf.gz -o output.vcf.gz";


    /**
     * Remap Type=String PS phase set ids in VCF format to Type=Integer.
     *
     * @deprecated will be removed in version 3.0
     * @param inputVcfFile input VCF file, if any
     * @param outputVcfFile output VCF file, if any
     */
    public RemapPhaseSet(final File inputVcfFile, final File outputVcfFile) {
        this(inputVcfFile == null ? null : inputVcfFile.toPath(), outputVcfFile);
    }

    /**
     * Remap Type=String PS phase set ids in VCF format to Type=Integer.
     *
     * @since 2.1
     * @param inputVcfPath input VCF path, if any
     * @param outputVcfFile output VCF file, if any
     */
    public RemapPhaseSet(final Path inputVcfPath, final File outputVcfFile) {
        this.inputVcfPath = inputVcfPath;
        this.outputVcfFile = outputVcfFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputVcfFile);

            final PrintWriter w = writer;
            VcfReader.stream(reader(inputVcfPath), new VcfStreamAdapter() {
                    private boolean wroteSamples = false;
                    private boolean remapPhaseSet = false;
                    private List<VcfSample> samples = new ArrayList<VcfSample>();
                    private int phaseSetId = 0;
                    private Map<String, Integer> phaseSetIds = new HashMap<String, Integer>();

                    @Override
                    public void header(final VcfHeader header) {
                        VcfHeaderLines headerLines = VcfHeaderLines.fromHeader(header);
                        if (headerLines.getFormatHeaderLines().containsKey("PS") &&
                            VcfHeaderLineType.String.equals(headerLines.getFormatHeaderLines().get("PS").getType())) {

                            remapPhaseSet = true;
                            VcfHeader.Builder builder = VcfHeader.builder().withFileFormat(header.getFileFormat());
                            for (String meta : header.getMeta()) {
                                if (meta.startsWith("##FORMAT=<ID=PS")) {
                                    String description = headerLines.getFormatHeaderLines().get("PS").getDescription() + " (converted from Type=String to Type=Integer)";
                                    builder.withMeta("##FORMAT=<ID=PS,Number=1,Type=Integer,Description=\"" + description + "\">");
                                }
                                else {
                                    builder.withMeta(meta);
                                }
                            }
                            VcfWriter.writeHeader(builder.build(), w);
                        }
                        else {
                            VcfWriter.writeHeader(header, w);
                        }
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

                        // write out record, remapping phase set if necessary
                        if (remapPhaseSet) {
                            Map<String, VcfGenotype> genotypes = new HashMap<String, VcfGenotype>(record.getGenotypes().size());
                            for (Map.Entry<String, VcfGenotype> entry : record.getGenotypes().entrySet()) {
                                String sampleId = entry.getKey();
                                VcfGenotype genotype = entry.getValue();

                                if (genotype.containsFieldKey("PS")) {
                                    String oldPhaseSetId = genotype.getFieldString("PS");
                                    if (!phaseSetIds.containsKey(oldPhaseSetId)) {
                                        phaseSetIds.put(oldPhaseSetId, phaseSetId);
                                        phaseSetId++;
                                    }
                                    Integer newPhaseSetId = phaseSetIds.get(oldPhaseSetId);
                                    genotypes.put(sampleId, VcfGenotype.builder(genotype).replaceField("PS", newPhaseSetId.toString()).build());
                                }
                                else {
                                    genotypes.put(sampleId, genotype);
                                }
                            }
                            VcfWriter.writeRecord(samples, VcfRecord.builder(record).replaceGenotypes(genotypes).build(), w);
                        }
                        else {
                            VcfWriter.writeRecord(samples, record, w);
                        }
                    }
                });

            return 0;
        }
        finally {
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
        PathArgument inputVcfPath = new PathArgument("i", "input-vcf-path", "input VCF path, default stdin", false);
        FileArgument outputVcfFile = new FileArgument("o", "output-vcf-file", "output VCF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputVcfPath, outputVcfFile);
        CommandLine commandLine = new CommandLine(args);

        RemapPhaseSet remapPhaseSet = null;
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
            remapPhaseSet = new RemapPhaseSet(inputVcfPath.getValue(), outputVcfFile.getValue());
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
            System.exit(remapPhaseSet.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
