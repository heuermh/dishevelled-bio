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

import java.util.Collections;
import java.util.List;

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

import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.GenotypeAllele;
import org.bdgenomics.formats.avro.TranscriptEffect;
import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.VariantAnnotation;

import org.dishevelled.bio.variant.vcf.VcfGenotype;
import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for GenotypesToVcfRecord.
 *
 * @author  Michael Heuer
 */
public final class GenotypesToVcfRecordTest {
    private final Logger logger = LoggerFactory.getLogger(GenotypesToVcfRecordTest.class);
    private Converter<TranscriptEffect, String> transcriptEffectConverter;
    private Converter<List<Genotype>, VcfRecord> genotypesConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        transcriptEffectConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<TranscriptEffect, String>>() {}));
        genotypesConverter = new GenotypesToVcfRecord(transcriptEffectConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(genotypesConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullTranscriptEffectConverter() {
        new GenotypesToVcfRecord(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        genotypesConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(genotypesConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(genotypesConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertEmptyStrict() {
        genotypesConverter.convert(Collections.<Genotype>emptyList(), ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertEmptyLenient() {
        assertNull(genotypesConverter.convert(Collections.<Genotype>emptyList(), ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertEmptySilent() {
        assertNull(genotypesConverter.convert(Collections.<Genotype>emptyList(), ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullVariantStrict() {
        Genotype g = Genotype.newBuilder()
            .setSampleId("sampleId")
            .build();

        genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullVariantLenient() {
        Genotype g = Genotype.newBuilder()
            .setSampleId("sampleId")
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullVariantSilent() {
        Genotype g = Genotype.newBuilder()
            .setSampleId("sampleId")
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullSampleIdStrict() {
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Genotype g = Genotype.newBuilder()
            .setVariant(v)
            .build();

        genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullSampleIdLenient() {
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Genotype g = Genotype.newBuilder()
            .setVariant(v)
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSampleIdSilent() {
        Variant v = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Genotype g = Genotype.newBuilder()
            .setVariant(v)
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertMismatchedVariantsStrict() {
        Variant v1 = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Variant v2 = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("C")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Genotype g1 = Genotype.newBuilder()
            .setVariant(v1)
            .setSampleId("sampleId1")
            .build();

        Genotype g2 = Genotype.newBuilder()
            .setVariant(v2)
            .setSampleId("sampleId2")
            .build();

        genotypesConverter.convert(ImmutableList.of(g1, g2), ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertMismatchedVariantsLenient() {
        Variant v1 = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Variant v2 = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("C")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Genotype g1 = Genotype.newBuilder()
            .setVariant(v1)
            .setSampleId("sampleId1")
            .build();

        Genotype g2 = Genotype.newBuilder()
            .setVariant(v2)
            .setSampleId("sampleId2")
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g1, g2), ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertMismatchedVariantsSilent() {
        Variant v1 = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("T")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Variant v2 = Variant.newBuilder()
            .setContigName("1")
            .setReferenceAllele("A")
            .setAlternateAllele("C")
            .setStart(1000L)
            .setEnd(1001L)
            .setNames(ImmutableList.of("id"))
            .setFiltersApplied(true)
            .setFiltersPassed(true)
            .build();

        Genotype g1 = Genotype.newBuilder()
            .setVariant(v1)
            .setSampleId("sampleId1")
            .build();

        Genotype g2 = Genotype.newBuilder()
            .setVariant(v2)
            .setSampleId("sampleId2")
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g1, g2), ConversionStringency.SILENT, logger));
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

        Genotype g = Genotype.newBuilder()
            .setVariant(v)
            .setSampleId("sampleId")
            .build();

        genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.STRICT, logger);
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

        Genotype g = Genotype.newBuilder()
            .setVariant(v)
            .setSampleId("sampleId")
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.LENIENT, logger));
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

        Genotype g = Genotype.newBuilder()
            .setVariant(v)
            .setSampleId("sampleId")
            .build();

        assertNull(genotypesConverter.convert(ImmutableList.of(g), ConversionStringency.SILENT, logger));
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

        Genotype g1 = Genotype.newBuilder()
            .setVariant(v)
            .setAlleles(ImmutableList.of(GenotypeAllele.REF, GenotypeAllele.ALT))
            .setSampleId("sampleId1")
            .build();

        Genotype g2 = Genotype.newBuilder()
            .setVariant(v)
            .setAlleles(ImmutableList.of(GenotypeAllele.OTHER_ALT, GenotypeAllele.NO_CALL))
            .setSampleId("sampleId2")
            .build();

        Genotype g3 = Genotype.newBuilder()
            .setVariant(v)
            .setAlleles(ImmutableList.of(GenotypeAllele.REF, GenotypeAllele.ALT))
            .setSampleId("sampleId3")
            .setPhased(true)
            .build();

        Genotype g4 = Genotype.newBuilder()
            .setVariant(v)
            .setAlleles(ImmutableList.of(GenotypeAllele.OTHER_ALT, GenotypeAllele.NO_CALL))
            .setSampleId("sampleId4")
            .setPhased(true)
            .build();

        VcfRecord record = genotypesConverter.convert(ImmutableList.of(g1, g2, g3, g4), ConversionStringency.STRICT, logger);

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

        assertEquals(4, record.getGenotypes().size());
        assertTrue(record.getGenotypes().containsKey("sampleId1"));
        assertTrue(record.getGenotypes().containsKey("sampleId2"));
        assertTrue(record.getGenotypes().containsKey("sampleId3"));
        assertTrue(record.getGenotypes().containsKey("sampleId4"));

        VcfGenotype genotype1 = record.getGenotypes().get("sampleId1");
        assertEquals("0/1", genotype1.getGt());

        VcfGenotype genotype2 = record.getGenotypes().get("sampleId2");
        assertEquals("./.", genotype2.getGt());

        VcfGenotype genotype3 = record.getGenotypes().get("sampleId3");
        assertEquals("0|1", genotype3.getGt());

        VcfGenotype genotype4 = record.getGenotypes().get("sampleId4");
        assertEquals(".|.", genotype4.getGt());
    }
}
