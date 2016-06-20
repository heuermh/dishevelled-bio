/*

    dsh-bio-feature  Sequence features.
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
package org.dishevelled.bio.feature;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;

/**
 * GFF3 writer.
 *
 * @author  Michael Heuer
 */
public final class Gff3Writer {

    /**
     * Private no-arg constructor.
     */
    private Gff3Writer() {
        // empty
    }


    /**
     * Write the specified GFF3 record with the specified print writer.
     *
     * @param record GFF3 record to write, must not be null
     * @param writer print writer to write GFF3 record with, must not be null
     */
    public static void write(final Gff3Record record, final PrintWriter writer) {
        checkNotNull(record);
        checkNotNull(writer);
        writer.println(record.toString());
    }

    /**
     * Write zero or more GFF3 records with the specified print writer.
     *
     * @param records zero or more GFF3 records to write, must not be null
     * @param writer print writer to write GFF3 records with, must not be null
     */
    public static void write(final Iterable<Gff3Record> records, final PrintWriter writer) {
        checkNotNull(records);
        checkNotNull(writer);
        for (Gff3Record record : records) {
            writer.println(record.toString());
        }
    }
}
