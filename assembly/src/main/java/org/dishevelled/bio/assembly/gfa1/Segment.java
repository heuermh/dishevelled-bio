/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import org.dishevelled.bio.annotation.Annotation;

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
     * @param annotations annotations, must not be null
     */
    public Segment(final String id,
                   @Nullable final String sequence,
                   final Map<String, Annotation> annotations) {

        super(annotations);
        checkNotNull(id);

        this.id = id;
        this.sequence = sequence;

        hashCode = Objects.hash(this.id, this.sequence, getAnnotations());
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
     * Return true if this segment has a sequence.
     *
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
     * Return true if the annotations for this segment contain
     * the reserved key <code>LN</code>.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>LN</code>
     */
    public boolean containsLn() {
        return containsAnnotationKey("LN");
    }

    /**
     * Return the Type=i value for the reserved key <code>LN</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>LN</code>
     *    as an integer
     */
    public int getLn() {
        return getAnnotationInteger("LN");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>LN</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>LN</code>
     *   as an integer
     */
    public Optional<Integer> getLnOpt() {
        return getAnnotationIntegerOpt("LN");
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>LN</code>, for length.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>LN</code>, for length
     */
    public boolean containsLength() {
        return containsLn();
    }

    /**
     * Return the length of this segment (Type=i value for
     * the reserved key <code>LN</code> as an integer).
     *
     * @return the length of this segment (Type=i value for
     *    the reserved key <code>LN</code> as an integer)
     */
    public int getLength() {
        return getLn();
    }

    /**
     * Return an optional wrapping the length of this segment
     * (Type=i value for the reserved key <code>LN</code> as an integer).
     *
     * @return an optional wrapping the length of this segment
     *    (Type=i value for the reserved key <code>LN</code> as an integer)
     */
    public Optional<Integer> getLengthOpt() {
        return getLnOpt();
    }

    //

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>RC</code>.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>RC</code>
     */
    public boolean containsRc() {
        return containsAnnotationKey("RC");
    }

    /**
     * Return the Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>RC</code>
     *    as an integer
     */
    public int getRc() {
        return getAnnotationInteger("RC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>RC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>RC</code>
     *   as an integer
     */
    public Optional<Integer> getRcOpt() {
        return getAnnotationIntegerOpt("RC");
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>RC</code>, for read count.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>RC</code>, for read count
     */
    public boolean containsReadCount() {
        return containsRc();
    }

    /**
     * Return the read count for this segment (Type=i value for
     * the reserved key <code>RC</code> as an integer).
     *
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
     * @return an optional wrapping the read count for this segment
     *    (Type=i value for the reserved key <code>RC</code> as an integer)
     */
    public Optional<Integer> getReadCountOpt() {
        return getRcOpt();
    }

    //

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>FC</code>.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>FC</code>
     */
    public boolean containsFc() {
        return containsAnnotationKey("FC");
    }

    /**
     * Return the Type=i value for the reserved key <code>FC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>FC</code>
     *    as an integer
     */
    public int getFc() {
        return getAnnotationInteger("FC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>FC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>FC</code>
     *   as an integer
     */
    public Optional<Integer> getFcOpt() {
        return getAnnotationIntegerOpt("FC");
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>FC</code>, for fragment count.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>FC</code>, for fragment count
     */
    public boolean containsFragmentCount() {
        return containsFc();
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>FC</code>, for fragment count.
     *
     * @return if the annotations for this segment contain
     *    the reserved key <code>FC</code>, for fragment count
     */
    public int getFragmentCount() {
        return getFc();
    }

    /**
     * Return an optional wrapping the fragment count for this segment
     * (Type=i value for the reserved key <code>FC</code> as an integer).
     *
     * @return an optional wrapping the fragment count for this segment
     *    (Type=i value for the reserved key <code>FC</code> as an integer)
     */
    public Optional<Integer> getFragmentCountOpt() {
        return getFcOpt();
    }

    //

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>KC</code>.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>KC</code>
     */
    public boolean containsKc() {
        return containsAnnotationKey("KC");
    }

    /**
     * Return the Type=i value for the reserved key <code>KC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>KC</code>
     *    as an integer
     */
    public int getKc() {
        return getAnnotationInteger("KC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>KC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>KC</code>
     *   as an integer
     */
    public Optional<Integer> getKcOpt() {
        return getAnnotationIntegerOpt("KC");
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>KC</code>, for k-mer count.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>KC</code>, for k-mer count
     */
    public boolean containsKmerCount() {
        return containsKc();
    }

    /**
     * Return the k-mer count for this segment (Type=i value for
     * the reserved key <code>KC</code> as an integer).
     *
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
     * @return an optional wrapping the k-mer count for this segment
     *    (Type=i value for the reserved key <code>KC</code> as an integer)
     */
    public Optional<Integer> getKmerCountOpt() {
        return getKcOpt();
    }

    //

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>SH</code>.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>SH</code>
     */
    public boolean containsSh() {
        return containsAnnotationKey("SH");
    }

    /**
     * Return the Type=H value for the reserved key <code>SH</code>
     * as a byte array.
     *
     * @return the Type=Z value for the reserved key <code>SH</code>
     *    as a byte array
     */
    public byte[] getSh() {
        return getAnnotationByteArray("SH");
    }

    /**
     * Return an optional Type=H value for the reserved key <code>SH</code>
     * as a byte array.
     *
     * @return an optional Type=H value for the reserved key <code>SH</code>
     *   as a byte array
     */
    public Optional<byte[]> getShOpt() {
        return getAnnotationByteArrayOpt("SH");
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>SH</code>, for SHA-256 checksum of the
     * sequence.
     *
     * @return true if the annotations for this segment contain
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
     * @return an optional wrapping the SHA-256 checksum of the sequence
     *    for this segment (Type=H value for the reserved key <code>SH</code>
     *    as a byte array)
     */
    public Optional<byte[]> getSequenceChecksumOpt() {
        return getShOpt();
    }

    //

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>UR</code>.
     *
     * @return true if the annotations for this segment contain
     *    the reserved key <code>UR</code>
     */
    public boolean containsUr() {
        return containsAnnotationKey("UR");
    }

    /**
     * Return the Type=Z value for the reserved key <code>UR</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>UR</code>
     *    as a string
     */
    public String getUr() {
        return getAnnotationString("UR");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>UR</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>UR</code>
     *   as a string
     */
    public Optional<String> getUrOpt() {
        return getAnnotationStringOpt("UR");
    }

    /**
     * Return true if the annotations for this segment contain
     * the reserved key <code>UR</code>.
     *
     * @return true if the annotations for this segment contain
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
            && Objects.equals(sequence, s.getSequence())
            && Objects.equals(getAnnotations(), s.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "S", id, sequence == null ? "*" : sequence);
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
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
        checkArgument(value.startsWith("S"), "segment value must start with S");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("segment value must have at least three tokens, was " + tokens.size());
        }
        String id = tokens.get(1);
        String sequence = "*".equals(tokens.get(2)) ? null : tokens.get(2);

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 3; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(token);
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Segment(id, sequence, annotations.build());
    }
}
