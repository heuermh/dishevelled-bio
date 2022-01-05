/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
package org.dishevelled.bio.alignment.gaf;

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
 * GAF (graph alignment format) record.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class GafRecord extends AnnotatedRecord {

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

    /** Path name. */
    private final String pathName;

    /** Path length. */
    private final long pathLength;

    /** Path start. */
    private final long pathStart;

    /** Path end. */
    private final long pathEnd;

    /** Number of residue matches. */
    private final long matches;

    /** Alignment block length. */
    private final long alignmentBlockLength;

    /** Mapping quality. */
    private final int mappingQuality;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new GAF (graph alignment format) record.
     *
     * @param queryName query name, must not be null
     * @param queryLength query length
     * @param queryStart query start
     * @param queryEnd query end
     * @param strand relative strand, must be '+' or '-'
     * @param pathName path name, must not be null
     * @param pathLength path length
     * @param pathStart path start
     * @param pathEnd path end
     * @param matches number of residue matches
     * @param alignmentBlockLength alignment block length
     * @param mappingQuality mapping quality
     * @param annotations annotations, must not be null
     */
    public GafRecord(final String queryName,
                     final long queryLength,
                     final long queryStart,
                     final long queryEnd,
                     final char strand,
                     final String pathName,
                     final long pathLength,
                     final long pathStart,
                     final long pathEnd,
                     final long matches,
                     final long alignmentBlockLength,
                     final int mappingQuality,
                     final Map<String, Annotation> annotations) {
        super(annotations);
        checkNotNull(queryName);
        checkNotNull(pathName);
        checkArgument('+' == strand || '-' == strand, "strand must be one of { '+', '-' }");

        this.queryName = queryName;
        this.queryLength = queryLength;
        this.queryStart = queryStart;
        this.queryEnd = queryEnd;
        this.strand = strand;
        this.pathName = pathName;
        this.pathLength = pathLength;
        this.pathStart = pathStart;
        this.pathEnd = pathEnd;
        this.matches = matches;
        this.alignmentBlockLength = alignmentBlockLength;
        this.mappingQuality = mappingQuality;

        hashCode = Objects.hash(this.queryName, this.queryLength, this.queryStart,
                                this.queryEnd, this.strand, this.pathName, this.pathLength,
                                this.pathStart, this.pathEnd, this.matches, this.alignmentBlockLength,
                                this.mappingQuality, getAnnotations());
    }


    /**
     * Return the query name for this GAF record.
     *
     * @return the query name for this GAF record
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Return the query length for this GAF record.
     *
     * @return the query length for this GAF record
     */
    public long getQueryLength() {
        return queryLength;
    }

    /**
     * Return the query start for this GAF record.
     *
     * @return the query start for this GAF record
     */
    public long getQueryStart() {
        return queryStart;
    }

    /**
     * Return the query end for this GAF record.
     *
     * @return the query end for this GAF record
     */
    public long getQueryEnd() {
        return queryEnd;
    }

    /**
     * Return the relative strand for this GAF record.
     *
     * @return the relative strand for this GAF record
     */
    public char getStrand() {
        return strand;
    }

    /**
     * Return the path name for this GAF record.
     *
     * @return the path name for this GAF record.
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Return the path length for this GAF record.
     *
     * @return the path length for this GAF record
     */
    public long getPathLength() {
        return pathLength;
    }

    /**
     * Return the path start for this GAF record.
     *
     * @return the path start for this GAF record
     */
    public long getPathStart() {
        return pathStart;
    }

    /**
     * Return the path end for this GAF record.
     *
     * @return the path end for this GAF record
     */
    public long getPathEnd() {
        return pathEnd;
    }

    /**
     * Return the number of residue matches for this GAF record.
     *
     * @return the number of residue matches for this GAF record
     */
    public long getMatches() {
        return matches;
    }

    /**
     * Return the alignment block length for this GAF record.
     *
     * @return the alignment block length for this GAF record
     */
    public long getAlignmentBlockLength() {
        return alignmentBlockLength;
    }

    /**
     * Return the mapping quality for this GAF record.
     *
     * @return the mapping quality for this GAF record
     */
    public int getMappingQuality() {
        return mappingQuality;
    }

    // todo:
    // cigar  cs:Z ?


    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof GafRecord)) {
            return false;
        }
        GafRecord r = (GafRecord) o;

        return Objects.equals(queryName, r.getQueryName())
            && Objects.equals(queryLength, r.getQueryLength())
            && Objects.equals(queryStart, r.getQueryStart())
            && Objects.equals(queryEnd, r.getQueryEnd())
            && Objects.equals(strand, r.getStrand())
            && Objects.equals(pathName, r.getPathName())
            && Objects.equals(pathLength, r.getPathLength())
            && Objects.equals(pathStart, r.getPathStart())
            && Objects.equals(pathEnd, r.getPathEnd())
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
                        pathName, pathLength, pathStart, pathEnd, matches, alignmentBlockLength,
                        mappingQuality);
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a GAF record from the specified value.
     *
     * @param value value, must not be null
     * @return a GAF record parsed from the specified value
     */
    public static GafRecord valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 12) {
            throw new IllegalArgumentException("GAF record value must have at least twelve tokens, was " + tokens.size());
        }
        String queryName = tokens.get(0);
        long queryLength = Long.parseLong(tokens.get(1));
        long queryStart = Long.parseLong(tokens.get(2));
        long queryEnd = Long.parseLong(tokens.get(3));
        char strand = tokens.get(4).charAt(0);
        String pathName = tokens.get(5);
        long pathLength = Long.parseLong(tokens.get(6));
        long pathStart = Long.parseLong(tokens.get(7));
        long pathEnd = Long.parseLong(tokens.get(8));
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

        return new GafRecord(queryName,
                             queryLength,
                             queryStart,
                             queryEnd,
                             strand,
                             pathName,
                             pathLength,
                             pathStart,
                             pathEnd,
                             matches,
                             alignmentBlockLength,
                             mappingQuality,
                             annotations.build());
    }
}
