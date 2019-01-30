/*

    dsh-bio-variant  Variants.
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
package org.dishevelled.bio.variant.vcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.dishevelled.bio.variant.vcf.VcfGenotype.builder;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for VcfGenotype.
 *
 * @author  Michael Heuer
 */
public final class VcfGenotypeTest {
    private ListMultimap<String, String> empty;
    private ListMultimap<String, String> fields;

    @Before
    public void setUp() {
        empty = ImmutableListMultimap.<String, String>builder().build();
        fields = ImmutableListMultimap.<String, String>builder().put("GT", "1|1").build();
    }

    @Test
    public void testBuilder() {
        assertNotNull(builder());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithNullRef() {
        builder().withRef(null).withAlt("G").withField("GT", "1|1").build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithNullAlt() {
        builder().withRef("A").withAlt((String[]) null).withField("GT", "1|1").build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithFieldNullId() {
        builder().withRef("A").withAlt("G").withField(null, "1|1");
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithFieldNullValue() {
        builder().withRef("A").withAlt("G").withField("GT", null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithFieldsNull() {
        builder().withRef("A").withAlt("G").withFields(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderBuildDefaultFields() {
        builder().withRef("A").withAlt("G").build();
    }

    @Test
    public void testBuilderBuildFields() {
        assertEquals(fields, builder().withRef("A").withAlt("G").withFields(fields).build().getFields());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderBuildEmptyFields() {
        builder().withRef("A").withAlt("G").withFields(empty).build();
    }

    @Test
    public void testBuilderReset() {
        assertEquals("0|1", builder()
            .withRef("A").withAlt("G").withField("GT", "1|1")
            .reset()
            .withRef("A").withAlt("G").withField("GT", "0|1")
            .build().getGt());
    }

    @Test
    public void testBuilderBuildWithFields() {
        VcfGenotype genotype = builder().withRef("A").withAlt("G").withFields(fields).build();
        assertEquals("1|1", genotype.getGt());
        assertEquals(fields, genotype.getFields());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderBuildMultipleWithGt() {
        builder().withRef("A").withAlt("G").withField("GT", "0|1").withField("GT", "1|1").build();
    }
}
