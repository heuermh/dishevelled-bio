/*

    dsh-convert  Convert between various data models.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.convert.dishevelled;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

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
 * Convert dishevelled VcfRecord to a list of bdg-formats VariantAnnotations.
 *
 * @author  Michael Heuer
 */
@Immutable
final class VcfRecordToVariantAnnotations extends AbstractConverter<VcfRecord, List<VariantAnnotation>> {

    /** Convert dishevelled VcfRecord to a list of bdg-formats Variants. */
    private final Converter<VcfRecord, List<Variant>> variantConverter;

    /** Convert String to TranscriptEffect. */
    private final Converter<String, TranscriptEffect> transcriptEffectConverter;


    /**
     * Convert dishevelled VcfRecord to a list of bdg-formats VariantAnnotations.
     *
     * @param variantConverter convert dishevelled VcfRecord to a list of bdg-formats Variants, must not be null
     * @param transcriptEffectConverter convert String to TranscriptEffect, must not be null
     */
    VcfRecordToVariantAnnotations(final Converter<VcfRecord, List<Variant>> variantConverter,
                                  final Converter<String, TranscriptEffect> transcriptEffectConverter) {
        super(VcfRecord.class, List.class);
        checkNotNull(variantConverter);
        checkNotNull(transcriptEffectConverter);
        this.variantConverter = variantConverter;
        this.transcriptEffectConverter = transcriptEffectConverter;
    }


    @Override
    public List<VariantAnnotation> convert(final VcfRecord vcfRecord,
                                           final ConversionStringency stringency,
                                           final Logger logger) throws ConversionException {

        if (vcfRecord == null) {
            warnOrThrow(vcfRecord, "must not be null", null, stringency, logger);
            return null;
        }

        List<VariantAnnotation> variantAnnotations = new ArrayList<VariantAnnotation>();
        List<Variant> variants = variantConverter.convert(vcfRecord, stringency, logger);

        VariantAnnotation.Builder vab = VariantAnnotation.newBuilder();

        // Number=0, Number=1 VCF INFO reserved keys shared across all alternate alleles in the same VCF record
        vcfRecord.getInfo().get("AA").forEach(ancestralAllele -> vab.setAncestralAllele(ancestralAllele));
        vcfRecord.getInfo().get("DB").forEach(dbSnp -> vab.setDbSnp(Boolean.valueOf(dbSnp)));
        vcfRecord.getInfo().get("H2").forEach(hapMap2 -> vab.setHapMap2(Boolean.valueOf(hapMap2)));
        vcfRecord.getInfo().get("H3").forEach(hapMap3 -> vab.setHapMap3(Boolean.valueOf(hapMap3)));
        vcfRecord.getInfo().get("VALIDATED").forEach(validated -> vab.setValidated(Boolean.valueOf(validated)));
        vcfRecord.getInfo().get("1000G").forEach(thousandGenomes -> vab.setThousandGenomes(Boolean.valueOf(thousandGenomes)));

        for (int i = 0, size = variants.size(); i < size; i++) {
            Variant variant = variants.get(i);
            vab.setVariant(variant);

            final int index = i;
            // Number=A VCF INFO reserved keys split for multi-allelic sites by index
            vcfRecord.getInfo().get("AC").forEach(alleleCount -> vab.setAlleleCount(splitToInteger(vcfRecord, alleleCount, index, stringency, logger)));
            vcfRecord.getInfo().get("AF").forEach(alleleFrequency -> vab.setAlleleFrequency(splitToFloat(vcfRecord, alleleFrequency, index, stringency, logger)));
            vcfRecord.getInfo().get("CIGAR").forEach(cigar -> vab.setCigar(splitToString(vcfRecord, cigar, index, stringency, logger)));

            // Number=R VCF INFO reserved keys split for multi-allelic sites by index
            vcfRecord.getInfo().get("AD").forEach(readDepth -> vab.setReadDepth(splitToInteger(vcfRecord, readDepth, index + 1, stringency, logger)));
            vcfRecord.getInfo().get("ADF").forEach(forwardReadDepth -> vab.setForwardReadDepth(splitToInteger(vcfRecord, forwardReadDepth, index + 1, stringency, logger)));
            vcfRecord.getInfo().get("ADR").forEach(reverseReadDepth -> vab.setReverseReadDepth(splitToInteger(vcfRecord, reverseReadDepth, index + 1, stringency, logger)));

            // Number=. VCF INFO key ANN split for multi-allelic sites by alternate allele
            if (vcfRecord.getInfo().containsKey("ANN")) {
                List<TranscriptEffect> transcriptEffects = new ArrayList<TranscriptEffect>();
                for (String ann : vcfRecord.getInfo().get("ANN")) {
                    String[] tokens = ann.split(",");
                    for (String token : tokens) {
                        TranscriptEffect transcriptEffect = transcriptEffectConverter.convert(token, stringency, logger);
                        if (transcriptEffect != null && transcriptEffect.getAlternateAllele().equals(variant.getAlternateAllele())) {
                            transcriptEffects.add(transcriptEffect);
                        }
                    }
                }
                vab.setTranscriptEffects(transcriptEffects);
            }
            variantAnnotations.add(vab.build());
        }
        return variantAnnotations;
    }

    // todo: could/should these be conversions?
    Integer splitToInteger(final VcfRecord vcfRecord,
                           final String value,
                           final int index,
                           final ConversionStringency stringency,
                           final Logger logger) throws ConversionException {

        List<String> tokens = Splitter.on(",").splitToList(value);
        try {
            return Integer.valueOf(tokens.get(index));
        }
        catch (IndexOutOfBoundsException | NumberFormatException e) {
            warnOrThrow(vcfRecord, String.format("could not split %s to integer", value), e, stringency, logger);
        }
        return null;
    }

    Float splitToFloat(final VcfRecord vcfRecord,
                       final String value,
                       final int index,
                       final ConversionStringency stringency,
                       final Logger logger) throws ConversionException {

        List<String> tokens = Splitter.on(",").splitToList(value);
        try {
            return Float.valueOf(tokens.get(index));
        }
        catch (IndexOutOfBoundsException | NumberFormatException e) {
            warnOrThrow(vcfRecord, String.format("could not split %s to float", value), e, stringency, logger);
        }
        return null;
    }

    String splitToString(final VcfRecord vcfRecord,
                         final String value,
                         final int index,
                         final ConversionStringency stringency,
                         final Logger logger) throws ConversionException {

        List<String> tokens = Splitter.on(",").splitToList(value);
        try {
            return tokens.get(index);
        }
        catch (IndexOutOfBoundsException e) {
            warnOrThrow(vcfRecord, String.format("could not split %s to string", value), e, stringency, logger);
        }
        return null;
    }
}
