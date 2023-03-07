/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
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
package org.dishevelled.bio.convert;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Dbxref;
import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.OntologyTerm;
import org.bdgenomics.formats.avro.Strand;

import org.dishevelled.bio.feature.gff3.Gff3Record;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Feature to dishevelled Gff3Record.
 *
 * @author  Michael Heuer
 */
@Immutable
final class FeatureToGff3Record extends AbstractConverter<Feature, Gff3Record> {

    /** Convert Dbxref to String. */
    private final Converter<Dbxref, String> dbxrefConverter;

    /** Convert OntologyTerm to String. */
    private final Converter<OntologyTerm, String> ontologyTermConverter;

    /** Convert Strand to String. */
    private final Converter<Strand, String> strandConverter;


    /**
     * Convert bdg-formats Feature to dishevelled Gff3Record.
     *
     * @param dbxrefConverter convert Dbxref to String, must not be null
     * @param ontologyTermConverter convert OntologyTerm to String, must not be null
     * @param strandConverter convert Strand to String, must not be null
     */
    FeatureToGff3Record(final Converter<Dbxref, String> dbxrefConverter, final Converter<OntologyTerm, String> ontologyTermConverter, final Converter<Strand, String> strandConverter) {
        super(Feature.class, Gff3Record.class);

        checkNotNull(dbxrefConverter);
        checkNotNull(ontologyTermConverter);
        checkNotNull(strandConverter);

        this.dbxrefConverter = dbxrefConverter;
        this.ontologyTermConverter = ontologyTermConverter;
        this.strandConverter = strandConverter;
    }


    @Override
    public Gff3Record convert(final Feature feature,
                              final ConversionStringency stringency,
                              final Logger logger) throws ConversionException {

        if (feature == null) {
            warnOrThrow(feature, "must not be null", null, stringency, logger);
            return null;
        }

        String seqid = feature.getReferenceName();
        String source = feature.getSource();
        String featureType = feature.getFeatureType();
        long start = feature.getStart();
        long end = feature.getEnd();
        Double score = feature.getScore();
        String strand = strandConverter.convert(feature.getStrand(), stringency, logger);
        Integer phase = feature.getPhase();

        ListMultimap<String, String> attributes = ArrayListMultimap.create();

        // 1..1 attributes
        if (feature.getFeatureId() != null) {
            attributes.put("ID", feature.getName());
        }
        if (feature.getName() != null) {
            attributes.put("Name", feature.getName());
        }
        if (feature.getGeneId() != null) {
            attributes.put("gene_id", feature.getGeneId());
        }
        if (feature.getTranscriptId() != null) {
            attributes.put("transcript_id", feature.getTranscriptId());
        }
        if (feature.getExonId() != null) {
            attributes.put("exon_id", feature.getExonId());
        }
        if (feature.getTarget() != null) {
            attributes.put("Target", feature.getTarget());
        }
        if (feature.getGap() != null) {
            attributes.put("Gap", feature.getGap());
        }
        if (feature.getDerivesFrom() != null) {
            attributes.put("Derives_from", feature.getDerivesFrom());
        }
        if (feature.getCircular() != null) {
            attributes.put("Is_circular", String.valueOf(feature.getCircular()));
        }

        // 1..* attributes
        for (String alias : feature.getAliases()) {
            attributes.put("Alias", alias);
        }
        for (String parent : feature.getParentIds()) {
            attributes.put("Parent", parent);
        }
        for (String note : feature.getNotes()) {
            attributes.put("Note", note);
        }
        for (Dbxref dbxref : feature.getDbxrefs()) {
            attributes.put("Dbxref", dbxrefConverter.convert(dbxref, stringency, logger));
        }
        for (OntologyTerm ontologyTerm : feature.getOntologyTerms()) {
            attributes.put("Ontology_term", ontologyTermConverter.convert(ontologyTerm, stringency, logger));
        }

        // remaining attributes
        for (Map.Entry<String, String> entry : feature.getAttributes().entrySet()) {
            attributes.put(entry.getKey(), entry.getValue());
        }

        Gff3Record gff3Record = null;
        try {
            gff3Record = new Gff3Record(seqid, source, featureType, start, end, score, strand, phase, attributes);
        }
        catch (IllegalArgumentException e) {
            warnOrThrow(feature, "caught IllegalArgumentException", e, stringency, logger);
        }
        return gff3Record;
    }
}
