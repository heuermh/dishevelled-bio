/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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

import static org.dishevelled.bio.variant.vcf.header.VcfHeaderLineParser.optionalString;
import static org.dishevelled.bio.variant.vcf.header.VcfHeaderLineParser.parseEntries;
import static org.dishevelled.bio.variant.vcf.header.VcfHeaderLineParser.requiredNumber;
import static org.dishevelled.bio.variant.vcf.header.VcfHeaderLineParser.requiredString;
import static org.dishevelled.bio.variant.vcf.header.VcfHeaderLineParser.requiredType;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF INFO header line.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfInfoHeaderLine {
    /** Header line ID. */
    private final String id;

    /** INFO header line number. */
    private final VcfHeaderLineNumber number;

    /** INFO header line type. */
    private final VcfHeaderLineType type;

    /** Header line description. */
    private final String description;

    /** INFO header line source. */
    private final String source;

    /** INFO header line version. */
    private final String version;

    /** Header line attributes. */
    private final ListMultimap<String, String> attributes;

    /**
     * Create a new VCF INFO header line.
     *
     * @param id header line ID, must not be null
     * @param number INFO header line number, must not be null
     * @param type INFO header line type, must not be null
     * @param description header line description, must not be null
     * @param source INFO header line source, if any
     * @param version INFO header line version, if any
     * @param attributes header line attributes, must not be null
     */
    VcfInfoHeaderLine(final String id,
                      final VcfHeaderLineNumber number,
                      final VcfHeaderLineType type,
                      final String description,
                      final String source,
                      final String version,
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
        this.source = source;
        this.version = version;
        this.attributes = ImmutableListMultimap.copyOf(attributes);
    }


    /**
     * Return the ID for this VCF INFO header line.
     *
     * @return the ID for this VCF INFO header line
     */
    public String getId() {
        return id;
    }

    /**
     * Return the number for this VCF INFO header line.
     *
     * @return the number for this VCF INFO header line
     */
    public VcfHeaderLineNumber getNumber() {
        return number;
    }

    /**
     * Return the type for this VCF INFO header line.
     *
     * @return the type for this VCF INFO header line
     */
    public VcfHeaderLineType getType() {
        return type;
    }

    /**
     * Return the description for this VCF INFO header line.
     *
     * @return the description for this VCF INFO header line
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the source for this VCF INFO header line.
     *
     * @return the source for this VCF INFO header line
     */
    public String getSource() {
        return source;
    }

    /**
     * Return the version for this VCF INFO header line.
     *
     * @return the version for this VCF INFO header line
     */
    public String getVersion() {
        return version;
    }

    /**
     * Return the attributes for this VCF INFO header line.
     *
     * @return the attributes for this VCF INFO header line
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##INFO=<ID=");
        sb.append(id);
        sb.append(",Number=");
        sb.append(number);
        sb.append(",Type=");
        sb.append(type);
        sb.append(",Description=\"");
        sb.append(description);
        sb.append("\"");
        if (source != null) {
            sb.append(",Source=\"");
            sb.append(source);
            sb.append("\"");
        }
        if (version != null) {
            sb.append(",Version=\"");
            sb.append(version);
            sb.append("\"");
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
     * Parse the specified value into a VCF INFO header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF INFO header line
     */
    public static VcfInfoHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("##INFO="));
        ListMultimap<String, String> entries = parseEntries(value.replace("##INFO=", ""));

        String id = requiredString("ID", entries);
        VcfHeaderLineNumber number = requiredNumber("Number", entries);
        VcfHeaderLineType type = requiredType("Type", entries);
        String description = requiredString("Description", entries);
        String source = optionalString("Source", entries);
        String version = optionalString("Version", entries);

        ListMultimap<String, String> attributes = ArrayListMultimap.create(entries);
        attributes.removeAll("ID");
        attributes.removeAll("Number");
        attributes.removeAll("Type");
        attributes.removeAll("Description");
        attributes.removeAll("Source");
        attributes.removeAll("Version");

        return new VcfInfoHeaderLine(id, number, type, description, source, version, attributes);
    }
}
