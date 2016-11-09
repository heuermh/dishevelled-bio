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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Variant;

import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;

/**
 * Convert dishevelled VcfRecord to a list of bdg-formats Variants.
 *
 * @author  Michael Heuer
 */
@Immutable
final class VcfRecordToVariants extends AbstractConverter<VcfRecord, List<Variant>> {

    /**
     * Convert dishevelled VcfRecord to a list of bdg-formats Variants.
     */
    VcfRecordToVariants() {
        super(VcfRecord.class, List.class);
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

        vcfRecord.getInfo().get("SOMATIC").forEach(somatic -> vb.setSomatic(Boolean.valueOf(somatic)));

        List<Variant> variants = new ArrayList<Variant>(vcfRecord.getAlt().length);
        for (String alt : vcfRecord.getAlt()) {
            if (alt == null) {
                warnOrThrow(vcfRecord, "alt must not be null", null, stringency, logger);
            }
            else {
                variants.add(vb.setAlternateAllele(alt).build());
            }
        }
        return variants;
    }

    static boolean isMissingValue(final String value) {
        return ".".equals(value);
    }
}
