/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
package org.dishevelled.bio.alignment.sam;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;

import javax.annotation.concurrent.Immutable;

/**
 * SAM writer.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamWriter {

    /**
     * Private no-arg constructor.
     */
    private SamWriter() {
        // empty
    }


    /**
     * Write SAM with the specified print writer.
     *
     * @param header SAM header, must not be null
     * @param records zero or more SAM records, must not be null
     * @param writer print writer to write SAM with, must not be null
     */
    public static void write(final SamHeader header,
                             final Iterable<SamRecord> records,
                             final PrintWriter writer) {

        checkNotNull(header);
        checkNotNull(records);
        checkNotNull(writer);

        writeHeader(header, writer);
        writeRecords(records, writer);
    }

    /**
     * Write SAM header with the specified print writer.
     *
     * @param header SAM header, must not be null
     * @param writer print writer to write SAM with, must not be null
     */
    public static void writeHeader(final SamHeader header, final PrintWriter writer) {
        checkNotNull(header);
        checkNotNull(writer);

        header.getHeaderLineOpt().ifPresent(hl -> writer.println(hl));
        for (SamSequenceHeaderLine sequenceHeaderLine : header.getSequenceHeaderLines()) {
            writer.println(sequenceHeaderLine);
        }
        for (SamReadGroupHeaderLine readGroupHeaderLine : header.getReadGroupHeaderLines()) {
            writer.println(readGroupHeaderLine);
        }
        for (SamProgramHeaderLine programHeaderLine : header.getProgramHeaderLines()) {
            writer.println(programHeaderLine);
        }
        for (SamCommentHeaderLine commentHeaderLine : header.getCommentHeaderLines()) {
            writer.println(commentHeaderLine);
        }
    }

    /**
     * Write SAM records with the specified print writer.
     *
     * @param records zero or more SAM records, must not be null
     * @param writer print writer to write SAM with, must not be null
     */
    public static void writeRecords(final Iterable<SamRecord> records,
                                    final PrintWriter writer) {
        checkNotNull(records);
        checkNotNull(writer);

        for (SamRecord record : records) {
            writeRecord(record, writer);
        }
    }

    /**
     * Write a SAM record with the specified print writer.
     *
     * @param record SAM record, must not be null
     * @param writer print writer to write SAM with, must not be null
     */
    public static void writeRecord(final SamRecord record, final PrintWriter writer) {
        checkNotNull(record);
        checkNotNull(writer);
        writer.println(record.toString());
    }
}
