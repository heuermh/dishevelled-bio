/*

    dsh-bio-adam  Adapt dsh-bio models to ADAM.
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
package org.dishevelled.bio.adam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;

import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.apache.spark.SparkContext;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.bdgenomics.adam.rdd.ADAMContext;

import org.bdgenomics.adam.rdd.feature.FeatureRDD;

import org.bdgenomics.adam.rdd.variant.GenotypeRDD;
import org.bdgenomics.adam.rdd.variant.VariantRDD;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.Variant;

import org.dishevelled.bio.convert.dishevelled.DishevelledModule;

import org.dishevelled.bio.feature.BedReader;
import org.dishevelled.bio.feature.BedRecord;
import org.dishevelled.bio.feature.Gff3Reader;
import org.dishevelled.bio.feature.Gff3Record;

import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends ADAMContext with load methods for dsh-bio models.
 */
public class DishevelledAdamContext extends ADAMContext {
    /** Java Spark context. */
    private final transient JavaSparkContext javaSparkContext;

    /** Convert dishevelled BedRecord to bdg-formats Feature. */
    private final Converter<BedRecord, Feature> bedFeatureConverter;

    /** Convert dishevelled Gff3Record to bdg-formats Feature. */
    private final Converter<Gff3Record, Feature> gff3FeatureConverter;

    /** Convert dishevelled VcfRecord to a list of bdg-formats Variants. */
    private final Converter<VcfRecord, List<Variant>> variantConverter;

    /** Convert dishevelled VcfRecord to a list of bdg-formats Genotypes. */
    private final Converter<VcfRecord, List<Genotype>> genotypeConverter;

    /** Logger. */ // not sure why ADAMContext.log is not accessible
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Create a new DishevelledAdamContext with the specified Spark context.
     *
     * @param sc Spark context, must not be null
     */
    public DishevelledAdamContext(final SparkContext sc) {        
        super(sc);

        javaSparkContext = new JavaSparkContext(sc);

        Injector injector = Guice.createInjector(new DishevelledModule(), new BdgenomicsModule());
        bedFeatureConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<BedRecord, Feature>>() {}));
        gff3FeatureConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<Gff3Record, Feature>>() {}));
        variantConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfRecord, List<Variant>>>() {}));
        genotypeConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfRecord, List<Genotype>>>() {}));
    }


    /**
     * Load the specified path in BED format as features.
     *
     * @param path path in BED format
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD dshLoadBed(final String path) throws IOException {
        logger.info("Loading " + path + " as BED format...");
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            JavaRDD<BedRecord> bedRecords = javaSparkContext.parallelize((List<BedRecord>) BedReader.read(reader));
            JavaRDD<Feature> features = bedRecords.map(record -> bedFeatureConverter.convert(record, ConversionStringency.STRICT, logger));
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in GFF3 format as features.
     *
     * @param path path in GFF3 format
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD dshLoadGff3(final String path) throws IOException {
        logger.info("Loading " + path + " as GFF3 format...");
        // will this work via HDFS?
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            JavaRDD<Gff3Record> gff3Records = javaSparkContext.parallelize((List<Gff3Record>) Gff3Reader.read(reader));
            JavaRDD<Feature> features = gff3Records.map(record -> gff3FeatureConverter.convert(record, ConversionStringency.STRICT, logger));
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in VCF/BCF format as variants.
     *
     * @param path path in VCF/BCF format
     * @throws IOException if an I/O error occurs
     */
    public VariantRDD dshLoadVariants(final String path) throws IOException {
        logger.info("Loading " + path + " in VCF/BCF format as variants...");
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            JavaRDD<VcfRecord> vcfRecords = javaSparkContext.parallelize((List<VcfRecord>) VcfReader.records(reader));
            JavaRDD<Variant> variants = vcfRecords.flatMap(record -> variantConverter.convert(record, ConversionStringency.STRICT, logger).iterator());
            return new VariantRDD(variants.rdd(), null, null);
        }
    }

    /**
     * Load the specified path in VCF/BCF format as genotypes.
     *
     * @param path path in VCF/BCF format
     * @throws IOException if an I/O error occurs
     */
    public GenotypeRDD dshLoadGenotypes(final String path) throws IOException {
        logger.info("Loading " + path + " in VCF/BCF format as genotypes...");
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            JavaRDD<VcfRecord> vcfRecords = javaSparkContext.parallelize((List<VcfRecord>) VcfReader.records(reader));
            JavaRDD<Genotype> genotypes = vcfRecords.flatMap(record -> genotypeConverter.convert(record, ConversionStringency.STRICT, logger).iterator());
            return new GenotypeRDD(genotypes.rdd(), null, null, null);
        }
    }
}
