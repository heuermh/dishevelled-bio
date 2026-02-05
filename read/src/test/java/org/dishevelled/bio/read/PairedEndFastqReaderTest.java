/*

    dsh-bio-reads  Reads.
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
package org.dishevelled.bio.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.dishevelled.bio.read.PairedEndFastqReader.isLeft;
import static org.dishevelled.bio.read.PairedEndFastqReader.isRight;
import static org.dishevelled.bio.read.PairedEndFastqReader.prefix;
import static org.dishevelled.bio.read.PairedEndFastqReader.streamInterleaved;
import static org.dishevelled.bio.read.PairedEndFastqReader.streamPaired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for PairedEndFastqReader.
 *
 * @author  Michael Heuer
 */
public final class PairedEndFastqReaderTest {
    private Fastq left;
    private Fastq right;
    private Fastq invalidPrefix;
    private Fastq mismatchPrefix;
    private Reader firstReader;
    private Reader secondReader;
    private Reader reader;
    private PairedEndListener listener;

    @Before
    public void setUp() throws Exception {
        left = Fastq.builder().withDescription("prefix 1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build();
        right = Fastq.builder().withDescription("prefix 2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build();
        mismatchPrefix = Fastq.builder().withDescription("mismatch 2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build();

        ByteArrayOutputStream first = new ByteArrayOutputStream();
        new SangerFastqWriter().write(first, left);
        firstReader = new StringReader(first.toString());

        ByteArrayOutputStream second = new ByteArrayOutputStream();
        new SangerFastqWriter().write(second, right);
        secondReader = new StringReader(second.toString());

        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, right));
        reader = new StringReader(interleaved.toString());

        listener = new PairedEndAdapter();
    }

    @Test(expected=NullPointerException.class)
    public void testIsLeftNull() {
        isLeft(null);
    }

    @Test
    public void testIsLeft() {
        assertTrue(isLeft(left));
        assertFalse(isLeft(right));
    }

    @Test(expected=NullPointerException.class)
    public void testIsRightNull() {
        isRight(null);
    }

    @Test
    public void testIsRight() {
        assertFalse(isRight(left));
        assertTrue(isRight(right));
    }

    @Test(expected=NullPointerException.class)
    public void testPrefixNull() {
        prefix(null);
    }

    @Test
    public void testPrefixSpace() {
        assertEquals("prefix", prefix(left));
        assertEquals("prefix", prefix(right));
    }

    @Test
    public void testPrefixPlus() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix+1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix+2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixUnderscore() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix_1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix_2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixBackslash() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix\\1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix\\2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixForwardSlash() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix/1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix/2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixWhitespacePlus() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix +1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix +2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixWhitespaceUnderscore() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix _1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix _2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixWhitespaceBackslash() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix \\1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix \\2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixWhitespaceForwardSlash() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix /1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix /2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testIsLeftIlluminaMetadata() {
        assertTrue(isLeft(Fastq.builder().withDescription("prefix 1:N:0:2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertFalse(isLeft(Fastq.builder().withDescription("prefix 2:Y:2:42").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testIsRightIlluminaMetadata() {
        assertFalse(isRight(Fastq.builder().withDescription("prefix 1:N:0:2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertTrue(isRight(Fastq.builder().withDescription("prefix 2:Y:2:42").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixIlluminaMetadata() {
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix 1:N:0:2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("prefix", prefix(Fastq.builder().withDescription("prefix 2:Y:2:42").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testIsLeftIndexSequences() {
        assertTrue(isLeft(Fastq.builder().withDescription("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123 1:N:0:ATCACG").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertFalse(isLeft(Fastq.builder().withDescription("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123 2:N:0:ATCACG").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testIsRightIndexSequences() {
        assertFalse(isRight(Fastq.builder().withDescription("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123 1:N:0:ATCACG").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertTrue(isRight(Fastq.builder().withDescription("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123 2:N:0:ATCACG").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test
    public void testPrefixIndexSequences() {
        assertEquals("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123", prefix(Fastq.builder().withDescription("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123 1:N:0:ATCACG").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
        assertEquals("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123", prefix(Fastq.builder().withDescription("HISEQ_HU01:89:H7YRLADXX:1:1101:1116:2123 2:N:0:ATCACG").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build()));
    }

    @Test(expected=NullPointerException.class)
    public void testStreamPairedNullFirstReader() throws Exception {
        streamPaired(null, secondReader, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamPairedNullSecondReader() throws Exception {
        streamPaired(firstReader, null, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamPairedNullListener() throws Exception {
        streamPaired(firstReader, secondReader, null);
    }

    @Test
    public void testStreamPaired() throws Exception {
        streamPaired(firstReader, secondReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                    assertEquals(PairedEndFastqReaderTest.this.right.getDescription(), right.getDescription());
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    fail("unpaired " + unpaired);
                }
            });
    }

    @Test
    public void testStreamPairedUnpaired() throws Exception {
        streamPaired(firstReader, firstReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    fail("paired " + left + " " + right);
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                }
            });
    }

    @Test
    public void testStreamPairedMismatchPrefix() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, mismatchPrefix);
        Reader mismatchPrefixReader = new StringReader(outputStream.toString());

        streamPaired(firstReader, mismatchPrefixReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    fail("paired " + left + " " + right);
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    assertTrue(left.getDescription().equals(unpaired.getDescription()) ||
                               mismatchPrefix.getDescription().equals(unpaired.getDescription()));
                }
            });
    }

    @Test(expected=NullPointerException.class)
    public void testStreamInterleavedNullReader() throws Exception {
        streamInterleaved(null, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamInterleavedNullListener() throws Exception {
        streamInterleaved(reader, null);
    }

    @Test
    public void testStreamInterleaved() throws Exception {
        streamInterleaved(reader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                    assertEquals(PairedEndFastqReaderTest.this.right.getDescription(), right.getDescription());
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    fail("unpaired " + unpaired);
                }
            });
    }

    @Test
    public void testStreamInterleavedUnpairedLeft() throws Exception {
        streamInterleaved(firstReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedUnpairedRight() throws Exception {
        streamInterleaved(secondReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedOnlyLeft() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, left));
        Reader onlyLeftReader = new StringReader(interleaved.toString());

        streamInterleaved(onlyLeftReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedOnlyRight() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(right, right));
        Reader onlyRightReader = new StringReader(interleaved.toString());

        streamInterleaved(onlyRightReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedDuplicateLeft() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, left, right));
        Reader duplicateLeftReader = new StringReader(interleaved.toString());

        streamInterleaved(duplicateLeftReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedDuplicateRight() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, right, right));
        Reader duplicateRightReader = new StringReader(interleaved.toString());

        streamInterleaved(duplicateRightReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedMismatchPrefix() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, mismatchPrefix));
        Reader mismatchPrefixReader = new StringReader(interleaved.toString());

        streamInterleaved(mismatchPrefixReader, listener);
    }
}
