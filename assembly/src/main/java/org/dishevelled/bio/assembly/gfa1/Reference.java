/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa1;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * Reference.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Reference {
    /** Identifier for this reference. */
    private final String id;

    /** Orientation for this reference. */
    private final Orientation orientation;


    /**
     * Create a new reference.
     *
     * @param id identifier, must not be null
     * @param orientation orientation, must not be null
     */
    public Reference(final String id,
                     final Orientation orientation) {

        checkNotNull(id);
        checkNotNull(orientation);

        this.id = id;
        this.orientation = orientation;
    }


    /**
     * Return the identifier for this reference.
     *
     * @deprecated replaced by name field, to be removed in version 3.0
     * @return the identifier for this reference
     */
    public String getId() {
        return id;
    }

    /**
     * Return the name for this reference.
     *
     * @since 2.0.3
     * @return the name for this reference
     */
    public String getName() {
        return getId();
    }

    /**
     * Return the orientation for this reference.
     *
     * @return the orientation for this reference
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Return true if the orientation for this reference is <code>Orientation.FORWARD</code>.
     *
     * @return true if the orientation for this reference is <code>Orientation.FORWARD</code>
     */
    public boolean isForwardOrientation() {
        return orientation.isForward();
    }

    /**
     * Return true if the orientation for this reference is <code>Orientation.REVERSE</code>.
     *
     * @return true if the orientation for this reference is <code>Orientation.REVERSE</code>
     */
    public boolean isReverseOrientation() {
        return orientation.isReverse();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orientation);
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        Reference r = (Reference) o;

        return Objects.equals(id, r.getId())
            && Objects.equals(orientation, r.getOrientation());
    }

    @Override
    public String toString() {
        return id + (Orientation.REVERSE.equals(orientation) ? "-" : "+");
    }

    /**
     * Return this reference as a string split by the tab character.
     *
     * @return this reference as a string split by the tab character
     */
    public String splitToString() {
        return id + "\t" + (Orientation.REVERSE.equals(orientation) ? "-" : "+");
    }

    /**
     * Parse a reference from the specified value.
     *
     * @param value value, must not be null
     * @return a reference parsed from the specified value
     */
    public static Reference valueOf(final String value) {
        checkNotNull(value);
        checkArgument(!value.isEmpty(), "reference value must not be empty");

        String id = value.substring(0, value.length() - 1);
        if (value.endsWith("-")) {
            return new Reference(id, Orientation.REVERSE);
        }
        else if (value.endsWith("+")) {
            return new Reference(id, Orientation.FORWARD);
        }
        throw new IllegalArgumentException("reference value '" + value + "' must have an orientation");
    }

    /**
     * Parse a reference from the specified values.
     *
     * @param id id, must not be null
     * @param orientation orientation, must be one of {<code>+</code>,<code>-</code>}
     * @return a reference parsed from the specified values
     */
    public static Reference splitValueOf(final String id, final String orientation) {
        checkNotNull(id);
        checkNotNull(orientation);
        checkNotNull(!id.isEmpty(), "reference id must not be empty");

        if ("-".equals(orientation)) {
            return new Reference(id, Orientation.REVERSE);
        }
        else if ("+".equals(orientation)) {
            return new Reference(id, Orientation.FORWARD);
        }
        throw new IllegalArgumentException("reference orientation must be one of {+,-}, was " + orientation);
    }
}
