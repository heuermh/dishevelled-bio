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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.GenotypeAllele;
import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.TranscriptEffect;
import org.bdgenomics.formats.avro.VariantAnnotation;

import org.dishevelled.bio.variant.vcf.VcfGenotype;
import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VcfRecordToVariantAnnotations.
 *
 * @author  Michael Heuer
 */
public final class VcfRecordToVariantAnnotationsTest {
    private final Logger logger = LoggerFactory.getLogger(VcfRecordToGenotypesTest.class);
    private Converter<VcfRecord, List<Variant>> variantsConverter;
    private Converter<String, TranscriptEffect> transcriptEffectConverter;
    private Converter<VcfRecord, List<VariantAnnotation>> vcfRecordConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        transcriptEffectConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, TranscriptEffect>>() {}));

        variantsConverter = new VcfRecordToVariants();
        vcfRecordConverter = new VcfRecordToVariantAnnotations(variantsConverter, transcriptEffectConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(vcfRecordConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullVariantConverter() {
        new VcfRecordToVariantAnnotations(null, transcriptEffectConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullTranscriptEffectConverter() {
        new VcfRecordToVariantAnnotations(variantsConverter, null);
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
        info.put("AC", "2"); // Number=A Integer
        info.put("AF", "0.333"); // Number=A Float
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
        info.put("DB", "true"); // Number=0 Flag
        info.put("AD", "4,42"); // Number=R Integer
        info.put("ANN", "C|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T|||||453|");
        
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

        List<VariantAnnotation> variantAnnotations = vcfRecordConverter.convert(vcfRecord, ConversionStringency.STRICT, logger);
        assertNotNull(variantAnnotations);
        assertEquals(1, variantAnnotations.size());

        VariantAnnotation variantAnnotation = variantAnnotations.get(0);
        assertTrue(variantAnnotation.getDbSnp()); // Number=0 Flag
        assertEquals(Integer.valueOf(2), variantAnnotation.getAlleleCount()); // Number=A Integer
        assertEquals(Float.valueOf(0.333f), variantAnnotation.getAlleleFrequency()); // Number=A Float
        assertEquals(Integer.valueOf(42), variantAnnotation.getReadDepth()); // Number=R Integer
        assertNotNull(variantAnnotation.getTranscriptEffects());
        assertEquals(1, variantAnnotation.getTranscriptEffects().size());

        TranscriptEffect transcriptEffect = variantAnnotation.getTranscriptEffects().get(0);
        assertEquals("C", transcriptEffect.getAlternateAllele());
        assertTrue(transcriptEffect.getEffects().contains("upstream_gene_variant"));
        assertEquals("TAS1R3", transcriptEffect.getGeneName());
        assertEquals("ENSG00000169962", transcriptEffect.getGeneId());
        assertEquals("transcript", transcriptEffect.getFeatureType());
        assertEquals("protein_coding", transcriptEffect.getBiotype());
        assertNull(transcriptEffect.getRank());
        assertNull(transcriptEffect.getTotal());
        assertEquals("c.-485C>T", transcriptEffect.getTranscriptHgvs());
        assertNull(transcriptEffect.getProteinHgvs());
        assertNull(transcriptEffect.getCdnaPosition());
        assertNull(transcriptEffect.getCdnaLength());
        assertNull(transcriptEffect.getCdsPosition());
        assertNull(transcriptEffect.getCdsLength());
        assertNull(transcriptEffect.getProteinPosition());
        assertNull(transcriptEffect.getProteinLength());
        assertEquals(Integer.valueOf(453), transcriptEffect.getDistance());
        assertTrue(transcriptEffect.getMessages().isEmpty());
    }
}
