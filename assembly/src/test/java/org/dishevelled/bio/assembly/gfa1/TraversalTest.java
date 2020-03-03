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
package org.dishevelled.bio.assembly.gfa1;

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
 * Unit test for Traversal.
 *
 * @author  Michael Heuer
 */
public class TraversalTest {
    private String pathName;
    private int ordinal;
    private Reference source;
    private Reference target;
    private String overlap;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        pathName = "pathName";
        ordinal = 0;
        source = Reference.valueOf("1+");
        target = Reference.valueOf("2-");
        overlap = "0M";
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullPathName() {
        new Traversal(null, ordinal, source, target, overlap, tags);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCtrInvalidOrdinal() {
        new Traversal(pathName, -1, source, target, overlap, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSource() {
        new Traversal(pathName, ordinal, null, target, overlap, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTarget() {
        new Traversal(pathName, ordinal, source, null, overlap, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Traversal(pathName, ordinal, source, target, overlap, null);
    }

    @Test
    public void testCtr() {
        Traversal traversal = new Traversal(pathName, ordinal, source, target, overlap, tags);
        assertEquals(pathName, traversal.getPathName());
        assertEquals(ordinal, traversal.getOrdinal());
        assertEquals(source, traversal.getSource());
        assertEquals(target, traversal.getTarget());
        assertEquals(overlap, traversal.getOverlap());
        assertEquals(tags, traversal.getTags());
        assertEquals("T\tpathName\t0\t1\t+\t2\t-\t0M\taa:i:42", traversal.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Traversal.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Segment.valueOf("T\tpathName");
    }

    @Test
    public void testValueOf() {
        Traversal traversal = Traversal.valueOf("T\tpathName\t0\t1\t+\t2\t-\t0M\taa:i:42");
        assertEquals(pathName, traversal.getPathName());
        assertEquals(ordinal, traversal.getOrdinal());
        assertEquals(source, traversal.getSource());
        assertEquals(target, traversal.getTarget());
        assertEquals(overlap, traversal.getOverlap());
        assertEquals(tags, traversal.getTags());
        assertEquals("T\tpathName\t0\t1\t+\t2\t-\t0M\taa:i:42", traversal.toString());
    }

    @Test
    public void testValueOfNoTags() {
        Traversal traversal = Traversal.valueOf("T\tpathName\t0\t1\t+\t2\t-\t*");
        assertTrue(traversal.getTags().isEmpty());
    }

    @Test
    public void testValueOfNoTagsTrailingTab() {
        Traversal traversal = Traversal.valueOf("T\tpathName\t0\t1\t+\t2\t-\t*\t");
        assertTrue(traversal.getTags().isEmpty());
    }
}
