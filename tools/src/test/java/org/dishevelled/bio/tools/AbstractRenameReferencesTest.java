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

import static org.junit.Assert.assertEquals;

import static org.dishevelled.bio.tools.AbstractRenameReferences.addChr;
import static org.dishevelled.bio.tools.AbstractRenameReferences.removeChr;

import org.junit.Test;

/**
 * Unit test for AbstractRenameReferences.
 *
 * @author  Michael Heuer
 */
public final class AbstractRenameReferencesTest {

    @Test
    public void testAddChr() {
        assertEquals("chr1", addChr("1"));
        assertEquals("chr1", addChr("chr1"));
        assertEquals("chrX", addChr("X"));
        assertEquals("chrX", addChr("chrX"));
        assertEquals("chrM", addChr("MT"));
        assertEquals("chrM", addChr("chrM"));
        assertEquals("KI270757.1", addChr("KI270757.1"));
        assertEquals("KI270757.1", addChr("KI270757v1"));
    }

    @Test
    public void testRemoveChr() {
        assertEquals("1", removeChr("1"));
        assertEquals("1", removeChr("chr1"));
        assertEquals("X", removeChr("X"));
        assertEquals("X", removeChr("chrX"));
        assertEquals("MT", removeChr("MT"));
        assertEquals("MT", removeChr("chrM"));
        assertEquals("KI270757.1", removeChr("KI270757.1"));
        assertEquals("KI270757.1", removeChr("KI270757v1"));
        assertEquals("GL00", removeChr("GL00"));
        assertEquals("GL00", removeChr("chrUn_GL00"));
    }
}
