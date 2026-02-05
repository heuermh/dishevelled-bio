/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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
 * SAM sequence header line.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamSequenceHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM sequence header line.
     *
     * @param annotations annotation values keyed by key, must not be null
     */
    private SamSequenceHeaderLine(final Map<String, String> annotations) {
        super("SQ", annotations);
    }

    // required annotations

    public String getSn() {
        return getAnnotation("SN");
    }

    public String getLn() {
        return getAnnotation("LN");
    }

    // optional annotations

    public boolean containsAs() {
        return containsAnnotationKey("AS");
    }

    public String getAs() {
        return getAnnotation("AS");
    }

    public Optional<String> getAsOpt() {
        return getAnnotationOpt("AS");
    }

    public boolean containsM5() {
        return containsAnnotationKey("M5");
    }

    public String getM5() {
        return getAnnotation("M5");
    }

    public Optional<String> getM5Opt() {
        return getAnnotationOpt("M5");
    }

    public boolean containsSp() {
        return containsAnnotationKey("SP");
    }

    public String getSp() {
        return getAnnotation("SP");
    }

    public Optional<String> getSpOpt() {
        return getAnnotationOpt("SP");
    }

    public boolean containsUr() {
        return containsAnnotationKey("UR");
    }

    public String getUr() {
        return getAnnotation("UR");
    }

    public Optional<String> getUrOpt() {
        return getAnnotationOpt("UR");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getKey());

        // required annotations
        sb.append("\t");
        sb.append("SN:");
        sb.append(getSn());
        sb.append("\t");
        sb.append("LN:");
        sb.append(getLn());

        // optional annotations
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

        // remaining annotations
        Set<String> remainingKeys = new HashSet<String>(getAnnotations().keySet());
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
            sb.append(getAnnotation(key));
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

        Map<String, String> annotations = parseAnnotations(value.replace("@SQ", "").trim());
        if (!annotations.containsKey("SN")) {
            throw new IllegalArgumentException("required annotation SN missing");
        }
        if (!annotations.containsKey("LN")) {
            throw new IllegalArgumentException("required annotation LN missing");
        }
        return new SamSequenceHeaderLine(annotations);
    }
}
