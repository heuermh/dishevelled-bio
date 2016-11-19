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
    private final String key;
    private final String value;

    VcfHeaderLine(final String key, final String value) {
        checkNotNull(key);
        checkNotNull(value);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

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
