/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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

import static org.dishevelled.bio.alignment.sam.SamReader.header;
import static org.dishevelled.bio.alignment.sam.SamReader.stream;
import static org.dishevelled.bio.alignment.sam.SamReader.records;

import static org.apache.commons.codec.binary.Hex.decodeHex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;

import java.nio.CharBuffer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.google.common.primitives.Bytes;

import org.dishevelled.bio.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for SamReader.
 *
 * @author  Michael Heuer
 */
public final class SamReaderTest {
    private Readable readable;
    private Readable emptyReadable;
    private SamListener listener;
    private static final String SAM = "NA12878-platinum-chr20.1-60250.sam";

    @Before
    public void setUp() {
        readable = CharBuffer.wrap("@HD\tVN:1.5\tSO:coordinate");
        emptyReadable = CharBuffer.wrap("");
        listener = new SamAdapter() {};
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream((Readable) null, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, listener);
    }

    @Test
    public void testStreamFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(createFile(SAM)))) {
            stream(reader, new SamAdapter() {
                    @Override
                    public boolean record(final SamRecord record) {
                        return validateRecord(record);
                    }
                });
        }
    }

    @Test(expected=NullPointerException.class)
    public void testHeaderNullReadable() throws Exception {
        header((Readable) null);
    }

    @Test
    public void testHeaderEmptyReadable() throws Exception {
        SamHeader header = header(emptyReadable);
        assertFalse(header.getHeaderLineOpt().isPresent());
        assertTrue(header.getSequenceHeaderLines().isEmpty());
        assertTrue(header.getReadGroupHeaderLines().isEmpty());
        assertTrue(header.getProgramHeaderLines().isEmpty());
        assertTrue(header.getCommentHeaderLines().isEmpty());
    }

    @Test
    public void testHeader() throws Exception {
        SamHeader header = header(readable);
        assertTrue(header.getHeaderLineOpt().isPresent());
        SamHeaderLine headerLine = header.getHeaderLineOpt().get();
        assertEquals("1.5", headerLine.getVn());
        assertEquals("coordinate", headerLine.getSo());
        assertFalse(headerLine.containsGo());
        assertFalse(headerLine.getAnnotations().containsKey("GO"));
        assertTrue(header.getSequenceHeaderLines().isEmpty());
        assertTrue(header.getReadGroupHeaderLines().isEmpty());
        assertTrue(header.getProgramHeaderLines().isEmpty());
        assertTrue(header.getCommentHeaderLines().isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testHeadeNullFile() throws Exception {
        header((File) null);
    }

    @Test
    public void testHeaderFile() throws Exception {
        validateHeader(header(createFile(SAM)));
    }

    @Test(expected=NullPointerException.class)
    public void testHeaderNullUrl() throws Exception {
        header((URL) null);
    }

    @Test
    public void testHeaderUrl() throws Exception {
        validateHeader(header(createUrl(SAM)));
    }

    @Test(expected=NullPointerException.class)
    public void testHeaderNullInputStream() throws Exception {
        header((InputStream) null);
    }

    @Test
    public void testHeaderInputStream() throws Exception {
        validateHeader(header(createInputStream(SAM)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullReadable() throws Exception {
        records((Readable) null);
    }

    @Test
    public void testRecordsEmptyReadable() throws Exception {
        records(emptyReadable);
    }

    @Test
    public void testRecords() throws Exception {
        Iterable<SamRecord> records = records(readable);
        assertNotNull(records);
        assertTrue(ImmutableList.copyOf(records).isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullFile() throws Exception {
        records((File) null);
    }

    @Test
    public void testRecordsFile() throws Exception {
        validateRecords(records(createFile(SAM)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullUrl() throws Exception {
        records((URL) null);
    }

    @Test
    public void testRecordsUrl() throws Exception {
        validateRecords(records(createUrl(SAM)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullInputStream() throws Exception {
        records((InputStream) null);
    }

    @Test
    public void testRecordsInputStream() throws Exception {
        validateRecords(records(createInputStream(SAM)));
    }

    @Test
    public void testRecordsTags() throws Exception {
        for (SamRecord record : records(createInputStream("tags.sam"))) {
            if ("StandardTags".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XS:A:-
                assertEquals(0, record.getAnnotationInteger("NM"));
                assertEquals("10", record.getAnnotationString("MD"));
                assertEquals('-', record.getAnnotationCharacter("XS"));
            }
            else if ("MDTagWithEdits".equals(record.getQname())) {
                // NM:i:2  MD:Z:3G4T1
                assertEquals(2, record.getAnnotationInteger("NM"));
                assertEquals("3G4T1", record.getAnnotationString("MD"));
            }
            else if ("HexByteArray".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:H:010203
                assertEquals(0, record.getAnnotationInteger("NM"));
                assertEquals("10", record.getAnnotationString("MD"));
                assertEquals("010203", record.getAnnotationString("XB"));
                assertTrue(Arrays.equals(decodeHex("010203"), record.getAnnotationByteArray("XB")));
                assertEquals(Bytes.asList(decodeHex("010203")), record.getAnnotationBytes("XB"));
            }
            else if ("LengthOneArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:c,1  XI:B:i,1  XS:B:s,1  XF:B:f,1
                assertEquals(0, record.getAnnotationInteger("NM"));
                assertEquals("10", record.getAnnotationString("MD"));
                assertEquals(ImmutableList.of(1), record.getAnnotationIntegers("XB"));
                assertEquals(ImmutableList.of(1), record.getAnnotationIntegers("XI"));
                assertEquals(ImmutableList.of(1), record.getAnnotationIntegers("XS"));
                assertEquals(ImmutableList.of(1.0f), record.getAnnotationFloats("XF"));
            }
            else if ("LongerArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:c,1,2,3  XI:B:i,1,2,3  XS:B:s,1,2,3  XS:B:f,1,2,3
                assertEquals(0, record.getAnnotationInteger("NM"));
                assertEquals("10", record.getAnnotationString("MD"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getAnnotationIntegers("XB"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getAnnotationIntegers("XI"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getAnnotationIntegers("XS"));
                assertEquals(ImmutableList.of(1.0f, 2.0f, 3.0f), record.getAnnotationFloats("XF"));
            }
            else if ("SignedArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:c,-1  XI:B:i,-1  XS:B:s,-1
                assertEquals(0, record.getAnnotationInteger("NM"));
                assertEquals("10", record.getAnnotationString("MD"));
                assertEquals(ImmutableList.of(-1), record.getAnnotationIntegers("XB"));
                assertEquals(ImmutableList.of(-1), record.getAnnotationIntegers("XI"));
                assertEquals(ImmutableList.of(-1), record.getAnnotationIntegers("XS"));
            }
            else if ("UnsignedArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:C,1,2,3  XI:B:I,1,2,3  XS:B:S,1,2,3
                assertEquals(0, record.getAnnotationInteger("NM"));
                assertEquals("10", record.getAnnotationString("MD"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getAnnotationIntegers("XB"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getAnnotationIntegers("XI"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getAnnotationIntegers("XS"));
            }
        }
    }

    @Test
    public void testRoundTrip() throws Exception {
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

        SamRecord expected = new SamRecord("ERR194147.765130386",
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

        File tmp = File.createTempFile("samReaderTest", ".sam");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            SamWriter.writeRecord(expected, writer);
        }
        SamRecord observed = records(tmp).iterator().next();

        assertEquals(expected.getQname(), observed.getQname());
        assertEquals(expected.getFlag(), observed.getFlag());
        assertEquals(expected.getRname(), observed.getRname());
        assertEquals(expected.getPos(), observed.getPos());
        assertEquals(expected.getMapq(), observed.getMapq());
        assertEquals(expected.getCigar(), observed.getCigar());
        assertEquals(expected.getRnext(), observed.getRnext());
        assertEquals(expected.getPnext(), observed.getPnext());
        assertEquals(expected.getTlen(), observed.getTlen());
        assertEquals(expected.getSeq(), observed.getSeq());
        assertEquals(expected.getQual(), observed.getQual());
        assertEquals(expected.getAnnotations(), observed.getAnnotations());

        assertEquals("B", observed.getAnnotations().get("ZB").getType());
        assertEquals("i", observed.getAnnotations().get("ZB").getArrayType());
        assertEquals(Integer.valueOf(1), observed.getAnnotationIntegers("ZB").get(0));
        assertEquals(Integer.valueOf(2), observed.getAnnotationIntegers("ZB").get(1));

        assertEquals("B", observed.getAnnotations().get("ZT").getType());
        assertEquals("f", observed.getAnnotations().get("ZT").getArrayType());
        assertEquals(Float.valueOf(3.4f), observed.getAnnotationFloats("ZT").get(0));
        assertEquals(Float.valueOf(4.5f), observed.getAnnotationFloats("ZT").get(1));
    }

    @Test
    public void testRoundTripWholeFile() throws Exception {
        SamHeader expectedHeader = header(createFile(SAM));
        Iterable<SamRecord> expectedRecords = records(createFile(SAM));
        SamRecord expectedRecord = expectedRecords.iterator().next();

        File tmp = File.createTempFile("samReaderTest", ".sam");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            SamWriter.write(expectedHeader, expectedRecords, writer);
        }

        SamHeader observedHeader = header(tmp);
        assertTrue(observedHeader.getHeaderLineOpt().isPresent());
        assertEquals(expectedHeader.getSequenceHeaderLines().size(), observedHeader.getSequenceHeaderLines().size());
        assertEquals(expectedHeader.getReadGroupHeaderLines().size(), observedHeader.getReadGroupHeaderLines().size());
        assertEquals(expectedHeader.getProgramHeaderLines().size(), observedHeader.getProgramHeaderLines().size());
        assertEquals(expectedHeader.getCommentHeaderLines().size(), observedHeader.getCommentHeaderLines().size());

        Iterable<SamRecord> observedRecords = records(tmp);
        assertEquals(Iterables.size(expectedRecords), Iterables.size(observedRecords));
    }

    private static void validateHeader(final SamHeader header) {
        assertNotNull(header);

        assertTrue(header.getHeaderLineOpt().isPresent());
        SamHeaderLine headerLine = header.getHeaderLineOpt().get();
        assertEquals("1.5", headerLine.getVn());
        assertEquals("coordinate", headerLine.getSo());
        assertFalse(headerLine.containsGo());
        assertFalse(headerLine.getAnnotations().containsKey("GO"));

        assertFalse(header.getSequenceHeaderLines().isEmpty());
        for (SamSequenceHeaderLine sequenceHeaderLine : header.getSequenceHeaderLines()) {
            if ("chr6".equals(sequenceHeaderLine.getSn())) {
                assertEquals("170805979", sequenceHeaderLine.getLn());
                assertFalse(sequenceHeaderLine.containsSp());
                assertFalse(sequenceHeaderLine.getAnnotations().containsKey("SP"));
            }
        }

        assertFalse(header.getReadGroupHeaderLines().isEmpty());
        assertEquals(1, header.getReadGroupHeaderLines().size());
        SamReadGroupHeaderLine readGroupHeaderLine = header.getReadGroupHeaderLines().get(0);
        assertEquals("NA12878-1", readGroupHeaderLine.getId());
        assertEquals("NA12878-1", readGroupHeaderLine.getSm());
        assertEquals("NA12878-1", readGroupHeaderLine.getPu());
        assertEquals("illumina", readGroupHeaderLine.getPl());

        assertFalse(header.getProgramHeaderLines().isEmpty());
        for (SamProgramHeaderLine programHeaderLine : header.getProgramHeaderLines()) {
            if ("bwa_3".equals(programHeaderLine.getId())) {
                assertEquals("bwa", programHeaderLine.getPn());
                assertEquals("0.7.15-r1140", programHeaderLine.getVn());
            }
        }

        assertFalse(header.getCommentHeaderLines().isEmpty());
        for (SamCommentHeaderLine commentHeaderLine : header.getCommentHeaderLines()) {
            if ("all".equals(commentHeaderLine.getAnnotation("ST")) && "all".equals(commentHeaderLine.getAnnotation("PA"))) {
                assertEquals("checksum", commentHeaderLine.getAnnotation("TY"));
                assertEquals("crc32prod", commentHeaderLine.getAnnotation("HA"));
            }
        }
    }

    private static boolean validateRecord(final SamRecord record) {
        assertNotNull(record);

        if ("ERR194147.765130386".equals(record.getQname())) {
            assertEquals(99, record.getFlag());
            assertEquals("chr20", record.getRname());
            assertEquals(60250, record.getPos());
            assertEquals(60, record.getMapq());
            assertEquals("101M", record.getCigar());
            assertEquals("=", record.getRnext());
            assertEquals(60512, record.getPnext());
            assertEquals(363, record.getTlen());
            assertEquals("ACTCCATCCCATTCCATTCCACTCCCTTCATTTCCATTCCAGTCCATTCCATTCCATTCCATTCCATTCCACTCCACTCCATTCCATTCCACTGCACTCCA", record.getSeq());
            assertEquals("CCCFFFFFHHHHHJJJJJJJJJJJJJJJJJJJJJJJJJIJJJJJJIIJJJJJJJJJJJJJHIIJIJGIJJJJJJJJJJJJJIJJJJJJJJJJJGGHHHFF@", record.getQual());
            assertEquals(0, record.getAnnotationInteger("NM"));
            assertEquals("101", record.getAnnotationString("MD"));
            assertEquals(101, record.getAnnotationInteger("AS"));
            assertEquals(55, record.getAnnotationInteger("XS"));
            assertEquals("NA12878-1", record.getAnnotationString("RG"));
            assertEquals(60, record.getAnnotationInteger("MQ"));
            assertEquals(3614, record.getAnnotationInteger("ms"));
            assertEquals(60612, record.getAnnotationInteger("mc"));
            assertEquals("101M", record.getAnnotationString("MC"));
        }
        return true;
    }

    private static void validateRecords(final Iterable<SamRecord> records) {
        assertNotNull(records);

        int count = 0;
        for (SamRecord record : records) {
            validateRecord(record);
            count++;
        }
        assertEquals(180, count);
    }

    private static URL createUrl(final String name) {
        return SamReaderTest.class.getResource(name);
    }

    private static InputStream createInputStream(final String name) {
        return SamReaderTest.class.getResourceAsStream(name);
    }

    private static File createFile(final String name) throws IOException {
        File file = File.createTempFile("samReaderTest", ".sam");
        Files.write(Resources.toByteArray(SamReaderTest.class.getResource(name)), file);
        file.deleteOnExit();
        return file;
    }
}
