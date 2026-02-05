/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.pangenome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for Scaffold.
 *
 * @author  Michael Heuer
 */
public final class ScaffoldTest {

    @Test(expected=NullPointerException.class)
    public void testConstructorNullName() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        Haplotype haplotype = new Haplotype(1, sample);
        new Scaffold(null, null, haplotype);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullHaplotype() {
        new Scaffold("scaffold", null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        Haplotype haplotype = new Haplotype(1, sample);
        new Scaffold("scaffold", 0L, haplotype);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNegativeLength() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        Haplotype haplotype = new Haplotype(1, sample);
        new Scaffold("scaffold", -1L, haplotype);
    }

    @Test
    public void testConstructor() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        Haplotype haplotype = new Haplotype(1, sample);
        Scaffold scaffold = new Scaffold("scaffold", 100L, haplotype);
        assertEquals("scaffold", scaffold.getName());
        assertEquals(new Long(100L), scaffold.getLength());
        assertTrue(scaffold.getLengthOpt().isPresent());
        assertEquals(new Long(100L), scaffold.getLengthOpt().get());
        assertEquals(haplotype, scaffold.getHaplotype());
    }

    @Test
    public void testConstructorNullLength() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        Haplotype haplotype = new Haplotype(1, sample);
        Scaffold scaffold = new Scaffold("scaffold", null, haplotype);
        assertEquals("scaffold", scaffold.getName());
        assertNull(scaffold.getLength());
        assertFalse(scaffold.getLengthOpt().isPresent());
        assertEquals(haplotype, scaffold.getHaplotype());
    }
}
