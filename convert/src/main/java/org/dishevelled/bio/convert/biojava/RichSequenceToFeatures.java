/*

    dsh-convert  Convert between various data models.
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
package org.dishevelled.bio.convert.biojava;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Dbxref;
import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.OntologyTerm;
import org.bdgenomics.formats.avro.Strand;

import org.biojava.bio.seq.StrandedFeature;

import org.biojava.bio.symbol.Location;

import org.biojava.ontology.OntoTools;

import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;

import org.slf4j.Logger;

/**
 * Convert Biojava 1.x RichSequence to a list of bdg-formats Features.
 *
 * @author  Michael Heuer
 */
@Immutable
final class RichSequenceToFeatures extends AbstractConverter<RichSequence, List<Feature>> {

    /** Convert String to Dbxref. */
    private final Converter<String, Dbxref> dbxrefConverter;

    /** Convert String to OntologyTerm. */
    private final Converter<String, OntologyTerm> ontologyTermConverter;

    /** Convert String to Strand. */
    private final Converter<String, Strand> strandConverter;


    /**
     * Convert Biojava 1.x RichSequence to bdg-formats Sequence.
     *
     * @param dbxrefConverter convert String to Dbxref, must not be null
     * @param ontologyTermConverter convert String to OntologyTerm, must not be null
     * @param strandConverter convert String to Strand, must not be null
     */
    RichSequenceToFeatures(final Converter<String, Dbxref> dbxrefConverter,
                           final Converter<String, OntologyTerm> ontologyTermConverter,
                           final Converter<String, Strand> strandConverter) {
        super(RichSequence.class, List.class);

        checkNotNull(dbxrefConverter);
        checkNotNull(ontologyTermConverter);
        checkNotNull(strandConverter);

        this.dbxrefConverter = dbxrefConverter;
        this.ontologyTermConverter = ontologyTermConverter;
        this.strandConverter = strandConverter;
    }


    @Override
    public List<Feature> convert(final RichSequence richSequence,
                                 final ConversionStringency stringency,
                                 final Logger logger) throws ConversionException {

        if (richSequence == null) {
            warnOrThrow(richSequence, "must not be null", null, stringency, logger);
            return null;
        }

        final Feature.Builder fb = Feature.newBuilder()
            .setContigName(richSequence.getName());

        int size = richSequence.getFeatureSet().size();
        List<Feature> features = new ArrayList<Feature>(size);
        for (org.biojava.bio.seq.Feature feature : richSequence.getFeatureSet()) {

            if (feature.getSourceTerm() != null && feature.getSourceTerm() != OntoTools.ANY) {
                fb.setSource(feature.getSourceTerm().getName());
            }
            else {
                fb.setSource(feature.getSource());
            }

            if (feature.getTypeTerm() != null && feature.getTypeTerm() != OntoTools.ANY) {
                fb.setFeatureType(feature.getTypeTerm().getName());
            }
            else {
                fb.setFeatureType(feature.getType());
            }

            Location location = feature.getLocation();
            if (location != null) {
                if (!location.isContiguous()) {
                    warnOrThrow(richSequence, "feature location is not contiguous", null, stringency, logger);;
                }
                fb.setStart(Long.valueOf(location.getMin() - 1));
                fb.setEnd(Long.valueOf(location.getMax()));
            }

            if (feature instanceof StrandedFeature) {
                StrandedFeature strandedFeature = (StrandedFeature) feature;

                if (strandedFeature.getStrand() != null) {
                    fb.setStrand(strandConverter.convert(String.valueOf(strandedFeature.getStrand().getToken()), stringency, logger));
                }
            }

            if (feature instanceof RichFeature) {
                RichFeature richFeature = (RichFeature) feature;

                if (richFeature.getName() != null) {
                    fb.setName(richFeature.getName());
                }
            }

            // todo: dbxref, ontology term, attributes

            features.add(fb.build());
        }
        return features;
    }
}
