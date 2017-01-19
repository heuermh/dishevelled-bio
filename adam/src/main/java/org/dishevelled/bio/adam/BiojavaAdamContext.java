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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.spark.SparkContext;

import org.apache.spark.rdd.RDD;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.bdgenomics.adam.rdd.ADAMContext;

import org.bdgenomics.adam.rdd.feature.FeatureRDD;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Read;
import org.bdgenomics.formats.avro.Sequence;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.SangerFastqReader;

import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import org.dishevelled.bio.convert.biojava.BiojavaModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends ADAMContext with load methods for Biojava 1.x models.
 */
public class BiojavaAdamContext extends ADAMContext {
    /** Java Spark context. */
    private final transient JavaSparkContext javaSparkContext;

    /** Convert Biojava 1.x Fastq to bdg-formats Read. */
    private final Converter<Fastq, Read> readConverter;

    /** Convert Biojava 1.x Sequence to bdg-formats Sequence. */
    private final Converter<org.biojava.bio.seq.Sequence, Sequence> sequenceConverter;

    /** Convert Biojava 1.x RichSequence to bdg-formats Sequence. */
    private final Converter<RichSequence, Sequence> richSequenceConverter;

    /** Convert Biojava 1.x RichSequence to a list of bdg-formats Features. */
    private final Converter<RichSequence, List<Feature>> featureConverter;

    /** Logger. */ // not sure why ADAMContext.log is not accessible
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Create a new BiojavaAdamContext with the specified Spark context.
     *
     * @param sc Spark context, must not be null
     */
    public BiojavaAdamContext(final SparkContext sc) {        
        super(sc);

        javaSparkContext = new JavaSparkContext(sc);

        Injector injector = Guice.createInjector(new BiojavaModule(), new BdgenomicsModule());
        readConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<Fastq, Read>>() {}));
        sequenceConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<org.biojava.bio.seq.Sequence, Sequence>>() {}));
        richSequenceConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<RichSequence, Sequence>>() {}));
        featureConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<RichSequence, List<Feature>>>() {}));
    }


    /**
     * Create and return an InputStream for the HDFS path represented by the specified file name.
     *
     * @param fileName file name, must not be null
     * @return an InputStream for the HDFS path represented by the specified file name
     * @throws IOException if an I/O error occurs
     */
    InputStream inputStream(final String fileName) throws IOException {
        checkNotNull(fileName);
        Path path = new Path(fileName);
        //FileSystem fileSystem = path.getFileSystem(sc.hadoopConfiguration);
        FileSystem fileSystem = path.getFileSystem(null);
        return fileSystem.open(path);
    }

    /**
     * Create and return a BufferedReader for the HDFS path represented by the specified file name.
     *
     * @param fileName file name, must not be null
     * @return a BufferedReader for the HDFS path represented by the specified file name
     * @throws IOException if an I/O error occurs
     */
    BufferedReader reader(final String fileName) throws IOException {
        return new BufferedReader(new InputStreamReader(inputStream(fileName)));
    }

    /**
     * Load the specified path in FASTQ format as reads.
     *
     * @param path path in FASTQ format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Read> biojavaLoadFastq(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTQ format...");
        FastqReader fastqReader = new SangerFastqReader();
        try (InputStream inputStream = inputStream(path)) {
            JavaRDD<Fastq> fastqs = javaSparkContext.parallelize((List<Fastq>) fastqReader.read(inputStream));
            JavaRDD<Read> reads = fastqs.map(fastq -> readConverter.convert(fastq, ConversionStringency.STRICT, logger));
            return reads.rdd();
        }
    }

    /**
     * Load the specified path in FASTA format and DNA alphabet as sequences using biojava Sequence.
     *
     * @param path path in FASTA format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaLoadFastaDna(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTA format and DNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<org.biojava.bio.seq.Sequence> biojavaSequences = javaSparkContext.parallelize(collect(SeqIOTools.readFastaDNA(reader)));
            JavaRDD<Sequence> sequences = biojavaSequences.map(biojavaSequence -> sequenceConverter.convert(biojavaSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in FASTA format and RNA alphabet as sequences using biojava Sequence.
     *
     * @param path path in FASTA format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaLoadFastaRna(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTA format and RNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<org.biojava.bio.seq.Sequence> biojavaSequences = javaSparkContext.parallelize(collect(SeqIOTools.readFastaRNA(reader)));
            JavaRDD<Sequence> sequences = biojavaSequences.map(biojavaSequence -> sequenceConverter.convert(biojavaSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in FASTA format and PROTEIN alphabet as sequences using biojava Sequence.
     *
     * @param path path in FASTA format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaLoadFastaProtein(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTA format and PROTEIN alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<org.biojava.bio.seq.Sequence> biojavaSequences = javaSparkContext.parallelize(collect(SeqIOTools.readFastaProtein(reader)));
            JavaRDD<Sequence> sequences = biojavaSequences.map(biojavaSequence -> sequenceConverter.convert(biojavaSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }


    /**
     * Load the specified path in FASTA format and DNA alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in FASTA format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadFastaDna(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTA format and DNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readFastaDNA(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in FASTA format and RNA alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in FASTA format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadFastaRna(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTA format and RNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readFastaRNA(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in FASTA format and PROTEIN alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in FASTA format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadFastaProtein(final String path) throws IOException {
        logger.info("Loading " + path + " as FASTA format and PROTEIN alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readFastaProtein(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }


    /**
     * Load the specified path in Genbank format and DNA alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in Genbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadGenbankDna(final String path) throws IOException {
        logger.info("Loading " + path + " as Genbank format and DNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readGenbankDNA(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in Genbank format and RNA alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in Genbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadGenbankRna(final String path) throws IOException {
        logger.info("Loading " + path + " as Genbank format and RNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readGenbankRNA(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in Genbank format and PROTEIN alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in Genbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadGenbankProtein(final String path) throws IOException {
        logger.info("Loading " + path + " as Genbank format and PROTEIN alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readGenbankProtein(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }


    /**
     * Load the specified path in EMBL format and DNA alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in EMBL format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadEmblDna(final String path) throws IOException {
        logger.info("Loading " + path + " as EMBL format and DNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readEMBLDNA(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in EMBL format and RNA alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in EMBL format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadEmblRna(final String path) throws IOException {
        logger.info("Loading " + path + " as EMBL format and RNA alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readEMBLRNA(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }

    /**
     * Load the specified path in EMBL format and PROTEIN alphabet as sequences using biojavax RichSequence.
     *
     * @param path path in EMBL format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public RDD<Sequence> biojavaxLoadEmblProtein(final String path) throws IOException {
        logger.info("Loading " + path + " as EMBL format and PROTEIN alphabet...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readEMBLProtein(reader, null)));
            JavaRDD<Sequence> sequences = richSequences.map(richSequence -> richSequenceConverter.convert(richSequence, ConversionStringency.STRICT, logger));
            return sequences.rdd();
        }
    }
    

    /**
     * Load the specified path in Genbank format and DNA alphabet as features using biojavax RichSequence.
     *
     * @param path path in Genkbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD biojavaxLoadGenbankDnaFeatures(final String path) throws IOException {
        logger.info("Loading " + path + " as Genbank format and DNA alphabet features...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readGenbankDNA(reader, null)));
            JavaRDD<Feature> features = richSequences.flatMap(richSequence -> featureConverter.convert(richSequence, ConversionStringency.STRICT, logger).iterator());
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in Genbank format and RNA alphabet as features using biojavax RichSequence.
     *
     * @param path path in Genkbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD biojavaxLoadGenbankRnaFeatures(final String path) throws IOException {
        logger.info("Loading " + path + " as Genbank format and RNA alphabet features...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readGenbankRNA(reader, null)));
            JavaRDD<Feature> features = richSequences.flatMap(richSequence -> featureConverter.convert(richSequence, ConversionStringency.STRICT, logger).iterator());
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in Genbank format and PROTEIN alphabet as features using biojavax RichSequence.
     *
     * @param path path in Genkbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD biojavaxLoadGenbankProteinFeatures(final String path) throws IOException {
        logger.info("Loading " + path + " as Genbank format and PROTEIN alphabet features...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readGenbankProtein(reader, null)));
            JavaRDD<Feature> features = richSequences.flatMap(richSequence -> featureConverter.convert(richSequence, ConversionStringency.STRICT, logger).iterator());
            return FeatureRDD.apply(features.rdd());
        }
    }


    /**
     * Load the specified path in EMBL format and DNA alphabet as features using biojavax RichSequence.
     *
     * @param path path in Genkbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD biojavaxLoadEmblDnaFeatures(final String path) throws IOException {
        logger.info("Loading " + path + " as EMBL format and DNA alphabet features...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readEMBLDNA(reader, null)));
            JavaRDD<Feature> features = richSequences.flatMap(richSequence -> featureConverter.convert(richSequence, ConversionStringency.STRICT, logger).iterator());
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in EMBL format and RNA alphabet as features using biojavax RichSequence.
     *
     * @param path path in Genkbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD biojavaxLoadEmblRnaFeatures(final String path) throws IOException {
        logger.info("Loading " + path + " as EMBL format and RNA alphabet features...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readEMBLRNA(reader, null)));
            JavaRDD<Feature> features = richSequences.flatMap(richSequence -> featureConverter.convert(richSequence, ConversionStringency.STRICT, logger).iterator());
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Load the specified path in EMBL format and PROTEIN alphabet as features using biojavax RichSequence.
     *
     * @param path path in Genkbank format, must not be null
     * @throws IOException if an I/O error occurs
     */
    public FeatureRDD biojavaxLoadEmblProteinFeatures(final String path) throws IOException {
        logger.info("Loading " + path + " as EMBL format and PROTEIN alphabet features...");
        try (BufferedReader reader = reader(path)) {
            JavaRDD<RichSequence> richSequences = javaSparkContext.parallelize(collect(RichSequence.IOTools.readEMBLProtein(reader, null)));
            JavaRDD<Feature> features = richSequences.flatMap(richSequence -> featureConverter.convert(richSequence, ConversionStringency.STRICT, logger).iterator());
            return FeatureRDD.apply(features.rdd());
        }
    }

    /**
     * Collect the sequences in the specified iterator to a list.
     *
     * @param iterator iterator to collect
     * @return the sequences in the specified iterator collected to a list
     * @throws IOException if an I/O error occurs
     */
    static List<org.biojava.bio.seq.Sequence> collect(final SequenceIterator iterator) {
        List<org.biojava.bio.seq.Sequence> sequences = new ArrayList<org.biojava.bio.seq.Sequence>();
        try {
            while (iterator.hasNext()) {
                sequences.add(iterator.nextSequence());
            }
        }
        catch (BioException e) {
            // ignore
        }
        return sequences;
    }

    /**
     * Collect the rich sequences in the specified iterator to a list.
     *
     * @param iterator iterator to collect
     * @return the rich sequences in the specified iterator collected to a list
     * @throws IOException if an I/O error occurs
     */
    static List<RichSequence> collect(final RichSequenceIterator iterator) {
        List<RichSequence> sequences = new ArrayList<RichSequence>();
        try {
            while (iterator.hasNext()) {
                sequences.add(iterator.nextRichSequence());
            }
        }
        catch (BioException e) {
            // ignore
        }
        return sequences;
    }
}
