/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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

import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Unit test for Segment.
 *
 * @author  Michael Heuer
 */
public class SegmentTest {
    private String id;
    private int length;
    private String sequence;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        id = "id";
        length = 42;
        sequence = "actg";
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullId() {
        new Segment(null, length, sequence, tags);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCtrInvalidLength() {
        new Segment(id, -1, sequence, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Segment(id, length, sequence, null);
    }

    @Test
    public void testCtr() {
        Segment segment = new Segment(id, length, sequence, tags);
        assertEquals(id, segment.getId());
        assertEquals(length, segment.getLength());
        assertEquals(sequence, segment.getSequence());
        assertEquals(tags, segment.getTags());
        assertEquals("S\tid\t42\tactg\taa:i:42", segment.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Segment.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Segment.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Segment.valueOf("S\tid\t42");
    }

    @Test
    public void testValueOf() {
        Segment segment = Segment.valueOf("S\tid\t42\tactg\taa:i:42");
        assertEquals(id, segment.getId());
        assertEquals(length, segment.getLength());
        assertEquals(sequence, segment.getSequence());
        assertEquals(tags, segment.getTags());
    }

    @Test
    public void testEquals() {
        Segment segment1 = Segment.valueOf("S\tid\t42\tactg\taa:i:42");
        Segment segment2 = Segment.valueOf("S\tid\t42\tactg\taa:i:42");
        Segment segment3 = Segment.valueOf("S\tid\t42\tactg\taa:i:43");
        assertFalse(segment1.equals(null));
        assertFalse(segment1.equals(new Object()));
        assertTrue(segment1.equals(segment2));
        assertFalse(segment1.equals(segment3));
    }
}
