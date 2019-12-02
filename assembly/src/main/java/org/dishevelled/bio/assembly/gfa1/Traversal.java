/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

import org.dishevelled.bio.assembly.gfa.Reference;
import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Traversal.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Traversal extends Gfa1Record {
    /** Path identifier for this traversal. */
    private final String pathId;

    /** Ordinal for this traversal. */
    private final int ordinal;

    /** Source reference for this traversal. */
    private final Reference source;

    /** Target reference for this traversal. */
    private final Reference target;

    /** Overlap for this traversal. */
    private final String overlap;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new traversal GFA 1.0 record.
     *
     * @param pathId path identifier, must not be null
     * @param ordinal ordinal, must be at least zero
     * @param source source reference, must not be null
     * @param target target reference, must not be null
     * @param overlap overlap, if any
     * @param tags tags, must not be null
     */
    public Traversal(final String pathId,
                     final int ordinal,
                     final Reference source,
                     final Reference target,
                     @Nullable final String overlap,
                     final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(pathId);
        checkNotNull(source);
        checkNotNull(target);
        checkArgument(ordinal >= 0, "ordinal must be at least zero");

        this.pathId = pathId;
        this.ordinal = ordinal;
        this.source = source;
        this.target = target;
        this.overlap = overlap;

        hashCode = Objects.hash(this.pathId, this.ordinal, this.source, this.target, this.overlap, getTags());
    }

    public String getPathId() {
        return pathId;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public Reference getSource() {
        return source;
    }

    public Reference getTarget() {
        return target;
    }

    public String getOverlap() {
        return overlap;
    }

    public Optional<String> getOverlapOpt() {
        return Optional.ofNullable(overlap);
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
        if (!(o instanceof Traversal)) {
            return false;
        }
        Traversal t = (Traversal) o;

        return Objects.equals(pathId, t.getPathId())
            && Objects.equals(ordinal, t.getOrdinal())
            && Objects.equals(source, t.getSource())
            && Objects.equals(target, t.getTarget())
            && Objects.equals(overlap, t.getOverlap())
            && Objects.equals(getTags(), t.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "T", pathId, ordinal, source.splitToString(), target.splitToString(), getOverlapOpt().orElse("*"));
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }
}
