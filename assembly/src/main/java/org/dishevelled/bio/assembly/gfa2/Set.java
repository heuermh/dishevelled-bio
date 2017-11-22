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
import com.google.common.collect.ImmutableSet;

/**
 * Set.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Set extends Gfa2Record {
    private final String id;
    private final java.util.Set<String> ids;

    public Set(@Nullable final String id,
               final java.util.Set<String> ids,
               final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(ids);

        this.id = id;
        this.ids = ImmutableSet.copyOf(ids);
    }

    public String getId() {
        return id;
    }

    public java.util.Set getIds() {
        return ids;
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "U", id == null ? "*" : id, Joiner.on(" ").join(ids));
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }

    public static Set valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("U"), "value must start with U");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("value must have at least three tokens, was " + tokens.size());
        }
        String id = "*".equals(tokens.get(1)) ? null : tokens.get(1);
        java.util.Set<String> ids = ImmutableSet.copyOf(Splitter.on(" ").split(tokens.get(2)));

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 8; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getTag(), tag);
        }

        return new Set(id, ids, tags.build());
    }
}
