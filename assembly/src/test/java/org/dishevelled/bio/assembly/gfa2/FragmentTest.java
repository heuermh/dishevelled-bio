/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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

import org.dishevelled.bio.annotation.Annotation;

/**
 * Unit test for Fragment.
 *
 * @author  Michael Heuer
 */
public class FragmentTest {
    private String segmentId;
    private Reference external;
    private Position segmentStart;
    private Position segmentEnd;
    private Position fragmentStart;
    private Position fragmentEnd;
    private Alignment alignment;
    private Map<String, Annotation> annotations;

    @Before
    public void setUp() {
        segmentId = "segmentId";
        external = Reference.valueOf("external+");
        segmentStart = Position.valueOf("1");
        segmentEnd = Position.valueOf("10");
        fragmentStart = Position.valueOf("101");
        fragmentEnd = Position.valueOf("110");
        alignment = Alignment.valueOf("10M");
        annotations = ImmutableMap.<String, Annotation>builder().put("aa", new Annotation("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSegmentId() {
        new Fragment(null, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullExternal() {
        new Fragment(segmentId, null, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSegmentStart() {
        new Fragment(segmentId, external, null, segmentEnd, fragmentStart, fragmentEnd, alignment, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSegmentEnd() {
        new Fragment(segmentId, external, segmentStart, null, fragmentStart, fragmentEnd, alignment, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullFragmentStart() {
        new Fragment(segmentId, external, segmentStart, segmentEnd, null, fragmentEnd, alignment, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullFragmentEnd() {
        new Fragment(segmentId, external, segmentStart, segmentEnd, fragmentStart, null, alignment, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullAnnotations() {
        new Fragment(segmentId, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment, null);
    }

    @Test
    public void testCtr() {
        Fragment fragment = new Fragment(segmentId, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment, annotations);
        assertEquals(segmentId, fragment.getSegmentId());
        assertEquals(external, fragment.getExternal());
        assertEquals(segmentStart, fragment.getSegmentStart());
        assertEquals(segmentEnd, fragment.getSegmentEnd());
        assertEquals(fragmentStart, fragment.getFragmentStart());
        assertEquals(fragmentEnd, fragment.getFragmentEnd());
        assertEquals(alignment, fragment.getAlignment());
        assertEquals(annotations, fragment.getAnnotations());
        assertEquals("F\tsegmentId\texternal+\t1\t10\t101\t110\t10M\taa:i:42", fragment.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Fragment.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Fragment.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Fragment.valueOf("F\tsegmentId\texternal+\t1\t10\t101\t110");
    }

    @Test
    public void testValueOf() {
        Fragment fragment = Fragment.valueOf("F\tsegmentId\texternal+\t1\t10\t101\t110\t10M\taa:i:42");
        assertEquals(segmentId, fragment.getSegmentId());
        assertEquals(external, fragment.getExternal());
        assertEquals(segmentStart, fragment.getSegmentStart());
        assertEquals(segmentEnd, fragment.getSegmentEnd());
        assertEquals(fragmentStart, fragment.getFragmentStart());
        assertEquals(fragmentEnd, fragment.getFragmentEnd());
        assertEquals(alignment, fragment.getAlignment());
        assertEquals(annotations, fragment.getAnnotations());
    }

    @Test
    public void testEquals() {
        Fragment fragment1 = Fragment.valueOf("F\tsegmentId\texternal+\t1\t10\t101\t110\t10M\taa:i:42");
        Fragment fragment2 = Fragment.valueOf("F\tsegmentId\texternal+\t1\t10\t101\t110\t10M\taa:i:42");
        Fragment fragment3 = Fragment.valueOf("F\tsegmentId\texternal+\t1\t10\t101\t110\t10M\taa:i:43");
        assertFalse(fragment1.equals(null));
        assertFalse(fragment1.equals(new Object()));
        assertTrue(fragment1.equals(fragment2));
        assertFalse(fragment1.equals(fragment3));
    }
}
