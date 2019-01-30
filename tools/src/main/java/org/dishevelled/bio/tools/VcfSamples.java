/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;

import org.dishevelled.bio.variant.vcf.VcfGenome;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfSample;
import org.dishevelled.bio.variant.vcf.VcfStreamAdapter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Extract samples from VCF format.
 *
 * @author  Michael Heuer
 */
public final class VcfSamples implements Callable<Integer> {
    private final File inputVcfFile;
    private final File outputSampleFile;
    private static final String USAGE = "dsh-vcf-samples -i input.vcf.gz -o samples.txt";


    /**
     * Extract samples from VCF format.
     *
     * @param inputVcfFile input VCF file, if any
     * @param outputSampleFile output sample file, if any
     */
    public VcfSamples(final File inputVcfFile, final File outputSampleFile) {
        this.inputVcfFile = inputVcfFile;
        this.outputSampleFile = outputSampleFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputSampleFile);

            final PrintWriter w = writer;
            VcfReader.stream(reader(inputVcfFile), new VcfStreamAdapter() {
                    @Override
                    public void sample(final VcfSample sample) {
                        if (sample.getGenomes().length == 0) {
                            w.println(sample.getId());
                        }
                        else {
                            for (VcfGenome genome : sample.getGenomes()) {
                                w.println(Joiner.on("\t").join(sample.getId(), genome.getId(), genome.getMixture(), genome.getDescription()));
                            }
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
        FileArgument inputVcfFile = new FileArgument("i", "input-vcf-file", "input VCF file, default stdin", false);
        FileArgument outputSampleFile = new FileArgument("o", "output-sample-file", "output sample file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputVcfFile, outputSampleFile);
        CommandLine commandLine = new CommandLine(args);

        VcfSamples vcfSamples = null;
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
            vcfSamples = new VcfSamples(inputVcfFile.getValue(), outputSampleFile.getValue());
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
            System.exit(vcfSamples.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
