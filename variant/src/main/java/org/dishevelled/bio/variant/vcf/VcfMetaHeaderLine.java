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

import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.optionalNumber;
import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.optionalType;
import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.parseEntries;
import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.requiredString;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF META header line.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfMetaHeaderLine {
    private final String id;
    private final VcfHeaderLineNumber number;
    private final VcfHeaderLineType type;
    private final ListMultimap<String, String> attributes;

    VcfMetaHeaderLine(final String id,
                      final VcfHeaderLineNumber number,
                      final VcfHeaderLineType type,
                      final ListMultimap<String, String> attributes) {
        checkNotNull(id);
        checkNotNull(attributes);
        this.id = id;
        this.number = number;
        this.type = type;
        this.attributes = ImmutableListMultimap.copyOf(attributes);
    }

    /**
     * Return the id for this VCF META header line.
     *
     * @return the id for this VCF META header line
     */
    public String getId() {
        return id;
    }

    /**
     * Return the number for this VCF META header line.
     *
     * @return the number for this VCF META header line
     */
    public VcfHeaderLineNumber getNumber() {
        return number;
    }

    /**
     * Return the type for this VCF META header line.
     *
     * @return the type for this VCF META header line
     */
    public VcfHeaderLineType getType() {
        return type;
    }

    /**
     * Return the attributes for this VCF META header line.
     *
     * @return the attributes for this VCF META header line
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##META=<ID=");
        sb.append(id);
        if (number != null) {
            sb.append(",Number=");
            sb.append(number);
        }
        if (type != null) {
            sb.append(",Type=");
            sb.append(type);
        }
        for (Map.Entry<String, String> entry : attributes.entries()) {
            sb.append(",");
            sb.append(entry.getKey());
            sb.append("=\"");
            sb.append(entry.getValue());
            sb.append("\"");
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * Parse the specified value into a VCF META header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF META header line
     */
    public static VcfMetaHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("##META="));
        ListMultimap<String, String> entries = parseEntries(value.replace("##META=", ""));

        String id = requiredString("ID", entries);
        VcfHeaderLineNumber number = optionalNumber("Number", entries);
        VcfHeaderLineType type = optionalType("Type", entries);

        ListMultimap<String, String> attributes = ArrayListMultimap.create(entries);
        attributes.removeAll("ID");
        attributes.removeAll("Number");
        attributes.removeAll("Type");

        return new VcfMetaHeaderLine(id, number, type, attributes);
    }
}
