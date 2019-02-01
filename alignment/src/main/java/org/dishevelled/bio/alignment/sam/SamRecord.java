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

import static org.dishevelled.bio.alignment.sam.SamFields.parseByteArray;
import static org.dishevelled.bio.alignment.sam.SamFields.parseBytes;
import static org.dishevelled.bio.alignment.sam.SamFields.parseCharacter;
import static org.dishevelled.bio.alignment.sam.SamFields.parseFloat;
import static org.dishevelled.bio.alignment.sam.SamFields.parseFloats;
import static org.dishevelled.bio.alignment.sam.SamFields.parseInteger;
import static org.dishevelled.bio.alignment.sam.SamFields.parseIntegers;
import static org.dishevelled.bio.alignment.sam.SamFields.parseString;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

/**
 * SAM record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class SamRecord {
    /** Line number. */
    private final long lineNumber;

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

    /** Optional field values keyed by tag. */
    private final ListMultimap<String, String> fields;

    /** Optional field types keyed by tag. */
    private final Map<String, String> fieldTypes;

    /** Optional field array types keyed by tag. */
    private final Map<String, String> fieldArrayTypes;


    /**
     * Create a new SAM record.
     *
     * @param lineNumber line number
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
     * @param fields optional field values keyed by tag, must not be null
     * @param fieldTypes optional field types keyed by tag, must not be null
     * @param fieldArrayTypes optional field array types keyed by tag, must not be null
     */
    private SamRecord(final long lineNumber,
                      @Nullable final String qname,
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
                      final ListMultimap<String, String> fields,
                      final Map<String, String> fieldTypes,
                      final Map<String, String> fieldArrayTypes) {

        checkNotNull(fields);
        checkNotNull(fieldTypes);
        checkNotNull(fieldArrayTypes);

        this.lineNumber = lineNumber;
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
        this.fields = fields;
        this.fieldTypes = fieldTypes;
        this.fieldArrayTypes = fieldArrayTypes;
    }


    /**
     * Return the line number for this SAM record.
     *
     * @return the line number for this SAM record
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * Return the QNAME mandatory field for this SAM record. May be null.
     *
     * @return the QNAME mandatory field for this SAM record.
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
     * @return the FLAG mandatory field for this SAM record.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Return the RNAME mandatory field for this SAM record. May be null.
     *
     * @return the RNAME mandatory field for this SAM record.
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
     * @return the POS mandatory field for this SAM record.
     */
    public int getPos() {
        return pos;
    }

    /**
     * Return the MAPQ mandatory field for this SAM record.
     *
     * @return the MAPQ mandatory field for this SAM record.
     */
    public int getMapq() {
        return mapq;
    }

    /**
     * Return the CIGAR mandatory field for this SAM record. May be null.
     *
     * @return the CIGAR mandatory field for this SAM record.
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
     * @return the RNEXT mandatory field for this SAM record.
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
     * @return the PNEXT mandatory field for this SAM record.
     */
    public int getPnext() {
        return pnext;
    }

    /**
     * Return the TLEN mandatory field for this SAM record.
     *
     * @return the TLEN mandatory field for this SAM record.
     */
    public int getTlen() {
        return tlen;
    }

    /**
     * Return the SEQ mandatory field for this SAM record. May be null.
     *
     * @return the SEQ mandatory field for this SAM record.
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
     * @return the QUAL mandatory field for this SAM record.
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

    /**
     * Return the optional field values for this SAM record keyed by tag.
     *
     * @return the optional field values for this SAM record keyed by tag
     */
    public ListMultimap<String, String> getFields() {
        return fields;
    }

    /**
     * Return the optional field types for this SAM record keyed by tag.
     *
     * @return the optional field types for this SAM record keyed by tag
     */
    public Map<String, String> getFieldTypes() {
        return fieldTypes;
    }

    /**
     * Return the optional field array types for this SAM record keyed by tag.
     *
     * @return the optional field array types for this SAM record keyed by tag
     */
    public Map<String, String> getFieldArrayTypes() {
        return fieldArrayTypes;
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
        return containsFieldKey("AM");
    }

    /**
     * Return the Type=i value for the reserved key <code>AM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>AM</code>
     *    as an integer
     */
    public int getAm() {
        return getFieldInteger("AM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>AM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>AM</code>
     *   as an integer
     */
    public Optional<Integer> getAmOpt() {
        return getFieldIntegerOpt("AM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>AS</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>AS</code>
     */
    public boolean containsAs() {
        return containsFieldKey("AS");
    }

    /**
     * Return the Type=i value for the reserved key <code>AS</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>AS</code>
     *    as an integer
     */
    public int getAs() {
        return getFieldInteger("AS");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>AS</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>AS</code>
     *   as an integer
     */
    public Optional<Integer> getAsOpt() {
        return getFieldIntegerOpt("AS");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>BC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>BC</code>
     */
    public boolean containsBc() {
        return containsFieldKey("BC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>BC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>BC</code>
     *    as a string
     */
    public String getBc() {
        return getFieldString("BC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>BC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>BC</code>
     *   as a string
     */
    public Optional<String> getBcOpt() {
        return getFieldStringOpt("BC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>BQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>BQ</code>
     */
    public boolean containsBq() {
        return containsFieldKey("BQ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>BQ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>BQ</code>
     *    as a string
     */
    public String getBq() {
        return getFieldString("BQ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>BQ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>BQ</code>
     *   as a string
     */
    public Optional<String> getBqOpt() {
        return getFieldStringOpt("BQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>BZ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>BZ</code>
     */
    public boolean containsBz() {
        return containsFieldKey("BZ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>BZ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>BZ</code>
     *    as a string
     */
    public String getBz() {
        return getFieldString("BZ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>BZ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>BZ</code>
     *   as a string
     */
    public Optional<String> getBzOpt() {
        return getFieldStringOpt("BZ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CC</code>
     */
    public boolean containsCc() {
        return containsFieldKey("CC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CC</code>
     *    as a string
     */
    public String getCc() {
        return getFieldString("CC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CC</code>
     *   as a string
     */
    public Optional<String> getCcOpt() {
        return getFieldStringOpt("CC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CG</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CG</code>
     */
    public boolean containsCg() {
        return containsFieldKey("CG");
    }

    /**
     * Return the Type=B first letter I value for the reserved key <code>CG</code>
     * as an immutable list of integers.
     *
     * @return the Type=B first letter I value for the reserved key <code>CG</code>
     *    as an immutable list of integers
     */
    public List<Integer> getCg() {
        return getFieldIntegers("CG");
    }

    /**
     * Return an optional Type=B first letter I value for the reserved key <code>CG</code>
     * as an immutable list of integers.
     *
     * @return an optional Type=B first letter I value for the reserved key <code>CG</code>
     *   as an immutable list of integers
     */
    public Optional<List<Integer>> getCgOpt() {
        return getFieldIntegersOpt("CG");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CM</code>
     */
    public boolean containsCm() {
        return containsFieldKey("CM");
    }

    /**
     * Return the Type=i value for the reserved key <code>CM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>CM</code>
     *    as an integer
     */
    public int getCm() {
        return getFieldInteger("CM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>CM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>CM</code>
     *   as an integer
     */
    public Optional<Integer> getCmOpt() {
        return getFieldIntegerOpt("CM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CO</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CO</code>
     */
    public boolean containsCo() {
        return containsFieldKey("CO");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CO</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CO</code>
     *    as a string
     */
    public String getCo() {
        return getFieldString("CO");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CO</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CO</code>
     *   as a string
     */
    public Optional<String> getCoOpt() {
        return getFieldStringOpt("CO");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CP</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CP</code>
     */
    public boolean containsCp() {
        return containsFieldKey("CP");
    }

    /**
     * Return the Type=i value for the reserved key <code>CP</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>CP</code>
     *    as an integer
     */
    public int getCp() {
        return getFieldInteger("CP");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>CP</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>CP</code>
     *   as an integer
     */
    public Optional<Integer> getCpOpt() {
        return getFieldIntegerOpt("CP");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CQ</code>
     */
    public boolean containsCq() {
        return containsFieldKey("CQ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CQ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CQ</code>
     *    as a string
     */
    public String getCq() {
        return getFieldString("CQ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CQ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CQ</code>
     *   as a string
     */
    public Optional<String> getCqOpt() {
        return getFieldStringOpt("CQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CS</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CS</code>
     */
    public boolean containsCs() {
        return containsFieldKey("CS");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CS</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CS</code>
     *    as a string
     */
    public String getCs() {
        return getFieldString("CS");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CS</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CS</code>
     *   as a string
     */
    public Optional<String> getCsOpt() {
        return getFieldStringOpt("CS");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>CT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>CT</code>
     */
    public boolean containsCt() {
        return containsFieldKey("CT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>CT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>CT</code>
     *    as a string
     */
    public String getCt() {
        return getFieldString("CT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>CT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>CT</code>
     *   as a string
     */
    public Optional<String> getCtOpt() {
        return getFieldStringOpt("CT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>E2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>E2</code>
     */
    public boolean containsE2() {
        return containsFieldKey("E2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>E2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>E2</code>
     *    as a string
     */
    public String getE2() {
        return getFieldString("E2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>E2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>E2</code>
     *   as a string
     */
    public Optional<String> getE2Opt() {
        return getFieldStringOpt("E2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>FI</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>FI</code>
     */
    public boolean containsFi() {
        return containsFieldKey("FI");
    }

    /**
     * Return the Type=i value for the reserved key <code>FI</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>FI</code>
     *    as an integer
     */
    public int getFi() {
        return getFieldInteger("FI");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>FI</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>FI</code>
     *   as an integer
     */
    public Optional<Integer> getFiOpt() {
        return getFieldIntegerOpt("FI");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>FS</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>FS</code>
     */
    public boolean containsFs() {
        return containsFieldKey("FS");
    }

    /**
     * Return the Type=Z value for the reserved key <code>FS</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>FS</code>
     *    as a string
     */
    public String getFs() {
        return getFieldString("FS");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>FS</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>FS</code>
     *   as a string
     */
    public Optional<String> getFsOpt() {
        return getFieldStringOpt("FS");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>FZ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>FZ</code>
     */
    public boolean containsFz() {
        return containsFieldKey("FZ");
    }

    /**
     * Return the Type=B first letter S value for the reserved key <code>FZ</code>
     * as an immutable list of integers.
     *
     * @return the Type=B first letter S value for the reserved key <code>FZ</code>
     *    as an immutable list of integers
     */
    public List<Integer> getFz() {
        return getFieldIntegers("FZ");
    }

    /**
     * Return an optional Type=B first letter S value for the reserved key <code>FZ</code>
     * as an immutable list of integers.
     *
     * @return an optional Type=B first letter S value for the reserved key <code>FZ</code>
     *   as an immutable list of integers
     */
    public Optional<List<Integer>> getFzOpt() {
        return getFieldIntegersOpt("FZ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>H0</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>H0</code>
     */
    public boolean containsH0() {
        return containsFieldKey("H0");
    }

    /**
     * Return the Type=i value for the reserved key <code>H0</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>H0</code>
     *    as an integer
     */
    public int getH0() {
        return getFieldInteger("H0");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>H0</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>H0</code>
     *   as an integer
     */
    public Optional<Integer> getH0Opt() {
        return getFieldIntegerOpt("H0");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>H1</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>H1</code>
     */
    public boolean containsH1() {
        return containsFieldKey("H1");
    }

    /**
     * Return the Type=i value for the reserved key <code>H1</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>H1</code>
     *    as an integer
     */
    public int getH1() {
        return getFieldInteger("H1");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>H1</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>H1</code>
     *   as an integer
     */
    public Optional<Integer> getH1Opt() {
        return getFieldIntegerOpt("H1");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>H2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>H2</code>
     */
    public boolean containsH2() {
        return containsFieldKey("H2");
    }

    /**
     * Return the Type=i value for the reserved key <code>H2</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>H2</code>
     *    as an integer
     */
    public int getH2() {
        return getFieldInteger("H2");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>H2</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>H2</code>
     *   as an integer
     */
    public Optional<Integer> getH2Opt() {
        return getFieldIntegerOpt("H2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>HI</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>HI</code>
     */
    public boolean containsHi() {
        return containsFieldKey("HI");
    }

    /**
     * Return the Type=i value for the reserved key <code>HI</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>HI</code>
     *    as an integer
     */
    public int getHi() {
        return getFieldInteger("HI");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>HI</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>HI</code>
     *   as an integer
     */
    public Optional<Integer> getHiOpt() {
        return getFieldIntegerOpt("HI");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>IH</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>IH</code>
     */
    public boolean containsIh() {
        return containsFieldKey("IH");
    }

    /**
     * Return the Type=i value for the reserved key <code>IH</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>IH</code>
     *    as an integer
     */
    public int getIh() {
        return getFieldInteger("IH");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>IH</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>IH</code>
     *   as an integer
     */
    public Optional<Integer> getIhOpt() {
        return getFieldIntegerOpt("IH");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>LB</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>LB</code>
     */
    public boolean containsLb() {
        return containsFieldKey("LB");
    }

    /**
     * Return the Type=Z value for the reserved key <code>LB</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>LB</code>
     *    as a string
     */
    public String getLb() {
        return getFieldString("LB");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>LB</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>LB</code>
     *   as a string
     */
    public Optional<String> getLbOpt() {
        return getFieldStringOpt("LB");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MC</code>
     */
    public boolean containsMc() {
        return containsFieldKey("MC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>MC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>MC</code>
     *    as a string
     */
    public String getMc() {
        return getFieldString("MC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>MC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>MC</code>
     *   as a string
     */
    public Optional<String> getMcOpt() {
        return getFieldStringOpt("MC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MD</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MD</code>
     */
    public boolean containsMd() {
        return containsFieldKey("MD");
    }

    /**
     * Return the Type=Z value for the reserved key <code>MD</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>MD</code>
     *    as a string
     */
    public String getMd() {
        return getFieldString("MD");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>MD</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>MD</code>
     *   as a string
     */
    public Optional<String> getMdOpt() {
        return getFieldStringOpt("MD");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MI</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MI</code>
     */
    public boolean containsMi() {
        return containsFieldKey("MI");
    }

    /**
     * Return the Type=Z value for the reserved key <code>MI</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>MI</code>
     *    as a string
     */
    public String getMi() {
        return getFieldString("MI");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>MI</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>MI</code>
     *   as a string
     */
    public Optional<String> getMiOpt() {
        return getFieldStringOpt("MI");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>MQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>MQ</code>
     */
    public boolean containsMq() {
        return containsFieldKey("MQ");
    }

    /**
     * Return the Type=i value for the reserved key <code>MQ</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>MQ</code>
     *    as an integer
     */
    public int getMq() {
        return getFieldInteger("MQ");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>MQ</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>MQ</code>
     *   as a integer
     */
    public Optional<Integer> getMqOpt() {
        return getFieldIntegerOpt("MQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>NH</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>NH</code>
     */
    public boolean containsNh() {
        return containsFieldKey("NH");
    }

    /**
     * Return the Type=i value for the reserved key <code>NH</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>NH</code>
     *    as an integer
     */
    public int getNh() {
        return getFieldInteger("NH");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>NH</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>NH</code>
     *   as an integer
     */
    public Optional<Integer> getNhOpt() {
        return getFieldIntegerOpt("NH");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>NM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>NM</code>
     */
    public boolean containsNm() {
        return containsFieldKey("NM");
    }

    /**
     * Return the Type=i value for the reserved key <code>NM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>NM</code>
     *    as an integer
     */
    public int getNm() {
        return getFieldInteger("NM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>NM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>NM</code>
     *   as an integer
     */
    public Optional<Integer> getNmOpt() {
        return getFieldIntegerOpt("NM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OC</code>
     */
    public boolean containsOc() {
        return containsFieldKey("OC");
    }

    /**
     * Return the Type=Z value for the reserved key <code>OC</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>OC</code>
     *    as a string
     */
    public String getOc() {
        return getFieldString("OC");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>OC</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>OC</code>
     *   as a string
     */
    public Optional<String> getOcOpt() {
        return getFieldStringOpt("OC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OP</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OP</code>
     */
    public boolean containsOp() {
        return containsFieldKey("OP");
    }

    /**
     * Return the Type=i value for the reserved key <code>OP</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>OP</code>
     *    as an integer
     */
    public int getOp() {
        return getFieldInteger("OP");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>OP</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>OP</code>
     *   as an integer
     */
    public Optional<Integer> getOpOpt() {
        return getFieldIntegerOpt("OP");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OQ</code>
     */
    public boolean containsOq() {
        return containsFieldKey("OQ");
    }

    /**
     * Return the Type=Z value for the reserved key <code>OQ</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>OQ</code>
     *    as a string
     */
    public String getOq() {
        return getFieldString("OQ");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>OQ</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>OQ</code>
     *   as a string
     */
    public Optional<String> getOqOpt() {
        return getFieldStringOpt("OQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>OX</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>OX</code>
     */
    public boolean containsOx() {
        return containsFieldKey("OX");
    }

    /**
     * Return the Type=Z value for the reserved key <code>OX</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>OX</code>
     *    as a string
     */
    public String getOx() {
        return getFieldString("OX");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>OX</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>OX</code>
     *   as a string
     */
    public Optional<String> getOxOpt() {
        return getFieldStringOpt("OX");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PG</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PG</code>
     */
    public boolean containsPg() {
        return containsFieldKey("PG");
    }

    /**
     * Return the Type=Z value for the reserved key <code>PG</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>PG</code>
     *    as a string
     */
    public String getPg() {
        return getFieldString("PG");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>PG</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>PG</code>
     *   as a string
     */
    public Optional<String> getPgOpt() {
        return getFieldStringOpt("PG");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PQ</code>
     */
    public boolean containsPq() {
        return containsFieldKey("PQ");
    }

    /**
     * Return the Type=i value for the reserved key <code>PQ</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>PQ</code>
     *    as a integer
     */
    public int getPq() {
        return getFieldInteger("PQ");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>PQ</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>PQ</code>
     *   as a integer
     */
    public Optional<Integer> getPqOpt() {
        return getFieldIntegerOpt("PQ");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PT</code>
     */
    public boolean containsPt() {
        return containsFieldKey("PT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>PT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>PT</code>
     *    as a string
     */
    public String getPt() {
        return getFieldString("PT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>PT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>PT</code>
     *   as a string
     */
    public Optional<String> getPtOpt() {
        return getFieldStringOpt("PT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>PU</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>PU</code>
     */
    public boolean containsPu() {
        return containsFieldKey("PU");
    }

    /**
     * Return the Type=Z value for the reserved key <code>PU</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>PU</code>
     *    as a string
     */
    public String getPu() {
        return getFieldString("PU");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>PU</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>PU</code>
     *   as a string
     */
    public Optional<String> getPuOpt() {
        return getFieldStringOpt("PU");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>Q2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>Q2</code>
     */
    public boolean containsQ2() {
        return containsFieldKey("Q2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>Q2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>Q2</code>
     *    as a string
     */
    public String getQ2() {
        return getFieldString("Q2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>Q2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>Q2</code>
     *   as a string
     */
    public Optional<String> getQ2Opt() {
        return getFieldStringOpt("Q2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>QT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>QT</code>
     */
    public boolean containsQt() {
        return containsFieldKey("QT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>QT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>QT</code>
     *    as a string
     */
    public String getQt() {
        return getFieldString("QT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>QT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>QT</code>
     *   as a string
     */
    public Optional<String> getQtOpt() {
        return getFieldStringOpt("QT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>QX</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>QX</code>
     */
    public boolean containsQx() {
        return containsFieldKey("QX");
    }

    /**
     * Return the Type=Z value for the reserved key <code>QX</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>QX</code>
     *    as a string
     */
    public String getQx() {
        return getFieldString("QX");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>QX</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>QX</code>
     *   as a string
     */
    public Optional<String> getQxOpt() {
        return getFieldStringOpt("QX");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>R2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>R2</code>
     */
    public boolean containsR2() {
        return containsFieldKey("R2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>R2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>R2</code>
     *    as a string
     */
    public String getR2() {
        return getFieldString("R2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>R2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>R2</code>
     *   as a string
     */
    public Optional<String> getR2Opt() {
        return getFieldStringOpt("R2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>RG</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>RG</code>
     */
    public boolean containsRg() {
        return containsFieldKey("RG");
    }

    /**
     * Return the Type=Z value for the reserved key <code>RG</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>RG</code>
     *    as a string
     */
    public String getRg() {
        return getFieldString("RG");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>RG</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>RG</code>
     *   as a string
     */
    public Optional<String> getRgOpt() {
        return getFieldStringOpt("RG");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>RT</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>RT</code>
     */
    public boolean containsRt() {
        return containsFieldKey("RT");
    }

    /**
     * Return the Type=Z value for the reserved key <code>RT</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>RT</code>
     *    as a string
     */
    public String getRt() {
        return getFieldString("RT");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>RT</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>RT</code>
     *   as a string
     */
    public Optional<String> getRtOpt() {
        return getFieldStringOpt("RT");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>RX</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>RX</code>
     */
    public boolean containsRx() {
        return containsFieldKey("RX");
    }

    /**
     * Return the Type=Z value for the reserved key <code>RX</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>RX</code>
     *    as a string
     */
    public String getRx() {
        return getFieldString("RX");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>RX</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>RX</code>
     *   as a string
     */
    public Optional<String> getRxOpt() {
        return getFieldStringOpt("RX");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>SA</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>SA</code>
     */
    public boolean containsSa() {
        return containsFieldKey("SA");
    }

    /**
     * Return the Type=Z value for the reserved key <code>SA</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>SA</code>
     *    as a string
     */
    public String getSa() {
        return getFieldString("SA");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>SA</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>SA</code>
     *   as a string
     */
    public Optional<String> getSaOpt() {
        return getFieldStringOpt("SA");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>SM</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>SM</code>
     */
    public boolean containsSm() {
        return containsFieldKey("SM");
    }

    /**
     * Return the Type=i value for the reserved key <code>SM</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>SM</code>
     *    as a integer
     */
    public int getSm() {
        return getFieldInteger("SM");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>SM</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>SM</code>
     *   as a integer
     */
    public Optional<Integer> getSmOpt() {
        return getFieldIntegerOpt("SM");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>TC</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>TC</code>
     */
    public boolean containsTc() {
        return containsFieldKey("TC");
    }

    /**
     * Return the Type=i value for the reserved key <code>TC</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>TC</code>
     *    as a integer
     */
    public int getTc() {
        return getFieldInteger("TC");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>TC</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>TC</code>
     *   as a integer
     */
    public Optional<Integer> getTcOpt() {
        return getFieldIntegerOpt("TC");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>U2</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>U2</code>
     */
    public boolean containsU2() {
        return containsFieldKey("U2");
    }

    /**
     * Return the Type=Z value for the reserved key <code>U2</code>
     * as a string.
     *
     * @return the Type=Z value for the reserved key <code>U2</code>
     *    as a string
     */
    public String getU2() {
        return getFieldString("U2");
    }

    /**
     * Return an optional Type=Z value for the reserved key <code>U2</code>
     * as a string.
     *
     * @return an optional Type=Z value for the reserved key <code>U2</code>
     *   as a string
     */
    public Optional<String> getU2Opt() {
        return getFieldStringOpt("U2");
    }

    /**
     * Return true if the optional fields for this SAM record contain
     * the reserved key <code>UQ</code>.
     *
     * @return if the optional fields for this SAM record contain
     *    the reserved key <code>UQ</code>
     */
    public boolean containsUq() {
        return containsFieldKey("UQ");
    }

    /**
     * Return the Type=i value for the reserved key <code>UQ</code>
     * as an integer.
     *
     * @return the Type=i value for the reserved key <code>UQ</code>
     *    as an integer
     */
    public int getUq() {
        return getFieldInteger("UQ");
    }

    /**
     * Return an optional Type=i value for the reserved key <code>UQ</code>
     * as an integer.
     *
     * @return an optional Type=i value for the reserved key <code>UQ</code>
     *   as an integer
     */
    public Optional<Integer> getUqOpt() {
        return getFieldIntegerOpt("UQ");
    }

    /**
     * Return true if this SAM record contains the specified optional field key.
     *
     * @param key key
     * @return true if this SAM record contains the specified optional field key
     */
    public boolean containsFieldKey(final String key) {
        return fields.containsKey(key);
    }

    /**
     * Return the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return the Type=A field value for the specified key parsed into a character
     */
    public char getFieldCharacter(final String key) {
        return parseCharacter(key, fields);
    }
    
    /**
     * Return the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return the Type=f field value for the specified key parsed into a float
     */
    public float getFieldFloat(final String key) {
        return parseFloat(key, fields);
    }

    /**
     * Return the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return the Type=i field value for the specified key parsed into an integer
     */
    public int getFieldInteger(final String key) {
        return parseInteger(key, fields);
    }

    /**
     * Return the Type=H field value for the specified key parsed into a byte array.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into a byte array
     */
    public byte[] getFieldByteArray(final String key) {
        return parseByteArray(key, fields);
    }

    /**
     * Return the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public List<Byte> getFieldBytes(final String key) {
        return parseBytes(key, fields);
    }

    /**
     * Return the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return the Type=Z field value for the specified key parsed into a string
     */
    public String getFieldString(final String key) {
        return parseString(key, fields);
    }

    /**
     * Return the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public List<Float> getFieldFloats(final String key) {
        return parseFloats(key, fields);
    }

    /**
     * Return the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public List<Integer> getFieldIntegers(final String key) {
        return parseIntegers(key, fields);
    }

    /**
     * Return an optional wrapping the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=A field value for the specified key parsed into a character
     */
    public Optional<Character> getFieldCharacterOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldCharacter(key) : null);
    }

    /**
     * Return an optional wrapping the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=f field value for the specified key parsed into a float
     */
    public Optional<Float> getFieldFloatOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloat(key) : null);
    }

    /**
     * Return an optional wrapping the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=i field value for the specified key parsed into an integer
     */
    public Optional<Integer> getFieldIntegerOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldInteger(key) : null);
    }

    /**
     * Return an optional wrapping the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=Z field value for the specified key parsed into a string
     */
    public Optional<String> getFieldStringOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldString(key) : null);
    }

    /**
     * Return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public Optional<List<Byte>> getFieldBytesOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldBytes(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public Optional<List<Float>> getFieldFloatsOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloats(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public Optional<List<Integer>> getFieldIntegersOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldIntegers(key) : null);
    }

    /**
     * Return a new SAM record builder.
     *
     * @return a new SAM record builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Return a new SAM record builder populated with the fields in the specified SAM record.
     *
     * @param record SAM record, must not be null
     * @return a new SAM record builder populated with the fields in the specified SAM record
     */
    public static Builder builder(final SamRecord record) {
        checkNotNull(record);
        return new Builder()
            .withLineNumber(record.getLineNumber())
            .withQname(record.getQname())
            .withFlag(record.getFlag())
            .withRname(record.getRname())
            .withPos(record.getPos())
            .withMapq(record.getMapq())
            .withCigar(record.getCigar())
            .withRnext(record.getRnext())
            .withPnext(record.getPnext())
            .withTlen(record.getTlen())
            .withSeq(record.getSeq())
            .withQual(record.getQual())
            .withFields(record.getFields(), record.getFieldTypes(), record.getFieldArrayTypes());
    }

    /**
     * SAM record builder.
     */
    public static final class Builder {
        /** Line number. */
        private long lineNumber;

        /** QNAME mandatory field. */
        private String qname;

        /** FLAG mandatory field. */
        private int flag;

        /** RNAME mandatory field. */
        private String rname;

        /** POS mandatory field. */
        private int pos;

        /** MAPQ mandatory field. */
        private int mapq;

        /** CIGAR mandatory field. */
        private String cigar;

        /** RNEXT mandatory field. */
        private String rnext;

        /** PNEXT mandatory field. */
        private int pnext;

        /** TLEN mandatory field. */
        private int tlen;

        /** SEQ mandatory field. */
        private String seq;

        /** QUAL mandatory field. */
        private String qual;

        /** Optional field values keyed by tag. */
        private ImmutableListMultimap.Builder<String, String> fields = ImmutableListMultimap.builder();

        /** Optional field types keyed by tag. */
        private ImmutableMap.Builder<String, String> fieldTypes = ImmutableMap.builder();

        /** Optional field array types keyed by tag. */
        private ImmutableMap.Builder<String, String> fieldArrayTypes = ImmutableMap.builder();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this SAM record builder configured with the specified line number.
         *
         * @param lineNumber line number
         * @return this SAM record builder configured with the specified line number
         */
        public Builder withLineNumber(final long lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified QNAME mandatory field.
         *
         * @param qname QNAME mandatory field
         * @return this SAM record builder configured with the specified QNAME mandatory field
         */
        public Builder withQname(@Nullable final String qname) {
            this.qname = qname;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified FLAG mandatory field.
         *
         * @param flag FLAG mandatory field
         * @return this SAM record builder configured with the specified FLAG mandatory field
         */
        public Builder withFlag(final int flag) {
            this.flag = flag;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified RNAME mandatory field.
         *
         * @param rname RNAME mandatory field
         * @return this SAM record builder configured with the specified RNAME mandatory field
         */
        public Builder withRname(@Nullable final String rname) {
            this.rname = rname;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified POS mandatory field.
         *
         * @param pos POS mandatory field
         * @return this SAM record builder configured with the specified POS mandatory field
         */
        public Builder withPos(final int pos) {
            this.pos = pos;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified MAPQ mandatory field.
         *
         * @param mapq MAPQ mandatory field
         * @return this SAM record builder configured with the specified MAPQ mandatory field
         */
        public Builder withMapq(final int mapq) {
            this.mapq = mapq;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified CIGAR mandatory field.
         *
         * @param cigar CIGAR mandatory field
         * @return this SAM record builder configured with the specified CIGAR mandatory field
         */
        public Builder withCigar(@Nullable final String cigar) {
            this.cigar = cigar;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified RNEXT mandatory field.
         *
         * @param rnext RNEXT mandatory field
         * @return this SAM record builder configured with the specified RNEXT mandatory field
         */
        public Builder withRnext(@Nullable final String rnext) {
            this.rnext = rnext;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified PNEXT mandatory field.
         *
         * @param pnext PNEXT mandatory field
         * @return this SAM record builder configured with the specified PNEXT mandatory field
         */
        public Builder withPnext(final int pnext) {
            this.pnext = pnext;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified TLEN mandatory field.
         *
         * @param tlen TLEN mandatory field
         * @return this SAM record builder configured with the specified TLEN mandatory field
         */
        public Builder withTlen(final int tlen) {
            this.tlen = tlen;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified SEQ mandatory field.
         *
         * @param seq SEQ mandatory field
         * @return this SAM record builder configured with the specified SEQ mandatory field
         */
        public Builder withSeq(@Nullable final String seq) {
            this.seq = seq;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified QUAL mandatory field.
         *
         * @param qual QUAL mandatory field
         * @return this SAM record builder configured with the specified QUAL mandatory field
         */
        public Builder withQual(@Nullable final String qual) {
            this.qual = qual;
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified optional field.
         *
         * @param tag optional field tag
         * @param type optional field type
         * @param value optional field value
         * @return this SAM record builder configured with the specified optional field
         */
        public Builder withField(final String tag, final String type, final String value) {
            return withArrayField(tag, type, null, value);
        }

        /**
         * Return this SAM record builder configured with the specified optional array field.
         *
         * @param tag optional array field tag
         * @param type optional array field type
         * @param arrayType optional array field arra type
         * @param values variable number of optional array field values
         * @return this SAM record builder configured with the specified optional array field
         */
        public Builder withArrayField(final String tag, final String type, @Nullable final String arrayType, final String... values) {
            checkNotNull(values);

            fieldTypes.put(tag, type);
            if (arrayType != null) {
                fieldArrayTypes.put(tag, arrayType);
            }
            for (String value : values) {
                fields.put(tag, value);
            }
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified optional fields.
         *
         * @param fields optional field values keyed by tag
         * @param fieldTypes optional field types keyed by tag
         * @param fieldArrayTypes optional field array types keyed by tag
         * @return this SAM record builder configured with the specified optional fields
         */
        public Builder withFields(final ListMultimap<String, String> fields, final Map<String, String> fieldTypes, @Nullable final Map<String, String> fieldArrayTypes) {
            this.fields.putAll(fields);
            this.fieldTypes.putAll(fieldTypes);
            if (fieldArrayTypes != null) {
                this.fieldArrayTypes.putAll(fieldArrayTypes);
            }
            return this;
        }

        /**
         * Return this SAM record builder configured with the specified optional field replacing
         * the previously configured value. Use sparingly, more expensive than <code>withField</code>.
         *
         * @param tag optional field tag
         * @param type optional field type
         * @param value optional field value
         * @return this SAM record builder configured with the specified optional field replacing
         *    the previously configured value
         */
        public Builder replaceField(final String tag, final String type, final String value) {
            return replaceArrayField(tag, type, null, value);
        }

        /**
         * Return this SAM record builder configured with the specified optional array field replacing
         * the previously configured value(s). Use sparingly, more expensive than <code>withArrayField</code>.
         *
         * @param tag optional array field tag
         * @param type optional array field type
         * @param arrayType optional array field array type
         * @param values variable number of optional field values
         * @return this SAM record builder configured with the specified optional array field replacing
         *    the previously configured value(s)
         */
        public Builder replaceArrayField(final String tag, final String type, @Nullable final String arrayType, final String... values) {
            checkNotNull(values);

            // copy old field values except tag
            ListMultimap<String, String> oldFields = this.fields.build();
            this.fields = ImmutableListMultimap.builder();
            for (String key : oldFields.keys()) {
                if (!key.equals(tag)) {
                    this.fields.putAll(key, oldFields.get(key));
                }
            }
            Map<String, String> oldFieldTypes = this.fieldTypes.build();
            this.fieldTypes = ImmutableMap.builder();
            for (String key : oldFieldTypes.keySet()) {
                if (!key.equals(tag)) {
                    this.fieldTypes.put(key, oldFieldTypes.get(key));
                }
            }
            Map<String, String> oldFieldArrayTypes = this.fieldArrayTypes.build();
            this.fieldArrayTypes = ImmutableMap.builder();
            for (String key : oldFieldArrayTypes.keySet()) {
                if (!key.equals(tag)) {
                    this.fieldArrayTypes.put(key, oldFieldArrayTypes.get(key));
                }
            }

            // add new value(s)
            return withArrayField(tag, type, arrayType, values);
        }

        /**
         * Return this SAM record builder configured with the specified optional fields replacing
         * the previously configured field(s). Use sparingly, more expensive than <code>withFields</code>.
         *
         * @param fields optional field values keyed by tag
         * @param fieldTypes optional field types keyed by tag
         * @param fieldArrayTypes optional field array types keyed by tag
         * @return this SAM record builder configured with the specified optional fields replacing
         *    the previously configured field(s)
         */
        public Builder replaceFields(final ListMultimap<String, String> fields, final Map<String, String> fieldTypes, final Map<String, String> fieldArrayTypes) {
            this.fields = ImmutableListMultimap.builder();
            this.fieldTypes = ImmutableMap.builder();
            this.fieldArrayTypes = ImmutableMap.builder();
            return withFields(fields, fieldTypes, fieldArrayTypes);
        }

        /**
         * Reset this SAM record builder.
         *
         * @return this SAM record builder
         */
        public Builder reset() {
            lineNumber = -1L;
            qname = null;
            flag = 0;
            rname = null;
            pos = 0;
            mapq = 255;
            cigar = null;
            rnext = null;
            pnext = 0;
            tlen = 0;
            seq = null;
            qual = null;
            fields = ImmutableListMultimap.builder();
            fieldTypes = ImmutableMap.builder();
            fieldArrayTypes = ImmutableMap.builder();
            return this;
        }

        /**
         * Create and return a new SAM record populated from the configuration of this SAM record builder.
         *
         * @return a new SAM record populated from the configuration of this SAM record builder
         */
        public SamRecord build() {
            // todo: check fields and field types and field array types are consistent
            // todo: check if seq and qual are the same length
            //   etc.
            
            return new SamRecord(lineNumber, qname, flag, rname, pos, mapq, cigar, rnext, pnext, tlen, seq, qual, fields.build(), fieldTypes.build(), fieldArrayTypes.build());
        }
    }
}
