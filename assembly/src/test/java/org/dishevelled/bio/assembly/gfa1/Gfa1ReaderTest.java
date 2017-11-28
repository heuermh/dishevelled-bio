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
package org.dishevelled.bio.assembly.gfa1;

import static org.junit.Assert.assertNotNull;

import static org.dishevelled.bio.assembly.gfa1.Gfa1Reader.read;
import static org.dishevelled.bio.assembly.gfa1.Gfa1Reader.stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Gfa1Reader.
 *
 * @author  Michael Heuer
 */
public class Gfa1ReaderTest {
    private BufferedReader readable;

    @Before
    public void setUp() throws IOException {
        readable = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("example1.gfa")));
    }

    @After
    public void tearDown() throws Exception {
        readable.close();
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullReadable() throws Exception {
        read(null);
    }

    @Test
    public void testRead() throws Exception {
        for (Gfa1Record record : read(readable)) {
            assertNotNull(record);
        }
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream(null, new Gfa1Listener() {
                @Override
                public boolean record(final Gfa1Record record) {
                    return true;
                }
            });
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, new Gfa1Listener() {
                @Override
                public boolean record(final Gfa1Record record) {
                    assertNotNull(record);
                    return true;
                }
            });
    }
}
