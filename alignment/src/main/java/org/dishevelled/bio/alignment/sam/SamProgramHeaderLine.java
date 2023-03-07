/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
 * SAM program header line.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamProgramHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM program header line.
     *
     * @param annotations annotation values keyed by key, must not be null
     */
    private SamProgramHeaderLine(final Map<String, String> annotations) {
        super("PG", annotations);
    }

    // required annotations

    public String getId() {
        return getAnnotation("ID");
    }

    // optional annotations

    public boolean containsPn() {
        return containsAnnotationKey("PN");
    }

    public String getPn() {
        return getAnnotation("PN");
    }

    public Optional<String> getPnOpt() {
        return getAnnotationOpt("PN");
    }

    public boolean containsCl() {
        return containsAnnotationKey("CL");
    }

    public String getCl() {
        return getAnnotation("CL");
    }

    public Optional<String> getClOpt() {
        return getAnnotationOpt("CL");
    }

    public boolean containsPp() {
        return containsAnnotationKey("PP");
    }

    public String getPp() {
        return getAnnotation("PP");
    }

    public Optional<String> getPpOpt() {
        return getAnnotationOpt("PP");
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

    public boolean containsVn() {
        return containsAnnotationKey("VN");
    }

    public String getVn() {
        return getAnnotation("VN");
    }

    public Optional<String> getVnOpt() {
        return getAnnotationOpt("VN");
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

        // remaining annotations
        Set<String> remainingKeys = new HashSet<String>(getAnnotations().keySet());
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
            sb.append(getAnnotation(key));
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

        Map<String, String> annotations = parseAnnotations(value.replace("@PG", "").trim());
        if (!annotations.containsKey("ID")) {
            throw new IllegalArgumentException("required annotation ID missing");
        }
        return new SamProgramHeaderLine(annotations);
    }
}
