/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Filter DNA or protein sequences in FASTA format.
 *
 * @since 1.3.3
 * @author  Michael Heuer
 */
public final class FilterFasta extends AbstractFilter {
    private final List<Filter> filters;
    private final Path inputFastaPath;
    private final File outputFastaFile;
    private final String alphabet;
    private final int lineWidth;
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_LINE_WIDTH = 70;
    static final String DESCRIPTION_LINE = "description_line";
    private static final String USAGE = "dsh-filter-fasta --length 2000 -i input.fasta.gz -o output.fasta.gz";


    /**
     * Filter DNA or protein sequences in FASTA format.
     *
     * @param filters list of filters, must not be null
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param lineWidth line width
     */
    public FilterFasta(final List<Filter> filters, final File inputFastaFile, final File outputFastaFile, final int lineWidth) {
        this(filters,
             inputFastaFile == null ? null : inputFastaFile.toPath(),
             outputFastaFile,
             lineWidth);
    }

    /**
     * Filter DNA or protein sequences in FASTA format.
     *
     * @since 2.1
     * @param filters list of filters, must not be null
     * @param inputFastaPath input FASTA path, if any
     * @param outputFastaFile output FASTA file, if any
     * @param lineWidth line width
     */
    public FilterFasta(final List<Filter> filters, final Path inputFastaPath, final File outputFastaFile, final int lineWidth) {
        this(filters, inputFastaPath, outputFastaFile, DEFAULT_ALPHABET, lineWidth);
    }

    /**
     * Filter DNA or protein sequences in FASTA format.
     *
     * @since 2.0
     * @param filters list of filters, must not be null
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     * @param alphabet input FASTA file alphabet { dna, protein }, if any
     * @param lineWidth line width
     */
    public FilterFasta(final List<Filter> filters,
                       final File inputFastaFile,
                       final File outputFastaFile,
                       final String alphabet,
                       final int lineWidth) {

        this(filters,
             inputFastaFile == null ? null : inputFastaFile.toPath(),
             outputFastaFile,
             alphabet,
             lineWidth);
    }

    /**
     * Filter DNA or protein sequences in FASTA format.
     *
     * @since 2.1
     * @param filters list of filters, must not be null
     * @param inputFastaPath input FASTA path, if any
     * @param outputFastaFile output FASTA file, if any
     * @param alphabet input FASTA file alphabet { dna, protein }, if any
     * @param lineWidth line width
     */
    public FilterFasta(final List<Filter> filters,
                       final Path inputFastaPath,
                       final File outputFastaFile,
                       final String alphabet,
                       final int lineWidth) {

        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputFastaPath = inputFastaPath;
        this.outputFastaFile = outputFastaFile;
        this.alphabet = alphabet;
        this.lineWidth = lineWidth;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastaPath);
            writer = writer(outputFastaFile);

            for (SequenceIterator sequences = isProteinAlphabet() ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();
                // write out record
                boolean pass = true;
                for (Filter filter : filters) {
                    pass &= filter.accept(sequence);
                }
                if (pass) {
                    writeSequence(sequence, lineWidth, writer);
                }
            }

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

    boolean isProteinAlphabet() {
        return alphabet != null && (alphabet.equalsIgnoreCase("protein") || alphabet.equalsIgnoreCase("aa"));
    }

    /**
     * Filter.
     */
    interface Filter {

        /**
         * Return true if the specified FASTA sequence record should be accepted by this filter.
         *
         * @param sequence FASTA sequence record
         * @return true if the specified FASTA sequence record should be accepted by this filter
         */
        boolean accept(Sequence sequence);
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
        public boolean accept(final Sequence sequence) {
            return sequence.length() > length;
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
        public boolean accept(final Sequence sequence) {
            try {
                compiledScript.getEngine().put("r", sequence);
                compiledScript.eval();
                return (Boolean) compiledScript.getEngine().get("result");
            }
            catch (ScriptException e) {
                throw new RuntimeException("could not evaluate compiled script, caught " + e.getMessage(), e);
            }
        }
    }

    // copied with mods from biojava-legacy FastaFormat, as it uses PrintStream not PrintWriter
    static String describeSequence(final Sequence sequence) {
        return sequence.getAnnotation().containsProperty(DESCRIPTION_LINE) ?
            (String) sequence.getAnnotation().getProperty(DESCRIPTION_LINE) : (String) sequence.getName();
    }

    static void writeSequence(final Sequence sequence, final int lineWidth, final PrintWriter writer) throws IOException {
        writer.print(">");
        writer.println(describeSequence(sequence));
        for (int i = 1, length = sequence.length(); i <= length; i += lineWidth) {
            writer.println(sequence.subStr(i, Math.min(i + lineWidth - 1, length)));
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
        PathArgument inputFastaPath = new PathArgument("i", "input-fasta-path", "input FASTA path, default stdin", false);
        FileArgument outputFastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);
        StringArgument alphabet = new StringArgument("b", "alphabet", "input FASTA alphabet { dna, protein }, default dna", false);
        IntegerArgument lineWidth = new IntegerArgument("w", "line-width", "line width, default " + DEFAULT_LINE_WIDTH, false);

        ArgumentList arguments = new ArgumentList(about, help, lengthFilter, scriptFilter, inputFastaPath, outputFastaFile, alphabet, lineWidth);
        CommandLine commandLine = new CommandLine(args);

        FilterFasta filterFasta = null;
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
            filterFasta = new FilterFasta(filters, inputFastaPath.getValue(), outputFastaFile.getValue(), alphabet.getValue(DEFAULT_ALPHABET), lineWidth.getValue(DEFAULT_LINE_WIDTH));
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
            System.exit(filterFasta.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
