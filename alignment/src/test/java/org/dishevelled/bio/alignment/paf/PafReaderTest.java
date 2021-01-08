/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.paf;

import static org.dishevelled.bio.alignment.paf.PafReader.read;
import static org.dishevelled.bio.alignment.paf.PafReader.stream;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.google.common.primitives.Bytes;

import org.dishevelled.bio.annotation.Annotation;

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
    private PafListener pafListener;
    private static final String PAF = "simple.paf"; // todo

    @Before
    public void setUp() {
        readable = CharBuffer.wrap(""); // todo
        emptyReadable = CharBuffer.wrap("");
        pafListener = new PafListener() {
                @Override
                public boolean record(final PafRecord record) {
                    return true;
                }
            };
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream((Readable) null, pafListener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, pafListener);
    }

    @Test
    public void testStreamFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(createFile(PAF)))) {
            stream(reader, new PafListener() {
                    @Override
                    public boolean record(final PafRecord record) {
                        return validateRecord(record);
                    }
                });
        }
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullReadable() throws Exception {
        read((Readable) null);
    }

    @Test
    public void testReadEmptyReadable() throws Exception {
        read(emptyReadable);
    }

    @Test
    public void testRead() throws Exception {
        Iterable<PafRecord> records = read(readable);
        assertNotNull(records);
        assertTrue(ImmutableList.copyOf(records).isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullFile() throws Exception {
        read((File) null);
    }

    @Test
    public void testReadFile() throws Exception {
        validateRecords(read(createFile(PAF)));
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullUrl() throws Exception {
        read((URL) null);
    }

    @Test
    public void testReadUrl() throws Exception {
        validateRecords(read(createUrl(PAF)));
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullInputStream() throws Exception {
        read((InputStream) null);
    }

    @Test
    public void testReadInputStream() throws Exception {
        validateRecords(read(createInputStream(PAF)));
    }

    @Test
    public void testRoundTrip() throws Exception {
        Map<String, Annotation> annotations = new HashMap<String, Annotation>();
        annotations.put("NM", Annotation.valueOf("NM:i:0"));
        annotations.put("MD", Annotation.valueOf("MD:Z:101"));
        annotations.put("AS", Annotation.valueOf("AS:i:101"));
        annotations.put("XS", Annotation.valueOf("XS:i:55"));
        annotations.put("RG", Annotation.valueOf("RG:Z:NA12878-1"));
        annotations.put("MQ", Annotation.valueOf("MQ:i:60"));
        annotations.put("ms", Annotation.valueOf("ms:i:3614"));
        annotations.put("mc", Annotation.valueOf("mc:i:60612"));
        annotations.put("MC", Annotation.valueOf("MC:Z:101M"));
        annotations.put("ZB", Annotation.valueOf("ZB:B:i1,2"));
        annotations.put("ZT", Annotation.valueOf("ZT:B:f3.4,4.5"));

        PafRecord expected = new PafRecord("query",
                                           100L,
                                           10L,
                                           20L,
                                           '-',
                                           "target",
                                           200L,
                                           20L,
                                           30L,
                                           42L,
                                           10L,
                                           32,
                                           annotations);

        File tmp = File.createTempFile("pafReaderTest", ".paf");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            PafWriter.write(expected, writer);
        }
        PafRecord observed = read(tmp).iterator().next();

        assertEquals(expected.getQueryName(), observed.getQueryName());
    }

    @Test
    public void testRoundTripWholeFile() throws Exception {
        Iterable<PafRecord> expectedRecords = read(createFile(PAF));
        PafRecord expectedRecord = expectedRecords.iterator().next();

        File tmp = File.createTempFile("pafReaderTest", ".paf");
        tmp.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(new FileWriter(tmp))) {
            PafWriter.write(expectedRecords, writer);
        }

        Iterable<PafRecord> observedRecords = read(tmp);
        assertEquals(Iterables.size(expectedRecords), Iterables.size(observedRecords));
    }

    private static boolean validateRecord(final PafRecord record) {
        assertNotNull(record);
        return true;
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
