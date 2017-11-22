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

import javax.annotation.concurrent.Immutable;

/**
 * Reference.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Reference {
    private final String id;
    private final Orientation orientation;

    public Reference(final String id,
                     final Orientation orientation) {

        checkNotNull(id);
        checkNotNull(orientation);

        this.id = id;
        this.orientation = orientation;
    }

    public String getId() {
        return id;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return id + (Orientation.REVERSE.equals(orientation) ? "-" : "+");
    }

    public static Reference valueOf(final String value) {
        checkNotNull(value);
        checkArgument(!value.isEmpty(), "value must not be empty");

        String id = value.substring(0, value.length() - 1);
        if (value.endsWith("-")) {
            return new Reference(id, Orientation.REVERSE);
        }
        else if (value.endsWith("+")) {
            return new Reference(id, Orientation.FORWARD);
        }
        throw new IllegalArgumentException("value must have an orientation");
    }
}
