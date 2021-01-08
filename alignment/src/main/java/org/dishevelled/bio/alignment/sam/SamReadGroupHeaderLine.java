/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

/**
 * SAM read group header line.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamReadGroupHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM read group header line.
     *
     * @param annotations annotation values keyed by key, must not be null
     */
    private SamReadGroupHeaderLine(final Map<String, String> annotations) {
        super("RG", annotations);
    }

    // required annotations

    public String getId() {
        return getAnnotation("ID");
    }

    // optional annotations

    public boolean containsCn() {
        return containsAnnotationKey("CN");
    }

    public String getCn() {
        return getAnnotation("CN");
    }

    public Optional<String> getCnOpt() {
        return getAnnotationOpt("CN");
    }

    public boolean containsDs() {
        return containsAnnotationKey("DS");
    }

    public String getDs() {
        return getAnnotation("DS");
    }

    public Optional<String> getDsOpt() {
        return getAnnotationOpt("DS");
    }

    public boolean containsDt() {
        return containsAnnotationKey("DT");
    }

    public String getDt() {
        return getAnnotation("DT");
    }

    public Optional<String> getDtOpt() {
        return getAnnotationOpt("DT");
    }

    public boolean containsFo() {
        return containsAnnotationKey("FO");
    }

    public String getFo() {
        return getAnnotation("FO");
    }

    public Optional<String> getFoOpt() {
        return getAnnotationOpt("FO");
    }

    public boolean containsKs() {
        return containsAnnotationKey("KS");
    }

    public String getKs() {
        return getAnnotation("KS");
    }

    public Optional<String> getKsOpt() {
        return getAnnotationOpt("KS");
    }

    public boolean containsLb() {
        return containsAnnotationKey("LB");
    }

    public String getLb() {
        return getAnnotation("LB");
    }

    public Optional<String> getLbOpt() {
        return getAnnotationOpt("LB");
    }
    
    public boolean containsPg() {
        return containsAnnotationKey("PG");
    }

    public String getPg() {
        return getAnnotation("PG");
    }

    public Optional<String> getPgOpt() {
        return getAnnotationOpt("PG");
    }

    public boolean containsPi() {
        return containsAnnotationKey("PI");
    }

    public String getPi() {
        return getAnnotation("PI");
    }

    public Optional<String> getPiOpt() {
        return getAnnotationOpt("PI");
    }

    public boolean containsPl() {
        return containsAnnotationKey("PL");
    }

    public String getPl() {
        return getAnnotation("PL");
    }

    public Optional<String> getPlOpt() {
        return getAnnotationOpt("PL");
    }

    public boolean containsPm() {
        return containsAnnotationKey("PM");
    }

    public String getPm() {
        return getAnnotation("PM");
    }

    public Optional<String> getPmOpt() {
        return getAnnotationOpt("PM");
    }

    public boolean containsPu() {
        return containsAnnotationKey("PU");
    }

    public String getPu() {
        return getAnnotation("PU");
    }

    public Optional<String> getPuOpt() {
        return getAnnotationOpt("PU");
    }

    public boolean containsSm() {
        return containsAnnotationKey("SM");
    }

    public String getSm() {
        return getAnnotation("SM");
    }

    public Optional<String> getSmOpt() {
        return getAnnotationOpt("SM");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getKey());

        // required annotations
        sb.append("\t");
        sb.append("ID:");
        sb.append(getId());

        // optional annotations
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

        // remaining annotations
        Set<String> remainingKeys = new HashSet<String>(getAnnotations().keySet());
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
            sb.append(getAnnotation(key));
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

        Map<String, String> annotations = parseAnnotations(value.replace("@RG", "").trim());
        if (!annotations.containsKey("ID")) {
            throw new IllegalArgumentException("required annotation ID missing");
        }
        return new SamReadGroupHeaderLine(annotations);
    }
}
