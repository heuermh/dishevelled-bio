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
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.SangerFastqWriter;
import org.biojava.bio.program.fastq.StreamListener;

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
 * Filter sequences in FASTQ format.
 *
 * @since 1.3.3
 * @author  Michael Heuer
 */
public final class FilterFastq extends AbstractFilter {
    private final List<Filter> filters;
    private final Path inputFastqPath;
    private final File outputFastqFile;
    private final FastqReader fastqReader = new SangerFastqReader();
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "dsh-filter-fastq --length 2000 -i input.fastq.bgz -o output.fastq.bgz";


    /**
     * Filter sequences in FASTQ format.
     *
     * @deprecated will be removed in version 3.0
     * @param filters list of filters, must not be null
     * @param inputFastqFile input FASTQ file, if any
     * @param outputFastqFile output FASTQ file, if any
     */
    public FilterFastq(final List<Filter> filters, final File inputFastqFile, final File outputFastqFile) {
        this(filters, inputFastqFile == null ? null : inputFastqFile.toPath(), outputFastqFile);
    }

    /**
     * Filter sequences in FASTQ format.
     *
     * @since 2.1
     * @param filters list of filters, must not be null
     * @param inputFastqPath input FASTQ path, if any
     * @param outputFastqFile output FASTQ file, if any
     */
    public FilterFastq(final List<Filter> filters, final Path inputFastqPath, final File outputFastqFile) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputFastqPath = inputFastqPath;
        this.outputFastqFile = outputFastqFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastqPath);
            writer = writer(outputFastqFile);

            final PrintWriter w = writer;
            fastqReader.stream(reader, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        // write out record
                        boolean pass = true;
                        for (Filter filter : filters) {
                            pass &= filter.accept(fastq);
                        }
                        if (pass) {
                            try {
                                fastqWriter.append(w, fastq);
                            }
                            catch (IOException e) {
                                throw new RuntimeException("could not write fastq", e);
                            }
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
         * Return true if the specified FASTQ record should be accepted by this filter.
         *
         * @param fastq FASTQ record
         * @return true if the specified FASTQ record should be accepted by this filter
         */
        boolean accept(Fastq fastq);
    }

    /**
     * Length filter.
     */
    public static final class LengthFilter implements Filter {
        /** Length. */
        private final int length;

        /**
         * Create a new length filter with the specified length.
         *
         * @param length length
         */
        public LengthFilter(final int length) {
            this.length = length;
        }

        @Override
        public boolean accept(final Fastq fastq) {
            return fastq.getSequence().length() > length;
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
        public boolean accept(final Fastq fastq) {
            try {
                compiledScript.getEngine().put("r", fastq);
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
        IntegerArgument lengthFilter = new IntegerArgument("n", "length", "filter by length", false);
        StringArgument scriptFilter = new StringArgument("e", "script", "filter by script, eval against r", false);
        PathArgument inputFastqPath = new PathArgument("i", "input-fastq-path", "input FASTQ path, default stdin", false);
        FileArgument outputFastqFile = new FileArgument("o", "output-fastq-file", "output FASTQ file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, lengthFilter, scriptFilter, inputFastqPath, outputFastqFile);
        CommandLine commandLine = new CommandLine(args);

        FilterFastq filterFastq = null;
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
            if (lengthFilter.wasFound()) {
                filters.add(new LengthFilter(lengthFilter.getValue()));
            }
            if (scriptFilter.wasFound()) {
                filters.add(new ScriptFilter(scriptFilter.getValue()));
            }
            filterFastq = new FilterFastq(filters, inputFastqPath.getValue(), outputFastqFile.getValue());
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
            System.exit(filterFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
