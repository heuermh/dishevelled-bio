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

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.assembly.gfa2.Gfa2Listener;
import org.dishevelled.bio.assembly.gfa2.Gfa2Reader;
import org.dishevelled.bio.assembly.gfa2.Gfa2Record;
import org.dishevelled.bio.assembly.gfa2.Gfa2Writer;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Filter assembly in GFA 2.0 format.
 *
 * @since 1.3
 * @author  Michael Heuer
 */
public final class FilterGfa2 extends AbstractFilter {
    private final List<Filter> filters;
    private final Path inputGfa2Path;
    private final File outputGfa2File;
    private static final String USAGE = "dsh-filter-gfa2 -i input.gfa2.bgz -o output.gfa2.bgz";


    /**
     * Filter assembly in GFA2 format.
     *
     * @deprecated will be removed in version 3.0
     * @param filters list of filters, must not be null
     * @param inputGfa2File input GFA2 file, if any
     * @param outputGfa2File output GFA2 file, if any
     */
    public FilterGfa2(final List<Filter> filters, final File inputGfa2File, final File outputGfa2File) {
        this(filters, inputGfa2File == null ? null : inputGfa2File.toPath(), outputGfa2File);
    }

    /**
     * Filter assembly in GFA2 format.
     *
     * @since 2.1
     * @param filters list of filters, must not be null
     * @param inputGfa2Path input GFA2 path, if any
     * @param outputGfa2File output GFA2 file, if any
     */
    public FilterGfa2(final List<Filter> filters, final Path inputGfa2Path, final File outputGfa2File) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputGfa2Path = inputGfa2Path;
        this.outputGfa2File = outputGfa2File;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputGfa2File);

            final PrintWriter w = writer;
            Gfa2Reader.stream(reader(inputGfa2Path), new Gfa2Listener() {
                    @Override
                    public boolean record(final Gfa2Record record) {
                        // write out record
                        boolean pass = true;
                        for (Filter filter : filters) {
                            pass &= filter.accept(record);
                        }
                        if (pass) {
                            Gfa2Writer.write(record, w);
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
         * Return true if the specified GFA 2.0 record should be accepted by this filter.
         *
         * @param record GFA 2.0 record
         * @return true if the specified GFA 2.0 record should be accepted by this filter
         */
        boolean accept(Gfa2Record record);
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
        public boolean accept(final Gfa2Record record) {
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
        StringArgument scriptFilter = new StringArgument("e", "script", "filter by script, eval against r", false);
        PathArgument inputGfa2Path = new PathArgument("i", "input-gfa2-path", "input GFA 2.0 path, default stdin", false);
        FileArgument outputGfa2File = new FileArgument("o", "output-gfa2-file", "output GFA 2.0 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, scriptFilter, inputGfa2Path, outputGfa2File);
        CommandLine commandLine = new CommandLine(args);

        FilterGfa2 filterGfa2 = null;
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
            if (scriptFilter.wasFound()) {
                filters.add(new ScriptFilter(scriptFilter.getValue()));
            }
            filterGfa2 = new FilterGfa2(filters, inputGfa2Path.getValue(), outputGfa2File.getValue());
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
            System.exit(filterGfa2.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
