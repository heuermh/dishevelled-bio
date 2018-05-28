# dishevelled-bio adam-explorer

### Hacking dishevelled-bio adam-explorer

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Apache Maven 3.3.9 or later, http://maven.apache.org
 * Apache Spark 2.3.0 or later, http://spark.apache.org


To build

    $ mvn install


To run

```
$ spark-shell \
    --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
    --conf spark.kryo.registrator=org.bdgenomics.adam.serialization.ADAMKryoRegistrator \
    --jars target/dsh-bio-adam-explorer-1.1-SNAPSHOT.jar

Spark context available as 'sc'.
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.3.0
      /_/

Using Scala version 2.11.8 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_111)
Type in expressions to have them evaluated.
Type :help for more information.

scala> import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.ADAMContext._

scala> import org.dishevelled.bio.adam.explorer.ADAMExplorer._
import org.dishevelled.bio.adam.explorer.ADAMExplorer._

scala> val alignments = sc.loadAlignments("alignments.bam")
alignments: org.bdgenomics.adam.rdd.read.AlignmentRecordRDD = RDDBoundAlignmentRecordRDD
with 85 reference sequences, 3 read groups, and 0 processing steps

scala> explore(alignments)
res0: Int = 0

scala>
```

![adam-explorer screenshot](https://github.com/heuermh/cannoli/raw/master/images/screen-shot.png)

