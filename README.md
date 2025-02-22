# dishevelled-bio
dishevelled.org bio

[![Build Status](https://travis-ci.org/heuermh/dishevelled-bio.svg?branch=master)](https://travis-ci.org/heuermh/dishevelled-bio)
[![Maven Central](https://img.shields.io/maven-central/v/org.dishevelled/dsh-bio.svg?maxAge=600)](http://search.maven.org/#search%7Cga%7C1%7Corg.dishevelled)


### Hacking dishevelled-bio

Install

 * JDK 17 or later, https://openjdk.java.net
 * Apache Maven 3.8.5 or later, https://maven.apache.org

To build

    $ mvn install


### Running dishevelled-bio tools

In addition to APIs in various modules, dishevelled.org bio also provides a set of
command line tools, gathered together into a single top-level `dsh-bio` script

```bash
$ dsh-bio --help
usage:
dsh-bio [command] [args]


commands:
  bin-fastq-quality-scores    bin quality scores from files in FASTQ format
  compress-bed    compress features in BED format to splittable bgzf or bzip2 compression codecs
  compress-fasta    compress sequences in FASTA format to splittable bgzf or bzip2 compression codecs
  compress-fastq    compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs
  compress-gaf    compress alignments in GAF format to splittable bgzf or bzip2 compression codecs
  compress-gfa1    compress assembly in GFA 1.0 format to splittable bgzf or bzip2 compression codecs
  compress-gfa2    compress assembly in GFA 2.0 format to splittable bgzf or bzip2 compression codecs
  compress-gff3    compress features in GFF3 format to splittable bgzf or bzip2 compression codecs
  compress-paf    compress alignments in PAF format to splittable bgzf or bzip2 compression codecs
  compress-rgfa    compress assembly in rGFA format to splittable bgzf or bzip2 compression codecs
  compress-sam    compress alignments in SAM format to splittable bgzf or bzip2 compression codecs
  compress-vcf    compress variants and genotypes in VCF format to splittable bgzf or bzip2 compression codecs
  count-fastq    count DNA sequences in FASTQ format
  create-sequence-dictionary    create a SequenceDictionary from DNA sequences in FASTA format
  disinterleave-fastq    convert interleaved FASTQ format into first and second DNA sequence files in FASTQ format
  downsample-fastq    downsample DNA sequences from files in FASTQ format
  downsample-interleaved-fastq    downsample DNA sequences from a file in interleaved FASTQ format
  export-segments    export assembly segment sequences in GFA 1.0 format to FASTA format
  extract-fasta    extract matching DNA or protein sequences in FASTA format
  extract-fasta-kmers    extract kmers from DNA or protein sequences in FASTA format
  extract-fasta-kmers-to-parquet    (beta) extract kmers from DNA or protein sequences in FASTA format to Parquet format
  extract-fasta-kmers-to-parquet3    (beta) extract kmers from DNA or protein sequences in FASTA format to Parquet format
  extract-fastq    extract matching DNA sequences in FASTQ format
  extract-fastq-by-length    extract DNA sequences in FASTQ format with a range of lengths
  fasta-index-to-pangenome    convert DNA sequences in FASTA index (.fai) format to pangenome text format
  fasta-index-to-pangenome-tree    convert DNA sequences in FASTA index (.fai) format to pangenome tree format
  fasta-to-fastq    convert DNA sequences in FASTA format to FASTQ format
  fasta-to-pangenome    convert DNA sequences in FASTA format to pangenome text format
  fasta-to-pangenome-tree    convert DNA sequences in FASTA format to pangenome tree format
  fasta-to-parquet    (beta) convert DNA or protein sequences in FASTA format to Parquet format
  fasta-to-parquet2    (beta) convert DNA or protein sequences in FASTA format to Parquet format, with transaction size
  fasta-to-parquet3    (beta) convert DNA or protein sequences in FASTA format to Parquet format, with partition size
  fasta-to-parquet4    (beta) convert DNA or protein sequences in FASTA format to Parquet format, with flush after
  fasta-to-parquet5    (beta) convert DNA or protein sequences in FASTA format to Parquet format, per thread output
  fasta-to-parquet6    (beta) convert DNA or protein sequences in FASTA format to Parquet format, per thread output, no view
  fasta-to-text    convert DNA or protein sequences in FASTA format to tab-separated values (tsv) text format
  fastq-description    output description lines from DNA sequences in FASTQ format
  fastq-sequence-length    output sequence lengths from DNA sequences in FASTQ format
  fastq-to-bam    convert DNA sequences in FASTQ format to unaligned BAM format
  fastq-to-fasta    convert DNA sequences in FASTQ format to FASTA format
  fastq-to-text    convert DNA sequences in FASTQ format to tab-separated values (tsv) text format
  filter-bed    filter features in BED format
  filter-fasta    filter sequences in FASTA format
  filter-fastq    filter sequences in FASTQ format
  filter-gaf    filter alignments in GAF format
  filter-gfa1    filter assembly in GFA 1.0 format
  filter-gfa2    filter assembly in GFA 2.0 format
  filter-gff3    filter features in GFF3 format
  filter-paf    filter alignments in PAF format
  filter-rgfa    filter assembly in rGFA format
  filter-sam    filter alignments in SAM format
  filter-vcf    filter variants in VCF format
  gfa1-to-gfa2    convert GFA 1.0 format to GFA 2.0 format
  gff3-to-bed    convert transcript features in GFF3 format to BED format
  identify-gfa1    add identifier annotation to records in GFA 1.0 format
  interleave-fastq    convert first and second DNA sequence files in FASTQ format to interleaved FASTQ format
  interleaved-fastq-to-bam    convert DNA sequences in interleaved FASTQ format to unaligned BAM format
  links-to-cytoscape-edges    convert links in GFA 1.0 format to edges.txt format for Cytoscape
  links-to-property-graph    convert links in GFA 1.0 format to property graph CSV format
  list-filesystems    list filesystem providers
  reassemble-paths    reassemble paths in GFA 1.0 format from traversal records
  remap-dbsnp    remap DB Type=String flags in VCF format to DB Type=Flag and dbsnp Type=String fields
  remap-phase-set    remap PS Type=String phase set ids in VCF format to PS Type=Integer
  rename-bed-references    rename references in BED files
  rename-gff3-references    rename references in GFF3 files
  rename-vcf-references    rename references in VCF files
  segments-to-cytoscape-nodes    convert segments in GFA 1.0 format to nodes.txt format for Cytoscape
  segments-to-property-graph    convert segments in GFA 1.0 format to property graph CSV format
  split-bed    split files in BED format
  split-fasta    split files in FASTA format
  split-fastq    split files in FASTQ format
  split-gaf    split files in GAF format
  split-gff3    split files in GFF3 format
  split-interleaved-fastq    split files in interleaved FASTQ format
  split-paf    split files in PAF format
  split-sam    split files in SAM format
  split-vcf    split files in VCF format
  text-to-fasta    convert DNA or protein sequences in tab-separated values (tsv) text format to FASTA format
  text-to-fastq    convert DNA sequences in tab-separated values (tsv) text format to FASTQ format
  traversals-to-cytoscape-edges    convert traversals in GFA 1.0 format to edges.txt format for Cytoscape
  traversals-to-property-graph    convert traversals in GFA 1.0 format to property graph CSV format
  traverse-paths    traverse paths in GFA 1.0 format
  truncate-fasta    truncate DNA or protein sequences in FASTA format
  truncate-paths    truncate paths in GFA 1.0 format
  variant-table-to-vcf    convert Ensembl variant table to VCF format
  vcf-pedigree    extract a pedigree from VCF format
  vcf-samples    extract samples from VCF format

arguments:
   -a, --about  display about message [optional]
   -v, --version  display about message [optional]
   -h, --help  display help message [optional]


$ dsh-bio split-bed --help
usage:
dsh-split-bed -r 100 -i foo.bed.gz

arguments:
   -a, --about  display about message [optional]
   -h, --help  display help message [optional]
   -i, --input-path [interface java.nio.file.Path]  input BED path, default stdin [optional]
   -b, --bytes [class java.lang.String]  split input path at next record after each n bytes [optional]
   -r, --records [class java.lang.Long]  split input path after each n records [optional]
   -p, --prefix [class java.lang.String]  output file prefix [optional]
   -d, --left-pad [class java.lang.Integer]  left pad split index in output file name [optional]
   -s, --suffix [class java.lang.String]  output file suffix, e.g. .bed.gz [optional]
```

Each command is also available as a separate script, e.g. `dsh-split-bed`

```bash
$ dsh-split-bed --help
usage:
dsh-split-bed -r 100 -i foo.bed.gz

arguments:
   -a, --about  display about message [optional]
   -h, --help  display help message [optional]
   -i, --input-path [interface java.nio.file.Path]  input BED path, default stdin [optional]
   -b, --bytes [class java.lang.String]  split input path at next record after each n bytes [optional]
   -r, --records [class java.lang.Long]  split input path after each n records [optional]
   -p, --prefix [class java.lang.String]  output file prefix [optional]
   -d, --left-pad [class java.lang.Integer]  left pad split index in output file name [optional]
   -s, --suffix [class java.lang.String]  output file suffix, e.g. .bed.gz [optional]
```


#### Compression

Across the dishevelled.org bio command line tools, stdin and stdout should behave as expected,
and files and streams compressed with Zstandard (zstd), XZ, GZIP, BZip2, and block-compressed GZIP
(BGZF) are handled transparently. Use file extensions `.zst`, `.xz`, `.gz`, `.bz2`, and `.bgz`
respectively to force the issue, if necessary.


#### File systems

As of version 2.1, cloud storage file systems from Google Cloud Storage (via `gs://` paths)
and Amazon Simple Storage Service (Amazon S3) (via `s3://` paths) are supported for input paths.

```
$ dsh-bio list-filesystems
Installed filesystem providers:
  file	sun.nio.fs.MacOSXFileSystemProvider
  jar	jdk.nio.zipfs.ZipFileSystemProvider
  jrt	jdk.internal.jrtfs.JrtFileSystemProvider
  gs	com.google.cloud.storage.contrib.nio.CloudStorageFileSystemProvider
  s3	software.amazon.nio.spi.s3.S3FileSystemProvider
  s3x	software.amazon.nio.spi.s3.S3XFileSystemProvider
```


#### Expressions

Commands with a `--script` argument expect an expression written in JavaScript that evaluates
to boolean true or false against a record, provided in the context as variable `r`.  For example,
with `dsh-filter-bed`, to filter BED records by chromosome and score

```javascript
r.getChrom() == 1 && r.getScore() > 10
```
specified on the command line as

```bash
$ dsh-bio filter-bed -i input.bed --script "r.getChrom() == 1 && r.getScore() > 10"
```


### Installing dishevelled-bio via Conda

The dishevelled.org bio command line tools are available in Conda via Bioconda, https://bioconda.github.io

```bash
$ conda install dsh-bio
```


### Installing dishevelled-bio via Homebrew

The dishevelled.org bio command line tools are available in Homebrew via Brewsci/bio, https://github.com/brewsci/homebrew-bio

```bash
$ brew install brewsci/bio/dsh-bio
```


### Installing dishevelled-bio via Docker

The dishevelled.org bio command line tools are available in Docker via BioContainers, https://biocontainers.pro

```bash
$ docker pull quay.io/biocontainers/dsh-bio:{tag}
```

Find `{tag}` on the tag search page, https://quay.io/repository/biocontainers/dsh-bio?tab=tags


### Acknowledgments

This repository contains test data copyrighted by the UniProt Consortium, see https://www.uniprot.org/terms.

These test data distributed under the Creative Commons Attribution (CC BY 4.0) License.
