/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa1;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;

import javax.annotation.concurrent.Immutable;

/**
 * Graphical Fragment Assembly (GFA) 1.0 writer.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Gfa1Writer {

    /**
     * Private no-arg constructor.
     */
    private Gfa1Writer() {
        // empty
    }


    /**
     * Write the specified GFA 1.0 record with the specified print writer.
     *
     * @param record GFA 1.0 record to write, must not be null
     * @param writer print writer to write GFA 1.0 record with, must not be null
     */
    public static void write(final Gfa1Record record, final PrintWriter writer) {
        checkNotNull(record);
        checkNotNull(writer);
        writer.println(record.toString());
    }

    /**
     * Write zero or more GFA 1.0 records with the specified print writer.
     *
     * @param records zero or more GFA 1.0 records to write, must not be null
     * @param writer print writer to write GFA 1.0 records with, must not be null
     */
    public static void write(final Iterable<Gfa1Record> records, final PrintWriter writer) {
        checkNotNull(records);
        checkNotNull(writer);
        for (Gfa1Record record : records) {
            writer.println(record.toString());
        }
    }
}
