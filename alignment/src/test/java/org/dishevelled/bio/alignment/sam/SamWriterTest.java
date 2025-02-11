/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for SamWriter.
 *
 * @author  Michael Heuer
 */
public final class SamWriterTest {
    private SamHeader header;
    private SamRecord record;
    private List<SamRecord> records;
    private ByteArrayOutputStream outputStream;
    private PrintWriter writer;
    private static final String SAM_HEADER = "@HD\tVN:1.5\tSO:coordinate";
    private static final String SAM_RECORD = "ERR194147.765130386\t99\tchr20\t60250\t60\t101M\t=\t60512\t363\tACTCCATCCCATTCCATTCCACTCCCTTCATTTCCATTCCAGTCCATTCCATTCCATTCCATTCCATTCCACTCCACTCCATTCCATTCCACTGCACTCCA\tCCCFFFFFHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJIJJJJJJIIJJJJJJJJJJJJJHIIJIJGIJJJJJJJJJJJJJIJJJJJJJJJJJGGHHHFF@";

    @Before
    public void setUp() {
        header = SamHeader.builder()
            .withHeaderLine(SamHeaderLine.valueOf(SAM_HEADER))
            .build();

        Map<String, Annotation> annotations = ImmutableMap.<String, Annotation>builder()
            .put("NM", Annotation.valueOf("NM:i:0"))
            .put("MD", Annotation.valueOf("MD:Z:101"))
            .put("AS", Annotation.valueOf("AS:i:101"))
            .put("XS", Annotation.valueOf("XS:i:55"))
            .put("RG", Annotation.valueOf("RG:Z:NA12878-1"))
            .put("MQ", Annotation.valueOf("MQ:i:60"))
            .put("ms", Annotation.valueOf("ms:i:3614"))
            .put("mc", Annotation.valueOf("mc:i:60612"))
            .put("MC", Annotation.valueOf("MC:Z:101M"))
            .put("ZB", Annotation.valueOf("ZB:B:i,1,2"))
            .put("ZT", Annotation.valueOf("ZT:B:f,3.4,4.5"))
            .build();

        record = new SamRecord("ERR194147.765130386",
                               99,
                               "chr20",
                               60250,
                               60,
                               "101M",
                               "=",
                               60512,
                               363,
                               "ACTCCATCCCATTCCATTCCACTCCCTTCATTTCCATTCCAGTCCATTCCATTCCATTCCATTCCATTCCACTCCACTCCATTCCATTCCACTGCACTCCA",
                               "CCCFFFFFHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJIJJJJJJIIJJJJJJJJJJJJJHIIJIJGIJJJJJJJJJJJJJIJJJJJJJJJJJGGHHHFF@",
                               annotations);

        records = ImmutableList.of(record);
        outputStream = new ByteArrayOutputStream();
        writer = new PrintWriter(outputStream);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullHeaders() {
        write(null, records, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullRecords() {
        write(header, null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullPrintWriter() {
        write(header, records, null);
    }

    @Test
    public void testWrite() {
        write(header, records, writer);
        writer.close();
        String sam = outputStream.toString();
        assertTrue(sam.startsWith(SAM_HEADER));
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
