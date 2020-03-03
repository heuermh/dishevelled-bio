/*

    dsh-bio-variant  Variants.
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
package org.dishevelled.bio.variant.vcf.header;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Parser for VCF header lines.
 *
 * @author  Michael Heuer
 */
@Immutable
final class VcfHeaderLineParser {

    /**
     * Return the required flag value for the specified key.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the required float value for the specified key
     */
    static Boolean requiredFlag(final String key, final ListMultimap<String, String> entries) {
        return Boolean.valueOf(requiredString(key, entries));
    }

    /**
     * Return the required float value for the specified key.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the required float value for the specified key
     */
    static Float requiredFloat(final String key, final ListMultimap<String, String> entries) {
        return Float.valueOf(requiredString(key, entries));
    }

    /**
     * Return the required integer value for the specified key.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the required integer value for the specified key
     */
    static Integer requiredInteger(final String key, final ListMultimap<String, String> entries) {
        return Integer.valueOf(requiredString(key, entries));
    }

    /**
     * Return the required number value for the specified key.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the required number value for the specified key
     */
    static VcfHeaderLineNumber requiredNumber(final String key, final ListMultimap<String, String> entries) {
        return VcfHeaderLineNumber.valueOf(requiredString(key, entries));
    }

    /**
     * Return the required type value for the specified key.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the required type value for the specified key
     */
    static VcfHeaderLineType requiredType(final String key, final ListMultimap<String, String> entries) {
        return VcfHeaderLineType.valueOf(requiredString(key, entries));
    }

    /**
     * Return the required flag value for the specified key.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the required flag value for the specified key
     */
    static String requiredString(final String key, final ListMultimap<String, String> entries) {
        checkNotNull(key);
        checkNotNull(entries);
        List<String> values = entries.get(key);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("required key " + key + " not found");
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("found more than one value for required key " + key);
        }
        return values.get(0);
    }

    /**
     * Return the optional flag value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional flag value, or null if no such value exists
     */
    static Boolean optionalFlag(final String key, final ListMultimap<String, String> entries) {
        String value = optionalString(key, entries);
        return value == null ? null : Boolean.valueOf(value);
    }

    /**
     * Return the optional float value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional float value, or null if no such value exists
     */
    static Float optionalFloat(final String key, final ListMultimap<String, String> entries) {
        String value = optionalString(key, entries);
        return value == null ? null : Float.valueOf(value);
    }

    /**
     * Return the optional integer value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional integer value, or null if no such value exists
     */
    static Integer optionalInteger(final String key, final ListMultimap<String, String> entries) {
        String value = optionalString(key, entries);
        return value == null ? null : Integer.valueOf(value);
    }

    /**
     * Return the optional long value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional long value, or null if no such value exists
     */
    static Long optionalLong(final String key, final ListMultimap<String, String> entries) {
        String value = optionalString(key, entries);
        return value == null ? null : Long.valueOf(value);
    }

    /**
     * Return the optional number value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional number value, or null if no such value exists
     */
    static VcfHeaderLineNumber optionalNumber(final String key, final ListMultimap<String, String> entries) {
        String value = optionalString(key, entries);
        return value == null ? null : VcfHeaderLineNumber.valueOf(value);
    }

    /**
     * Return the optional type value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional type value, or null if no such value exists
     */
    static VcfHeaderLineType optionalType(final String key, final ListMultimap<String, String> entries) {
        String value = optionalString(key, entries);
        return value == null ? null : VcfHeaderLineType.valueOf(value);
    }

    /**
     * Return the optional string value, if any.
     *
     * @param key key, must not be null
     * @param entries entries, must not be null
     * @return the optional string value, or null if no such value exists
     */
    static String optionalString(final String key, final ListMultimap<String, String> entries) {
        checkNotNull(key);
        checkNotNull(entries);

        List<String> values = entries.get(key);
        if (values.isEmpty()) {
            return null;
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("found more than one value for optional key " + key);
        }
        return values.get(0);
    }

    /** Pattern for structured header lines. */
    static final Pattern STRUCTURED = Pattern.compile("^##[a-zA-Z0-9_-]+=<.*ID=.+>$");

    /**
     * Return true if the specified line is a structured header line.
     *
     * @param line line, must not be null
     * @return true if the specified line is a structured header line
     */
    static boolean isStructured(final String line) {
        checkNotNull(line);
        Matcher m = STRUCTURED.matcher(line);
        return m.matches();
    }

// following method adapted from htsjdk/src/main/java/htsjdk/variant/vcf/VCFHeaderLineTranslator.java, and is licensed
/*
* Copyright (c) 2012 The Broad Institute
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
* THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

    /**
     * Parse the specified VCF header line into a list multimap of key value entries.
     *
     * @param line line, must not be null
     * @return the specified VCF header line parsed into a list multimap of key value entries
     */
    static ListMultimap<String, String> parseEntries(final String line) {
        checkNotNull(line);

        final ListMultimap<String, String> entries = ArrayListMultimap.create();
        final StringBuilder builder = new StringBuilder();

        String key = "";
        int index = 0;
        boolean inQuote = false;
        boolean escape = false;

        for (char c: line.toCharArray()) {
            if (c == '\"') {
                if (escape) {
                    builder.append(c);
                    escape = false;
                }
                else {
                    inQuote = !inQuote;
                }
            }
            else if (inQuote) {
                if (escape) {
                    if (c == '\\') {
                        builder.append(c);
                    }
                    else {
                        builder.append('\\');
                        builder.append(c);
                    }
                    escape = false;
                }
                else if (c != '\\') {
                    builder.append(c);
                }
                else {
                    escape = true;
                }
            }
            else {
                escape = false;
                switch (c) {
                    case '<':
                        if (index != 0) {
                            builder.append(c);
                        }
                        break;
                    case '>':
                        if (index == line.length() - 1) {
                            entries.put(key, builder.toString());
                        }
                        break;
                    case '=':
                        key = builder.toString().trim();
                        builder.delete(0, builder.length());
                        break;
                    case ',':
                        entries.put(key, builder.toString());
                        builder.delete(0, builder.length());
                        break;
                    default:
                        builder.append(c);
                }
            }
            index++;
        }
        if (inQuote) {
            throw new RuntimeException("Unclosed quote in header line value " + line);
        }
        return entries;
    }
}
