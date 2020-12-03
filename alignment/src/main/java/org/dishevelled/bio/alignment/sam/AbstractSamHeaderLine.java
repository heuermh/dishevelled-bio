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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

/**
 * Abstract SAM header line.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
abstract class AbstractSamHeaderLine {
    /** Header line key. */
    private final String key;

    /** Optional annotation values keyed by key. */
    private final Map<String, String> annotations;


    /**
     * Create a new abstract SAM header line with the specified key and annotations.
     *
     * @param key key, must not be null
     * @param annotations annotation values keyed by key, must not be null
     */
    protected AbstractSamHeaderLine(final String key, final Map<String, String> annotations) {
        checkNotNull(key);
        checkNotNull(annotations);
        this.key = key;
        this.annotations = ImmutableMap.copyOf(annotations);
    }


    /**
     * Return the key for this SAM header line.
     *
     * @return the key for this SAM header line
     */
    public final String getKey() {
        return key;
    }

    /**
     * Return the annotation values for this SAM header line keyed by key.
     *
     * @return the annotation values for this SAM header line keyed by key
     */
    public final Map<String, String> getAnnotations() {
        return annotations;
    }

    /**
     * Return true if this SAM record contains the specified optional annotation key.
     *
     * @param key key
     * @return true if this SAM record contains the specified optional annotation key
     */
    public final boolean containsAnnotationKey(final String key) {
        return annotations.containsKey(key);
    }

    /**
     * Return the annotation value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return the annotation value for the specified key parsed into a string
     */
    public final String getAnnotation(final String key) {
        return annotations.get(key);
    }

    /**
     * Return an optional wrapping the annotation value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return an optional wrapping the annotation value for the specified key parsed into a string
     */
    public final Optional<String> getAnnotationOpt(final String key) {
        return Optional.ofNullable(getAnnotation(key));
    }

    /**
     * Parse SAM header annotations.
     *
     * @param value value to parse, must not be null
     * @return map of SAM header annotations
     */
    protected static final Map<String, String> parseAnnotations(final String value) {
        checkNotNull(value);
        ImmutableMap.Builder<String, String> annotations = ImmutableMap.builder();
        String[] tokens = value.split("\t");
        for (String token : tokens) {
            if (token.length() < 4) {
                throw new IllegalArgumentException("invalid annotation " + token + ", must have at least four characters, e.g. AH:*");
            }
            String k = token.substring(0, 2);
            String v = token.substring(3);
            annotations.put(k, v);
        }
        return annotations.build();
    }
}
