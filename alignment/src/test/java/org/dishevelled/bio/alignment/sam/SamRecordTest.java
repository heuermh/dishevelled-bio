/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import org.junit.Test;

/**
 * Unit test for SamRecord.
 *
 * @author  Michael Heuer
 */
public final class SamRecordTest {

    @Test(expected=NullPointerException.class)
    public void testBuilderNullRecord() {
        SamRecord.builder(null);
    }

    @Test
    public void testBuilder() {
        SamRecord.Builder builder = SamRecord.builder();
        assertNotNull(builder);

        SamRecord record = builder
            .withLineNumber(1)
            .withQname("qname")
            .withFlag(1)
            .withRname("rname")
            .withPos(42)
            .withMapq(10)
            .withCigar("8M")
            .withRnext("rnext")
            .withPnext(36)
            .withTlen(88)
            .withSeq("AATTCCGG")
            .withQual("12344321")
            .withField("ZA", "A", "c")
            .withField("ZI", "i", "42")
            .withField("ZF", "f", "3.14")
            .withField("ZZ", "Z", "hello world")
            .withArrayField("ZB", "B", "i", "1", "2")
            .withArrayField("ZT", "B", "f", "3.4", "4.5")
            .build();

        assertEquals(1, record.getLineNumber());
        assertEquals("qname", record.getQname());
        assertEquals(1, record.getFlag());
        assertEquals("rname", record.getRname());
        assertEquals(42, record.getPos());
        assertEquals(10, record.getMapq());
        assertEquals("8M", record.getCigar());
        assertEquals("rnext", record.getRnext());
        assertEquals(36, record.getPnext());
        assertEquals(88, record.getTlen());
        assertEquals("AATTCCGG", record.getSeq());
        assertEquals("12344321", record.getQual());
        assertEquals('c', record.getFieldCharacter("ZA"));
        assertEquals(42, record.getFieldInteger("ZI"));
        assertEquals(3.14f, record.getFieldFloat("ZF"), 0.1f);
        assertEquals("hello world", record.getFieldString("ZZ"));
        assertEquals(ImmutableList.of(1, 2), record.getFieldIntegers("ZB"));
        assertEquals(ImmutableList.of(3.4f, 4.5f), record.getFieldFloats("ZT"));
        assertEquals("A", record.getFieldTypes().get("ZA"));
        assertEquals("B", record.getFieldTypes().get("ZB"));
        assertEquals("i", record.getFieldArrayTypes().get("ZB"));
        assertEquals("f", record.getFieldArrayTypes().get("ZT"));
    }

    @Test
    public void testBuilderReset() {
        SamRecord record = SamRecord.builder()
            .withLineNumber(1)
            .withQname("qname")
            .withFlag(1)
            .withRname("rname")
            .withPos(42)
            .withMapq(10)
            .withCigar("8M")
            .withRnext("rnext")
            .withPnext(36)
            .withTlen(88)
            .withSeq("AATTCCGG")
            .withQual("12344321")
            .withField("ZA", "A", "c")
            .withField("ZI", "i", "42")
            .withField("ZF", "f", "3.14")
            .withField("ZZ", "Z", "hello world")
            .withArrayField("ZB", "B", "i", "1", "2")
            .withArrayField("ZT", "B", "f", "3.4", "4.5")
            .reset()
            .build();

        assertEquals(-1, record.getLineNumber());
        assertNull(record.getQname());
        assertEquals(0, record.getFlag());
        assertNull(record.getRname());
        assertEquals(0, record.getPos());
        assertEquals(255, record.getMapq());
        assertNull(record.getCigar());
        assertNull(record.getRnext());
        assertEquals(0, record.getPnext());
        assertEquals(0, record.getTlen());
        assertNull(record.getSeq());
        assertNull(record.getQual());
        assertTrue(record.getFields().isEmpty());
        assertTrue(record.getFieldTypes().isEmpty());
        assertTrue(record.getFieldArrayTypes().isEmpty());
    }

    @Test
    public void testBuilderReplaceField() {
        SamRecord record = SamRecord.builder()
            .withField("ZA", "A", "c")
            .replaceField("ZA", "A", "d")
            .build();

        assertEquals('d', record.getFieldCharacter("ZA"));
    }

    @Test
    public void testBuilderReplaceArrayField() {
        SamRecord record = SamRecord.builder()
            .withArrayField("ZB", "B", "i", "1", "2")
            .replaceArrayField("ZB", "B", "f", "3.4", "4.5")
            .build();

        assertEquals(ImmutableList.of(3.4f, 4.5f), record.getFieldFloats("ZB"));
    }

    @Test
    public void testBuilderWithFields() {
        ListMultimap<String, String> fields = ImmutableListMultimap.<String, String>builder()
            .put("ZZ", "hello world")
            .put("ZB", "1")
            .put("ZB", "2")
            .build();

        Map<String, String> fieldTypes = ImmutableMap.<String, String>builder()
            .put("ZZ", "Z")
            .put("ZB", "B")
            .build();

        Map<String, String> fieldArrayTypes = ImmutableMap.<String, String>builder()
            .put("ZB", "i")
            .build();

        SamRecord record = SamRecord.builder()
            .withField("ZA", "A", "c")
            .withFields(fields, fieldTypes, fieldArrayTypes)
            .build();

        assertEquals('c', record.getFieldCharacter("ZA"));
        assertEquals("hello world", record.getFieldString("ZZ"));
        assertEquals(ImmutableList.of(1, 2), record.getFieldIntegers("ZB"));
    }

    @Test
    public void testBuilderReplaceFields() {
        ListMultimap<String, String> fields = ImmutableListMultimap.<String, String>builder()
            .put("ZZ", "hello world")
            .put("ZB", "1")
            .put("ZB", "2")
            .build();

        Map<String, String> fieldTypes = ImmutableMap.<String, String>builder()
            .put("ZZ", "Z")
            .put("ZB", "B")
            .build();

        Map<String, String> fieldArrayTypes = ImmutableMap.<String, String>builder()
            .put("ZB", "i")
            .build();

        SamRecord record = SamRecord.builder()
            .withField("ZZ", "Z", "replace me")
            .withArrayField("ZB", "B", "f", "3.4", "4.5")
            .replaceFields(fields, fieldTypes, fieldArrayTypes)
            .build();

        assertEquals("hello world", record.getFieldString("ZZ"));
        assertEquals(ImmutableList.of(1, 2), record.getFieldIntegers("ZB"));
    }

    @Test
    public void testBuilderCopy() {
        SamRecord record = SamRecord.builder()
            .withLineNumber(1)
            .withQname("qname")
            .withFlag(1)
            .withRname("rname")
            .withPos(42)
            .withMapq(10)
            .withCigar("8M")
            .withRnext("rnext")
            .withPnext(36)
            .withTlen(88)
            .withSeq("AATTCCGG")
            .withQual("12344321")
            .withField("ZA", "A", "c")
            .withField("ZI", "i", "42")
            .withField("ZF", "f", "3.14")
            .withField("ZZ", "Z", "hello world")
            .withArrayField("ZB", "B", "i", "1", "2")
            .withArrayField("ZT", "B", "f", "3.4", "4.5")
            .build();

        SamRecord copy = SamRecord.builder(record)
            .withQname("copy of qname")
            .build();

        assertEquals("copy of qname", copy.getQname());
        assertEquals(record.getFlag(), copy.getFlag());
        assertEquals(record.getRname(), copy.getRname());
        assertEquals(record.getPos(), copy.getPos());
        assertEquals(record.getMapq(), copy.getMapq());
        assertEquals(record.getCigar(), copy.getCigar());
        assertEquals(record.getRnext(), copy.getRnext());
        assertEquals(record.getPnext(), copy.getPnext());
        assertEquals(record.getTlen(), copy.getTlen());
        assertEquals(record.getSeq(), copy.getSeq());
        assertEquals(record.getQual(), copy.getQual());
        assertEquals(record.getFields(), copy.getFields());
        assertEquals(record.getFieldTypes(), copy.getFieldTypes());
        assertEquals(record.getFieldArrayTypes(), copy.getFieldArrayTypes());
    }
}
