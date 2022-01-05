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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.nio.ByteBuffer;

/**
 * Utility methods on sequences.
 *
 * @since 1.1
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

    private static char toChar(final byte b) {
        switch (b) {
        case 0: return 'T';
        case 1: return 'C';
        case 2: return 'A';
        case 3: return 'G';
        default: throw new IllegalArgumentException("invalid bits " + b);
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
            byte base0 = (byte) ((b >> 6) & 3);
            byte base1 = (byte) ((b >> 4) & 3);
            byte base2 = (byte) ((b >> 2) & 3);
            byte base3 = (byte) (b & 3);

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
     * Valid unambiguous DNA sequence symbols are <code>{ A, C, G, T, a, c, g, t }</code>.
     * Similar to <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format7">twoBit format</a>
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

    private static byte toByte(final char c) {
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
     * Valid unambiguous DNA sequence symbols are <code>{ A, C, G, T, a, c, g, t }</code>.
     * Similar to <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format7">twoBit format</a>
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
            byte base0 = toByte(sequence.charAt(i));
            byte base1 = (i + 1 < length) ? toByte(sequence.charAt(i + 1)) : 0;
            byte base2 = (i + 2 < length) ? toByte(sequence.charAt(i + 2)) : 0;
            byte base3 = (i + 3 < length) ? toByte(sequence.charAt(i + 3)) : 0;
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

    private static char nibbleToChar(final byte b) {
        switch (b) {
        case 0: return 'T';
        case 1: return 'C';
        case 2: return 'A';
        case 3: return 'G';
        case 4: return 'N';
        // case 5 is masked flag, we don't set it
        default: throw new IllegalArgumentException("invalid bits " + b);
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
            byte base0 = (byte) ((b >> 4) & 7);
            byte base1 = (byte) (b & 7);
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
     * Valid DNA sequence with N ambiguity symbols are <code>{ A, C, G, T, N, a, c, g, t, n }</code>.
     * Similar to <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format8">.nib format</a>
     * the DNA symbols are packed two bases to the byte. The first base is packed in the
     * high-order 4 bits (nibble); the second base is packed in the low-order four bits:
     * <code>byte = (base0&lt;&lt;4) + base1</code>. The numerical representations for the
     * bases are T - 0, C - 1, A - 2, G - 3, N - 4.
     *
     * @param sequence DNA sequence with N ambiguity symbols to encode, must not be null
     * @return the specified DNA sequence with N ambiguity symbols encoded to a new byte buffer
     * @throws IllegalArgumentException if the specified sequence contains any ambiguity symbols
     *   other than { N, n }
     */
    public static ByteBuffer encodeWithNs(final String sequence) {
        checkNotNull(sequence);
        return encodeWithNs(sequence, ByteBuffer.allocate(sequence.length()/2 + 1));
    }

    private static byte nibbleToByte(final char c) {
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
     * Valid DNA sequence with N ambiguity symbols are <code>{ A, C, G, T, N, a, c, g, t, n }</code>.
     * Similar to <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format8">.nib format</a>
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
     *   other than <code>{ N, n }</code>
     */
    public static ByteBuffer encodeWithNs(final String sequence, final ByteBuffer bytes) {
        checkNotNull(sequence);
        checkNotNull(bytes);
        bytes.mark();
        int length = sequence.length();
        for (int i = 0; i < length; i += 2) {
            byte base0 = nibbleToByte(sequence.charAt(i));
            byte base1 = (i + 1 < length) ? nibbleToByte(sequence.charAt(i + 1)) : 0;
            bytes.put((byte) ((base0 << 4) + base1));
        }
        bytes.reset();
        return bytes;
    }

    /**
     * Decode the specified byte buffer as a DNA sequence with ambiguity symbols
     * the specified length as a string.
     *
     * @since 1.2
     * @see #encode(String,ByteBuffer)
     * @param bytes byte buffer, must not be null
     * @param length length, must be at least 0
     * @return the specified byte buffer decoded as a DNA sequence with ambiguity symbols
     *    the specified length as a string
     * @throws IOException if an I/O error occurs
     */
    public static String decodeWithAmbiguity(final ByteBuffer bytes, final int length) throws IOException {
        checkNotNull(bytes);
        checkArgument(length >= 0, "length must be at least 0");
        StringBuilder sb = new StringBuilder(length);
        decodeWithAmbiguity(bytes, length, sb);
        return sb.toString();
    }

    /**
     * Decode the specified byte buffer as a DNA sequence with ambiguity symbols
     * the specified length to the specified appendable.
     *
     * @since 1.2
     * @see #encode(String,ByteBuffer)
     * @param <T> appendable type
     * @param bytes byte buffer, must not be null
     * @param length length, must be at least 0
     * @param appendable appendable to decode to, must not be null
     * @return the specified byte buffer decoded as a DNA sequence with ambiguity symbols
     *    the specified length to the specified appendable
     * @throws IOException if an I/O error occurs
     */
    public static <T extends Appendable> T decodeWithAmbiguity(final ByteBuffer bytes, final int length, final T appendable) throws IOException {
        checkNotNull(bytes);
        checkArgument(length >= 0, "length must be at least 0");
        checkNotNull(appendable);

        for (int i = 0; i < length; i += 2) {
            byte b = bytes.get();
            byte base0 = (byte) ((b >> 4) & 15);
            byte base1 = (byte) (b & 15);
            appendable.append(ambiguousNibbleToChar(base0));
            if (i + 1 < length) {
                appendable.append(ambiguousNibbleToChar(base1));
            }
        }
        return appendable;
    }

    private static char ambiguousNibbleToChar(final byte b) {
        switch (b) {
        case 0: return '=';
        case 1: return 'A';
        case 2: return 'C';
        case 3: return 'M';
        case 4: return 'G';
        case 5: return 'R';
        case 6: return 'S';
        case 7: return 'V';
        case 8: return 'T';
        case 9: return 'W';
        case 10: return 'Y';
        case 11: return 'H';
        case 12: return 'K';
        case 13: return 'D';
        case 14: return 'B';
        case 15: return 'N';
        default: throw new IllegalArgumentException("invalid bits " + b);
        }
    }

    /**
     * Encode the specified DNA sequence with ambiguity symbols to a new byte buffer.
     *
     * Per the <a href="https://samtools.github.io/hts-specs/">BAM specification</a>,
     * ambiguity symbols <code>{ =, A, a, C, c, M, m, G, g, R, r, S, s, V, v, T, t, W, w, Y,
     * y, H, h, K, k, D, d, B, b, N, n }</code> are mapped to bytes in the range
     * <code>[0, 15]</code>, with other characters mapped to <code>N</code>;
     * high nibble first (1st symbol in the highest 4-bit of the 1st byte).
     *
     * @since 1.2
     * @param sequence DNA sequence with ambiguity symbols to encode, must not be null
     * @return the specified DNA sequence with ambiguity symbols encoded to a new byte buffer
     */
    public static ByteBuffer encodeWithAmbiguity(final String sequence) {
        checkNotNull(sequence);
        return encodeWithAmbiguity(sequence, ByteBuffer.allocate(sequence.length()/2 + 1));
    }

    /**
     * Encode the specified DNA sequence with ambiguity symbols to the specified byte buffer.
     *
     * Per the <a href="https://samtools.github.io/hts-specs/">BAM specification</a>,
     * ambiguity symbols <code>{ =, A, a, C, c, M, m, G, g, R, r, S, s, V, v, T, t, W, w, Y,
     * y, H, h, K, k, D, d, B, b, N, n }</code> are mapped to bytes in the range
     * <code>[0, 15]</code>, with other characters mapped to <code>N</code>;
     * high nibble first (1st symbol in the highest 4-bit of the 1st byte).
     *
     * @since 1.2
     * @param sequence DNA sequence with ambiguity symbols to encode, must not be null
     * @param bytes byte buffer, must not be null
     * @return the specified DNA sequence with ambiguity symbols encoded to the specified byte
     *    buffer
     */
    public static ByteBuffer encodeWithAmbiguity(final String sequence, final ByteBuffer bytes) {
        checkNotNull(sequence);
        checkNotNull(bytes);
        bytes.mark();
        int length = sequence.length();
        for (int i = 0; i < length; i += 2) {
            byte base0 = ambiguousNibbleToByte(sequence.charAt(i));
            byte base1 = (i + 1 < length) ? ambiguousNibbleToByte(sequence.charAt(i + 1)) : 0;
            bytes.put((byte) ((base0 << 4) + base1));
        }
        bytes.reset();
        return bytes;
    }

    private static byte ambiguousNibbleToByte(final char c) {
        switch (c) {
        case '=':
            return 0;
        case 'a':
        case 'A':
            return 1;
        case 'c':
        case 'C':
            return 2;
        case 'm':
        case 'M':
            return 3;
        case 'g':
        case 'G':
            return 4;
        case 'r':
        case 'R':
            return 5;
        case 's':
        case 'S':
            return 6;
        case 'v':
        case 'V':
            return 7;
        case 't':
        case 'T':
            return 8;
        case 'w':
        case 'W':
            return 9;
        case 'y':
        case 'Y':
            return 10;
        case 'h':
        case 'H':
            return 11;
        case 'k':
        case 'K':
            return 12;
        case 'd':
        case 'D':
            return 13;
        case 'b':
        case 'B':
            return 14;
        case 'n':
        case 'N':
        default:
            return 15;
        }
    }

    // useful for debug
    static String formatBits(final byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
}
