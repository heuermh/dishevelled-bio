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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;

/**
 * Path GFA 2.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Path extends Gfa2Record {
    /** Optional identifier for this path. */
    private final String id;

    /** List of references for this path. */
    private final List<Reference> references;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new path GFA 2.0 record.
     *
     * @param id identifier, if any
     * @param references list of reference, must not be null
     * @param annotations annotations, must not be null
     */
    public Path(@Nullable final String id,
                final List<Reference> references,
                final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(references);
        checkNotNull(annotations);

        this.id = id;
        this.references = ImmutableList.copyOf(references);

        hashCode = Objects.hash(this.id, this.references, getAnnotations());
    }


    /**
     * Return true if this path has an identifier.
     *
     * @since 1.3.2
     * @return true if this path has an identifier
     */
    public boolean hasId() {
        return id != null;
    }

    /**
     * Return the identifier for this path, if any.
     *
     * @return the identifier for this path, if any
     */
    public String getId() {
        return id;
    }

    /**
     * Return an optional wrapping the identifier for this path.
     *
     * @return an optional wrapping the identifier for this path
     */
    public Optional<String> getIdOpt() {
        return Optional.ofNullable(id);
    }

    /**
     * Return an immutable list of references for this path.
     *
     * @return an immutable list of references for this path
     */
    public List<Reference> getReferences() {
        return references;
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
        if (!(o instanceof Path)) {
            return false;
        }
        Path p = (Path) o;

        return Objects.equals(id, p.getId())
            && Objects.equals(references, p.getReferences())
            && Objects.equals(getAnnotations(), p.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "O", id == null ? "*" : id, Joiner.on(" ").join(references));
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a path GFA 2.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a path GFA 2.0 record parsed from the specified value
     */
    public static Path valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("O"), "path value must start with O");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("path value must have at least three tokens, was " + tokens.size());
        }
        String id = "*".equals(tokens.get(1)) ? null : tokens.get(1);
        List<Reference> references = Splitter
            .on(" ")
            .splitToList(tokens.get(2))
            .stream()
            .map(Reference::valueOf)
            .collect(Collectors.toList());

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 3; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(token);
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Path(id, references, annotations.build());
    }
}
