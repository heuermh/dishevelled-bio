/*

    dsh-bio-adam  Adapt dsh-bio models to ADAM.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLine;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.spark.SparkContext;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.bdgenomics.adam.converters.DefaultHeaderLines;

import org.bdgenomics.adam.models.SequenceDictionary;
import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.adam.ds.ADAMContext;

import org.bdgenomics.adam.ds.feature.FeatureDataset;

import org.bdgenomics.adam.ds.variant.GenotypeDataset;
import org.bdgenomics.adam.ds.variant.VariantDataset;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.Sample;
import org.bdgenomics.formats.avro.Variant;

import org.dishevelled.bio.convert.DishevelledModule;

import org.dishevelled.bio.convert.htsjdk.HtsjdkModule;

import org.dishevelled.bio.feature.bed.BedReader;
import org.dishevelled.bio.feature.bed.BedRecord;
import org.dishevelled.bio.feature.gff3.Gff3Reader;
import org.dishevelled.bio.feature.gff3.Gff3Record;

import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfSample;

import org.dishevelled.bio.variant.vcf.header.VcfContigHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfFilterHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfFormatHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfInfoHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLines;

import scala.Option;

import scala.collection.JavaConversions;
import scala.collection.Seq;

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

    /** Convert dishevelled VcfFilterHeaderLine to htsjdk VCFFilterHeaderLine. */
    private final Converter<VcfFilterHeaderLine, VCFFilterHeaderLine> filterHeaderLineConverter;

    /** Convert dishevelled VcfFormatHeaderLine to htsjdk VCFFormatHeaderLine. */
    private final Converter<VcfFormatHeaderLine, VCFFormatHeaderLine> formatHeaderLineConverter;

    /** Convert dishevelled VcfInfoHeaderLine to htsjdk VCFInfoHeaderLine. */
    private final Converter<VcfInfoHeaderLine, VCFInfoHeaderLine> infoHeaderLineConverter;


    /**
     * Create a new DishevelledAdamContext with the specified Spark context.
     *
     * @param sc Spark context, must not be null
     */
    public DishevelledAdamContext(final SparkContext sc) {        
        super(sc);

        javaSparkContext = new JavaSparkContext(sc);

        Injector injector = Guice.createInjector(new DishevelledModule(), new HtsjdkModule(), new BdgenomicsModule());
        bedFeatureConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<BedRecord, Feature>>() {}));
        gff3FeatureConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<Gff3Record, Feature>>() {}));
        variantConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfRecord, List<Variant>>>() {}));
        genotypeConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfRecord, List<Genotype>>>() {}));
        filterHeaderLineConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfFilterHeaderLine, VCFFilterHeaderLine>>() {}));
        formatHeaderLineConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfFormatHeaderLine, VCFFormatHeaderLine>>() {}));
        infoHeaderLineConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<VcfInfoHeaderLine, VCFInfoHeaderLine>>() {}));
    }


    /**
     * Create and return a BufferedReader for the HDFS path represented by the specified file name.
     *
     * @param fileName file name, must not be null
     * @return a BufferedReader for the HDFS path represented by the specified file name
     * @throws IOException if an I/O error occurs
     */
    BufferedReader reader(final String fileName) throws IOException {
        checkNotNull(fileName);
        Path path = new Path(fileName);
        FileSystem fileSystem = path.getFileSystem(javaSparkContext.hadoopConfiguration());
        return new BufferedReader(new InputStreamReader(fileSystem.open(path)));
    }

    /**
     * Load the specified path in BED format as features.
     *
     * @param path path in BED format
     * @return a dataset of features
     * @throws IOException if an I/O error occurs
     */
    public FeatureDataset dshLoadBed(final String path) throws IOException {
        info("Loading " + path + " as BED format...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<BedRecord> bedRecords = javaSparkContext.parallelize((List<BedRecord>) BedReader.read(reader));
            JavaRDD<Feature> features = bedRecords.map(record -> bedFeatureConverter.convert(record, ConversionStringency.STRICT, logger().logger()));
            return FeatureDataset.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in GFF3 format as features.
     *
     * @param path path in GFF3 format
     * @return a dataset of features
     * @throws IOException if an I/O error occurs
     */
    public FeatureDataset dshLoadGff3(final String path) throws IOException {
        info("Loading " + path + " as GFF3 format...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<Gff3Record> gff3Records = javaSparkContext.parallelize((List<Gff3Record>) Gff3Reader.read(reader));
            JavaRDD<Feature> features = gff3Records.map(record -> gff3FeatureConverter.convert(record, ConversionStringency.STRICT, logger().logger()));
            return FeatureDataset.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in VCF/BCF format as variants.
     *
     * @param path path in VCF/BCF format
     * @return a dataset of variants
     * @throws IOException if an I/O error occurs
     */
    public VariantDataset dshLoadVariants(final String path) throws IOException {
        info("Loading " + path + " in VCF/BCF format as variants...");

        // read header
        VcfHeaderLines vcfHeaderLines = null;
        try (BufferedReader reader = reader(path)) {
            VcfHeader vcfHeader = VcfReader.header(reader);
            vcfHeaderLines = VcfHeaderLines.fromHeader(vcfHeader);
        }
        // read records
        try (BufferedReader reader = reader(path)) {
            JavaRDD<VcfRecord> vcfRecords = javaSparkContext.parallelize((List<VcfRecord>) VcfReader.records(reader));
            JavaRDD<Variant> variants = vcfRecords.flatMap(record -> variantConverter.convert(record, ConversionStringency.STRICT, logger().logger()).iterator());
            return VariantDataset.apply(variants.rdd(), createSequenceDictionary(vcfHeaderLines), createHeaderLines(vcfHeaderLines));
        }
    }

    /**
     * Load the specified path in VCF/BCF format as genotypes.
     *
     * @param path path in VCF/BCF format
     * @return a dataset of genotypes
     * @throws IOException if an I/O error occurs
     */
    public GenotypeDataset dshLoadGenotypes(final String path) throws IOException {
        info("Loading " + path + " in VCF/BCF format as genotypes...");

        // read header
        VcfHeaderLines vcfHeaderLines = null;
        try (BufferedReader reader = reader(path)) {
            VcfHeader vcfHeader = VcfReader.header(reader);
            vcfHeaderLines = VcfHeaderLines.fromHeader(vcfHeader);
        }
        // read samples
        List<Sample> samples = new ArrayList<Sample>();
        try (BufferedReader reader = reader(path)) {
            for (VcfSample sample : VcfReader.samples(reader)) {
                samples.add(Sample.newBuilder().setId(sample.getId()).build());
            }
        }
        // read records
        try (BufferedReader reader = reader(path)) {
            JavaRDD<VcfRecord> vcfRecords = javaSparkContext.parallelize((List<VcfRecord>) VcfReader.records(reader));
            JavaRDD<Genotype> genotypes = vcfRecords.flatMap(record -> genotypeConverter.convert(record, ConversionStringency.STRICT, logger().logger()).iterator());
            return GenotypeDataset.apply(genotypes.rdd(), createSequenceDictionary(vcfHeaderLines), JavaConversions.asScalaBuffer(samples), createHeaderLines(vcfHeaderLines));
        }
    }

    /**
     * Convert the specified VcfHeaderLines to a sequence of htsjdk VCF header lines, merged with the
     * default header lines specified by ADAM.
     *
     * @param vcfHeaderLines VCF header lines to convert
     * @return a sequence of htsjdk header lines
     */
    Seq<VCFHeaderLine> createHeaderLines(final VcfHeaderLines vcfHeaderLines) {
        Map<String, VCFInfoHeaderLine> infoHeaderLines = new HashMap<String, VCFInfoHeaderLine>();
        for (VCFInfoHeaderLine infoHeaderLine : JavaConversions.asJavaIterable(DefaultHeaderLines.infoHeaderLines())) {
            infoHeaderLines.put(infoHeaderLine.getID(), infoHeaderLine);
        }
        for (Map.Entry<String, VcfInfoHeaderLine> entry : vcfHeaderLines.getInfoHeaderLines().entrySet()) {
            String id = entry.getKey();
            VcfInfoHeaderLine infoHeaderLine = entry.getValue();
            if (!infoHeaderLines.containsKey(id)) {
                infoHeaderLines.put(id, infoHeaderLineConverter.convert(infoHeaderLine, ConversionStringency.STRICT, logger().logger()));
            }
        }

        Map<String, VCFFormatHeaderLine> formatHeaderLines = new HashMap<String, VCFFormatHeaderLine>();
        for (VCFFormatHeaderLine formatHeaderLine : JavaConversions.asJavaIterable(DefaultHeaderLines.formatHeaderLines())) {
            formatHeaderLines.put(formatHeaderLine.getID(), formatHeaderLine);
        }
        for (Map.Entry<String, VcfFormatHeaderLine> entry : vcfHeaderLines.getFormatHeaderLines().entrySet()) {
            String id = entry.getKey();
            VcfFormatHeaderLine formatHeaderLine = entry.getValue();
            if (!formatHeaderLines.containsKey(id)) {
                formatHeaderLines.put(id, formatHeaderLineConverter.convert(formatHeaderLine, ConversionStringency.STRICT, logger().logger()));
            }
        }

        List<VCFFilterHeaderLine> filterHeaderLines = new ArrayList<VCFFilterHeaderLine>(vcfHeaderLines.getFilterHeaderLines().size());
        for (VcfFilterHeaderLine filterHeaderLine : vcfHeaderLines.getFilterHeaderLines().values()) {
            filterHeaderLines.add(filterHeaderLineConverter.convert(filterHeaderLine, ConversionStringency.STRICT, logger().logger()));
        }

        List<VCFHeaderLine> headerLines = new ArrayList<VCFHeaderLine>(infoHeaderLines.size() + formatHeaderLines.size() + filterHeaderLines.size());
        headerLines.addAll(infoHeaderLines.values());
        headerLines.addAll(formatHeaderLines.values());
        headerLines.addAll(filterHeaderLines);
        return JavaConversions.asScalaBuffer(headerLines);
    }

    /**
     * Log the specified info message.
     *
     * @param message info message to log
     */
    private void info(final String message) {
        // necessary because it doesn't appear possible to implement scala.Function0 from java
        if (logger().isInfoEnabled()) {
            logger().logger().info(message);
        }
    }

    static SequenceDictionary createSequenceDictionary(final VcfHeaderLines vcfHeaderLines) {
        Collection<VcfContigHeaderLine> vcfContigHeaderLines = vcfHeaderLines.getContigHeaderLines().values();
            
        if (vcfContigHeaderLines.isEmpty()) {
            return SequenceDictionary.empty();
        }
        List<SequenceRecord> sequenceRecords = new ArrayList<SequenceRecord>(vcfContigHeaderLines.size());
        for (VcfContigHeaderLine contigHeaderLine : vcfContigHeaderLines) {
            sequenceRecords.add(new SequenceRecord
                                (
                                 contigHeaderLine.getId(),
                                 contigHeaderLine.getLength(),
                                 Option.apply(contigHeaderLine.getUrl()),
                                 Option.apply(contigHeaderLine.getMd5()),
                                 firstOrNone(contigHeaderLine.getAttributes().get("REFSEQ")),
                                 firstOrNone(contigHeaderLine.getAttributes().get("GENBANK")),
                                 firstOrNone(contigHeaderLine.getAttributes().get("assembly")),
                                 firstOrNone(contigHeaderLine.getAttributes().get("species")),
                                 Option.apply(null)
                                 ));
        }
        return new SequenceDictionary(JavaConversions.asScalaBuffer(sequenceRecords).toVector());
    }

    static Option<String> firstOrNone(final List<String> values) {
        return values.isEmpty() ? Option.apply((String) null) : Option.apply(values.get(0));
    }
}
