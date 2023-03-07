/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
 * Containment GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Containment extends Gfa1Record {
    /** Container reference for this containment. */
    private final Reference container;

    /** Contained reference for this containment. */
    private final Reference contained;

    /** Position for this containment. */
    private final int position;

    /** Overlap in cigar format for this containment. */
    private final String overlap;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new containment GFA 1.0 record.
     *
     * @param container container reference, must not be null
     * @param contained contained reference, must not be null
     * @param position position, must be at least zero
     * @param overlap overlap in cigar format, if any
     * @param annotations annotations, must not be null
     */
    public Containment(final Reference container,
                       final Reference contained,
                       final int position,
                       @Nullable final String overlap,
                       final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(container);
        checkNotNull(contained);
        checkArgument(position >= 0, "position must be at least zero, was " + position);

        this.container = container;
        this.contained = contained;
        this.position = position;
        this.overlap = overlap;

        hashCode = Objects.hash(this.container, this.contained, this.position, this.overlap, getAnnotations());
    }


    /**
     * Return the container reference for this containment.
     *
     * @return the container reference for this containment
     */
    public Reference getContainer() {
        return container;
    }

    /**
     * Return the contained reference for this containment.
     *
     * @return the contained reference for this containment
     */
    public Reference getContained() {
        return contained;
    }

    /**
     * Return the position for this containment (0-based coordinate system). The position is
     * the leftmost position of the contained segment in the container segment in its forward
     * orientation (i.e. before this is oriented according to the container reference orientation).
     *
     * @return the position for this containment (0-based coordinate system)
     */
    public int getPosition() {
        return position;
    }

    /**
     * Return true if this containment has an overlap in cigar format.
     *
     * @return true if this containment has an overlap in cigar format.
     */
    public boolean hasOverlap() {
        return overlap != null;
    }

    /**
     * Return the overlap in cigar format for this containment, if any.
     *
     * @return the overlap in cigar format for this containment, if any
     */
    public String getOverlap() {
        return overlap;
    }

    /**
     * Return an optional wrapping the overlap in cigar format for this containment.
     *
     * @return an optional wrapping the overlap in cigar format for this containment
     */
    public Optional<String> getOverlapOpt() {
        return Optional.ofNullable(overlap);
    }


    // optional fields

    /**
     * Return true if the annotations for this containment contain
     * the reserved key <code>RC</code>.
     *
     * @return true if the annotations for this containment contain
     *    the reserved key <code>RC</code>
     */
    public boolean containsRc() {
        return containsAnnotationKey("RC");
    }

    /**
     * Return the Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>RC</code>
     *    as an integer
     */
    public int getRc() {
        return getAnnotationInteger("RC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>RC</code>
     *   as an integer
     */
    public Optional<Integer> getRcOpt() {
        return getAnnotationIntegerOpt("RC");
    }

    /**
     * Return true if the annotations for this containment contain
     * the reserved key <code>RC</code>, for read count.
     *
     * @return true if the annotations for this containment contain
     *    the reserved key <code>RC</code>, for read count
     */
    public boolean containsReadCount() {
        return containsRc();
    }

    /**
     * Return the read count for this containment (Type=i value for the
     * reserved key <code>RC</code> as an integer).
     *
     * @return the read count for this containment (Type=i value for the
     *    reserved key <code>RC</code> as an integer)
     */
    public int getReadCount() {
        return getRc();
    }

    /**
     * Return an optional wrapping the read count for this containment
     * (Type=i value for the reserved key <code>RC</code> as an integer).
     *
     * @return an optional wrapping the read count for this containment
     *    (Type=i value for the reserved key <code>RC</code> as an integer)
     */
    public Optional<Integer> getReadCountOpt() {
        return getRcOpt();
    }

    //

    /**
     * Return true if the annotations for this containment contain
     * the reserved key <code>NM</code>.
     *
     * @return true if the annotations for this containment contain
     *    the reserved key <code>NM</code>
     */
    public boolean containsNm() {
        return containsAnnotationKey("NM");
    }

    /**
     * Return the Type=i value for the reserved key <code>NM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>NM</code>
     *    as an integer
     */
    public int getNm() {
        return getAnnotationInteger("NM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>NM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>NM</code>
     *   as an integer
     */
    public Optional<Integer> getNmOpt() {
        return getAnnotationIntegerOpt("NM");
    }

    /**
     * Return true if the annotations for this containment contain
     * the reserved key <code>NM</code>, for mismatch count.
     *
     * @return true if the annotations for this containment contain
     *    the reserved key <code>NM</code>, for mismatch count
     */
    public boolean containsMismatchCount() {
        return containsNm();
    }

    /**
     * Return the mismatch count for this containment (Type=i value
     * for the reserved key <code>NM</code> as an integer).
     *
     * @return the mismatch count for this containment (Type=i value
     *    for the reserved key <code>NM</code> as an integer)
     */
    public int getMismatchCount() {
        return getNm();
    }

    /**
     * Return an optional wrapping the mismatch count for this containment
     * (Type=i value for the reserved key <code>NM</code> as an integer).
     *
     * @return an optional wrapping the mismatch count for this containment
     *    (Type=i value for the reserved key <code>NM</code> as an integer)
     */
    public Optional<Integer> getMismatchCountOpt() {
        return getNmOpt();
    }

    //

    /**
     * Return true if the annotations for this containment contain
     * the reserved key <code>ID</code>.
     *
     * @return true if the annotations for this containment contain
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
        if (!(o instanceof Containment)) {
            return false;
        }
        Containment c = (Containment) o;

        return Objects.equals(container, c.getContainer())
            && Objects.equals(contained, c.getContained())
            && Objects.equals(position, c.getPosition())
            && Objects.equals(overlap, c.getOverlap())
            && Objects.equals(getAnnotations(), c.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "C", container.splitToString(), contained.splitToString(), position, overlap == null ? "*" : overlap);
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a containment GFA 1.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a containment GFA 1.0 record parsed from the specified value
     */
    public static Containment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("C"), "containment value must start with C");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 7) {
            throw new IllegalArgumentException("containment value must have at least seven tokens, was " + tokens.size());
        }
        Reference container = Reference.splitValueOf(tokens.get(1), tokens.get(2));
        Reference contained = Reference.splitValueOf(tokens.get(3), tokens.get(4));
        int position = Integer.parseInt(tokens.get(5));
        String overlap = "*".equals(tokens.get(6)) ? null : tokens.get(6);

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 7; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(token);
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Containment(container, contained, position, overlap, annotations.build());
    }
}
