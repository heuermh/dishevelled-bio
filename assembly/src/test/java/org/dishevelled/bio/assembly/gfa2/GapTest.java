/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.gfa2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import org.dishevelled.bio.assembly.gfa.Reference;
import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Unit test for Gap.
 *
 * @author  Michael Heuer
 */
public class GapTest {
    private String id;
    private Reference source;
    private Reference target;
    private int distance;
    private Integer variance;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        id = "id";
        source = Reference.valueOf("source+");
        target = Reference.valueOf("target+");
        distance = 42;
        variance = Integer.valueOf(2);
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSource() {
        new Gap(id, null, target, distance, variance, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTarget() {
        new Gap(id, source, null, distance, variance, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Gap(id, source, target, distance, variance, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCtrInvalidDistance() {
        new Gap(id, source, target, -1, variance, tags);
    }

    @Test
    public void testCtr() {
        Gap gap = new Gap(id, source, target, distance, variance, tags);
        assertEquals(id, gap.getId());
        assertEquals(source, gap.getSource());
        assertEquals(target, gap.getTarget());
        assertEquals(distance, gap.getDistance());
        assertEquals(variance, gap.getVariance());
        assertEquals(tags, gap.getTags());
        assertEquals("G\tid\tsource+\ttarget+\t42\t2\taa:i:42", gap.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Gap.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Gap.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Gap.valueOf("G\tid\tsource+\ttarget+\t42");
    }

    @Test
    public void testValueOf() {
        Gap gap = Gap.valueOf("G\tid\tsource+\ttarget+\t42\t2\taa:i:42");
        assertEquals(id, gap.getId());
        assertEquals(source, gap.getSource());
        assertEquals(target, gap.getTarget());
        assertEquals(distance, gap.getDistance());
        assertEquals(variance, gap.getVariance());
        assertEquals(tags, gap.getTags());
    }

    @Test
    public void testEquals() {
        Gap gap1 = Gap.valueOf("G\tid\tsource+\ttarget+\t42\t2\taa:i:42");
        Gap gap2 = Gap.valueOf("G\tid\tsource+\ttarget+\t42\t2\taa:i:42");
        Gap gap3 = Gap.valueOf("G\tid\tsource+\ttarget+\t42\t2\taa:i:43");
        assertFalse(gap1.equals(null));
        assertFalse(gap1.equals(new Object()));
        assertTrue(gap1.equals(gap2));
        assertFalse(gap1.equals(gap3));
    }
}
