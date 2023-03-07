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
package org.dishevelled.bio.variant.ann;

import static org.dishevelled.bio.variant.ann.SnpEffVariantAnnotation.annotate;
import static org.dishevelled.bio.variant.ann.SnpEffVariantAnnotation.valueOf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import java.util.List;

import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfRecordParser;

import org.junit.Test;

/**
 * Unit test for SnpEffVariantAnnotation.
 *
 * @author  Michael Heuer
 */
public class SnpEffVariantAnnotationTest {

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooShort() {
        valueOf("T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T|||||453");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfRankNumberFormatException() {
        valueOf("T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding|not a number|c.-485C>T|||||453|");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfCdnaPositionNumberFormatException() {
        valueOf("T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T||not a number|||453|");
    }

    @Test
    public void testEquals() {
        SnpEffVariantAnnotation ann1 = valueOf("T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T|||||453|");
        SnpEffVariantAnnotation ann2 = valueOf("T|intergenic_region|MODIFIER|CPTP-TAS1R3|ENSG00000224051-ENSG00000169962|intergenic_region|ENSG00000224051-ENSG00000169962|||n.1330861C>T||||||");
        assertTrue(ann1.equals(ann1));
        assertTrue(ann2.equals(ann2));
        assertFalse(ann1.equals(ann2));
        assertFalse(ann2.equals(ann1));
        assertFalse(ann1.equals(new Object()));
    }

    @Test
    public void testValueOf() {
        SnpEffVariantAnnotation ann = valueOf("T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T|||||453|");
        assertEquals("T", ann.getAlternateAllele());
        assertTrue(ann.getEffects().contains("upstream_gene_variant"));
        assertEquals("MODIFIER", ann.getAnnotationImpact());
        assertEquals("TAS1R3", ann.getGeneName());
        assertEquals("ENSG00000169962", ann.getGeneId());
        assertEquals("transcript", ann.getFeatureType());
        assertEquals("ENST00000339381.5", ann.getFeatureId());
        assertEquals("protein_coding", ann.getBiotype());
        assertNull(ann.getRank());
        assertNull(ann.getTotal());
        assertEquals("c.-485C>T", ann.getTranscriptHgvs());
        assertNull(ann.getProteinHgvs());
        assertNull(ann.getCdnaPosition());
        assertNull(ann.getCdnaLength());
        assertNull(ann.getCdsPosition());
        assertNull(ann.getCdsLength());
        assertNull(ann.getProteinPosition());
        assertNull(ann.getProteinLength());
        assertEquals(Integer.valueOf(453), ann.getDistance());
        assertTrue(ann.getMessages().isEmpty());
        assertEquals("T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T|||||453|", ann.toString());
    }

    @Test
    public void testAnnotate() throws Exception {
        StringReader reader = new StringReader("1\t1330861\trs527850923\tC\tT\t0.0\tPASS\tANN=T|upstream_gene_variant|MODIFIER|TAS1R3|ENSG00000169962|transcript|ENST00000339381.5|protein_coding||c.-485C>T|||||453|,T|downstream_gene_variant|MODIFIER|DVL1|ENSG00000107404|transcript|ENST00000378891.9|protein_coding||c.*5281G>A|||||4415|,T|downstream_gene_variant|MODIFIER|CPTP|ENSG00000224051|transcript|ENST00000343938.8|protein_coding||c.*3098C>T|||||1964|,T|downstream_gene_variant|MODIFIER|CPTP|ENSG00000224051|transcript|ENST00000464957.1|processed_transcript||n.*2867C>T|||||2867|,T|downstream_gene_variant|MODIFIER|CPTP|ENSG00000224051|transcript|ENST00000488011.1|protein_coding||c.*3390C>T|||||3390|WARNING_TRANSCRIPT_INCOMPLETE,T|downstream_gene_variant|MODIFIER|DVL1|ENSG00000107404|transcript|ENST00000610709.2|protein_coding||c.*5281G>A|||||4418|,T|downstream_gene_variant|MODIFIER|DVL1|ENSG00000107404|transcript|ENST00000378888.9|protein_coding||c.*5281G>A|||||4415|,T|intergenic_region|MODIFIER|CPTP-TAS1R3|ENSG00000224051-ENSG00000169962|intergenic_region|ENSG00000224051-ENSG00000169962|||n.1330861C>T||||||");
        for (VcfRecord record : VcfRecordParser.records(reader)) {
            List<SnpEffVariantAnnotation> annotations = annotate(record);
            assertEquals(8, annotations.size());
        }
    }
}
