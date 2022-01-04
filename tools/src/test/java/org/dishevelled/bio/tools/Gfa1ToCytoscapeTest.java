/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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
 * Unit test for Gfa1ToCytoscape.
 *
 * @author  Michael Heuer
 */
public final class Gfa1ToCytoscapeTest {
    private File inputGfa1File;
    private File outputNodesFile;
    private File outputEdgesFile;

    @Before
    public void setUp() throws IOException {
        inputGfa1File = File.createTempFile("gfa1ToCytoscapeTest", ".gfa");
        outputNodesFile = File.createTempFile("gfa1ToCytoscapeTest", ".nodes.txt");
        outputEdgesFile = File.createTempFile("gfa1ToCytoscapeTest", ".edges.txt");
    }

    @After
    public void tearDown() {
        inputGfa1File.delete();
        outputNodesFile.delete();
        outputEdgesFile.delete();
    }

    @Test
    public void testConstructor() {
        assertNotNull(new Gfa1ToCytoscape(inputGfa1File, outputNodesFile, outputEdgesFile));
    }

    @Test
    public void testConstructorNullInputGfa1File() {
        assertNotNull(new Gfa1ToCytoscape(null, outputNodesFile, outputEdgesFile));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOutputNodesFile() {
        new Gfa1ToCytoscape(inputGfa1File, null, outputEdgesFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOutputEdgesFile() {
        new Gfa1ToCytoscape(inputGfa1File, outputNodesFile, null);
    }
}
