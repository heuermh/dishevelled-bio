/*

    dsh-convert  Convert between various data models.
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
package org.dishevelled.bio.convert.dishevelled;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;

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

import org.bdgenomics.formats.avro.TranscriptEffect;
import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.VariantAnnotation;

import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VariantToVcfRecord.
 *
 * @author  Michael Heuer
 */
public final class VariantToVcfRecordTest {
    private final Logger logger = LoggerFactory.getLogger(VariantToVcfRecordTest.class);
    private Converter<TranscriptEffect, String> transcriptEffectConverter;
    private Converter<Variant, VcfRecord> variantConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        transcriptEffectConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<TranscriptEffect, String>>() {}));
        variantConverter = new VariantToVcfRecord(transcriptEffectConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(variantConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullTranscriptEffectConverter() {
        new VariantToVcfRecord(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        variantConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(variantConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(variantConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullAltStrict() {
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        variantConverter.convert(v, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullAltLenient() {
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        assertNull(variantConverter.convert(v, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullAltSilent() {
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        assertNull(variantConverter.convert(v, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        VariantAnnotation ann = VariantAnnotation.newBuilder()
            .setAncestralAllele("A")
            .setDbSnp(true)
            .setHapMap2(true)
            // hapMap3 is not set
            .setValidated(true)
            .setThousandGenomes(true)
            .setSomatic(false)
            .setAlleleCount(100)
            .setAlleleFrequency(0.5f)
            .setReferenceReadDepth(10)
            .setReadDepth(20)
            // todo:  create transcript effects
            // todo:  add non-reserved INFO key
            .build();
            
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .setAnnotation(ann)
            .build();

        VcfRecord record = variantConverter.convert(v, ConversionStringency.STRICT, logger);

        assertEquals("1", record.getChrom());
        assertEquals("A", record.getRef());
        assertEquals("T", record.getAlt()[0]);
        assertEquals(1001L, record.getPos());
        assertEquals("id", record.getId()[0]);
        assertEquals("PASS", record.getFilter()[0]);
        assertEquals("A", record.getAa());
        assertTrue(record.containsDb() && record.getDb());
        assertTrue(record.containsH2() && record.getH2());
        assertFalse(record.containsH3());
        assertTrue(record.containsValidated() && record.getValidated());
        assertTrue(record.contains1000g() && record.get1000g());
        assertFalse(record.containsSomatic());
        assertEquals(1, record.getAc().size());
        assertEquals(Integer.valueOf(100), record.getAc().get(0));
        assertEquals(1, record.getAf().size());
        assertEquals(0.5f, record.getAf().get(0), 0.1f);
        assertEquals(2, record.getAd().size());
        assertEquals(Integer.valueOf(10), record.getAd().get(0));
        assertEquals(Integer.valueOf(20), record.getAd().get(1));
        assertFalse(record.containsAdf());
        assertFalse(record.containsAdr());
        assertFalse(record.containsInfoKey("ANN"));
    }
}
