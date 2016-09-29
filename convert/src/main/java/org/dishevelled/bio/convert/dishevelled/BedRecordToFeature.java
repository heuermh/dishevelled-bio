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

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

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
public final class BedRecordToFeature extends AbstractConverter<BedRecord, Feature> {

    /** Convert String to Strand. */
    private final Converter<String, Strand> strandConverter;


    /**
     * Package private no-arg constructor.
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

        if (bedRecord.name() != null) {
            fb.setName(bedRecord.name());
        }
        if (bedRecord.score() != null) {
            try {
                fb.setScore(Double.valueOf(bedRecord.score()));
            }
            catch (NumberFormatException e) {
                warnOrThrow(bedRecord, "caught NumberFormatException", e, stringency, logger);
            }
        }
        if (bedRecord.strand() != null) {
            fb.setStrand(strandConverter.convert(bedRecord.strand(), stringency, logger));
        }

        //if (bedRecord.format() == BedRecord.Format.BED12) {
        if (bedRecord.itemRgb() != null) {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("thickStart", String.valueOf(bedRecord.thickStart()));
            attributes.put("thickEnd", String.valueOf(bedRecord.thickStart()));
            attributes.put("itemRgb", bedRecord.itemRgb());
            attributes.put("blockCount", String.valueOf(bedRecord.blockCount()));
            attributes.put("blockSizes", Arrays.asList(bedRecord.blockSizes()).stream().map(blockSize -> String.valueOf(blockSize)).collect(joining(",")));
            attributes.put("blockStarts", Arrays.asList(bedRecord.blockStarts()).stream().map(blockStart -> String.valueOf(blockStart)).collect(joining(",")));
            fb.setAttributes(attributes);
        }
        return fb.build();
    }
}
