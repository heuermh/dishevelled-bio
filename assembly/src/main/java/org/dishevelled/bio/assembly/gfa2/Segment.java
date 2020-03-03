/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Segment GFA 2.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Segment extends Gfa2Record {
    /** Identifier for this segment. */
    private final String id;

    /** Length for this segment. */
    private final int length;

    /** Optional sequence for this segment. */
    private final String sequence;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new segment GFA 2.0 record.
     *
     * @param id identifier, must not be null
     * @param length length, must be at least zero
     * @param sequence sequence, if any
     * @param tags tags, must not be null
     */
    public Segment(final String id,
                   final int length,
                   @Nullable final String sequence,
                   final Map<String, Tag> tags) {

        super(tags);
        checkNotNull(id);
        checkArgument(length >= 0, "length must be at least zero");

        this.id = id;
        this.length = length;
        this.sequence = sequence;

        hashCode = Objects.hash(this.id, this.length, this.sequence, getTags());
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
     * Return an optional wrapping the identifier for this segment.
     *
     * @deprecated will be removed in 2.0, identifier is always non-null
     * @return an optional wrapping the identifier for this segment
     */
    public Optional<String> getIdOpt() {
        return Optional.ofNullable(id);
    }

    /**
     * Return the length for this segment.
     *
     * @return the length for this segment
     */
    public int getLength() {
        return length;
    }

    /**
     * Return true if this segment has a sequence.
     *
     * @since 1.3.2
     * @return true if this segment has a sequence
     */
    public boolean hasSequence() {
        return sequence != null;
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
     * the reserved key <code>RC</code>.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>RC</code>
     */
    public boolean containsRc() {
        return containsTagKey("RC");
    }

    /**
     * Return the Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @since 1.3.2
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
     * @since 1.3.2
     * @return an optional Type=i value for the reserved key <code>RC</code>
     *   as an integer
     */
    public Optional<Integer> getRcOpt() {
        return getTagIntegerOpt("RC");
    }

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>RC</code>, for read count.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>RC</code>, for read count
     */
    public boolean containsReadCount() {
        return containsRc();
    }

    /**
     * Return the read count for this segment (Type=i value for
     * the reserved key <code>RC</code> as an integer).
     *
     * @since 1.3.2
     * @return the read count for this segment (Type=i value for
     *    the reserved key <code>RC</code> as an integer)
     */
    public int getReadCount() {
        return getRc();
    }

    /**
     * Return an optional wrapping the read count for this segment
     * (Type=i value for the reserved key <code>RC</code> as an integer).
     *
     * @since 1.3.2
     * @return an optional wrapping the read count for this segment
     *    (Type=i value for the reserved key <code>RC</code> as an integer)
     */
    public Optional<Integer> getReadCountOpt() {
        return getRcOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>FC</code>.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>FC</code>
     */
    public boolean containsFc() {
        return containsTagKey("FC");
    }

    /**
     * Return the Type=i value for the reserved key <code>FC</code>
     * as an integer.
     *
     * @since 1.3.2
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
     * @since 1.3.2
     * @return an optional Type=i value for the reserved key <code>FC</code>
     *   as an integer
     */
    public Optional<Integer> getFcOpt() {
        return getTagIntegerOpt("FC");
    }

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>FC</code>, for fragment count.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>FC</code>, for fragment count
     */
    public boolean containsFragmentCount() {
        return containsFc();
    }

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>FC</code>, for fragment count.
     *
     * @since 1.3.2
     * @return if the tags for this segment contain
     *    the reserved key <code>FC</code>, for fragment count
     */
    public int getFragmentCount() {
        return getFc();
    }

    /**
     * Return an optional wrapping the fragment count for this segment
     * (Type=i value for the reserved key <code>FC</code> as an integer).
     *
     * @since 1.3.2
     * @return an optional wrapping the fragment count for this segment
     *    (Type=i value for the reserved key <code>FC</code> as an integer)
     */
    public Optional<Integer> getFragmentCountOpt() {
        return getFcOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>KC</code>.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>KC</code>
     */
    public boolean containsKc() {
        return containsTagKey("KC");
    }

    /**
     * Return the Type=i value for the reserved key <code>KC</code>
     * as an integer.
     *
     * @since 1.3.2
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
     * @since 1.3.2
     * @return an optional Type=i value for the reserved key <code>KC</code>
     *   as an integer
     */
    public Optional<Integer> getKcOpt() {
        return getTagIntegerOpt("KC");
    }

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>KC</code>, for k-mer count.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>KC</code>, for k-mer count
     */
    public boolean containsKmerCount() {
        return containsKc();
    }

    /**
     * Return the k-mer count for this segment (Type=i value for
     * the reserved key <code>KC</code> as an integer).
     *
     * @since 1.3.2
     * @return the k-mer count for this segment (Type=i value for
     *    the reserved key <code>KC</code> as an integer)
     */
    public int getKmerCount() {
        return getKc();
    }

    /**
     * Return an optional wrapping the k-mer count for this segment
     * (Type=i value for the reserved key <code>KC</code> as an integer).
     *
     * @since 1.3.2
     * @return an optional wrapping the k-mer count for this segment
     *    (Type=i value for the reserved key <code>KC</code> as an integer)
     */
    public Optional<Integer> getKmerCountOpt() {
        return getKcOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>SH</code>.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>SH</code>
     */
    public boolean containsSh() {
        return containsTagKey("SH");
    }

    /**
     * Return the Type=H value for the reserved key <code>SH</code>
     * as a byte array.
     *
     * @since 1.3.2
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
     * @since 1.3.2
     * @return an optional Type=H value for the reserved key <code>SH</code>
     *   as a byte array
     */
    public Optional<byte[]> getShOpt() {
        return getTagByteArrayOpt("SH");
    }

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>SH</code>, for SHA-256 checksum of the
     * sequence.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>SH</code>, for SHA-256 checksum of the
     *    sequence
     */
    public boolean containsSequenceChecksum() {
        return containsSh();
    }

    /**
     * Return the SHA-256 checksum of the sequence for this segment
     * (Type=H value for the reserved key <code>SH</code> as a byte array).
     *
     * @since 1.3.2
     * @return the SHA-256 checksum of the sequence for this segment
     *    (Type=H value for the reserved key <code>SH</code> as a byte array)
     */
    public byte[] getSequenceChecksum() {
        return getSh();
    }

    /**
     * Return an optional wrapping the SHA-256 checksum of the sequence
     * for this segment (Type=H value for the reserved key <code>SH</code>
     * as a byte array).
     *
     * @since 1.3.2
     * @return an optional wrapping the SHA-256 checksum of the sequence
     *    for this segment (Type=H value for the reserved key <code>SH</code>
     *    as a byte array)
     */
    public Optional<byte[]> getSequenceChecksumOpt() {
        return getShOpt();
    }

    //

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>UR</code>.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>UR</code>
     */
    public boolean containsUr() {
        return containsTagKey("UR");
    }

    /**
     * Return the Type=Z value for the reserved key <code>UR</code>
     * as a string.
     *
     * @since 1.3.2
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
     * @since 1.3.2
     * @return an optional Type=Z value for the reserved key <code>UR</code>
     *   as a string
     */
    public Optional<String> getUrOpt() {
        return getTagStringOpt("UR");
    }

    /**
     * Return true if the tags for this segment contain
     * the reserved key <code>UR</code>.
     *
     * @since 1.3.2
     * @return true if the tags for this segment contain
     *    the reserved key <code>UR</code>
     */
    public boolean containsSequenceUri() {
        return containsUr();
    }

    /**
     * Return the URI or local file-system path of the sequence for
     * this segment (Type=Z value for the reserved key <code>UR</code>
     * as a string). If it does not start with a standard protocol
     * (e.g. ftp), it is assumed to be a local path.
     *
     * @since 1.3.2
     * @return the URI or local file-system path of the sequence for
     *    this segment (Type=Z value for the reserved key <code>UR</code>
     *    as a string)
     */
    public String getSequenceUri() {
        return getUr();
    }

    /**
     * Return an optional wrapping the URI or local file-system path of
     * the sequence for this segment (Type=Z value for the reserved key
     * <code>UR</code> as a string). If it does not start with a standard
     * protocol (e.g. ftp), it is assumed to be a local path.
     *
     * @since 1.3.2
     * @return an optional wrapping the URI or local file-system path of
     *    the sequence for this segment (Type=Z value for the reserved key
     *    <code>UR</code> as a string)
     */
    public Optional<String> getSequenceUriOpt() {
        return getUrOpt();
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
        if (!(o instanceof Segment)) {
            return false;
        }
        Segment s = (Segment) o;

        return Objects.equals(id, s.getId())
            && Objects.equals(length, s.getLength())
            && Objects.equals(sequence, s.getSequence())
            && Objects.equals(getTags(), s.getTags());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "S", id, length, sequence == null ? "*" : sequence);
        if (!getTags().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getTags().values());
        }
        return sb.toString();
    }


    /**
     * Parse a segment GFA 2.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a segment GFA 2.0 record parsed from the specified value
     */
    public static Segment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("S"), "segment value must start with S");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 4) {
            throw new IllegalArgumentException("segment value must have at least four tokens, was " + tokens.size());
        }

        String id = tokens.get(1);
        int length = Integer.parseInt(tokens.get(2));
        String sequence = "*".equals(tokens.get(3)) ? null : tokens.get(3);

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 4; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Tag tag = Tag.valueOf(token);
                tags.put(tag.getName(), tag);
            }
        }

        return new Segment(id, length, sequence, tags.build());
    }
}
