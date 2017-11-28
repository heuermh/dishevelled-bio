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
package org.dishevelled.bio.assembly.gfa1;

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
 * Link GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Link extends Gfa1Record {
    /** Source reference for this link. */
    private final Reference source;

    /** Target reference for this link. */
    private final Reference target;

    /** Overlap for this link. */
    private final String overlap;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new link GFA 1.0 record.
     *
     * @param source source reference, must not be null
     * @param target target reference, must not be null
     * @param overlap overlap, if any
     * @param tags tags, must not be null
     */
    public Link(final Reference source,
                final Reference target,
                @Nullable final String overlap,
                final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(source);
        checkNotNull(target);

        this.source = source;
        this.target = target;
        this.overlap = overlap;

        hashCode = Objects.hash(this.source, this.target, this.overlap, getTags());
    }


    /**
     * Return the source reference for this link.
     *
     * @return the source reference for this link
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Return the target reference for this link.
     *
     * @return the target reference for this link
     */
    public Reference getTarget() {
        return target;
    }

    /**
     * Return the overlap in cigar format for this link, if any.
     *
     * @return the overlap in cigar format for this link, if any
     */
    public String getOverlap() {
        return overlap;
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
        if (!(o instanceof Link)) {
            return false;
        }
        Link l = (Link) o;

        return Objects.equals(source, l.getSource())
            && Objects.equals(target, l.getTarget())
            && Objects.equals(overlap, l.getOverlap())
            && Objects.equals(getTags(), l.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "L", source.splitToString(), target.splitToString(), overlap == null ? "*" : overlap);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }


    /**
     * Parse a link GFA 1.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a link GFA 1.0 record parsed from the specified value
     */
    public static Link valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("L"), "value must start with L");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 6) {
            throw new IllegalArgumentException("value must have at least six tokens, was " + tokens.size());
        }
        Reference source = Reference.splitValueOf(tokens.get(1), tokens.get(2));
        Reference target = Reference.splitValueOf(tokens.get(3), tokens.get(4));
        String overlap = "*".equals(tokens.get(5)) ? null : tokens.get(5);

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 6; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getName(), tag);
        }

        return new Link(source, target, overlap, tags.build());
    }
}
