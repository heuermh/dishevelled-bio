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
package org.dishevelled.bio.convert.dishevelled;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Guice;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.Variant;

import org.dishevelled.bio.feature.BedRecord;
import org.dishevelled.bio.feature.Gff3Record;

import org.dishevelled.bio.variant.vcf.VcfRecord;

/**
 * Unit test for DishevelledModule.
 *
 * @author  Michael Heuer
 */
public final class DishevelledModuleTest {
    private DishevelledModule module;

    @Before
    public void setUp() {
        module = new DishevelledModule();
    }

    @Test
    public void testConstructor() {
        assertNotNull(module);
    }

    @Test
    public void testDishevelledModule() {
        Injector injector = Guice.createInjector(module, new BdgenomicsModule(), new TestModule());
        Target target = injector.getInstance(Target.class);
        assertNotNull(target.getBedRecordToFeature());
        assertNotNull(target.getFeatureToBedRecord());
        assertNotNull(target.getGff3RecordToFeature());
        assertNotNull(target.getFeatureToGff3Record());
        assertNotNull(target.getVcfRecordToVariants());
        assertNotNull(target.getVcfRecordToGenotypes());
        assertNotNull(target.getGenotypesToVcfRecord());
        assertNotNull(target.getVariantToVcfRecord());
    }

    /**
     * Injection target.
     */
    static class Target {
        Converter<BedRecord, Feature> bedRecordToFeature;
        Converter<Feature, BedRecord> featureToBedRecord;
        Converter<Gff3Record, Feature> gff3RecordToFeature;
        Converter<Feature, Gff3Record> featureToGff3Record;
        Converter<VcfRecord, List<Variant>> vcfRecordToVariants;
        Converter<VcfRecord, List<Genotype>> vcfRecordToGenotypes;
        Converter<List<Genotype>, VcfRecord> genotypesToVcfRecord;
        Converter<Variant, VcfRecord> variantToVcfRecord;

        @Inject
        Target(Converter<BedRecord, Feature> bedRecordToFeature,
               Converter<Feature, BedRecord> featureToBedRecord,
               Converter<Gff3Record, Feature> gff3RecordToFeature,
               Converter<Feature, Gff3Record> featureToGff3Record,
               Converter<VcfRecord, List<Variant>> vcfRecordToVariants,
               Converter<VcfRecord, List<Genotype>> vcfRecordToGenotypes,
               Converter<List<Genotype>, VcfRecord> genotypesToVcfRecord,
               Converter<Variant, VcfRecord> variantToVcfRecord) {
            this.bedRecordToFeature = bedRecordToFeature;
            this.featureToBedRecord = featureToBedRecord;
            this.gff3RecordToFeature = gff3RecordToFeature;
            this.featureToGff3Record = featureToGff3Record;
            this.vcfRecordToVariants = vcfRecordToVariants;
            this.vcfRecordToGenotypes = vcfRecordToGenotypes;
            this.genotypesToVcfRecord = genotypesToVcfRecord;
            this.variantToVcfRecord = variantToVcfRecord;
        }

        Converter<BedRecord, Feature> getBedRecordToFeature() {
            return bedRecordToFeature;
        }

        Converter<Feature, BedRecord> getFeatureToBedRecord() {
            return featureToBedRecord;
        }

        Converter<Gff3Record, Feature> getGff3RecordToFeature() {
            return gff3RecordToFeature;
        }

        Converter<Feature, Gff3Record> getFeatureToGff3Record() {
            return featureToGff3Record;
        }

        Converter<VcfRecord, List<Variant>> getVcfRecordToVariants() {
            return vcfRecordToVariants;
        }

        Converter<VcfRecord, List<Genotype>> getVcfRecordToGenotypes() {
            return vcfRecordToGenotypes;
        }

        Converter<List<Genotype>, VcfRecord> getGenotypesToVcfRecord() {
            return genotypesToVcfRecord;
        }

        Converter<Variant, VcfRecord> getVariantToVcfRecord() {
            return variantToVcfRecord;
        }
    }

    /**
     * Test module.
     */
    class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Target.class);
        }
    }
}
