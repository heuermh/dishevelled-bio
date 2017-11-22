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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

/**
 * Fragment.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Fragment extends Gfa2Record {
    private final String segmentId;
    private final Reference external;
    private final Position segmentStart;
    private final Position segmentEnd;
    private final Position fragmentStart;
    private final Position fragmentEnd;
    private final Alignment alignment;

    public Fragment(final String segmentId,
                    final Reference external,
                    final Position segmentStart,
                    final Position segmentEnd,
                    final Position fragmentStart,
                    final Position fragmentEnd,
                    @Nullable final Alignment alignment,
                    final Map<String, Tag> tags) {

        super(tags);
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
    }

    public String getSegmentId() {
        return segmentId;
    }

    public Reference getExternal() {
        return external;
    }

    public Position getSegmentStart() {
        return segmentStart;
    }

    public Position getSegmentEnd() {
        return segmentEnd;
    }

    public Position getFragmentStart() {
        return fragmentStart;
    }

    public Position getFragmentEnd() {
        return fragmentEnd;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "F", segmentId, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment == null ? "*" : alignment);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }

    public static Fragment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("F"), "value must start with F");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 8) {
            throw new IllegalArgumentException("value must have at least eight tokens, was " + tokens.size());
        }
        String segmentId = tokens.get(1);
        Reference external = Reference.valueOf(tokens.get(2));
        Position segmentStart = Position.valueOf(tokens.get(3));
        Position segmentEnd = Position.valueOf(tokens.get(4));
        Position fragmentStart = Position.valueOf(tokens.get(5));
        Position fragmentEnd = Position.valueOf(tokens.get(6));
        Alignment alignment = Alignment.valueOf(tokens.get(7));

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 8; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getTag(), tag);
        }

        return new Fragment(segmentId, external, segmentStart, segmentEnd, fragmentStart, fragmentEnd, alignment, tags.build());
    }
}
