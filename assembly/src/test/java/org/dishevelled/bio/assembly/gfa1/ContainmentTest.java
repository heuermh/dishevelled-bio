/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import org.dishevelled.bio.annotation.Annotation;

/**
 * Unit test for Containment.
 *
 * @author  Michael Heuer
 */
public class ContainmentTest {
    private Reference container;
    private Reference contained;
    private int position;
    private String overlap;
    private Map<String, Annotation> annotations;

    @Before
    public void setUp() {
        container = Reference.valueOf("source+");
        contained = Reference.valueOf("target+");
        position = 42;
        overlap = "10M";
        annotations = ImmutableMap.<String, Annotation>builder().put("aa", new Annotation("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullContainer() {
        new Containment(null, contained, position, overlap, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullContained() {
        new Containment(container, null, position, overlap, annotations);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullAnnotations() {
        new Containment(container, contained, position, overlap, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCtrInvalidPosition() {
        new Containment(container, contained, -1, overlap, annotations);
    }

    @Test
    public void testCtr() {
        Containment containment = new Containment(container, contained, position, overlap, annotations);
        assertEquals(container, containment.getContainer());
        assertEquals(contained, containment.getContained());
        assertEquals(position, containment.getPosition());
        assertEquals(overlap, containment.getOverlap());
        assertEquals(annotations, containment.getAnnotations());
        assertEquals("C\tsource\t+\ttarget\t+\t42\t10M\taa:i:42", containment.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Containment.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Containment.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Containment.valueOf("C\tsource\t+\ttarget\t+\t42");
    }

    @Test
    public void testValueOf() {
        Containment containment = Containment.valueOf("C\tsource\t+\ttarget\t+\t42\t10M\taa:i:42");
        assertEquals(container, containment.getContainer());
        assertEquals(contained, containment.getContained());
        assertEquals(position, containment.getPosition());
        assertEquals(overlap, containment.getOverlap());
        assertEquals(annotations, containment.getAnnotations());
    }

    @Test
    public void testEquals() {
        Containment containment1 = Containment.valueOf("C\tsource\t+\ttarget\t+\t42\t10M\taa:i:42");
        Containment containment2 = Containment.valueOf("C\tsource\t+\ttarget\t+\t42\t10M\taa:i:42");
        Containment containment3 = Containment.valueOf("C\tsource\t+\ttarget\t+\t42\t10M\taa:i:43");
        assertFalse(containment1.equals(null));
        assertFalse(containment1.equals(new Object()));
        assertTrue(containment1.equals(containment2));
        assertFalse(containment1.equals(containment3));
    }
}
