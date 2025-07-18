/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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
package org.dishevelled.bio.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;

import java.util.SortedMap;

import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

/**
 * Command line tools.
 *
 * @author  Michael Heuer
 */
public final class Tools implements Callable<Integer> {
    private final String[] args;
    private final SortedMap<String, Command> commands;
    private static final String USAGE = "dsh-bio [command] [args]";

    /**
     * Create a new tools callable with the specified map of commands keyed
     * by name and array of arguments.
     *
     * @param args args, must not be null
     * @param commands map of commands keyed by name, must not be null
     */
    public Tools(final String[] args, final SortedMap<String, Command> commands) {
        checkNotNull(args);
        checkNotNull(commands);
        this.args = args;
        this.commands = ImmutableSortedMap.copyOf(commands);
    }


    @Override
    public Integer call() throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("[command] required");
        }
        Command command = commands.get(args[0]);
        if (command == null) {
            throw new IllegalArgumentException("invalid command " + args[0]);
        }
        Method main = command.getCommandClass().getMethod("main", String[].class);
        main.invoke(null, new Object[] { dropFirst(args) });
        return 0;
    }

    /**
     * Return the usage message.
     *
     * @return the usage message
     */
    String usage() {
        StringBuilder sb = new StringBuilder();
        sb.append(USAGE);
        sb.append("\n\n");
        sb.append("commands:");
        for (Command command : commands.values()) {
            sb.append("\n  ");
            sb.append(command.getName());
            sb.append("    ");
            sb.append(command.getDescription());
        }
        return sb.toString();
    }


    /**
     * Return an array containing the first element of the specified array.
     *
     * @param args args
     * @return an array containing the first element of the specified array
     */
    static String[] first(final String[] args) {
        return args.length == 0 ? args : new String[] { args[0] };
    }

    /**
     * Drop the first element from the specified array.
     *
     * @param args args
     * @return a copy of the specified array after dropping the first element
     */
    static String[] dropFirst(final String[] args) {
        if (args.length == 0) {
            return args;
        }
        if (args.length == 1) {
            return new String[0];
        }
        String[] remainder = new String[args.length - 1];
        System.arraycopy(args, 1, remainder, 0, args.length - 1);
        return remainder;
    }

    /** Map of commands keyed by command name. */
    static SortedMap<String, Command> COMMANDS = new ImmutableSortedMap.Builder<String, Command>(Ordering.natural())
        .put("bin-fastq-quality-scores", new Command("bin-fastq-quality-scores", "bin quality scores from files in FASTQ format", BinFastqQualityScores.class))
        .put("compress-bed", new Command("compress-bed", "compress features in BED format to splittable bgzf or bzip2 compression codecs", CompressBed.class))
        .put("compress-fasta", new Command("compress-fasta", "compress sequences in FASTA format to splittable bgzf or bzip2 compression codecs", CompressFasta.class))
        .put("compress-fastq", new Command("compress-fastq", "compress sequences in FASTQ format to splittable bgzf or bzip2 compression codecs", CompressFastq.class))
        .put("compress-gaf", new Command("compress-gaf", "compress alignments in GAF format to splittable bgzf or bzip2 compression codecs", CompressGaf.class))
        .put("compress-gfa1", new Command("compress-gfa1", "compress assembly in GFA 1.0 format to splittable bgzf or bzip2 compression codecs", CompressGfa1.class))
        .put("compress-gfa2", new Command("compress-gfa2", "compress assembly in GFA 2.0 format to splittable bgzf or bzip2 compression codecs", CompressGfa2.class))
        .put("compress-gff3", new Command("compress-gff3", "compress features in GFF3 format to splittable bgzf or bzip2 compression codecs", CompressGff3.class))
        .put("compress-paf", new Command("compress-paf", "compress alignments in PAF format to splittable bgzf or bzip2 compression codecs", CompressPaf.class))
        .put("compress-rgfa", new Command("compress-rgfa", "compress assembly in rGFA format to splittable bgzf or bzip2 compression codecs", CompressRgfa.class))
        .put("compress-sam", new Command("compress-sam", "compress alignments in SAM format to splittable bgzf or bzip2 compression codecs", CompressSam.class))
        .put("compress-vcf", new Command("compress-vcf", "compress variants and genotypes in VCF format to splittable bgzf or bzip2 compression codecs", CompressVcf.class))
        .put("count-fastq", new Command("count-fastq", "count DNA sequences in FASTQ format", CountFastq.class))
        .put("create-sequence-dictionary", new Command("create-sequence-dictionary", "create a SequenceDictionary from DNA sequences in FASTA format", CreateSequenceDictionary.class))
        .put("disinterleave-fastq", new Command("disinterleave-fastq", "convert interleaved FASTQ format into first and second DNA sequence files in FASTQ format", DisinterleaveFastq.class))
        .put("downsample-fastq", new Command("downsample-fastq", "downsample DNA sequences from files in FASTQ format", DownsampleFastq.class))
        .put("downsample-interleaved-fastq", new Command("downsample-interleaved-fastq", "downsample DNA sequences from a file in interleaved FASTQ format", DownsampleInterleavedFastq.class))
        .put("export-segments", new Command("export-segments", "export assembly segment sequences in GFA 1.0 format to FASTA format", ExportSegments.class))
        .put("extract-fasta", new Command("extract-fasta", "extract matching DNA or protein sequences in FASTA format", ExtractFasta.class))
        .put("extract-fasta-kmers", new Command("extract-fasta-kmers", "extract kmers from DNA or protein sequences in FASTA format", ExtractFastaKmers.class))
        .put("extract-fasta-kmers-to-parquet", new Command("extract-fasta-kmers-to-parquet", "(beta) extract kmers from DNA or protein sequences in FASTA format to Parquet format", ExtractFastaKmersToParquet.class))
        .put("extract-fasta-kmers-to-parquet3", new Command("extract-fasta-kmers-to-parquet3", "(beta) extract kmers from DNA or protein sequences in FASTA format to Parquet format", ExtractFastaKmersToParquet3.class))
        .put("extract-fastq", new Command("extract-fastq", "extract matching DNA sequences in FASTQ format", ExtractFastq.class))
        .put("extract-fastq-by-length", new Command("extract-fastq-by-length", "extract DNA sequences in FASTQ format with a range of lengths", ExtractFastqByLength.class))
        .put("extract-uniprot-features", new Command("extract-uniprot-features", "extract protein features from UniProt XML format", ExtractUniprotFeatures.class))
        .put("extract-uniprot-features-to-parquet", new Command("extract-uniprot-features-to-parquet", "extract protein features from UniProt XML format to Parquet format", ExtractUniprotFeaturesToParquet.class))
        .put("extract-uniprot-features-to-partitioned-parquet", new Command("extract-uniprot-features-to-partitioned-parquet", "extract protein features from UniProt XML format to partitioned Parquet format", ExtractUniprotFeaturesToPartitionedParquet.class))
        .put("extract-uniprot-sequences", new Command("extract-uniprot-sequences", "extract protein sequences from UniProt XML format", ExtractUniprotSequences.class))
        .put("fasta-to-fastq", new Command("fasta-to-fastq", "convert DNA sequences in FASTA format to FASTQ format", FastaToFastq.class))
        .put("fasta-to-pangenome", new Command("fasta-to-pangenome", "convert DNA sequences in FASTA format to pangenome text format", FastaToPangenome.class))
        .put("fasta-to-pangenome-tree", new Command("fasta-to-pangenome-tree", "convert DNA sequences in FASTA format to pangenome tree format", FastaToPangenomeTree.class))
        .put("fasta-to-parquet", new Command("fasta-to-parquet", "(beta) convert DNA or protein sequences in FASTA format to Parquet format", FastaToParquet.class))
        .put("fasta-to-parquet2", new Command("fasta-to-parquet2", "(beta) convert DNA or protein sequences in FASTA format to Parquet format, with transaction size", FastaToParquet2.class))
        .put("fasta-to-parquet3", new Command("fasta-to-parquet3", "(beta) convert DNA or protein sequences in FASTA format to Parquet format, with partition size", FastaToParquet3.class))
        .put("fasta-to-parquet4", new Command("fasta-to-parquet4", "(beta) convert DNA or protein sequences in FASTA format to Parquet format, with flush after", FastaToParquet4.class))
        .put("fasta-to-parquet5", new Command("fasta-to-parquet5", "(beta) convert DNA or protein sequences in FASTA format to Parquet format, per thread output", FastaToParquet5.class))
        .put("fasta-to-parquet6", new Command("fasta-to-parquet6", "(beta) convert DNA or protein sequences in FASTA format to Parquet format, per thread output, no view", FastaToParquet6.class))
        .put("fasta-to-text", new Command("fasta-to-text", "convert DNA or protein sequences in FASTA format to tab-separated values (tsv) text format", FastaToText.class))
        .put("fasta-index-to-pangenome", new Command("fasta-index-to-pangenome", "convert DNA sequences in FASTA index (.fai) format to pangenome text format", FastaIndexToPangenome.class))
        .put("fasta-index-to-pangenome-tree", new Command("fasta-index-to-pangenome-tree", "convert DNA sequences in FASTA index (.fai) format to pangenome tree format", FastaIndexToPangenomeTree.class))
        .put("fastq-description", new Command("fastq-description", "output description lines from DNA sequences in FASTQ format", FastqDescription.class))
        .put("fastq-sequence-length", new Command("fastq-sequence-length", "output sequence lengths from DNA sequences in FASTQ format", FastqSequenceLength.class))
        .put("fastq-to-bam", new Command("fastq-to-bam", "convert DNA sequences in FASTQ format to unaligned BAM format", FastqToBam.class))
        .put("fastq-to-fasta", new Command("fastq-to-fasta", "convert DNA sequences in FASTQ format to FASTA format", FastqToFasta.class))
        .put("fastq-to-parquet", new Command("fastq-to-parquet", "convert DNA sequences in FASTQ format to Parquet format", FastqToParquet.class))
        .put("fastq-to-partitioned-parquet", new Command("fastq-to-partitioned-parquet", "convert DNA sequences in FASTQ format to partitioned Parquet format", FastqToPartitionedParquet.class))
        .put("fastq-to-text", new Command("fastq-to-text", "convert DNA sequences in FASTQ format to tab-separated values (tsv) text format", FastqToText.class))
        .put("filter-bed", new Command("filter-bed", "filter features in BED format", FilterBed.class))
        .put("filter-fasta", new Command("filter-fasta", "filter sequences in FASTA format", FilterFasta.class))
        .put("filter-fastq", new Command("filter-fastq", "filter sequences in FASTQ format", FilterFastq.class))
        .put("filter-gaf", new Command("filter-gaf", "filter alignments in GAF format", FilterGaf.class))
        .put("filter-gfa1", new Command("filter-gfa1", "filter assembly in GFA 1.0 format", FilterGfa1.class))
        .put("filter-gfa2", new Command("filter-gfa2", "filter assembly in GFA 2.0 format", FilterGfa2.class))
        .put("filter-gff3", new Command("filter-gff3", "filter features in GFF3 format", FilterGff3.class))
        .put("filter-paf", new Command("filter-paf", "filter alignments in PAF format", FilterPaf.class))
        .put("filter-rgfa", new Command("filter-rgfa", "filter assembly in rGFA format", FilterRgfa.class))
        .put("filter-sam", new Command("filter-sam", "filter alignments in SAM format", FilterSam.class))
        .put("filter-vcf", new Command("filter-vcf", "filter variants in VCF format", FilterVcf.class))
        .put("gfa1-to-gfa2", new Command("gfa1-to-gfa2", "convert GFA 1.0 format to GFA 2.0 format", Gfa1ToGfa2.class))
        .put("gff3-to-bed", new Command("gff3-to-bed", "convert transcript features in GFF3 format to BED format", Gff3ToBed.class))
        .put("identify-gfa1", new Command("identify-gfa1", "add identifier annotation to records in GFA 1.0 format", IdentifyGfa1.class))
        .put("interleave-fastq", new Command("interleave-fastq", "convert first and second DNA sequence files in FASTQ format to interleaved FASTQ format", InterleaveFastq.class))
        .put("interleaved-fastq-to-bam", new Command("interleaved-fastq-to-bam", "convert DNA sequences in interleaved FASTQ format to unaligned BAM format", InterleavedFastqToBam.class))
        //.put("intersect-bed", new Command("intersect-bed", "similar to bedtools2 intersect -v", IntersectBed.class))
        .put("links-to-cytoscape-edges", new Command("links-to-cytoscape-edges", "convert links in GFA 1.0 format to edges.txt format for Cytoscape", LinksToCytoscapeEdges.class))
        .put("links-to-property-graph", new Command("links-to-property-graph", "convert links in GFA 1.0 format to property graph CSV format", LinksToPropertyGraph.class))
        .put("list-filesystems", new Command("list-filesystems", "list filesystem providers", ListFilesystems.class))
        .put("parquet-reads-to-fastq", new Command("parquet-reads-to-fastq", "convert reads in Parquet format to FASTQ format", ParquetReadsToFastq.class))
        .put("reassemble-paths", new Command("reassemble-paths", "reassemble paths in GFA 1.0 format from traversal records", ReassemblePaths.class))
        .put("rename-bed-references", new Command("rename-bed-references", "rename references in BED files", RenameBedReferences.class))
        .put("rename-gff3-references", new Command("rename-gff3-references", "rename references in GFF3 files", RenameGff3References.class))
        .put("rename-vcf-references", new Command("rename-vcf-references", "rename references in VCF files", RenameVcfReferences.class))
        .put("remap-dbsnp", new Command("remap-dbsnp", "remap DB Type=String flags in VCF format to DB Type=Flag and dbsnp Type=String fields", RemapDbSnp.class))
        .put("remap-phase-set", new Command("remap-phase-set", "remap PS Type=String phase set ids in VCF format to PS Type=Integer", RemapPhaseSet.class))
        .put("segments-to-cytoscape-nodes", new Command("segments-to-cytoscape-nodes", "convert segments in GFA 1.0 format to nodes.txt format for Cytoscape", SegmentsToCytoscapeNodes.class))
        .put("segments-to-property-graph", new Command("segments-to-property-graph", "convert segments in GFA 1.0 format to property graph CSV format", SegmentsToPropertyGraph.class))
        .put("split-bed", new Command("split-bed", "split files in BED format", SplitBed.class))
        .put("split-fasta", new Command("split-fasta", "split files in FASTA format", SplitFasta.class))
        .put("split-fastq", new Command("split-fastq", "split files in FASTQ format", SplitFastq.class))
        .put("split-gaf", new Command("split-gaf", "split files in GAF format", SplitGaf.class))
        .put("split-gff3", new Command("split-gff3", "split files in GFF3 format", SplitGff3.class))
        .put("split-interleaved-fastq", new Command("split-interleaved-fastq", "split files in interleaved FASTQ format", SplitInterleavedFastq.class))
        .put("split-paf", new Command("split-paf", "split files in PAF format", SplitPaf.class))
        .put("split-sam", new Command("split-sam", "split files in SAM format", SplitSam.class))
        .put("split-vcf", new Command("split-vcf", "split files in VCF format", SplitVcf.class))
        .put("summarize-uniprot-entries", new Command("summarize-uniprot-entries", "summarize UniProt entries in XML format", SummarizeUniprotEntries.class))
        .put("summarize-uniprot-entries-to-parquet", new Command("summarize-uniprot-entries-to-parquet", "summarize UniProt entries in XML format to Parquet format", SummarizeUniprotEntriesToParquet.class))
        .put("text-to-fasta", new Command("text-to-fasta", "convert DNA or protein sequences in tab-separated values (tsv) text format to FASTA format", TextToFasta.class))
        .put("text-to-fastq", new Command("text-to-fastq", "convert DNA sequences in tab-separated values (tsv) text format to FASTQ format", TextToFastq.class))
        .put("traversals-to-cytoscape-edges", new Command("traversals-to-cytoscape-edges", "convert traversals in GFA 1.0 format to edges.txt format for Cytoscape", TraversalsToCytoscapeEdges.class))
        .put("traversals-to-property-graph", new Command("traversals-to-property-graph", "convert traversals in GFA 1.0 format to property graph CSV format", TraversalsToPropertyGraph.class))
        .put("traverse-paths", new Command("traverse-paths", "traverse paths in GFA 1.0 format", TraversePaths.class))
        .put("truncate-fasta", new Command("truncate-fasta", "truncate DNA or protein sequences in FASTA format", TruncateFasta.class))
        .put("truncate-paths", new Command("truncate-paths", "truncate paths in GFA 1.0 format", TruncatePaths.class))
        .put("variant-table-to-vcf", new Command("variant-table-to-vcf", "convert Ensembl variant table to VCF format", EnsemblVariantTableToVcf.class))
        .put("vcf-pedigree", new Command("vcf-pedigree", "extract a pedigree from VCF format", VcfPedigree.class))
        .put("vcf-samples", new Command("vcf-samples", "extract samples from VCF format", VcfSamples.class))
        .put("vcf-to-parquet", new Command("vcf-to-parquet", "convert variants in VCF format to Parquet format", VcfToParquet.class))
        .build();

    /**
     * Command.
     */
    static class Command {
        /** Name for this command. */
        private final String name;

        /** Description for this command. */
        private final String description;

        /** Class for this command. */
        private final Class<?> commandClass;


        /**
         * Create a new command.
         *
         * @param name name for this command
         * @param description description for this command
         * @param commandClass class for this command
         */
        Command(final String name, final String description, final Class<?> commandClass) {
            this.name = name;
            this.description = description;
            this.commandClass = commandClass;
        }


        /**
         * Return the name for this command.
         *
         * @return the name for this command
         */
        String getName() {
            return name;
        }

        /**
         * Return the description for this command.
         *
         * @return the description for this command
         */
        String getDescription() {
            return description;
        }

        /**
         * Return the class for this command.
         *
         * @return the class for this command
         */
        Class<?> getCommandClass() {
            return commandClass;
        }
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch version = new Switch("v", "version", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        ArgumentList arguments = new ArgumentList(about, version, help);
        CommandLine commandLine = new CommandLine(first(args));

        Tools tools = new Tools(args, COMMANDS);
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (args.length == 0) {
                throw new CommandLineParseException("[command] required");
            }
            if (about.wasFound() || version.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(tools.usage(), null, commandLine, arguments, System.out);
                System.exit(0);
            }
        }
        catch (CommandLineParseException e) {
            if (about.wasFound() || version.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(tools.usage(), null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(tools.usage(), e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(tools.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
