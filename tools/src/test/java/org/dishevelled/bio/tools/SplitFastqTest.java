/*

    dsh-bio-tools  Command line tools.
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
package org.dishevelled.bio.tools;

import static org.dishevelled.compress.Readers.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.SangerFastqReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for SplitFastq.
 *
 * @author  Michael Heuer
 */
public final class SplitFastqTest {
    private File pairedFile;
    private File unpairedFile;
    private File firstFastqFile;
    private File secondFastqFile;

    @Before
    public void setUp() throws IOException {
        pairedFile = File.createTempFile("splitFastqTest", ".fq.gz");
        unpairedFile = File.createTempFile("splitFastqTest", ".fq.gz");
        firstFastqFile = File.createTempFile("splitFastqTest", ".fq");
        secondFastqFile = File.createTempFile("splitFastqTest", ".fq");
    }

    @After
    public void tearDown() {
        pairedFile.delete();
        unpairedFile.delete();
        firstFastqFile.delete();
        secondFastqFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullPairedFile() {
        new SplitFastq(null, unpairedFile, firstFastqFile, secondFastqFile);
    }

    public void testConstructorNullUnpairedFile() {
        assertNotNull(new SplitFastq(pairedFile, null, firstFastqFile, secondFastqFile));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFirstFastqFile() {
        new SplitFastq(pairedFile, unpairedFile, null, secondFastqFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSecondFastqFile() {
        new SplitFastq(pairedFile, unpairedFile, firstFastqFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new SplitFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile));
    }

    @Test
    public void testSplitFastq() throws Exception {
        copyResource("interleaved.fq.gz", pairedFile);
        copyResource("empty-unpaired.fq.gz", unpairedFile);
        new SplitFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();

        assertEquals(4, countFastq(firstFastqFile));
        assertEquals(4, countFastq(secondFastqFile));
    }

    @Test
    public void testSplitFastqUnpairedLeft() throws Exception {
        copyResource("interleaved.fq.gz", pairedFile);
        copyResource("unpaired-left.fq.gz", unpairedFile);
        new SplitFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();

        assertEquals(5, countFastq(firstFastqFile));
        assertEquals(4, countFastq(secondFastqFile));
    }

    @Test
    public void testSplitFastqUnpairedRight() throws Exception {
        copyResource("interleaved.fq.gz", pairedFile);
        copyResource("unpaired-right.fq.gz", unpairedFile);
        new SplitFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();

        assertEquals(4, countFastq(firstFastqFile));
        assertEquals(5, countFastq(secondFastqFile));
    }

    @Test(expected=IOException.class)
    public void testSplitFastqInvalidInterleaved() throws Exception {
        copyResource("invalid-interleaved.fq.gz", pairedFile);
        copyResource("empty-unpaired.fq.gz", unpairedFile);
        new SplitFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();
    }

    private static int countFastq(final File file) throws Exception {
        FastqReader fastqReader = new SangerFastqReader();
        int count = 0;
        for (Fastq fastq : fastqReader.read(file)) {
            count++;
        }
        return count;
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(SplitFastqTest.class.getResource(name)), file);
    }
}
