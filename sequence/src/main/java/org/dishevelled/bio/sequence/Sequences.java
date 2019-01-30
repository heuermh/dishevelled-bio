/*

    dsh-bio-sequence  Sequences.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.nio.ByteBuffer;

/**
 * Utility methods on sequences.
 *
 * @author  Michael Heuer
 */
public final class Sequences {

    /**
     * Decode the specified byte buffer as an unambiguous DNA sequence the specified length
     * as a string.
     *
     * @see #encode(String,ByteBuffer)
     * @param bytes byte buffer, must not be null
     * @param length length, must be at least 0
     * @return the specified byte buffer decoded as an unambiguous DNA sequence the specified
     *    length as a string
     * @throws IOException if an I/O error occurs
     */
    public static String decode(final ByteBuffer bytes, final int length) throws IOException {
        checkNotNull(bytes);
        checkArgument(length >= 0, "length must be at least 0");
        StringBuilder sb = new StringBuilder(length);
        decode(bytes, length, sb);
        return sb.toString();
    }

    private static char toChar(final int i) {
        switch (i) {
        case 0: return 'T';
        case 1: return 'C';
        case 2: return 'A';
        case 3: return 'G';
        default: throw new IllegalArgumentException("invalid bits " + i);
        }
    }

    /**
     * Decode the specified byte buffer as an unambiguous DNA sequence the specified length
     * to the specified appendable.
     *
     * @see #encode(String,ByteBuffer)
     * @param <T> appendable type
     * @param bytes byte buffer, must not be null
     * @param length length, must be at least 0
     * @param appendable appendable to decode to, must not be null
     * @return the specified byte buffer decoded as an unambiguous DNA sequence the specified
     *    length to the specified appendable
     * @throws IOException if an I/O error occurs
     */
    public static <T extends Appendable> T decode(final ByteBuffer bytes, final int length, final T appendable) throws IOException {
        checkNotNull(bytes);
        checkArgument(length >= 0, "length must be at least 0");
        checkNotNull(appendable);

        for (int i = 0; i < length; i += 4) {
            byte b = bytes.get();
            int base0 = (b >> 6) & 3;
            int base1 = (b >> 4) & 3;
            int base2 = (b >> 2) & 3;
            int base3 = b & 3;

            appendable.append(toChar(base0));
            if (i + 1 < length) {
                appendable.append(toChar(base1));
            }
            if (i + 2 < length) {
                appendable.append(toChar(base2));
            }
            if (i + 3 < length) {
                appendable.append(toChar(base3));
            }
        }
        return appendable;
    }

    /**
     * Encode the specified unambiguous DNA sequence to a new byte buffer.
     *
     * Valid unambiguous DNA sequence symbols are {A,C,G,T,a,c,g,t}. Similar to
     * <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format7">twoBit format</a>
     * the DNA symbols are packed to two bits per base, represented as so: T - 00,
     * C - 01, A - 10, G - 11. The first base is in the most significant 2-bit byte;
     * the last base is in the least significant 2 bits. For example, the sequence TCAG
     * is represented as 00011011.
     *
     * @param sequence unambiguous DNA sequence to encode, must not be null
     * @return the specified unambiguous DNA sequence encoded to a new byte buffer
     * @throws IllegalArgumentException if the specified sequence contains any ambiguity symbols
     */
    public static ByteBuffer encode(final String sequence) {
        checkNotNull(sequence);
        return encode(sequence, ByteBuffer.allocate(sequence.length()/4 + 1));
    }

    private static int toInt(final char c) {
        switch (c) {
        case 't':
        case 'T':
            return 0;
        case 'c':
        case 'C':
            return 1;
        case 'a':
        case 'A':
            return 2;
        case 'g':
        case 'G':
            return 3;
        default: throw new IllegalArgumentException("invalid symbol " + c);
        }
    }

    /**
     * Encode the specified unambiguous DNA sequence to the specified byte buffer.
     *
     * Valid unambiguous DNA sequence symbols are {A,C,G,T,a,c,g,t}. Similar to
     * <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format7">twoBit format</a>
     * the DNA symbols are packed to two bits per base, represented as so: T - 00,
     * C - 01, A - 10, G - 11. The first base is in the most significant 2-bit byte;
     * the last base is in the least significant 2 bits. For example, the sequence TCAG
     * is represented as 00011011.
     *
     * @param sequence unambiguous DNA sequence to encode, must not be null
     * @param bytes byte buffer, must not be null
     * @return the specified unambiguous DNA sequence encoded to the specified byte buffer
     * @throws IllegalArgumentException if the specified sequence contains any ambiguity symbols
     */
    public static ByteBuffer encode(final String sequence, final ByteBuffer bytes) {
        checkNotNull(sequence);
        checkNotNull(bytes);

        bytes.mark();
        int length = sequence.length();
        for (int i = 0; i < length; i += 4) {
            int base0 = toInt(sequence.charAt(i));
            int base1 = (i + 1 < length) ? toInt(sequence.charAt(i + 1)) : 0;
            int base2 = (i + 2 < length) ? toInt(sequence.charAt(i + 2)) : 0;
            int base3 = (i + 3 < length) ? toInt(sequence.charAt(i + 3)) : 0;
            bytes.put((byte) ((base0 << 6) + (base1 << 4) + (base2 << 2) + base3));
        }
        bytes.reset();
        return bytes;
    }

    /**
     * Decode the specified byte buffer as a DNA sequence with N ambiguity symbols the specified length
     * as a string.
     *
     * @see #encodeWithNs(String,ByteBuffer)
     * @param bytes byte buffer, must not be null
     * @param length length, must be at least 0
     * @return the specified byte buffer decoded as a DNA sequence with N ambiguity symbols the specified
     *    length as a string
     * @throws IOException if an I/O error occurs
     */
    public static String decodeWithNs(final ByteBuffer bytes, final int length) throws IOException {
        checkNotNull(bytes);
        checkArgument(length >= 0, "length must be at least 0");
        StringBuilder sb = new StringBuilder(length);
        decodeWithNs(bytes, length, sb);
        return sb.toString();
    }

    private static char nibbleToChar(final int i) {
        switch (i) {
        case 0: return 'T';
        case 1: return 'C';
        case 2: return 'A';
        case 3: return 'G';
        case 4: return 'N';
        // case 5 is masked flag, we don't set it
        default: throw new IllegalArgumentException("invalid bits " + i);
        }
    }

    /**
     * Decode the specified byte buffer as a DNA sequence with N ambiguity symbols the specified length
     * to the specified appendable.
     *
     * @see #encodeWithNs(String,ByteBuffer)
     * @param <T> appendable type
     * @param bytes byte buffer, must not be null
     * @param length length, must be at least 0
     * @param appendable appendable to decode to, must not be null
     * @return the specified byte buffer decoded as a DNA sequence with N ambiguity symbols the specified
     *    length to the specified appendable
     * @throws IOException if an I/O error occurs
     */
    public static <T extends Appendable> T decodeWithNs(final ByteBuffer bytes, final int length, final T appendable) throws IOException {
        checkNotNull(bytes);
        checkArgument(length >= 0, "length must be at least 0");
        checkNotNull(appendable);

        for (int i = 0; i < length; i += 2) {
            byte b = bytes.get();
            int base0 = (b >> 4) & 7;
            int base1 = b & 7;
            appendable.append(nibbleToChar(base0));
            if (i + 1 < length) {
                appendable.append(nibbleToChar(base1));
            }
        }
        return appendable;
    }
    
    /**
     * Encode the specified DNA sequence with N ambiguity symbols to a new byte buffer.
     *
     * Valid DNA sequence with N ambiguity symbols are {A,C,G,T,N,a,c,g,t,n}. Similar to
     * <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format8">.nib format</a>
     * the DNA symbols are packed two bases to the byte. The first base is packed in the
     * high-order 4 bits (nibble); the second base is packed in the low-order four bits:
     * <code>byte = (base0&lt;&lt;4) + base1</code>. The numerical representations for the
     * bases are T - 0, C - 1, A - 2, G - 3, N - 4.
     *
     * @param sequence DNA sequence with N ambiguity symbols to encode, must not be null
     * @return the specified DNA sequence with N ambiguity symbols encoded to a new byte buffer
     * @throws IllegalArgumentException if the specified sequence contains any ambiguity symbols
     *   other than {N,n}
     */
    public static ByteBuffer encodeWithNs(final String sequence) {
        checkNotNull(sequence);
        return encodeWithNs(sequence, ByteBuffer.allocate(sequence.length()/2 + 1));
    }

    private static int nibbleToInt(final char c) {
        switch (c) {
        case 't':
        case 'T':
            return 0;
        case 'c':
        case 'C':
            return 1;
        case 'a':
        case 'A':
            return 2;
        case 'g':
        case 'G':
            return 3;
        case 'n':
        case 'N':
            return 4;
        default: throw new IllegalArgumentException("invalid symbol " + c);
        }
    }

    /**
     * Encode the specified DNA sequence with N ambiguity symbols to the specified byte buffer.
     *
     * Valid DNA sequence with N ambiguity symbols are {A,C,G,T,N,a,c,g,t,n}. Similar to
     * <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format8">.nib format</a>
     * the DNA symbols are packed two bases to the byte. The first base is packed in the
     * high-order 4 bits (nibble); the second base is packed in the low-order four bits:
     * <code>byte = (base0&lt;&lt;4) + base1</code>. The numerical representations for the
     * bases are T - 0, C - 1, A - 2, G - 3, N - 4.
     *
     * @param sequence DNA sequence with N ambiguity symbols to encode, must not be null
     * @param bytes byte buffer, must not be null
     * @return the specified DNA sequence with N ambiguity symbols encoded to the specified byte
     *    buffer
     * @throws IllegalArgumentException if the specified sequence contains any ambiguity symbols
     *   other than {N,n}
     */
    public static ByteBuffer encodeWithNs(final String sequence, final ByteBuffer bytes) {
        checkNotNull(sequence);
        checkNotNull(bytes);
        bytes.mark();
        int length = sequence.length();
        for (int i = 0; i < length; i += 2) {
            int base0 = nibbleToInt(sequence.charAt(i));
            int base1 = (i + 1 < length) ? nibbleToInt(sequence.charAt(i + 1)) : 0;
            bytes.put((byte) ((base0 << 4) + base1));
        }
        bytes.reset();
        return bytes;
    }

    // useful for debug
    static String formatBits(final byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
}
