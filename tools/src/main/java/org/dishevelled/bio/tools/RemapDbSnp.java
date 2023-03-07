/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
import java.util.List;

import java.util.concurrent.Callable;

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
 * Remap DB Type=String flags in VCF format to DB Type=Flag and dbsnp Type=String fields.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class RemapDbSnp implements Callable<Integer> {
    private final Path inputVcfPath;
    private final File outputVcfFile;
    private static final String USAGE = "dsh-remap-dbsnp -i input.vcf.gz -o output.vcf.gz";


    /**
     * Remap DB Type=String flags in VCF format to DB Type=Flag and dbsnp Type=String fields.
     *
     * @param inputVcfFile input VCF file, if any
     * @param outputVcfFile output VCF file, if any
     */
    public RemapDbSnp(final File inputVcfFile, final File outputVcfFile) {
        this(inputVcfFile == null ? null : inputVcfFile.toPath(), outputVcfFile);
    }

    /**
     * Remap DB Type=String flags in VCF format to DB Type=Flag and dbsnp Type=String fields.
     *
     * @since 2.1
     * @param inputVcfPath input VCF path, if any
     * @param outputVcfFile output VCF file, if any
     */
    public RemapDbSnp(final Path inputVcfPath, final File outputVcfFile) {
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
                    private boolean remapDbSnp = false;
                    private List<VcfSample> samples = new ArrayList<VcfSample>();

                    @Override
                    public void header(final VcfHeader header) {
                        VcfHeaderLines headerLines = VcfHeaderLines.fromHeader(header);

                        // add additional dbsnp INFO field header line if necessary
                        if (headerLines.getInfoHeaderLines().containsKey("DB") &&
                            VcfHeaderLineType.String.equals(headerLines.getInfoHeaderLines().get("DB").getType())) {

                            remapDbSnp = true;
                            VcfHeader.Builder builder = VcfHeader.builder().withFileFormat(header.getFileFormat());
                            for (String meta : header.getMeta()) {
                                if (meta.startsWith("##INFO=<ID=DB")) {
                                    builder.withMeta("##INFO=<ID=DB,Number=0,Type=Flag,Description=\"dbSNP membership\">");
                                    builder.withMeta("##INFO=<ID=dbsnp,Number=1,Type=String,Description=\"dbSNP identifier\">");
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

                        // write out record, remapping DB INFO field if necessary
                        if (remapDbSnp && record.containsInfoKey("DB")) {
                            String dbSnpId = record.getInfoString("DB");
                            VcfRecord copy = VcfRecord.builder(record)
                                .replaceInfo("DB", "true")
                                .replaceInfo("dbsnp", dbSnpId)
                                .build();
                            VcfWriter.writeRecord(samples, copy, w);
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

        RemapDbSnp remapDbSnp = null;
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
            remapDbSnp = new RemapDbSnp(inputVcfPath.getValue(), outputVcfFile.getValue());
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
            System.exit(remapDbSnp.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
