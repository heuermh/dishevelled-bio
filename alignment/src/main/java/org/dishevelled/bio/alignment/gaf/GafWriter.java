/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
package org.dishevelled.bio.alignment.gaf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;

import javax.annotation.concurrent.Immutable;

/**
 * GAF (graph alignment format) writer.
 *
 * @since 1.4
 * @author  Michal Heuer
 */
@Immutable
public final class GafWriter {

    /**
     * Private no-arg constructor.
     */
    private GafWriter() {
        // empty
    }


    /**
     * Write the specified GAF record with the specified print writer.
     *
     * @param record GAF record to write, must not be null
     * @param writer print writer to write GAF record with, must not be null
     */
    public static void write(final GafRecord record, final PrintWriter writer) {
        checkNotNull(record);
        checkNotNull(writer);
        writer.println(record.toString());
    }

    /**
     * Write zero or more GAF records with the specified print writer.
     *
     * @param records zero or more GAF records to write, must not be null
     * @param writer print writer to write GAF records with, must not be null
     */
    public static void write(final Iterable<GafRecord> records, final PrintWriter writer) {
        checkNotNull(records);
        checkNotNull(writer);
        for (GafRecord record : records) {
            writer.println(record.toString());
        }
    }
}

