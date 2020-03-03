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
package org.dishevelled.bio.alignment.sam;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.apache.commons.codec.binary.Hex.decodeHex;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import com.google.common.primitives.Bytes;

import org.apache.commons.codec.DecoderException;

/**
 * Utility methods on SAM fields.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@Immutable
final class SamFields {

    /**
     * Private no-arg constructor.
     */
    private SamFields() {
        // empty
    }


    /**
     * Convert the specified string to a character.
     *
     * @param value value to convert, must not be null and must have length equal to one
     * @return the specified String converted to a character
     */
    private static char toChar(final String value) {
        checkNotNull(value);
        checkArgument(value.length() == 1, "Type=A value " + value + " not one character");

        return value.charAt(0);
    }

    /**
     * Parse the Type=A field value for the specified key into a character.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=A field value for the specified key parsed into a character
     */
    static char parseCharacter(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);

        List<String> values = fields.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=A value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=A value for key " + key);
        }
        return toChar(values.get(0));
    }

    /**
     * Parse the Type=i field value for the specified key into an integer.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=i field value for the specified key parsed into an integer
     */
    static int parseInteger(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);

        List<String> values = fields.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=i value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=i value for key " + key);
        }
        return Integer.valueOf(values.get(0));
    }

    /**
     * Parse the Type=f field value for the specified key into a float.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=f field value for the specified key parsed into a float
     */
    static float parseFloat(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);

        List<String> values = fields.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=f value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=f value for key " + key);
        }
        return Float.valueOf(values.get(0));
    }

    /**
     * Return the Type=Z field value for the specified key as a string.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=Z field value for the specified key as a string
     */
    static String parseString(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);

        List<String> values = fields.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=Z value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=Z value for key " + key);
        }
        return values.get(0);
    }

    /**
     * Return the Type=H field value for the specified key as a byte array.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=H field value for the specified key as a byte array
     */
    static byte[] parseByteArray(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);

        List<String> values = fields.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=H value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=H value for key " + key);
        }
        try {
            return decodeHex(values.get(0));
        }
        catch (DecoderException e) {
            throw new IllegalArgumentException("could not decode hex value for key " + key, e);
        }
    }

    /**
     * Return the Type=H field value for the specified key as a list of bytes.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=H field value for the specified key as a list of bytes
     */
    static List<Byte> parseBytes(final String key, final ListMultimap<String, String> fields) {
        return Bytes.asList(parseByteArray(key, fields));
    }

    /**
     * Convert the specified list of strings into an immutable list of Integers.
     *
     * @param values list of Strings to convert, must not be null
     * @return the specified list of strings converted into an immutable list of Integers
     */
    private static List<Integer> parseIntegers(final List<String> values) {
        checkNotNull(values);

        ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (String value : values) {
            builder.add(Integer.valueOf(value));
        }
        return builder.build();
    }

    /**
     * Convert the specified list of strings into an immutable list of floats.
     *
     * @param values list of Strings to convert, must not be null
     * @return the specified list of strings converted into an immutable list of floats
     */
    private static List<Float> parseFloats(final List<String> values) {
        checkNotNull(values);

        ImmutableList.Builder<Float> builder = ImmutableList.builder();
        for (String value : values) {
            builder.add(Float.valueOf(value));
        }
        return builder.build();
    }

    /**
     * Parse the Type=B first letter [cCsSiI] field value for the specified key into an immutable list of integers.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=B first letter [cCsSiI] field value for the specified key parsed into an immutable list of integers
     */
    static List<Integer> parseIntegers(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);
        return parseIntegers(fields.get(key));
    }

    /**
     * Parse the Type=B first letter f field value for the specified key into an immutable list of floats.
     *
     * @param key key, must not be null
     * @param fields fields, must not be null
     * @return the Type=f first letter f field value for the specified key parsed into an immutable list of floats
     */
    static List<Float> parseFloats(final String key, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);
        return parseFloats(fields.get(key));
    }

    /**
     * Parse the Type=B first letter [cCsSiI] field value for the specified key into an immutable list of integers
     * of size equal to the specified length.
     *
     * @param key key, must not be null
     * @param length length, must be greater than zero
     * @param fields fields, must not be null
     * @return the Type=B first letter [cCsSiI] field value for the specified key parsed into an immutable list of integers
     *    of size equal to the specified length
     */
    static List<Integer> parseIntegers(final String key, final int length, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);
        checkArgument(length > 0, "length must be at least one");

        List<String> values = fields.get(key);
        if (values.size() != length) {
            throw new IllegalArgumentException("expected " + length + " Type=B first letter [cCsSiI] values, found " + values.size());
        }
        return parseIntegers(values);
    }

    /**
     * Parse the Type=B first letter f field value for the specified key into an immutable list of floats
     * of size equal to the specified length.
     *
     * @param key key, must not be null
     * @param length length, must be greater than zero
     * @param fields fields, must not be null
     * @return the Type=B first letter f field value for the specified key parsed into an immutable list of floats
     *    of size equal to the specified length
     */
    static List<Float> parseFloats(final String key, final int length, final ListMultimap<String, String> fields) {
        checkNotNull(key);
        checkNotNull(fields);
        checkArgument(length > 0, "length must be at least one");

        List<String> values = fields.get(key);
        if (values.size() != length) {
            throw new IllegalArgumentException("expected " + length + " Type=B first letter f values, found " + values.size());
        }
        return parseFloats(values);
    }
}
