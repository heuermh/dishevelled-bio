/*

    dsh-bio-protein  Protein sequences and metadata.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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
package org.dishevelled.bio.protein.uniprot;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * Location.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
@Immutable
public final class Location {
    /** Begin position, if any. */
    private final Position begin;

    /** End position, if any. */
    private final Position end;

    /** Position, if any. */
    private final Position position;

    /** Location sequence, if any. */
    private final String sequence;


    /**
     * Create a new location with the specified begin and end positions.
     *
     * @param begin begin position, must not be null
     * @param end end position, must not be null
     * @param sequence location sequence, if any
     */
    Location(final Position begin, final Position end, @Nullable final String sequence) {
        checkNotNull(begin);
        checkNotNull(end);

        this.begin = begin;
        this.end = end;
        position = null;
        this.sequence = sequence;
    }

    /**
     * Create a new location with the specified position.
     *
     * @param position position, must not be null
     * @param sequence location sequence, if any
     */
    Location(final Position position, @Nullable final String sequence) {
        checkNotNull(position);

        begin = null;
        end = null;
        this.position = position;
        this.sequence = sequence;
    }


    /**
     * Return the begin position for this location, may be null.
     *
     * @return the begin position for this location, may be null
     */
    public Position getBegin() {
        return begin;
    }

    /**
     * Return the end position for this location, may be null.
     *
     * @return the end position for this location, may be null
     */
    public Position getEnd() {
        return end;
    }

    /**
     * Return the position for this location, may be null.
     *
     * @return the position for this location, may be null
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Return the sequence for this location, may be null.
     *
     * @return the sequence for this location, may be null
     */
    public String getSequence() {
        return sequence;
    }
}
