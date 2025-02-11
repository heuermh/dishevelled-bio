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

/**
 * Orientation.
 *
 * @author  Michael Heuer
 */
public enum Orientation {

    /** Forward orientation. */
    FORWARD,

    /** Reverse orientation. */
    REVERSE;

    /**
     * Return the symbol for this orientation, + or -.
     *
     * @since 2.0.3
     * @return the symbol for this orientation, + or -
     */
    public String getSymbol() {
        return isForward() ? "+" : "-";
    }

    /**
     * Return true if this orientation is <code>Orientation.FORWARD</code>.
     *
     * @return true if this orientation is <code>Orientation.FORWARD</code>
     */
    public boolean isForward() {
        return FORWARD.equals(this);
    }

    /**
     * Return true if this orientation is <code>Orientation.REVERSE</code>.
     *
     * @return true if this orientation is <code>Orientation.REVERSE</code>
     */
    public boolean isReverse() {
        return REVERSE.equals(this);
    }

    /**
     * Return the orientation opposite of this orientation.
     *
     * @return the orientation opposite of this orientation
     */
    public Orientation flip() {
        return isForward() ? REVERSE : FORWARD;
    }
}
