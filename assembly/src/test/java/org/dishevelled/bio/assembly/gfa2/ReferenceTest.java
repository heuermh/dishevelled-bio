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

import org.junit.Test;

/**
 * Unit test for Reference.
 *
 * @author  Michael Heuer
 */
public class ReferenceTest {

    @Test(expected=NullPointerException.class)
    public void testCtrNullId() {
        new Reference(null, Orientation.FORWARD);
    }

    @Test(expected=NullPointerException.class)
    public void testCtrNullOrientation() {
        new Reference("id", null);
    }

    @Test
    public void testCtr() {
        Reference reference = new Reference("id", Orientation.FORWARD);
        assertEquals("id", reference.getId());
        assertEquals(Orientation.FORWARD, reference.getOrientation());
        assertEquals("id+", reference.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        Reference.valueOf(null);
    }

    @Test
    public void testValueOfForward() {
        Reference reference = Reference.valueOf("id+");
        assertEquals("id", reference.getId());
        assertEquals(Orientation.FORWARD, reference.getOrientation());
    }

    @Test
    public void testValueOfReverse() {
        Reference reference = Reference.valueOf("id-");
        assertEquals("id", reference.getId());
        assertEquals(Orientation.REVERSE, reference.getOrientation());
    }
}
