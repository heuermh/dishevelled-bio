/*

    dsh-bio-tools  Command line tools.
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
            throw new IllegalArgumentException("invalid command " + command);
        }
        Method main = command.getCommandClass().getMethod("main", String[].class);
        main.invoke(null, new Object[] { dropFirst(args) });
        return 0;
    }

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

    static String[] first(final String[] args) {
        return args.length == 0 ? args : new String[] { args[0] };
    }

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

    static SortedMap<String, Command> COMMANDS = new ImmutableSortedMap.Builder<String, Command>(Ordering.natural())
        .put("downsample-fastq", new Command("downsample-fastq", "downsample sequences from files in FASTQ format", DownsampleFastq.class))
        .put("downsample-interleaved-fastq", new Command("downsample-interleaved-fastq", "downsample sequences from a file in interleaved FASTQ format", DownsampleInterleavedFastq.class))
        .put("extract-fasta", new Command("extract-fasta", "extract matching sequences in FASTA format", ExtractFasta.class))
        .put("extract-fastq", new Command("extract-fastq", "extract matching sequences in FASTQ format", ExtractFastq.class))
        .put("fasta-to-fastq", new Command("fasta-to-fastq", "convert sequences in FASTA format to FASTQ format", FastaToFastq.class))
        .put("fastq-description", new Command("fastq-description", "output description lines from sequences in FASTQ format", FastqDescription.class))
        .put("fastq-to-fasta", new Command("fastq-to-fasta", "convert sequences in FASTQ format to FASTA format", FastqToFasta.class))
        .put("filter-vcf", new Command("filter-vcf", "filter variants in VCF format", FilterVcf.class))
        .put("interleave-fastq", new Command("interleave-fastq", "convert first and second sequence files in FASTQ format to interleaved FASTQ format", InterleaveFastq.class))
        //.put("intersect-bed", new Command("intersect-bed", "similar to bedtools2 intersect -v", IntersectBed.class))
        .put("remap-phase-set", new Command("remap-phase-set", "remap Type=String PS phase set ids in VCF format to Type=Integer", RemapPhaseSet.class))
        .put("split-fastq", new Command("split-fastq", "convert interleaved FASTQ format into first and second sequence files in FASTQ format", SplitFastq.class))
        .put("vcf-pedigree", new Command("vcf-pedigree", "extract a pedigree from VCF format", VcfPedigree.class))
        .put("vcf-samples", new Command("vcf-samples", "extract samples from VCF format", VcfSamples.class))
        .build();

    static class Command {
        private final String name;
        private final String description;
        private final Class<?> commandClass;

        Command(final String name, final String description, final Class<?> commandClass) {
            this.name = name;
            this.description = description;
            this.commandClass = commandClass;
        }

        String getName() {
            return name;
        }

        String getDescription() {
            return description;
        }

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
        Switch help = new Switch("h", "help", "display help message");
        ArgumentList arguments = new ArgumentList(about, help);
        CommandLine commandLine = new CommandLine(first(args));

        Tools tools = new Tools(args, COMMANDS);
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (args.length == 0) {
                throw new CommandLineParseException("[command] required");
            }
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(tools.usage(), null, commandLine, arguments, System.out);
                System.exit(0);
            }
        }
        catch (CommandLineParseException e) {
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
