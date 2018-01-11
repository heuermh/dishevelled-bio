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

import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Unit test for Header.
 *
 * @author  Michael Heuer
 */
public class HeaderTest {
    private Map<String, Tag> tags;

    @Before
    public void setUp() {
        tags = ImmutableMap.<String, Tag>builder().put("aa", new Tag("aa", "i", "42")).build();
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullTags() {
        new Header(null);
    }

    @Test
    public void testCtr() {
        Header header = new Header(tags);
        assertEquals(tags, header.getTags());
        assertEquals("H\taa:i:42", header.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Header.valueOf(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfEmpty() {
        Header.valueOf("");
    }

    @Test
    public void testValueOf() {
        Header header = Header.valueOf("H\taa:i:42");
        assertEquals(tags, header.getTags());
    }

    @Test
    public void testEquals() {
        Header header1 = Header.valueOf("H\taa:i:42");
        Header header2 = Header.valueOf("H\taa:i:42");
        Header header3 = Header.valueOf("H\taa:i:43");
        assertFalse(header1.equals(null));
        assertFalse(header1.equals(new Object()));
        assertTrue(header1.equals(header2));
        assertFalse(header1.equals(header3));
    }
}
