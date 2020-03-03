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
    private String id;
    private List<Reference> references;
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        id = "id";
        references = ImmutableList.of(Reference.valueOf("source+"), Reference.valueOf("target+"));
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullReferences() {
        new Path(id, null, tags);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Path(id, references, null);
    }

    @Test
    public void testCtr() {
        Path path = new Path(id, references, tags);
        assertEquals(id, path.getId());
        assertEquals(references, path.getReferences());
        assertEquals(tags, path.getTags());
        assertEquals("O\tid\tsource+ target+\taa:i:42", path.toString());
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
        Path.valueOf("O\tid");
    }

    @Test
    public void testValueOf() {
        Path path = Path.valueOf("O\tid\tsource+ target+\taa:i:42");
        assertEquals(id, path.getId());
        assertEquals(references, path.getReferences());
        assertEquals(tags, path.getTags());
    }

    @Test
    public void testEquals() {
        Path path1 = Path.valueOf("O\tid\tsource+ target+\taa:i:42");
        Path path2 = Path.valueOf("O\tid\tsource+ target+\taa:i:42");
        Path path3 = Path.valueOf("O\tid\tsource+ target+\taa:i:43");
        assertFalse(path1.equals(null));
        assertFalse(path1.equals(new Object()));
        assertTrue(path1.equals(path2));
        assertFalse(path1.equals(path3));
    }
}
