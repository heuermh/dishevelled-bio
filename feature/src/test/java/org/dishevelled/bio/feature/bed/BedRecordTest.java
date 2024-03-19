/*

    dsh-bio-feature  Sequence features.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.dishevelled.bio.feature.bed.BedRecord.valueOf;

import com.google.common.collect.Range;

import org.junit.Test;

/**
 * Unit test for BedRecord.
 *
 * @author  Michael Heuer
 */
public final class BedRecordTest {

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooShort() {
        valueOf("chr1\t11873");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooLong() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,\thi mom");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfStartNumberFormatException() {
        valueOf("chr1\tnot a number\t14409");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfEndNumberFormatException() {
        valueOf("chr1\t11873\tnot a number");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfStartLessThanZero() {
        valueOf("chr1\t-1\t14409");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfEndLessThanZero() {
        valueOf("chr1\t11873\t-1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfEndLessThanStart() {
        valueOf("chr1\t11873\t11870");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickStartLessThanZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t-1\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickStartLessThanStart() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t10873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickStartGreaterThanEnd() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t15409\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickEndLessThanZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t-1\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickEndLessThanThickStart() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11872\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStrand() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfBlockCountLessThanZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t-1\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfBlockSizesNumberFormatException() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,not a number,1189,\t0,739,1347,");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfBlockStartsNumberFormatException() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,not a number,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooManyBlockSizes() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,42,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooManyBlockStarts() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,42,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testScoreTooLow() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t-1\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testScoreTooHigh() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t1001\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testScoreInvalid() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\tinvalid\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testItemRgbInvalid() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\tinvalid\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBED12BlockCountZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t0\t\t");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBED12FirstBlockStart() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t353,109,1189,\t1,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBED12LastBlockEnd() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1188,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBED12BlockOverlap() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1689,\t0,739,847,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBED12BlocksOutOfOrder() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t4\t354,100,100,1189,\t0,900,700,1347,");
    }

    @Test
    public void testEquals() {
        BedRecord record1 = valueOf("chr1\t11873\t14409");
        BedRecord record2 = valueOf("chr1\t11873\t14409\tuc001aaa.3");
        assertTrue(record1.equals(record1));
        assertTrue(record2.equals(record2));
        assertFalse(record1.equals(record2));
        assertFalse(record2.equals(record1));
        assertFalse(record1.equals(new Object()));
    }

    @Test
    public void testValueOfBED3() {
        BedRecord record = valueOf("chr1\t11873\t14409");
        assertEquals("chr1", record.getChrom());
        assertEquals(11873L, record.getStart());
        assertEquals(14409L, record.getEnd());
        assertEquals(BedFormat.BED3, record.getFormat());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409", record.toString());
    }

    @Test
    public void testValueOfBED4() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3");
        assertEquals("chr1", record.getChrom());
        assertEquals(11873L, record.getStart());
        assertEquals(14409L, record.getEnd());
        assertEquals("uc001aaa.3", record.getName());
        assertEquals(BedFormat.BED4, record.getFormat());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3", record.toString());
    }

    @Test
    public void testValueOfBED5() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0");
        assertEquals("chr1", record.getChrom());
        assertEquals(11873L, record.getStart());
        assertEquals(14409L, record.getEnd());
        assertEquals("uc001aaa.3", record.getName());
        assertEquals(0, record.getScore());
        assertEquals(BedFormat.BED5, record.getFormat());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0", record.toString());
    }

    @Test
    public void testValueOfBED6() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+");
        assertEquals("chr1", record.getChrom());
        assertEquals(11873L, record.getStart());
        assertEquals(14409L, record.getEnd());
        assertEquals("uc001aaa.3", record.getName());
        assertEquals(0, record.getScore());
        assertEquals("+", record.getStrand());
        assertEquals(BedFormat.BED6, record.getFormat());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0\t+", record.toString());
    }

    @Test
    public void testValueOfBED12() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
        assertEquals("chr1", record.getChrom());
        assertEquals(11873L, record.getStart());
        assertEquals(14409L, record.getEnd());
        assertEquals("uc001aaa.3", record.getName());
        assertEquals(0, record.getScore());
        assertEquals("+", record.getStrand());
        assertEquals(11873L, record.getThickStart());
        assertEquals(11873L, record.getThickEnd());
        assertEquals("0", record.getItemRgb());
        assertEquals(3, record.getBlockCount());
        assertEquals(3, record.getBlockSizes().length);
        assertEquals(354L, record.getBlockSizes()[0]);
        assertEquals(109L, record.getBlockSizes()[1]);
        assertEquals(1189L, record.getBlockSizes()[2]);
        assertEquals(3, record.getBlockStarts().length);
        assertEquals(0L, record.getBlockStarts()[0]);
        assertEquals(739L, record.getBlockStarts()[1]);
        assertEquals(1347L, record.getBlockStarts()[2]);
        assertEquals(BedFormat.BED12, record.getFormat());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189\t0,739,1347", record.toString());
    }

    @Test
    public void testValueOfBED12DefaultName() {
        BedRecord record = valueOf("chr1\t11873\t14409\t\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
        assertEquals(".", record.getName());
    }

    @Test
    public void testValueOfBED12DefaultStrand() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
        assertEquals(".", record.getStrand());
    }

    @Test
    public void testValueOfBED12DefaultScore() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
        assertEquals(0, record.getScore());
    }

    @Test
    public void testValueOfBED12DefaultItemRgb() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t\t3\t354,109,1189,\t0,739,1347,");
        assertEquals("0", record.getItemRgb());
    }

    @Test
    public void testValueOfBED12ValidItemRgb() {
        BedRecord record = valueOf("chr19\t250275\t250322\tname7\t902\t-\t250276\t250321\t128,128,0\t2\t10,10\t0,37");
        assertEquals("128,128,0", record.getItemRgb());
    }

    @Test
    public void testValueOfBed12LeadingZerosItemRgb() {
        BedRecord record = valueOf("chr19\t250131\t250167\tname3\t914\t-\t250132\t250166\t000,000,000\t2\t10,10\t0,26");
        assertEquals("0,0,0", record.getItemRgb());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfBed12InvalidItemRgb() {
        valueOf("chr19\t250000\t250036\tname1\t889\t+\t250001\t250035\t256,128,0\t2\t10,10\t0,26");
    }
}
