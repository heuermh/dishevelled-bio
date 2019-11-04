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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Segment GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Segment extends Gfa1Record {
    /** Identifier for this segment. */
    private final String id;

    /** Optional sequence for this segment. */
    private final String sequence;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new segment GFA 1.0 record.
     *
     * @param id identifier, must not be null
     * @param sequence sequence, if any
     * @param tags tags, must not be null
     */
    public Segment(final String id,
                   @Nullable final String sequence,
                   final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(id);

        this.id = id;
        this.sequence = sequence;

        hashCode = Objects.hash(this.id, this.sequence, getTags());
    }


    /**
     * Return the identifier for this segment.
     *
     * @return the identifier for this segment
     */
    public String getId() {
        return id;
    }

    /**
     * Return the sequence for this segment, if any.
     *
     * @return the sequence for this segment, if any
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Return an optional wrapping the sequence for this segment.
     *
     * @return an optional wrapping the sequence for this segment
     */
    public Optional<String> getSequenceOpt() {
        return Optional.ofNullable(sequence);
    }


    // optional fields

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>LN</code>.
     *
     * @return if the tags for this segment contain
     *    the reserved key <code>LN</code>
     */
    public boolean containsLn() {
        return containsTagKey("LN");
    }

    /**
     * Return the Type=i value for the reserved key <code>LN</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>LN</code>
     *    as an integer
     */
    public int getLn() {
        return getTagInteger("LN");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>LN</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>LN</code>
     *   as an integer
     */
    public Optional<Integer> getLnOpt() {
        return getTagIntegerOpt("LN");
    }

    public boolean containsLength() {
        return containsLn();
    }
    public int getLength() {
        return getLn();
    }
    public Optional<Integer> getLengthOpt() {
        return getLnOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>RC</code>.
     *
     * @return if the tags for this segment contain
     *    the reserved key <code>RC</code>
     */
    public boolean containsRc() {
        return containsTagKey("RC");
    }

    /**
     * Return the Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>RC</code>
     *    as an integer
     */
    public int getRc() {
        return getTagInteger("RC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>RC</code>
     *   as an integer
     */
    public Optional<Integer> getRcOpt() {
        return getTagIntegerOpt("RC");
    }

    public boolean containsReadCount() {
        return containsRc();
    }
    public int getReadCount() {
        return getRc();
    }
    public Optional<Integer> getReadCountOpt() {
        return getRcOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>FC</code>.
     *
     * @return if the tags for this segment contain
     *    the reserved key <code>FC</code>
     */
    public boolean containsFc() {
        return containsTagKey("FC");
    }

    /**
     * Return the Type=i value for the reserved key <code>FC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>FC</code>
     *    as an integer
     */
    public int getFc() {
        return getTagInteger("FC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>FC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>FC</code>
     *   as an integer
     */
    public Optional<Integer> getFcOpt() {
        return getTagIntegerOpt("FC");
    }

    public boolean containsFragmentCount() {
        return containsFc();
    }
    public int getFragmentCount() {
        return getFc();
    }
    public Optional<Integer> getFragmentCountOpt() {
        return getFcOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>KC</code>.
     *
     * @return if the tags for this segment contain
     *    the reserved key <code>KC</code>
     */
    public boolean containsKc() {
        return containsTagKey("KC");
    }

    /**
     * Return the Type=i value for the reserved key <code>KC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>KC</code>
     *    as an integer
     */
    public int getKc() {
        return getTagInteger("KC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>KC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>KC</code>
     *   as an integer
     */
    public Optional<Integer> getKcOpt() {
        return getTagIntegerOpt("KC");
    }

    public boolean containsKmerCount() {
        return containsKc();
    }
    public int getKmerCount() {
        return getKc();
    }
    public Optional<Integer> getKmerCountOpt() {
        return getKcOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>SH</code>.
     *
     * @return if the tags for this segment contain
     *    the reserved key <code>SH</code>
     */
    public boolean containsSh() {
        return containsTagKey("SH");
    }

    /**
     * Return the Type=H value for the reserved key <code>SH</code>
     * as a byte array.
     *
     * @return the Type=Z value for the reserved key <code>SH</code>
     *    as a byte array
     */
    public byte[] getSh() {
        return getTagByteArray("SH");
    }

    /**
     * Return an optional Type=H value for the reserved key <code>SH</code>
     * as a byte array.
     *
     * @return an optional Type=H value for the reserved key <code>SH</code>
     *   as a byte array
     */
    public Optional<byte[]> getShOpt() {
        return getTagByteArrayOpt("SH");
    }

    public boolean containsSequenceChecksum() {
        return containsSh();
    }
    public byte[] getSequenceChecksum() {
        return getSh();
    }
    public Optional<byte[]> getSequenceChecksumOpt() {
        return getShOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>UR</code>.
     *
     * @return if the tags for this segment contain
     *    the reserved key <code>UR</code>
     */
    public boolean containsUr() {
        return containsTagKey("UR");
    }

    /**
     * Return the Type=Z value for the reserved key <code>UR</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>UR</code>
     *    as a string
     */
    public String getUr() {
        return getTagString("UR");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>UR</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>UR</code>
     *   as a string
     */
    public Optional<String> getUrOpt() {
        return getTagStringOpt("UR");
    }

    public boolean containsSequenceUri() {
        return containsUr();
    }
    public String getSequenceUri() {
        return getUr();
    }
    public Optional<String> getSequenceUriOpt() {
        return getUrOpt();
    }

    //

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Segment)) {
            return false;
        }
        Segment s = (Segment) o;

        return Objects.equals(id, s.getId())
            && Objects.equals(sequence, s.getSequence())
            && Objects.equals(getTags(), s.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "S", id, sequence == null ? "*" : sequence);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }


    /**
     * Parse a segment GFA 1.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a segment GFA 1.0 record parsed from the specified value
     */
    public static Segment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("S"), "value must start with S");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("value must have at least three tokens, was " + tokens.size());
        }
        String id = tokens.get(1);
        String sequence = "*".equals(tokens.get(2)) ? null : tokens.get(2);

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 3; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getName(), tag);
        }

        return new Segment(id, sequence, tags.build());
    }
}
