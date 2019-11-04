/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.assembly.gfa.GfaTags.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

/**
 * Graphical Fragment Assembly (GFA) record.
 *
 * @author  Michael Heuer
 */
public abstract class GfaRecord {
    /** Map of tags keyed by tag name. */
    private final Map<String, Tag> tags;


    /**
     * Create a new GFA record with the specified tags.
     *
     * @param tags tags, must not be null
     */
    protected GfaRecord(final Map<String, Tag> tags) {
        checkNotNull(tags);
        this.tags = ImmutableMap.copyOf(tags);
    }


    /**
     * Return an immutable map of tags keyed by tag name
     * for this GFA record.
     *
     * @return an immutable map of tags keyed by tag name
     *    for this GFA record
     */
    public final Map<String, Tag> getTags() {
        return tags;
    }

    /**
     * Return true if this GFA record contains the specified tag key.
     *
     * @param key key
     * @return true if this GFA record contains the specified tag key
     */
    public final boolean containsTagKey(final String key) {
        return tags.containsKey(key);
    }

    /**
     * Return the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return the Type=A field value for the specified key parsed into a character
     */
    public final char getTagCharacter(final String key) {
        return parseCharacter(key, tags);
    }

    /**
     * Return the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return the Type=f field value for the specified key parsed into a float
     */
    public final float getTagFloat(final String key) {
        return parseFloat(key, tags);
    }

    /**
     * Return the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return the Type=i field value for the specified key parsed into an integer
     */
    public final int getTagInteger(final String key) {
        return parseInteger(key, tags);
    }

    /**
     * Return the Type=H field value for the specified key parsed into a byte array.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into a byte array
     */
    public final byte[] getTagByteArray(final String key) {
        return parseByteArray(key, tags);
    }

    /**
     * Return the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public final List<Byte> getTagBytes(final String key) {
        return parseBytes(key, tags);
    }

    /**
     * Return the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return the Type=Z field value for the specified key parsed into a string
     */
    public final String getTagString(final String key) {
        return parseString(key, tags);
    }

    /**
     * Return the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public final List<Float> getTagFloats(final String key) {
        return parseFloats(key, tags);
    }

    /**
     * Return the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public final List<Integer> getTagIntegers(final String key) {
        return parseIntegers(key, tags);
    }

    /**
     * Return an optional wrapping the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=A field value for the specified key parsed into a character
     */
    public final Optional<Character> getTagCharacterOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagCharacter(key) : null);
    }

    /**
     * Return an optional wrapping the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=f field value for the specified key parsed into a float
     */
    public final Optional<Float> getTagFloatOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagFloat(key) : null);
    }

    /**
     * Return an optional wrapping the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=i field value for the specified key parsed into an integer
     */
    public final Optional<Integer> getTagIntegerOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagInteger(key) : null);
    }

    /**
     * Return an optional wrapping the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=Z field value for the specified key parsed into a string
     */
    public final Optional<String> getTagStringOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagString(key) : null);
    }

    /**
     * Return an optional wrapping the Type=H field value for the specified key parsed into a byte array.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=H field value for the specified key parsed into a byte array
     */
    public final Optional<byte[]> getTagByteArrayOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagByteArray(key) : null);
    }

    /**
     * Return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public final Optional<List<Byte>> getTagBytesOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagBytes(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public final Optional<List<Float>> getTagFloatsOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagFloats(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public final Optional<List<Integer>> getTagIntegersOpt(final String key) {
        return Optional.ofNullable(containsTagKey(key) ? getTagIntegers(key) : null);
    }
}
