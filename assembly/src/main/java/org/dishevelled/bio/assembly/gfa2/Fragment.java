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

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;

/**
 * Fragment GFA 2.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Fragment extends Gfa2Record {
    /** Segment identifier for this fragment. */
    private final String segmentId;

    /** External reference for this fragment. */
    private final Reference external;

    /** Segment start position for this fragment. */
    private final Position segmentStart;

    /** Segment end position for this fragment. */
    private final Position segmentEnd;

    /** Fragment start position for this fragment. */
    private final Position fragmentStart;

    /** Fragment end position for this fragment. */
    private final Position fragmentEnd;

    /** Optional alignment for this fragment. */
    private final Alignment alignment;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new fragment GFA 2.0 record.
     *
     * @param segmentId segment identifier, must not be null
     * @param external external reference, must not be null
     * @param segmentStart segment start position, must not be null
     * @param segmentEnd segment end position, must not be null
     * @param fragmentStart fragment start position, must not be null
     * @param fragmentEnd fragment end position, must not be null
     * @param alignment alignment, if any
     * @param annotations annotations, must not be null
     */
    public Fragment(final String segmentId,
                    final Reference external,
                    final Position segmentStart,
                    final Position segmentEnd,
                    final Position fragmentStart,
                    final Position fragmentEnd,
                    @Nullable final Alignment alignment,
                    final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(segmentId);
        checkNotNull(external);
        checkNotNull(segmentStart);
        checkNotNull(segmentEnd);
        checkNotNull(fragmentStart);
        checkNotNull(fragmentEnd);

        this.segmentId = segmentId;
        this.external = external;
        this.segmentStart = segmentStart;
        this.segmentEnd = segmentEnd;
        this.fragmentStart = fragmentStart;
        this.fragmentEnd = fragmentEnd;
        this.alignment = alignment;

        hashCode = Objects.hash(this.segmentId, this.external, this.segmentStart,
                                this.segmentEnd, this.fragmentStart, this.fragmentEnd,
                                this.alignment, getAnnotations());
    }


    /**
     * Return the segment identifier for this fragment.
     *
     * @return the segment identifier for this fragment
     */
    public String getSegmentId() {
        return segmentId;
    }

    /**
     * Return the external reference for this fragment.
     *
     * @return the external reference for this fragment
     */
    public Reference getExternal() {
        return external;
    }

    /**
     * Return the segment start position for this fragment.
     *
     * @return the segment start position for this fragment
     */
    public Position getSegmentStart() {
        return segmentStart;
    }

    /**
     * Return the segment end position for this fragment.
     *
     * @return the segment end position for this fragment
     */
    public Position getSegmentEnd() {
        return segmentEnd;
    }

    /**
     * Return the fragment start position for this fragment.
     *
     * @return the fragment start position for this fragment
     */
    public Position getFragmentStart() {
        return fragmentStart;
    }

    /**
     * Return the fragment end position for this fragment.
     *
     * @return the fragment end position for this fragment
     */
    public Position getFragmentEnd() {
        return fragmentEnd;
    }

    /**
     * Return true if this fragment has an alignment.
     *
     * @since 1.3.2
     * @return true if this fragment has an alignment
     */
    public boolean hasAlignment() {
        return alignment != null;
    }

    /**
     * Return the alignment for this fragment, if any.
     *
     * @return the alignment for this fragment, if any
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Return an optional wrapping the alignment for this fragment.
     *
     * @return an optional wrapping the alignment for this fragment
     */
    public Optional<Alignment> getAlignmentOpt() {
        return Optional.ofNullable(alignment);
    }


    // optional fields

    /**
     * Return true if the annotations for this fragment contain
     * the reserved key <code>TS</code>.
     *
     * @since 1.3.2
     * @return true if the annotations for this fragment contain
     *    the reserved key <code>TS</code>
     */
    public boolean containsTs() {
        return containsAnnotationKey("TS");
    }

    /**
     * Return the Type=i value for the reserved key <code>TS</code>
     * as an integer.
     *
     * @since 1.3.2
     * @return the Type=i value for the reserved key <code>TS</code>
     *    as an integer
     */
    public int getTs() {
        return getAnnotationInteger("TS");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>TS</code>
     * as an integer.
     *
     * @since 1.3.2
     * @return an optional Type=i value for the reserved key <code>TS</code>
     *   as an integer
     */
    public Optional<Integer> getTsOpt() {
        return getAnnotationIntegerOpt("TS");
    }

    /**
     * Return true if the annotations for this fragment contain
     * the reserved key <code>TS</code>, for trace spacing.
     *
     * @since 1.3.2
     * @return true if the annotations for this fragment contain
     *    the reserved key <code>TS</code>, for trace spacing
     */
    public boolean containsTraceSpacing() {
        return containsTs();
    }

    /**
     * Return the trace spacing for this fragment (Type=i value for
     * the reserved key <code>TS</code> as an integer).
     *
     * @since 1.3.2
     * @return the trace spacing for this fragment (Type=i value for
     *    the reserved key <code>TS</code> as an integer)
     */
    public int getTraceSpacing() {
        return getTs();
    }

    /**
     * Return an optional wrapping the trace spacing for this fragment
     * (Type=i value for the reserved key <code>TS</code> as an integer).
     *
     * @since 1.3.2
     * @return an optional wrapping the trace spacing for this fragment
     *    (Type=i value for the reserved key <code>TS</code> as an integer)
     */
    public Optional<Integer> getTraceSpacingOpt() {
        return getTsOpt();
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
        if (!(o instanceof Fragment)) {
            return false;
        }
        Fragment f = (Fragment) o;

        return Objects.equals(segmentId, f.getSegmentId())
            && Objects.equals(external, f.getExternal())
            && Objects.equals(segmentStart, f.getSegmentStart())
            && Objects.equals(segmentEnd, f.getSegmentEnd())
            && Objects.equals(fragmentStart, f.getFragmentStart())
            && Objects.equals(fragmentEnd, f.getFragmentEnd())
            && Objects.equals(alignment, f.getAlignment())
            && Objects.equals(getAnnotations(), f.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "F", segmentId, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment == null ? "*" : alignment);
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a fragment GFA 2.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a fragment GFA 2.0 record parsed from the specified value
     */
    public static Fragment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("F"), "fragment value must start with F");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 8) {
            throw new IllegalArgumentException("fragment value must have at least eight tokens, was " + tokens.size());
        }
        String segmentId = tokens.get(1);
        Reference external = Reference.valueOf(tokens.get(2));
        Position segmentStart = Position.valueOf(tokens.get(3));
        Position segmentEnd = Position.valueOf(tokens.get(4));
        Position fragmentStart = Position.valueOf(tokens.get(5));
        Position fragmentEnd = Position.valueOf(tokens.get(6));
        Alignment alignment = Alignment.valueOf(tokens.get(7));

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 8; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(token);
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Fragment(segmentId, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment, annotations.build());
    }
}
