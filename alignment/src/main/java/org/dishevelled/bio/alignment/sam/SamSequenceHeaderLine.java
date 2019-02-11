/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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
package org.dishevelled.bio.alignment.sam;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.alignment.sam.SamHeaderParser.parseFields;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

/**
 * SAM sequence header line.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@Immutable
public final class SamSequenceHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM sequence header line.
     *
     * @param fields field values keyed by tag, must not be null
     */
    private SamSequenceHeaderLine(final Map<String, String> fields) {
        super("SQ", fields);
    }

    // required fields

    public String getSn() {
        return getField("SN");
    }

    public String getLn() {
        return getField("LN");
    }

    // optional fields

    public boolean containsAs() {
        return containsFieldKey("AS");
    }

    public String getAs() {
        return getField("AS");
    }

    public Optional<String> getAsOpt() {
        return getFieldOpt("AS");
    }

    public boolean containsM5() {
        return containsFieldKey("M5");
    }

    public String getM5() {
        return getField("M5");
    }

    public Optional<String> getM5Opt() {
        return getFieldOpt("M5");
    }

    public boolean containsSp() {
        return containsFieldKey("SP");
    }

    public String getSp() {
        return getField("SP");
    }

    public Optional<String> getSpOpt() {
        return getFieldOpt("SP");
    }

    public boolean containsUr() {
        return containsFieldKey("UR");
    }

    public String getUr() {
        return getField("UR");
    }

    public Optional<String> getUrOpt() {
        return getFieldOpt("UR");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getTag());

        // required fields
        sb.append("\t");
        sb.append("SN:");
        sb.append(getSn());
        sb.append("\t");
        sb.append("LN:");
        sb.append(getLn());

        // optional fields
        if (containsAs()) {
            sb.append("\t");
            sb.append("AS:");
            sb.append(getAs());
        }
        if (containsM5()) {
            sb.append("\t");
            sb.append("M5:");
            sb.append(getM5());
        }
        if (containsSp()) {
            sb.append("\t");
            sb.append("SP:");
            sb.append(getSp());
        }
        if (containsUr()) {
            sb.append("\t");
            sb.append("UR:");
            sb.append(getUr());
        }

        // remaining fields
        Set<String> remainingKeys = new HashSet<String>(getFields().keySet());
        remainingKeys.remove("SN");
        remainingKeys.remove("LN");
        remainingKeys.remove("AS");
        remainingKeys.remove("M5");
        remainingKeys.remove("SP");
        remainingKeys.remove("UR");

        for (String key : remainingKeys) {
            sb.append("\t");
            sb.append(key);
            sb.append(":");
            sb.append(getField(key));
        }

        return sb.toString();
    }

    /**
     * Parse the specified value into a SAM sequence header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a SAM sequence header line
     */
    public static SamSequenceHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("@SQ"));

        Map<String, String> fields = parseFields(value.replace("@SQ", "").trim());
        if (!fields.containsKey("SN")) {
            throw new IllegalArgumentException("required field SN missing");
        }
        if (!fields.containsKey("LN")) {
            throw new IllegalArgumentException("required field LN missing");
        }
        return new SamSequenceHeaderLine(fields);
    }
}
