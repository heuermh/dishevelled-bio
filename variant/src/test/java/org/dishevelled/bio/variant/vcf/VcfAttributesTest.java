/*

    dsh-bio-variant  Variants.
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
package org.dishevelled.bio.variant.vcf;

import static org.junit.Assert.assertEquals;

import static org.dishevelled.bio.variant.vcf.VcfAttributes.*;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for VcfAttributes.
 *
 * @author  Michael Heuer
 */
public final class VcfAttributesTest {
    private Map<String, VcfGenotype> genotypes;
    private VcfRecord record;

    @Before
    public void setUp() throws Exception {
        VcfGenotype.Builder genotypeBuilder = VcfGenotype.builder().withField("GT", "1|1");

        genotypes = ImmutableMap.<String, VcfGenotype>builder()
            .put("NA19131", genotypeBuilder.build())
            .put("K=2", genotypeBuilder.build())
            .put("K=3", genotypeBuilder.reset().withField("GT", "0/0/1").build())
            .put("K=4", genotypeBuilder.reset().withField("GT", "0/0/0/1").build())
            .put("K=5", genotypeBuilder.reset().withField("GT", "0/0/0/0/1").build())
            .put("K=6", genotypeBuilder.reset().withField("GT", "0/0/0/0/0/1").build())
            .build();

        record = VcfRecord.builder()
            .withLineNumber(3L)
            .withChrom("22")
            .withPos(16140370L)
            .withId(new String[] { "rs2096606" })
            .withRef("A")
            .withAlt(new String[] { "G", "T" })
            .withQual(100.0d)
            .withFilter(new String[] { "PASS" })
            .withFormat(new String[] { "GT" })
            .withGenotypes(genotypes)

            // Number=0 Type=Flag
            .withInfo("FLAG", "true")
            .withInfo("FLAGS", "true", "false")

            // Number=[., 1, 4, A, R] Type=Char
            .withInfo("ANY_CHAR", "a", "b", "c", "d", "e", "f")
            .withInfo("ONE_CHAR", "a")
            .withInfo("FOUR_CHARS", "a", "b", "c", "d")
            .withInfo("A_CHARS", "a", "b")
            .withInfo("R_CHARS", "a", "b", "c")

            // Number=[., 1, 4, A, R] Type=Float
            .withInfo("ANY_FLOAT", "1.0", "2.1", "3.2", "4.3", "5.4", "6.5")
            .withInfo("ONE_FLOAT", "1.0")
            .withInfo("FOUR_FLOATS", "1.0", "2.1", "3.2", "4.3")
            .withInfo("A_FLOATS", "1.0", "2.1")
            .withInfo("R_FLOATS", "1.0", "2.1", "3.2")

            // Number=[., 1, 4, A, R] Type=Integer
            .withInfo("ANY_INT", "1", "2", "3", "4", "5", "6")
            .withInfo("ONE_INT", "1")
            .withInfo("FOUR_INTS", "1", "2", "3", "4")
            .withInfo("A_INTS", "1", "2")
            .withInfo("R_INTS", "1", "2", "3")

            // Number=[., 1, 4, A, R] Type=String
            .withInfo("ANY_STRING", "foo", "bar", "baz", "qux", "garply", "waldo")
            .withInfo("ONE_STRING", "foo")
            .withInfo("FOUR_STRINGS", "foo", "bar", "baz", "qux")
            .withInfo("A_STRINGS", "foo", "bar")
            .withInfo("R_STRINGS", "foo", "bar", "baz")

            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testNumberANullRecord() {
        numberA(null);
    }

    @Test
    public void testNumberA() {
        assertEquals(2, numberA(record));
    }

    @Test(expected=NullPointerException.class)
    public void testNumberRNullRecord() {
        numberR(null);
    }

    @Test
    public void testNumberR() {
        assertEquals(3, numberR(record));
    }

    @Test(expected=NullPointerException.class)
    public void testNumberGNullRecord() {
        numberG(null, genotypes.get("NA19131"));
    }

    @Test(expected=NullPointerException.class)
    public void testNumberGNullGenotype() {
        numberG(record, null);
    }

    @Ignore
    public void testNumberG() {
        assertEquals(10, numberG(record, genotypes.get("K=2")));
        assertEquals(10, numberG(record, genotypes.get("K=3")));
        assertEquals(10, numberG(record, genotypes.get("K=4")));
        assertEquals(10, numberG(record, genotypes.get("K=5")));
        assertEquals(10, numberG(record, genotypes.get("K=6")));
    }

    @Test(expected=NullPointerException.class)
    public void testParseFlagNullKey() {
        parseFlag(null, record.getInfo());
    }

    @Test(expected=NullPointerException.class)
    public void testParseFlagNullAttributes() {
        parseFlag("FLAG", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFlagEmpty() {
        parseFlag("EMPTY", record.getInfo());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFlagTooMany() {
        parseFlag("FLAGS", record.getInfo());
    }

    @Test
    public void testParseFlag() {
        assertEquals(true, parseFlag("FLAG", record.getInfo()));
    }
}
