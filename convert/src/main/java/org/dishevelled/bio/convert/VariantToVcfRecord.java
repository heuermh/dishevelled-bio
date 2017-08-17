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
import java.util.Map;

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
 * Convert bdg-formats Variant to a dishevelled VcfRecord.
 *
 * @author  Michael Heuer
 */
@Immutable
final class VariantToVcfRecord extends AbstractConverter<Variant, VcfRecord> {

    /** Convert TranscriptEffect to String. */
    private final Converter<TranscriptEffect, String> transcriptEffectConverter;


    /**
     * Convert bdg-formats Variant to a dishevelled VcfRecord.
     *
     * @param transcriptEffectConverter convert TranscriptEffect to String, must not be null
     */
    VariantToVcfRecord(final Converter<TranscriptEffect, String> transcriptEffectConverter) {
        super(Variant.class, VcfRecord.class);

        checkNotNull(transcriptEffectConverter);
        this.transcriptEffectConverter = transcriptEffectConverter;
    }


    @Override
    public VcfRecord convert(final Variant variant,
                             final ConversionStringency stringency,
                             final Logger logger) throws ConversionException {

        if (variant == null) {
            warnOrThrow(variant, "must not be null", null, stringency, logger);
            return null;
        }

        VcfRecord.Builder vb = VcfRecord.builder()
            .withLineNumber(-1L)
            .withChrom(variant.getContigName())
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

        try {
            return vb.build();
        }
        catch (NullPointerException | IllegalArgumentException e) {
            warnOrThrow(variant, e.getMessage(), e, stringency, logger);
        }
        return null;
    }

    private static String[] toStringArray(final String value) {
        return value == null ? null : new String[] { value };
    }

    private static String[] toStringArray(final List<String> values) {
        return values.toArray(new String[0]);
    }
}
