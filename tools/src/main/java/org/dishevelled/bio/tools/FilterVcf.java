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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import org.dishevelled.bio.range.Ranges;

import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;
import org.dishevelled.bio.variant.vcf.VcfSample;
import org.dishevelled.bio.variant.vcf.VcfWriter;
import org.dishevelled.bio.variant.vcf.VcfStreamAdapter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.DoubleArgument;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.StringListArgument;

/**
 * Filter variants in VCF format.
 *
 * @author  Michael Heuer
 */
public final class FilterVcf extends AbstractFilter {
    private final List<Filter> filters;
    private final Path inputVcfPath;
    private final File outputVcfFile;
    private static final String USAGE = "dsh-filter-vcf -d rs149201999 -i input.vcf.gz -o output.vcf.gz";


    /**
     * Filter variants in VCF format.
     *
     * @deprecated will be removed in version 3.0
     * @param filters list of filters, must not be null
     * @param inputVcfFile input VCF file, if any
     * @param outputVcfFile output VCF file, if any
     */
    public FilterVcf(final List<Filter> filters, final File inputVcfFile, final File outputVcfFile) {
        this(filters, inputVcfFile == null ? null : inputVcfFile.toPath(), outputVcfFile);
    }

    /**
     * Filter variants in VCF format.
     *
     * @since 2.1
     * @param filters list of filters, must not be null
     * @param inputVcfPath input VCF path, if any
     * @param outputVcfFile output VCF file, if any
     */
    public FilterVcf(final List<Filter> filters, final Path inputVcfPath, final File outputVcfFile) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputVcfPath = inputVcfPath;
        this.outputVcfFile = outputVcfFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputVcfFile);

            final PrintWriter w = writer;
            VcfReader.stream(reader(inputVcfPath), new VcfStreamAdapter() {
                    private boolean wroteSamples = false;
                    private List<VcfSample> samples = new ArrayList<VcfSample>();

                    @Override
                    public void header(final VcfHeader header) {
                        VcfWriter.writeHeader(header, w);
                    }

                    @Override
                    public void sample(final VcfSample sample) {
                        samples.add(sample);
                    }

                    @Override
                    public void record(final VcfRecord record) {
                        // write out samples
                        if (!wroteSamples) {
                            VcfWriter.writeColumnHeader(samples, w);
                            wroteSamples = true;
                        }

                        // write out record
                        boolean pass = true;
                        for (Filter filter : filters) {
                            pass &= filter.accept(record);
                        }
                        if (pass) {
                            VcfWriter.writeRecord(samples, record, w);
                        }
                    }
                });

            return 0;
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
            }
        }
    }

    /**
     * Filter.
     */
    interface Filter {

        /**
         * Return true if the specified VCF record should be accepted by this filter.
         *
         * @param record VCF record
         * @return true if the specified VCF record should be accepted by this filter
         */
        boolean accept(VcfRecord record);
    }

    /**
     * Id filter.
     */
    public static final class IdFilter implements Filter {
        /** List of ids. */
        private final List<String> ids;

        /**
         * Create a new id filter with the specified list of ids.
         *
         * @param ids list of ids, must not be null
         */
        public IdFilter(final List<String> ids) {
            checkNotNull(ids);
            this.ids = ids;
        }

        @Override
        public boolean accept(final VcfRecord record) {
            for (String id : record.getId()) {
                if (ids.contains(id)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Range filter.
     */
    public static final class RangeFilter implements Filter {
        /** Chromosome. */
        private final String chrom;

        /** Range. */
        private final Range<Long> range;

        /** Range format regular expression. */
        private final Pattern RANGE = Pattern.compile("^(.*):([0-9]+)-([0-9]+)$");

        /**
         * Create a new range filter with the specified range format.
         *
         * @param value range format, must not be null
         */
        public RangeFilter(final String value) {
            checkNotNull(value);
            Matcher m = RANGE.matcher(value);
            if (!m.matches()) {
                throw new IllegalArgumentException("invalid range format, expected chrom:start-end in 0-based coordinates");
            }
            this.chrom = m.group(1);
            long start = Long.parseLong(m.group(2));
            long end = Long.parseLong(m.group(3));
            this.range = Range.closedOpen(start, end);
        }

        @Override
        public boolean accept(final VcfRecord record) {
            return chrom.equals(record.getChrom()) && Ranges.intersect(range, Range.singleton(record.getPos() - 1L));
        }
    }

    /**
     * Quality score filter.
     */
    public static final class QualFilter implements Filter {
        /** Quality score. */
        private final double qual;

        /**
         * Create a new quality score filter with the specified quality score.
         *
         * @param qual quality score
         */
        public QualFilter(final double qual) {
            this.qual = qual;
        }

        @Override
        public boolean accept(final VcfRecord record) {
            return record.getQual() >= qual;
        }
    }

    /**
     * Filter filter.
     */
    public static final class FilterFilter implements Filter {
        @Override
        public boolean accept(final VcfRecord record) {
            return (record.getFilter().length == 1 && "PASS".equals(record.getFilter()[0]));
        }
    }

    /**
     * Script filter.
     *
     * @since 1.1
     */
    public static final class ScriptFilter implements Filter {
        /** Compiled script. */
        private final CompiledScript compiledScript;

        /**
         * Create a new script filter with the specified script.
         *
         * @param script script
         */
        public ScriptFilter(final String script) {
            ScriptEngine engine = createScriptEngine();
            try {
                Compilable compilable = (Compilable) engine;
                compiledScript = compilable.compile("function test(r) { return (" + script + ") }\nvar result = test(r)");
            }
            catch (ScriptException e) {
                throw new IllegalArgumentException("could not compile script, caught " + e.getMessage(), e);
            }
         }

        @Override
        public boolean accept(final VcfRecord record) {
            try {
                compiledScript.getEngine().put("r", record);
                compiledScript.eval();
                return (Boolean) compiledScript.getEngine().get("result");
            }
            catch (ScriptException e) {
                throw new RuntimeException("could not evaluate compiled script, caught " + e.getMessage(), e);
            }
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
        StringListArgument idFilter = new StringListArgument("d", "id", "filter by id, specify as id1,id2,id3", false);
        StringArgument rangeFilter = new StringArgument("r", "range", "filter by range, specify as chrom:start-end in 0-based coordindates", false);
        DoubleArgument qualFilter = new DoubleArgument("q", "qual", "filter by quality score", false);
        Switch filterFilter = new Switch("f", "filter", "filter to records that have passed all filters");
        StringArgument scriptFilter = new StringArgument("e", "script", "filter by script, eval against r", false);
        PathArgument inputVcfPath = new PathArgument("i", "input-vcf-path", "input VCF path, default stdin", false);
        FileArgument outputVcfFile = new FileArgument("o", "output-vcf-file", "output VCF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, idFilter, rangeFilter, qualFilter, filterFilter, scriptFilter, inputVcfPath, outputVcfFile);
        CommandLine commandLine = new CommandLine(args);

        FilterVcf filterVcf = null;
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            List<Filter> filters = new ArrayList<Filter>();
            if (idFilter.wasFound()) {
                filters.add(new IdFilter(idFilter.getValue()));
            }
            if (rangeFilter.wasFound()) {
                filters.add(new RangeFilter(rangeFilter.getValue()));
            }
            if (qualFilter.wasFound()) {
                filters.add(new QualFilter(qualFilter.getValue()));
            }
            if (filterFilter.wasFound()) {
                filters.add(new FilterFilter());
            }
            if (scriptFilter.wasFound()) {
                filters.add(new ScriptFilter(scriptFilter.getValue()));
            }
            filterVcf = new FilterVcf(filters, inputVcfPath.getValue(), outputVcfFile.getValue());
        }
        catch (CommandLineParseException e) {
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        catch (NullPointerException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(filterVcf.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
