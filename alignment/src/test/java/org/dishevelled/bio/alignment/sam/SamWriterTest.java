/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
package org.dishevelled.bio.alignment.sam;

import static org.dishevelled.bio.alignment.sam.SamWriter.write;
import static org.dishevelled.bio.alignment.sam.SamWriter.writeRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for SamWriter.
 *
 * @author  Michael Heuer
 */
public final class SamWriterTest {
    private SamRecord record;
    private List<SamRecord> records;
    private ByteArrayOutputStream outputStream;
    private PrintWriter writer;
    private static final String SAM_RECORD = "ERR194147.765130386\t99\tchr20\t60250\t60\t101M\t=\t60512\t363\tACTCCATCCCATTCCATTCCACTCCCTTCATTTCCATTCCAGTCCATTCCATTCCATTCCATTCCATTCCACTCCACTCCATTCCATTCCACTGCACTCCA\tCCCFFFFFHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJIJJJJJJIIJJJJJJJJJJJJJHIIJIJGIJJJJJJJJJJJJJIJJJJJJJJJJJGGHHHFF@";

    @Before
    public void setUp() throws Exception {
        record = SamRecord.builder()
            .withQname("ERR194147.765130386")
            .withFlag(99)
            .withRname("chr20")
            .withPos(60250)
            .withMapq(60)
            .withCigar("101M")
            .withRnext("=")
            .withPnext(60512)
            .withTlen(363)
            .withSeq("ACTCCATCCCATTCCATTCCACTCCCTTCATTTCCATTCCAGTCCATTCCATTCCATTCCATTCCATTCCACTCCACTCCATTCCATTCCACTGCACTCCA")
            .withQual("CCCFFFFFHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJIJJJJJJIIJJJJJJJJJJJJJHIIJIJGIJJJJJJJJJJJJJIJJJJJJJJJJJGGHHHFF@")
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
    public void testWriteNullRecords() throws Exception {
        write(null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullPrintWriter() throws Exception {
        write(records, null);
    }

    @Test
    public void testWrite() throws Exception {
        write(records, writer);
        writer.close();
        String sam = outputStream.toString();
        assertTrue(sam.startsWith(SAM_RECORD));
        assertTrue(sam.contains("NM:i:0"));
        assertTrue(sam.contains("MD:Z:101"));
        assertTrue(sam.contains("AS:i:101"));
        assertTrue(sam.contains("XS:i:55"));
        assertTrue(sam.contains("RG:Z:NA12878-1"));
        assertTrue(sam.contains("MQ:i:60"));
        assertTrue(sam.contains("ms:i:3614"));
        assertTrue(sam.contains("mc:i:60612"));
        assertTrue(sam.contains("MC:Z:101M"));
        assertTrue(sam.contains("ZB:B:i,1,2"));
        assertTrue(sam.contains("ZT:B:f,3.4,4.5"));
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullRecord() throws Exception {
        writeRecord(null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullPrintWriter() throws Exception {
        writeRecord(record, null);
    }

    @Test
    public void testWriteRecord() throws Exception {
        writeRecord(record, writer);
        writer.close();
        String sam = outputStream.toString();
        assertTrue(sam.startsWith(SAM_RECORD));
        assertTrue(sam.contains("NM:i:0"));
        assertTrue(sam.contains("MD:Z:101"));
        assertTrue(sam.contains("AS:i:101"));
        assertTrue(sam.contains("XS:i:55"));
        assertTrue(sam.contains("RG:Z:NA12878-1"));
        assertTrue(sam.contains("MQ:i:60"));
        assertTrue(sam.contains("ms:i:3614"));
        assertTrue(sam.contains("mc:i:60612"));
        assertTrue(sam.contains("MC:Z:101M"));
        assertTrue(sam.contains("ZB:B:i,1,2"));
        assertTrue(sam.contains("ZT:B:f,3.4,4.5"));
    }
}