/*

    dsh-bio-reads  Reads.
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
package org.dishevelled.bio.read;

/**
 * Runtime exception thrown on error in reading paired end FASTQ reads.
 *
 * @author  Michael Heuer
 */
public final class PairedEndFastqReaderException extends RuntimeException {

    /**
     * Create a new paired end reader FASTQ exception with the specified message.
     *
     * @param message message
     */
    PairedEndFastqReaderException(final String message) {
        super(message);
    }
}
