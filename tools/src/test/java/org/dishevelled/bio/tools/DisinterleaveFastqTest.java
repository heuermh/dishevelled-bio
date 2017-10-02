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
 * Unit test for DisinterleaveFastq.
 *
 * @author  Michael Heuer
 */
public final class DisinterleaveFastqTest {
    private File pairedFile;
    private File unpairedFile;
    private File firstFastqFile;
    private File secondFastqFile;

    @Before
    public void setUp() throws IOException {
        pairedFile = File.createTempFile("disinterleaveFastqTest", ".fq.gz");
        unpairedFile = File.createTempFile("disinterleaveFastqTest", ".fq.gz");
        firstFastqFile = File.createTempFile("disinterleaveFastqTest", ".fq");
        secondFastqFile = File.createTempFile("disinterleaveFastqTest", ".fq");
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
        new DisinterleaveFastq(null, unpairedFile, firstFastqFile, secondFastqFile);
    }

    public void testConstructorNullUnpairedFile() {
        assertNotNull(new DisinterleaveFastq(pairedFile, null, firstFastqFile, secondFastqFile));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFirstFastqFile() {
        new DisinterleaveFastq(pairedFile, unpairedFile, null, secondFastqFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSecondFastqFile() {
        new DisinterleaveFastq(pairedFile, unpairedFile, firstFastqFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new DisinterleaveFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile));
    }

    @Test
    public void testDisinterleaveFastq() throws Exception {
        copyResource("interleaved.fq.gz", pairedFile);
        copyResource("empty-unpaired.fq.gz", unpairedFile);
        new DisinterleaveFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();

        assertEquals(4, countFastq(firstFastqFile));
        assertEquals(4, countFastq(secondFastqFile));
    }

    @Test
    public void testDisinterleaveFastqUnpairedLeft() throws Exception {
        copyResource("interleaved.fq.gz", pairedFile);
        copyResource("unpaired-left.fq.gz", unpairedFile);
        new DisinterleaveFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();

        assertEquals(5, countFastq(firstFastqFile));
        assertEquals(4, countFastq(secondFastqFile));
    }

    @Test
    public void testDisinterleaveFastqUnpairedRight() throws Exception {
        copyResource("interleaved.fq.gz", pairedFile);
        copyResource("unpaired-right.fq.gz", unpairedFile);
        new DisinterleaveFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();

        assertEquals(4, countFastq(firstFastqFile));
        assertEquals(5, countFastq(secondFastqFile));
    }

    @Test(expected=IOException.class)
    public void testDisinterleaveFastqInvalidInterleaved() throws Exception {
        copyResource("invalid-interleaved.fq.gz", pairedFile);
        copyResource("empty-unpaired.fq.gz", unpairedFile);
        new DisinterleaveFastq(pairedFile, unpairedFile, firstFastqFile, secondFastqFile).call();
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
        Files.write(Resources.toByteArray(DisinterleaveFastqTest.class.getResource(name)), file);
    }
}
