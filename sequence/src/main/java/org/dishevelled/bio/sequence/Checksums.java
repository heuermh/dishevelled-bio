/*

    dsh-bio-sequence  Sequences.
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
package org.dishevelled.bio.sequence;

/**
 * Checksums for sequences.
 *
 * @since 2.4
 * @author  Michael Heuer
 */
public final class Checksums {

    /** Cached CRC64 implementation. */
    private static final Crc64Iso CRC64ISO = new Crc64Iso();

    /** Cached improved CRC64 implementation. */
    private static final ImprovedCrc64 IMPROVEDCRC64 = new ImprovedCrc64();

    /** Cached SHA-256 hash implementation. */
    private static final Sha256 SHA256 = new Sha256();

    /** Cached <code>sha512t24u</code> truncated digest implementation. */
    private static final Sha512t24u SHA512T24U = new Sha512t24u();


    /**
     * Return the CRC64 checksum for the specified DNA or protein sequence.
     *
     * Used in UniProtKB, https://www.uniprot.org/help/checksum.
     *
     * The checksum is computed as the sequence 64-bit Cyclic Redundancy Check value (CRC64)
     * using the generator polynomial: <code>x^64 + x^4 + x^3 + x + 1</code>. The algorithm is
     * described in the ISO 3309 standard.
     *
     * Press W.H., Flannery B.P., Teukolsky S.A. and Vetterling W.T. Cyclic redundancy and other
     * checksums, Numerical recipes in C 2nd ed., pp 896-902, Cambridge University Press (1993).
     *
     * @param sequence DNA or protein sequence, must not be null
     * @return the CRC64 checksum for the specified DNA or protein sequence
     */
    public static String crc64(final String sequence) {
        return CRC64ISO.checksum(sequence);
    }

    /**
     * Return the improved CRC64 checksum for the specified DNA or protein sequence.
     *
     * The CRC64 algorithm employed in the SWISSPROT and TrEMBL data banks is shown to
     * have a flaw which greatly increases the likelihood of duplicate key values being
     * generated for pairs of sequences differing in only 2, 3 or 4 positions. A new CRC
     * function has been implemented which behaves with better statistical properties when
     * applied to a large set of similar but distinct protein sequences.
     *
     * Jones. An Improved 64-bit Cyclic Redundancy Check for Protein Sequences.
     * http://www0.cs.ucl.ac.uk/staff/David.Jones/crcnote.pdf
     *
     * @param sequence DNA or protein sequence, must not be null
     * @return the improved CRC64 checksum for the specified DNA or protein sequence
     */
    public static String improvedCrc64(final String sequence) {
        return IMPROVEDCRC64.checksum(sequence);
    }

    /**
     * Return the SHA-256 hash for the specified DNA or protein sequence.
     *
     * @param sequence DNA or protein sequence, must not be null
     * @return the SHA-256 hash for the specified DNA or protein sequence
     */
    public static String sha256(final String sequence) {
        return SHA256.sha256(sequence);
    }

    /**
     * Return the <code>sha512t24u</code> truncated digest for the specified DNA or protein sequence.
     *
     * The sha512t24u truncated digest algorithm computes an ASCII digest from binary data. The method
     * uses two well-established standard algorithms, the SHA-512 hash function, which generates a binary
     * digest from binary data, and Base64 URL encoding, which encodes binary data using printable characters.
     *
     * See https://vrs.ga4gh.org/en/1.0/impl-guide/computed_identifiers.html#truncated-digest
     *
     * @param sequence DNA or protein sequence, must not be null
     * @return the <code>sha512t24u</code> truncated digest for the specified DNA or protein sequence
     */
    public static String sha512t24u(final String sequence) {
        return SHA512T24U.sha512t24u(sequence);
    }
}
