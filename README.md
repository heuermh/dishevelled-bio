# dishevelled-bio
dishevelled.org bio

[![Build Status](https://travis-ci.org/heuermh/dishevelled-bio.svg?branch=master)](https://travis-ci.org/heuermh/dishevelled-bio)
[![Maven Central](https://img.shields.io/maven-central/v/org.dishevelled/dsh-bio.svg?maxAge=600)](http://search.maven.org/#search%7Cga%7C1%7Corg.dishevelled)


### Hacking dishevelled-bio

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Apache Maven 3.3.9 or later, http://maven.apache.org

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
  compress-bed    compress features in BED format to splittable bgzf or bzip2 compression codecs
  compress-fastq    compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs
  compress-gff3    compress features in GFF3 format to splittable bgzf or bzip2 compression codecs
  compress-vcf    compress variants and genotypes in VCF format to splittable bgzf or bzip2 compression codecs
  disinterleave-fastq    convert interleaved FASTQ format into first and second sequence files in FASTQ format
  downsample-fastq    downsample sequences from files in FASTQ format
  downsample-interleaved-fastq    downsample sequences from a file in interleaved FASTQ format
  extract-fasta    extract matching sequences in FASTA format
  extract-fastq    extract matching sequences in FASTQ format
  extract-fastq-by-length    extract sequences in FASTQ format with a range of lengths
  fasta-to-fastq    convert sequences in FASTA format to FASTQ format
  fastq-description    output description lines from sequences in FASTQ format
  fastq-sequence-length    output sequence lengths from sequences in FASTQ format
  fastq-to-fasta    convert sequences in FASTQ format to FASTA format
  filter-vcf    filter variants in VCF format
  gfa1-to-gfa2    convert GFA 1.0 format to GFA 2.0 format
  interleave-fastq    convert first and second sequence files in FASTQ format to interleaved FASTQ format
  remap-phase-set    remap Type=String PS phase set ids in VCF format to Type=Integer
  split-bed    split files in BED format
  split-fasta    split files in FASTA format
  split-fastq    split files in FASTQ format
  split-gff3    split files in GFF3 format
  split-interleaved-fastq    split files in interleaved FASTQ format
  split-vcf    split files in VCF format
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
   -i, --input-file [class java.io.File]  input BED file, default stdin [optional]
   -b, --bytes [class java.lang.String]  split input file at next record after each n bytes [optional]
   -r, --records [class java.lang.Long]  split input file after each n records [optional]
   -p, --prefix [class java.lang.String]  output file prefix [optional]
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
   -i, --input-file [class java.io.File]  input BED file, default stdin [optional]
   -b, --bytes [class java.lang.String]  split input file at next record after each n bytes [optional]
   -r, --records [class java.lang.Long]  split input file after each n records [optional]
   -p, --prefix [class java.lang.String]  output file prefix [optional]
   -s, --suffix [class java.lang.String]  output file suffix, e.g. .bed.gz [optional]
```


#### Compression

Across the dishevelled.org bio command line tools, stdin and stdout should behave as expected,
and files and streams compressed with GZIP, BZip2, and block-compressed GZIP (BGZF) are
handled transparently. Use file extensions `.gz`, `.bz2`, and `.bgz` respectively
to force the issue, if necessary.


#### Expressions

Commands with a `--script` argument expect an expression written in JavaScript that evaluates
to boolean true or false against a record, provided in the context as variable `r`.  For example,
with `dsh-filter-bed`, to filter BED records by chromosome and score

```javascript
1 == r.getChrom() && r.getScore() > 10.0
```
specified on the command line as

```bash
$ dsh-filter-bed -i input.bed --script "1 == r.getChrom() && r.getScore() > 10.0"
```

### Installing dishevelled-bio via Conda

The dishevelled.org bio command line tools are available in Conda via Bioconda, https://bioconda.github.io/

```bash
$ conda install dsh-bio
```


### Installing dishevelled-bio via Homebrew

The dishevelled.org bio command line tools are available in Homebrew via Brewsci/bio, https://github.com/brewsci/homebrew-bio

```bash
$ brew install brewsci/bio/dsh-bio
```
