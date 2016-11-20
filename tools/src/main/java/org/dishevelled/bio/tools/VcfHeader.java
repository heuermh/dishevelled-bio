/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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

import java.util.Collections;

import java.util.concurrent.Callable;

import org.dishevelled.bio.variant.vcf.VcfHeaderLines;
import org.dishevelled.bio.variant.vcf.VcfReader;
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

/**
 * Extract and validate header lines from VCF format.
 *
 * @author  Michael Heuer
 */
public final class VcfHeader implements Callable<Integer> {
    private final File inputVcfFile;
    private final File outputVcfHeaderFile;
    private final boolean validate;
    private static final String USAGE = "dsh-vcf-header --validate -i input.vcf.gz -o header.vcf.bgz";


    /**
     * Extract and validate header lines from VCF format.
     *
     * @param inputVcfFile input VCF file, if any
     * @param outputVcfHeaderFile output VCF header file, if any
     * @param validate true to validate
     */
    public VcfHeader(final File inputVcfFile, final File outputVcfHeaderFile, final boolean validate) {
        this.inputVcfFile = inputVcfFile;
        this.outputVcfHeaderFile = outputVcfHeaderFile;
        this.validate = validate;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputVcfHeaderFile);
            org.dishevelled.bio.variant.vcf.VcfHeader header = VcfReader.header(reader(inputVcfFile));

            if (validate) {
                VcfHeaderLines.fromHeader(header);
            }
            VcfWriter.write(header, Collections.<VcfSample>emptyList(), Collections.<VcfRecord>emptyList(), writer);

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
        FileArgument inputVcfFile = new FileArgument("i", "input-vcf-file", "input VCF file, default stdin", false);
        FileArgument outputVcfHeaderFile = new FileArgument("o", "output-vcf-header-file", "output VCF header file, default stdout", false);
        Switch validate = new Switch("d", "validate", "validate VCF header lines");

        ArgumentList arguments = new ArgumentList(about, help, inputVcfFile, outputVcfHeaderFile, validate);
        CommandLine commandLine = new CommandLine(args);

        VcfHeader vcfHeader = null;
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
            vcfHeader = new VcfHeader(inputVcfFile.getValue(), outputVcfHeaderFile.getValue(), validate.wasFound());
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
        try {
            System.exit(vcfHeader.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
