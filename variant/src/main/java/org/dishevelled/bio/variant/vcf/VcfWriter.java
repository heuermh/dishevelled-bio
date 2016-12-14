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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

/**
 * VCF writer.
 *
 * @author  Michael Heuer
 */
public final class VcfWriter {

    /**
     * Private no-arg constructor.
     */
    private VcfWriter() {
        // empty
    }


    /**
     * Write VCF with the specified print writer.
     *
     * @param header VCF header, must not be null
     * @param samples zero or more VCF samples, must not be null
     * @param records zero or more VCF records, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void write(final VcfHeader header,
                             final List<VcfSample> samples,
                             final List<VcfRecord> records,
                             final PrintWriter writer) {

        writeHeader(header, writer);
        writeColumnHeader(samples, writer);
        writeRecords(samples, records, writer);
    }

    /**
     * Write VCF header with the specified print writer.
     *
     * @param header VCF header, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeHeader(final VcfHeader header, final PrintWriter writer) {
        checkNotNull(header);
        checkNotNull(writer);

        for (String meta : header.getMeta()) {
            writer.println(meta);
        }
    }

    /**
     * Write VCF column header with the specified print writer.
     *
     * @param samples zero or more VCF samples, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeColumnHeader(final List<VcfSample> samples, final PrintWriter writer) {
        checkNotNull(samples);
        checkNotNull(writer);

        StringBuilder sb = new StringBuilder("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO");
        if (!samples.isEmpty()) {
            sb.append("\tFORMAT");
        }
        for (VcfSample sample : samples) {
            sb.append("\t");
            sb.append(sample.getId());
        }
        writer.println(sb.toString());
    }

    /**
     * Write VCF records with the specified print writer.
     *
     * @param samples zero or more VCF samples, must not be null
     * @param records zero or more VCF records, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeRecords(final List<VcfSample> samples,
                                    final List<VcfRecord> records,
                                    final PrintWriter writer) {
        checkNotNull(samples);
        checkNotNull(records);
        checkNotNull(writer);

        for (VcfRecord record : records) {
            writeRecord(samples, record, writer);
        }
    }

    /**
     * Write VCF record with the specified print writer.
     *
     * @param samples zero or more VCF samples, must not be null
     * @param record VCF record, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeRecord(final List<VcfSample> samples, final VcfRecord record, final PrintWriter writer) {
        checkNotNull(samples);
        checkNotNull(record);
        checkNotNull(writer);

        StringBuilder sb = new StringBuilder();
        sb.append(record.getChrom());
        sb.append("\t");
        sb.append(record.getPos());

        sb.append("\t");
        if (record.getId() == null || record.getId().length == 0) {
            sb.append(".");
        }
        else {
            sb.append(Joiner.on(";").join(record.getId()));
        }

        sb.append("\t");
        sb.append(record.getRef());
        sb.append("\t");
        sb.append(Joiner.on(",").join(record.getAlt()));

        sb.append("\t");
        if (record.getQual() == null || Double.isNaN(record.getQual())) {
            sb.append(".");
        }
        else if (record.getQual() - record.getQual().intValue() == 0.0d) {
            sb.append(record.getQual().intValue());
        }
        else {
            sb.append(record.getQual());
        }

        sb.append("\t");
        if (record.getFilter() == null || record.getFilter().length == 0) {
            sb.append(".");
        }
        else {
            sb.append(Joiner.on(";").join(record.getFilter()));
        }

        sb.append("\t");
        if (record.getInfo().isEmpty()) {
            sb.append(".");
        }
        else {
            // convert info values to strings
            Map<String, String> infoStrings = new HashMap<String, String>(record.getInfo().size());
            for (String key : record.getInfo().keySet()) {
                infoStrings.put(key, Joiner.on(",").join(record.getInfo().get(key)));
            }
            // then join, removing value for flags
            sb.append(Joiner.on(";").withKeyValueSeparator("=").join(infoStrings).replace("=true", ""));
        }

        if (!samples.isEmpty()) {
            sb.append("\t");
            sb.append(Joiner.on(":").join(record.getFormat()));
            for (VcfSample sample : samples) {
                sb.append("\t");

                List<String> values = new ArrayList<String>();
                for (String formatId : record.getFormat()) {
                    List<String> fieldValues = record.getGenotypes().get(sample.getId()).getFields().get(formatId);
                    values.add(fieldValues.isEmpty() ? "." : Joiner.on(",").join(fieldValues));
                }
                sb.append(Joiner.on(":").join(values));
            }
        }
        writer.println(sb.toString());
    }
}
