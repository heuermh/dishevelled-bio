/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import org.dishevelled.bio.assembly.gfa.Reference;
import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Unit test for Path.
 *
 * @author  Michael Heuer
 */
public class PathTest {
    private String name;
    private List<Reference> segments;
    private List<String> overlaps;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        name = "name";
        segments = ImmutableList.of(Reference.valueOf("source+"), Reference.valueOf("target+"));
        overlaps = ImmutableList.of("10M");
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullName() {
        new Path(null, segments, overlaps, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSegments() {
        new Path(name, null, overlaps, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Path(name, segments, overlaps, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCtrIncorrectOverlapSize() {
        new Path(name, segments, ImmutableList.of("10M", "20M"), tags);
    }

    @Test
    public void testCtr() {
        Path path = new Path(name, segments, overlaps, tags);
        assertEquals(name, path.getName());
        assertEquals(segments, path.getSegments());
        assertEquals(overlaps, path.getOverlaps());
        assertEquals(tags, path.getTags());
        assertEquals("P\tname\tsource+,target+\t10M\taa:i:42", path.toString());
    }

    @Test
    public void testCtrEmptySegments() {
        Path path = new Path(name, Collections.<Reference>emptyList(), null, tags);
        assertEquals(name, path.getName());
        assertTrue(path.getSegments().isEmpty());
        assertFalse(path.hasOverlaps());
        assertEquals(tags, path.getTags());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Path.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Path.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Path.valueOf("P\tname\tsource+,target+");
    }

    @Test
    public void testValueOf() {
        Path path = Path.valueOf("P\tname\tsource+,target+\t10M\taa:i:42");
        assertEquals(name, path.getName());
        assertEquals(segments, path.getSegments());
        assertEquals(overlaps, path.getOverlaps());
        assertEquals(tags, path.getTags());
    }

    @Test
    public void testValueOfMultipleOverlaps() {
        Path path = Path.valueOf("P\tname\tsource+,target+,additional+\t10M,20M\taa:i:42");
        assertEquals(name, path.getName());
        assertEquals(ImmutableList.of("10M", "20M"), path.getOverlaps());
        assertEquals(tags, path.getTags());
    }

    @Test
    public void testValueOfEmptySegments() {
        Path path = Path.valueOf("P\tname\t*\t*\taa:i:42");
        assertEquals(name, path.getName());
        assertTrue(path.getSegments().isEmpty());
        assertFalse(path.hasOverlaps());
        assertEquals(tags, path.getTags());
    }

    @Test
    public void testValueOfEmptySegmentsNoTags() {
        Path path = Path.valueOf("P\tname\t*\t*");
        assertEquals(name, path.getName());
        assertTrue(path.getSegments().isEmpty());
        assertFalse(path.hasOverlaps());
        assertTrue(path.getTags().isEmpty());
    }

    @Test
    public void testEquals() {
        Path path1 = Path.valueOf("P\tname\tsource+,target+\t10M\taa:i:42");
        Path path2 = Path.valueOf("P\tname\tsource+,target+\t10M\taa:i:42");
        Path path3 = Path.valueOf("P\tname\tsource+,target+\t10M\taa:i:43");
        assertFalse(path1.equals(null));
        assertFalse(path1.equals(new Object()));
        assertTrue(path1.equals(path2));
        assertFalse(path1.equals(path3));
    }
}
