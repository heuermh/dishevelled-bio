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
 * Gap GFA 2.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Gap extends Gfa2Record {
    /** Optional identifier for this gap. */
    private final String id;

    /** Source reference for this gap. */
    private final Reference source;

    /** Target reference for this gap. */
    private final Reference target;

    /** Distance for this gap. */
    private final int distance;

    /** Optional variance for this gap. */
    private final Integer variance;


    /**
     * Create a new gap GFA 2.0 record.
     *
     * @param id identifier, if any
     * @param source source reference, must not be null
     * @param target target reference, must not be null
     * @param distance distance, must be at least zero
     * @param variance variance, if any
     * @param tags tags, must not be null
     */
    public Gap(@Nullable final String id,
               final Reference source,
               final Reference target,
               final int distance,
               @Nullable final Integer variance,
               final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(source);
        checkNotNull(target);
        checkArgument(distance >= 0, "distance must be at least zero");

        this.id = id;
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.variance = variance;
    }


    /**
     * Return the identifier for this gap, if any.
     *
     * @return the identifier for this gap, if any
     */
    public String getId() {
        return id;
    }

    /**
     * Return the source reference for this gap.
     *
     * @return the source reference for this gap
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Return the target reference for this gap.
     *
     * @return the target reference for this gap
     */
    public Reference getTarget() {
        return target;
    }

    /**
     * Return the distance for this gap.
     *
     * @return the distance for this gap
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Return the variance for this gap, if any.
     *
     * @return the variance for this gap, if any
     */
    public Integer getVariance() {
        return variance;
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "G", id == null ? "*" : id, source, target, distance, variance == null ? "*" : variance);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }


    /**
     * Parse a gap GFA 2.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a gap GFA 2.0 record parsed from the specified value
     */
    public static Gap valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("G"), "value must start with G");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 6) {
            throw new IllegalArgumentException("value must have at least six tokens, was " + tokens.size());
        }
        String id = "*".equals(tokens.get(1)) ? null : tokens.get(1);
        Reference source = Reference.valueOf(tokens.get(2));
        Reference target = Reference.valueOf(tokens.get(3));
        int distance = Integer.parseInt(tokens.get(4));
        Integer variance = "*".equals(tokens.get(5)) ? null : Integer.parseInt(tokens.get(5));

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 6; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getName(), tag);
        }

        return new Gap(id, source, target, distance, variance, tags.build());
    }
}
