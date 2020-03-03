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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.GenotypeAllele;
import org.bdgenomics.formats.avro.TranscriptEffect;
import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.VariantAnnotation;

import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;

/**
 * Convert a list of bdg-formats Genotypes to a dishevelled VcfRecord.
 *
 * @author  Michael Heuer
 */
@Immutable
final class GenotypesToVcfRecord extends AbstractConverter<List<Genotype>, VcfRecord> {

    /** Convert TranscriptEffect to String. */
    private final Converter<TranscriptEffect, String> transcriptEffectConverter;


    /**
     * Convert a list of bdg-formats Genotypes to a dishevelled VcfRecord.
     *
     * @param transcriptEffectConverter convert TranscriptEffect to String, must not be null
     */
    GenotypesToVcfRecord(final Converter<TranscriptEffect, String> transcriptEffectConverter) {
        super(List.class, VcfRecord.class);

        checkNotNull(transcriptEffectConverter);
        this.transcriptEffectConverter = transcriptEffectConverter;
    }


    @Override
    public VcfRecord convert(final List<Genotype> genotypes,
                             final ConversionStringency stringency,
                             final Logger logger) throws ConversionException {

        if (genotypes == null) {
            warnOrThrow(genotypes, "must not be null", null, stringency, logger);
            return null;
        }
        if (genotypes.isEmpty()) {
            warnOrThrow(genotypes, "must not be empty", null, stringency, logger);
            return null;
        }

        // verify all genotypes are from the same variant and sampleId is set
        Variant variant = null;
        for (Genotype genotype : genotypes) {
            if (genotype.getVariant() == null) {
                warnOrThrow(genotypes, "must not contain a genotype with null variant", null, stringency, logger);
                return null;
            }
            if (genotype.getSampleId() == null) {
                warnOrThrow(genotypes, "must not contain a genotype with null sampleId", null, stringency, logger);
                return null;
            }
            if (variant == null) {
                variant = genotype.getVariant();
            }
            else {
                if (!variant.equals(genotype.getVariant())) {
                    warnOrThrow(genotypes, "must not contain genotypes with different variants", null, stringency, logger);
                    return null;
                }
            }
        }

        // todo: can this be delegated to VariantsToVcfRecord? perhaps with copy ctr in VcfRecord.Builder

        VcfRecord.Builder vb = VcfRecord.builder()
            .withLineNumber(-1L)
            .withChrom(variant.getReferenceName())
            .withPos(variant.getStart() + 1L)
            .withId(toStringArray(variant.getNames()))
            .withRef(variant.getReferenceAllele())
            .withAlt(toStringArray(variant.getAlternateAllele()));

        if (variant.getFiltersApplied()) {
            if (variant.getFiltersPassed()) {
                vb.withFilter("PASS");
            }
            else {
                vb.withFilter(toStringArray(variant.getFiltersFailed()));
            }
        }

        List<String> format = new ArrayList<String>();
        VariantAnnotation ann = variant.getAnnotation();
        if (ann != null) {
            if (ann.getAncestralAllele() != null) {
                format.add("AA");
                vb.withInfo("AA", ann.getAncestralAllele());
            }
            if (ann.getAlleleCount() != null) {
                format.add("AC");
                vb.withInfo("AC", String.valueOf(ann.getAlleleCount()));
            }
            if (ann.getReadDepth() != null && ann.getReferenceReadDepth() != null) {
                format.add("AD");
                vb.withInfo("AD", String.valueOf(ann.getReferenceReadDepth()), String.valueOf(ann.getReadDepth()));
            }
            if (ann.getForwardReadDepth() != null && ann.getReferenceForwardReadDepth() != null) {
                format.add("ADF");
                vb.withInfo("ADF", String.valueOf(ann.getReferenceForwardReadDepth()), String.valueOf(ann.getForwardReadDepth()));
            }
            if (ann.getReverseReadDepth() != null && ann.getReferenceReverseReadDepth() != null) {
                format.add("ADR");
                vb.withInfo("ADR", String.valueOf(ann.getReferenceReverseReadDepth()), String.valueOf(ann.getReverseReadDepth()));
            }
            if (ann.getAlleleFrequency() != null) {
                format.add("AF");
                vb.withInfo("AF", String.valueOf(ann.getAlleleFrequency()));
            }
            if (ann.getCigar() != null) {
                format.add("CIGAR");
                vb.withInfo("CIGAR", ann.getCigar());
            }
            if (ann.getDbSnp() != null && ann.getDbSnp()) {
                format.add("DB");
                vb.withInfo("DB", "true");
            }
            if (ann.getHapMap2() != null && ann.getHapMap2()) {
                format.add("H2");
                vb.withInfo("H2", "true");
            }
            if (ann.getHapMap3() != null && ann.getHapMap3()) {
                format.add("H3");
                vb.withInfo("H3", "true");
            }
            if (ann.getValidated() != null && ann.getValidated()) {
                format.add("VALIDATED");
                vb.withInfo("VALIDATED", "true");
            }
            if (ann.getThousandGenomes() != null && ann.getThousandGenomes()) {
                format.add("1000G");
                vb.withInfo("1000G", "true");
            }
            if (ann.getSomatic() != null && ann.getSomatic()) {
                format.add("SOMATIC");
                vb.withInfo("SOMATIC", "true");
            }

            if (!ann.getTranscriptEffects().isEmpty()) {
                format.add("ANN");
                ann.getTranscriptEffects()
                    .stream()
                    .map(te -> transcriptEffectConverter.convert(te, stringency, logger))
                    .forEach(s -> vb.withInfo("ANN", s));
            }

            for (Map.Entry<String, String> entry : ann.getAttributes().entrySet()) {
                format.add(entry.getKey());
                vb.withInfo(entry.getKey(), entry.getValue());
            }
            vb.withFormat(toStringArray(format));
        }

        for (Genotype genotype : genotypes) {
            String sample = genotype.getSampleId();
            List<String> alleles = genotype.getAlleles()
                .stream()
                .map(a -> toGt(a))
                .collect(toList());

            if (genotype.getPhased() != null && genotype.getPhased()) {
                vb.withGenotype(sample, "GT", Joiner.on("|").join(alleles));
            }
            else {
                vb.withGenotype(sample, "GT", Joiner.on("/").join(alleles));
            }
        }

        try {
            return vb.build();
        }
        catch (NullPointerException | IllegalArgumentException e) {
            warnOrThrow(genotypes, e.getMessage(), e, stringency, logger);
        }
        return null;
    }

    static String toGt(final GenotypeAllele allele) {
        switch (allele) {
            case REF: return "0";
            case ALT: return "1";
            case OTHER_ALT:
            case NO_CALL:
            default:
                return ".";
        }
    }

    private static String[] toStringArray(final String value) {
        return value == null ? null : new String[] { value };
    }

    private static String[] toStringArray(final List<String> values) {
        return values.toArray(new String[0]);
    }
}
