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
package org.dishevelled.bio.assembly.gfa1;

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

import org.dishevelled.bio.annotation.Annotation;

/**
 * Traversal.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Traversal extends Gfa1Record {
    /** Path name for this traversal. */
    private final String pathName;

    /** Ordinal for this traversal. */
    private final int ordinal;

    /** Source reference for this traversal. */
    private final Reference source;

    /** Target reference for this traversal. */
    private final Reference target;

    /** Overlap in cigar format for this traversal. */
    private final String overlap;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new traversal GFA 1.0 record.
     *
     * @param pathName path name, must not be null
     * @param ordinal ordinal, must be at least zero
     * @param source source reference, must not be null
     * @param target target reference, must not be null
     * @param overlap overlap, if any
     * @param annotations annotations, must not be null
     */
    public Traversal(final String pathName,
                     final int ordinal,
                     final Reference source,
                     final Reference target,
                     @Nullable final String overlap,
                     final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(pathName);
        checkNotNull(source);
        checkNotNull(target);
        checkArgument(ordinal >= 0, "ordinal must be at least zero");

        this.pathName = pathName;
        this.ordinal = ordinal;
        this.source = source;
        this.target = target;
        this.overlap = overlap;

        hashCode = Objects.hash(this.pathName, this.ordinal, this.source, this.target, this.overlap, getAnnotations());
    }


    /**
     * Return the path name for this traversal.
     *
     * @return the path name for ths traversal
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Return the ordinal for this traversal.
     *
     * @return the ordinal for this traversal
     */
    public int getOrdinal() {
        return ordinal;
    }

    /**
     * Return the source reference for this traversal.
     *
     * @return the source reference for this traversal
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Return the target reference for this traversal.
     *
     * @return the target reference for this traversal
     */
    public Reference getTarget() {
        return target;
    }

    /**
     * Return true if this traversal has an overlap in cigar format.
     *
     * @return true if this traversal has an overlap in cigar format.
     */
    public boolean hasOverlap() {
        return overlap != null;
    }

    /**
     * Return the overlap in cigar format for this traversal, if any.
     *
     * @return the overlap in cigar format for this traversal, if any
     */
    public String getOverlap() {
        return overlap;
    }

    /**
     * Return an optional wrapping the overlap for this traversal.
     *
     * @return an optional wrapping the overlap for this traversal
     */
    public Optional<String> getOverlapOpt() {
        return Optional.ofNullable(overlap);
    }


    // optional fields

    /**
     * Return true if the annotations for this traversal contain
     * the reserved key <code>ID</code>.
     *
     * @return true if the annotations for this traversal contain
     *    the reserved key <code>ID</code>
     */
    public boolean containsId() {
        return containsAnnotationKey("ID");
    }

    /**
     * Return the Type=Z value for the reserved key <code>ID</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>ID</code>
     *    as a string
     */
    public String getId() {
        return getAnnotationString("ID");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>ID</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>ID</code>
     *   as a string
     */
    public Optional<String> getIdOpt() {
        return getAnnotationStringOpt("ID");
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
        if (!(o instanceof Traversal)) {
            return false;
        }
        Traversal t = (Traversal) o;

        return Objects.equals(pathName, t.getPathName())
            && Objects.equals(ordinal, t.getOrdinal())
            && Objects.equals(source, t.getSource())
            && Objects.equals(target, t.getTarget())
            && Objects.equals(overlap, t.getOverlap())
            && Objects.equals(getAnnotations(), t.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "t", pathName, ordinal, source.splitToString(), target.splitToString(), getOverlapOpt().orElse("*"));
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }

    /**
     * Parse a traversal GFA 1.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a traversal GFA 1.0 record parsed from the specified value
     */
    public static Traversal valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("t"), "traversal value must start with t");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 8) {
            throw new IllegalArgumentException("traversal value must have at least eight tokens, was " + tokens.size());
        }
        String name = tokens.get(1);
        int ordinal = Integer.parseInt(tokens.get(2));
        Reference source = Reference.splitValueOf(tokens.get(3), tokens.get(4));
        Reference target = Reference.splitValueOf(tokens.get(5), tokens.get(6));
        String overlap = "*".equals(tokens.get(7)) ? null : tokens.get(7);

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 8; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(tokens.get(i));
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Traversal(name, ordinal, source, target, overlap, annotations.build());
    }
}
