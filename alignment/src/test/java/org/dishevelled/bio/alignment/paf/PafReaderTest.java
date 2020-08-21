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

import static org.dishevelled.bio.alignment.paf.PafReader.parse;
import static org.dishevelled.bio.alignment.paf.PafReader.stream;
import static org.dishevelled.bio.alignment.paf.PafReader.records;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.google.common.primitives.Bytes;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for PafReader.
 *
 * @author  Michael Heuer
 */
public final class PafReaderTest {
    private Readable readable;
    private Readable emptyReadable;
    private PafParseListener parseListener;
    private PafStreamListener streamListener;
    private static final String PAF = "simple.paf"; // todo

    @Before
    public void setUp() {
        readable = CharBuffer.wrap(""); // todo
        emptyReadable = CharBuffer.wrap("");
        parseListener = new PafParseAdapter();
        streamListener = new PafStreamAdapter();
    }

    @Test(expected=NullPointerException.class)
    public void testParseNullReadable() throws Exception {
        parse(null, parseListener);
    }

    @Test(expected=NullPointerException.class)
    public void testParseNullParseListener() throws Exception {
        parse(readable, null);
    }

    @Test
    public void testParse() throws Exception {
        parse(readable, parseListener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream(null, streamListener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullStreamListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, streamListener);
    }

    @Test
    public void testStreamFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(createFile(PAF)))) {
            stream(reader, new PafStreamAdapter() {
                    @Override
                    public void record(final PafRecord record) {
                        validateRecord(record);
                    }
                });
        }
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
        Iterable<PafRecord> records = records(readable);
        assertNotNull(records);
        assertTrue(ImmutableList.copyOf(records).isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullFile() throws Exception {
        records((File) null);
    }

    @Test
    public void testRecordsFile() throws Exception {
        validateRecords(records(createFile(PAF)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullUrl() throws Exception {
        records((URL) null);
    }

    @Test
    public void testRecordsUrl() throws Exception {
        validateRecords(records(createUrl(PAF)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullInputStream() throws Exception {
        records((InputStream) null);
    }

    @Test
    public void testRecordsInputStream() throws Exception {
        validateRecords(records(createInputStream(PAF)));
    }

    /*
    @Test
    public void testRecordsTags() throws Exception {
        for (PafRecord record : records(createInputStream("tags.paf"))) {
            if ("StandardTags".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XS:A:-
                assertEquals(0, record.getFieldInteger("NM"));
                assertEquals("10", record.getFieldString("MD"));
                assertEquals('-', record.getFieldCharacter("XS"));
                assertEquals("i", record.getFieldTypes().get("NM"));
                assertEquals("Z", record.getFieldTypes().get("MD"));
                assertEquals("A", record.getFieldTypes().get("XS"));
            }
            else if ("MDTagWithEdits".equals(record.getQname())) {
                // NM:i:2  MD:Z:3G4T1
                assertEquals(2, record.getFieldInteger("NM"));
                assertEquals("3G4T1", record.getFieldString("MD"));
                assertEquals("i", record.getFieldTypes().get("NM"));
                assertEquals("Z", record.getFieldTypes().get("MD"));
            }
            else if ("HexByteArray".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:H:010203
                assertEquals(0, record.getFieldInteger("NM"));
                assertEquals("10", record.getFieldString("MD"));
                assertEquals("010203", record.getFieldString("XB"));
                assertTrue(Arrays.equals(decodeHex("010203"), record.getFieldByteArray("XB")));
                assertEquals(Bytes.asList(decodeHex("010203")), record.getFieldBytes("XB"));
                assertEquals("i", record.getFieldTypes().get("NM"));
                assertEquals("Z", record.getFieldTypes().get("MD"));
                assertEquals("H", record.getFieldTypes().get("XB"));
            }
            else if ("LengthOneArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:c,1  XI:B:i,1  XS:B:s,1  XF:B:f,1
                assertEquals(0, record.getFieldInteger("NM"));
                assertEquals("10", record.getFieldString("MD"));
                assertEquals(ImmutableList.of(1), record.getFieldIntegers("XB"));
                assertEquals(ImmutableList.of(1), record.getFieldIntegers("XI"));
                assertEquals(ImmutableList.of(1), record.getFieldIntegers("XS"));
                assertEquals(ImmutableList.of(1.0f), record.getFieldFloats("XF"));
                assertEquals("B", record.getFieldTypes().get("XB"));
                assertEquals("c", record.getFieldArrayTypes().get("XB"));
                assertEquals("B", record.getFieldTypes().get("XI"));
                assertEquals("i", record.getFieldArrayTypes().get("XI"));
                assertEquals("B", record.getFieldTypes().get("XS"));
                assertEquals("s", record.getFieldArrayTypes().get("XS"));
                assertEquals("B", record.getFieldTypes().get("XF"));
                assertEquals("f", record.getFieldArrayTypes().get("XF"));
            }
            else if ("LongerArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:c,1,2,3  XI:B:i,1,2,3  XS:B:s,1,2,3  XS:B:f,1,2,3
                assertEquals(0, record.getFieldInteger("NM"));
                assertEquals("10", record.getFieldString("MD"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getFieldIntegers("XB"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getFieldIntegers("XI"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getFieldIntegers("XS"));
                assertEquals(ImmutableList.of(1.0f, 2.0f, 3.0f), record.getFieldFloats("XF"));
                assertEquals("B", record.getFieldTypes().get("XB"));
                assertEquals("c", record.getFieldArrayTypes().get("XB"));
                assertEquals("B", record.getFieldTypes().get("XI"));
                assertEquals("i", record.getFieldArrayTypes().get("XI"));
                assertEquals("B", record.getFieldTypes().get("XS"));
                assertEquals("s", record.getFieldArrayTypes().get("XS"));
                assertEquals("B", record.getFieldTypes().get("XF"));
                assertEquals("f", record.getFieldArrayTypes().get("XF"));
            }
            else if ("SignedArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:c,-1  XI:B:i,-1  XS:B:s,-1
                assertEquals(0, record.getFieldInteger("NM"));
                assertEquals("10", record.getFieldString("MD"));
                assertEquals(ImmutableList.of(-1), record.getFieldIntegers("XB"));
                assertEquals(ImmutableList.of(-1), record.getFieldIntegers("XI"));
                assertEquals(ImmutableList.of(-1), record.getFieldIntegers("XS"));
                assertEquals("B", record.getFieldTypes().get("XB"));
                assertEquals("c", record.getFieldArrayTypes().get("XB"));
                assertEquals("B", record.getFieldTypes().get("XI"));
                assertEquals("i", record.getFieldArrayTypes().get("XI"));
                assertEquals("B", record.getFieldTypes().get("XS"));
                assertEquals("s", record.getFieldArrayTypes().get("XS"));
            }
            else if ("UnsignedArrays".equals(record.getQname())) {
                // NM:i:0  MD:Z:10  XB:B:C,1,2,3  XI:B:I,1,2,3  XS:B:S,1,2,3
                assertEquals(0, record.getFieldInteger("NM"));
                assertEquals("10", record.getFieldString("MD"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getFieldIntegers("XB"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getFieldIntegers("XI"));
                assertEquals(ImmutableList.of(1, 2, 3), record.getFieldIntegers("XS"));
                assertEquals("B", record.getFieldTypes().get("XB"));
                assertEquals("C", record.getFieldArrayTypes().get("XB"));
                assertEquals("B", record.getFieldTypes().get("XI"));
                assertEquals("I", record.getFieldArrayTypes().get("XI"));
                assertEquals("B", record.getFieldTypes().get("XS"));
                assertEquals("S", record.getFieldArrayTypes().get("XS"));
            }
        }
    }
    */

    @Test
    public void testRoundTripDefaults() throws Exception {
        PafRecord expected = PafRecord.builder()
            .withLineNumber(1)
            .build();

        File tmp = File.createTempFile("pafReaderTest", ".paf");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            PafWriter.writeRecord(expected, writer);
        }
        PafRecord observed = records(tmp).iterator().next();

        assertEquals(expected.getLineNumber(), observed.getLineNumber());
    }

    @Test
    public void testRoundTripOptionalFields() throws Exception {
        PafRecord expected = PafRecord.builder()
            .withField("ZA", "A", "c")
            .withField("ZI", "i", "42")
            .withField("ZF", "f", "3.14")
            .withField("ZH", "H", "010203")
            .withField("ZZ", "Z", "hello world!")
            .withArrayField("ZB", "B", "i", "1", "2")
            .withArrayField("ZT", "B", "f", "3.4", "4.5")
            .build();

        File tmp = File.createTempFile("pafReaderTest", ".paf");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            PafWriter.writeRecord(expected, writer);
        }
        PafRecord observed = records(tmp).iterator().next();

        assertEquals(expected.getFieldCharacter("ZA"), observed.getFieldCharacter("ZA"));
        assertEquals(expected.getFieldInteger("ZI"), observed.getFieldInteger("ZI"));
        assertEquals(expected.getFieldFloat("ZF"), observed.getFieldFloat("ZF"), 0.1f);
        assertTrue(Arrays.equals(expected.getFieldByteArray("ZH"), observed.getFieldByteArray("ZH")));
        assertEquals(expected.getFieldString("ZZ"), observed.getFieldString("ZZ"));
        assertEquals(expected.getFieldIntegers("ZB"), observed.getFieldIntegers("ZB"));
        assertEquals(expected.getFieldFloats("ZT"), observed.getFieldFloats("ZT"));
    }

    @Test
    public void testRoundTripWholeFile() throws Exception {
        Iterable<PafRecord> expectedRecords = records(createFile(PAF));
        PafRecord expectedRecord = expectedRecords.iterator().next();

        File tmp = File.createTempFile("pafReaderTest", ".paf");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            PafWriter.write(expectedRecords, writer);
        }

        Iterable<PafRecord> observedRecords = records(tmp);
        assertEquals(Iterables.size(expectedRecords), Iterables.size(observedRecords));
    }

    private static void validateRecord(final PafRecord record) {
        assertNotNull(record);

        /*
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
            assertEquals(0, record.getFieldInteger("NM"));
            assertEquals("101", record.getFieldString("MD"));
            assertEquals(101, record.getFieldInteger("AS"));
            assertEquals(55, record.getFieldInteger("XS"));
            assertEquals("NA12878-1", record.getFieldString("RG"));
            assertEquals(60, record.getFieldInteger("MQ"));
            assertEquals(3614, record.getFieldInteger("ms"));
            assertEquals(60612, record.getFieldInteger("mc"));
            assertEquals("101M", record.getFieldString("MC"));
            assertEquals("i", record.getFieldTypes().get("NM"));
            assertEquals("Z", record.getFieldTypes().get("MC"));
        }
        */
    }

    private static void validateRecords(final Iterable<PafRecord> records) {
        assertNotNull(records);

        int count = 0;
        for (PafRecord record : records) {
            validateRecord(record);
            count++;
        }
        assertEquals(1, count);
    }

    private static URL createUrl(final String name) {
        return PafReaderTest.class.getResource(name);
    }

    private static InputStream createInputStream(final String name) {
        return PafReaderTest.class.getResourceAsStream(name);
    }

    private static File createFile(final String name) throws IOException {
        File file = File.createTempFile("pafReaderTest", ".paf");
        Files.write(Resources.toByteArray(PafReaderTest.class.getResource(name)), file);
        file.deleteOnExit();
        return file;
    }
}
