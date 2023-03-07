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

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;

import org.dishevelled.bio.variant.vcf.VcfPedigree.Relationship;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfSample;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

import org.dishevelled.graph.Edge;

/**
 * Extract a pedigree from VCF format.
 *
 * @author  Michael Heuer
 */
public final class VcfPedigree implements Callable<Integer> {
    private final Path inputVcfPath;
    private final File outputPedigreeFile;
    private static final String USAGE = "dsh-vcf-pedigree -i input.vcf.gz -o pedigree.txt";


    /**
     * Extract a pedigree from VCF format.
     *
     * @param inputVcfFile input VCF file, if any
     * @param outputPedigreeFile output pedigree file, if any
     */
    public VcfPedigree(final File inputVcfFile, final File outputPedigreeFile) {
        this(inputVcfFile == null ? null : inputVcfFile.toPath(), outputPedigreeFile);
    }

    /**
     * Extract a pedigree from VCF format.
     *
     * @since 2.1
     * @param inputVcfPath input VCF path, if any
     * @param outputPedigreeFile output pedigree file, if any
     */
    public VcfPedigree(final Path inputVcfPath, final File outputPedigreeFile) {
        this.inputVcfPath = inputVcfPath;
        this.outputPedigreeFile = outputPedigreeFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputVcfPath);
            writer = writer(outputPedigreeFile);

            for (Edge<VcfSample, Relationship> edge : VcfReader.pedigree(reader).getGraph().edges()) {

                // cytoscape edge.txt format
                String sourceSampleId = edge.source().getValue().getId();
                String sourceLabel = edge.getValue().getSourceLabel();
                String targetSampleId = edge.target().getValue().getId();
                writer.println(Joiner.on("\t").join(sourceSampleId, sourceLabel, targetSampleId));
            }
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
        PathArgument inputVcfPath = new PathArgument("i", "input-vcf-path", "input VCF path, default stdin", false);
        FileArgument outputPedigreeFile = new FileArgument("o", "output-pedigree-file", "output pedigree file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputVcfPath, outputPedigreeFile);
        CommandLine commandLine = new CommandLine(args);

        VcfPedigree vcfPedigree = null;
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
            vcfPedigree = new VcfPedigree(inputVcfPath.getValue(), outputPedigreeFile.getValue());
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
            System.exit(vcfPedigree.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
