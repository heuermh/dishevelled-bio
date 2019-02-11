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
 * SAM read group header line.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
@Immutable
public final class SamReadGroupHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM read group header line.
     *
     * @param fields field values keyed by tag, must not be null
     */
    private SamReadGroupHeaderLine(final Map<String, String> fields) {
        super("RG", fields);
    }

    // required fields

    public String getId() {
        return getField("ID");
    }

    // optional fields

    public boolean containsCn() {
        return containsFieldKey("CN");
    }

    public String getCn() {
        return getField("CN");
    }

    public Optional<String> getCnOpt() {
        return getFieldOpt("CN");
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

    public boolean containsDt() {
        return containsFieldKey("DT");
    }

    public String getDt() {
        return getField("DT");
    }

    public Optional<String> getDtOpt() {
        return getFieldOpt("DT");
    }

    public boolean containsFo() {
        return containsFieldKey("FO");
    }

    public String getFo() {
        return getField("FO");
    }

    public Optional<String> getFoOpt() {
        return getFieldOpt("FO");
    }

    public boolean containsKs() {
        return containsFieldKey("KS");
    }

    public String getKs() {
        return getField("KS");
    }

    public Optional<String> getKsOpt() {
        return getFieldOpt("KS");
    }

    public boolean containsLb() {
        return containsFieldKey("LB");
    }

    public String getLb() {
        return getField("LB");
    }

    public Optional<String> getLbOpt() {
        return getFieldOpt("LB");
    }
    
    public boolean containsPg() {
        return containsFieldKey("PG");
    }

    public String getPg() {
        return getField("PG");
    }

    public Optional<String> getPgOpt() {
        return getFieldOpt("PG");
    }

    public boolean containsPi() {
        return containsFieldKey("PI");
    }

    public String getPi() {
        return getField("PI");
    }

    public Optional<String> getPiOpt() {
        return getFieldOpt("PI");
    }

    public boolean containsPl() {
        return containsFieldKey("PL");
    }

    public String getPl() {
        return getField("PL");
    }

    public Optional<String> getPlOpt() {
        return getFieldOpt("PL");
    }

    public boolean containsPm() {
        return containsFieldKey("PM");
    }

    public String getPm() {
        return getField("PM");
    }

    public Optional<String> getPmOpt() {
        return getFieldOpt("PM");
    }

    public boolean containsPu() {
        return containsFieldKey("PU");
    }

    public String getPu() {
        return getField("PU");
    }

    public Optional<String> getPuOpt() {
        return getFieldOpt("PU");
    }

    public boolean containsSm() {
        return containsFieldKey("SM");
    }

    public String getSm() {
        return getField("SM");
    }

    public Optional<String> getSmOpt() {
        return getFieldOpt("SM");
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
        if (containsCn()) {
            sb.append("\t");
            sb.append("CN:");
            sb.append(getCn());
        }
        if (containsDs()) {
            sb.append("\t");
            sb.append("DS:");
            sb.append(getDs());
        }
        if (containsDt()) {
            sb.append("\t");
            sb.append("DT:");
            sb.append(getDt());
        }
        if (containsFo()) {
            sb.append("\t");
            sb.append("FO:");
            sb.append(getFo());
        }
        if (containsKs()) {
            sb.append("\t");
            sb.append("KS:");
            sb.append(getKs());
        }
        if (containsLb()) {
            sb.append("\t");
            sb.append("LB:");
            sb.append(getLb());
        }
        if (containsPg()) {
            sb.append("\t");
            sb.append("PG:");
            sb.append(getPg());
        }
        if (containsPi()) {
            sb.append("\t");
            sb.append("PI:");
            sb.append(getPi());
        }
        if (containsPl()) {
            sb.append("\t");
            sb.append("PL:");
            sb.append(getPl());
        }
        if (containsPm()) {
            sb.append("\t");
            sb.append("PM:");
            sb.append(getPm());
        }
        if (containsPu()) {
            sb.append("\t");
            sb.append("PU:");
            sb.append(getPu());
        }
        if (containsSm()) {
            sb.append("\t");
            sb.append("SM:");
            sb.append(getSm());
        }

        // remaining fields
        Set<String> remainingKeys = new HashSet<String>(getFields().keySet());
        remainingKeys.remove("ID");
        remainingKeys.remove("CN");
        remainingKeys.remove("DS");
        remainingKeys.remove("DT");
        remainingKeys.remove("FO");
        remainingKeys.remove("KS");
        remainingKeys.remove("LB");
        remainingKeys.remove("PG");
        remainingKeys.remove("PI");
        remainingKeys.remove("PL");
        remainingKeys.remove("PM");
        remainingKeys.remove("PU");
        remainingKeys.remove("SM");

        for (String key : remainingKeys) {
            sb.append("\t");
            sb.append(key);
            sb.append(":");
            sb.append(getField(key));
        }

        return sb.toString();
    }

    /**
     * Parse the specified value into a SAM read group header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a SAM read group header line
     */
    public static SamReadGroupHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("@RG"));

        Map<String, String> fields = parseFields(value.replace("@RG", "").trim());
        if (!fields.containsKey("ID")) {
            throw new IllegalArgumentException("required field ID missing");
        }
        return new SamReadGroupHeaderLine(fields);
    }
}
