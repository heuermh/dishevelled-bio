/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
    ##INFO=<ID=ANN,Number=.,Type=String,Description="Functional annotations: 'Allele | Annotation | Annotation_Impact | Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank / Total | HGVS.c | HGVS.p | cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | ERRORS / WARNINGS / INFO' ">
     */
    private final String alternateAllele;
    private final List<String> effects;
    private final String annotationImpact;
    private final String geneName;
    private final String geneId;
    private final String featureType;
    private final String featureId;
    private final String biotype;
    private final Integer rank;
    private final Integer total;
    private final String transcriptHgvs;
    private final String proteinHgvs;
    private final Integer cdnaPosition;
    private final Integer cdnaLength;
    private final Integer cdsPosition;
    private final Integer cdsLength;
    private final Integer proteinPosition;
    private final Integer proteinLength;
    private final Integer distance;
    private final List<String> messages;
    private final int hashCode;


    /**
     * Create a new SnpEff variant annotation.
     *
     * @param alternateAllele alternate allele, if any
     * @param effects list of effects, must not be null
     * @param annotationImpact annotation impact, if any
     * @param geneName gene name, if any
     * @param geneId gene id, if any
     * @param featureType feature type, if any
     * @param featureId feature id, if any
     * @param biotype transcript biotype, if any
     * @param rank intron or exon rank, if any
     * @param total total number of introns or exons, if any
     * @param transcriptHgvs HGVS.c annotation, if any
     * @param proteinHgvs HGVS.p annotation, if any
     * @param cdnaPosition cDNA position, if any
     * @param cdnaLength cDNA length, if any
     * @param cdsPosition CDS position, if any
     * @param cdsLength CDS length, if any
     * @param proteinPosition protein position, if any
     * @param proteinLength protein length, if any
     * @param distance distance, if any
     * @param messages list of messages, must not be null
     */
    private SnpEffVariantAnnotation(final String alternateAllele, final List<String> effects, final String annotationImpact,
                                    final String geneName, final String geneId, final String featureType, final String featureId,
                                    final String biotype, final Integer rank, final Integer total, final String transcriptHgvs,
                                    final String proteinHgvs, final Integer cdnaPosition, final Integer cdnaLength,
                                    final Integer cdsPosition, final Integer cdsLength, final Integer proteinPosition,
                                    final Integer proteinLength, final Integer distance, final List<String> messages) {

        checkNotNull(effects);
        checkNotNull(messages);

        this.alternateAllele = alternateAllele;
        this.effects = ImmutableList.copyOf(effects);
        this.annotationImpact = annotationImpact;
        this.geneName = geneName;
        this.geneId = geneId;
        this.featureType = featureType;
        this.featureId = featureId;
        this.biotype = biotype;
        this.rank = rank;
        this.total = total;
        this.transcriptHgvs = transcriptHgvs;
        this.proteinHgvs = proteinHgvs;
        this.cdnaPosition = cdnaPosition;
        this.cdnaLength = cdnaLength;
        this.cdsPosition = cdsPosition;
        this.cdsLength = cdsLength;
        this.proteinPosition = proteinPosition;
        this.proteinLength = proteinLength;
        this.distance = distance;
        this.messages = ImmutableList.copyOf(messages);

        hashCode = Objects.hash(this.alternateAllele, this.effects, this.annotationImpact, this.geneName, this.geneId,
                                this.featureType, this.featureId, this.biotype, this.rank, this.total, this.transcriptHgvs,
                                this.proteinHgvs, this.cdnaPosition, this.cdnaLength, this.cdsPosition, this.cdsLength,
                                this.proteinPosition, this.proteinLength, this.distance, this.messages);
    }


    /**
     * Return the alternate allele for this SnpEff variant annotation, if any.
     *
     * @return the alternate allele for this SnpEff variant annotation, if any
     */
    public String getAlternateAllele() {
        return alternateAllele;
    }

    /**
     * Return the list of effects for this SnpEff variant annotation.
     *
     * @return the list of effects for this SnpEff variant annotation
     */
    public List<String> getEffects() {
        return effects;
    }

    /**
     * Return the annotation impact for this SnpEff variant annotation, if any.
     *
     * @return the annotation impact for this SnpEff variant annotation, if any
     */
    public String getAnnotationImpact() {
        return annotationImpact;
    }

    /**
     * Return the gene name for this SnpEff variant annotation, if any.
     *
     * @return the gene name for this SnpEff variant annotation, if any
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * Return the gene id for this SnpEff variant annotation, if any.
     *
     * @return the gene id for this SnpEff variant annotation, if any
     */
    public String getGeneId() {
        return geneId;
    }

    /**
     * Return the feature type for this SnpEff variant annotation, if any.
     *
     * @return the feature type for this SnpEff variant annotation, if any
     */
    public String getFeatureType() {
        return featureType;
    }

    /**
     * Return the feature id for this SnpEff variant annotation, if any.
     *
     * @return the feature id for this SnpEff variant annotation, if any
     */
    public String getFeatureId() {
        return featureId;
    }

    /**
     * Return the transcript biotype for this SnpEff variant annotation, if any.
     *
     * @return the transcript biotype for this SnpEff variant annotation, if any
     */
    public String getBiotype() {
        return biotype;
    }

    /**
     * Return the intron or exon rank for this SnpEff variant annotation, if any.
     *
     * @return the intron or exon rank for this SnpEff variant annotation, if any
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * Return the total number of introns or exons for this SnpEff variant annotation, if any.
     *
     * @return the total number of introns or exons for this SnpEff variant annotation, if any
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * Return the HGVS.c annotation for this SnpEff variant annotation, if any.
     *
     * @return the HGVS.c annotation for this SnpEff variant annotation, if any
     */
    public String getTranscriptHgvs() {
        return transcriptHgvs;
    }

    /**
     * Return the HGVS.p annotation for this SnpEff variant annotation, if any.
     *
     * @return the HGVS.p annotation for this SnpEff variant annotation, if any
     */
    public String getProteinHgvs() {
        return proteinHgvs;
    }

    /**
     * Return the cDNA position for this SnpEff variant annotation, if any.
     *
     * @return the cDNA position for this SnpEff variant annotation, if any
     */
    public Integer getCdnaPosition() {
        return cdnaPosition;
    }

    /**
     * Return the cDNA length for this SnpEff variant annotation, if any.
     *
     * @return the cDNA length for this SnpEff variant annotation, if any
     */
    public Integer getCdnaLength() {
        return cdnaLength;
    }

    /**
     * Return the CDS position for this SnpEff variant annotation, if any.
     *
     * @return the CDS position for this SnpEff variant annotation, if any
     */
    public Integer getCdsPosition() {
        return cdsPosition;
    }

    /**
     * Return the CDS length for this SnpEff variant annotation, if any.
     *
     * @return the CDS length for this SnpEff variant annotation, if any
     */
    public Integer getCdsLength() {
        return cdsLength;
    }

    /**
     * Return the amino acid position for this SnpEff variant annotation, if any.
     *
     * @return the amino acid position for this SnpEff variant annotation, if any
     */
    public Integer getProteinPosition() {
        return proteinPosition;
    }

    /**
     * Return the amino acid length for this SnpEff variant annotation, if any.
     *
     * @return the amino acid length for this SnpEff variant annotation, if any
     */
    public Integer getProteinLength() {
        return proteinLength;
    }

    /**
     * Return the distance for this SnpEff variant annotation, if any.
     *
     * @return the distance for this SnpEff variant annotation, if any
     */
    public Integer getDistance() {
        return distance;
    }

    /**
     * Return the list of messages for this SnpEff variant annotation.
     *
     * @return the list of messages for this SnpEff variant annotation
     */
    public List<String> getMessages() {
        return messages;
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

        return Objects.equals(alternateAllele, ann.alternateAllele)
            && Objects.equals(effects, ann.effects)
            && Objects.equals(annotationImpact, ann.annotationImpact)
            && Objects.equals(geneName, ann.geneName)
            && Objects.equals(geneId, ann.geneId)
            && Objects.equals(featureType, ann.featureType)
            && Objects.equals(featureId, ann.featureId)
            && Objects.equals(biotype, ann.biotype)
            && Objects.equals(rank, ann.rank)
            && Objects.equals(total, ann.total)
            && Objects.equals(transcriptHgvs, ann.transcriptHgvs)
            && Objects.equals(proteinHgvs, ann.proteinHgvs)
            && Objects.equals(cdnaPosition, ann.cdnaPosition)
            && Objects.equals(cdnaLength, ann.cdnaLength)
            && Objects.equals(cdsPosition, ann.cdsPosition)
            && Objects.equals(cdsLength, ann.cdsLength)
            && Objects.equals(proteinPosition, ann.proteinPosition)
            && Objects.equals(proteinLength, ann.proteinLength)
            && Objects.equals(distance, ann.distance)
            && Objects.equals(messages, ann.messages);
    }

    @Override
    public String toString() {
        String effects = Joiner.on("&").join(this.effects);
        String messages = Joiner.on("&").join(this.messages);
        return Joiner.on("|").join(nullToEmpty(alternateAllele), effects, nullToEmpty(annotationImpact),
                                   nullToEmpty(geneName), nullToEmpty(geneId), nullToEmpty(featureType),
                                   nullToEmpty(featureId), nullToEmpty(biotype), nullToEmpty(rank, total),
                                   nullToEmpty(transcriptHgvs), nullToEmpty(proteinHgvs), nullToEmpty(cdnaPosition, cdnaLength),
                                   nullToEmpty(cdsPosition, cdsLength), nullToEmpty(proteinPosition, proteinLength),
                                   nullToEmpty(distance), messages);
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
            throw new IllegalArgumentException("value must have sixteen fields ( Allele | Annotation | Annotation_Impact | "
                + "Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank / Total | HGVS.c | HGVS.p | "
                + "cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | MESSAGES / WARNINGS / INFO)");
        }

        String alternateAllele = emptyToNull(tokens.get(0));
        List<String> effects = Splitter.on("&").omitEmptyStrings().splitToList(tokens.get(1));
        String annotationImpact = emptyToNull(tokens.get(2));
        String geneName = emptyToNull(tokens.get(3));
        String geneId = emptyToNull(tokens.get(4));
        String featureType = emptyToNull(tokens.get(5));
        String featureId = emptyToNull(tokens.get(6));
        String biotype = emptyToNull(tokens.get(7));
        Integer rank = numerator(tokens.get(8));
        Integer total = denominator(tokens.get(8));
        String transcriptHgvs = emptyToNull(tokens.get(9));
        String proteinHgvs = emptyToNull(tokens.get(10));
        Integer cdnaPosition = numerator(tokens.get(11));
        Integer cdnaLength = denominator(tokens.get(11));
        Integer cdsPosition = numerator(tokens.get(12));
        Integer cdsLength = denominator(tokens.get(12));
        Integer proteinPosition = numerator(tokens.get(13));
        Integer proteinLength = denominator(tokens.get(13));
        Integer distance = emptyToNullInteger(tokens.get(14));
        List<String> messages = Splitter.on("&").omitEmptyStrings().splitToList(tokens.get(15));

        return new SnpEffVariantAnnotation(alternateAllele, effects, annotationImpact, geneName, geneId,
                                           featureType, featureId, biotype, rank, total, transcriptHgvs, proteinHgvs,
                                           cdnaPosition, cdnaLength, cdsPosition, cdsLength, proteinPosition,
                                           proteinLength, distance, messages);
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
