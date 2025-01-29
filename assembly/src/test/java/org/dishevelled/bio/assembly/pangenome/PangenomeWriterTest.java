/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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

import static org.dishevelled.bio.assembly.pangenome.PangenomeReader.readFastaIndex;

import static org.dishevelled.bio.assembly.pangenome.PangenomeWriter.write;
import static org.dishevelled.bio.assembly.pangenome.PangenomeWriter.writeSorted;
import static org.dishevelled.bio.assembly.pangenome.PangenomeWriter.writeSortedTree;
import static org.dishevelled.bio.assembly.pangenome.PangenomeWriter.writeTree;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for PangenomeWriter.
 *
 * @author  Michael Heuer
 */
public final class PangenomeWriterTest {
    private Pangenome pangenome;

    @Before
    public void setUp() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("test.fa.fai")))) {
            pangenome = readFastaIndex(reader);
        }
    }
    
    @Test
    public void testWrite() throws Exception {
        System.out.println("writing pangenome...");
        write(pangenome, new PrintWriter(new OutputStreamWriter(System.out), true));
    }

    @Test
    public void testWriteSorted() throws Exception {
        System.out.println("writing sorted pangenome...");
        writeSorted(pangenome, new PrintWriter(new OutputStreamWriter(System.out), true));
    }

    @Test
    public void testWriteTree() throws Exception { 
        System.out.println("writing pangenome tree...");
        writeTree(pangenome, new PrintWriter(new OutputStreamWriter(System.out), true));
    }

    @Test
    public void testWriteSortedTree() throws Exception { 
        System.out.println("writing sorted pangenome tree...");
        writeSortedTree(pangenome, new PrintWriter(new OutputStreamWriter(System.out), true));
    }
}
