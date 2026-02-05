/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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
package org.dishevelled.bio.tools;

import static org.dishevelled.bio.tools.AbstractSplit.toBytes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for AbstractSplit.
 *
 * @author  Michael Heuer
 */
public final class AbstractSplitTest {

    @Test(expected=NullPointerException.class)
    public void testToBytesNull() {
        toBytes(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToBytesEmpty() {
        toBytes("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToBytesWhitespace() {
        toBytes(" \t\n");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToBytesInvalid() {
        toBytes("invalid");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToBytesMissingValue() {
        toBytes("kb");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToBytesUnknownUnit() {
        toBytes("42 hogshead");
    }

    @Test
    public void testToBytes() {
        assertEquals(42L, toBytes("42"));
        assertEquals(42L, toBytes("42b"));
        assertEquals(42L, toBytes("42B"));
        assertEquals(42L, toBytes("42 b"));
        assertEquals(42L, toBytes("42 B"));
        assertEquals(42L, toBytes("42\tb"));
        assertEquals(42L, toBytes("42\tB"));

        assertEquals(43008L, toBytes("42k"));
        assertEquals(43008L, toBytes("42kb"));
        assertEquals(43008L, toBytes("42 k"));
        assertEquals(43008L, toBytes("42 kb"));
        assertEquals(43008L, toBytes("42\tk"));
        assertEquals(43008L, toBytes("42\tkb"));
        assertEquals(43008L, toBytes("42K"));
        assertEquals(43008L, toBytes("42KB"));

        assertEquals(44040192L, toBytes("42m"));
        assertEquals(44040192L, toBytes("42mb"));
        assertEquals(44040192L, toBytes("42M"));
        assertEquals(44040192L, toBytes("42MB"));

        assertEquals(45097156608L, toBytes("42g"));
        assertEquals(45097156608L, toBytes("42gb"));
        assertEquals(45097156608L, toBytes("42G"));
        assertEquals(45097156608L, toBytes("42GB"));

        assertEquals(46179488366592L, toBytes("42t"));
        assertEquals(46179488366592L, toBytes("42tb"));
        assertEquals(46179488366592L, toBytes("42T"));
        assertEquals(46179488366592L, toBytes("42TB"));
    }
}
