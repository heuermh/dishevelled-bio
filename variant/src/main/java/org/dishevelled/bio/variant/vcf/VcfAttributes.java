/*

    dsh-bio-variant  Variants.
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
package org.dishevelled.bio.variant.vcf;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

/**
 * Utility methods on VCF attributes (INFO and FORMAT).
 *
 * @author  Michael Heuer
 */
@Immutable
final class VcfAttributes {

    /**
     * Private no-arg constructor.
     */
    private VcfAttributes() {
        // empty
    }


    /**
     * Return the count for Number=G attributes for the specified VCF genotype.
     *
     * @param genotype VCF genotype, must not be null
     * @return the count for Number=G attributes for the specified VCF genotype
     */
    static int numberG(final VcfGenotype genotype) {
        checkNotNull(genotype);

        int n = genotype.getAlt().length;
        int k = genotype.getGt().split("[|/]").length;

        // todo: check preconditions
        // (n choose k), adapted from commons-math CombinatoricsUtils.java, may overflow
        int result = 1;
        int i = n - k + 1;
        for (int j = 1; j <= k; j++) {
            result = result * i / j;
            i++;
        }
        return result;
    }


    /**
     * Convert the specified string to a character.
     *
     * @param value value to convert, must not be null and must have length equal to one
     * @return the specified String converted to a character
     */
    private static char toChar(final String value) {
        checkNotNull(value);
        checkArgument(value.length() == 1, "Type=Character value " + value + " not one character");

        return value.charAt(0);
    }

    /**
     * Parse the Type=Character attribute value for the specified key into a character.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Character attribute value for the specified key parsed into a character
     */
    static char parseCharacter(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);

        List<String> values = attributes.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=Character value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=Character value for key " + key);
        }
        return toChar(values.get(0));
    }

    /**
     * Parse the Type=Flag attribute value for the specified key into a boolean.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Flag attribute value for the specified key parsed into a boolean
     */
    static boolean parseFlag(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);

        List<String> values = attributes.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=Flag value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=Flag value for key " + key);
        }
        return Boolean.valueOf(values.get(0));
    }

    /**
     * Parse the Type=Integer attribute value for the specified key into an integer.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Integer attribute value for the specified key parsed into an integer
     */
    static int parseInteger(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);

        List<String> values = attributes.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=Integer value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=Integer value for key " + key);
        }
        return Integer.valueOf(values.get(0));
    }

    /**
     * Parse the Type=Float attribute value for the specified key into a float.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Float attribute value for the specified key parsed into a float
     */
    static float parseFloat(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);

        List<String> values = attributes.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=Float value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=Float value for key " + key);
        }
        return Float.valueOf(values.get(0));
    }

    /**
     * Return the Type=String attribute value for the specified key as a string.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=String attribute value for the specified key as a string
     */
    static String parseString(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);

        List<String> values = attributes.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Type=String value missing for key " + key);
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("more than one Type=String value for key " + key);
        }
        return values.get(0);
    }


    /**
     * Convert the specified list of strings into an immutable list of characters.
     *
     * @param values list of Strings to convert, must not be null
     * @return the specified list of strings converted into an immutable list of characters
     */
    private static List<Character> parseCharacters(final List<String> values) {
        checkNotNull(values);

        ImmutableList.Builder<Character> builder = ImmutableList.builder();
        for (String value : values) {
            builder.add(toChar(value));
        }
        return builder.build();
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
     * Return the specified list of strings as an immutable list of strings.
     *
     * @param values list of Strings to convert, must not be null
     * @return the specified list of strings as an immutable list of strings
     */
    private static List<String> parseStrings(final List<String> values) {
        checkNotNull(values);

        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String value : values) {
            builder.add(value);
        }
        return builder.build();
    }


    /**
     * Parse the Type=Character Number=. attribute value for the specified key into an immutable list of characters.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Character Number=. attribute value for the specified key parsed into an immutable list of characters
     */
    static List<Character> parseCharacters(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        return parseCharacters(attributes.get(key));
    }

    /**
     * Parse the Type=Integer Number=. attribute value for the specified key into an immutable list of integers.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Integer Number=. attribute value for the specified key parsed into an immutable list of integers
     */
    static List<Integer> parseIntegers(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        return parseIntegers(attributes.get(key));
    }

    /**
     * Parse the Type=Float Number=. attribute value for the specified key into an immutable list of floats.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=Float Number=. attribute value for the specified key parsed into an immutable list of floats
     */
    static List<Float> parseFloats(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        return parseFloats(attributes.get(key));
    }

    /**
     * Return the Type=String Number=. attribute value for the specified key as an immutable list of strings.
     *
     * @param key key, must not be null
     * @param attributes, must not be null
     * @return the Type=String Number=. attribute value for the specified key as an immutable list of strings
     */
    static List<String> parseStrings(final String key, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        return parseStrings(attributes.get(key));
    }


    /**
     * Parse the Type=Character Number=[n, A, R, G] attribute value for the specified key into an immutable list of characters
     * of size equal to the specified number.
     *
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @param attributes, must not be null
     * @return the Type=Character Number=[n, A, R, G] attribute value for the specified key parsed into an immutable list of characters
     *    of size equal to the specified number
     */
    static List<Character> parseCharacters(final String key, final int number, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        checkArgument(number > 0, "number must be at least one");

        List<String> values = attributes.get(key);
        if (values.size() != number) {
            throw new IllegalArgumentException("expected " + number + " Type=Character values, found " + values.size());
        }
        return parseCharacters(values);
    }

    /**
     * Parse the Type=Integer Number=[n, A, R, G] attribute value for the specified key into an immutable list of integers
     * of size equal to the specified number.
     *
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @param attributes, must not be null
     * @return the Type=Integer Number=[n, A, R, G] attribute value for the specified key parsed into an immutable list of integers
     *    of size equal to the specified number
     */
    static List<Integer> parseIntegers(final String key, final int number, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        checkArgument(number > 0, "number must be at least one");

        List<String> values = attributes.get(key);
        if (values.size() != number) {
            throw new IllegalArgumentException("expected " + number + " Type=Integer values, found " + values.size());
        }
        return parseIntegers(values);
    }

    /**
     * Parse the Type=Float Number=[n, A, R, G] attribute value for the specified key into an immutable list of floats
     * of size equal to the specified number.
     *
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @param attributes, must not be null
     * @return the Type=Float Number=[n, A, R, G] attribute value for the specified key parsed into an immutable list of floats
     *    of size equal to the specified number
     */
    static List<Float> parseFloats(final String key, final int number, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        checkArgument(number > 0, "number must be at least one");

        List<String> values = attributes.get(key);
        if (values.size() != number) {
            throw new IllegalArgumentException("expected " + number + " Type=Float values, found " + values.size());
        }
        return parseFloats(values);
    }

    /**
     * Return the Type=String Number=[n, A, R, G] attribute value for the specified key as an immutable list of strings
     * of size equal to the specified number.
     *
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @param attributes, must not be null
     * @return the Type=String Number=[n, A, R, G] attribute value for the specified key as an immutable list of strings
     *    of size equal to the specified number
     */
    static List<String> parseStrings(final String key, final int number, final ListMultimap<String, String> attributes) {
        checkNotNull(key);
        checkNotNull(attributes);
        checkArgument(number > 0, "number must be at least one");

        List<String> values = attributes.get(key);
        if (values.size() != number) {
            throw new IllegalArgumentException("expected " + number + " Type=String values, found " + values.size());
        }
        return parseStrings(values);
    }
}
