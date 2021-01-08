/*

    dsh-bio-annotation  Support for SAM-style annotation fields.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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
package org.dishevelled.bio.annotation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Annotation.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class Annotation {
    /** Name of this annotation. */
    private final String name;

    /** Type for this annotation. */
    private final String type;

    /** Array type for this annotation, if any. */
    private final String arrayType;

    /** Value for this annotation. */
    private final String value;

    /** Cached hash code. */
    private final int hashCode;

    /** Valid types. */
    private static final Pattern VALID_TYPES = Pattern.compile("[AifZHB]");

    /** Valid array types. */
    private static final Pattern VALID_ARRAY_TYPES = Pattern.compile("[cCsSiIf]");


    /**
     * Create a new annotation.
     *
     * @param name name, must not be null
     * @param type type, must not be null
     * @param arrayType array type, if any
     * @param value value, must not be null
     */
    public Annotation(final String name, final String type, final String value) {
        this(name, type, null, value);
    }

    /**
     * Create a new annotation.
     *
     * @since 2.0
     * @param name name, must not be null
     * @param type type, must not be null
     * @param arrayType array type, if any
     * @param value value, must not be null
     */
    public Annotation(final String name, final String type, @Nullable final String arrayType, final String value) {
        checkNotNull(name);
        checkNotNull(type);
        checkNotNull(value);

        // validate type
        checkArgument(VALID_TYPES.matcher(type).matches(), "type must match [AifZHB]");

        // validate arrayType
        if ("B".equals(type)) {
            checkNotNull(arrayType, "if type is B, array type must be specified");
            checkArgument(VALID_ARRAY_TYPES.matcher(arrayType).matches(), "if type is B, array type must match [cCsSiIf]");
        }

        this.name = name;
        this.type = type;
        this.arrayType = arrayType;
        this.value = value;
        this.hashCode = Objects.hash(name, type, arrayType, value);
    }


    /**
     * Return the name of this annotation.
     *
     * @return the name of this annotation
     */
    public String getName() {
        return name;
    }

    /**
     * Return the type for this annotation.
     *
     * @return the type for this annotation
     */
    public String getType() {
        return type;
    }

    /**
     * Return the array type for this annotation, if any.
     *
     * @since 2.0
     * @return the array type for this annotation
     */
    public String getArrayType() {
        return arrayType;
    }

    /**
     * Return an optional wrapping the array type for this annotation.
     *
     * @since 2.0
     * @return an optional wrapping the array type for this annotation
     */
    public Optional<String> getArrayTypeOpt() {
        return Optional.ofNullable(arrayType);
    }

    /**
     * Return the value for this annotation.
     *
     * @return the value for this annotation
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Annotation)) {
            return false;
        }
        Annotation t = (Annotation) o;

        return Objects.equals(name, t.getName())
            && Objects.equals(type, t.getType())
            && Objects.equals(arrayType, t.getArrayType())
            && Objects.equals(value, t.getValue());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on(":");
        return arrayType == null ? joiner.join(name, type, value) : joiner.join(name, type, arrayType + "," + value);
    }


    /**
     * Parse a annotation from the specified value.
     *
     * @param value value, must not be null
     * @return a annotation parsed from the specified value
     */
    public static Annotation valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on(":").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("annotation value '" + value + "' must have at least three tokens, was " + tokens.size());
        }
        String n = tokens.get(0);
        String t = tokens.get(1);
        String v = tokens.get(2);

        if ("B".equals(t)) {
            if (v.length() == 0) {
                throw new IllegalArgumentException("annotation value '" + value + "' missing array type in value for type B");
            }
            String arrayType = v.substring(0, 1);
            String remainder = v.length() > 2 ? v.substring(2) : "";
            return new Annotation(n, t, arrayType, remainder);
        }
        return new Annotation(n, t, null, v);
    }
}
