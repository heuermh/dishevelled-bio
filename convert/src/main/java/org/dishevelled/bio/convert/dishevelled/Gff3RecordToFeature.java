/*

    dsh-convert  Convert between various data models.
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
package org.dishevelled.bio.convert.dishevelled;

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
public final class Gff3RecordToFeature extends AbstractConverter<Gff3Record, Feature> {

    /** Convert String to Dbxref. */
    private final Converter<String, Dbxref> dbxrefConverter;

    /** Convert String to OntologyTerm. */
    private final Converter<String, OntologyTerm> ontologyTermConverter;

    /** Convert String to Strand. */
    private final Converter<String, Strand> strandConverter;


    /**
     * Package private no-arg constructor.
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
            .setContigName(gff3Record.seqid())
            .setSource(gff3Record.source())
            .setFeatureType(gff3Record.featureType())
            .setStart(gff3Record.start())
            .setEnd(gff3Record.end())
            .setScore(gff3Record.score())
            .setStrand(strandConverter.convert(gff3Record.strand(), stringency, logger))
            .setPhase(gff3Record.phase());

        // 1..1 attributes, last one in wins
        gff3Record.attributes().get("Name").forEach(name -> fb.setName(name));
        gff3Record.attributes().get("gene_id").forEach(geneId -> fb.setGeneId(geneId));
        gff3Record.attributes().get("transcript_id").forEach(transcriptId -> fb.setTranscriptId(transcriptId));
        gff3Record.attributes().get("exon_id").forEach(exonId -> fb.setExonId(exonId));
        gff3Record.attributes().get("Target").forEach(target -> fb.setTarget(target));
        gff3Record.attributes().get("Gap").forEach(gap -> fb.setGap(gap));
        gff3Record.attributes().get("Derives_from").forEach(derivesFrom -> fb.setDerivesFrom(derivesFrom));
        gff3Record.attributes().get("Is_circular").forEach(circular -> fb.setCircular(Boolean.valueOf(circular)));

        // 1..* attributes
        List<String> aliases = gff3Record.attributes().get("Alias");
        if (!aliases.isEmpty()) {
            fb.setAliases(aliases);
        }

        List<String> parentIds = gff3Record.attributes().get("Parent");
        if (!parentIds.isEmpty()) {
            fb.setParentIds(parentIds);
        }

        List<String> notes = gff3Record.attributes().get("Note");
        if (!notes.isEmpty()) {
            fb.setNotes(notes);
        }

        List<String> dbxrefs = gff3Record.attributes().get("Dbxref");
        if (!dbxrefs.isEmpty()) {
            fb.setDbxrefs(dbxrefs.stream().map(dbxref -> dbxrefConverter.convert(dbxref, stringency, logger)).collect(toList()));
        }

        List<String> ontologyTerms = gff3Record.attributes().get("Ontology_term");
        if (!ontologyTerms.isEmpty()) {
            fb.setOntologyTerms(ontologyTerms.stream().map(ontologyTerm -> ontologyTermConverter.convert(ontologyTerm, stringency, logger)).collect(toList()));
        }

        // remaining attributes
        Map<String, String> remaining = new HashMap<String, String>();
        for (String key : gff3Record.attributes().keySet()) {
            if (!isReservedKey(key)) {
                List<String> values = gff3Record.attributes().get(key);
                if (values.size() > 1 && !stringency.isSilent()) {
                    logger.warn("duplicate key {} found in attributes for GFF3 record, will lose all but last value", key);
                }
                remaining.put(key, values.get(values.size() - 1));
            }
        }
        fb.setAttributes(remaining);

        return fb.build();
    }

    static boolean isReservedKey(final String key) {
        return RESERVED_KEYS.contains(key);
    }

    /** List of GFF3 reserved attribute keys. */
    private static final List<String> RESERVED_KEYS = ImmutableList.of("Name", "gene_id", "transcript_id", "exon_id", "Target", "Gap", "Derives_from", "Is_circular", "Alias", "Parent", "Note", "Dbxref", "Ontology_term");
}
