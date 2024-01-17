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
        assertEquals("0", record.getScore());
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
        assertEquals("0", record.getScore());
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
        assertEquals("0", record.getScore());
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
}
