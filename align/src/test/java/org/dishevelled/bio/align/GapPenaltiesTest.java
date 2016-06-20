/*

    dsh-bio-align  Sequence alignment.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.align;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.dishevelled.bio.align.GapPenalties.builder;
import static org.dishevelled.bio.align.GapPenalties.create;

import org.junit.Test;

/**
 * Unit test for GapPenalties.
 *
 * @author  Michael Heuer
 */
public final class GapPenaltiesTest {

    @Test
    public void testBuilder() {
        assertNotNull(builder());
    }

    @Test
    public void testCreate() {
        GapPenalties gapPenalties = create(0, 1, 2, 3, 4);

        assertEquals((short) 0, gapPenalties.match());
        assertEquals((short) 1, gapPenalties.replace());
        assertEquals((short) 2, gapPenalties.insert());
        assertEquals((short) 3, gapPenalties.delete());
        assertEquals((short) 4, gapPenalties.extend());
    }

    @Test
    public void testGapPenalities() {
        GapPenalties gapPenalties = builder()
            .withMatch(0)
            .withReplace(1)
            .withInsert(2)
            .withDelete(3)
            .withExtend(4)
            .build();

        assertEquals((short) 0, gapPenalties.match());
        assertEquals((short) 1, gapPenalties.replace());
        assertEquals((short) 2, gapPenalties.insert());
        assertEquals((short) 3, gapPenalties.delete());
        assertEquals((short) 4, gapPenalties.extend());
    }
}
