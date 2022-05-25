# dishevelled-bio adam

### Hacking dishevelled-bio adam

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Apache Maven 3.3.9 or later, http://maven.apache.org
 * Apache Spark 3.2.1 or later, http://spark.apache.org
 * ADAM: Genomic Data System 1.0 or later, https://github.com/bigdatagenomics/adam

To build

    $ mvn install


### Running dishevelled-bio adam using ```spark-shell```

```
$ spark-shell \
    --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
    --conf spark.kryo.registrator=org.dishevelled.bio.adam.DishevelledKryoRegistrator \
    --jars target/dsh-bio-adam-$VERSION.jar,$PATH_TO_ADAM_ASSEMBLY_JAR

Using SPARK_SHELL=/usr/local/bin/spark-shell
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.2.1
      /_/

Using Scala version 2.12.15 (OpenJDK 64-Bit Server VM, Java 11.0.9)
Type in expressions to have them evaluated.
Type :help for more information.

scala> import org.dishevelled.bio.adam.DishevelledAdamContext
import org.dishevelled.bio.adam.DishevelledAdamContext

scala> val dac = new DishevelledAdamContext(sc)
dac: org.dishevelled.bio.adam.DishevelledAdamContext = org.dishevelled.bio.adam.DishevelledAdamContext@1aebe759

scala> val bedFeatures = dac.dshLoadBed("dvl1.200.bed")
bedFeatures: org.bdgenomics.adam.rdd.feature.FeatureRDD =                          
FeatureRDD(MapPartitionsRDD[1] at map at DishevelledAdamContext.java:145,SequenceDictionary{
1->1358505})

scala> bedFeatures.rdd.first
res0: org.bdgenomics.formats.avro.Feature = {"featureId": null, "name": "106624", "source": null, "featureType": null, "contigName": "1", "start": 1331345, "end": 1331536, "strand": "FORWARD", "phase": null, "frame": null, "score": 13.53, "geneId": null, "transcriptId": null, "exonId": null, "aliases": [], "parentIds": [], "target": null, "gap": null, "derivesFrom": null, "notes": [], "dbxrefs": [], "ontologyTerms": [], "circular": null, "attributes": {}}

scala> val gff3Features = dac.dshLoadGff3("dvl1.200.gff3")
gff3Features: org.bdgenomics.adam.rdd.feature.FeatureRDD =
FeatureRDD(MapPartitionsRDD[13] at map at DishevelledAdamContext.java:160,SequenceDictionary{
1->1363542})

scala> gff3Features.rdd.first
res5: org.bdgenomics.formats.avro.Feature = {"featureId": "ENSG00000169962", "name": "ENSG00000169962", "source": "Ensembl", "featureType": "gene", "contigName": "1", "start": 1331313, "end": 1335306, "strand": "FORWARD", "phase": null, "frame": null, "score": null, "geneId": null, "transcriptId": null, "exonId": null, "aliases": [], "parentIds": [], "target": null, "gap": null, "derivesFrom": null, "notes": [], "dbxrefs": [], "ontologyTerms": [], "circular": null, "attributes": {"biotype": "protein_coding"}}

scala> val variants = dac.dshLoadVariants("small.vcf")
variants: org.bdgenomics.adam.rdd.variant.VariantRDD = VariantRDD(MapPartitionsRDD[7] at flatMap at DishevelledAdamContext.java:175,null,null)

scala> variants.rdd.first
res1: org.bdgenomics.formats.avro.Variant = {"contigName": "1", "start": 14396, "end": 14400, "names": [], "referenceAllele": "CTGT", "alternateAllele": "C", "filtersApplied": true, "filtersPassed": false, "filtersFailed": ["IndelQD"], "annotation": {"ancestralAllele": null, "alleleCount": 2, "readDepth": null, "forwardReadDepth": null, "reverseReadDepth": null, "referenceReadDepth": null, "referenceForwardReadDepth": null, "referenceReverseReadDepth": null, "alleleFrequency": 0.333, "cigar": null, "dbSnp": null, "hapMap2": null, "hapMap3": null, "validated": null, "thousandGenomes": null, "somatic": false, "transcriptEffects": [], "attributes": {}}}

scala> val genotypes = dac.dshLoadGenotypes("small.vcf")
genotypes: org.bdgenomics.adam.rdd.variant.GenotypeRDD = GenotypeRDD(MapPartitionsRDD[9] at flatMap at DishevelledAdamContext.java:190,null,null,null)

scala> genotypes.rdd.first
res2: org.bdgenomics.formats.avro.Genotype = {"variant": {"contigName": "1", "start": 14396, "end": 14400, "names": [], "referenceAllele": "CTGT", "alternateAllele": "C", "filtersApplied": true, "filtersPassed": false, "filtersFailed": ["IndelQD"], "annotation": {"ancestralAllele": null, "alleleCount": 2, "readDepth": null, "forwardReadDepth": null, "reverseReadDepth": null, "referenceReadDepth": null, "referenceForwardReadDepth": null, "referenceReverseReadDepth": null, "alleleFrequency": 0.333, "cigar": null, "dbSnp": null, "hapMap2": null, "hapMap3": null, "validated": null, "thousandGenomes": null, "somatic": false, "transcriptEffects": [], "attributes": {}}}, "contigName": "1", "start": 14396, "end": 14400, "variantCallingAnnotations": null, "sampleId": "NA12892", "sampleDescriptio...
```
