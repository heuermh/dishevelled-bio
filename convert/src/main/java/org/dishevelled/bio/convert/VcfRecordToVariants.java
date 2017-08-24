/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.TranscriptEffect;
import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.VariantAnnotation;

import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;

/**
 * Convert dishevelled VcfRecord to a list of bdg-formats Variants.
 *
 * @author  Michael Heuer
 */
@Immutable
final class VcfRecordToVariants extends AbstractConverter<VcfRecord, List<Variant>> {

    /** Convert String to TranscriptEffect. */
    private final Converter<String, TranscriptEffect> transcriptEffectConverter;


    /**
     * Convert dishevelled VcfRecord to a list of bdg-formats Variants.
     *
     * @param transcriptEffectConverter convert String to TranscriptEffect, must not be null
     */
    VcfRecordToVariants(final Converter<String, TranscriptEffect> transcriptEffectConverter) {
        super(VcfRecord.class, List.class);

        checkNotNull(transcriptEffectConverter);
        this.transcriptEffectConverter = transcriptEffectConverter;
    }


    @Override
    public List<Variant> convert(final VcfRecord vcfRecord,
                                 final ConversionStringency stringency,
                                 final Logger logger) throws ConversionException {

        if (vcfRecord == null) {
            warnOrThrow(vcfRecord, "must not be null", null, stringency, logger);
            return null;
        }

        final Variant.Builder vb = Variant.newBuilder()
            .setContigName(vcfRecord.getChrom())
            .setReferenceAllele(vcfRecord.getRef())
            .setStart(vcfRecord.getPos() - 1L);

        vb.setEnd(vb.getStart() + vb.getReferenceAllele().length());

        if (vcfRecord.getId() != null && vcfRecord.getId().length > 0) {
            vb.setNames(ImmutableList.copyOf(vcfRecord.getId()));
        }

        if (vcfRecord.getFilter() == null
            || vcfRecord.getFilter().length == 0
            || isMissingValue(vcfRecord.getFilter()[0])) {

            vb.setFiltersApplied(false);
        }
        else if ("PASS".equals(vcfRecord.getFilter()[0])) {
            vb.setFiltersApplied(true);
            vb.setFiltersPassed(true);
        }
        else {
            vb.setFiltersApplied(true);
            vb.setFiltersPassed(false);
            vb.setFiltersFailed(ImmutableList.copyOf(vcfRecord.getFilter()));
        }

        final VariantAnnotation.Builder vab = VariantAnnotation.newBuilder();

        // Number=0, Number=1 VCF INFO reserved keys shared across all alternate alleles in the same VCF record
        vcfRecord.getAaOpt().ifPresent(ancestralAllele -> vab.setAncestralAllele(ancestralAllele));
        vcfRecord.getDbOpt().ifPresent(dbSnp -> vab.setDbSnp(Boolean.valueOf(dbSnp)));
        vcfRecord.getH2Opt().ifPresent(hapMap2 -> vab.setHapMap2(Boolean.valueOf(hapMap2)));
        vcfRecord.getH3Opt().ifPresent(hapMap3 -> vab.setHapMap3(Boolean.valueOf(hapMap3)));
        vcfRecord.getValidatedOpt().ifPresent(validated -> vab.setValidated(Boolean.valueOf(validated)));
        vcfRecord.get1000gOpt().ifPresent(thousandGenomes -> vab.setThousandGenomes(Boolean.valueOf(thousandGenomes)));
        vcfRecord.getSomaticOpt().ifPresent(somatic -> vab.setSomatic(Boolean.valueOf(somatic)));

        List<Variant> variants = new ArrayList<Variant>(vcfRecord.getAlt().length);
        for (int i = 0, size = vcfRecord.getAlt().length; i < size; i++) {
            String alt = vcfRecord.getAlt()[i];

            if (alt == null) {
                warnOrThrow(vcfRecord, "alt must not be null", null, stringency, logger);
            }
            else {
                Variant variant = vb.setAlternateAllele(alt).build();

                // Number=A VCF INFO reserved keys split for multi-allelic sites by index
                if (vcfRecord.containsAc()) {
                    vab.setAlleleCount(vcfRecord.getAc().get(i));
                }
                else {
                    vab.clearAlleleCount();
                }

                if (vcfRecord.containsAf()) {
                    vab.setAlleleFrequency(vcfRecord.getAf().get(i));
                }
                else {
                    vab.clearAlleleFrequency();
                }

                if (vcfRecord.containsCigar()) {
                    vab.setCigar(vcfRecord.getCigar().get(i));
                }
                else {
                    vab.clearCigar();
                }

                // Number=R VCF INFO reserved keys split for multi-allelic sites by index
                if (vcfRecord.containsAd()) {
                    vab.setReferenceReadDepth(vcfRecord.getAd().get(0));
                    vab.setReadDepth(vcfRecord.getAd().get(i + 1));
                }
                else {
                    vab.clearReferenceReadDepth();
                    vab.clearReadDepth();
                }

                if (vcfRecord.containsAdf()) {
                    vab.setReferenceForwardReadDepth(vcfRecord.getAdf().get(0));
                    vab.setForwardReadDepth(vcfRecord.getAdf().get(i + 1));
                }
                else {
                    vab.clearReferenceForwardReadDepth();
                    vab.clearForwardReadDepth();
                }

                if (vcfRecord.containsAdr()) {
                    vab.setReferenceReverseReadDepth(vcfRecord.getAdr().get(0));
                    vab.setReverseReadDepth(vcfRecord.getAdr().get(i + 1));
                }
                else {
                    vab.clearReferenceReverseReadDepth();
                    vab.clearReverseReadDepth();
                }

                // Number=. VCF INFO key ANN split for multi-allelic sites by alternate allele
                if (vcfRecord.containsInfoKey("ANN")) {
                    List<TranscriptEffect> transcriptEffects = new ArrayList<TranscriptEffect>();
                    for (String token : vcfRecord.getInfoStrings("ANN")) {
                        TranscriptEffect transcriptEffect = transcriptEffectConverter.convert(token, stringency, logger);
                        if (transcriptEffect != null && transcriptEffect.getAlternateAllele().equals(variant.getAlternateAllele())) {
                            transcriptEffects.add(transcriptEffect);
                        }
                    }
                    vab.setTranscriptEffects(transcriptEffects);
                }
                variant.setAnnotation(vab.build());
                variants.add(variant);
            }
        }
        return variants;
    }

    static boolean isMissingValue(final String value) {
        return ".".equals(value);
    }
}
