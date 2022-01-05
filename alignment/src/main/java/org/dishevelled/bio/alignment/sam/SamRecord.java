/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.sam;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;
import org.dishevelled.bio.annotation.AnnotatedRecord;

/**
 * SAM record.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamRecord extends AnnotatedRecord {

    /** QNAME mandatory field. */
    private final String qname;

    /** FLAG mandatory field. */
    private final int flag;

    /** RNAME mandatory field. */
    private final String rname;

    /** POS mandatory field. */
    private final int pos;

    /** MAPQ mandatory field. */
    private final int mapq;

    /** CIGAR mandatory field. */
    private final String cigar;

    /** RNEXT mandatory field. */
    private final String rnext;

    /** PNEXT mandatory field. */
    private final int pnext;

    /** TLEN mandatory field. */
    private final int tlen;

    /** SEQ mandatory field. */
    private final String seq;

    /** QUAL mandatory field. */
    private final String qual;

    /** Cached hash code. */
    private final int hashCode;


    /**
     * Create a new SAM record.
     *
     * @param qname QNAME mandatory field
     * @param flag FLAG mandatory field
     * @param rname RNAME mandatory field
     * @param pos POS mandatory field
     * @param mapq MAPQ mandatory field
     * @param cigar CIGAR mandatory field
     * @param rnext RNEXT mandatory field
     * @param pnext PNEXT mandatory field
     * @param tlen TLEN mandatory field
     * @param seq SEQ mandatory field
     * @param qual QUAL mandatory field
     * @param annotations annotations, must not be null
     */
    public SamRecord(@Nullable final String qname,
                     final int flag,
                     @Nullable final String rname,
                     final int pos,
                     final int mapq,
                     @Nullable final String cigar,
                     @Nullable final String rnext,
                     final int pnext,
                     final int tlen,
                     @Nullable final String seq,
                     @Nullable final String qual,
                     final Map<String, Annotation> annotations) {

        super(annotations);
        this.qname = qname;
        this.flag = flag;
        this.rname = rname;
        this.pos = pos;
        this.mapq = mapq;
        this.cigar = cigar;
        this.rnext = rnext;
        this.pnext = pnext;
        this.tlen = tlen;
        this.seq = seq;
        this.qual = qual;

        hashCode = Objects.hash(this.qname, this.flag, this.rname, this.pos, this.mapq, this.cigar,
                                this.rnext, this.pnext, this.tlen, this.seq, this.qual, getAnnotations());
    }


    /**
     * Return the QNAME mandatory field for this SAM record. May be null.
     *
     * @return the QNAME mandatory field for this SAM record
     */
    public String getQname() {
        return qname;
    }

    /**
     * Return an optional wrapping the QNAME mandatory field for this SAM record.
     *
     * @return an optional wrapping the QNAME mandatory field for this SAM record
     */
    public Optional<String> getQnameOpt() {
        return Optional.ofNullable(qname);
    }

    /**
     * Return the FLAG mandatory field for this SAM record.
     *
     * @return the FLAG mandatory field for this SAM record
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Return the RNAME mandatory field for this SAM record. May be null.
     *
     * @return the RNAME mandatory field for this SAM record
     */
    public String getRname() {
        return rname;
    }

    /**
     * Return an optional wrapping the RNAME mandatory field for this SAM record.
     *
     * @return an optional wrapping the RNAME mandatory field for this SAM record
     */
    public Optional<String> getRnameOpt() {
        return Optional.ofNullable(rname);
    }

    /**
     * Return the POS mandatory field for this SAM record.
     *
     * @return the POS mandatory field for this SAM record
     */
    public int getPos() {
        return pos;
    }

    /**
     * Return the MAPQ mandatory field for this SAM record.
     *
     * @return the MAPQ mandatory field for this SAM record
     */
    public int getMapq() {
        return mapq;
    }

    /**
     * Return the CIGAR mandatory field for this SAM record. May be null.
     *
     * @return the CIGAR mandatory field for this SAM record
     */
    public String getCigar() {
        return cigar;
    }

    /**
     * Return an optional wrapping the CIGAR mandatory field for this SAM record.
     *
     * @return an optional wrapping the CIGAR mandatory field for this SAM record
     */
    public Optional<String> getCigarOpt() {
        return Optional.ofNullable(cigar);
    }

    /**
     * Return the RNEXT mandatory field for this SAM record. May be null.
     *
     * @return the RNEXT mandatory field for this SAM record
     */
    public String getRnext() {
        return rnext;
    }

    /**
     * Return an optional wrapping the RNEXT mandatory field for this SAM record.
     *
     * @return an optional wrapping the RNEXT mandatory field for this SAM record
     */
    public Optional<String> getRnextOpt() {
        return Optional.ofNullable(rnext);
    }

    /**
     * Return the PNEXT mandatory field for this SAM record.
     *
     * @return the PNEXT mandatory field for this SAM record
     */
    public int getPnext() {
        return pnext;
    }

    /**
     * Return the TLEN mandatory field for this SAM record.
     *
     * @return the TLEN mandatory field for this SAM record
     */
    public int getTlen() {
        return tlen;
    }

    /**
     * Return the SEQ mandatory field for this SAM record. May be null.
     *
     * @return the SEQ mandatory field for this SAM record
     */
    public String getSeq() {
        return seq;
    }

    /**
     * Return an optional wrapping the SEQ mandatory field for this SAM record.
     *
     * @return an optional wrapping the SEQ mandatory field for this SAM record
     */
    public Optional<String> getSeqOpt() {
        return Optional.ofNullable(seq);
    }

    /**
     * Return the QUAL mandatory field for this SAM record. May be null.
     *
     * @return the QUAL mandatory field for this SAM record
     */
    public String getQual() {
        return qual;
    }

    /**
     * Return an optional wrapping the QUAL mandatory field for this SAM record.
     *
     * @return an optional wrapping the QUAL mandatory field for this SAM record
     */
    public Optional<String> getQualOpt() {
        return Optional.ofNullable(qual);
    }


    /*

      Tag Type Description
      --------------------
      AM i The smallest template-independent mapping quality of segments in the rest
      AS i Alignment score generated by aligner
      BC Z Barcode sequence identifying the sample
      BQ Z Offset to base alignment quality (BAQ)
      BZ Z Phred quality of the unique molecular barcode bases in the OX tag
      CC Z Reference name of the next hit
      CG B,I BAM only: CIGAR in BAM's binary encoding if (and only if) it consists of > 65535 operators
      CM i Edit distance between the color sequence and the color reference (see also NM})
      CO Z Free-text comments
      CP i Leftmost coordinate of the next hit
      CQ Z Color read base qualities
      CS Z Color read sequence
      CT Z Complete read annotation tag, used for consensus annotation dummy features
      E2 Z The 2nd most likely base calls
      FI i The index of segment in the template
      FS Z Segment suffix
      FZ B,S Flow signal intensities
      GC ? Reserved for backwards compatibility reasons
      GQ ? Reserved for backwards compatibility reasons
      GS ? Reserved for backwards compatibility reasons
      H0 i Number of perfect hits
      H1 i Number of 1-difference hits (see also NM)
      H2 i Number of 2-difference hits
      HI i Query hit index
      IH i Number of stored alignments in SAM that contains the query in the current record
      LB Z Library
      MC Z CIGAR string for mate/next segment
      MD Z String for mismatching positions
      MF ? Reserved for backwards compatibility reasons
      MI Z Molecular identifier; a string that uniquely identifies the molecule from which the record was derived
      MQ i Mapping quality of the mate/next segment
      NH i Number of reported alignments that contains the query in the current record
      NM i Edit distance to the reference
      OC Z Original CIGAR
      OP i Original mapping position
      OQ Z Original base quality
      OX Z Original unique molecular barcode bases
      PG Z Program
      PQ i Phred likelihood of the template
      PT Z Read annotations for parts of the padded read sequence
      PU Z Platform unit
      Q2 Z Phred quality of the mate/next segment sequence in the R2 tag
      QT Z Phred quality of the sample-barcode sequence in the BC (or RT) tag
      QX Z Quality score of the unique molecular identifier in the RX tag
      R2 Z Sequence of the mate/next segment in the template
      RG Z Read group
      RT Z Barcode sequence (deprecated; use BC instead)
      RX Z Sequence bases of the (possibly corrected) unique molecular identifier
      SA Z Other canonical alignments in a chimeric alignment
      SM i Template-independent mapping quality
      SQ ? Reserved for backwards compatibility reasons
      S2 ? Reserved for backwards compatibility reasons
      TC i The number of segments in the template
      U2 Z Phred probability of the 2nd call being wrong conditional on the best being wrong
      UQ i Phred likelihood of the segment, conditional on the mapping being correct
      X? ? Reserved for end users
      Y? ? Reserved for end users
      Z? ? Reserved for end users

    */

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>AM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>AM</code>
     */
    public boolean containsAm() {
        return containsAnnotationKey("AM");
    }

    /**
     * Return the Type=i value for the reserved key <code>AM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>AM</code>
     *    as an integer
     */
    public int getAm() {
        return getAnnotationInteger("AM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>AM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>AM</code>
     *   as an integer
     */
    public Optional<Integer> getAmOpt() {
        return getAnnotationIntegerOpt("AM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>AS</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>AS</code>
     */
    public boolean containsAs() {
        return containsAnnotationKey("AS");
    }

    /**
     * Return the Type=i value for the reserved key <code>AS</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>AS</code>
     *    as an integer
     */
    public int getAs() {
        return getAnnotationInteger("AS");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>AS</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>AS</code>
     *   as an integer
     */
    public Optional<Integer> getAsOpt() {
        return getAnnotationIntegerOpt("AS");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>BC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>BC</code>
     */
    public boolean containsBc() {
        return containsAnnotationKey("BC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>BC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>BC</code>
     *    as a string
     */
    public String getBc() {
        return getAnnotationString("BC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>BC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>BC</code>
     *   as a string
     */
    public Optional<String> getBcOpt() {
        return getAnnotationStringOpt("BC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>BQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>BQ</code>
     */
    public boolean containsBq() {
        return containsAnnotationKey("BQ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>BQ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>BQ</code>
     *    as a string
     */
    public String getBq() {
        return getAnnotationString("BQ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>BQ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>BQ</code>
     *   as a string
     */
    public Optional<String> getBqOpt() {
        return getAnnotationStringOpt("BQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>BZ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>BZ</code>
     */
    public boolean containsBz() {
        return containsAnnotationKey("BZ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>BZ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>BZ</code>
     *    as a string
     */
    public String getBz() {
        return getAnnotationString("BZ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>BZ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>BZ</code>
     *   as a string
     */
    public Optional<String> getBzOpt() {
        return getAnnotationStringOpt("BZ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CC</code>
     */
    public boolean containsCc() {
        return containsAnnotationKey("CC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CC</code>
     *    as a string
     */
    public String getCc() {
        return getAnnotationString("CC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CC</code>
     *   as a string
     */
    public Optional<String> getCcOpt() {
        return getAnnotationStringOpt("CC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CG</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CG</code>
     */
    public boolean containsCg() {
        return containsAnnotationKey("CG");
    }

    /**
     * Return the Type=B first letter I value for the reserved key <code>CG</code>
     * as an immutable list of integers.
     *
     * @return the Type=B first letter I value for the reserved key <code>CG</code>
     *    as an immutable list of integers
     */
    public List<Integer> getCg() {
        return getAnnotationIntegers("CG");
    }

    /**
     * Return an optional Type=B first letter I value for the reserved key <code>CG</code>
     * as an immutable list of integers.
     *
     * @return an optional Type=B first letter I value for the reserved key <code>CG</code>
     *   as an immutable list of integers
     */
    public Optional<List<Integer>> getCgOpt() {
        return getAnnotationIntegersOpt("CG");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CM</code>
     */
    public boolean containsCm() {
        return containsAnnotationKey("CM");
    }

    /**
     * Return the Type=i value for the reserved key <code>CM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>CM</code>
     *    as an integer
     */
    public int getCm() {
        return getAnnotationInteger("CM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>CM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>CM</code>
     *   as an integer
     */
    public Optional<Integer> getCmOpt() {
        return getAnnotationIntegerOpt("CM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CO</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CO</code>
     */
    public boolean containsCo() {
        return containsAnnotationKey("CO");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CO</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CO</code>
     *    as a string
     */
    public String getCo() {
        return getAnnotationString("CO");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CO</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CO</code>
     *   as a string
     */
    public Optional<String> getCoOpt() {
        return getAnnotationStringOpt("CO");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CP</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CP</code>
     */
    public boolean containsCp() {
        return containsAnnotationKey("CP");
    }

    /**
     * Return the Type=i value for the reserved key <code>CP</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>CP</code>
     *    as an integer
     */
    public int getCp() {
        return getAnnotationInteger("CP");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>CP</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>CP</code>
     *   as an integer
     */
    public Optional<Integer> getCpOpt() {
        return getAnnotationIntegerOpt("CP");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CQ</code>
     */
    public boolean containsCq() {
        return containsAnnotationKey("CQ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CQ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CQ</code>
     *    as a string
     */
    public String getCq() {
        return getAnnotationString("CQ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CQ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CQ</code>
     *   as a string
     */
    public Optional<String> getCqOpt() {
        return getAnnotationStringOpt("CQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CS</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CS</code>
     */
    public boolean containsCs() {
        return containsAnnotationKey("CS");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CS</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CS</code>
     *    as a string
     */
    public String getCs() {
        return getAnnotationString("CS");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CS</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CS</code>
     *   as a string
     */
    public Optional<String> getCsOpt() {
        return getAnnotationStringOpt("CS");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CT</code>
     */
    public boolean containsCt() {
        return containsAnnotationKey("CT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CT</code>
     *    as a string
     */
    public String getCt() {
        return getAnnotationString("CT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CT</code>
     *   as a string
     */
    public Optional<String> getCtOpt() {
        return getAnnotationStringOpt("CT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>E2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>E2</code>
     */
    public boolean containsE2() {
        return containsAnnotationKey("E2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>E2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>E2</code>
     *    as a string
     */
    public String getE2() {
        return getAnnotationString("E2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>E2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>E2</code>
     *   as a string
     */
    public Optional<String> getE2Opt() {
        return getAnnotationStringOpt("E2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>FI</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>FI</code>
     */
    public boolean containsFi() {
        return containsAnnotationKey("FI");
    }

    /**
     * Return the Type=i value for the reserved key <code>FI</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>FI</code>
     *    as an integer
     */
    public int getFi() {
        return getAnnotationInteger("FI");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>FI</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>FI</code>
     *   as an integer
     */
    public Optional<Integer> getFiOpt() {
        return getAnnotationIntegerOpt("FI");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>FS</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>FS</code>
     */
    public boolean containsFs() {
        return containsAnnotationKey("FS");
    }

    /**
     * Return the Type=Z value for the reserved key <code>FS</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>FS</code>
     *    as a string
     */
    public String getFs() {
        return getAnnotationString("FS");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>FS</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>FS</code>
     *   as a string
     */
    public Optional<String> getFsOpt() {
        return getAnnotationStringOpt("FS");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>FZ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>FZ</code>
     */
    public boolean containsFz() {
        return containsAnnotationKey("FZ");
    }

    /**
     * Return the Type=B first letter S value for the reserved key <code>FZ</code>
     * as an immutable list of integers.
     *
     * @return the Type=B first letter S value for the reserved key <code>FZ</code>
     *    as an immutable list of integers
     */
    public List<Integer> getFz() {
        return getAnnotationIntegers("FZ");
    }

    /**
     * Return an optional Type=B first letter S value for the reserved key <code>FZ</code>
     * as an immutable list of integers.
     *
     * @return an optional Type=B first letter S value for the reserved key <code>FZ</code>
     *   as an immutable list of integers
     */
    public Optional<List<Integer>> getFzOpt() {
        return getAnnotationIntegersOpt("FZ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>H0</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>H0</code>
     */
    public boolean containsH0() {
        return containsAnnotationKey("H0");
    }

    /**
     * Return the Type=i value for the reserved key <code>H0</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>H0</code>
     *    as an integer
     */
    public int getH0() {
        return getAnnotationInteger("H0");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>H0</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>H0</code>
     *   as an integer
     */
    public Optional<Integer> getH0Opt() {
        return getAnnotationIntegerOpt("H0");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>H1</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>H1</code>
     */
    public boolean containsH1() {
        return containsAnnotationKey("H1");
    }

    /**
     * Return the Type=i value for the reserved key <code>H1</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>H1</code>
     *    as an integer
     */
    public int getH1() {
        return getAnnotationInteger("H1");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>H1</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>H1</code>
     *   as an integer
     */
    public Optional<Integer> getH1Opt() {
        return getAnnotationIntegerOpt("H1");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>H2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>H2</code>
     */
    public boolean containsH2() {
        return containsAnnotationKey("H2");
    }

    /**
     * Return the Type=i value for the reserved key <code>H2</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>H2</code>
     *    as an integer
     */
    public int getH2() {
        return getAnnotationInteger("H2");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>H2</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>H2</code>
     *   as an integer
     */
    public Optional<Integer> getH2Opt() {
        return getAnnotationIntegerOpt("H2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>HI</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>HI</code>
     */
    public boolean containsHi() {
        return containsAnnotationKey("HI");
    }

    /**
     * Return the Type=i value for the reserved key <code>HI</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>HI</code>
     *    as an integer
     */
    public int getHi() {
        return getAnnotationInteger("HI");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>HI</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>HI</code>
     *   as an integer
     */
    public Optional<Integer> getHiOpt() {
        return getAnnotationIntegerOpt("HI");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>IH</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>IH</code>
     */
    public boolean containsIh() {
        return containsAnnotationKey("IH");
    }

    /**
     * Return the Type=i value for the reserved key <code>IH</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>IH</code>
     *    as an integer
     */
    public int getIh() {
        return getAnnotationInteger("IH");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>IH</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>IH</code>
     *   as an integer
     */
    public Optional<Integer> getIhOpt() {
        return getAnnotationIntegerOpt("IH");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>LB</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>LB</code>
     */
    public boolean containsLb() {
        return containsAnnotationKey("LB");
    }

    /**
     * Return the Type=Z value for the reserved key <code>LB</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>LB</code>
     *    as a string
     */
    public String getLb() {
        return getAnnotationString("LB");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>LB</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>LB</code>
     *   as a string
     */
    public Optional<String> getLbOpt() {
        return getAnnotationStringOpt("LB");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MC</code>
     */
    public boolean containsMc() {
        return containsAnnotationKey("MC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>MC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>MC</code>
     *    as a string
     */
    public String getMc() {
        return getAnnotationString("MC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>MC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>MC</code>
     *   as a string
     */
    public Optional<String> getMcOpt() {
        return getAnnotationStringOpt("MC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MD</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MD</code>
     */
    public boolean containsMd() {
        return containsAnnotationKey("MD");
    }

    /**
     * Return the Type=Z value for the reserved key <code>MD</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>MD</code>
     *    as a string
     */
    public String getMd() {
        return getAnnotationString("MD");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>MD</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>MD</code>
     *   as a string
     */
    public Optional<String> getMdOpt() {
        return getAnnotationStringOpt("MD");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MI</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MI</code>
     */
    public boolean containsMi() {
        return containsAnnotationKey("MI");
    }

    /**
     * Return the Type=Z value for the reserved key <code>MI</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>MI</code>
     *    as a string
     */
    public String getMi() {
        return getAnnotationString("MI");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>MI</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>MI</code>
     *   as a string
     */
    public Optional<String> getMiOpt() {
        return getAnnotationStringOpt("MI");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MQ</code>
     */
    public boolean containsMq() {
        return containsAnnotationKey("MQ");
    }

    /**
     * Return the Type=i value for the reserved key <code>MQ</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>MQ</code>
     *    as an integer
     */
    public int getMq() {
        return getAnnotationInteger("MQ");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>MQ</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>MQ</code>
     *   as a integer
     */
    public Optional<Integer> getMqOpt() {
        return getAnnotationIntegerOpt("MQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>NH</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>NH</code>
     */
    public boolean containsNh() {
        return containsAnnotationKey("NH");
    }

    /**
     * Return the Type=i value for the reserved key <code>NH</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>NH</code>
     *    as an integer
     */
    public int getNh() {
        return getAnnotationInteger("NH");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>NH</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>NH</code>
     *   as an integer
     */
    public Optional<Integer> getNhOpt() {
        return getAnnotationIntegerOpt("NH");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>NM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>NM</code>
     */
    public boolean containsNm() {
        return containsAnnotationKey("NM");
    }

    /**
     * Return the Type=i value for the reserved key <code>NM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>NM</code>
     *    as an integer
     */
    public int getNm() {
        return getAnnotationInteger("NM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>NM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>NM</code>
     *   as an integer
     */
    public Optional<Integer> getNmOpt() {
        return getAnnotationIntegerOpt("NM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OC</code>
     */
    public boolean containsOc() {
        return containsAnnotationKey("OC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>OC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>OC</code>
     *    as a string
     */
    public String getOc() {
        return getAnnotationString("OC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>OC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>OC</code>
     *   as a string
     */
    public Optional<String> getOcOpt() {
        return getAnnotationStringOpt("OC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OP</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OP</code>
     */
    public boolean containsOp() {
        return containsAnnotationKey("OP");
    }

    /**
     * Return the Type=i value for the reserved key <code>OP</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>OP</code>
     *    as an integer
     */
    public int getOp() {
        return getAnnotationInteger("OP");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>OP</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>OP</code>
     *   as an integer
     */
    public Optional<Integer> getOpOpt() {
        return getAnnotationIntegerOpt("OP");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OQ</code>
     */
    public boolean containsOq() {
        return containsAnnotationKey("OQ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>OQ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>OQ</code>
     *    as a string
     */
    public String getOq() {
        return getAnnotationString("OQ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>OQ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>OQ</code>
     *   as a string
     */
    public Optional<String> getOqOpt() {
        return getAnnotationStringOpt("OQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OX</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OX</code>
     */
    public boolean containsOx() {
        return containsAnnotationKey("OX");
    }

    /**
     * Return the Type=Z value for the reserved key <code>OX</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>OX</code>
     *    as a string
     */
    public String getOx() {
        return getAnnotationString("OX");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>OX</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>OX</code>
     *   as a string
     */
    public Optional<String> getOxOpt() {
        return getAnnotationStringOpt("OX");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PG</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PG</code>
     */
    public boolean containsPg() {
        return containsAnnotationKey("PG");
    }

    /**
     * Return the Type=Z value for the reserved key <code>PG</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>PG</code>
     *    as a string
     */
    public String getPg() {
        return getAnnotationString("PG");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>PG</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>PG</code>
     *   as a string
     */
    public Optional<String> getPgOpt() {
        return getAnnotationStringOpt("PG");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PQ</code>
     */
    public boolean containsPq() {
        return containsAnnotationKey("PQ");
    }

    /**
     * Return the Type=i value for the reserved key <code>PQ</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>PQ</code>
     *    as a integer
     */
    public int getPq() {
        return getAnnotationInteger("PQ");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>PQ</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>PQ</code>
     *   as a integer
     */
    public Optional<Integer> getPqOpt() {
        return getAnnotationIntegerOpt("PQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PT</code>
     */
    public boolean containsPt() {
        return containsAnnotationKey("PT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>PT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>PT</code>
     *    as a string
     */
    public String getPt() {
        return getAnnotationString("PT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>PT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>PT</code>
     *   as a string
     */
    public Optional<String> getPtOpt() {
        return getAnnotationStringOpt("PT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PU</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PU</code>
     */
    public boolean containsPu() {
        return containsAnnotationKey("PU");
    }

    /**
     * Return the Type=Z value for the reserved key <code>PU</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>PU</code>
     *    as a string
     */
    public String getPu() {
        return getAnnotationString("PU");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>PU</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>PU</code>
     *   as a string
     */
    public Optional<String> getPuOpt() {
        return getAnnotationStringOpt("PU");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>Q2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>Q2</code>
     */
    public boolean containsQ2() {
        return containsAnnotationKey("Q2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>Q2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>Q2</code>
     *    as a string
     */
    public String getQ2() {
        return getAnnotationString("Q2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>Q2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>Q2</code>
     *   as a string
     */
    public Optional<String> getQ2Opt() {
        return getAnnotationStringOpt("Q2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>QT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>QT</code>
     */
    public boolean containsQt() {
        return containsAnnotationKey("QT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>QT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>QT</code>
     *    as a string
     */
    public String getQt() {
        return getAnnotationString("QT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>QT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>QT</code>
     *   as a string
     */
    public Optional<String> getQtOpt() {
        return getAnnotationStringOpt("QT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>QX</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>QX</code>
     */
    public boolean containsQx() {
        return containsAnnotationKey("QX");
    }

    /**
     * Return the Type=Z value for the reserved key <code>QX</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>QX</code>
     *    as a string
     */
    public String getQx() {
        return getAnnotationString("QX");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>QX</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>QX</code>
     *   as a string
     */
    public Optional<String> getQxOpt() {
        return getAnnotationStringOpt("QX");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>R2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>R2</code>
     */
    public boolean containsR2() {
        return containsAnnotationKey("R2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>R2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>R2</code>
     *    as a string
     */
    public String getR2() {
        return getAnnotationString("R2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>R2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>R2</code>
     *   as a string
     */
    public Optional<String> getR2Opt() {
        return getAnnotationStringOpt("R2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>RG</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>RG</code>
     */
    public boolean containsRg() {
        return containsAnnotationKey("RG");
    }

    /**
     * Return the Type=Z value for the reserved key <code>RG</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>RG</code>
     *    as a string
     */
    public String getRg() {
        return getAnnotationString("RG");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>RG</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>RG</code>
     *   as a string
     */
    public Optional<String> getRgOpt() {
        return getAnnotationStringOpt("RG");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>RT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>RT</code>
     */
    public boolean containsRt() {
        return containsAnnotationKey("RT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>RT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>RT</code>
     *    as a string
     */
    public String getRt() {
        return getAnnotationString("RT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>RT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>RT</code>
     *   as a string
     */
    public Optional<String> getRtOpt() {
        return getAnnotationStringOpt("RT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>RX</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>RX</code>
     */
    public boolean containsRx() {
        return containsAnnotationKey("RX");
    }

    /**
     * Return the Type=Z value for the reserved key <code>RX</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>RX</code>
     *    as a string
     */
    public String getRx() {
        return getAnnotationString("RX");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>RX</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>RX</code>
     *   as a string
     */
    public Optional<String> getRxOpt() {
        return getAnnotationStringOpt("RX");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>SA</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>SA</code>
     */
    public boolean containsSa() {
        return containsAnnotationKey("SA");
    }

    /**
     * Return the Type=Z value for the reserved key <code>SA</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>SA</code>
     *    as a string
     */
    public String getSa() {
        return getAnnotationString("SA");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>SA</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>SA</code>
     *   as a string
     */
    public Optional<String> getSaOpt() {
        return getAnnotationStringOpt("SA");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>SM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>SM</code>
     */
    public boolean containsSm() {
        return containsAnnotationKey("SM");
    }

    /**
     * Return the Type=i value for the reserved key <code>SM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>SM</code>
     *    as a integer
     */
    public int getSm() {
        return getAnnotationInteger("SM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>SM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>SM</code>
     *   as a integer
     */
    public Optional<Integer> getSmOpt() {
        return getAnnotationIntegerOpt("SM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>TC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>TC</code>
     */
    public boolean containsTc() {
        return containsAnnotationKey("TC");
    }

    /**
     * Return the Type=i value for the reserved key <code>TC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>TC</code>
     *    as a integer
     */
    public int getTc() {
        return getAnnotationInteger("TC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>TC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>TC</code>
     *   as a integer
     */
    public Optional<Integer> getTcOpt() {
        return getAnnotationIntegerOpt("TC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>U2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>U2</code>
     */
    public boolean containsU2() {
        return containsAnnotationKey("U2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>U2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>U2</code>
     *    as a string
     */
    public String getU2() {
        return getAnnotationString("U2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>U2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>U2</code>
     *   as a string
     */
    public Optional<String> getU2Opt() {
        return getAnnotationStringOpt("U2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>UQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>UQ</code>
     */
    public boolean containsUq() {
        return containsAnnotationKey("UQ");
    }

    /**
     * Return the Type=i value for the reserved key <code>UQ</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>UQ</code>
     *    as an integer
     */
    public int getUq() {
        return getAnnotationInteger("UQ");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>UQ</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>UQ</code>
     *   as an integer
     */
    public Optional<Integer> getUqOpt() {
        return getAnnotationIntegerOpt("UQ");
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof SamRecord)) {
            return false;
        }
        SamRecord r = (SamRecord) o;

        return Objects.equals(qname, r.getQname())
            && Objects.equals(flag, r.getFlag())
            && Objects.equals(rname, r.getRname())
            && Objects.equals(pos, r.getPos())
            && Objects.equals(mapq, r.getMapq())
            && Objects.equals(cigar, r.getCigar())
            && Objects.equals(rnext, r.getRnext())
            && Objects.equals(pnext, r.getPnext())
            && Objects.equals(tlen, r.getTlen())
            && Objects.equals(seq, r.getSeq())
            && Objects.equals(qual, r.getQual())
            && Objects.equals(getAnnotations(), r.getAnnotations());
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb,
                        qname == null ? "*" : qname,
                        flag,
                        rname == null ? "*" : rname,
                        pos,
                        mapq,
                        cigar == null ? "*" : cigar,
                        rnext == null ? "*" : rnext,
                        pnext,
                        tlen,
                        seq == null ? "*" : seq,
                        qual == null ? "*" : qual);

        if (!getAnnotations().isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, getAnnotations().values());
        }
        return sb.toString();
    }

    /**
     * Parse a SAM record from the specified value.
     *
     * @param value value, must not be null
     * @return a SAM record parsed from the specified value
     */
    public static SamRecord valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 11) {
            throw new IllegalArgumentException("invalid record, expected 11 or more tokens, found " + tokens.size());
        }
        // QNAME String [!-?A-~]{1,254} Query template NAME
        String qname = "*".equals(tokens.get(0)) ? null : tokens.get(0);
        // FLAG Int [0,2^16-1] bitwise FLAG
        int flag = Integer.parseInt(tokens.get(1));
        // RNAME String \*|[!-()+-<>-~][!-~]* Reference sequence NAME
        String rname = "*".equals(tokens.get(2)) ? null : tokens.get(2);
        // POS Int [0,2^31-1] 1-based leftmost mapping POSition
        int pos = Integer.parseInt(tokens.get(3));
        // MAPQ Int [0,28-1] MAPping Quality
        int mapq = Integer.parseInt(tokens.get(4));
        // CIGAR String \*|([0-9]+[MIDNSHPX=])+ CIGAR string
        String cigar = "*".equals(tokens.get(5)) ? null : tokens.get(5);
        // RNEXT String \*|=|[!-()+-<>-~][!-~]* Ref. name of the mate/next read
        String rnext = "*".equals(tokens.get(6)) ? null : tokens.get(6);
        // PNEXT Int [0,2^31-1] Position of the mate/next read
        int pnext = Integer.parseInt(tokens.get(7));
        // TLEN Int [-2^31+1,2^31-1] observed Template LENgth
        int tlen = Integer.parseInt(tokens.get(8));
        // SEQ String \*|[A-Za-z=.]+ segment SEQuence
        String seq = "*".equals(tokens.get(9)) ? null : tokens.get(9);
        // QUAL String [!-~]+ ASCII of Phred-scaled base QUALity+33
        String qual = "*".equals(tokens.get(10)) ? null : tokens.get(10);

        ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
        for (int i = 11; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.isEmpty()) {
                Annotation annotation = Annotation.valueOf(tokens.get(i));
                annotations.put(annotation.getName(), annotation);
            }
        }

        return new SamRecord(qname,
                             flag,
                             rname,
                             pos,
                             mapq,
                             cigar,
                             rnext,
                             pnext,
                             tlen,
                             seq,
                             qual,
                             annotations.build());
    }
}
