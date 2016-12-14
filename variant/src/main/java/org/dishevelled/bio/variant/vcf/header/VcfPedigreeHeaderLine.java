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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.variant.vcf.header.VcfHeaderLineParser.parseEntries;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF PEDIGREE header line.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfPedigreeHeaderLine {
    /** Header line attributes. */
    private final ListMultimap<String, String> attributes;


    /**
     * Create a new VCF PEDIGREE header line.
     *
     * @param attributes header line attributes, must not be null
     */
    VcfPedigreeHeaderLine(final ListMultimap<String, String> attributes) {
        checkNotNull(attributes);
        this.attributes = ImmutableListMultimap.copyOf(attributes);
    }


    /**
     * Return the attributes for this VCF PEDIGREE header line.
     *
     * @return the attributes for this VCF PEDIGREE header line
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##PEDIGREE=<");
        for (Iterator<Map.Entry<String, String>> entries = attributes.entries().iterator(); entries.hasNext(); ) {
            Map.Entry<String, String> entry = entries.next();
            sb.append(entry.getKey());
            sb.append("=\"");
            sb.append(entry.getValue());
            sb.append("\"");
            if (entries.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * Parse the specified value into a VCF PEDIGREE header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF PEDIGREE header line
     */
    public static VcfPedigreeHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("##PEDIGREE="));
        ListMultimap<String, String> entries = parseEntries(value.replace("##PEDIGREE=", ""));
        return new VcfPedigreeHeaderLine(entries);
    }
}
