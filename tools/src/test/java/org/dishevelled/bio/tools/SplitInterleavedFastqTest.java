/*

    dsh-bio-tools  Command line tools.
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
package org.dishevelled.bio.tools;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for SplitInterleavedFastq.
 *
 * @author  Michael Heuer
 */
public final class SplitInterleavedFastqTest {
    private File inputFile;
    private Long bytes;
    private Long records;
    private String prefix;
    private String suffix;
    private SplitInterleavedFastq splitInterleavedFastq;

    @Before
    public void setUp() throws Exception {
        prefix = "split";
        suffix = ".ifq.gz";
        splitInterleavedFastq = new SplitInterleavedFastq(inputFile, bytes, records, prefix, suffix);
    }

    @Test
    public void testConstructor() {
        assertNotNull(splitInterleavedFastq);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullPrefix() {
        new SplitInterleavedFastq(inputFile, bytes, records, null, suffix);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSuffix() {
        new SplitInterleavedFastq(inputFile, bytes, records, prefix, null);
    }
}
