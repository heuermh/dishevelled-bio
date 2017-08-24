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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

/**
 * VCF record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfRecord {
    /** Line number. */
    private final long lineNumber;

    /** Chromosome. */
    private final String chrom;

    /** Position. */
    private final long pos;

    /** Array of ids. */
    private final String[] id;

    /** Reference allele. */
    private final String ref;

    /** Array of alternate alleles. */
    private final String[] alt;

    /** QUAL score. */
    private final Double qual;

    /** Filter. */
    private final String[] filter;

    /** INFO key-value(s) pairs. */
    private final ListMultimap<String, String> info;

    /** Format. */
    private final String[] format;

    /** Genotypes keyed by sample id. */
    private final Map<String, VcfGenotype> genotypes;

    /** The count for Number=A attributes for this VCF record. */
    private final int a;

    /** The count for Number=R attributes for this VCF record. */
    private final int r;


    /**
     * Create a new VCF record.
     *
     * @param lineNumber line number
     * @param chrom chromosome, must not be null
     * @param pos position
     * @param id array of ids
     * @param ref reference allele, must not be null
     * @param alt array of alternate alleles, must not be null
     * @param qual QUAL score
     * @param filter array of filters
     * @param info INFO key-value(s) pairs, must not be null
     * @param format array of format keys
     * @param genotypes genotypes keyed by sample id, must not be null
     */
    private VcfRecord(final long lineNumber,
                      final String chrom,
                      final long pos,
                      final String[] id,
                      final String ref,
                      final String[] alt,
                      final Double qual,
                      final String[] filter,
                      final ListMultimap<String, String> info,
                      final String[] format,
                      final Map<String, VcfGenotype> genotypes) {

        checkNotNull(chrom, "chrom must not be null");
        checkNotNull(ref, "ref must not be null");
        checkNotNull(alt, "alt must not be null");
        checkNotNull(info, "info must not be null");
        checkNotNull(genotypes, "genotypes must not be null");

        // verify genotypes have correct ref and alt
        for (VcfGenotype genotype : genotypes.values()) {
            checkArgument(ref.equals(genotype.getRef()),
                "ref " + ref + " and genotype ref " + genotype.getRef() + " must be equal");
            checkArgument(Arrays.equals(alt, genotype.getAlt()),
                "alt " + Arrays.toString(alt) + " and genotype alt " + Arrays.toString(genotype.getAlt()) + " must be equal");
        }

        this.lineNumber = lineNumber;
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.info = info;
        this.format = format;
        this.genotypes = genotypes;

        this.a = this.alt.length;
        this.r = this.a + 1;
    }


    /**
     * Return the line number for this VCF record.
     *
     * @return the line number for this VCF record
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * Return the chromosome for this VCF record.
     *
     * @return the chromosome for this VCF record
     */
    public String getChrom() {
        return chrom;
    }

    /**
     * Return the position for this VCF record.
     *
     * @return the position for this VCF record
     */
    public long getPos() {
        return pos;
    }

    /**
     * Return the array of ids for this VCF record.
     *
     * @return the array of ids for this VCF record
     */
    public String[] getId() {
        return id;
    }

    /**
     * Return the reference allele for this VCF record.
     *
     * @return the reference allele for this VCF record
     */
    public String getRef() {
        return ref;
    }

    /**
     * Return the alternate alleles for this VCF record.
     *
     * @return the alternate alleles for this VCF record
     */
    public String[] getAlt() {
        return alt;
    }

    /**
     * Return the QUAL score for this VCF record.
     *
     * @return the QUAL score for this VCF record
     */
    public Double getQual() {
        return qual;
    }

    /**
     * Return the filter for this VCF record.
     *
     * @return the filter for this VCF record
     */
    public String[] getFilter() {
        return filter;
    }

    /**
     * Return the INFO key-value(s) pairs for this VCF record.
     *
     * @return the INFO key-value(s) pairs for this VCF record
     */
    public ListMultimap<String, String> getInfo() {
        return info;
    }


    // INFO key-value(s) pairs for VCF INFO reserved keys

    /**
     * Return the count for Number=A attributes for this VCF record.
     *
     * @return the count for Number=A attributes for this VCF record
     */
    public int a() {
        return a;
    }

    /**
     * Return the count for Number=R attributes for this VCF record.
     *
     * @return the count for Number=R attributes for this VCF record
     */
    public int r() {
        return r;
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>AA</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>AA</code>
     */
    public boolean containsAa() {
        return containsInfoKey("AA");
    }

    /**
     * Return the Number=1 Type=String value for the VCF INFO reserved key <code>AA</code>
     * as a string.
     *
     * @return the Number=1 Type=String value for the VCF INFO reserved key <code>AA</code>
     *    as a string
     */
    public String getAa() {
        return getInfoString("AA");
    }

    /**
     * Return an optional Number=1 Type=String value for the VCF INFO reserved key <code>AA</code>
     * as a string.
     *
     * @return an optional Number=1 Type=String value for the VCF INFO reserved key <code>AA</code>
     *    as a string
     */
    public Optional<String> getAaOpt() {
        return getInfoStringOpt("AA");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>AC</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>AC</code>
     */
    public boolean containsAc() {
        return containsInfoKey("AC");
    }

    /**
     * Return the Number=A Type=Integer value for the VCF INFO reserved key <code>AC</code>
     * as an immutable list of integers.
     *
     * @return the Number=A Type=Integer value for the VCF INFO reserved key <code>AC</code>
     *    as an immutable list of integers
     */
    public List<Integer> getAc() {
        return getInfoIntegers("AC", a());
    }

    /**
     * Return an optional Number=A Type=Integer value for the VCF INFO reserved key <code>AC</code>
     * as an immutable list of integers.
     *
     * @return an optional Number=A Type=Integer value for the VCF INFO reserved key <code>AC</code>
     *    as an immutable list of integers
     */
    public Optional<List<Integer>> getAcOpt() {
        return getInfoIntegersOpt("AC", a());
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>AD</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>AD</code>
     */
    public boolean containsAd() {
        return containsInfoKey("AD");
    }

    /**
     * Return the Number=R Type=Integer value for the VCF INFO reserved key <code>AD</code>
     * as an immutable list of integers.
     *
     * @return the Number=R Type=Integer value for the VCF INFO reserved key <code>AD</code>
     *    as an immutable list of integers
     */
    public List<Integer> getAd() {
        return getInfoIntegers("AD", r());
    }

    /**
     * Return an optional Number=R Type=Integer value for the VCF INFO reserved key <code>AD</code>
     * as an immutable list of integers.
     *
     * @return an optional Number=R Type=Integer value for the VCF INFO reserved key <code>AD</code>
     *    as an immutable list of integers
     */
    public Optional<List<Integer>> getAdOpt() {
        return getInfoIntegersOpt("AD", r());
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>ADF</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>ADF</code>
     */
    public boolean containsAdf() {
        return containsInfoKey("ADF");
    }

    /**
     * Return the Number=R Type=Integer value for the VCF INFO reserved key <code>ADF</code>
     * as an immutable list of integers.
     *
     * @return the Number=R Type=Integer value for the VCF INFO reserved key <code>ADF</code>
     *    as an immutable list of integers
     */
    public List<Integer> getAdf() {
        return getInfoIntegers("ADF", r());
    }

    /**
     * Return an optional Number=R Type=Integer value for the VCF INFO reserved key <code>ADF</code>
     * as an immutable list of integers.
     *
     * @return an optional Number=R Type=Integer value for the VCF INFO reserved key <code>ADF</code>
     *    as an immutable list of integers
     */
    public Optional<List<Integer>> getAdfOpt() {
        return getInfoIntegersOpt("ADF", r());
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>ADR</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>ADR</code>
     */
    public boolean containsAdr() {
        return containsInfoKey("ADR");
    }

    /**
     * Return the Number=R Type=Integer value for the VCF INFO reserved key <code>ADR</code>
     * as an immutable list of integers.
     *
     * @return the Number=R Type=Integer value for the VCF INFO reserved key <code>ADR</code>
     *    as an immutable list of integers
     */
    public List<Integer> getAdr() {
        return getInfoIntegers("ADR", r());
    }

    /**
     * Return an optional Number=R Type=Integer value for the VCF INFO reserved key <code>ADR</code>
     * as an immutable list of integers.
     *
     * @return an optional Number=R Type=Integer value for the VCF INFO reserved key <code>ADR</code>
     *    as an immutable list of integers
     */
    public Optional<List<Integer>> getAdrOpt() {
        return getInfoIntegersOpt("ADR", r());
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>AF</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>AF</code>
     */
    public boolean containsAf() {
        return containsInfoKey("AF");
    }

    /**
     * Return the Number=A Type=Float value for the VCF INFO reserved key <code>AF</code>
     * as an immutable list of floats.
     *
     * @return the Number=A Type=Float value for the VCF INFO reserved key <code>AF</code>
     *    as an immutable list of floats
     */
    public List<Float> getAf() {
        return getInfoFloats("AF", a());
    }

    /**
     * Return an optional Number=A Type=Float value for the VCF INFO reserved key <code>AF</code>
     * as an immutable list of floats.
     *
     * @return an optional Number=A Type=Float value for the VCF INFO reserved key <code>AF</code>
     *    as an immutable list of floats
     */
    public Optional<List<Float>> getAfOpt() {
        return getInfoFloatsOpt("AF", a());
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>AN</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>AN</code>
     */
    public boolean containsAn() {
        return containsInfoKey("AN");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF INFO reserved key <code>AN</code>
     * as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF INFO reserved key <code>AN</code>
     *    as an integer
     */
    public int getAn() {
        return getInfoInteger("AN");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>AN</code>
     * as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>AN</code>
     *    as an integer
     */
    public Optional<Integer> getAnOpt() {
        return getInfoIntegerOpt("AN");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>BQ</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>BQ</code>
     */
    public boolean containsBq() {
        return containsInfoKey("BQ");
    }

    /**
     * Return the Number=1 Type=Float value for the VCF INFO reserved key <code>BQ</code>
     * as a float.
     *
     * @return the Number=1 Type=Float value for the VCF INFO reserved key <code>BQ</code>
     *    as a float
     */
    public float getBq() {
        return getInfoFloat("BQ");
    }

    /**
     * Return an optional Number=1 Type=Float value for the VCF INFO reserved key <code>BQ</code>
     * as a float.
     *
     * @return an optional Number=1 Type=Float value for the VCF INFO reserved key <code>BQ</code>
     *    as a float
     */
    public Optional<Float> getBqOpt() {
        return getInfoFloatOpt("BQ");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>CIGAR</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>CIGAR</code>
     */
    public boolean containsCigar() {
        return containsInfoKey("CIGAR");
    }

    /**
     * Return the Number=A Type=String value for the VCF INFO reserved key <code>CIGAR</code>
     * as an immutable list of floats.
     *
     * @return the Number=A Type=String value for the VCF INFO reserved key <code>CIGAR</code>
     *    as an immutable list of strings
     */
    public List<String> getCigar() {
        return getInfoStrings("CIGAR", a());
    }

    /**
     * Return an optional Number=A Type=String value for the VCF INFO reserved key <code>CIGAR</code>
     * as an immutable list of strings.
     *
     * @return an optional Number=A Type=String value for the VCF INFO reserved key <code>CIGAR</code>
     *    as an immutable list of strings
     */
    public Optional<List<String>> getCigarOpt() {
        return getInfoStringsOpt("CIGAR", a());
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>DB</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>DB</code>
     */
    public boolean containsDb() {
        return containsInfoKey("DB");
    }

    /**
     * Return the Number=0 Type=Flag value for the VCF INFO reserved key <code>DB</code>
     * as a boolean.
     *
     * @return the Number=0 Type=Flag value for the VCF INFO reserved key <code>DB</code>
     *    as a boolean
     */
    public boolean getDb() {
        return getInfoFlag("DB");
    }

    /**
     * Return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>DB</code>
     * as a boolean.
     *
     * @return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>DB</code>
     *    as a boolean
     */
    public Optional<Boolean> getDbOpt() {
        return getInfoFlagOpt("DB");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>DP</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>DP</code>
     */
    public boolean containsDp() {
        return containsInfoKey("DP");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF INFO reserved key <code>DP</code>
     * as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF INFO reserved key <code>DP</code>
     *    as an integer
     */
    public int getDp() {
        return getInfoInteger("DP");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>DP</code>
     * as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>DP</code>
     *    as an integer
     */
    public Optional<Integer> getDpOpt() {
        return getInfoIntegerOpt("DP");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>END</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>END</code>
     */
    public boolean containsEnd() {
        return containsInfoKey("END");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF INFO reserved key <code>END</code>
     * as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF INFO reserved key <code>END</code>
     *    as an integer
     */
    public int getEnd() {
        return getInfoInteger("END");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>END</code>
     * as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>END</code>
     *    as an integer
     */
    public Optional<Integer> getEndOpt() {
        return getInfoIntegerOpt("END");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>H2</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>H2</code>
     */
    public boolean containsH2() {
        return containsInfoKey("H2");
    }

    /**
     * Return the Number=0 Type=Flag value for the VCF INFO reserved key <code>H2</code>
     * as a boolean.
     *
     * @return the Number=0 Type=Flag value for the VCF INFO reserved key <code>H2</code>
     *    as a boolean
     */
    public boolean getH2() {
        return getInfoFlag("H2");
    }

    /**
     * Return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>H2</code>
     * as a boolean.
     *
     * @return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>H2</code>
     *    as a boolean
     */
    public Optional<Boolean> getH2Opt() {
        return getInfoFlagOpt("H2");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>H3</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>H3</code>
     */
    public boolean containsH3() {
        return containsInfoKey("H3");
    }

    /**
     * Return the Number=0 Type=Flag value for the VCF INFO reserved key <code>H3</code>
     * as a boolean.
     *
     * @return the Number=0 Type=Flag value for the VCF INFO reserved key <code>H3</code>
     *    as a boolean
     */
    public boolean getH3() {
        return getInfoFlag("H3");
    }

    /**
     * Return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>H3</code>
     * as a boolean.
     *
     * @return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>H3</code>
     *    as a boolean
     */
    public Optional<Boolean> getH3Opt() {
        return getInfoFlagOpt("H3");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>MQ</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>MQ</code>
     */
    public boolean containsMq() {
        return containsInfoKey("MQ");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ</code>
     * as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ</code>
     *    as an integer
     */
    public int getMq() {
        return getInfoInteger("MQ");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ</code>
     * as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ</code>
     *    as an integer
     */
    public Optional<Integer> getMqOpt() {
        return getInfoIntegerOpt("MQ");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>MQ0</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>MQ0</code>
     */
    public boolean containsMq0() {
        return containsInfoKey("MQ0");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ0</code>
     * as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ0</code>
     *    as an integer
     */
    public int getMq0() {
        return getInfoInteger("MQ0");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ0</code>
     * as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>MQ0</code>
     *    as an integer
     */
    public Optional<Integer> getMq0Opt() {
        return getInfoIntegerOpt("MQ0");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>NS</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>NS</code>
     */
    public boolean containsNs() {
        return containsInfoKey("NS");
    }

    /**
     * Return the Number=1 Type=Integer value for the VCF INFO reserved key <code>NS</code>
     * as an integer.
     *
     * @return the Number=1 Type=Integer value for the VCF INFO reserved key <code>NS</code>
     *    as an integer
     */
    public int getNs() {
        return getInfoInteger("NS");
    }

    /**
     * Return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>NS</code>
     * as an integer.
     *
     * @return an optional Number=1 Type=Integer value for the VCF INFO reserved key <code>NS</code>
     *    as an integer
     */
    public Optional<Integer> getNsOpt() {
        return getInfoIntegerOpt("NS");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>SB</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>SB</code>
     */
    public boolean containsSb() {
        return containsInfoKey("SB");
    }

    /**
     * Return the Number=1 Type=Float value for the VCF INFO reserved key <code>SB</code>
     * as a float.
     *
     * @return the Number=1 Type=Float value for the VCF INFO reserved key <code>SB</code>
     *    as a float
     */
    public float getSb() {
        return getInfoFloat("SB");
    }

    /**
     * Return an optional Number=1 Type=Float value for the VCF INFO reserved key <code>SB</code>
     * as a float.
     *
     * @return an optional Number=1 Type=Float value for the VCF INFO reserved key <code>SB</code>
     *    as a float
     */
    public Optional<Float> getSbOpt() {
        return getInfoFloatOpt("SB");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>SOMATIC</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>SOMATIC</code>
     */
    public boolean containsSomatic() {
        return containsInfoKey("SOMATIC");
    }

    /**
     * Return the Number=0 Type=Flag value for the VCF INFO reserved key <code>SOMATIC</code>
     * as a boolean.
     *
     * @return the Number=0 Type=Flag value for the VCF INFO reserved key <code>SOMATIC</code>
     *    as a boolean
     */
    public boolean getSomatic() {
        return getInfoFlag("SOMATIC");
    }

    /**
     * Return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>SOMATIC</code>
     * as a boolean.
     *
     * @return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>SOMATIC</code>
     *    as a boolean
     */
    public Optional<Boolean> getSomaticOpt() {
        return getInfoFlagOpt("SOMATIC");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>VALIDATED</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>VALIDATED</code>
     */
    public boolean containsValidated() {
        return containsInfoKey("VALIDATED");
    }

    /**
     * Return the Number=0 Type=Flag value for the VCF INFO reserved key <code>VALIDATED</code>
     * as a boolean.
     *
     * @return the Number=0 Type=Flag value for the VCF INFO reserved key <code>VALIDATED</code>
     *    as a boolean
     */
    public boolean getValidated() {
        return getInfoFlag("VALIDATED");
    }

    /**
     * Return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>VALIDATED</code>
     * as a boolean.
     *
     * @return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>VALIDATED</code>
     *    as a boolean
     */
    public Optional<Boolean> getValidatedOpt() {
        return getInfoFlagOpt("VALIDATED");
    }

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the VCF INFO reserved key <code>1000G</code>.
     *
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the VCF INFO reserved key <code>1000G</code>
     */
    public boolean contains1000g() {
        return containsInfoKey("1000G");
    }

    /**
     * Return the Number=0 Type=Flag value for the VCF INFO reserved key <code>1000G</code>
     * as a boolean.
     *
     * @return the Number=0 Type=Flag value for the VCF INFO reserved key <code>1000G</code>
     *    as a boolean
     */
    public boolean get1000g() {
        return getInfoFlag("1000G");
    }

    /**
     * Return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>1000G</code>
     * as a boolean.
     *
     * @return an optional Number=0 Type=Flag value for the VCF INFO reserved key <code>1000G</code>
     *    as a boolean
     */
    public Optional<Boolean> get1000gOpt() {
        return getInfoFlagOpt("1000G");
    }


    // INFO key-value(s) pairs for VCF INFO non-reserved keys

    /**
     * Return true if the INFO key-value(s) pairs for this VCF record contains
     * the specified key.
     *
     * @param key key, must not be null
     * @return true if the INFO key-value(s) pairs for this VCF record contains
     *    the specified key
     */
    public boolean containsInfoKey(final String key) {
        return info.containsKey(key);
    }


    /**
     * Return the Number=1 Type=Character value for the specified key
     * as a character.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Character value for the specified key
     *    as a character
     */
    public char getInfoCharacter(final String key) {
        return parseCharacter(key, info);
    }

    /**
     * Return the Number=0 Type=Flag value for the specified key
     * as a boolean.
     *
     * @param key key, must not be null
     * @return the Number=0 Type=Flag value for the specified key
     *    as a boolean
     */
    public boolean getInfoFlag(final String key) {
        return parseFlag(key, info);
    }

    /**
     * Return the Number=1 Type=Float value for the specified key
     * as a float.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Float value for the specified key
     *    as a float
     */
    public float getInfoFloat(final String key) {
        return parseFloat(key, info);
    }

    /**
     * Return the Number=1 Type=Integer value for the specified key
     * as an integer.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Integer value for the specified key
     *    as an integer
     */
    public int getInfoInteger(final String key) {
        return parseInteger(key, info);
    }

    /**
     * Return the Number=1 Type=String value for the specified key
     * as a string.
     *
     * @param key key, must not be null
     * @return the Number=1 Type=Float value for the specified key
     *    as a string
     */
    public String getInfoString(final String key) {
        return parseString(key, info);
    }


    /**
     * Return the Number=&#46; Type=Character value for the specified key
     * as an immutable list of characters.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=Character value for the specified key
     *    as an immutable list of characters
     */
    public List<Character> getInfoCharacters(final String key) {
        return parseCharacters(key, info);
    }

    /**
     * Return the Number=&#46; Type=Float value for the specified key
     * as an immutable list of floats.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=Float value for the specified key
     *    as an immutable list of floats
     */
    public List<Float> getInfoFloats(final String key) {
        return parseFloats(key, info);
    }

    /**
     * Return the Number=&#46; Type=Integer value for the specified key
     * as an immutable list of integers.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=Integer value for the specified key
     *    as an immutable list of integers
     */
    public List<Integer> getInfoIntegers(final String key) {
        return parseIntegers(key, info);
    }

    /**
     * Return the Number=&#46; Type=String value for the specified key
     * as an immutable list of strings.
     *
     * @param key key, must not be null
     * @return the Number=&#46; Type=String value for the specified key
     *    as an immutable list of strings
     */
    public List<String> getInfoStrings(final String key) {
        return parseStrings(key, info);
    }


    /**
     * Return the Number=[n, A, R] Type=Character value for the specified key
     * as an immutable list of characters of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R] Type=Character value for the specified key
     *    as an immutable list of characters of size equal to the specified number
     */
    public List<Character> getInfoCharacters(final String key, final int number) {
        return parseCharacters(key, number, info);
    }

    /**
     * Return the Number=[n, A, R] Type=Float value for the specified key
     * as an immutable list of floats of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R] Type=Float value for the specified key
     *    as an immutable list of floats of size equal to the specified number
     */
    public List<Float> getInfoFloats(final String key, final int number) {
        return parseFloats(key, number, info);
    }

    /**
     * Return the Number=[n, A, R] Type=Integer value for the specified key
     * as an immutable list of integers of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R] Type=Integer value for the specified key
     *    as an immutable list of integers of size equal to the specified number
     */
    public List<Integer> getInfoIntegers(final String key, final int number) {
        return parseIntegers(key, number, info);
    }

    /**
     * Return the Number=[n, A, R] Type=String value for the specified key
     * as an immutable list of strings of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return the Number=[n, A, R] Type=String value for the specified key
     *    as an immutable list of strings of size equal to the specified number
     */
    public List<String> getInfoStrings(final String key, final int number) {
        return parseStrings(key, number, info);
    }


    /**
     * Return an optional Number=1 Type=Character value for the specified key
     * as a character.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=Character value for the specified key
     *    as a character
     */
    public Optional<Character> getInfoCharacterOpt(final String key) {
        return Optional.ofNullable(containsInfoKey(key) ? getInfoCharacter(key) : null);
    }

    /**
     * Return an optional Number=0 Type=Flag value for the specified key
     * as a boolean.
     *
     * @param key key, must not be null
     * @return an optional Number=0 Type=Flag value for the specified key
     *    as a boolean
     */
    public Optional<Boolean> getInfoFlagOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoFlag(key) : null);
    }

    /**
     * Return an optional Number=1 Type=Float value for the specified key
     * as a float.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=Float value for the specified key
     *    as a float
     */
    public Optional<Float> getInfoFloatOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoFloat(key) : null);
    }

    /**
     * Return an optional Number=1 Type=Integer value for the specified key
     * as an integer.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=Integer value for the specified key
     *    as an integer
     */
    public Optional<Integer> getInfoIntegerOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoInteger(key) : null);
    }

    /**
     * Return an optional Number=1 Type=String value for the specified key
     * as a string.
     *
     * @param key key, must not be null
     * @return an optional Number=1 Type=String value for the specified key
     *    as a string
     */
    public Optional<String> getInfoStringOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoString(key) : null);
    }


    /**
     * Return an optional Number=&#46; Type=Character value for the specified key
     * as an immutable list of characters.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=Character value for the specified key
     *    as an immutable list of characters
     */
    public Optional<List<Character>> getInfoCharactersOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoCharacters(key) : null);
    }

    /**
     * Return an optional Number=&#46; Type=Float value for the specified key
     * as an immutable list of floats.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=Float value for the specified key
     *    as an immutable list of floats
     */
    public Optional<List<Float>> getInfoFloatsOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoFloats(key) : null);
    }

    /**
     * Return an optional Number=&#46; Type=Integer value for the specified key
     * as an immutable list of integers.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=Integer value for the specified key
     *    as an immutable list of integers
     */
    public Optional<List<Integer>> getInfoIntegersOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoIntegers(key) : null);
    }

    /**
     * Return an optional Number=&#46; Type=String value for the specified key
     * as an immutable list of strings.
     *
     * @param key key, must not be null
     * @return an optional Number=&#46; Type=String value for the specified key
     *    as an immutable list of strings
     */
    public Optional<List<String>> getInfoStringsOpt(final String key) {
       return Optional.ofNullable(containsInfoKey(key) ? getInfoStrings(key) : null);
    }


    /**
     * Return an optional Number=[n, A, R] Type=Character value for the specified key
     * as an immutable list of characters of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R] Type=Character value for the specified key
     *    as an immutable list of characters of size equal to the specified number
     */
    public Optional<List<Character>> getInfoCharactersOpt(final String key, final int number) {
        return Optional.ofNullable(containsInfoKey(key) ? getInfoCharacters(key, number) : null);
    }

    /**
     * Return an optional Number=[n, A, R] Type=Float value for the specified key
     * as an immutable list of floats of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R] Type=Float value for the specified key
     *    as an immutable list of floats of size equal to the specified number
     */
    public Optional<List<Float>> getInfoFloatsOpt(final String key, final int number) {
        return Optional.ofNullable(containsInfoKey(key) ? getInfoFloats(key, number) : null);
    }

    /**
     * Return an optional Number=[n, A, R] Type=Integer value for the specified key
     * as an immutable list of integers of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R] Type=Integer value for the specified key
     *    as an immutable list of integers of size equal to the specified number
     */
    public Optional<List<Integer>> getInfoIntegersOpt(final String key, final int number) {
        return Optional.ofNullable(containsInfoKey(key) ? getInfoIntegers(key, number) : null);
    }

    /**
     * Return an optional Number=[n, A, R] Type=String value for the specified key
     * as an immutable list of strings of size equal to the specified number.  For the
     * count for Number=A and Number=R attributes for this VCF record, use the methods
     * <code>a()</code> and <code>r()</code>, respectively.
     *
     * @see #a()
     * @see #r()
     * @param key key, must not be null
     * @param number number, must be greater than zero
     * @return an optional Number=[n, A, R] Type=String value for the specified key
     *    as an immutable list of strings of size equal to the specified number
     */
    public Optional<List<String>> getInfoStringsOpt(final String key, final int number) {
        return Optional.ofNullable(containsInfoKey(key) ? getInfoStrings(key, number) : null);
    }


    /**
     * Return the format for this VCF record.
     *
     * @return the format for this VCF record
     */
    public String[] getFormat() {
        return format;
    }

    /**
     * Return the genotypes keyed by sample id for this VCF record.
     *
     * @return the genotypes keyed by sample id for this VCF record
     */
    public Map<String, VcfGenotype> getGenotypes() {
        return genotypes;
    }

    /**
     * Create and return a new VCF record builder.
     *
     * @return a new VCF record builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create and return a new VCF record builder populated from the fields in the specified VCF record.
     *
     * @param record VCF record, must not be null
     * @return a new VCF record builder populated from the fields in the specified VCF record
     */
    public static Builder builder(final VcfRecord record) {
        checkNotNull(record, "record must not be null");
        return new Builder()
            .withLineNumber(record.getLineNumber())
            .withChrom(record.getChrom())
            .withPos(record.getPos())
            .withId(record.getId())
            .withRef(record.getRef())
            .withAlt(record.getAlt())
            .withQual(record.getQual())
            .withFilter(record.getFilter())
            .withInfo(record.getInfo())
            .withFormat(record.getFormat())
            .withGenotypes(record.getGenotypes());
    }

    /**
     * VCF record builder.
     */
    public static final class Builder {
        /** Line number. */
        private long lineNumber;

        /** Chromosome. */
        private String chrom;

        /** Position. */
        private long pos;

        /** Array of ids. */
        private String[] id;

        /** Reference allele. */
        private String ref;

        /** Array of alternate alleles. */
        private String[] alt;

        /** QUAL score. */
        private Double qual;

        /** Filter. */
        private String[] filter;

        /** Map of INFO key-value(s) pairs. */
        private ImmutableListMultimap.Builder<String, String> info = ImmutableListMultimap.builder();

        /** Format. */
        private String[] format;

        /** Map builder for genotypes keyed by sample id. */
        private ImmutableMap.Builder<String, VcfGenotype> genotypes = ImmutableMap.builder();

        /** Map of genotype fields keyed by sample id. */
        private ConcurrentMap<String, ListMultimap<String, String>> genotypeFields = new ConcurrentHashMap<String, ListMultimap<String, String>>();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF record builder configured with the specified line number.
         *
         * @param lineNumber line number
         * @return this VCF record builder configured with the specified line number
         */
        public Builder withLineNumber(final long lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified chromosome.
         *
         * @param chrom chromosome
         * @return this VCF record builder configured with the specified chromosome
         */
        public Builder withChrom(final String chrom) {
            this.chrom = chrom;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified position.
         *
         * @param pos position
         * @return this VCF record builder configured with the specified position
         */
        public Builder withPos(final long pos) {
            this.pos = pos;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified ids.
         *
         * @param id ids
         * @return this VCF record builder configured with the specified ids
         */
        public Builder withId(final String... id) {
            this.id = id;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified reference allele.
         *
         * @param ref reference allele
         * @return this VCF record builder configured with the specified reference allele
         */
        public Builder withRef(final String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified alternate alleles.
         *
         * @param alt alternate alleles
         * @return this VCF record builder configured with the specified alternate alleles
         */
        public Builder withAlt(final String... alt) {
            this.alt = alt;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified QUAL score.
         *
         * @param qual QUAL score
         * @return this VCF record builder configured with the specified QUAL score
         */
        public Builder withQual(final Double qual) {
            this.qual = qual;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified filter.
         *
         * @param filter filter
         * @return this VCF record builder configured with the specified filter
         */
        public Builder withFilter(final String... filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified INFO key-value(s) pair.
         *
         * @param id INFO ID key, must not be null
         * @param values INFO values, must not be null
         * @return this VCF record builder configured with the specified INFO key-value(s) pair
         */
        public Builder withInfo(final String id, final String... values) {
            checkNotNull(values);
            for (String value : values) {
                info.put(id, value);
            }
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified INFO key-value pairs.
         *
         * @param info INFO key-value pairs, must not be null
         * @return this VCF record builder configured with the specified INFO key-value pairs
         */
        public Builder withInfo(final ListMultimap<String, String> info) {
            this.info.putAll(info);
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified INFO key-value(s) pair
         * replacing the previously configured value(s).  Use sparingly, more expensive than
         * <code>withInfo</code>.
         *
         * @param id INFO ID key, must not be null
         * @param values INFO values, must not be null
         * @return this VCF record builder configured with the specified INFO key-value(s) pair
         *    replacing the previously configured value(s)
         */
        public Builder replaceInfo(final String id, final String... values) {
            checkNotNull(values);

            // copy old info values except id
            ListMultimap<String, String> oldInfo = this.info.build();
            this.info = ImmutableListMultimap.builder();
            for (String key : oldInfo.keys()) {
                if (!key.equals(id)) {
                    this.info.putAll(key, oldInfo.get(key));
                }
            }
            // add new value(s)
            for (String value : values) {
                info.put(id, value);
            }
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified INFO key-value pairs.
         * Use sparingly, more expensive than <code>withInfo</code>.
         *
         * @param info INFO key-value pairs, must not be null
         * @return this VCF record builder configured with the specified INFO key-value pairs
         */
        public Builder replaceInfo(final ListMultimap<String, String> info) {
            this.info = ImmutableListMultimap.builder();
            this.info.putAll(info);
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified format.
         *
         * @param format format
         * @return this VCF record builder configured with the specified format
         */
        public Builder withFormat(final String... format) {
            this.format = format;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotype field keyed by sample id.
         *
         * @param sampleId sample id, must not be null
         * @param formatId genotype field format id, must not be null
         * @param values values, must not be null
         * @return this VCF record builder configured with the specified genotype field keyed by sample id
         */
        public Builder withGenotype(final String sampleId, final String formatId, final String... values) {
            checkNotNull(values);
            genotypeFields.putIfAbsent(sampleId, ArrayListMultimap.<String, String>create());
            for (String value : values) {
                genotypeFields.get(sampleId).put(formatId, value);
            }
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotype keyed by sample id.
         *
         * @param sampleId sample id, must not be null
         * @param genotype genotype, must not be null
         * @return this VCF record builder configured with the specified genotype keyed by sample id
         */
        public Builder withGenotype(final String sampleId, final VcfGenotype genotype) {
            genotypes.put(sampleId, genotype);
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotypes keyed by sample id.
         *
         * @param genotypes genotypes keyed by sample id, must not be null
         * @return this VCF record builder configured with the specified genotypes keyed by sample id
         */
        public Builder withGenotypes(final Map<String, VcfGenotype> genotypes) {
            this.genotypes.putAll(genotypes);
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotypes keyed by sample id.
         * Use sparingly, more expensive than <code>withGenotypes</code>.
         *
         * @param genotypes genotypes keyed by sample id, must not be null
         * @return this VCF record builder configured with the specified genotypes keyed by sample id
         */
        public Builder replaceGenotypes(final Map<String, VcfGenotype> genotypes) {
            this.genotypes = ImmutableMap.builder();
            this.genotypes.putAll(genotypes);
            return this;
        }

        /**
         * Reset this VCF record builder.
         *
         * @return this VCF record builder
         */
        public Builder reset() {
            lineNumber = -1L;
            chrom = null;
            pos = -1L;
            id = null;
            ref = null;
            alt = null;
            qual = null;
            filter = null;
            info = ImmutableListMultimap.builder();
            format = null;
            genotypes = ImmutableMap.builder();
            genotypeFields.clear();
            return this;
        }

        /**
         * Create and return a new VCF record populated from the configuration of this VCF record builder.
         *
         * @return a new VCF record populated from the configuration of this VCF record builder
         */
        public VcfRecord build() {
            // build genotypes from genotype fields if necessary
            for (Map.Entry<String, ListMultimap<String, String>> entry : genotypeFields.entrySet()) {
                String sampleId = entry.getKey();
                genotypes.put(sampleId, VcfGenotype.builder().withRef(ref).withAlt(alt).withFields(entry.getValue()).build());
            }
            return new VcfRecord(lineNumber, chrom, pos, id, ref, alt, qual, filter, info.build(), format, genotypes.build());
        }
    }
}
