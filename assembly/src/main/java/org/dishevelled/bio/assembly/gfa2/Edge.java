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
 * Edge.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Edge extends Gfa2Record {
    private final String id;
    private final Reference source;
    private final Reference target;
    private final Position sourceStart;
    private final Position sourceEnd;
    private final Position targetStart;
    private final Position targetEnd;
    private final Alignment alignment;

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
    }

    public String getId() {
        return id;
    }

    public Reference getSource() {
        return source;
    }

    public Reference getTarget() {
        return target;
    }

    public Position getSourceStart() {
        return sourceStart;
    }

    public Position getSourceEnd() {
        return sourceEnd;
    }

    public Position getTargetStart() {
        return targetStart;
    }

    public Position getTargetEnd() {
        return targetEnd;
    }

    public Alignment getAlignment() {
        return alignment;
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
            tags.put(tag.getTag(), tag);
        }

        return new Edge(id, source, target, sourceStart, sourceEnd, targetStart, targetEnd, alignment, tags.build());
    }
}
