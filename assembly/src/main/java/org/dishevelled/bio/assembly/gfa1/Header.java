/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.gfa1;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;

/**
 * Header GFA 1.0 record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Header extends Gfa1Record {

    /**
     * Create a new header GFA 1.0 record.
     *
     * @param annotations annotations, must not be null
     */
    public Header(final Map<String, Annotation> annotations) {
        super(annotations);
    }


    // optional fields

    /**
     * Return true if the annotations for this header contain
     * the reserved key <code>VN</code>.
     *
     * @return true if the annotations for this header contain
     *    the reserved key <code>VN</code>
     */
    public boolean containsVn() {
        return containsAnnotationKey("VN");
    }

    /**
     * Return the Type=Z value for the reserved key <code>VN</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>VN</code>
     *    as a string
     */
    public String getVn() {
        return getAnnotationString("VN");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>VN</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>VN</code>
     *   as a string
     */
    public Optional<String> getVnOpt() {
        return getAnnotationStringOpt("Vn");
    }

    /**
     * Return true if the annotations for this header contain
     * the reserved key <code>VN</code>, for version number.
     *
     * @return true if the annotations for this header contain
     *    the reserved key <code>VN</code>, for version number
     */
    public boolean containsVersionNumber() {
        return containsVn();
    }

    /**
     * Return the version number for this header (Type=Z value for the
     * reserved key <code>VN</code> as a string).
     *
     * @return the version number for this header (Type=Z value for the
     *    reserved key <code>VN</code> as a string)
     */
    public String getVersionNumber() {
        return getVn();
    }

    /**
     * Return an optional wrapping the version number for this header
     * (Type Z value for the reserved key <code>VN</code> as a string).
     *
     * @return an optional wrapping the version number for this header
     *    (Type Z value for the reserved key <code>VN</code> as a string)
     */
    public Optional<String> getVersionNumberOpt() {
        return getVnOpt();
    }


    @Override
    public int hashCode() {
        return getAnnotations().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Header)) {
            return false;
        }
        Header h = (Header) o;

        return getAnnotations().equals(h.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        sb.append("H");
        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }


    /**
     * Parse a header GFA 1.0 record from the specified value.
     *
     * @param value value, must not be null
     * @return a header GFA 1.0 record parsed from the specified value
     */
    public static Header valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("H"), "header value must start with H");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(tokens.get(i));
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new Header(annotations.build());
    }
}
