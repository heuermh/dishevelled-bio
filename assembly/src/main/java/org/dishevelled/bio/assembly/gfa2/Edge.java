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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.assembly.gfa.Reference;
import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Edge GFA 2.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Edge extends Gfa2Record {
    /** Optional identifier for this edge. */
    private final String id;

    /** Source reference for this edge. */
    private final Reference source;

    /** Target reference for this edge. */
    private final Reference target;

    /** Source start position for this edge. */
    private final Position sourceStart;

    /** Source end position for this edge. */
    private final Position sourceEnd;

    /** Target start position for this edge. */
    private final Position targetStart;

    /** Target end position for this edge. */
    private final Position targetEnd;

    /** Optional alignment for this edge. */
    private final Alignment alignment;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new edge GFA 2.0 record.
     *
     * @param id identifier, if any
     * @param source source reference, must not be null
     * @param target target reference, must not be null
     * @param sourceStart source start position, must not be null
     * @param sourceEnd source end position, must not be null
     * @param targetStart target start position, must not be null
     * @param targetEnd target end position, must not be null
     * @param alignment alignment, if any
     * @param tags tags, must not be null
     */
    public Edge(@Nullable final String id,
                final Reference source,
                final Reference target,
                final Position sourceStart,
                final Position sourceEnd,
                final Position targetStart,
                final Position targetEnd,
                @Nullable final Alignment alignment,
                final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(source);
        checkNotNull(target);
        checkNotNull(sourceStart);
        checkNotNull(sourceEnd);
        checkNotNull(targetStart);
        checkNotNull(targetEnd);

        this.id = id;
        this.source = source;
        this.target = target;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.targetStart = targetStart;
        this.targetEnd = targetEnd;
        this.alignment = alignment;

        hashCode = Objects.hash(this.id, this.source, this.target, this.sourceStart,
                                this.sourceEnd, this.targetStart, this.targetEnd, this.alignment,
                                getTags());
    }


    /**
     * Return the identifier for this edge, if any.
     *
     * @return the identifier for this edge, if any
     */
    public String getId() {
        return id;
    }

    /**
     * Return the source reference for this edge.
     *
     * @return the source reference for this edge
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Return the target reference for this edge.
     *
     * @return the target reference for this edge
     */
    public Reference getTarget() {
        return target;
    }

    /**
     * Return the source start position for this edge.
     *
     * @return the source start position for this edge
     */
    public Position getSourceStart() {
        return sourceStart;
    }

    /**
     * Return the source end position for this edge.
     *
     * @return the source end position for this edge
     */
    public Position getSourceEnd() {
        return sourceEnd;
    }

    /**
     * Return the target start position for this edge.
     *
     * @return the target start position for this edge
     */
    public Position getTargetStart() {
        return targetStart;
    }

    /**
     * Return the target end position for this edge.
     *
     * @return the target end position for this edge
     */
    public Position getTargetEnd() {
        return targetEnd;
    }

    /**
     * Return the alignment for this edge, if any.
     *
     * @return the alignment for this edge, if any
     */
    public Alignment getAlignment() {
        return alignment;
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
        if (!(o instanceof Edge)) {
            return false;
        }
        Edge e = (Edge) o;

        return Objects.equals(id, e.getId())
            && Objects.equals(source, e.getSource())
            && Objects.equals(target, e.getTarget())
            && Objects.equals(sourceStart, e.getSourceStart())
            && Objects.equals(sourceEnd, e.getSourceEnd())
            && Objects.equals(targetStart, e.getTargetStart())
            && Objects.equals(targetEnd, e.getTargetEnd())
            && Objects.equals(alignment, e.getAlignment())
            && Objects.equals(getTags(), e.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "E", id == null ? "*" : id, source, target, sourceStart, sourceEnd, targetStart, targetEnd, alignment == null ? "*" : alignment);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }


    /**
     * Parse an edge GFA 2.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return an edge GFA 2.0 record parsed from the specified value
     */
    public static Edge valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("E"), "value must start with E");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 9) {
            throw new IllegalArgumentException("value must have at least nine tokens, was " + tokens.size());
        }
        String id = "*".equals(tokens.get(1)) ? null : tokens.get(1);
        Reference source = Reference.valueOf(tokens.get(2));
        Reference target = Reference.valueOf(tokens.get(3));
        Position sourceStart = Position.valueOf(tokens.get(4));
        Position sourceEnd = Position.valueOf(tokens.get(5));
        Position targetStart = Position.valueOf(tokens.get(6));
        Position targetEnd = Position.valueOf(tokens.get(7));
        Alignment alignment = Alignment.valueOf(tokens.get(8));

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 9; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getName(), tag);
        }

        return new Edge(id, source, target, sourceStart, sourceEnd, targetStart, targetEnd, alignment, tags.build());
    }
}
