/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * Position.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Position {
    /** Position. */
    private final int position;

    /** True if this position is a terminal position. */
    private final boolean terminal;


    /**
     * Create a new position.
     *
     * @param position position, must be at least zero
     * @param terminal true if this position is a terminal position
     */
    public Position(final int position, final boolean terminal) {
        checkArgument(position >= 0, "position must be at least zero");
        this.position = position;
        this.terminal = terminal;
    }


    /**
     * Return the position for this position.
     *
     * @return the position for this position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Return true if this position is a terminal position.
     *
     * @return true if this position is a terminal position
     */
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, terminal);
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position p = (Position) o;

        return Objects.equals(position, p.getPosition())
            && Objects.equals(terminal, p.isTerminal());
    }

    @Override
    public String toString() {
        return terminal ? position + "$" : String.valueOf(position);
    }


    /**
     * Parse a position from the specified value.
     *
     * @param value value, must not be null
     * @return a position parsed from the specified value
     */
    public static Position valueOf(final String value) {
        checkNotNull(value);
        if (value.endsWith("$")) {
            return new Position(Integer.parseInt(value.substring(0, value.length() - 1)), true);
        }
        return new Position(Integer.parseInt(value), false);
    }
}
