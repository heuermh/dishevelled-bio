/*

    dsh-bio-annotation  Support for SAM-style annotation fields.
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
package org.dishevelled.bio.annotation;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.annotation.Annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

/**
 * Abstract record with annotation fields.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
public abstract class AnnotatedRecord {
    /** Map of annotations keyed by annotation name. */
    private final Map<String, Annotation> annotations;


    /**
     * Create a new annotated record with the specified annotations.
     *
     * @param annotations annotations, must not be null
     */
    protected AnnotatedRecord(final Map<String, Annotation> annotations) {
        checkNotNull(annotations);
        this.annotations = ImmutableMap.copyOf(annotations);
    }


    /**
     * Return an immutable map of annotations keyed by annotation name
     * for this record.
     *
     * @return an immutable map of annotations keyed by annotation name
     *    for this record
     */
    public final Map<String, Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Return the annotation for the specified key for this record, if any.
     *
     * @param key key, must not be null
     * @return the annotation for the specified key for this record, if any
     */
    public final Annotation getAnnotation(final String key) {
        checkNotNull(key);
        return annotations.get(key);
    }

    /**
     * Return an optional wrapping the annotation for the specified key
     * for this record.
     *
     * @param key key, must not be null
     * @return an optional wrapping the annotation for the specified key
     *    for this record
     */
    public final Optional<Annotation> getAnnotationOpt(final String key) {
        return Optional.ofNullable(getAnnotation(key));
    }

    /**
     * Return true if this record contains the specified annotation key.
     *
     * @param key key
     * @return true if this record contains the specified annotation key
     */
    public final boolean containsAnnotationKey(final String key) {
        return annotations.containsKey(key);
    }

    /**
     * Return the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return the Type=A field value for the specified key parsed into a character
     */
    public final char getAnnotationCharacter(final String key) {
        return parseCharacter(key, annotations);
    }

    /**
     * Return the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return the Type=f field value for the specified key parsed into a float
     */
    public final float getAnnotationFloat(final String key) {
        return parseFloat(key, annotations);
    }

    /**
     * Return the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return the Type=i field value for the specified key parsed into an integer
     */
    public final int getAnnotationInteger(final String key) {
        return parseInteger(key, annotations);
    }

    /**
     * Return the Type=H field value for the specified key parsed into a byte array.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into a byte array
     */
    public final byte[] getAnnotationByteArray(final String key) {
        return parseByteArray(key, annotations);
    }

    /**
     * Return the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public final List<Byte> getAnnotationBytes(final String key) {
        return parseBytes(key, annotations);
    }

    /**
     * Return the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return the Type=Z field value for the specified key parsed into a string
     */
    public final String getAnnotationString(final String key) {
        return parseString(key, annotations);
    }

    /**
     * Return the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public final List<Float> getAnnotationFloats(final String key) {
        return parseFloats(key, annotations);
    }

    /**
     * Return the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public final List<Integer> getAnnotationIntegers(final String key) {
        return parseIntegers(key, annotations);
    }

    /**
     * Return an optional wrapping the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=A field value for the specified key parsed into a character
     */
    public final Optional<Character> getAnnotationCharacterOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationCharacter(key) : null);
    }

    /**
     * Return an optional wrapping the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=f field value for the specified key parsed into a float
     */
    public final Optional<Float> getAnnotationFloatOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationFloat(key) : null);
    }

    /**
     * Return an optional wrapping the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=i field value for the specified key parsed into an integer
     */
    public final Optional<Integer> getAnnotationIntegerOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationInteger(key) : null);
    }

    /**
     * Return an optional wrapping the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=Z field value for the specified key parsed into a string
     */
    public final Optional<String> getAnnotationStringOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationString(key) : null);
    }

    /**
     * Return an optional wrapping the Type=H field value for the specified key parsed into a byte array.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=H field value for the specified key parsed into a byte array
     */
    public final Optional<byte[]> getAnnotationByteArrayOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationByteArray(key) : null);
    }

    /**
     * Return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public final Optional<List<Byte>> getAnnotationBytesOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationBytes(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public final Optional<List<Float>> getAnnotationFloatsOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationFloats(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public final Optional<List<Integer>> getAnnotationIntegersOpt(final String key) {
        return Optional.ofNullable(containsAnnotationKey(key) ? getAnnotationIntegers(key) : null);
    }
}
