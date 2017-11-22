/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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
 * VCF contig header line.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfContigHeaderLine {
    /** Header line ID. */
    private final String id;

    /** Contig header line length. */
    private final Long length;

    /** Contig header line md5. */
    private final String md5;

    /** Contig header line URL. */
    private final String url;

    /** Header line attributes. */
    private final ListMultimap<String, String> attributes;


    /**
     * Create a new VCF contig header line.
     *
     * @param id header line ID, must not be null
     * @param length contig header line length, if any
     * @param md5 contig header line md5, if any
     * @param url contig header line URL, if any
     * @param attributes header line attributes, must not be null
     */
    VcfContigHeaderLine(final String id,
                        final Long length,
                        final String md5,
                        final String url,
                        final ListMultimap<String, String> attributes) {
        checkNotNull(id);
        checkNotNull(attributes);

        this.id = id;
        this.length = length;
        this.md5 = md5;
        this.url = url;
        this.attributes = ImmutableListMultimap.copyOf(attributes);
    }


    /**
     * Return the ID for this VCF contig header line.
     *
     * @return the ID for this VCF contig header line
     */
    public String getId() {
        return id;
    }

    /**
     * Return the length for this VCF contig header line.
     *
     * @return the length for this VCF contig header line
     */
    public Long getLength() {
        return length;
    }

    /**
     * Return the MD5 for this VCF contig header line.
     *
     * @return the MD5 for this VCF contig header line
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Return the URL for this VCF contig header line.
     *
     * @return the URL for this VCF contig header line
     */
    public String getUrl() {
        return url;
    }

    /**
     * Return the attributes for this VCF contig header line.
     *
     * @return the attributes for this VCF contig header line
     */
    public ListMultimap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("##contig=<ID=");
        sb.append(id);
        if (length != null) {
            sb.append(",length=");
            sb.append(length);
        }
        if (md5 != null) {
            sb.append(",MD5=\"");
            sb.append(md5);
            sb.append("\"");
        }
        if (url != null) {
            sb.append(",URL=\"");
            sb.append(url);
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
     * Parse the specified value into a VCF contig header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a VCF contig header line
     */
    public static VcfContigHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("##contig="));
        ListMultimap<String, String> entries = VcfHeaderLineParser.parseEntries(value.replace("##contig=", ""));

        String id = VcfHeaderLineParser.requiredString("ID", entries);
        Long length = VcfHeaderLineParser.optionalLong("length", entries);
        String md5 = VcfHeaderLineParser.optionalString("MD5", entries);
        String url = VcfHeaderLineParser.optionalString("URL", entries);

        ListMultimap<String, String> attributes = ArrayListMultimap.create(entries);
        attributes.removeAll("ID");
        attributes.removeAll("length");
        attributes.removeAll("MD5");
        attributes.removeAll("URL");

        return new VcfContigHeaderLine(id, length, md5, url, attributes);
    }
}
