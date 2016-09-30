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

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Strand;

import org.dishevelled.bio.feature.BedRecord;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Feature to dishevelled BedRecord.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class FeatureToBedRecord extends AbstractConverter<Feature, BedRecord> {

    /** Convert Strand to String. */
    private final Converter<Strand, String> strandConverter;


    /**
     * Convert bdg-formats Feature to dishevelled BedRecord.
     *
     * @param strandConverter convert Strand to String, must not be null
     */
    FeatureToBedRecord(final Converter<Strand, String> strandConverter) {
        super(Feature.class, BedRecord.class);
        checkNotNull(strandConverter);
        this.strandConverter = strandConverter;
    }


    @Override
    public BedRecord convert(final Feature feature,
                             final ConversionStringency stringency,
                             final Logger logger) throws ConversionException {

        if (feature == null) {
            warnOrThrow(feature, "must not be null", null, stringency, logger);
            return null;
        }

        BedRecord bedRecord = null;
        try {
            String chrom = feature.getContigName();
            long start = feature.getStart();
            long end = feature.getEnd();
            String name = feature.getName();
            String score = feature.getScore() == null ? null : String.valueOf(feature.getScore());
            String strand = strandConverter.convert(feature.getStrand(), stringency, logger);

            if (!feature.getAttributes().containsKey("thickStart")) {
                // use BED6 format
                bedRecord = new BedRecord(chrom, start, end, name, score, strand);
            }
            else {
                long thickStart = Long.parseLong(feature.getAttributes().get("thickStart"));
                long thickEnd = Long.parseLong(feature.getAttributes().get("thickEnd"));
                String itemRgb = feature.getAttributes().get("itemRgb");
                int blockCount = Integer.parseInt(feature.getAttributes().get("blockCount"));
                long[] blockSizes = parseLongArray(feature.getAttributes().get("blockSizes"));
                long[] blockStarts = parseLongArray(feature.getAttributes().get("blockStarts"));

                // use BED12 format
                bedRecord = new BedRecord(chrom, start, end, name, score, strand, thickStart, thickEnd, itemRgb, blockCount, blockSizes, blockStarts);
            }
        }
        catch (NumberFormatException e) {
            warnOrThrow(feature, "caught NumberFormatException", e, stringency, logger);
        }
        catch (IllegalArgumentException e) {
            warnOrThrow(feature, "caught IllegalArgumentException", e, stringency, logger);
        }
        return bedRecord;
    }

    private static long[] parseLongArray(final String value) {
        List<String> tokens = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(value);
        long[] longs = new long[tokens.size()];
        for (int i = 0, size = tokens.size(); i < size; i++) {
            longs[i] = Long.parseLong(tokens.get(i));
        }
        return longs;
    }
}
