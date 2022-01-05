/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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

import org.dishevelled.bio.feature.bed.BedListener;
import org.dishevelled.bio.feature.bed.BedReader;
import org.dishevelled.bio.feature.bed.BedRecord;
import org.dishevelled.bio.feature.bed.BedWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Filter features in BED format.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class FilterBed extends AbstractFilter {
    private final List<Filter> filters;
    private final File inputBedFile;
    private final File outputBedFile;
    private static final String USAGE = "dsh-filter-bed --score 20.0 -i input.bed.bgz -o output.bed.bgz";


    /**
     * Filter features in BED format.
     *
     * @param filters list of filters, must not be null
     * @param inputBedFile input BED file, if any
     * @param outputBedFile output BED file, if any
     */
    public FilterBed(final List<Filter> filters, final File inputBedFile, final File outputBedFile) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputBedFile = inputBedFile;
        this.outputBedFile = outputBedFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputBedFile);

            final PrintWriter w = writer;
            BedReader.stream(reader(inputBedFile), new BedListener() {
                    @Override
                    public boolean record(final BedRecord record) {
                        // write out record
                        boolean pass = true;
                        for (Filter filter : filters) {
                            pass &= filter.accept(record);
                        }
                        if (pass) {
                            BedWriter.write(record, w);
                        }
                        return true;
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
         * Return true if the specified BED record should be accepted by this filter.
         *
         * @param record BED record
         * @return true if the specified BED record should be accepted by this filter
         */
        boolean accept(BedRecord record);
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
        public boolean accept(final BedRecord record) {
            return chrom.equals(record.getChrom()) &&
                Ranges.intersect(range, Range.closedOpen(record.getStart(), record.getEnd()));
        }
    }

    /**
     * Score filter.
     */
    public static final class ScoreFilter implements Filter {
        /** Score. */
        private final double score;

        /**
         * Create a new score filter with the specified score.
         *
         * @param score score
         */
        public ScoreFilter(final double score) {
            this.score = score;
        }

        @Override
        public boolean accept(final BedRecord record) {
            return record.getScore() != null && Double.valueOf(record.getScore()) > score;
        }
    }

    /**
     * Script filter.
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
        public boolean accept(final BedRecord record) {
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
        StringArgument rangeFilter = new StringArgument("r", "range", "filter by range, specify as chrom:start-end in 0-based coordindates", false);
        IntegerArgument scoreFilter = new IntegerArgument("s", "score", "filter by score", false);
        StringArgument scriptFilter = new StringArgument("e", "script", "filter by script, eval against r", false);
        FileArgument inputBedFile = new FileArgument("i", "input-bed-file", "input BED file, default stdin", false);
        FileArgument outputBedFile = new FileArgument("o", "output-bed-file", "output BED file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, rangeFilter, scoreFilter, scriptFilter, inputBedFile, outputBedFile);
        CommandLine commandLine = new CommandLine(args);

        FilterBed filterBed = null;
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
            if (rangeFilter.wasFound()) {
                filters.add(new RangeFilter(rangeFilter.getValue()));
            }
            if (scoreFilter.wasFound()) {
                filters.add(new ScoreFilter(scoreFilter.getValue()));
            }
            if (scriptFilter.wasFound()) {
                filters.add(new ScriptFilter(scriptFilter.getValue()));
            }
            filterBed = new FilterBed(filters, inputBedFile.getValue(), outputBedFile.getValue());
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
            System.exit(filterBed.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
