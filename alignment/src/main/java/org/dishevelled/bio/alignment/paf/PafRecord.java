/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.paf;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;
import org.dishevelled.bio.annotation.AnnotatedRecord;

/**
 * PAF (a Pairwise mApping Format) record.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class PafRecord extends AnnotatedRecord {

    /** Query name. */
    private final String queryName;

    /** Query length. */
    private final long queryLength;

    /** Query start. */
    private final long queryStart;

    /** Query end. */
    private final long queryEnd;

    /** Relative strand. */
    private final char strand;

    /** Target name. */
    private final String targetName;

    /** Target length. */
    private final long targetLength;

    /** Target start. */
    private final long targetStart;

    /** Target end. */
    private final long targetEnd;

    /** Number of residue matches. */
    private final long matches;

    /** Alignment block length. */
    private final long alignmentBlockLength;

    /** Mapping quality. */
    private final int mappingQuality;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new PAF record.
     *
     * @param queryName query name, must not be null
     * @param queryLength query length
     * @param queryStart query start
     * @param queryEnd query end
     * @param strand relative strand, must be '+' or '-'
     * @param targetName target name, must not be null
     * @param targetLength target length
     * @param targetStart target start
     * @param targetEnd target end
     * @param matches number of residue matches
     * @param alignmentBlockLength alignment block length
     * @param mappingQuality mapping quality
     * @param annotations annotations, must not be null
     */
    public PafRecord(final String queryName,
                     final long queryLength,
                     final long queryStart,
                     final long queryEnd,
                     final char strand,
                     final String targetName,
                     final long targetLength,
                     final long targetStart,
                     final long targetEnd,
                     final long matches,
                     final long alignmentBlockLength,
                     final int mappingQuality,
                     final Map<String, Annotation> annotations) {
        super(annotations);
        checkNotNull(queryName);
        checkNotNull(targetName);
        checkArgument('+' == strand || '-' == strand, "strand must be one of { '+', '-' }");

        this.queryName = queryName;
        this.queryLength = queryLength;
        this.queryStart = queryStart;
        this.queryEnd = queryEnd;
        this.strand = strand;
        this.targetName = targetName;
        this.targetLength = targetLength;
        this.targetStart = targetStart;
        this.targetEnd = targetEnd;
        this.matches = matches;
        this.alignmentBlockLength = alignmentBlockLength;
        this.mappingQuality = mappingQuality;

        hashCode = Objects.hash(this.queryName, this.queryLength, this.queryStart,
                                this.queryEnd, this.strand, this.targetName, this.targetLength,
                                this.targetStart, this.targetEnd, this.matches, this.alignmentBlockLength,
                                this.mappingQuality, getAnnotations());
    }


    /**
     * Return the query name for this PAF record.
     *
     * @return the query name for this PAF record
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Return the query length for this PAF record.
     *
     * @return the query length for this PAF record
     */
    public long getQueryLength() {
        return queryLength;
    }

    /**
     * Return the query start for this PAF record.
     *
     * @return the query start for this PAF record
     */
    public long getQueryStart() {
        return queryStart;
    }

    /**
     * Return the query end for this PAF record.
     *
     * @return the query end for this PAF record
     */
    public long getQueryEnd() {
        return queryEnd;
    }

    /**
     * Return the relative strand for this PAF record.
     *
     * @return the relative strand for this PAF record
     */
    public char getStrand() {
        return strand;
    }

    /**
     * Return the target name for this PAF record.
     *
     * @return the target name for this PAF record.
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Return the target length for this PAF record.
     *
     * @return the target length for this PAF record
     */
    public long getTargetLength() {
        return targetLength;
    }

    /**
     * Return the target start for this PAF record.
     *
     * @return the target start for this PAF record
     */
    public long getTargetStart() {
        return targetStart;
    }

    /**
     * Return the target end for this PAF record.
     *
     * @return the target end for this PAF record
     */
    public long getTargetEnd() {
        return targetEnd;
    }

    /**
     * Return the number of residue matches for this PAF record.
     *
     * @return the number of residue matches for this PAF record
     */
    public long getMatches() {
        return matches;
    }

    /**
     * Return the alignment block length for this PAF record.
     *
     * @return the alignment block length for this PAF record
     */
    public long getAlignmentBlockLength() {
        return alignmentBlockLength;
    }

    /**
     * Return the mapping quality for this PAF record.
     *
     * @return the mapping quality for this PAF record
     */
    public int getMappingQuality() {
        return mappingQuality;
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
        if (!(o instanceof PafRecord)) {
            return false;
        }
        PafRecord r = (PafRecord) o;

        return Objects.equals(queryName, r.getQueryName())
            && Objects.equals(queryLength, r.getQueryLength())
            && Objects.equals(queryStart, r.getQueryStart())
            && Objects.equals(queryEnd, r.getQueryEnd())
            && Objects.equals(strand, r.getStrand())
            && Objects.equals(targetName, r.getTargetName())
            && Objects.equals(targetLength, r.getTargetLength())
            && Objects.equals(targetStart, r.getTargetStart())
            && Objects.equals(targetEnd, r.getTargetEnd())
            && Objects.equals(matches, r.getMatches())
            && Objects.equals(alignmentBlockLength, r.getAlignmentBlockLength())
            && Objects.equals(mappingQuality, r.getMappingQuality())
            && Objects.equals(getAnnotations(), r.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, queryName, queryLength, queryStart, queryEnd, strand,
                        targetName, targetLength, targetStart, targetEnd, matches, alignmentBlockLength,
                        mappingQuality);
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a PAF record from the specified value.
     *
     * @param value value, must not be null
     * @return a PAF record parsed from the specified value
     */
    public static PafRecord valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 12) {
            throw new IllegalArgumentException("PAF record value must have at least twelve tokens, was " + tokens.size());
        }
        String queryName = tokens.get(0);
        long queryLength = Long.parseLong(tokens.get(1));
        long queryStart = Long.parseLong(tokens.get(2));
        long queryEnd = Long.parseLong(tokens.get(3));
        char strand = tokens.get(4).charAt(0);
        String targetName = tokens.get(5);
        long targetLength = Long.parseLong(tokens.get(6));
        long targetStart = Long.parseLong(tokens.get(7));
        long targetEnd = Long.parseLong(tokens.get(8));
        long matches = Long.parseLong(tokens.get(9));
        long alignmentBlockLength = Long.parseLong(tokens.get(10));
        int mappingQuality = Integer.parseInt(tokens.get(11));

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 12; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(tokens.get(i));
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new PafRecord(queryName,
                             queryLength,
                             queryStart,
                             queryEnd,
                             strand,
                             targetName,
                             targetLength,
                             targetStart,
                             targetEnd,
                             matches,
                             alignmentBlockLength,
                             mappingQuality,
                             annotations.build());
    }
}
