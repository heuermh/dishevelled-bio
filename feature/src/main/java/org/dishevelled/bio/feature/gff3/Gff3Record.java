/*

    dsh-bio-feature  Sequence features.
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
package org.dishevelled.bio.feature.gff3;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;

import javax.annotation.concurrent.Immutable;

/**
 * GFF3 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Gff3Record {
    private final String seqid;
    private final String source;
    private final String featureType;
    private final long start;
    private final long end;
    private final Double score;
    private final String strand;
    private final Integer phase;
    private final ListMultimap<String, String> attributes;
    private final int hashCode;

    /**
     * Create a new GFF3 record.
     *
     * @param seqid seqid, must not be null
     * @param source source, must not be null
     * @param featureType feature type, must not be null
     * @param start start, must be at least 0L
     * @param end end, must be at least zero, and greater than or equal to start
     * @param score score
     * @param strand strand, if present must be <code>-</code>, <code>+</code>, or <code>?</code>
     * @param phase phase, if present must be <code>0</code>, <code>1</code>, or <code>2</code>
     * @param attributes attributes, must not be null
     */
    public Gff3Record(final String seqid, final String source, final String featureType, final long start, final long end, final Double score, final String strand, final Integer phase, final ListMultimap<String, String> attributes) {
        checkNotNull(seqid);
        checkNotNull(source);
        checkNotNull(featureType);
        checkNotNull(attributes);
        checkArgument(start >= 0L, "start must be at least zero, was " + start);
        checkArgument(end >= 0L, "end must be at least zero, was " + end);
        checkArgument(end >= start, "end must be greater than or equal to start, was " + end);

        if (strand != null) {
            checkArgument("-".equals(strand) || "+".equals(strand) || "?".equals(strand), "if present, strand must be either -, +, or ?, was " + strand);
        }
        if (phase != null) {
            checkArgument(phase >= 0 && phase < 3, "if present, phase must be either 0, 1, or 2, was " + phase);
        }

        this.seqid = seqid;
        this.source = source;
        this.featureType = featureType;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.phase = phase;
        this.attributes = ImmutableListMultimap.copyOf(attributes);
        hashCode = Objects.hash(this.seqid, this.source, this.featureType, this.start, this.end, this.score, this.strand, this.phase, this.attributes);
    }


    /**
     * Return the seqid for this GFF3 record.
     *
     * @return the seqid for this GFF3 record
     */
    public String getSeqid() {
        return seqid;
    }

    /**
     * Return the source for this GFF3 record.
     *
     * @return the source for this GFF3 record
     */
    public String getSource() {
        return source;
    }

    /**
     * Return the feature type for this GFF3 record.
     *
     * @return the feature type for this GFF3 record
     */
    public String getFeatureType() {
        return featureType;
    }

    /**
     * Return the start for this GFF3 record in 0-based coordinate system, closed open range.
     *
     * @return the start for this GFF3 record in 0-based coordinate system, closed open range
     */
    public long getStart() {
        return start;
    }

    /**
     * Return the end for this GFF3 record in 0-based coordinate system, closed open range.
     *
     * @return the end for this GFF3 record in 0-based coordinate system, closed open range
     */
    public long getEnd() {
        return end;
    }

    /**
     * Return the score for this GFF3 record, if any.
     *
     * @return the score for this GFF3 record, if any
     */
    public Double getScore() {
        return score;
    }

    /**
     * Return the strand for this GFF3 record.
     *
     * @return the strand for this GFF3 record
     */
    public String getStrand() {
        return strand;
    }

    /**
     * Return the phase for this GFF3 record, if any.
     *
     * @return the phase for this GFF3 record, if any
     */
    public Integer getPhase() {
        return phase;
    }

    /**
     * Return the attributes for this GFF3 record.
     *
     * @return the attributes for this GFF3 record
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Return this GFF3 record as a 0-based coordinate system, closed open range.
     *
     * @return this GFF3 record as a 0-based coordinate system, closed open range
     */
    public Range<Long> toRange() {
        return Range.closedOpen(start, end);
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
        if (!(o instanceof Gff3Record)) {
            return false;
        }
        Gff3Record gff3Record = (Gff3Record) o;

        return Objects.equals(seqid, gff3Record.seqid)
            && Objects.equals(source, gff3Record.source)
            && Objects.equals(featureType, gff3Record.featureType)
            && Objects.equals(start, gff3Record.start)
            && Objects.equals(end, gff3Record.end)
            && Objects.equals(score, gff3Record.score)
            && Objects.equals(strand, gff3Record.strand)
            && Objects.equals(phase, gff3Record.phase)
            && Objects.equals(attributes, gff3Record.attributes);
    }

    @Override
    public String toString() {
        String attributes = writeAttributes(this.attributes);
        return Joiner.on("\t").join(seqid, source, featureType, start + 1L, end, score == null ? "." : score, strand == null ? "." : strand, phase == null ? "." : phase, attributes);
    }

    /**
     * Parse the GFF3 attributes column into a list multimap of <code>&lt;String, String&gt;</code> entries.
     *
     * @param attributes attributes to parse, must not be null
     * @return the GFF3 attributes column parsed into a list multimap of <code>&lt;String, String&gt;</code> entries
     */
    static ListMultimap<String, String> parseAttributes(final String attributes) {
        checkNotNull(attributes);
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
        for (String token : attributes.split(";")) {
            String[] entry = token.split("=");
            builder.put(entry[0], entry[1]);
        }
        return builder.build();
    }

    /**
     * Write the list multimap of <code>&lt;String, String&gt;</code> entries as GFF3 attributes column format.
     *
     * @param attributes attributes to write, must not be null
     * @return the list multimap of <code>&lt;String, String&gt;</code> entries written as GFF3 attributes column format
     */
    static String writeAttributes(final ListMultimap<String, String> attributes) {
        checkNotNull(attributes);
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> entries = attributes.entries().iterator();
        if (entries.hasNext()) {
            Map.Entry<String, String> first = entries.next();
            sb.append(first.getKey());
            sb.append("=");
            sb.append(first.getValue());
        }
        while (entries.hasNext()) {
            sb.append(";");
            Map.Entry<String, String> next = entries.next();
            sb.append(next.getKey());
            sb.append("=");
            sb.append(next.getValue());            
        }
        return sb.toString();
    }

    /**
     * Return true if the specified value is the missing value (<code>"."</code>).
     *
     * @param value value
     * @return true if the specified value is the missing value (<code>"."</code>)
     */
    static boolean isMissingValue(final String value) {
        return ".".equals(value);
    }

    /**
     * Return a new GFF3 record parsed from the specified value.
     *
     * @param value value to parse
     * @return a new GFF3 record parsed from the specified value
     * @throws IllegalArgumentException if the value is not valid GFF3 format
     * @throws NumberFormatException if a number valued field cannot be parsed as a number
     */
    public static Gff3Record valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 9) {
            throw new IllegalArgumentException("value must have nine fields (seqid, source, featureType, start, end, score, strand, phase, attributes), was " + tokens.size());
        }
        String seqid = tokens.get(0);
        String source = tokens.get(1);
        String featureType = tokens.get(2);
        long start = Long.parseLong(tokens.get(3)) - 1L; // GFF3 coordinate system is 1-based
        long end = Long.parseLong(tokens.get(4)); // GFF3 ranges are closed
        Double score = isMissingValue(tokens.get(5)) ? null : Double.parseDouble(tokens.get(5));
        String strand = isMissingValue(tokens.get(6)) ? null : tokens.get(6);
        Integer phase = isMissingValue(tokens.get(7)) ? null : Integer.parseInt(tokens.get(7));
        ListMultimap<String, String> attributes = parseAttributes(tokens.get(8));
        return new Gff3Record(seqid, source, featureType, start, end, score, strand, phase, attributes);
    }
}
