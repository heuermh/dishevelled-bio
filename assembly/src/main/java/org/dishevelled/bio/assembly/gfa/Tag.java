/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.gfa;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Tag.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Tag {
    /** Name of this tag. */
    private final String name;

    /** Type for this tag. */
    private final String type;

    /** Value for this tag. */
    private final String value;


    /**
     * Create a new tag.
     *
     * @param name name, must not be null
     * @param type type, must not be null
     * @param value value, must not be null
     */
    public Tag(final String name, final String type, final String value) {
        checkNotNull(name);
        checkNotNull(type);
        checkNotNull(value);

        this.name = name;
        this.type = type;
        this.value = value;
    }


    /**
     * Return the name of this tag.
     *
     * @return the name of this tag
     */
    public String getName() {
        return name;
    }

    /**
     * Return the type for this tag.
     *
     * @return the type for this tag
     */
    public String getType() {
        return type;
    }

    /**
     * Return the value for this tag.
     *
     * @return the value for this tag
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag t = (Tag) o;

        return Objects.equals(name, t.getName())
            && Objects.equals(type, t.getType())
            && Objects.equals(value, t.getValue());
    }

    @Override
    public String toString() {
        return Joiner.on(":").join(name, type, value);
    }


    /**
     * Parse a tag from the specified value.
     *
     * @param value value, must not be null
     * @return a tag parsed from the specified value
     */
    public static Tag valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on(":").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("value must have at least three tokens, was " + tokens.size());
        }
        return new Tag(tokens.get(0), tokens.get(1), tokens.get(2));
    }
}