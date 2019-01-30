/*

    dsh-bio-variant  Variants.
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

    /** Count for Number=A attributes. */
    private final int a;

    /** Count for Number=R attributes. */
    private final int r;

    /** Count for Number=G attributes. */
    private final int g;


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

        this.a = alt.length;
        this.r = this.a + 1;
        this.g = numberG(this);
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
        return a;
    }

    /**
     * Return the count for Number=R attributes for this VCF genotype.
     *
     * @return the count for Number=R attributes for this VCF genotype
     */
    public int r() {
        return r;
    }

    /**
     * Return the count for Number=G attributes for this VCF genotype.
     *
     * @return the count for Number=G attributes for this VCF genotype
     */
    public int g() {
        return g;
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>AD</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>AD</code>
     */
    public boolean containsAd() {
        return containsFieldKey("AD");
    }

    /**
     * Return the Number=R Type=Integer value for the VCF genotype field
     * reserved key <code>AD</code> as an immutable list of integers.
     *
     * @return the Number=R Type=Integer value for the VCF genotype field
     *    reserved key <code>AD</code> as an immutable list of integers.
     */
    public List<Integer> getAd() {
        return getFieldIntegers("AD", r);
    }

    /**
     * Return an optional Number=R Type=Integer value for the VCF genotype field
     * reserved key <code>AD</code> as an immutable list of integers.
     *
     * @return an optional Number=R Type=Integer value for the VCF genotype field
     *    reserved key <code>AD</code> as an immutable list of integers
     */
    public Optional<List<Integer>> getAdOpt() {
        return getFieldIntegersOpt("AD", r);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>ADF</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>ADF</code>
     */
    public boolean containsAdf() {
        return containsFieldKey("ADF");
    }

    /**
     * Return the Number=R Type=Integer value for the VCF genotype field
     * reserved key <code>ADF</code> as an immutable list of integers.
     *
     * @return the Number=R Type=Integer value for the VCF genotype field
     *    reserved key <code>ADF</code> as an immutable list of integers.
     */
    public List<Integer> getAdf() {
        return getFieldIntegers("ADF", r);
    }

    /**
     * Return an optional Number=R Type=Integer value for the VCF genotype field
     * reserved key <code>ADF</code> as an immutable list of integers.
     *
     * @return an optional Number=R Type=Integer value for the VCF genotype field
     *    reserved key <code>ADF</code> as an immutable list of integers
     */
    public Optional<List<Integer>> getAdfOpt() {
        return getFieldIntegersOpt("ADF", r);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>ADR</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>ADR</code>
     */
    public boolean containsAdr() {
        return containsFieldKey("ADR");
    }

    /**
     * Return the Number=R Type=Integer value for the VCF genotype field
     * reserved key <code>ADR</code> as an immutable list of integers.
     *
     * @return the Number=R Type=Integer value for the VCF genotype field
     *    reserved key <code>ADR</code> as an immutable list of integers.
     */
    public List<Integer> getAdr() {
        return getFieldIntegers("ADR", r);
    }

    /**
     * Return an optional Number=R Type=Integer value for the VCF genotype field
     * reserved key <code>ADR</code> as an immutable list of integers.
     *
     * @return an optional Number=R Type=Integer value for the VCF genotype field
     *    reserved key <code>ADR</code> as an immutable list of integers
     */
    public Optional<List<Integer>> getAdrOpt() {
        return getFieldIntegersOpt("ADR", r);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>DP</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>DP</code>
     */
    public boolean containsDp() {
        return containsFieldKey("DP");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>DP</code> as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>DP</code> as an integer
     */
    public int getDp() {
        return getFieldInteger("DP");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>DP</code> as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>DP</code> as an integer
     */
    public Optional<Integer> getDpOpt() {
        return getFieldIntegerOpt("DP");
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>EC</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>EC</code>
     */
    public boolean containsEc() {
        return containsFieldKey("EC");
    }

    /**
     * Return the Number=A Type=Integer value for the VCF genotype field
     * reserved key <code>EC</code> as an immutable list of integers.
     *
     * @return the Number=A Type=Integer value for the VCF genotype field
     *    reserved key <code>EC</code> as an immutable list of integers.
     */
    public List<Integer> getEc() {
        return getFieldIntegers("EC", a);
    }

    /**
     * Return an optional Number=A Type=Integer value for the VCF genotype field
     * reserved key <code>EC</code> as an immutable list of integers.
     *
     * @return an optional Number=A Type=Integer value for the VCF genotype field
     *    reserved key <code>EC</code> as an immutable list of integers
     */
    public Optional<List<Integer>> getEcOpt() {
        return getFieldIntegersOpt("ADR", a);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>FT</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>FT</code>
     */
    public boolean containsFt() {
        return containsFieldKey("FT");
    }

    /**
     * Return the Number=1 Type=String value for the VCF genotype field
     * reserved key <code>FT</code> as a string.
     *
     * @return the Number=1 Type=String value for the VCF genotype field
     *    reserved key <code>FT</code> as a string
     */
    public String getFt() {
        return getFieldString("FT");
    }

    /**
     * Return an optional Number=1 Type=String value for the VCF genotype field
     * reserved key <code>FT</code> as a string.
     *
     * @return an optional Number=1 Type=String value for the VCF genotype field
     *    reserved key <code>FT</code> as a string
     */
    public Optional<String> getFtOpt() {
        return getFieldStringOpt("FT");
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>GL</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>GL</code>
     */
    public boolean containsGl() {
        return containsFieldKey("GL");
    }

    /**
     * Return the Number=G Type=Float value for the VCF genotype field
     * reserved key <code>GL</code> as an immutable list of floats.
     *
     * @return the Number=G Type=Float value for the VCF genotype field
     *    reserved key <code>GL</code> as an immutable list of floats.
     */
    public List<Float> getGl() {
        return getFieldFloats("GL", g);
    }

    /**
     * Return an optional Number=G Type=Float value for the VCF genotype field
     * reserved key <code>GL</code> as an immutable list of floats.
     *
     * @return an optional Number=G Type=Float value for the VCF genotype field
     *    reserved key <code>GL</code> as an immutable list of floats
     */
    public Optional<List<Float>> getGlOpt() {
        return getFieldFloatsOpt("GL", g);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>GP</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>GP</code>
     */
    public boolean containsGp() {
        return containsFieldKey("GP");
    }

    /**
     * Return the Number=G Type=Float value for the VCF genotype field
     * reserved key <code>GP</code> as an immutable list of floats.
     *
     * @return the Number=G Type=Float value for the VCF genotype field
     *    reserved key <code>GP</code> as an immutable list of floats.
     */
    public List<Float> getGp() {
        return getFieldFloats("GP", g);
    }

    /**
     * Return an optional Number=G Type=Float value for the VCF genotype field
     * reserved key <code>GP</code> as an immutable list of floats.
     *
     * @return an optional Number=G Type=Float value for the VCF genotype field
     *    reserved key <code>GP</code> as an immutable list of floats
     */
    public Optional<List<Float>> getGpOpt() {
        return getFieldFloatsOpt("GP", g);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>GQ</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>GQ</code>
     */
    public boolean containsGq() {
        return containsFieldKey("GQ");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>GQ</code> as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>GQ</code> as an integer
     */
    public int getGq() {
        return getFieldInteger("GQ");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>GQ</code> as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>GQ</code> as an integer
     */
    public Optional<Integer> getGqOpt() {
        return getFieldIntegerOpt("GQ");
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>GT</code>. Always returns true.
     *
     * @return true
     */
    public boolean containsGt() {
        return true;
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>HQ</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>HQ</code>
     */
    public boolean containsHq() {
        return containsFieldKey("HQ");
    }

    /**
     * Return the Number=2 Type=Integer value for the VCF genotype field
     * reserved key <code>HQ</code> as an immutable list of integers.
     *
     * @return the Number=2 Type=Integer value for the VCF genotype field
     *    reserved key <code>HQ</code> as an immutable list of integers.
     */
    public List<Integer> getHq() {
        return getFieldIntegers("HQ", 2);
    }

    /**
     * Return an optional Number=2 Type=Integer value for the VCF genotype field
     * reserved key <code>HQ</code> as an immutable list of integers.
     *
     * @return an optional Number=2 Type=Integer value for the VCF genotype field
     *    reserved key <code>HQ</code> as an immutable list of integers
     */
    public Optional<List<Integer>> getHqOpt() {
        return getFieldIntegersOpt("HQ", 2);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>MQ</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>MQ</code>
     */
    public boolean containsMq() {
        return containsFieldKey("MQ");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>MQ</code> as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>MQ</code> as an integer
     */
    public int getMq() {
        return getFieldInteger("MQ");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>MQ</code> as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>MQ</code> as an integer
     */
    public Optional<Integer> getMqOpt() {
        return getFieldIntegerOpt("MQ");
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>PL</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>PL</code>
     */
    public boolean containsPl() {
        return containsFieldKey("PL");
    }

    /**
     * Return the Number=G Type=Integer value for the VCF genotype field
     * reserved key <code>PL</code> as an immutable list of integers.
     *
     * @return the Number=G Type=Integer value for the VCF genotype field
     *    reserved key <code>PL</code> as an immutable list of integers.
     */
    public List<Integer> getPl() {
        return getFieldIntegers("PL", g);
    }

    /**
     * Return an optional Number=G Type=Integer value for the VCF genotype field
     * reserved key <code>PL</code> as an immutable list of integers.
     *
     * @return an optional Number=G Type=Integer value for the VCF genotype field
     *    reserved key <code>PL</code> as an immutable list of integers
     */
    public Optional<List<Integer>> getPlOpt() {
        return getFieldIntegersOpt("PL", g);
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>PQ</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>PQ</code>
     */
    public boolean containsPq() {
        return containsFieldKey("PQ");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>PQ</code> as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>PQ</code> as an integer
     */
    public int getPq() {
        return getFieldInteger("PQ");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>PQ</code> as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>PQ</code> as an integer
     */
    public Optional<Integer> getPqOpt() {
        return getFieldIntegerOpt("PQ");
    }

    /**
     * Return true if the genotype fields for this VCF genotype contains
     * the VCF genotype field reserved key <code>PS</code>.
     *
     * @return true if the genotype fields for this VCF genotype contains
     *    the VCF genotype field reserved key <code>PS</code>
     */
    public boolean containsPs() {
        return containsFieldKey("PS");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>PS</code> as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>PS</code> as an integer
     */
    public int getPs() {
        return getFieldInteger("PS");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF genotype field
     * reserved key <code>PS</code> as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF genotype field
     *    reserved key <code>PS</code> as an integer
     */
    public Optional<Integer> getPsOpt() {
        return getFieldIntegerOpt("PS");
    }


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
