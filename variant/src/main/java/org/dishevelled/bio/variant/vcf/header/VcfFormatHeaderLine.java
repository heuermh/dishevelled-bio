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

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF FORMAT header line.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfFormatHeaderLine {
    /** Header line ID. */
    private final String id;

    /** FORMAT header line number. */
    private final VcfHeaderLineNumber number;

    /** FORMAT header line type. */
    private final VcfHeaderLineType type;

    /** Header line description. */
    private final String description;

    /** Header line attributes. */
    private final ListMultimap<String, String> attributes;


    /**
     * Create a new VCF FORMAT header line.
     *
     * @param id header line ID, must not be null
     * @param number FORMAT header line number, must not be null
     * @param type FORMAT header line type, must not be null
     * @param description header line description, must not be null
     * @param attributes header line attributes, must not be null
     */
    VcfFormatHeaderLine(final String id,
                        final VcfHeaderLineNumber number,
                        final VcfHeaderLineType type,
                        final String description,
                        final ListMultimap<String, String> attributes) {
        checkNotNull(id);
        checkNotNull(number);
        checkNotNull(type);
        checkNotNull(description);
        checkNotNull(attributes);

        this.id = id;
        this.number = number;
        this.type = type;
        this.description = description;
        this.attributes = ImmutableListMultimap.copyOf(attributes);
    }


    /**
     * Return the ID for this VCF FORMAT header line.
     *
     * @return the ID for this VCF FORMAT header line
     */
    public String getId() {
        return id;
    }

    /**
     * Return the number for this VCF FORMAT header line.
     *
     * @return the number for this VCF FORMAT header line
     */
    public VcfHeaderLineNumber getNumber() {
        return number;
    }

    /**
     * Return the type for this VCF FORMAT header line.
     *
     * @return the type for this VCF FORMAT header line
     */
    public VcfHeaderLineType getType() {
        return type;
    }

    /**
     * Return the description for this VCF FORMAT header line.
     *
     * @return the description for this VCF FORMAT header line
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the attributes for this VCF FORMAT header line.
     *
     * @return the attributes for this VCF FORMAT header line
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##FORMAT=<ID=");
        sb.append(id);
        sb.append(",Number=");
        sb.append(number);
        sb.append(",Type=");
        sb.append(type);
        sb.append(",Description=\"");
        sb.append(description);
        sb.append("\"");
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
     * Parse the specified value into a VCF FORMAT header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF FORMAT header line
     */
    public static VcfFormatHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("##FORMAT="));
        ListMultimap<String, String> entries = VcfHeaderLineParser.parseEntries(value.replace("##FORMAT=", ""));

        String id = VcfHeaderLineParser.requiredString("ID", entries);
        VcfHeaderLineNumber number = VcfHeaderLineParser.requiredNumber("Number", entries);
        VcfHeaderLineType type = VcfHeaderLineParser.requiredType("Type", entries);
        String description = VcfHeaderLineParser.requiredString("Description", entries);

        ListMultimap<String, String> attributes = ArrayListMultimap.create(entries);
        attributes.removeAll("ID");
        attributes.removeAll("Number");
        attributes.removeAll("Type");
        attributes.removeAll("Description");

        return new VcfFormatHeaderLine(id, number, type, description, attributes);
    }
}
