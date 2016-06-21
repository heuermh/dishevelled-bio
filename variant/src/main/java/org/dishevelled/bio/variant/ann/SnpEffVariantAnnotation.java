/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.variant.ann;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.variant.vcf.VcfRecord;

/**
 * SnpEff variant annotation.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class SnpEffVariantAnnotation {
    /*
    ##INFO=<ID=ANN,Number=.,Type=String,Description="Functional annotations: 'Allele | Annotation | Annotation_Impact | Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank | HGVS.c | HGVS.p | cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | ERRORS / WARNINGS / INFO' ">
     */
    private final String allele;
    private final List<String> annotations;
    private final String annotationImpact;
    private final String geneName;
    private final String geneId;
    private final String featureType;
    private final String featureId;
    private final String transcriptBioType;
    private final Integer rank;
    private final String cHgvs;
    private final String pHgvs;
    private final Integer cdnaPosition;
    private final Integer cdnaLength;
    private final Integer cdsPosition;
    private final Integer cdsLength;
    private final Integer aminoAcidPosition;
    private final Integer aminoAcidLength;
    private final Integer distance;
    private final List<String> errors;
    private final int hashCode;

    private SnpEffVariantAnnotation(final String allele, final List<String> annotations, final String annotationImpact, final String geneName, final String geneId,
                                    final String featureType, final String featureId, final String transcriptBioType, final Integer rank, final String cHgvs, final String pHgvs,
                                    final Integer cdnaPosition, final Integer cdnaLength, final Integer cdsPosition, final Integer cdsLength, final Integer aminoAcidPosition,
                                    final Integer aminoAcidLength, final Integer distance, final List<String> errors) {

        checkNotNull(annotations);
        checkNotNull(errors);
        this.allele = allele;
        this.annotations = ImmutableList.copyOf(annotations);
        this.annotationImpact = annotationImpact;
        this.geneName = geneName;
        this.geneId = geneId;
        this.featureType = featureType;
        this.featureId = featureId;
        this.transcriptBioType = transcriptBioType;
        this.rank = rank;
        this.cHgvs = cHgvs;
        this.pHgvs = pHgvs;
        this.cdnaPosition = cdnaPosition;
        this.cdnaLength = cdnaLength;
        this.cdsPosition = cdsPosition;
        this.cdsLength = cdsLength;
        this.aminoAcidPosition = aminoAcidPosition;
        this.aminoAcidLength = aminoAcidLength;
        this.distance = distance;
        this.errors = ImmutableList.copyOf(errors);
        hashCode = Objects.hash(this.allele, this.annotations, this.annotationImpact, this.geneName, this.geneId, this.featureType, this.featureId,
                this.transcriptBioType, this.rank, this.cHgvs, this.pHgvs, this.cdnaPosition, this.cdnaLength, this.cdsPosition, this.cdsLength,
                this.aminoAcidPosition, this.aminoAcidLength, this.distance, this.errors);
    }

    public String allele() {
        return allele;
    }

    public List<String> annotations() {
        return annotations;
    }

    public String annotationImpact() {
        return annotationImpact;
    }

    public String geneName() {
        return geneName;
    }

    public String geneId() {
        return geneId;
    }

    public String featureType() {
        return featureType;
    }

    public String featureId() {
        return featureId;
    }

    public String transcriptBioType() {
        return transcriptBioType;
    }

    public Integer rank() {
        return rank;
    }

    public String cHgvs() {
        return cHgvs;
    }

    public String pHgvs() {
        return pHgvs;
    }

    public Integer cdnaPosition() {
        return cdnaPosition;
    }

    public Integer cdnaLength() {
        return cdnaLength;
    }

    public Integer cdsPosition() {
        return cdsPosition;
    }

    public Integer cdsLength() {
        return cdsLength;
    }

    public Integer aminoAcidPosition() {
        return aminoAcidPosition;
    }

    public Integer aminoAcidLength() {
        return aminoAcidLength;
    }

    public Integer distance() {
        return distance;
    }

    public List<String> errors() {
        return errors;
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
        if (!(o instanceof SnpEffVariantAnnotation)) {
            return false;
        }
        SnpEffVariantAnnotation ann = (SnpEffVariantAnnotation) o;

        return Objects.equals(allele, ann.allele)
                && Objects.equals(annotations, ann.annotations)
                && Objects.equals(annotationImpact, ann.annotationImpact)
                && Objects.equals(geneName, ann.geneName)
                && Objects.equals(geneId, ann.geneId)
                && Objects.equals(featureType, ann.featureType)
                && Objects.equals(featureId, ann.featureId)
                && Objects.equals(transcriptBioType, ann.transcriptBioType)
                && Objects.equals(rank, ann.rank)
                && Objects.equals(cHgvs, ann.cHgvs)
                && Objects.equals(pHgvs, ann.pHgvs)
                && Objects.equals(cdnaPosition, ann.cdnaPosition)
                && Objects.equals(cdnaLength, ann.cdnaLength)
                && Objects.equals(cdsPosition, ann.cdsPosition)
                && Objects.equals(cdsLength, ann.cdsLength)
                && Objects.equals(aminoAcidPosition, ann.aminoAcidPosition)
                && Objects.equals(aminoAcidLength, ann.aminoAcidLength)
                && Objects.equals(distance, ann.distance)
                && Objects.equals(errors, ann.errors);
    }

    @Override
    public String toString() {
        String annotations = Joiner.on("&").join(this.annotations);
        String errors = Joiner.on("&").join(this.errors);
        return Joiner.on("|").join(nullToEmpty(allele), annotations, nullToEmpty(annotationImpact), nullToEmpty(geneName), nullToEmpty(geneId),
                nullToEmpty(featureType), nullToEmpty(featureId), nullToEmpty(transcriptBioType), nullToEmpty(rank), nullToEmpty(cHgvs), nullToEmpty(pHgvs),
                nullToEmpty(cdnaPosition, cdnaLength), nullToEmpty(cdsPosition, cdsLength), nullToEmpty(aminoAcidPosition, aminoAcidLength), nullToEmpty(distance), errors);
    }

    /**
     * Return a new SnpEff variant annotation parsed from the specified value.
     *
     * @param value value to parse
     * @return a new SnpEff variant annotation parsed from the specified value
     * @throws IllegalArgumentException if the value is not valid SnpEff variant annotation format
     * @throws NumberFormatException if a number valued field cannot be parsed as a number
     */
    public static SnpEffVariantAnnotation valueOf(final String value) {
        checkNotNull(value);

        List<String> tokens = Splitter.on("|").splitToList(value);
        if (tokens.size() != 16) {
            throw new IllegalArgumentException("value must have sixteen fields (Allele | Annotation | Annotation_Impact | Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank | HGVS.c | HGVS.p | cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | ERRORS / WARNINGS / INFO)");
        }

        String allele = emptyToNull(tokens.get(0));
        List<String> annotations = Splitter.on("&").omitEmptyStrings().splitToList(tokens.get(1));
        String annotationImpact = emptyToNull(tokens.get(2));
        String geneName = emptyToNull(tokens.get(3));
        String geneId = emptyToNull(tokens.get(4));
        String featureType = emptyToNull(tokens.get(5));
        String featureId = emptyToNull(tokens.get(6));
        String transcriptBioType = emptyToNull(tokens.get(7));
        Integer rank = emptyToNullInteger(tokens.get(8));
        String cHgvs = emptyToNull(tokens.get(9));
        String pHgvs = emptyToNull(tokens.get(10));
        Integer cdnaPosition = numerator(tokens.get(11));
        Integer cdnaLength = denominator(tokens.get(11));
        Integer cdsPosition = numerator(tokens.get(12));
        Integer cdsLength = denominator(tokens.get(12));
        Integer aminoAcidPosition = numerator(tokens.get(13));
        Integer aminoAcidLength = denominator(tokens.get(13));
        Integer distance = emptyToNullInteger(tokens.get(14));
        List<String> errors = Splitter.on("&").omitEmptyStrings().splitToList(tokens.get(15));

        return new SnpEffVariantAnnotation(allele, annotations, annotationImpact, geneName, geneId, featureType, featureId, transcriptBioType,
                rank, cHgvs, pHgvs, cdnaPosition, cdnaLength, cdsPosition, cdsLength, aminoAcidPosition, aminoAcidLength, distance, errors);
    }

    /**
     * Return zero or more SnpEff variant annotations parsed from the INFO field ANN values of the specified VCF record.
     *
     * @param record VCF record to annotate, must not be null
     * @return zero or more SnpEff variant annotations parsed from the INFO field ANN values of the specified VCF record
     * @throws IllegalArgumentException if any of the INFO field ANN values are not valid SnpEff variant annotation format
     * @throws NumberFormatException if a number valued field cannot be parsed as a number
     */
    public static List<SnpEffVariantAnnotation> annotate(final VcfRecord record) {
        checkNotNull(record);
        ImmutableList.Builder<SnpEffVariantAnnotation> builder = ImmutableList.builder();
        for (String annotations : record.getInfo().get("ANN")) {
            for (String annotation : Splitter.on(",").split(annotations.replace("ANN=", ""))) {
                builder.add(valueOf(annotation));
            }
        }
        return builder.build();
    }

    private static String nullToEmpty(final String s) {
        return s == null ? "" : s;
    }

    private static String nullToEmpty(final Integer i) {
        return i == null ? "" : i.toString();
    }

    private static String nullToEmpty(final Integer a, final Integer b) {
        if (a == null && b == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(nullToEmpty(a));
        if (b != null) {
            sb.append("/");
            sb.append(b.toString());
        }
        return sb.toString();
    }

    private static String emptyToNull(final String s) {
        return "".equals(s) ? null : s;
    }

    private static Integer emptyToNullInteger(final String s) {
        return "".equals(s) ? null : Integer.parseInt(s);
    }

    private static Integer numerator(final String s) {
        if ("".equals(s)) {
            return null;
        }
        String[] tokens = s.split("/");
        return emptyToNullInteger(tokens[0]);
    }

    private static Integer denominator(final String s) {
        if ("".equals(s)) {
            return null;
        }
        String[] tokens = s.split("/");
        return (tokens.length < 2) ? null : emptyToNullInteger(tokens[1]);
    }
}
