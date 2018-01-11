/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Dbxref;
import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.OntologyTerm;
import org.bdgenomics.formats.avro.Strand;

import org.dishevelled.bio.feature.Gff3Record;

import org.slf4j.Logger;

/**
 * Convert dishevelled Gff3Record to bdg-formats Feature.
 *
 * @author  Michael Heuer
 */
@Immutable
final class Gff3RecordToFeature extends AbstractConverter<Gff3Record, Feature> {

    /** Convert String to Dbxref. */
    private final Converter<String, Dbxref> dbxrefConverter;

    /** Convert String to OntologyTerm. */
    private final Converter<String, OntologyTerm> ontologyTermConverter;

    /** Convert String to Strand. */
    private final Converter<String, Strand> strandConverter;

    /** List of GFF3 reserved attribute keys. */
    private static final List<String> RESERVED_KEYS = ImmutableList.of("ID", "Name", "gene_id", "transcript_id", "exon_id", "Target", "Gap", "Derives_from", "Is_circular", "Alias", "Parent", "Note", "Dbxref", "Ontology_term");


    /**
     * Convert dishevelled Gff3Record to bdg-formats Feature.
     *
     * @param dbxrefConverter convert String to Dbxref, must not be null
     * @param ontologyTermConverter convert String to OntologyTerm, must not be null
     * @param strandConverter convert String to Strand, must not be null
     */
    Gff3RecordToFeature(final Converter<String, Dbxref> dbxrefConverter,
                        final Converter<String, OntologyTerm> ontologyTermConverter,
                        final Converter<String, Strand> strandConverter) {
        super(Gff3Record.class, Feature.class);

        checkNotNull(dbxrefConverter);
        checkNotNull(ontologyTermConverter);
        checkNotNull(strandConverter);

        this.dbxrefConverter = dbxrefConverter;
        this.ontologyTermConverter = ontologyTermConverter;
        this.strandConverter = strandConverter;
    }


    @Override
    public Feature convert(final Gff3Record gff3Record,
                           final ConversionStringency stringency,
                           final Logger logger) throws ConversionException {

        if (gff3Record == null) {
            warnOrThrow(gff3Record, "must not be null", null, stringency, logger);
            return null;
        }
        final Feature.Builder fb = Feature.newBuilder()
            .setContigName(gff3Record.getSeqid())
            .setSource(gff3Record.getSource())
            .setFeatureType(gff3Record.getFeatureType())
            .setStart(gff3Record.getStart())
            .setEnd(gff3Record.getEnd())
            .setScore(gff3Record.getScore())
            .setStrand(strandConverter.convert(gff3Record.getStrand(), stringency, logger))
            .setPhase(gff3Record.getPhase());

        // 1..1 attributes
        gff3Record.getAttributes().get("ID").forEach(featureId -> fb.setFeatureId(featureId));
        gff3Record.getAttributes().get("Name").forEach(name -> fb.setName(name));
        gff3Record.getAttributes().get("gene_id").forEach(geneId -> fb.setGeneId(geneId));
        gff3Record.getAttributes().get("transcript_id").forEach(transcriptId -> fb.setTranscriptId(transcriptId));
        gff3Record.getAttributes().get("exon_id").forEach(exonId -> fb.setExonId(exonId));
        gff3Record.getAttributes().get("Target").forEach(target -> fb.setTarget(target));
        gff3Record.getAttributes().get("Gap").forEach(gap -> fb.setGap(gap));
        gff3Record.getAttributes().get("Derives_from").forEach(derivesFrom -> fb.setDerivesFrom(derivesFrom));
        gff3Record.getAttributes().get("Is_circular").forEach(circular -> fb.setCircular(Boolean.valueOf(circular)));

        // 1..* attributes
        List<String> aliases = gff3Record.getAttributes().get("Alias");
        if (!aliases.isEmpty()) {
            fb.setAliases(aliases);
        }

        List<String> parentIds = gff3Record.getAttributes().get("Parent");
        if (!parentIds.isEmpty()) {
            fb.setParentIds(parentIds);
        }

        List<String> notes = gff3Record.getAttributes().get("Note");
        if (!notes.isEmpty()) {
            fb.setNotes(notes);
        }

        List<String> dbxrefs = gff3Record.getAttributes().get("Dbxref");
        if (!dbxrefs.isEmpty()) {
            fb.setDbxrefs(dbxrefs.stream().map(dbxref -> dbxrefConverter.convert(dbxref, stringency, logger)).collect(toList()));
        }

        List<String> ontologyTerms = gff3Record.getAttributes().get("Ontology_term");
        if (!ontologyTerms.isEmpty()) {
            fb.setOntologyTerms(ontologyTerms.stream().map(ontologyTerm -> ontologyTermConverter.convert(ontologyTerm, stringency, logger)).collect(toList()));
        }

        // remaining attributes
        Map<String, String> remaining = new HashMap<String, String>();
        for (String key : gff3Record.getAttributes().keySet()) {
            if (!isReservedKey(key)) {
                List<String> values = gff3Record.getAttributes().get(key);
                if (values.size() > 1 && !stringency.isSilent()) {
                    logger.warn("duplicate key {} found in attributes for GFF3 record, will lose all but last value", key);
                }
                remaining.put(key, values.get(values.size() - 1));
            }
        }
        fb.setAttributes(remaining);

        return fb.build();
    }

    /**
     * Return true if the specified key is a GFF3 reserved key.
     *
     * @param key key
     * @return true if the specified key is a GFF3 reserved key 
     */
    static boolean isReservedKey(final String key) {
        return RESERVED_KEYS.contains(key);
    }
}
