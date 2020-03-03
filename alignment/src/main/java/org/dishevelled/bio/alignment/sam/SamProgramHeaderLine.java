/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
 * SAM program header line.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@Immutable
public final class SamProgramHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM program header line.
     *
     * @param fields field values keyed by tag, must not be null
     */
    private SamProgramHeaderLine(final Map<String, String> fields) {
        super("PG", fields);
    }

    // required fields

    public String getId() {
        return getField("ID");
    }

    // optional fields

    public boolean containsPn() {
        return containsFieldKey("PN");
    }

    public String getPn() {
        return getField("PN");
    }

    public Optional<String> getPnOpt() {
        return getFieldOpt("PN");
    }

    public boolean containsCl() {
        return containsFieldKey("CL");
    }

    public String getCl() {
        return getField("CL");
    }

    public Optional<String> getClOpt() {
        return getFieldOpt("CL");
    }

    public boolean containsPp() {
        return containsFieldKey("PP");
    }

    public String getPp() {
        return getField("PP");
    }

    public Optional<String> getPpOpt() {
        return getFieldOpt("PP");
    }

    public boolean containsDs() {
        return containsFieldKey("DS");
    }

    public String getDs() {
        return getField("DS");
    }

    public Optional<String> getDsOpt() {
        return getFieldOpt("DS");
    }

    public boolean containsVn() {
        return containsFieldKey("VN");
    }

    public String getVn() {
        return getField("VN");
    }

    public Optional<String> getVnOpt() {
        return getFieldOpt("VN");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getTag());

        // required fields
        sb.append("\t");
        sb.append("ID:");
        sb.append(getId());

        // optional fields
        if (containsPn()) {
            sb.append("\t");
            sb.append("PN:");
            sb.append(getPn());
        }
        if (containsCl()) {
            sb.append("\t");
            sb.append("CL:");
            sb.append(getCl());
        }
        if (containsPp()) {
            sb.append("\t");
            sb.append("PP:");
            sb.append(getPp());
        }
        if (containsDs()) {
            sb.append("\t");
            sb.append("DS:");
            sb.append(getDs());
        }
        if (containsVn()) {
            sb.append("\t");
            sb.append("VN:");
            sb.append(getVn());
        }

        // remaining fields
        Set<String> remainingKeys = new HashSet<String>(getFields().keySet());
        remainingKeys.remove("ID");
        remainingKeys.remove("PN");
        remainingKeys.remove("CL");
        remainingKeys.remove("PP");
        remainingKeys.remove("DS");
        remainingKeys.remove("VN");

        for (String key : remainingKeys) {
            sb.append("\t");
            sb.append(key);
            sb.append(":");
            sb.append(getField(key));
        }

        return sb.toString();
    }

    /**
     * Parse the specified value into a SAM program header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a SAM program header line
     */
    public static SamProgramHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("@PG"));

        Map<String, String> fields = parseFields(value.replace("@PG", "").trim());
        if (!fields.containsKey("ID")) {
            throw new IllegalArgumentException("required field ID missing");
        }
        return new SamProgramHeaderLine(fields);
    }
}
