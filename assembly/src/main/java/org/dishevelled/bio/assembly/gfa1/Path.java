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

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Path GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Path extends Gfa1Record {
    private final String name;
    private final List<Reference> segments;
    private final List<String> overlaps;
    private final int hashCode;

    public Path(final String name,
                final List<Reference> segments,
                @Nullable final List<String> overlaps,
                final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(name);
        checkNotNull(segments);

        this.name = name;
        this.segments = ImmutableList.copyOf(segments);
        this.overlaps = overlaps == null ? null : ImmutableList.copyOf(overlaps);

        hashCode = Objects.hash(this.name, this.segments, this.overlaps, getTags());
    }

    public String getName() {
        return name;
    }
    public List<Reference> getSegments() {
        return segments;
    }
    public boolean hasOverlaps() {
        return overlaps != null;
    }
    public List<String> getOverlaps() {
        return overlaps;
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
        Path p = (Path) o;

        return Objects.equals(name, p.getName())
            && Objects.equals(segments, p.getSegments())
            && Objects.equals(overlaps, p.getOverlaps())
            && Objects.equals(getTags(), p.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "P", name, Joiner.on(",").join(segments), Joiner.on(",").join(overlaps));
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }

    public static Path valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("P"), "value must start with P");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 4) {
            throw new IllegalArgumentException("value must have at least four tokens, was " + tokens.size());
        }
        String name = tokens.get(1);

        List<Reference> segments = Splitter
            .on(",")
            .splitToList(tokens.get(2))
            .stream()
            .map(v -> Reference.valueOf(v))
            .collect(Collectors.toList());

        List<String> overlaps = "*".equals(tokens.get(3)) ? null : ImmutableList.copyOf(Splitter.on(",").split(tokens.get(3)));

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 4; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getName(), tag);
        }

        return new Path(name, segments, overlaps, tags.build());
    }
}
