/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfSample;
import org.dishevelled.bio.variant.vcf.VcfStreamListener;
import org.dishevelled.bio.variant.vcf.VcfWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.LongArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Split VCF files.
 *
 * @author  Michael Heuer
 */
public final class SplitVcf extends AbstractSplit {
    private static final String USAGE = "dsh-split-vcf -r 100 -i foo.vcf.bgz";

    /**
     * Split VCF files.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     */
    public SplitVcf(final File inputFile, final Long bytes, final Long records, final String prefix, final String suffix) {
        super(inputFile, bytes, records, prefix, suffix);
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputFile);

            VcfReader.stream(reader, new VcfStreamListener() {
                    private long r = 0L;
                    private int files = 0;
                    private CountingWriter writer;
                    private VcfHeader header;
                    private List<VcfSample> samples = new ArrayList<VcfSample>();

                    @Override
                    public void header(final VcfHeader header) {
                        this.header = header;
                    }

                    @Override
                    public void sample(final VcfSample sample) {
                        samples.add(sample);
                    }

                    @Override
                    public void record(final VcfRecord record) {
                        if (writer == null) {
                            writer = createCountingWriter(files);
                            VcfWriter.writeHeader(header, writer.asPrintWriter());
                            VcfWriter.writeColumnHeader(samples, writer.asPrintWriter());
                        }
                        VcfWriter.writeRecord(samples, record, writer.asPrintWriter());
                        try {
                            writer.flush();
                        }
                        catch (IOException e) {
                            // ignore
                        }
                        r++;

                        if (r >= records || writer.getCount() >= bytes) {
                            r = 0L;
                            files++;

                            try {
                                writer.close();
                            }
                            catch (Exception e) {
                                // ignore
                            }
                            finally {
                                writer = null;
                            }
                        }
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
        }
    }

    static final String getBaseName(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        // trim trailing .vcf if present
        return baseName.endsWith(".vcf") ? baseName.substring(baseName.length() - 4) : baseName;
    }

    static final String getFileExtensions(final File file) {
        String baseName = Files.getNameWithoutExtension(file.getName());
        String extension = Files.getFileExtension(file.getName());
        // add .vcf to extension if present
        return baseName.endsWith(".vcf") ? ".vcf." + extension : "." + extension;
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch help = new Switch("h", "help", "display help message");
        FileArgument inputFile = new FileArgument("i", "input-file", "input VCF file, default stdin", false);
        StringArgument bytes = new StringArgument("b", "bytes", "split input file at next record after each n bytes", false);
        LongArgument records = new LongArgument("r", "records", "split input file after each n records", false);
        StringArgument prefix = new StringArgument("p", "prefix", "output file prefix", false);
        StringArgument suffix = new StringArgument("s", "suffix", "output file suffix, e.g. .vcf.bgz", false);
        ArgumentList arguments = new ArgumentList(help, inputFile, bytes, records, prefix, suffix);
        CommandLine commandLine = new CommandLine(args);

        SplitVcf splitVcf = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }

            Long b = bytes.wasFound() ? toBytes(bytes.getValue()) : null;

            String p = prefix.getValue();
            if (!prefix.wasFound()) {
                if (inputFile.wasFound()) {
                    p = getBaseName(inputFile.getValue());
                }
                else {
                    p = "x";
                }
            }

            String s = suffix.getValue();
            if (!suffix.wasFound()) {
                if (inputFile.wasFound()) {
                    s = getFileExtensions(inputFile.getValue());
                }
                else {
                    // if (Compress.isBgzfInputStream(...)) {
                    //   s = ".vcf.bgz";
                    // else if (Compress.isGzipInputStream(...)) { // method does not yet exist
                    //   s = ".vcf.gz";
                    // else if (Compress.isBzip2InputStream(...)) { // method does not yet exist
                    //   s = ".vcf.bz2"
                    // }
                    s = ".vcf";
                }
            }

            splitVcf = new SplitVcf(inputFile.getValue(), b, records.getValue(), p, s);
        }
        catch (CommandLineParseException | NullPointerException e) {
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(splitVcf.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
