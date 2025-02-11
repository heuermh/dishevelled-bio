/*

    dsh-bio-sequence  Sequences.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.charset.StandardCharsets;

/**
 * Abstract CRC64 ISO implementation, ported from Jones <code>crc64.c</code>.
 *
 * @since 2.4
 * @author  Michael Heuer
 */
abstract class AbstractCrc64 {

    /** Checksum table. */
    private long[] CRC_TABLE = new long[256];

    /** Polynomial. */
    private final long poly64Rev;

    /** Initial CRC value. */
    private final long initialCrc;


    /**
     * Create a new abstract CRC64 implementation.
     *
     * @param poly64Rev polynomial
     * @param initialCrc initial CRC value
     */
    AbstractCrc64(final long poly64Rev, final long initialCrc) {
        this.poly64Rev = poly64Rev;
        this.initialCrc = initialCrc;

        for (int i = 0; i < 256; i++) {
            long part = i;
            for (int j = 0; j < 8; j++) {
                if ((part & 1) != 0)
                    part = (part >>> 1) ^ poly64Rev;
                else
                    part >>>= 1;
            }
            CRC_TABLE[i] = part;
        }
    }


    final String checksum(final String sequence) {
        checkNotNull(sequence);
        byte[] bytes = sequence.getBytes(StandardCharsets.UTF_8);

        long crc = initialCrc;
        for (byte value : bytes) {
            crc = CRC_TABLE[(int) ((crc ^ value) & 0xFF)] ^ (crc >>> 8);
        }
        int low = (int) (crc & 0xffffffff);
        int high = (int) ((crc >>> 32) & 0xffffffff);
        return String.format("%08X%08X", high, low);
    }
}
