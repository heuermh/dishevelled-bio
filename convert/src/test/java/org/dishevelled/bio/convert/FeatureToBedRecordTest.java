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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Strand;

import org.dishevelled.bio.feature.BedFormat;
import org.dishevelled.bio.feature.BedRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for FeatureToBedRecord.
 *
 * @author  Michael Heuer
 */
public final class FeatureToBedRecordTest {
    private final Logger logger = LoggerFactory.getLogger(FeatureToBedRecordTest.class);
    private Converter<Strand, String> strandConverter;
    private Converter<Feature, BedRecord> featureConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        strandConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<Strand, String>>() {}));
        featureConverter = new FeatureToBedRecord(strandConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(featureConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrandConverter() {
        new FeatureToBedRecord(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        featureConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(featureConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(featureConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("thickStart", "11873");
        attributes.put("thickEnd", "11873");
        attributes.put("itemRgb", "0");
        attributes.put("blockCount", "3");
        attributes.put("blockSizes", "354,109,1189");
        attributes.put("blockStarts", "0,739,1347");

        Feature feature = Feature.newBuilder()
            .setContigName("chr1")
            .setStart(11873L)
            .setEnd(14409L)
            .setName("uc001aaa.3")
            .setStrand(Strand.FORWARD)
            .setAttributes(attributes)
            .build();

        BedRecord bedRecord = featureConverter.convert(feature, ConversionStringency.STRICT, logger);
        assertEquals("chr1", bedRecord.chrom());
        assertEquals(11873L, bedRecord.start());
        assertEquals(14409L, bedRecord.end());
        assertEquals("uc001aaa.3", bedRecord.name());
        assertNull(bedRecord.score());
        assertEquals("+", bedRecord.strand());
        assertEquals(11873L, bedRecord.thickStart());
        assertEquals(11873L, bedRecord.thickEnd());
        assertEquals("0", bedRecord.itemRgb());
        assertEquals(3, bedRecord.blockCount());
        assertEquals(3, bedRecord.blockSizes().length);
        assertEquals(354L, bedRecord.blockSizes()[0]);
        assertEquals(109L, bedRecord.blockSizes()[1]);
        assertEquals(1189L, bedRecord.blockSizes()[2]);
        assertEquals(3, bedRecord.blockStarts().length);
        assertEquals(0L, bedRecord.blockStarts()[0]);
        assertEquals(739L, bedRecord.blockStarts()[1]);
        assertEquals(1347L, bedRecord.blockStarts()[2]);
        assertEquals(BedFormat.BED12, bedRecord.format());
    }
}
