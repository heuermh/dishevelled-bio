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
package org.dishevelled.bio.variant.vcf;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.isStructured;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;

/**
 * VCF key-value header line.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfHeaderLine {
    /** Header line key. */
    private final String key;

    /** Header line value. */
    private final String value;


    /**
     * VCF key-value header line.
     *
     * @param key header line key, must not be null
     * @param value header line value, must not be null
     */
    VcfHeaderLine(final String key, final String value) {
        checkNotNull(key);
        checkNotNull(value);

        this.key = key;
        this.value = value;
    }


    /**
     * Return the key for this VCF key-value header line.
     *
     * @return the key for this VCF key-value header line
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the value for this VCF key-value header line.
     *
     * @return the value for this VCF key-value header line
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##");
        sb.append(key);
        sb.append("=");
        sb.append(value);
        return sb.toString();
    }

    /**
     * Parse the specified value into a VCF key-value header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF key-value header line
     */
    public static VcfHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("##"));
        checkArgument(!isStructured(value));

        String filteredValue = value.replace("##", "").replace("\"", "");
        int split = filteredValue.indexOf("=");
        String k = filteredValue.substring(0, split);
        String v = filteredValue.substring(split + 1);
        return new VcfHeaderLine(k, v);
    }
}