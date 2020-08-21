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

import static org.dishevelled.bio.alignment.paf.PafWriter.write;
import static org.dishevelled.bio.alignment.paf.PafWriter.writeRecord;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for PafWriter.
 *
 * @author  Michael Heuer
 */
public final class PafWriterTest {
    private PafRecord record;
    private List<PafRecord> records;
    private ByteArrayOutputStream outputStream;
    private PrintWriter writer;
    private static final String PAF_RECORD = "query\t100\t10\t20\t-\ttarget\t200\t20\t30\t42\t10\t32";

    @Before
    public void setUp() {
        record = PafRecord.builder()
            .withLineNumber(1)
            .withQueryName("query")
            .withQueryLength(100L)
            .withQueryStart(10L)
            .withQueryEnd(20L)
            .withStrand('-')
            .withTargetName("target")
            .withTargetLength(200L)
            .withTargetStart(20L)
            .withTargetEnd(30L)
            .withMatches(42L)
            .withAlignmentBlockLength(10L)
            .withMappingQuality(32)
            .withField("NM", "i", "0")
            .withField("MD", "Z", "101")
            .withField("AS", "i", "101")
            .withField("XS", "i", "55")
            .withField("RG", "Z", "NA12878-1")
            .withField("MQ", "i", "60")
            .withField("ms", "i", "3614")
            .withField("mc", "i", "60612")
            .withField("MC", "Z", "101M")
            .withArrayField("ZB", "B", "i", "1", "2")
            .withArrayField("ZT", "B", "f", "3.4", "4.5")
            .build();

        records = ImmutableList.of(record);
        outputStream = new ByteArrayOutputStream();
        writer = new PrintWriter(outputStream);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullRecords() {
        write(null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullPrintWriter() {
        write(records, null);
    }

    @Test
    public void testWrite() {
        write(records, writer);
        writer.close();
        String paf = outputStream.toString();
        assertTrue(paf.startsWith(PAF_RECORD));
        assertTrue(paf.contains("NM:i:0"));
        assertTrue(paf.contains("MD:Z:101"));
        assertTrue(paf.contains("AS:i:101"));
        assertTrue(paf.contains("XS:i:55"));
        assertTrue(paf.contains("RG:Z:NA12878-1"));
        assertTrue(paf.contains("MQ:i:60"));
        assertTrue(paf.contains("ms:i:3614"));
        assertTrue(paf.contains("mc:i:60612"));
        assertTrue(paf.contains("MC:Z:101M"));
        assertTrue(paf.contains("ZB:B:i,1,2"));
        assertTrue(paf.contains("ZT:B:f,3.4,4.5"));
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullRecord() {
        writeRecord(null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullPrintWriter() {
        writeRecord(record, null);
    }

    @Test
    public void testWriteRecord() {
        writeRecord(record, writer);
        writer.close();
        String paf = outputStream.toString();
        assertTrue(paf.startsWith(PAF_RECORD));
        assertTrue(paf.contains("NM:i:0"));
        assertTrue(paf.contains("MD:Z:101"));
        assertTrue(paf.contains("AS:i:101"));
        assertTrue(paf.contains("XS:i:55"));
        assertTrue(paf.contains("RG:Z:NA12878-1"));
        assertTrue(paf.contains("MQ:i:60"));
        assertTrue(paf.contains("ms:i:3614"));
        assertTrue(paf.contains("mc:i:60612"));
        assertTrue(paf.contains("MC:Z:101M"));
        assertTrue(paf.contains("ZB:B:i,1,2"));
        assertTrue(paf.contains("ZT:B:f,3.4,4.5"));
    }
}
