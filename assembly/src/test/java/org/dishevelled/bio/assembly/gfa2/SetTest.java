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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Unit test for Set.
 *
 * @author  Michael Heuer
 */
public class SetTest {
    private String id;
    private java.util.Set<String> ids;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        id = "id";
        ids = ImmutableSet.of("source", "target");
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullIds() {
        new Set(id, null, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Set(id, ids, null);
    }

    @Test
    public void testCtr() {
        Set set = new Set(id, ids, tags);
        assertEquals(id, set.getId());
        assertEquals(ids, set.getIds());
        assertEquals(tags, set.getTags());
        assertTrue("U\tid\tsource target\taa:i:42".equals(set.toString())
                   || "U\tid\ttarget source\taa:i:42".equals(set.toString()));
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Set.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Set.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Set.valueOf("U\tid");
    }

    @Test
    public void testValueOf() {
        Set set = Set.valueOf("U\tid\tsource target\taa:i:42");
        assertEquals(id, set.getId());
        assertEquals(ids, set.getIds());
        assertEquals(tags, set.getTags());
    }

    @Test
    public void testEquals() {
        Set set1 = Set.valueOf("U\tid\tsource target\taa:i:42");
        Set set2 = Set.valueOf("U\tid\tsource target\taa:i:42");
        Set set3 = Set.valueOf("U\tid\tsource target\taa:i:43");
        assertFalse(set1.equals(null));
        assertFalse(set1.equals(new Object()));
        assertTrue(set1.equals(set2));
        assertFalse(set1.equals(set3));
    }
}
