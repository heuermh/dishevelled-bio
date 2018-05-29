/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import java.io.File;

import com.google.common.collect.ImmutableList;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

/**
 * Separate main to run FilterSam-equivalent in htsjdk benchmark once.
 *
 * @author  Michael Heuer
 */
public final class runFilterSamHtsjdkOnce {

    public static void filterSamByMapq() throws Exception {
        SamReader reader = null;
        SAMFileWriter writer = null;
        try {
            File inputSamFile = new File("src/main/resources/org/dishevelled/bio/benchmarks/NA12878-platinum-chr20.10k.sam.gz");
            File outputSamFile = new File("filtered.sam.gz");

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

    /**
     * Main.
     *
     * @param args command line arguments, ignored
     */
    public static void main(final String args[]) throws Exception {
        long t = System.nanoTime();
        filterSamByMapq();
        System.out.println("took " + (System.nanoTime() - t));
    }
}
