/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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

import org.junit.Test;

/**
 * Unit test for Haplotype.
 *
 * @author  Michael Heuer
 */
public final class HaplotypeTest {

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSample() {
        new Haplotype(1, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNegativeIdentifier() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        new Haplotype(-1, sample);
    }

    @Test
    public void testConstructor() {
        Pangenome pangenome = new Pangenome();
        Sample sample = new Sample("sample", pangenome);
        Haplotype haplotype = new Haplotype(1, sample);
        assertEquals(1, haplotype.getIdentifier());
        assertEquals(sample, haplotype.getSample());
        assertEquals(0, haplotype.getScaffolds().size());
    }
}
