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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

/**
 * Abstract SAM header line.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
abstract class AbstractSamHeaderLine {
    /** Header line tag. */
    private final String tag;

    /** Optional field values keyed by tag. */
    private final Map<String, String> fields;


    /**
     * Create a new abstract SAM header line with the specified tag and fields.
     *
     * @param tag tag, must not be null
     * @param fields field values keyed by tag, must not be null
     */
    protected AbstractSamHeaderLine(final String tag, final Map<String, String> fields) {
        checkNotNull(tag);
        checkNotNull(fields);
        this.tag = tag;
        this.fields = ImmutableMap.copyOf(fields);
    }


    /**
     * Return the tag for this SAM header line.
     *
     * @return the tag for this SAM header line
     */
    public final String getTag() {
        return tag;
    }

    /**
     * Return the field values for this SAM header line keyed by tag.
     *
     * @return the field values for this SAM header line keyed by tag
     */
    public final Map<String, String> getFields() {
        return fields;
    }

    /**
     * Return true if this SAM record contains the specified optional field key.
     *
     * @param key key
     * @return true if this SAM record contains the specified optional field key
     */
    public final boolean containsFieldKey(final String key) {
        return fields.containsKey(key);
    }

    /**
     * Return the field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return the field value for the specified key parsed into a string
     */
    public final String getField(final String key) {
        return fields.get(key);
    }

    /**
     * Return an optional wrapping the field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return an optional wrapping the field value for the specified key parsed into a string
     */
    public final Optional<String> getFieldOpt(final String key) {
        return Optional.ofNullable(getField(key));
    }
}
