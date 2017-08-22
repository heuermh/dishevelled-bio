/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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
package org.dishevelled.bio.variant.vcf;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.variant.vcf.VcfAttributes.*;

import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF genotype.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfGenotype {
    /** Reference allele. */
    private final String ref;

    /** Array of alternate alleles. */
    private final String[] alt;

    /** Genotype fields. */
    private final ListMultimap<String, String> fields;


    /**
     * Create a new VCF genotype with the specified genotype fields.
     *
     * @param ref reference allele, must not be null
     * @param alt array of alternate alleles, must not be null
     * @param fields genotype fields, must not be null and must contain strictly one GT genotype field
     */
    private VcfGenotype(final String ref,
                        final String[] alt,
                        final ListMultimap<String, String> fields) {

        checkNotNull(ref, "ref must not be null");
        checkNotNull(alt, "alt must not be null");
        checkNotNull(fields, "fields must not be null");

        // check GT cardinality constraint
        checkArgument(fields.containsKey("GT"), "GT genotype field is required");
        checkArgument(fields.get("GT").size() == 1, "GT genotype field cardinality is strictly one, found " + fields.get("GT").size());

        this.ref = ref;
        this.alt = alt;
        this.fields = fields;
    }


    /**
     * Return the value of the GT genotype field for this VCF genotype.
     *
     * @return the value of the GT genotype field for this VCF genotype.
     */
    public String getGt() {
        return fields.get("GT").get(0);
    }

    /**
     * Return the reference allele for this VCF genotype.
     *
     * @return the reference allele for this VCF genotype
     */
    public String getRef() {
        return ref;
    }

    /**
     * Return the alternate alleles for this VCF genotype.
     *
     * @return the alternate alleles for this VCF genotype
     */
    public String[] getAlt() {
        return alt;
    }

    /**
     * Return the genotype fields for this VCF genotype.
     *
     * @return the genotype fields for this VCF genotype
     */
    public ListMultimap<String, String> getFields() {
        return fields;
    }


    // genotype fields for VCF FORMAT reserved keys

    /**
     * Return the count for Number=A attributes for this VCF genotype.
     *
     * @return the count for Number=A attributes for this VCF genotype
     */
    public int a() {
        return alt.length;
    }

    /**
     * Return the count for Number=R attributes for this VCF genotype.
     *
     * @return the count for Number=R attributes for this VCF genotype
     */
    public int r() {
        return a() + 1;
    }

    /**
     * Return the count for Number=G attributes for this VCF genotype.
     *
     * @return the count for Number=G attributes for this VCF genotype
     */
    public int g() { return -1; }


    // genotype fields for VCF FORMAT non-reserved keys

    /**
     * Return true if the genotype fields for this VCF genotype contain
     * the specified key.
     *
     * @param key key, must not be null
     * @return true if the genotype fields for this VCF genotype contain
     *    the specified key
     */
    public boolean containsFieldKey(final String key) {
        return fields.containsKey(key);
    }


    /**
     * Return the Number=1 Type=Character value for the specified key
     * as a character.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Character value for the specified key
     *    as a character
     */
    public char getFieldCharacter(final String key) {
        return parseCharacter(key, fields);
    }

    /**
     * Return the Number=1 Type=Float value for the specified key
     * as a float.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Float value for the specified key
     *    as a float
     */
    public float getFieldFloat(final String key) {
        return parseFloat(key, fields);
    }

    /**
     * Return the Number=1 Type=Integer value for the specified key
     * as an integer.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Integer value for the specified key
     *    as an integer
     */
    public int getFieldInteger(final String key) {
        return parseInteger(key, fields);
    }

    /**
     * Return the Number=1 Type=String value for the specified key
     * as a string.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Float value for the specified key
     *    as a string
     */
    public String getFieldString(final String key) {
        return parseString(key, fields);
    }


    /**
     * Return the Number=&#46; Type=Character value for the specified key
     * as an immutable list of characters.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=Character value for the specified key
     *    as an immutable list of characters
     */
    public List<Character> getFieldCharacters(final String key) {
        return parseCharacters(key, fields);
    }

    /**
     * Return the Number=&#46; Type=Float value for the specified key
     * as an immutable list of floats.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=Float value for the specified key
     *    as an immutable list of floats
     */
    public List<Float> getFieldFloats(final String key) {
        return parseFloats(key, fields);
    }

    /**
     * Return the Number=&#46; Type=Integer value for the specified key
     * as an immutable list of integers.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=Integer value for the specified key
     *    as an immutable list of integers
     */
    public List<Integer> getFieldIntegers(final String key) {
        return parseIntegers(key, fields);
    }

    /**
     * Return the Number=&#46; Type=String value for the specified key
     * as an immutable list of strings.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=String value for the specified key
     *    as an immutable list of strings
     */
    public List<String> getFieldStrings(final String key) {
        return parseStrings(key, fields);
    }


    /**
     * Return the Number=[n, A, R, G] Type=Character value for the specified key
     * as an immutable list of characters of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R, G] Type=Character value for the specified key
     *    as an immutable list of characters of size equal to the specified number
     */
    public List<Character> getFieldCharacters(final String key, final int number) {
        return parseCharacters(key, number, fields);
    }

    /**
     * Return the Number=[n, A, R, G] Type=Float value for the specified key
     * as an immutable list of floats of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R, G] Type=Float value for the specified key
     *    as an immutable list of floats of size equal to the specified number
     */
    public List<Float> getFieldFloats(final String key, final int number) {
        return parseFloats(key, number, fields);
    }

    /**
     * Return the Number=[n, A, R, G] Type=Integer value for the specified key
     * as an immutable list of integers of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R, G] Type=Integer value for the specified key
     *    as an immutable list of integers of size equal to the specified number
     */
    public List<Integer> getFieldIntegers(final String key, final int number) {
        return parseIntegers(key, number, fields);
    }

    /**
     * Return the Number=[n, A, R, G] Type=String value for the specified key
     * as an immutable list of strings of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R, G] Type=String value for the specified key
     *    as an immutable list of strings of size equal to the specified number
     */
    public List<String> getFieldStrings(final String key, final int number) {
        return parseStrings(key, number, fields);
    }


    /**
     * Return an optional Number=1 Type=Character value for the specified key
     * as a character.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=Character value for the specified key
     *    as a character
     */
    public Optional<Character> getFieldCharacterOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldCharacter(key) : null);
    }

    /**
     * Return an optional Number=1 Type=Float value for the specified key
     * as a float.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=Float value for the specified key
     *    as a float
     */
    public Optional<Float> getFieldFloatOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldFloat(key) : null);
    }

    /**
     * Return an optional Number=1 Type=Integer value for the specified key
     * as an integer.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=Integer value for the specified key
     *    as an integer
     */
    public Optional<Integer> getFieldIntegerOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldInteger(key) : null);
    }

    /**
     * Return an optional Number=1 Type=String value for the specified key
     * as a string.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=String value for the specified key
     *    as a string
     */
    public Optional<String> getFieldStringOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldString(key) : null);
    }


    /**
     * Return an optional Number=&#46; Type=Character value for the specified key
     * as an immutable list of characters.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=Character value for the specified key
     *    as an immutable list of characters
     */
    public Optional<List<Character>> getFieldCharactersOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldCharacters(key) : null);
    }

    /**
     * Return an optional Number=&#46; Type=Float value for the specified key
     * as an immutable list of floats.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=Float value for the specified key
     *    as an immutable list of floats
     */
    public Optional<List<Float>> getFieldFloatsOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldFloats(key) : null);
    }

    /**
     * Return an optional Number=&#46; Type=Integer value for the specified key
     * as an immutable list of integers.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=Integer value for the specified key
     *    as an immutable list of integers
     */
    public Optional<List<Integer>> getFieldIntegersOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldIntegers(key) : null);
    }

    /**
     * Return an optional Number=&#46; Type=String value for the specified key
     * as an immutable list of strings.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=String value for the specified key
     *    as an immutable list of strings
     */
    public Optional<List<String>> getFieldStringsOpt(final String key) {
       return Optional.ofNullable(containsFieldKey(key) ? getFieldStrings(key) : null);
    }


    /**
     * Return an optional Number=[n, A, R, G] Type=Character value for the specified key
     * as an immutable list of characters of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R, G] Type=Character value for the specified key
     *    as an immutable list of characters of size equal to the specified number
     */
    public Optional<List<Character>> getFieldCharactersOpt(final String key, final int number) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldCharacters(key, number) : null);
    }

    /**
     * Return an optional Number=[n, A, R, G] Type=Float value for the specified key
     * as an immutable list of floats of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R, G] Type=Float value for the specified key
     *    as an immutable list of floats of size equal to the specified number
     */
    public Optional<List<Float>> getFieldFloatsOpt(final String key, final int number) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloats(key, number) : null);
    }

    /**
     * Return an optional Number=[n, A, R, G] Type=Integer value for the specified key
     * as an immutable list of integers of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R, G] Type=Integer value for the specified key
     *    as an immutable list of integers of size equal to the specified number
     */
    public Optional<List<Integer>> getFieldIntegersOpt(final String key, final int number) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldIntegers(key, number) : null);
    }

    /**
     * Return an optional Number=[n, A, R, G] Type=String value for the specified key
     * as an immutable list of strings of size equal to the specified number.  For the
     * count for Number=A, Number=R, and Number=G attributes for this VCF genotype, use the
     * methods <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @see #g()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R, G] Type=String value for the specified key
     *    as an immutable list of strings of size equal to the specified number
     */
    public Optional<List<String>> getFieldStringsOpt(final String key, final int number) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldStrings(key, number) : null);
    }


    /**
     * Create and return a new VCF genotype builder.
     *
     * @return a new VCF genotype builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create and return a new VCF genotype builder populated from the fields in the specified VCF genotype.
     *
     * @param genotype VCF genotype, must not be null
     * @return a new VCF genotype builder populated from the fields in the specified VCF genotype
     */
    public static Builder builder(final VcfGenotype genotype) {
        checkNotNull(genotype, "genotype must not be null");
        return new Builder()
            .withRef(genotype.getRef())
            .withAlt(genotype.getAlt())
            .withFields(genotype.getFields());
    }

    /**
     * VCF genotype builder.
     */
    public static final class Builder {
        /** Reference allele. */
        private String ref;

        /** Array of alternate alleles. */
        private String[] alt;

        /** Genotype fields. */
        private ImmutableListMultimap.Builder<String, String> fields = ImmutableListMultimap.builder();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF genotype builder configured with the specified reference allele.
         *
         * @param ref reference allele
         * @return this VCF genotype builder configured with the specified reference allele
         */
        public Builder withRef(final String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified alternate alleles.
         *
         * @param alt alternate alleles
         * @return this VCF genotype builder configured with the specified alternate alleles
         */
        public Builder withAlt(final String... alt) {
            this.alt = alt;
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype field.
         *
         * @param id genotype field id, must not be null
         * @param values genotype field values, must not be null
         * @return this VCF genotype builder configured with the specified genotype field
         */
        public Builder withField(final String id, final String... values) {
            checkNotNull(values);
            for (String value : values) {
                fields.put(id, value);
            }
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype fields.
         *
         * @param fields genotype fields, must not be null
         * @return this VCF genotype builder configured with the specified genotype fields
         */
        public Builder withFields(final ListMultimap<String, String> fields) {
            this.fields.putAll(fields);
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype field
         * replacing the previously configured value(s).  Use sparingly, more expensive than
         * <code>withFields</code>.
         *
         * @param id genotype field id, must not be null
         * @param values genotype field values, must not be null
         * @return this VCF genotype builder configured with the specified genotype field
         *    replacing the previously configured value(s)
         */
        public Builder replaceField(final String id, final String... values) {
            checkNotNull(values);

            // copy old field values except id
            ListMultimap<String, String> oldFields = this.fields.build();
            this.fields = ImmutableListMultimap.builder();
            for (String key : oldFields.keys()) {
                if (!key.equals(id)) {
                    this.fields.putAll(key, oldFields.get(key));
                }
            }
            // add new value(s)
            for (String value : values) {
                fields.put(id, value);
            }
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype fields.
         * Use sparingly, more expensive than <code>withFields</code>.
         *
         * @param fields genotype fields, must not be null
         * @return this VCF genotype builder configured with the specified genotype fields
         */
        public Builder replaceFields(final ListMultimap<String, String> fields) {
            this.fields = ImmutableListMultimap.builder();
            this.fields.putAll(fields);
            return this;
        }

        /**
         * Reset this VCF genotype builder.
         *
         * @return this VCF genotype builder
         */
        public Builder reset() {
            ref = null;
            alt = null;
            fields = ImmutableListMultimap.builder();
            return this;
        }

        /**
         * Create and return a new VCF genotype populated from the configuration of this VCF genotype builder.
         *
         * @return a new VCF genotype populated from the configuration of this VCF genotype builder
         */
        public VcfGenotype build() {
            return new VcfGenotype(ref, alt, fields.build());
        }
    }
}
