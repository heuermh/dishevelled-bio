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
 * SAM program header line.
 *
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

    // PN, CL, PP, DS, VN

    public boolean containsSo() {
        return containsFieldKey("SO");
    }

    public String getSo() {
        return getField("SO");
    }

    public Optional<String> getSoOpt() {
        return getFieldOpt("SO");
    }

    public boolean containsGo() {
        return containsFieldKey("GO");
    }

    public String getGo() {
        return getField("GO");
    }

    public Optional<String> getGoOpt() {
        return getFieldOpt("GO");
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
        if (containsSo()) {
            sb.append("\t");
            sb.append("SO:");
            sb.append(getSo());
        }
        if (containsGo()) {
            sb.append("\t");
            sb.append("GO:");
            sb.append(getGo());
        }

        // remaining fields
        Set<String> remainingKeys = new HashSet<String>(getFields().keySet());
        remainingKeys.remove("ID");

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
