/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import static org.dishevelled.bio.benchmarks.Utils.copyResource;

import java.io.File;

import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * FilterSam-equivalent in htsjdk benchmarks.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class FilterSamHtsjdkBenchmarks {
    private File inputSamFile;
    private File outputSamFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        inputSamFile = File.createTempFile("filterSamHtsjdkBenchmarks", ".sam");
        outputSamFile = File.createTempFile("filterSamHtsjdkBenchmarks", ".sam");

        copyResource("CEUTrio.HiSeq.WGS.b37.NA12878.20.21.10k.sam", inputSamFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        inputSamFile.delete();
        outputSamFile.delete();
    }

    @Benchmark
    public void filterSamByMapq() {
        SamReader reader = null;
        SAMFileWriter writer = null;
        try {
            reader = SamReaderFactory.makeDefault().open(inputSamFile);
            writer = new SAMFileWriterFactory().makeSAMOrBAMWriter(reader.getFileHeader(), true, outputSamFile);
            for (SAMRecord samRecord : reader) {
                if (samRecord.getMappingQuality() >= 30) {
                    writer.addAlignment(samRecord);
                }
            }
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }
}
