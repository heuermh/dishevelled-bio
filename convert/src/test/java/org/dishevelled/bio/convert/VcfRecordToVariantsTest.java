/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

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

import org.dishevelled.bio.variant.vcf.VcfGenotype;
import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VcfRecordToVariants.
 *
 * @author  Michael Heuer
 */
public final class VcfRecordToVariantsTest {
    private final Logger logger = LoggerFactory.getLogger(VcfRecordToVariantsTest.class);
    private Converter<String, TranscriptEffect> transcriptEffectConverter;
    private Converter<VcfRecord, List<Variant>> vcfRecordConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        transcriptEffectConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, TranscriptEffect>>() {}));
        vcfRecordConverter = new VcfRecordToVariants(transcriptEffectConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(vcfRecordConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        vcfRecordConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(vcfRecordConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(vcfRecordConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        // todo: make this a junit data provider?

        /*
#CHROM  POS     ID      REF     ALT     QUAL    FILTER  INFO    FORMAT  NA12878 NA12891 NA12892
1       14397   .       CTGT    C       139.12  IndelQD AC=2;AF=0.333;AN=6;BaseQRankSum=1.800;ClippingRankSum=0.138;DP=69;FS=7.786;MLEAC=2;MLEAF=0.333;MQ=26.84;MQ0=0;MQRankSum=-1.906;QD=1.55;ReadPosRankSum=0.384     GT:AD:DP:FT:GQ:PL       0/1:16,4:20:rd:99:120,0,827     0/1:8,2:10:dp;rd:60:60,0,414    0/0:39,0:39:PASS:99:0,116,2114
         */
        VcfGenotype na12878 = VcfGenotype.builder()
            .withRef("CTGT")
            .withAlt("C")
            .withField("GT", "0/1")
            .withField("AD", "16", "4")
            .withField("DP", "20")
            .withField("FT", "rd")
            .withField("GQ", "99")
            .withField("PL", "120", "0", "827")
            .build();

        VcfGenotype na12891 = VcfGenotype.builder()
            .withRef("CTGT")
            .withAlt("C")
            .withField("GT", "0/1")
            .withField("AD", "8", "2")
            .withField("DP", "10")
            .withField("FT", "dp", "rd")
            .withField("GQ", "60")
            .withField("PL", "60", "0", "414")
            .build();

        VcfGenotype na12892 = VcfGenotype.builder()
            .withRef("CTGT")
            .withAlt("C")
            .withField("GT", "0/0")
            .withField("AD", "39", "0")
            .withField("DP", "39")
            .withField("FT", "PASS")
            .withField("GQ", "99")
            .withField("PL", "0", "116", "2114")
            .build();

        ListMultimap<String, String> info = ArrayListMultimap.create();
        info.put("AC", "2");
        info.put("AF", "0.333");
        info.put("AN", "6");
        info.put("BaseQRankSum", "1.800");
        info.put("ClippingRankSum", "0.138");
        info.put("DP", "69");
        info.put("FS", "7.786");
        info.put("MLEAC", "0.333");
        info.put("MQ", "26.84");
        info.put("MQ0", "0");
        info.put("MQRankSum", "-1.906");
        info.put("QD", "1.55");
        info.put("ReadPosRankSum", "0.384");
        
        VcfRecord vcfRecord = VcfRecord.builder()
            .withChrom("1")
            .withPos(14297)
            .withRef("CTGT")
            .withAlt("C")
            .withQual(139.12d)
            .withFilter("IndelQD")
            .withInfo(info)
            .withFormat("GT", "AD", "DP", "FT", "GQ", "PL")
            .withGenotype("NA12878", na12878)
            .withGenotype("NA12891", na12891)
            .withGenotype("NA12892", na12892)
            .build();

        List<Variant> variants = vcfRecordConverter.convert(vcfRecord, ConversionStringency.STRICT, logger);
        assertNotNull(variants);
        assertEquals(1, variants.size());

        Variant variant = variants.get(0);
        assertEquals("1", variant.getContigName());
        assertEquals(Long.valueOf(14297L - 1L), variant.getStart());
        assertEquals(Long.valueOf(variant.getStart() + 4L), variant.getEnd());
        assertEquals("CTGT", variant.getReferenceAllele());
        assertEquals("C", variant.getAlternateAllele());
        assertTrue(variant.getNames().isEmpty());
    }
}
