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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

import com.google.common.primitives.Longs;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Strand;

import org.dishevelled.bio.feature.BedRecord;

import org.slf4j.Logger;

/**
 * Convert dishevelled BedRecord to bdg-formats Feature.
 *
 * @author  Michael Heuer
 */
@Immutable
final class BedRecordToFeature extends AbstractConverter<BedRecord, Feature> {

    /** Convert String to Strand. */
    private final Converter<String, Strand> strandConverter;


    /**
     * Convert dishevelled BedRecord to bdg-formats Feature.
     *
     * @param strandConverter convert String to Strand, must not be null
     */
    BedRecordToFeature(final Converter<String, Strand> strandConverter) {
        super(BedRecord.class, Feature.class);

        checkNotNull(strandConverter);
        this.strandConverter = strandConverter;
    }


    @Override
    public Feature convert(final BedRecord bedRecord,
                           final ConversionStringency stringency,
                           final Logger logger) throws ConversionException {

        if (bedRecord == null) {
            warnOrThrow(bedRecord, "must not be null", null, stringency, logger);
            return null;
        }
        final Feature.Builder fb = Feature.newBuilder()
            .setContigName(bedRecord.chrom())
            .setStart(bedRecord.start())
            .setEnd(bedRecord.end());

        if (bedRecord.format().isAtLeastBED4() && !isMissingValue(bedRecord.name())) {
            fb.setName(bedRecord.name());
        }
        if (bedRecord.format().isAtLeastBED5() && !isMissingValue(bedRecord.score())) {
            try {
                fb.setScore(Double.valueOf(bedRecord.score()));
            }
            catch (NumberFormatException e) {
                warnOrThrow(bedRecord, "caught NumberFormatException", e, stringency, logger);
            }
        }
        if (bedRecord.format().isAtLeastBED6()) {
            fb.setStrand(strandConverter.convert(bedRecord.strand(), stringency, logger));
        }

        if (bedRecord.format().isAtLeastBED12()) {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("thickStart", String.valueOf(bedRecord.thickStart()));
            attributes.put("thickEnd", String.valueOf(bedRecord.thickEnd()));
            attributes.put("itemRgb", bedRecord.itemRgb());
            attributes.put("blockCount", String.valueOf(bedRecord.blockCount()));
            attributes.put("blockSizes", Joiner.on(",").join(Longs.asList(bedRecord.blockSizes())));
            attributes.put("blockStarts", Joiner.on(",").join(Longs.asList(bedRecord.blockStarts())));
            fb.setAttributes(attributes);
        }
        return fb.build();
    }

    static boolean isMissingValue(final String value) {
        return ".".equals(value);
    }
}
