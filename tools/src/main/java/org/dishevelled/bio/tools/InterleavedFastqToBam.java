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

import java.io.BufferedReader;
import java.io.File;

import java.util.List;

import javax.annotation.Nullable;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMUtils;

import htsjdk.samtools.util.SequenceUtil;
import htsjdk.samtools.util.StringUtil;

import org.biojava.bio.program.fastq.Fastq;

import org.dishevelled.bio.read.PairedEndAdapter;
import org.dishevelled.bio.read.PairedEndFastqReader;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.StringListArgument;

/**
 * Convert sequences in interleaved FASTQ format to unaligned BAM format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class InterleavedFastqToBam extends WithReadGroup {
    private final File fastqFile;
    private final File bamFile;
    private final SAMFileHeader header = new SAMFileHeader();
    private final SAMFileWriterFactory writerFactory = new SAMFileWriterFactory();
    private static final String USAGE = "dsh-interleaved-fastq-to-bam [args]";


    /**
     * Convert sequences in interleaved FASTQ format to unaligned BAM format.
     *
     * @param fastqFile input interleaved FASTQ file, if any
     * @param bamFile output BAM file, if any
     */
    public InterleavedFastqToBam(@Nullable final File fastqFile,
                                 @Nullable final File bamFile,
                                 @Nullable final String readGroupId,
                                 @Nullable final String readGroupSample,
                                 @Nullable final String readGroupLibrary,
                                 @Nullable final String readGroupPlatformUnit,
                                 @Nullable final Integer readGroupInsertSize,
                                 @Nullable final List<String> readGroupBarcodes) {

        super(readGroupId, readGroupSample, readGroupLibrary, readGroupPlatformUnit, readGroupInsertSize, readGroupBarcodes);
        this.fastqFile = fastqFile;
        this.bamFile = bamFile;

        // add read group to header if present
        toReadGroupOpt().ifPresent(readGroup -> header.addReadGroup(readGroup));
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        SAMFileWriter writer = null;
        try {
            reader = reader(fastqFile);

            if (bamFile == null) {
                writer = writerFactory.makeBAMWriter(header, false, System.out);
            }
            else {
                writer = writerFactory.makeBAMWriter(header, false, bamFile);
            }

            final SAMFileWriter sfw = writer;
            PairedEndFastqReader.streamInterleaved(reader, new PairedEndAdapter() {
                    @Override
                    public void paired(final Fastq left, final Fastq right) {
                        sfw.addAlignment(convertLeft(left));
                        sfw.addAlignment(convertRight(right));
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

    SAMRecord convertLeft(final Fastq left)
    {
        SAMRecord record = convert(left);
        record.setReadPairedFlag(true);
        record.setFirstOfPairFlag(true);
        return record;
    }

    SAMRecord convertRight(final Fastq right)
    {
        SAMRecord record = convert(right);
        record.setReadPairedFlag(true);
        record.setSecondOfPairFlag(true);
        return record;
    }

    private SAMRecord convert(final Fastq fastq)
    {
        SAMRecord record = new SAMRecord(header);
        record.setReadName(SequenceUtil.getSamReadNameFromFastqHeader(fastq.getDescription()));
        record.setReadBases(StringUtil.stringToBytes(fastq.getSequence()));
        record.setBaseQualities(SAMUtils.fastqToPhred(fastq.getQuality()));

        if (getReadGroupId() != null) {
            record.setAttribute("RG", getReadGroupId());

            if (getReadGroupLibrary() != null) {
                record.setAttribute("LB", getReadGroupLibrary());
            }
            if (getReadGroupPlatformUnit() != null) {
                record.setAttribute("PU", getReadGroupPlatformUnit());
            }
            if (getReadGroupInsertSize() != null) {
                record.setInferredInsertSize(getReadGroupInsertSize());
            }
        }
        record.setReadUnmappedFlag(true);
        return record;
    }
    
    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument fastqFile = new FileArgument("i", "input-fastq-file", "input interleaved FASTQ file, default stdin", false);
        FileArgument bamFile = new FileArgument("o", "output-bam-file", "output BAM file, default stdout", false);

        StringArgument readGroupId = createReadGroupIdArgument();
        StringArgument readGroupSample = createReadGroupSampleArgument();
        StringArgument readGroupLibrary = createReadGroupLibraryArgument();
        StringArgument readGroupPlatformUnit = createReadGroupPlatformUnitArgument();
        IntegerArgument readGroupInsertSize = createReadGroupInsertSizeArgument();
        StringListArgument readGroupBarcodes = createReadGroupBarcodesArgument();

        ArgumentList arguments = new ArgumentList(about, help, fastqFile, bamFile, readGroupId, readGroupSample, readGroupLibrary, readGroupPlatformUnit, readGroupInsertSize, readGroupBarcodes);
        CommandLine commandLine = new CommandLine(args);

        InterleavedFastqToBam interleavedFastqToBam = null;
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
            interleavedFastqToBam = new InterleavedFastqToBam(fastqFile.getValue(), bamFile.getValue(), readGroupId.getValue(), readGroupSample.getValue(), readGroupLibrary.getValue(), readGroupPlatformUnit.getValue(), readGroupInsertSize.getValue(), readGroupBarcodes.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(interleavedFastqToBam.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
