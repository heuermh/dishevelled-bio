/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

/**
 * VCF header line number.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfHeaderLineNumber {
    /** Cache of commonly used header line numbers. */
    private static final Map<String, VcfHeaderLineNumber> CACHE = ImmutableMap.<String, VcfHeaderLineNumber>builder()
        .put("A", new VcfHeaderLineNumber("A"))
        .put("R", new VcfHeaderLineNumber("R"))
        .put("G", new VcfHeaderLineNumber("G"))
        .put(".", new VcfHeaderLineNumber("."))
        .put("1", new VcfHeaderLineNumber(1))
        .put("2", new VcfHeaderLineNumber(2))
        .put("3", new VcfHeaderLineNumber(3))
        .put("4", new VcfHeaderLineNumber(4))
        .put("5", new VcfHeaderLineNumber(5))
        .put("6", new VcfHeaderLineNumber(6))
        .put("7", new VcfHeaderLineNumber(7))
        .put("8", new VcfHeaderLineNumber(8))
        .put("9", new VcfHeaderLineNumber(9))
        .put("10", new VcfHeaderLineNumber(10))
        .build();

    /** Name of this header line number. */
    private final String name;

    /** Value for this header line number. */
    private final int value;

    /** True if this header line number is numeric. */
    private final boolean isNumeric;


    /**
     * Create a new VCF header line number with the specified name.
     *
     * @param name name of this header line number
     */
    private VcfHeaderLineNumber(final String name) {
        this.name = name;
        this.value = 0;
        isNumeric = false;
    }

    /**
     * Create a new VCF header line number with the specified value.
     *
     * @param value value for this header line number
     */
    private VcfHeaderLineNumber(final int value) {
        this.name = "N";
        this.value = value;
        isNumeric = true;
    }


    /**
     * Return the name for this VCF header line number.
     *
     * @return the name for this VCF header line number
     */
    public String getName() {
        return name;
    }

    /**
     * Return the name for this VCF header line number.
     *
     * @return the name for this VCF header line number
     */
    public int getValue() {
        return value;
    }

    /**
     * Return true if this VCF header line number is numeric.
     *
     * @return true if this VCF header line number is numeric
     */
    public boolean isNumeric() {
        return isNumeric;
    }

    @Override
    public String toString() {
        if (isNumeric) {
            return String.valueOf(value);
        }
        else {
            return name;
        }
    }

    /**
     * Parse the specified value into a VCF header line number.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF header line number
     */
    public static VcfHeaderLineNumber valueOf(final String value) {
        checkNotNull(value);
        if (CACHE.containsKey(value)) {
            return CACHE.get(value);
        }
        Integer intValue = Integer.parseInt(value);
        return new VcfHeaderLineNumber(intValue);
    }
}
