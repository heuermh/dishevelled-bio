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

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import org.dishevelled.bio.assembly.gfa.Reference;
import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Unit test for Link.
 *
 * @author  Michael Heuer
 */
public class LinkTest {
    private Reference source;
    private Reference target;
    private String overlap;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        source = Reference.valueOf("source+");
        target = Reference.valueOf("target+");
        overlap = "10M";
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSource() {
        new Link(null, target, overlap, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTarget() {
        new Link(source, null, overlap, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Link(source, target, overlap, null);
    }

    @Test
    public void testCtr() {
        Link link = new Link(source, target, overlap, tags);
        assertEquals(source, link.getSource());
        assertEquals(target, link.getTarget());
        assertEquals(overlap, link.getOverlap());
        assertEquals(tags, link.getTags());
        assertEquals("L\tsource\t+\ttarget\t+\t10M\taa:i:42", link.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Link.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Link.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Link.valueOf("L\tsource\t+\ttarget\t+");
    }

    @Test
    public void testValueOf() {
        Link link = Link.valueOf("L\tsource\t+\ttarget\t+\t10M\taa:i:42");
        assertEquals(source, link.getSource());
        assertEquals(target, link.getTarget());
        assertEquals(overlap, link.getOverlap());
        assertEquals(tags, link.getTags());
    }

    @Test
    public void testEquals() {
        Link link1 = Link.valueOf("L\tsource\t+\ttarget\t+\t10M\taa:i:42");
        Link link2 = Link.valueOf("L\tsource\t+\ttarget\t+\t10M\taa:i:42");
        Link link3 = Link.valueOf("L\tsource\t+\ttarget\t+\t10M\taa:i:43");
        assertFalse(link1.equals(null));
        assertFalse(link1.equals(new Object()));
        assertTrue(link1.equals(link2));
        assertFalse(link1.equals(link3));
    }
}
