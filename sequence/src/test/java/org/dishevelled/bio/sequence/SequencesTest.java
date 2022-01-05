/*

    dsh-bio-sequence  Sequences.
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
package org.dishevelled.bio.sequence;

import static org.dishevelled.bio.sequence.Sequences.decode;
import static org.dishevelled.bio.sequence.Sequences.decodeWithNs;
import static org.dishevelled.bio.sequence.Sequences.decodeWithAmbiguity;
import static org.dishevelled.bio.sequence.Sequences.encode;
import static org.dishevelled.bio.sequence.Sequences.encodeWithNs;
import static org.dishevelled.bio.sequence.Sequences.encodeWithAmbiguity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Sequences.
 *
 * @author  Michael Heuer
 */
public final class SequencesTest {
    private ByteBuffer bytes;
    private static final int CAPACITY = 64;

    @Before
    public void setUp() {
        bytes = ByteBuffer.allocate(CAPACITY);
    }

    @Test(expected=NullPointerException.class)
    public void testDecodeNullBytes() throws Exception {
        decode(null, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDecodeIllegalLength() throws Exception {
        decode(bytes, -1);
    }

    @Test
    public void testDecodeLengthZero() throws Exception {
        assertEquals("", decode(bytes, 0));
    }

    @Test
    public void testDecodeGs() throws Exception {
        bytes.mark();
        bytes.put((byte) 255).limit(1).reset();
        assertEquals("GGGG", decode(bytes, 4));
    }

    @Test
    public void testDecodeTs() throws Exception {
        bytes.mark();
        bytes.put((byte) 0).limit(1).reset();
        assertEquals("TTTT", decode(bytes, 4));
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeNullSequence() {
        encode(null);
    }

    @Test
    public void testEncodeEmptySequence() {
        ByteBuffer encoded = encode("");
        assertNotNull(encoded);
    }

    @Test
    public void testEncode() {
        ByteBuffer encoded = encode("atgc");
        assertNotNull(encoded);
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeByteBufferNullSequence() {
        encode(null, bytes);
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeByteBufferNullBytes() {
        encode("atcg", null);
    }

    @Test
    public void testEncodeByteBufferEmptySequence() {
        ByteBuffer encoded = encode("", bytes);
        assertNotNull(encoded);
    }

    @Test
    public void testEncodeByteBuffer() {
        ByteBuffer encoded = encode("atgc", bytes);
        assertNotNull(encoded);
    }

    @Test
    public void roundTripMod4() throws Exception {
        assertEquals("ATGC", decode(encode("ATGC"), 4));
    }

    @Test
    public void roundTripEven() throws Exception {
        assertEquals("ATGCCC", decode(encode("ATGCCC"), 6));
    }

    @Test
    public void roundTripOdd() throws Exception {
        assertEquals("ATGCCCTTA", decode(encode("ATGCCCTTA"), 9));
    }

    @Test
    public void testEncodeInvalidSymbolN() throws Exception {
        try {
            encode("ATGCTN");
            fail("encodeNibble with N symbol expected IAE");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("invalid symbol"));
        }
    }

    @Test
    public void testEncodeInvalidSymboln() throws Exception {
        try {
            encode("ATGCTn");
            fail("encodeNibble with n symbol expected IAE");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("invalid symbol"));
        }
    }

    @Test
    public void testEncodeInvalidSymbolDot() throws Exception {
        try {
            encode("ATGCT.");
            fail("encode with . symbol expected IAE");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("invalid symbol"));
        }
    }

    // ---

    @Test(expected=NullPointerException.class)
    public void testDecodeWithNsNullBytes() throws Exception {
        decodeWithNs(null, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDecodeWithNsIllegalLength() throws Exception {
        decodeWithNs(bytes, -1);
    }

    @Test
    public void testDecodeWithNsLengthZero() throws Exception {
        assertEquals("", decodeWithNs(bytes, 0));
    }

    @Test
    public void testDecodeWithNsGs() throws Exception {
        bytes.mark();
        bytes.put((byte) 51).limit(1).reset();
        assertEquals("GG", decodeWithNs(bytes, 2));
    }

    @Test
    public void testDecodeWithNsTs() throws Exception {
        bytes.mark();
        bytes.put((byte) 0).limit(1).reset();
        assertEquals("TT", decodeWithNs(bytes, 2));
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeWithNsNullSequence() {
        encodeWithNs(null);
    }

    @Test
    public void testEncodeWithNsEmptySequence() {
        ByteBuffer encoded = encodeWithNs("");
        assertNotNull(encoded);
    }

    @Test
    public void testEncodeWithNs() {
        ByteBuffer encoded = encodeWithNs("atgc");
        assertNotNull(encoded);
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeWithNsByteBufferNullSequence() {
        encodeWithNs(null, bytes);
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeWithNsByteBufferNullBytes() {
        encodeWithNs("atcg", null);
    }

    @Test
    public void testEncodeWithNsByteBufferEmptySequence() {
        ByteBuffer encoded = encodeWithNs("", bytes);
        assertNotNull(encoded);
    }

    @Test
    public void testEncodeWithNsByteBuffer() {
        ByteBuffer encoded = encodeWithNs("atgc", bytes);
        assertNotNull(encoded);
    }

    @Test
    public void nibbleRoundTripMod4() throws Exception {
        assertEquals("ATGC", decodeWithNs(encodeWithNs("AtgC"), 4));
    }

    @Test
    public void nibbleRoundTripEven() throws Exception {
        assertEquals("ATGCCC", decodeWithNs(encodeWithNs("ATgcCC"), 6));
    }

    @Test
    public void nibbleRoundTripOdd() throws Exception {
        assertEquals("ATGCCCTTA", decodeWithNs(encodeWithNs("ATGCcCTTA"), 9));
    }

    @Test
    public void nibbleRoundTripNs() throws Exception {
        assertEquals("ATGCNNNNCGTA", decodeWithNs(encodeWithNs("ATGCNnnNCGTA"), 12));
    }

    @Test
    public void testEncodeWithNsInvalidSymbolDot() {
        try {
            encodeWithNs("ATGCT.");
            fail("encodeWithNs with . symbol expected IAE");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("invalid symbol"));
        }
    }

    // ---

    @Test(expected=NullPointerException.class)
    public void testDecodeWithAmbiguityNullBytes() throws Exception {
        decodeWithAmbiguity(null, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDecodeWithAmbiguityIllegalLength() throws Exception {
        decodeWithAmbiguity(bytes, -1);
    }

    @Test
    public void testDecodeWithAmbiguityLengthZero() throws Exception {
        assertEquals("", decodeWithAmbiguity(bytes, 0));
    }

    @Test
    public void testDecodeWithAmbiguityGs() throws Exception {
        bytes.mark();
        // bits 0100 0100 is 68
        bytes.put((byte) 68).limit(1).reset();
        assertEquals("GG", decodeWithAmbiguity(bytes, 2));
    }

    @Test
    public void testDecodeWithAmbiguityTs() throws Exception {
        bytes.mark();
        // bits 1000 1000 is 136
        bytes.put((byte) 136).limit(1).reset();
        assertEquals("TT", decodeWithAmbiguity(bytes, 2));
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeWithAmbiguityNullSequence() {
        encodeWithAmbiguity(null);
    }

    @Test
    public void testEncodeWithAmbiguityEmptySequence() {
        ByteBuffer encoded = encodeWithAmbiguity("");
        assertNotNull(encoded);
    }

    @Test
    public void testEncodeWithAmbiguity() {
        ByteBuffer encoded = encodeWithAmbiguity("atgc");
        assertNotNull(encoded);
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeWithAmbiguityByteBufferNullSequence() {
        encodeWithAmbiguity(null, bytes);
    }

    @Test(expected=NullPointerException.class)
    public void testEncodeWithAmbiguityByteBufferNullBytes() {
        encodeWithAmbiguity("atcg", null);
    }

    @Test
    public void testEncodeWithAmbiguityByteBufferEmptySequence() {
        ByteBuffer encoded = encodeWithAmbiguity("", bytes);
        assertNotNull(encoded);
    }

    @Test
    public void testEncodeWithAmbiguityByteBuffer() {
        ByteBuffer encoded = encodeWithAmbiguity("atgc", bytes);
        assertNotNull(encoded);
    }

    @Test
    public void ambiguousNibbleRoundTripMod4() throws Exception {
        assertEquals("ATGC", decodeWithAmbiguity(encodeWithAmbiguity("AtgC"), 4));
    }

    @Test
    public void ambiguousNibbleRoundTripEven() throws Exception {
        assertEquals("ATGCCC", decodeWithAmbiguity(encodeWithAmbiguity("ATgcCC"), 6));
    }

    @Test
    public void ambiguousNibbleRoundTripOdd() throws Exception {
        assertEquals("ATGCCCTTA", decodeWithAmbiguity(encodeWithAmbiguity("ATGCcCTTA"), 9));
    }

    @Test
    public void ambiguousNibbleRoundTripAmbiguity() throws Exception {
        assertEquals("ATGCNNNNCGTA", decodeWithAmbiguity(encodeWithAmbiguity("ATGCNnnNCGTA"), 12));
    }

    @Test
    public void testEncodeWithAmbiguityAllAmbiguousSymbols() throws Exception {
        assertEquals("=AACCMMGGRRSSVVTTWWYYHHKKDDBBNN", decodeWithAmbiguity(encodeWithAmbiguity("=AaCcMmGgRrSsVvTtWwYyHhKkDdBbNn"), 31));
    }

    @Test
    public void testEncodeWithAmbiguityInvalidSymbols() throws Exception {
        // all invalid symbols are mapped to `N`
        assertEquals("ATGCTNNNNN", decodeWithAmbiguity(encodeWithAmbiguity("ATGCT.zZ3#"), 10));
    }
}
