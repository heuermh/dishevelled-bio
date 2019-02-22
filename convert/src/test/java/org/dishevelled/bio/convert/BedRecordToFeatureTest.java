/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import org.dishevelled.bio.feature.BedRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for BedRecordToFeature.
 *
 * @author  Michael Heuer
 */
public final class BedRecordToFeatureTest {
    private final Logger logger = LoggerFactory.getLogger(BedRecordToFeatureTest.class);
    private Converter<String, Strand> strandConverter;
    private Converter<BedRecord, Feature> bedRecordConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        strandConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, Strand>>() {}));
        bedRecordConverter = new BedRecordToFeature(strandConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(bedRecordConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrandConverter() {
        new BedRecordToFeature(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        bedRecordConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(bedRecordConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(bedRecordConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        BedRecord bedRecord = BedRecord.valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189\t0,739,1347");
        Feature feature = bedRecordConverter.convert(bedRecord, ConversionStringency.STRICT, logger);
        assertEquals("chr1", feature.getReferenceName());
        assertEquals(Long.valueOf(11873L), feature.getStart());
        assertEquals(Long.valueOf(14409L), feature.getEnd());
        assertEquals("uc001aaa.3", feature.getName());
        assertEquals(0.0d, feature.getScore(), 0.1d);
        assertEquals(Strand.FORWARD, feature.getStrand());
        assertEquals("11873", feature.getAttributes().get("thickStart"));
        assertEquals("11873", feature.getAttributes().get("thickEnd"));
        assertEquals("0", feature.getAttributes().get("itemRgb"));
        assertEquals("3", feature.getAttributes().get("blockCount"));
        assertEquals("354,109,1189", feature.getAttributes().get("blockSizes"));
        assertEquals("0,739,1347", feature.getAttributes().get("blockStarts"));
    }
}
