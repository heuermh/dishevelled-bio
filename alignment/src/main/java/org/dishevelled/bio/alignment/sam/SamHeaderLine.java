/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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
 * SAM header line.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM header line.
     *
     * @param annotations annotation values keyed by key, must not be null
     */
    private SamHeaderLine(final Map<String, String> annotations) {
        super("HD", annotations);
    }

    // required annotations

    public String getVn() {
        return getAnnotation("VN");
    }

    // optional annotations

    public boolean containsSo() {
        return containsAnnotationKey("SO");
    }

    public String getSo() {
        return getAnnotation("SO");
    }

    public Optional<String> getSoOpt() {
        return getAnnotationOpt("SO");
    }

    public boolean containsGo() {
        return containsAnnotationKey("GO");
    }

    public String getGo() {
        return getAnnotation("GO");
    }

    public Optional<String> getGoOpt() {
        return getAnnotationOpt("GO");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getKey());

        // required annotations
        sb.append("\t");
        sb.append("VN:");
        sb.append(getVn());

        // optional annotations
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

        // remaining annotations
        Set<String> remainingKeys = new HashSet<String>(getAnnotations().keySet());
        remainingKeys.remove("VN");
        remainingKeys.remove("SO");
        remainingKeys.remove("GO");

        for (String key : remainingKeys) {
            sb.append("\t");
            sb.append(key);
            sb.append(":");
            sb.append(getAnnotation(key));
        }

        return sb.toString();
    }

    /**
     * Parse the specified value into a SAM header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a SAM header line
     */
    public static SamHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("@HD"));

        Map<String, String> annotations = parseAnnotations(value.replace("@HD", "").trim());
        if (!annotations.containsKey("VN")) {
            throw new IllegalArgumentException("required annotation VN missing");
        }
        return new SamHeaderLine(annotations);
    }
}
