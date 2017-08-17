/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
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
package org.dishevelled.bio.convert;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Dbxref;
import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.OntologyTerm;
import org.bdgenomics.formats.avro.Strand;
import org.bdgenomics.formats.avro.TranscriptEffect;
import org.bdgenomics.formats.avro.Variant;

import org.dishevelled.bio.feature.BedRecord;
import org.dishevelled.bio.feature.Gff3Record;

import org.dishevelled.bio.variant.vcf.VcfRecord;

/**
 * Guice module for the org.dishevelled.bio.convert package.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class DishevelledModule extends AbstractModule {
    @Override
    protected void configure() {
        // empty
    }

    @Provides @Singleton
    Converter<BedRecord, Feature> createBedRecordToFeature(final Converter<String, Strand> strandConverter) {
        return new BedRecordToFeature(strandConverter);
    }

    @Provides @Singleton
    Converter<Feature, BedRecord> createFeatureToBedRecord(final Converter<Strand, String> strandConverter) {
        return new FeatureToBedRecord(strandConverter);
    }

    @Provides @Singleton
    Converter<Gff3Record, Feature> createGff3RecordToFeature(final Converter<String, Dbxref> dbxrefConverter,
                                                             final Converter<String, OntologyTerm> ontologyTermConverter,
                                                             final Converter<String, Strand> strandConverter) {
        return new Gff3RecordToFeature(dbxrefConverter, ontologyTermConverter, strandConverter);
    }

    @Provides @Singleton
    Converter<Feature, Gff3Record> createFeatureToGff3Record(final Converter<Dbxref, String> dbxrefConverter,
                                                             final Converter<OntologyTerm, String> ontologyTermConverter,
                                                             final Converter<Strand, String> strandConverter) {
        return new FeatureToGff3Record(dbxrefConverter, ontologyTermConverter, strandConverter);
    }

    @Provides @Singleton
    Converter<VcfRecord, List<Variant>> createVcfRecordToVariants(final Converter<String, TranscriptEffect> transcriptEffectConverter) {
        return new VcfRecordToVariants(transcriptEffectConverter);
    }

    @Provides @Singleton
    Converter<VcfRecord, List<Genotype>> createVcfRecordToGenotypes(final Converter<VcfRecord, List<Variant>> variantConverter) {
        return new VcfRecordToGenotypes(variantConverter);
    }

    @Provides @Singleton
    Converter<List<Genotype>, VcfRecord> createGenotypesToVcfRecord(final Converter<TranscriptEffect, String> transcriptEffectConverter) {
        return new GenotypesToVcfRecord(transcriptEffectConverter);
    }

    @Provides @Singleton
    Converter<Variant, VcfRecord> createVariantToVcfRecord(final Converter<TranscriptEffect, String> transcriptEffectConverter) {
        return new VariantToVcfRecord(transcriptEffectConverter);
    }
}
