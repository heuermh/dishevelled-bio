# dsh-bio-native
GraalVM native image application for `dsh-bio`.

### Hacking dsh-bio-native

Install

 * Apache Maven 3.3.9 or later, https://maven.apache.org
 * GraalVM Community Edition 19.3.0 based on OpenJDK 8u232, https://www.graalvm.org/downloads/

```
$ gu install native-image
```

To build, use Maven with GraalVM specified in `JAVA_HOME`

```
$ JAVA_HOME=/path/to/.../graalvm-ce-java8-19.3.0/Contents/Home mvn package

$ ./target/dsh-bio-native --help
usage:
dsh-bio [command] [args]

commands:
  compress-bed    compress features in BED format to splittable bgzf or bzip2 compression codecs
  compress-fasta    compress sequences in FASTA format to splittable bgzf or bzip2 compression codecs
  compress-fastq    compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs
...
```

### Benchmarks

On OSX, compared to Oracle JDK 8

```
$ java -version
java version "1.8.0_191"
Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
```
the results are rather disappointing; `dsh-bio-native` is consisently slower.

#### Filtering features
```
$ wget ftp://ftp-trace.ncbi.nlm.nih.gov/giab/ftp/data/AshkenazimTrio/analysis/NIST_v4beta_SmallVariantDraftBenchmark_07192019/GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.bed

$ gzip GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.bed


$ time dsh-bio-native filter-bed --range "22:51212500-51220778" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.bed.gz
...
22	51219805	51220777

real 0m1.176s
user 0m1.028s
sys  0m0.123s


$ time dsh-bio filter-bed --range "22:51212500-51220778" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.bed.gz
...
22	51219805	51220777

real 0m0.871s
user 0m1.311s
sys  0m0.130s


$ time dsh-bio-native filter-bed --script "r.getChrom() == '22' && r.getStart() > 51212500" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.bed.gz
...
22	51219805	51220777

real 0m2.804s
user 0m2.923s
sys  0m0.162s


$ time dsh-bio filter-bed --script "r.getChrom() == '22' && r.getStart() > 51212500" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.bed.gz
...
22	51219805	51220777

real 0m2.368s
user 0m3.538s
sys  0m0.327s
```

#### Filtering variants
```
$ wget ftp://ftp-trace.ncbi.nlm.nih.gov/giab/ftp/data/AshkenazimTrio/analysis/NIST_v4beta_SmallVariantDraftBenchmark_07192019/GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.vcf.gz


$ time dsh-bio-native filter-vcf --range "22:51209446-51220442" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.vcf.gz
...
22	51220441	rs76759269	T	C	50	PASS	callable=CS_CCS15kbGATK4_callable,CS_CCS15kbDV_callable,CS_10XLRGATK_callable;datasets=7;difficultregion=hg19_self_chain_split_withalts_gt10k,superdupsmerged_all_sort;platforms=5;datasetsmissingcall=IonExome;callsetnames=CCS15kbGATK4,CCS15kbDV,10XLRGATK,HiSeqPE300xGATK,CGnormal,HiSeqPE300xfreebayes,HiSeq250x250GATK,HiSeq250x250freebayes,HiSeqMatePairGATK,HiSeqMatePairfreebayes,SolidSE75GATKHC;filt=CS_CGnormal_filt,CS_SolidSE75GATKHC_filt;platformnames=PacBio,10X,Illumina,CG,Solid;callsets=11;datasetnames=15KbCCS,10XChromiumLR,HiSeqPE300x,CGnormal,HiSeq250x250,HiSeqMatePair,SolidSE75bp	GT:PS:DP:ADALL:AD:GQ	1/1:.:828:132,391:0,92:601

real 1m34.760s
user 1m32.757s
sys   0m0.681s


$ time dsh-bio filter-vcf --range "22:51209446-51220442" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.vcf.gz
...
22	51220441	rs76759269	T	C	50	PASS	callable=CS_CCS15kbGATK4_callable,CS_CCS15kbDV_callable,CS_10XLRGATK_callable;datasets=7;difficultregion=hg19_self_chain_split_withalts_gt10k,superdupsmerged_all_sort;platforms=5;datasetsmissingcall=IonExome;callsetnames=CCS15kbGATK4,CCS15kbDV,10XLRGATK,HiSeqPE300xGATK,CGnormal,HiSeqPE300xfreebayes,HiSeq250x250GATK,HiSeq250x250freebayes,HiSeqMatePairGATK,HiSeqMatePairfreebayes,SolidSE75GATKHC;filt=CS_CGnormal_filt,CS_SolidSE75GATKHC_filt;platformnames=PacBio,10X,Illumina,CG,Solid;callsets=11;datasetnames=15KbCCS,10XChromiumLR,HiSeqPE300x,CGnormal,HiSeq250x250,HiSeqMatePair,SolidSE75bp	GT:PS:DP:ADALL:AD:GQ	1/1:.:828:132,391:0,92:601

real 0m42.546s
user 0m44.158s
sys   0m0.684s


$ time dsh-bio-native filter-vcf --script "r.getChrom() == '22' && r.getPos() > 51209446" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.vcf.gz
...
22	51220441	rs76759269	T	C	50	PASS	callable=CS_CCS15kbGATK4_callable,CS_CCS15kbDV_callable,CS_10XLRGATK_callable;datasets=7;difficultregion=hg19_self_chain_split_withalts_gt10k,superdupsmerged_all_sort;platforms=5;datasetsmissingcall=IonExome;callsetnames=CCS15kbGATK4,CCS15kbDV,10XLRGATK,HiSeqPE300xGATK,CGnormal,HiSeqPE300xfreebayes,HiSeq250x250GATK,HiSeq250x250freebayes,HiSeqMatePairGATK,HiSeqMatePairfreebayes,SolidSE75GATKHC;filt=CS_CGnormal_filt,CS_SolidSE75GATKHC_filt;platformnames=PacBio,10X,Illumina,CG,Solid;callsets=11;datasetnames=15KbCCS,10XChromiumLR,HiSeqPE300x,CGnormal,HiSeq250x250,HiSeqMatePair,SolidSE75bp	GT:PS:DP:ADALL:AD:GQ	1/1:.:828:132,391:0,92:601

real 1m55.306s
user 1m53.188s
sys   0m1.016s


$ time dsh-bio filter-vcf --script "r.getChrom() == '22' && r.getPos() > 51209446" -i GIAB_SmallVariant_Benchmark_v4beta_GRCh37_HG002.vcf.gz
...
22	51220441	rs76759269	T	C	50	PASS	callable=CS_CCS15kbGATK4_callable,CS_CCS15kbDV_callable,CS_10XLRGATK_callable;datasets=7;difficultregion=hg19_self_chain_split_withalts_gt10k,superdupsmerged_all_sort;platforms=5;datasetsmissingcall=IonExome;callsetnames=CCS15kbGATK4,CCS15kbDV,10XLRGATK,HiSeqPE300xGATK,CGnormal,HiSeqPE300xfreebayes,HiSeq250x250GATK,HiSeq250x250freebayes,HiSeqMatePairGATK,HiSeqMatePairfreebayes,SolidSE75GATKHC;filt=CS_CGnormal_filt,CS_SolidSE75GATKHC_filt;platformnames=PacBio,10X,Illumina,CG,Solid;callsets=11;datasetnames=15KbCCS,10XChromiumLR,HiSeqPE300x,CGnormal,HiSeq250x250,HiSeqMatePair,SolidSE75bp	GT:PS:DP:ADALL:AD:GQ	1/1:.:828:132,391:0,92:601

real 0m54.060s
user 0m54.175s
sys   0m1.374s
```
