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
import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.parseEntries;
import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.requiredString;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Structured VCF header line, e.g. ##name=&lt;ID=id,...&gt;
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfStructuredHeaderLine {
    /** Structured header line name. */
    private final String name;

    /** Header line ID. */
    private final String id;

    /** Header line attributes. */
    private final ListMultimap<String, String> attributes;


    /**
     * Create a new structured VCF header line.
     *
     * @param name structured header line name, must not be null
     * @param id header line ID, must not be null
     * @param attributes header line attributes, must not be null
     */
    VcfStructuredHeaderLine(final String name,
                            final String id,
                            final ListMultimap<String, String> attributes) {
        checkNotNull(name);
        checkNotNull(id);
        checkNotNull(attributes);

        this.name = name;
        this.id = id;
        this.attributes = ImmutableListMultimap.copyOf(attributes);
    }


    /**
     * Return the name for this structured VCF header line.
     *
     * @return the name for this structured VCF header line
     */
    public String getName() {
        return name;
    }

    /**
     * Return the ID for this structured VCF header line.
     *
     * @return the ID for this structured VCF header line
     */
    public String getId() {
        return id;
    }

    /**
     * Return the attributes for this structured VCF header line.
     *
     * @return the attributes for this structured VCF header line
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##");
        sb.append(name);
        sb.append("=<ID=");
        sb.append(id);
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
     * Parse the specified value into a structured VCF header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a structured VCF header line
     */
    public static VcfStructuredHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(isStructured(value));
        String name = value.substring(2, value.indexOf("="));
        ListMultimap<String, String> entries = parseEntries(value.replace("##" + name + "=", ""));

        String id = requiredString("ID", entries);

        ListMultimap<String, String> attributes = ArrayListMultimap.create(entries);
        attributes.removeAll("ID");

        return new VcfStructuredHeaderLine(name, id, attributes);
    }
}
