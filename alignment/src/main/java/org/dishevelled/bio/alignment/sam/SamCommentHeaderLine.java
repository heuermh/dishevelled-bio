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

import java.util.Map;

import javax.annotation.concurrent.Immutable;

/**
 * SAM comment header line.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamCommentHeaderLine extends AbstractSamHeaderLine {

    /**
     * Create a new SAM comment header line.
     *
     * @param annotations annotation values keyed by key, must not be null
     */
    private SamCommentHeaderLine(final Map<String, String> annotations) {
        super("CO", annotations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getKey());

        for (Map.Entry<String, String> entry : getAnnotations().entrySet()) {
            sb.append("\t");
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * Parse the specified value into a SAM comment header line.
     *
     * @param value value, must not be null
     * @return the specified value parsed into a SAM comment header line
     */
    public static SamCommentHeaderLine valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("@CO"));

        Map<String, String> annotations = parseAnnotations(value.replace("@CO", "").trim());
        return new SamCommentHeaderLine(annotations);
    }
}
