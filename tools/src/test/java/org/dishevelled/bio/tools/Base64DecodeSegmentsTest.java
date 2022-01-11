/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Base64DecodeSegments.
 *
 * @author  Michael Heuer
 */
public final class Base64DecodeSegmentsTest {
    private File inputGfa1File;
    private File outputGfa1File;

    @Before
    public void setUp() throws IOException {
        inputGfa1File = File.createTempFile("base64DecodeSegmentsTest", ".gfa");
        outputGfa1File = File.createTempFile("base64DecodeSegmentsTest", ".gfa");
    }

    @After
    public void tearDown() {
        inputGfa1File.delete();
        outputGfa1File.delete();
    }

    @Test
    public void testConstructor() {
        assertNotNull(new Base64DecodeSegments(inputGfa1File, false, false, outputGfa1File));
    }

    @Test
    public void testConstructorNullInputGfa1File() {
        assertNotNull(new Base64DecodeSegments(null, false, false, outputGfa1File));
    }

    @Test
    public void testConstructorNullOutputGfa1File() {
        assertNotNull(new Base64DecodeSegments(inputGfa1File, false, false, null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNsAndWithAmbiguity() {
        new Base64DecodeSegments(inputGfa1File, true, true, outputGfa1File);
    }
}
