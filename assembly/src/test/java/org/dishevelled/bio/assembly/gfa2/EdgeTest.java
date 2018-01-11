/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
 * Unit test for Edge.
 *
 * @author  Michael Heuer
 */
public class EdgeTest {
    private String id;
    private Reference source;
    private Reference target;
    private Position sourceStart;
    private Position sourceEnd;
    private Position targetStart;
    private Position targetEnd;
    private Alignment alignment;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        id = "id";
        source = Reference.valueOf("source+");
        target = Reference.valueOf("target+");
        sourceStart = Position.valueOf("1");
        sourceEnd = Position.valueOf("10");
        targetStart = Position.valueOf("101");
        targetEnd = Position.valueOf("110");
        alignment = Alignment.valueOf("10M");
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSource() {
        new Edge(id, null, target, sourceStart, sourceEnd, targetStart, targetEnd, alignment, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTarget() {
        new Edge(id, source, null, sourceStart, sourceEnd, targetStart, targetEnd, alignment, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSourceStart() {
        new Edge(id, source, target, null, sourceEnd, targetStart, targetEnd, alignment, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullSourceEnd() {
        new Edge(id, source, target, sourceStart, null, targetStart, targetEnd, alignment, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTargetStart() {
        new Edge(id, source, target, sourceStart, sourceEnd, null, targetEnd, alignment, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTargetEnd() {
        new Edge(id, source, target, sourceStart, sourceEnd, targetStart, null, alignment, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Edge(id, source, target, sourceStart, sourceEnd, targetStart, targetEnd, alignment, null);
    }

    @Test
    public void testCtr() {
        Edge edge = new Edge(id, source, target, sourceStart, sourceEnd, targetStart, targetEnd, alignment, tags);
        assertEquals(id, edge.getId());
        assertEquals(source, edge.getSource());
        assertEquals(target, edge.getTarget());
        assertEquals(sourceStart, edge.getSourceStart());
        assertEquals(sourceEnd, edge.getSourceEnd());
        assertEquals(targetStart, edge.getTargetStart());
        assertEquals(targetEnd, edge.getTargetEnd());
        assertEquals(alignment, edge.getAlignment());
        assertEquals(tags, edge.getTags());
        assertEquals("E\tid\tsource+\ttarget+\t1\t10\t101\t110\t10M\taa:i:42", edge.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Edge.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStart() {
        Edge.valueOf("H\tVN:Z:2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidTokens() {
        Edge.valueOf("E\tid\tsource+\ttarget+\t1\t10\t101\t110");
    }

    @Test
    public void testValueOf() {
        Edge edge = Edge.valueOf("E\tid\tsource+\ttarget+\t1\t10\t101\t110\t10M\taa:i:42");
        assertEquals(id, edge.getId());
        assertEquals(source, edge.getSource());
        assertEquals(target, edge.getTarget());
        assertEquals(sourceStart, edge.getSourceStart());
        assertEquals(sourceEnd, edge.getSourceEnd());
        assertEquals(targetStart, edge.getTargetStart());
        assertEquals(targetEnd, edge.getTargetEnd());
        assertEquals(alignment, edge.getAlignment());
        assertEquals(tags, edge.getTags());
    }

    @Test
    public void testEquals() {
        Edge edge1 = Edge.valueOf("E\tid\tsource+\ttarget+\t1\t10\t101\t110\t10M\taa:i:42");
        Edge edge2 = Edge.valueOf("E\tid\tsource+\ttarget+\t1\t10\t101\t110\t10M\taa:i:42");
        Edge edge3 = Edge.valueOf("E\tid\tsource+\ttarget+\t1\t10\t101\t110\t10M\taa:i:43");
        assertFalse(edge1.equals(null));
        assertFalse(edge1.equals(new Object()));
        assertTrue(edge1.equals(edge2));
        assertFalse(edge1.equals(edge3));
    }
}
