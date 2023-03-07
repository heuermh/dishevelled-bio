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
package org.dishevelled.bio.assembly.gfa2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for Alignment.
 *
 * @author  Michael Heuer
 */
public class AlignmentTest {

    @Test
    public void testCtrNullCigar() {
        Alignment alignment = new Alignment((String) null);
        assertFalse(alignment.hasCigar());
    }

    @Test
    public void testCtrNullTrace() {
        Alignment alignment = new Alignment((List<Integer>) null);
        assertFalse(alignment.hasTrace());
    }

    @Test
    public void testCtrCigar() {
        Alignment alignment = new Alignment("10M");
        assertEquals("10M", alignment.getCigar());
        assertEquals("10M", alignment.toString());
    }

    @Test
    public void testCtrTrace() {
        List<Integer> trace = new ArrayList<Integer>();
        trace.add(1);
        trace.add(42);
        Alignment alignment = new Alignment(trace);
        assertEquals(trace, alignment.getTrace());
        assertEquals("1,42", alignment.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Alignment.valueOf(null);
    }

    @Test
    public void testValueOfEmpty() {
        assertNull(Alignment.valueOf(""));
    }

    @Test
    public void testValueOfMissing() {
        assertNull(Alignment.valueOf("*"));
    }

    @Test
    public void testValueOfCigar() {
        Alignment alignment = Alignment.valueOf("10M");
        assertEquals("10M", alignment.getCigar());
    }

    @Test
    public void testValueOfTrace() {
        Alignment alignment = Alignment.valueOf("1,42");
        List<Integer> trace = alignment.getTrace();
        assertEquals(Integer.valueOf(1), trace.get(0));
        assertEquals(Integer.valueOf(42), trace.get(1));
    }
}
