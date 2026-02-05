/*

    dsh-bio-feature  Sequence features.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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
package org.dishevelled.bio.feature.bed;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.Range;

import com.google.common.primitives.Longs;

import javax.annotation.concurrent.Immutable;

/**
 * BED record.
 *
 * Supports the <a href="https://github.com/samtools/hts-specs/blob/master/BEDv1.pdf">
 * Browser Extensible Data (BED) format specification version 1.0</a>, with exception that
 * only tab characters are allowable as field separators.
 *
 * <ul>
 *   <li>BED3: A BED file where each feature is described by chrom, start, and end.
 *       For example: <code>chr1          11873   14409</code></li>
 *   <li>BED4: A BED file where each feature is described by chrom, start, end, and name.
 *       For example: <code>chr1  11873  14409  uc001aaa.3</code></li>
 *   <li>BED5: A BED file where each feature is described by chrom, start, end, name, and score.
 *       For example: <code>chr1 11873 14409 uc001aaa.3 0</code></li>
 *   <li>BED6: A BED file where each feature is described by chrom, start, end, name, score, and strand.
 *       For example: <code>chr1 11873 14409 uc001aaa.3 0 +</code></li>
 *   <li>BED12: A BED file where each feature is described by all twelve columns listed above.
 *       For example: <code>chr1 11873 14409 uc001aaa.3 0 + 11873 11873 0 3 354,109,1189, 0,739,1347,</code></li>
 * </ul>
 *
 * @author  Michael Heuer
 */
@Immutable
public final class BedRecord {
    private final BedFormat format;
    private final String chrom;
    private final long start;
    private final long end;
    private final String name;
    private final int score;
    private final String strand;
    private final long thickStart;
    private final long thickEnd;
    private final String itemRgb;
    private final int blockCount;
    private final long[] blockSizes;
    private final long[] blockStarts;
    private final int hashCode;

    /** Empty long array. */
    private static final long[] EMPTY = new long[0];

    /** R,G,B pattern. */
    private static final Pattern RGB = Pattern.compile("[0-9]{1,3},[0-9]{1,3},[0-9]{1,3}");

    /** Valid chrom pattern. */
    private static final Pattern CHROM = Pattern.compile("[A-Za-z0-9_]{1,255}");

    /** Valid name pattern. */
    private static final Pattern NAME = Pattern.compile("[\\x20-\\x7e]{1,255}");


    /**
     * Create a new BED record.
     *
     * @param format format, must not be null
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name, if present, must not contain control characters
     * @param score score, must be at least zero and less than or equal to 1000
     * @param strand strand, if present must be either <code>-</code> or <code>+</code>
     * @param thickStart thick start, must be greater than or equal to start
     *    and less than or equal to end
     * @param thickEnd thick end, must be greater than or equal to thick start
     *    and less than or equal to end
     * @param itemRgb item RGB, if present, must be in R,G,B format, e.g. <code>255,0,0</code>
     * @param blockCount block count, must be at least zero; if BED12+ format at least one
     * @param blockSizes block sizes, must be the same length as block count
     * @param blockStarts block starts, must be the same length as block count
     */
    private BedRecord(final BedFormat format, final String chrom, final long start, final long end, final String name, final int score, final String strand,
                     final long thickStart, final long thickEnd, final String itemRgb, final int blockCount, final long[] blockSizes, final long[] blockStarts) {

        checkNotNull(format);
        checkNotNull(chrom);
        checkArgument(start >= 0L, "start must be at least zero");
        checkArgument(end >= 0L, "end must be at least zero");
        checkArgument(end >= start, "end must be greater than or equal to start");
        checkArgument(score >= 0, "score must be at least zero");
        checkArgument(score <= 1000, "score must be less than or equal to 1000");
        checkArgument(thickStart >= start, "thickStart must be greater than or equal to start");
        checkArgument(thickStart <= end, "thickStart must be less than or equal to end");
        checkArgument(thickEnd >= thickStart, "thickEnd must be greater than or equal to thickStart");
        checkArgument(thickEnd <= end, "thickEnd must be less than or equal to end");
        checkArgument(blockCount >= 0, "blockCount must be at least zero");
        checkNotNull(blockSizes);
        checkNotNull(blockStarts);
        checkArgument(blockSizes.length == blockCount, "blockSizes must be the same length as blockCount");
        checkArgument(blockStarts.length == blockCount, "blockStarts must be the same length as blockCount");

        if (!CHROM.matcher(chrom).matches()) {
            throw new IllegalArgumentException("invalid format for chrom, must include only word characters [A-Za-z0-9_]");
        }

        // allow "." if not specified
        if (strand != null && !".".equals(strand)) {
            checkArgument("-".equals(strand) || "+".equals(strand), "if present, strand must be either - or +");
        }

        String fixedItemRgb = itemRgb;
        // allow "0" if not specified in R,G,B format
        if (fixedItemRgb != null && !"0".equals(itemRgb)) {
            // fixup to remove leading zeros
            if (RGB.matcher(itemRgb).matches()) {
                String[] tokens = itemRgb.split(",");
                int r = Integer.parseInt(tokens[0]);
                int g = Integer.parseInt(tokens[1]);
                int b = Integer.parseInt(tokens[2]);
                if (r > 255 || g > 255 || b > 255) {
                    throw new IllegalArgumentException("invalid R,G,B format for itemRgb, color values range [0-255]");
                }
                fixedItemRgb = r + "," + g + "," + b;
            }
            else {
                throw new IllegalArgumentException("if present, itemRgb must be in R,G,B format, e.g. 255,0,0");
            }
        }

        // validate name if at least BED4
        if (format.isAtLeastBED4()) {
            if (!NAME.matcher(name).matches()) {
                throw new IllegalArgumentException("if BED4+, name must not contain control characters");
            }
        }

        // validate blocks if at least BED12
        if (format.isAtLeastBED12()) {
            checkArgument(blockCount >= 1, "if BED12+, blockCount must be at least one");
            checkArgument(blockStarts[0] == 0L, "first block must start at start");
            checkArgument(start + blockStarts[blockCount - 1] + blockSizes[blockCount - 1] == end, "last block must end at end"); 

            long lastStart = 0L;
            long lastSize = 0L;
            for (int i = 0; i < blockCount; i++) {
                if (blockStarts[i] < lastStart) {
                    throw new IllegalArgumentException("blocks must be sorted in ascending order");
                }
                if (start + blockStarts[i] + blockSizes[i] > end) {
                    throw new IllegalArgumentException("start plus block extends beyond end");
                }
                if (lastStart + lastSize > blockStarts[i]) {
                    throw new IllegalArgumentException("blocks must not overlap");
                }
                if (blockSizes[i] < 0) {
                    throw new IllegalArgumentException("block size must be at least zero");
                }
                lastStart = blockStarts[i];
                lastSize = blockSizes[i];
            }
        }

        this.format = format;
        this.chrom = chrom;
        this.start = start;
        this.end = end;
        this.name = name;
        this.score = score;
        this.strand = strand;
        this.thickStart = thickStart;
        this.thickEnd = thickEnd;
        this.itemRgb = fixedItemRgb;
        this.blockCount = blockCount;
        this.blockSizes = blockSizes.clone();
        this.blockStarts = blockStarts.clone();

        hashCode = Objects.hash(this.format, this.chrom, this.start, this.end, this.name, this.score, this.strand, this.thickStart, this.thickEnd, this.itemRgb, this.blockCount, this.blockSizes, this.blockStarts);
    }

    /**
     * Create a new BED3 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     */
    public BedRecord(final String chrom, final long start, final long end) {
        this(BedFormat.BED3, chrom, start, end, null, 0, null, start, end, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED4 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name, if present, must not contain control characters
     */
    public BedRecord(final String chrom, final long start, final long end, final String name) {
        this(BedFormat.BED4, chrom, start, end, name, 0, null, start, end, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED5 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name, if present, must not contain control characters
     * @param score score, must be at least zero and less than or equal to 1000
     */
    public BedRecord(final String chrom, final long start, final long end, final String name, final int score) {
        this(BedFormat.BED5, chrom, start, end, name, score, null, start, end, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED6 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name, if present, must not contain control characters
     * @param score score, must be at least zero and less than or equal to 1000
     * @param strand strand, if present must be either <code>-</code> or <code>+</code>
     */
    public BedRecord(final String chrom, final long start, final long end, final String name, final int score, final String strand) {
        this(BedFormat.BED6, chrom, start, end, name, score, strand, start, end, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED12 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name, if present, must not contain control characters
     * @param score score, must be at least zero and less than or equal to 1000
     * @param strand strand, if present must be either <code>-</code> or <code>+</code>
     * @param thickStart thick start, must be greater than or equal to start
     *    and less than or equal to end
     * @param thickEnd thick end, must be greater than or equal to thick start
     *    and less than or equal to end
     * @param itemRgb item RGB, if present, must be in R,G,B format, e.g. <code>255,0,0</code>
     * @param blockCount block count, must be at least one
     * @param blockSizes block sizes, must be the same length as block count
     * @param blockStarts block starts, must be the same length as block count
     */
    public BedRecord(final String chrom, final long start, final long end, final String name, final int score, final String strand,
                     final long thickStart, final long thickEnd, final String itemRgb, final int blockCount, final long[] blockSizes, final long[] blockStarts) {
        this(BedFormat.BED12, chrom, start, end, name, score, strand, thickStart, thickEnd, itemRgb, blockCount, blockSizes, blockStarts);
    }


    /**
     * Return the chrom for this BED record.
     *
     * @return the chrom for this BED record
     */
    public String getChrom() {
        return chrom;
    }

    /**
     * Return the start for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the start for this BED record in 0-based coordinate system, closed open range
     */
    public long getStart() {
        return start;
    }

    /**
     * Return the end for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the end for this BED record in 0-based coordinate system, closed open range
     */
    public long getEnd() {
        return end;
    }

    /**
     * Return the name for this BED record, if any.
     *
     * @return the name for this BED record, if any
     */
    public String getName() {
        return name;
    }

    /**
     * Return the score for this BED record.
     *
     * @return the score for this BED record
     */
    public int getScore() {
        return score;
    }

    /**
     * Return the strand for this BED record, if any.
     *
     * @return the strand for this BED record, if any
     */
    public String getStrand() {
        return strand;
    }

    /**
     * Return the thick start for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the thick start for this BED record in 0-based coordinate system, closed open range
     */
    public long getThickStart() {
        return thickStart;
    }

    /**
     * Return the thick end for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the thick end for this BED record in 0-based coordinate system, closed open range
     */
    public long getThickEnd() {
        return thickEnd;
    }

    /**
     * Return the item RGB for this BED record, if any.
     *
     * @return the item RGB for this BED record, if any
     */
    public String getItemRgb() {
        return itemRgb;
    }

    /**
     * Return the block count for this BED record.
     *
     * @return the block count for this BED record
     */
    public int getBlockCount() {
        return blockCount;
    }

    /**
     * Return the block sizes for this BED record.
     *
     * @return the block sizes for this BED record
     */
    public long[] getBlockSizes() {
        return (long[]) blockSizes.clone();
    }

    /**
     * Return the block starts for this BED record.
     *
     * @return the block starts for this BED record
     */
    public long[] getBlockStarts() {
        return (long[]) blockStarts.clone();
    }

    /**
     * Return the format of this BED record.
     *
     * @return the format of this BED record
     */
    public BedFormat getFormat() {
        return format;
    }

    /**
     * Return this BED record as a 0-based coordinate system, closed open range.
     *
     * @return this BED record as a 0-based coordinate system, closed open range
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
        if (!(o instanceof BedRecord)) {
            return false;
        }
        BedRecord bedRecord = (BedRecord) o;

        return Objects.equals(format, bedRecord.format)
            && Objects.equals(chrom, bedRecord.chrom)
            && Objects.equals(start, bedRecord.start)
            && Objects.equals(end, bedRecord.end)
            && Objects.equals(name, bedRecord.name)
            && Objects.equals(score, bedRecord.score)
            && Objects.equals(strand, bedRecord.strand)
            && Objects.equals(thickStart, bedRecord.thickStart)
            && Objects.equals(thickEnd, bedRecord.thickEnd)
            && Objects.equals(itemRgb, bedRecord.itemRgb)
            && Objects.equals(blockCount, bedRecord.blockCount)
            && Arrays.equals(blockSizes, bedRecord.blockSizes)
            && Arrays.equals(blockStarts, bedRecord.blockStarts);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chrom);
        sb.append("\t");
        sb.append(start);
        sb.append("\t");
        sb.append(end);
        switch (format) {
            case BED3:
                break;
            case BED4:
                sb.append("\t");
                sb.append(name == null ? "." : name);
                break;
            case BED5:
                sb.append("\t");
                sb.append(name);
                sb.append("\t");
                sb.append(score);
                break;
            case BED6:
                sb.append("\t");
                sb.append(name == null ? "." : name);
                sb.append("\t");
                sb.append(score);
                sb.append("\t");
                sb.append(strand == null ? "." : strand);
                break;
            case BED12:
                sb.append("\t");
                sb.append(name == null ? "." : name);
                sb.append("\t");
                sb.append(score);
                sb.append("\t");
                sb.append(strand == null ? "." : strand);
                sb.append("\t");
                sb.append(thickStart);
                sb.append("\t");
                sb.append(thickEnd);
                sb.append("\t");
                sb.append(itemRgb == null ? "0" : itemRgb);
                sb.append("\t");
                sb.append(blockCount);
                sb.append("\t");
                sb.append(Joiner.on(",").join(Longs.asList(blockSizes)));
                sb.append("\t");
                sb.append(Joiner.on(",").join(Longs.asList(blockStarts)));
                break;
            default:
                break;
        }
        return sb.toString();
    }


    /**
     * Parse the specified value as an array of longs.
     *
     * @param value value to parse
     * @return the specified value parsed as an array of longs
     */
    private static long[] parseLongArray(final String value) {
        List<String> tokens = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(value);
        long[] longs = new long[tokens.size()];
        for (int i = 0, size = tokens.size(); i < size; i++) {
            longs[i] = Long.parseLong(tokens.get(i));
        }
        return longs;
    }

    /**
     * Return a new BED record parsed from the specified value.
     *
     * @param value value to parse
     * @return a new BED record parsed from the specified value
     * @throws IllegalArgumentException if the value is not valid BED[3,4,5,6,12] format
     * @throws NullPointerException if a required field is missing
     * @throws NumberFormatException if a long valued field cannot be parsed as a long 
     */
    public static BedRecord valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on('\t').trimResults().splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("value must have at least three fields (chrom, start, end)");
        }
        String chrom = tokens.get(0);
        long start = Long.parseLong(tokens.get(1));
        long end = Long.parseLong(tokens.get(2));
        if (tokens.size() == 3) {
            return new BedRecord(chrom, start, end);
        }
        else {
            String name = "".equals(tokens.get(3)) ? "." : tokens.get(3);
            if (tokens.size() == 4) {
                return new BedRecord(chrom, start, end, name);
            }
            else {
                int score = Integer.parseInt("".equals(tokens.get(4)) ? "0" : tokens.get(4));
                if (tokens.size() == 5) {
                    return new BedRecord(chrom, start, end, name, score);
                }
                else {
                    String strand = "".equals(tokens.get(5)) ? "." : tokens.get(5);
                    if (tokens.size() == 6) {
                        return new BedRecord(chrom, start, end, name, score, strand);
                    }
                    if (tokens.size() != 12) {
                        throw new IllegalArgumentException("value is not in BED3, BED4, BED5, BED6 or BED12 format");
                    }
                    long thickStart = Long.parseLong(tokens.get(6));
                    long thickEnd = Long.parseLong(tokens.get(7));
                    String itemRgb = "".equals(tokens.get(8)) ? "0" : tokens.get(8);
                    int blockCount = Integer.parseInt(tokens.get(9));
                    long[] blockSizes = parseLongArray(tokens.get(10));
                    long[] blockStarts = parseLongArray(tokens.get(11));

                    return new BedRecord(chrom, start, end, name, score, strand, thickStart, thickEnd, itemRgb, blockCount, blockSizes, blockStarts);
                }
            }
        }
    }
}
