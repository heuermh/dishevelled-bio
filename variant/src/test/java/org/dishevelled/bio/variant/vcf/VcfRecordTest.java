/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
package org.dishevelled.bio.variant.vcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.dishevelled.bio.variant.vcf.VcfRecord.builder;

import java.util.Map;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for VcfRecord.
 *
 * @author  Michael Heuer
 */
public final class VcfRecordTest {
    private long lineNumber;
    private String chrom;
    private long pos;
    private String[] id;
    private String ref;
    private String[] alt;
    private double qual;
    private String[] filter;
    private ListMultimap<String, String> info;
    private String[] format;
    private Map<String, VcfGenotype> genotypes;

    @Before
    public void setUp() {
        lineNumber = 42L;
        chrom = "22";
        pos = 16140370L;
        id = new String[] { "rs2096606" };
        ref = "A";
        alt = new String[] { "G" };
        qual = 100.0d;
        filter = new String[] { "PASS" };
        info = ImmutableListMultimap.<String, String>builder().build();
        format = new String[] { "GT" };
        VcfGenotype.Builder genotypeBuilder = VcfGenotype.builder().withRef("A").withAlt("G").withField("GT", "1|1");
        genotypes = ImmutableMap.<String, VcfGenotype>builder().put("NA19131", genotypeBuilder.build()).put("NA19223", genotypeBuilder.build()).build();
    }

    @Test
    public void testBuilder() {
        assertNotNull(builder());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithInfoNullKey() {
        builder().withInfo(null, "value");
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithInfoNull() {
        builder().withInfo(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithGenotypeNullSampleId() {
        builder().withGenotype(null, VcfGenotype.builder().withRef("A").withAlt("G").withField("GT", "1|1").build());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithGenotypeNullValue() {
        builder().withGenotype("NA19131", null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithGenotypesNullGenotypes() {
        builder().withGenotypes(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildNullChrom() {
        builder()
            .withLineNumber(lineNumber)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildNullRef() {
        builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildNullAlt() {
        builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildWithNullAlt() {
        builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(null)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildNullInfoValues() {
        builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo("NS", (String[]) null)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderMismatchedGenotypeRef() {
        VcfGenotype mismatchedRef = VcfGenotype.builder()
            .withRef("G")
            .withAlt(alt)
            .withField("GT", "1|1")
            .build();

        VcfRecord.builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotype("NA19139", mismatchedRef)
            .build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderMismatchedGenotypeAlt() {
        VcfGenotype mismatchedAlt = VcfGenotype.builder()
            .withRef(ref)
            .withAlt("C")
            .withField("GT", "1|1")
            .build();

        VcfRecord.builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotype("NA19139", mismatchedAlt)
            .build();
    }

    @Test
    public void testBuilderBuild() {
        VcfRecord record = builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();

        assertNotNull(record);
        assertEquals(lineNumber, record.getLineNumber());
        assertEquals(chrom, record.getChrom());
        assertEquals(pos, record.getPos());
        assertEquals(id, record.getId());
        assertEquals(ref, record.getRef());
        assertEquals(alt, record.getAlt());
        assertEquals(qual, record.getQual(), 0.1d);
        assertEquals(filter, record.getFilter());
        assertEquals(info, record.getInfo());
        assertEquals(format, record.getFormat());
        assertEquals(genotypes, record.getGenotypes());
    }

    @Test
    public void testBuilderReset() {
        VcfRecord record = builder()
            .withLineNumber(42L)
            .reset()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();

        assertNotNull(record);
        assertEquals(lineNumber, record.getLineNumber());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderNullVcfRecord() {
        builder(null);
    }

    @Test
    public void testBuilderVcfRecord() {
        VcfRecord record = builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();

        VcfRecord.Builder builder = builder(record);

        VcfRecord copy = builder.build();
        assertEquals(lineNumber, copy.getLineNumber());
        assertEquals(chrom, copy.getChrom());
        assertEquals(pos, copy.getPos());
        assertEquals(id, copy.getId());
        assertEquals(ref, copy.getRef());
        assertEquals(alt, copy.getAlt());
        assertEquals(qual, copy.getQual(), 0.1d);
        assertEquals(filter, copy.getFilter());
        assertEquals(info, copy.getInfo());
        assertEquals(format, copy.getFormat());
        assertEquals(genotypes, copy.getGenotypes());

        VcfRecord next = builder.withLineNumber(43L).build();
        assertEquals(43L, next.getLineNumber());
        assertEquals(chrom, next.getChrom());
        assertEquals(pos, next.getPos());
        assertEquals(id, next.getId());
        assertEquals(ref, next.getRef());
        assertEquals(alt, next.getAlt());
        assertEquals(qual, next.getQual(), 0.1d);
        assertEquals(filter, next.getFilter());
        assertEquals(info, next.getInfo());
        assertEquals(format, next.getFormat());
        assertEquals(genotypes, next.getGenotypes());
    }
}
