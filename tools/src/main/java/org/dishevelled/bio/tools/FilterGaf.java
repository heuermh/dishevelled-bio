/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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

import java.io.BufferedReader;
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

import org.dishevelled.bio.alignment.gaf.GafRecord;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Filter alignments in GAF format.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
public final class FilterGaf extends AbstractFilter {
    private final List<Filter> filters;
    private final Path inputGafPath;
    private final File outputGafFile;
    private static final String USAGE = "dsh-filter-gaf --mapping-quality 30 -i input.gaf.bgz -o output.gaf.bgz";


    /**
     * Filter alignments in GAF format.
     *
     * @deprecated will be removed in version 3.0
     * @param filters list of filters, must not be null
     * @param inputGafFile input GAF file, if any
     * @param outputGafFile output GAF file, if any
     */
    public FilterGaf(final List<Filter> filters, final File inputGafFile, final File outputGafFile) {
        this(filters, inputGafFile == null ? null : inputGafFile.toPath(), outputGafFile);
    }

    /**
     * Filter alignments in GAF format.
     *
     * @since 2.1
     * @param filters list of filters, must not be null
     * @param inputGafPath input GAF path, if any
     * @param outputGafFile output GAF file, if any
     */
    public FilterGaf(final List<Filter> filters, final Path inputGafPath, final File outputGafFile) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputGafPath = inputGafPath;
        this.outputGafFile = outputGafFile;
    }


    @Override
    public Integer call() throws Exception {
        int lineNumber = 0;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGafPath);
            writer = writer(outputGafFile);

            while (reader.ready()) {
                String line = reader.readLine();
                GafRecord record = GafRecord.valueOf(line);
                lineNumber++;

                // write out record
                boolean pass = true;
                for (Filter filter : filters) {
                    pass &= filter.accept(record);
                }
                if (pass) {
                    writer.println(record.toString());
                }
            }
            return 0;
        }
        catch (Exception e) {
            throw new Exception("could not read record at line number "
                                + lineNumber + ", caught" + e.getMessage(), e);
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // empty
            }
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
         * Return true if the specified GAF record should be accepted by this filter.
         *
         * @param record GAF record
         * @return true if the specified GAF record should be accepted by this filter
         */
        boolean accept(GafRecord record);
    }

    /**
     * Query range filter.
     */
    public static final class QueryRangeFilter implements Filter {
        /** Query name. */
        private final String queryName;

        /** Range. */
        private final Range<Long> range;

        /** Range format regular expression. */
        private final Pattern RANGE = Pattern.compile("^(.*):([0-9]+)-([0-9]+)$");

        /**
         * Create a new range filter with the specified range format.
         *
         * @param value range format, must not be null
         */
        public QueryRangeFilter(final String value) {
            checkNotNull(value);
            Matcher m = RANGE.matcher(value);
            if (!m.matches()) {
                throw new IllegalArgumentException("invalid range format, expected queryName:start-end in 0-based coordinates");
            }
            this.queryName = m.group(1);
            long start = Long.parseLong(m.group(2));
            long end = Long.parseLong(m.group(3));
            this.range = Range.closedOpen(start, end);
        }

        @Override
        public boolean accept(final GafRecord record) {
            return queryName.equals(record.getQueryName()) && Ranges.intersect(range, Range.closedOpen(record.getQueryStart(), record.getQueryEnd()));
        }
    }

    /**
     * Mapping quality filter.
     */
    public static final class MappingQualityFilter implements Filter {
        /** Mapping quality. */
        private final int mappingQuality;

        /**
         * Create a new mapping quality score filter with the specified mapping quality.
         *
         * @param mappingQuality mapping quality
         */
        public MappingQualityFilter(final int mappingQuality) {
            this.mappingQuality = mappingQuality;
        }

        @Override
        public boolean accept(final GafRecord record) {
            return record.getMappingQuality() >= mappingQuality;
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
        public boolean accept(final GafRecord record) {
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
        StringArgument queryRangeFilter = new StringArgument("r", "query", "filter by query range, specify as queryName:start-end in 0-based coordindates", false);
        IntegerArgument mappingQualityFilter = new IntegerArgument("q", "mapping-quality", "filter by mapping quality", false);
        StringArgument scriptFilter = new StringArgument("e", "script", "filter by script, eval against r", false);
        PathArgument inputGafPath = new PathArgument("i", "input-gaf-path", "input GAF path, default stdin", false);
        FileArgument outputGafFile = new FileArgument("o", "output-gaf-file", "output GAF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, queryRangeFilter, mappingQualityFilter, scriptFilter, inputGafPath, outputGafFile);
        CommandLine commandLine = new CommandLine(args);

        FilterGaf filterGaf = null;
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
            if (queryRangeFilter.wasFound()) {
                filters.add(new QueryRangeFilter(queryRangeFilter.getValue()));
            }
            if (mappingQualityFilter.wasFound()) {
                filters.add(new MappingQualityFilter(mappingQualityFilter.getValue()));
            }
            if (scriptFilter.wasFound()) {
                filters.add(new ScriptFilter(scriptFilter.getValue()));
            }
            filterGaf = new FilterGaf(filters, inputGafPath.getValue(), outputGafFile.getValue());
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
            System.exit(filterGaf.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
