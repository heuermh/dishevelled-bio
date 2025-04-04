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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
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
 * Path GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Path extends Gfa1Record {
    /** Name for this path. */
    private final String name;

    /** List of segment references for this path. */
    private final List<Reference> segments;

    /** List of overlaps for this path. */
    private final List<String> overlaps;

    /** Empty segments. */
    private static final List<Reference> EMPTY_SEGMENTS = Collections.emptyList();

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new path GFA 1.0 record.
     *
     * @param name name, must not be null
     * @param segments list of segment references, must not be null
     * @param overlaps list of overlaps, if any
     * @param annotations annotations, must not be null
     */
    public Path(final String name,
                final List<Reference> segments,
                @Nullable final List<String> overlaps,
                final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(name);
        checkNotNull(segments);
        if (overlaps != null) {
            checkArgument(overlaps.size() == (segments.size() - 1), "if specified, overlaps must have one fewer values than segments");
        }

        this.name = name;
        this.segments = ImmutableList.copyOf(segments);
        this.overlaps = overlaps == null ? null : ImmutableList.copyOf(overlaps);

        hashCode = Objects.hash(this.name, this.segments, this.overlaps, getAnnotations());
    }


    /**
     * Return the name for this path.
     *
     * @return the name for this path
     */
    public String getName() {
        return name;
    }

    /**
     * Return an immutable list of segment references for this path.
     *
     * @return an immutable list of segment references for this path
     */
    public List<Reference> getSegments() {
        return segments;
    }

    /**
     * Return true if this path has any overlaps.
     *
     * @return true if this path has any overlaps
     */
    public boolean hasOverlaps() {
        return overlaps != null;
    }

    /**
     * Return an immutable list of overlaps in cigar format for this path,
     * or null if none exist.
     *
     * @return an immutable list of overlaps in cigar format for this path,
     *    or null if none exist
     */
    public List<String> getOverlaps() {
        return overlaps;
    }

    /**
     * Return an optional wrapping the list of overlaps in cigar format for this path.
     *
     * @return an optional wrapping the list of overlap in cigar format for this path
     */
    public Optional<List<String>> getOverlapsOpt() {
        return Optional.ofNullable(overlaps);
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

        return Objects.equals(name, p.getName())
            && Objects.equals(segments, p.getSegments())
            && Objects.equals(overlaps, p.getOverlaps())
            && Objects.equals(getAnnotations(), p.getAnnotations());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("P");
        sb.append("\t");
        sb.append(name);
        sb.append("\t");

        // append segments, if any
        if (segments.isEmpty()) {
            sb.append("*");
        }
        else {
            sb.append(Joiner.on(",").join(segments));;
        }
        sb.append("\t");

        // append overlaps, if any
        if (overlaps == null || overlaps.isEmpty()) {
            sb.append("*");
        }
        else {
            sb.append(Joiner.on(",").join(overlaps));
        }

        // append annotations, if any
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            sb.append(Joiner.on("\t").join(getAnnotations().values()));
        }
        return sb.toString();
    }


    /**
     * Parse a path GFA 1.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a path GFA 1.0 record parsed from the specified value
     */
    public static Path valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("P"), "path value must start with P");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 4) {
            throw new IllegalArgumentException("path value must have at least four tokens, was " + tokens.size());
        }
        String name = tokens.get(1);

        List<Reference> segments = "*".equals(tokens.get(2)) ? EMPTY_SEGMENTS : Splitter
            .on(",")
            .splitToList(tokens.get(2))
            .stream()
            .map(Reference::valueOf)
            .collect(Collectors.toList());

        List<String> overlaps = "*".equals(tokens.get(3)) ? null : ImmutableList.copyOf(Splitter.on(",").split(tokens.get(3)));

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 4; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(token);
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Path(name, segments, overlaps, annotations.build());
    }
}
