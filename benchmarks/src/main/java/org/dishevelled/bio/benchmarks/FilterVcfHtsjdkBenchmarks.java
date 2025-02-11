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

import htsjdk.tribble.AbstractFeatureReader;

import htsjdk.tribble.readers.LineIterator;

import htsjdk.variant.variantcontext.VariantContext;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;

import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * FilterVcf-equivalent in htsjdk benchmarks.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class FilterVcfHtsjdkBenchmarks {
    private File inputVcfFile;
    private File outputVcfFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        inputVcfFile = File.createTempFile("filterVcfHtsjdkBenchmarks", ".vcf.gz");
        outputVcfFile = File.createTempFile("filterVcfHtsjdkBenchmarks", ".vcf.gz");

        copyResource("HG001_GRCh38_GIAB_highconf_CG-IllFB-IllGATKHC-Ion-10X-SOLID_CHROM1-X_v.3.3.2_highconf_PGandRTGphasetransfer.10k.0.vcf.gz", inputVcfFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        inputVcfFile.delete();
        outputVcfFile.delete();
    }

    private AbstractFeatureReader<VariantContext, LineIterator> createReader() throws Exception {
        return AbstractFeatureReader.getFeatureReader(inputVcfFile.getAbsolutePath(), new VCFCodec(), false);
    }

    private VariantContextWriter createWriter() {
        return new VariantContextWriterBuilder()
            .setOutputFile(outputVcfFile)
            .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF)
            .unsetOption(Options.INDEX_ON_THE_FLY)
            .build();
    }

    @Benchmark
    public void filterVcfByQualityScore() throws Exception {
        try (final AbstractFeatureReader<VariantContext, LineIterator> reader = createReader(); final VariantContextWriter writer = createWriter()) {
            writer.writeHeader((VCFHeader) reader.getHeader());
            for (final VariantContext vc : reader.iterator()) {
                if (vc.getPhredScaledQual() >= 30.0d) {
                    writer.add(vc);
                }
            }
        }
    }
}
