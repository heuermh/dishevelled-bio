/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
package org.dishevelled.bio.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.GenotypeAllele;

import org.dishevelled.bio.variant.vcf.VcfGenotype;
import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;

/**
 * Convert dishevelled VcfRecord to a list of bdg-formats Genotypes.
 *
 * @author  Michael Heuer
 */
@Immutable
final class VcfRecordToGenotypes extends AbstractConverter<VcfRecord, List<Genotype>> {

    /** Convert dishevelled VcfRecord to a list of bdg-formats Variants. */
    private final Converter<VcfRecord, List<Variant>> variantConverter;


    /**
     * Convert dishevelled VcfRecord to a list of bdg-formats Genotypes.
     *
     * @param variantConverter convert dishevelled VcfRecord to a list of bdg-formats Variants, must not be null
     */
    VcfRecordToGenotypes(final Converter<VcfRecord, List<Variant>> variantConverter) {
        super(VcfRecord.class, List.class);
        checkNotNull(variantConverter);
        this.variantConverter = variantConverter;
    }


    @Override
    public List<Genotype> convert(final VcfRecord vcfRecord,
                                  final ConversionStringency stringency,
                                  final Logger logger) throws ConversionException {

        if (vcfRecord == null) {
            warnOrThrow(vcfRecord, "must not be null", null, stringency, logger);
            return null;
        }

        List<Genotype> genotypes = new ArrayList<Genotype>();
        List<Variant> variants = variantConverter.convert(vcfRecord, stringency, logger);

        for (Variant variant : variants) {
            Genotype.Builder gb = Genotype.newBuilder()
                .setVariant(variant)
                .setReferenceName(variant.getReferenceName())
                .setStart(variant.getStart())
                .setEnd(variant.getEnd());

            for (Map.Entry<String, VcfGenotype> entry : vcfRecord.getGenotypes().entrySet()) {
                String sampleId = entry.getKey();
                gb.setSampleId(sampleId);

                VcfGenotype genotype = entry.getValue();
                String gt = genotype.getGt();

                List<GenotypeAllele> alleles = new ArrayList<GenotypeAllele>();
                if (gt.contains("/")) {
                    String[] indexes = gt.split("/");
                    for (String index : indexes) {
                        if (".".equals(index)) {
                            alleles.add(GenotypeAllele.NO_CALL);
                        }
                        else if ("0".equals(index)) {
                            alleles.add(GenotypeAllele.REF);
                        }
                        else if (variant.equals(variants.get(Integer.parseInt(index) - 1))) {
                            alleles.add(GenotypeAllele.ALT);
                        }
                        else {
                            alleles.add(GenotypeAllele.OTHER_ALT);
                        }
                    }
                }
                else if (gt.contains("|")) {
                    String[] indexes = gt.split("|");
                    for (String index : indexes) {
                        if (".".equals(index)) {
                            alleles.add(GenotypeAllele.NO_CALL);
                        }
                        else if ("0".equals(index)) {
                            alleles.add(GenotypeAllele.REF);
                        }
                        else if (variant.equals(variants.get(Integer.parseInt(index) - 1))) {
                            alleles.add(GenotypeAllele.ALT);
                        }
                        else {
                            alleles.add(GenotypeAllele.OTHER_ALT);
                        }
                    }
                }
                else {
                    if (".".equals(gt)) {
                        alleles.add(GenotypeAllele.NO_CALL);
                    }
                    else if ("0".equals(gt)) {
                        alleles.add(GenotypeAllele.REF);
                    }
                    else if (variant.equals(variants.get(Integer.parseInt(gt) - 1))) {
                        alleles.add(GenotypeAllele.ALT);
                    }
                    else {
                        alleles.add(GenotypeAllele.OTHER_ALT);
                    }
                }
                genotypes.add(gb.setAlleles(alleles).build());
            }
        }
        return genotypes;
    }
}
