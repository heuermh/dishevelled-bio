/*

    dsh-bio-protein  Protein sequences and metadata.
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
package org.dishevelled.bio.protein.uniprot;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * Position.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
@Immutable
public final class Position {
    /** Position, if any. */
    private final Integer position;

    /** Status for this position. */
    private final PositionStatus status;


    /**
     * Create a new position.
     *
     * @param position position, if any
     * @param status status, must not be null
     */
    Position(@Nullable final Integer position, final PositionStatus status) {
        checkNotNull(status);
        this.position = position;
        this.status = status;
    }


    /**
     * Return the position, may be null.
     *
     * @return the position, may be null
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * Return the status for this position.
     *
     * @return the status for this position
     */
    public PositionStatus getStatus() {
        return status;
    }
}
