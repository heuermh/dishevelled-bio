/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.dishevelled.bio.annotation.Annotation;

/**
 * Set GFA 2.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Set extends Gfa2Record {
    /** Optional identifier for this set. */
    private final String id;

    /** Unordered set of identifiers for this set. */
    private final java.util.Set<String> ids;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new set GFA 2.0 record.
     *
     * @param id identifier, if any
     * @param ids unordered set of identifiers, must not be null
     * @param annotations annotations, must not be null
     */
    public Set(@Nullable final String id,
               final java.util.Set<String> ids,
               final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(ids);

        this.id = id;
        this.ids = ImmutableSet.copyOf(ids);

        hashCode = Objects.hash(this.id, this.ids, getAnnotations());
    }


    /**
     * Return true if this set has an identifier.
     *
     * @since 1.3.2
     * @return true if this set has an identifier
     */
    public boolean hasId() {
        return id != null;
    }

    /**
     * Return the identifier for this set, if any.
     *
     * @return the identifier for this set, if any
     */
    public String getId() {
        return id;
    }

    /**
     * Return an optional wrapping the identifier for this set.
     *
     * @return an optional wrapping the identifier for this set
     */
    public Optional<String> getIdOpt() {
        return Optional.ofNullable(id);
    }

    /**
     * Return an immutable unordered set of identifiers for this set.
     *
     * @return an immutable unordered set of identifiers for this set
     */
    public java.util.Set<String> getIds() {
        return ids;
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
        if (!(o instanceof Set)) {
            return false;
        }
        Set s = (Set) o;

        return Objects.equals(id, s.getId())
            && Objects.equals(ids, s.getIds())
            && Objects.equals(getAnnotations(), s.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "U", id == null ? "*" : id, Joiner.on(" ").join(ids));
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a set GFA 2.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a set GFA 2.0 record parsed from the specified value
     */
    public static Set valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("U"), "set value must start with U");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("set value must have at least three tokens, was " + tokens.size());
        }
        String id = "*".equals(tokens.get(1)) ? null : tokens.get(1);
        java.util.Set<String> ids = ImmutableSet.copyOf(Splitter.on(" ").split(tokens.get(2)));

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 3; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(token);
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Set(id, ids, annotations.build());
    }
}
