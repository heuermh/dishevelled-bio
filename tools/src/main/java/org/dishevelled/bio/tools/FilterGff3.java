/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import org.dishevelled.bio.feature.gff3.Gff3Listener;
import org.dishevelled.bio.feature.gff3.Gff3Reader;
import org.dishevelled.bio.feature.gff3.Gff3Record;
import org.dishevelled.bio.feature.gff3.Gff3Writer;

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
 * Filter features in GFF3 format.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class FilterGff3 extends AbstractFilter {
    private final List<Filter> filters;
    private final File inputGff3File;
    private final File outputGff3File;
    private static final String USAGE = "dsh-filter-gff3 --score 20.0 -i input.gff3.bgz -o output.gff3.bgz";


    /**
     * Filter features in GFF3 format.
     *
     * @param filters list of filters, must not be null
     * @param inputGff3File input GFF3 file, if any
     * @param outputGff3File output GFF3 file, if any
     */
    public FilterGff3(final List<Filter> filters, final File inputGff3File, final File outputGff3File) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputGff3File = inputGff3File;
        this.outputGff3File = outputGff3File;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputGff3File);

            final PrintWriter w = writer;
            Gff3Reader.stream(reader(inputGff3File), new Gff3Listener() {
                    @Override
                    public boolean record(final Gff3Record record) {
                        // write out record
                        boolean pass = true;
                        for (Filter filter : filters) {
                            pass &= filter.accept(record);
                        }
                        if (pass) {
                            Gff3Writer.write(record, w);
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
         * Return true if the specified GFF3 record should be accepted by this filter.
         *
         * @param record GFF3 record
         * @return true if the specified GFF3 record should be accepted by this filter
         */
        boolean accept(Gff3Record record);
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
        public boolean accept(final Gff3Record record) {
            return chrom.equals(record.getSeqid()) &&
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
        public boolean accept(final Gff3Record record) {
            return record.getScore() != null && record.getScore() > score;
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
        public boolean accept(final Gff3Record record) {
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
        FileArgument inputGff3File = new FileArgument("i", "input-gff3-file", "input GFF3 file, default stdin", false);
        FileArgument outputGff3File = new FileArgument("o", "output-gff3-file", "output GFF3 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, rangeFilter, scoreFilter, scriptFilter, inputGff3File, outputGff3File);
        CommandLine commandLine = new CommandLine(args);

        FilterGff3 filterGff3 = null;
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
            filterGff3 = new FilterGff3(filters, inputGff3File.getValue(), outputGff3File.getValue());
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
            System.exit(filterGff3.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
