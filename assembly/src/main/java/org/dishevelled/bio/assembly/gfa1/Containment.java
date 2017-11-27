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

/**
 * Containment GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Containment extends Gfa1Record {
    private final Reference container;
    private final Reference contained;
    private final int position;
    private final String overlap;
    private final int hashCode;

    public Containment(final Reference container,
                       final Reference contained,
                       final int position,
                       @Nullable final String overlap,
                       final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(container);
        checkNotNull(contained);
        checkArgument(position >= 0, "position must be at least zero, was " + position);

        this.container = container;
        this.contained = contained;
        this.position = position;
        this.overlap = overlap;

        hashCode = Objects.hash(this.container, this.contained, this.position, this.overlap, getTags());
    }

    public Reference getContainer() {
        return container;
    }

    public Reference getContained() {
        return contained;
    }

    public int getPosition() {
        return position;
    }

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
        if (!(o instanceof Containment)) {
            return false;
        }
        Containment c = (Containment) o;

        return Objects.equals(container, c.getContainer())
            && Objects.equals(contained, c.getContained())
            && Objects.equals(position, c.getPosition())
            && Objects.equals(overlap, c.getOverlap())
            && Objects.equals(getTags(), c.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "C", container.splitToString(), contained.splitToString(), position, overlap == null ? "*" : overlap);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }

    public static Containment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("C"), "value must start with C");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 7) {
            throw new IllegalArgumentException("value must have at least seven tokens, was " + tokens.size());
        }
        Reference container = Reference.splitValueOf(tokens.get(1), tokens.get(2));
        Reference contained = Reference.splitValueOf(tokens.get(3), tokens.get(4));
        int position = Integer.parseInt(tokens.get(5));
        String overlap = "*".equals(tokens.get(6)) ? null : tokens.get(6);

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 7; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getName(), tag);
        }

        return new Containment(container, contained, position, overlap, tags.build());
    }
}
