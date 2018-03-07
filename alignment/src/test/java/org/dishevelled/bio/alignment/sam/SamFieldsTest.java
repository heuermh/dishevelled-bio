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

import static org.dishevelled.bio.alignment.sam.SamFields.parseByteArray;
import static org.dishevelled.bio.alignment.sam.SamFields.parseCharacter;
import static org.dishevelled.bio.alignment.sam.SamFields.parseInteger;
import static org.dishevelled.bio.alignment.sam.SamFields.parseIntegers;
import static org.dishevelled.bio.alignment.sam.SamFields.parseFloat;
import static org.dishevelled.bio.alignment.sam.SamFields.parseFloats;
import static org.dishevelled.bio.alignment.sam.SamFields.parseString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Unit test for SamFields.
 *
 * @author  Michael Heuer
 */
public final class SamFieldsTest {
    private ListMultimap<String, String> fields;

    @Before
    public void setUp() {
        fields = ImmutableListMultimap.<String, String>builder()
            .put("ZA", "c")
            .put("ZI", "42")
            .put("ZF", "3.14")
            .put("ZH", "010203")
            .put("ZB", "1")
            .put("ZB", "2")
            .put("ZT", "3.4")
            .put("ZT", "4.5")
            .put("ZZ", "hello world")
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testParseCharacterNullKey() {
        parseCharacter(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseCharacterNullFields() {
        parseCharacter("ZA", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseCharacterEmptyValues() {
        parseCharacter("ZE", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseCharacterTooManyValues() {
        parseCharacter("ZB", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseCharacterWrongType() {
        parseCharacter("ZI", fields);
    }

    @Test
    public void testParseCharacter() {
        assertEquals('c', parseCharacter("ZA", fields));
    }

    @Test(expected=NullPointerException.class)
    public void testParseIntegerNullKey() {
        parseInteger(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseIntegerNullFields() {
        parseInteger("ZI", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegerEmptyValues() {
        parseInteger("ZE", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegerTooManyValues() {
        parseInteger("ZB", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegerWrongType() {
        parseInteger("ZA", fields);
    }

    @Test
    public void testParseInteger() {
        assertEquals(42, parseInteger("ZI", fields));
    }

    @Test(expected=NullPointerException.class)
    public void testParseFloatNullKey() {
        parseFloat(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseFloatNullFields() {
        parseFloat("ZF", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatEmptyValues() {
        parseFloat("ZE", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatTooManyValues() {
        parseFloat("ZB", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatWrongType() {
        parseFloat("ZA", fields);
    }

    @Test
    public void testParseFloat() {
        assertEquals(3.14f, parseFloat("ZF", fields), 0.1f);
    }

    @Test(expected=NullPointerException.class)
    public void testParseStringNullKey() {
        parseString(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseStringNullFields() {
        parseString("ZZ", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseStringEmptyValues() {
        parseString("ZE", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseStringTooManyValues() {
        parseString("ZB", fields);
    }

    @Test
    public void testParseStringWrongType() {
        assertEquals("3.14", parseString("ZF", fields));
    }

    @Test
    public void testParseString() {
        assertEquals("hello world", parseString("ZZ", fields));
    }

    @Test(expected=NullPointerException.class)
    public void testParseByteArrayNullKey() {
        parseByteArray(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseByteArrayNullFields() {
        parseByteArray("ZH", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseByteArrayEmptyValues() {
        parseByteArray("ZE", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseByteArrayTooManyValues() {
        parseByteArray("ZB", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseByteArrayWrongType() {
        parseByteArray("ZF", fields);
    }


    @Test(expected=NullPointerException.class)
    public void testParseIntegersNullKey() {
        parseIntegers(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseIntegersNullFields() {
        parseIntegers("ZB", null);
    }

    @Test
    public void testParseIntegersEmptyValues() {
        assertTrue(parseIntegers("ZE", fields).isEmpty());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersWrongType() {
        parseIntegers("ZZ", fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersWrongArrayType() {
        parseIntegers("ZF", fields);
    }

    @Test
    public void testParseIntegers() {
        assertEquals(ImmutableList.of(1, 2), parseIntegers("ZB", fields));
    }

    @Test(expected=NullPointerException.class)
    public void testParseFloatsNullKey() {
        parseFloats(null, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseFloatsNullFields() {
        parseFloats("ZT", null);
    }

    @Test
    public void testParseFloatsEmptyValues() {
        assertTrue(parseFloats("ZE", fields).isEmpty());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatsWrongType() {
        parseFloats("ZZ", fields);
    }

    @Test
    public void testParseFloatsWrongArrayType() {
        assertEquals(ImmutableList.of(1.0f, 2.0f), parseFloats("ZB", fields));
    }

    @Test
    public void testParseFloats() {
        assertEquals(ImmutableList.of(3.4f, 4.5f), parseFloats("ZT", fields));
    }


    @Test(expected=NullPointerException.class)
    public void testParseIntegersLengthNullKey() {
        parseIntegers(null, 2, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseIntegersLengthNullFields() {
        parseIntegers("ZB", 2, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersInvalidLength() {
        parseIntegers("ZB", -1, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersLengthEmptyValues() {
        parseIntegers("ZE", 2, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersLengthWrongType() {
        parseIntegers("ZZ", 2, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersLengthWrongArrayType() {
        parseIntegers("ZF", 2, fields);
    }

    @Test
    public void testParseIntegersLength() {
        assertEquals(ImmutableList.of(1, 2), parseIntegers("ZB", 2, fields));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersLengthTooShort() {
        parseIntegers("ZB", 1, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseIntegersLengthTooLong() {
        parseIntegers("ZB", 3, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseFloatsLengthNullKey() {
        parseFloats(null, 2, fields);
    }

    @Test(expected=NullPointerException.class)
    public void testParseFloatsLengthNullFields() {
        parseFloats("ZT", 2, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatsInvalidLength() {
        parseFloats("ZT", -1, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatsLengthEmptyValues() {
        parseFloats("ZE", 2, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatsLengthWrongType() {
        parseFloats("ZZ", 2, fields);
    }

    @Test
    public void testParseFloatsLengthWrongArrayType() {
        assertEquals(ImmutableList.of(1.0f, 2.0f), parseFloats("ZB", 2, fields));
    }

    @Test
    public void testParseFloatsLength() {
        assertEquals(ImmutableList.of(3.4f, 4.5f), parseFloats("ZT", 2, fields));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatsLengthTooShort() {
        parseFloats("ZT", 1, fields);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseFloatsLengthTooLong() {
        parseFloats("ZT", 3, fields);
    }
}
