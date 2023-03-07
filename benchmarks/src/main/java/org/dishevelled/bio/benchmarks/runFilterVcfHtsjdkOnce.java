/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import java.io.File;

import htsjdk.tribble.AbstractFeatureReader;

import htsjdk.tribble.readers.LineIterator;

import htsjdk.variant.variantcontext.VariantContext;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;

import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Separate main to run FilterVcf-equivalent in htsjdk benchmark once.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class runFilterVcfHtsjdkOnce {
    private static AbstractFeatureReader<VariantContext, LineIterator> createReader(final File inputVcfFile) {
        return AbstractFeatureReader.getFeatureReader(inputVcfFile.getAbsolutePath(), new VCFCodec(), false);
    }

    private static VariantContextWriter createWriter(final File outputVcfFile) {
        return new VariantContextWriterBuilder().setOutputFile(outputVcfFile).setOutputFileType(VariantContextWriterBuilder.OutputType.VCF).unsetOption(Options.INDEX_ON_THE_FLY).build();
    }

    private static void filterVcfByQualityScore() throws Exception {
        File inputVcfFile = new File("src/main/resources/org/dishevelled/bio/benchmarks/HG001_GRCh38_GIAB_highconf_CG-IllFB-IllGATKHC-Ion-10X-SOLID_CHROM1-X_v.3.3.2_highconf_PGandRTGphasetransfer.10k.0.vcf.gz");
        File outputVcfFile = new File("filtered.vcf.gz");
        try (final AbstractFeatureReader<VariantContext, LineIterator> reader = createReader(inputVcfFile); final VariantContextWriter writer = createWriter(outputVcfFile)) {
            writer.writeHeader((VCFHeader) reader.getHeader());
            for (final VariantContext vc : reader.iterator()) {
                if (vc.getPhredScaledQual() >= 30.0d) {
                    writer.add(vc);
                }
            }
        }
    }

    /**
     * Main.
     *
     * @param args command line arguments, ignored
     * @throws Exception if an error occurs
     */
    public static void main(final String args[]) throws Exception {
        long t = System.nanoTime();
        filterVcfByQualityScore();
        System.out.println("took " + (System.nanoTime() - t));
    }
}
