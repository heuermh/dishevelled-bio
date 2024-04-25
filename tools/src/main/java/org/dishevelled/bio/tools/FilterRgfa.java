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

import java.io.File;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.assembly.gfa1.Containment;
import org.dishevelled.bio.assembly.gfa1.Gfa1Listener;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Gfa1Record;
import org.dishevelled.bio.assembly.gfa1.Gfa1Writer;
import org.dishevelled.bio.assembly.gfa1.Link;
import org.dishevelled.bio.assembly.gfa1.Path;
import org.dishevelled.bio.assembly.gfa1.Reference;
import org.dishevelled.bio.assembly.gfa1.Segment;
import org.dishevelled.bio.assembly.gfa1.Traversal;

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
 * Filter assembly in rGFA format.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
public final class FilterRgfa extends AbstractFilter {
    private final List<Filter> filters;
    private final java.nio.file.Path inputRgfaPath;
    private final File outputRgfaFile;
    private static final String USAGE = "dsh-filter-rgfa --read-count 40 -i input.rGFA.gfa.bgz -o output.rGFA.gfa.bgz";


    /**
     * Filter assembly in rGFA format.
     *
     * @deprecated will be removed in version 3.0
     * @param filters list of filters, must not be null
     * @param inputRgfaFile input rGFA file, if any
     * @param outputRgfaFile output rGFA file, if any
     */
    public FilterRgfa(final List<Filter> filters, final File inputRgfaFile, final File outputRgfaFile) {
        this(filters, inputRgfaFile == null ? null : inputRgfaFile.toPath(), outputRgfaFile);
    }

    /**
     * Filter assembly in rGFA format.
     *
     * @param filters list of filters, must not be null
     * @param inputRgfaPath input rGFA path, if any
     * @param outputRgfaFile output rGFA file, if any
     */
    public FilterRgfa(final List<Filter> filters, final java.nio.file.Path inputRgfaPath, final File outputRgfaFile) {
        checkNotNull(filters);
        this.filters = ImmutableList.copyOf(filters);
        this.inputRgfaPath = inputRgfaPath;
        this.outputRgfaFile = outputRgfaFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputRgfaFile);

            final PrintWriter w = writer;
            Gfa1Reader.stream(reader(inputRgfaPath), new Gfa1Listener() {
                    @Override
                    public boolean record(final Gfa1Record record) {
                        // write out record
                        boolean pass = true;
                        for (Filter filter : filters) {
                            pass &= filter.accept(record);
                        }
                        if (pass) {
                            Gfa1Writer.write(record, w);
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
         * Return true if the specified rGFA record should be accepted by this filter.
         *
         * @param record rGFA record
         * @return true if the specified rGFA record should be accepted by this filter
         */
        boolean accept(Gfa1Record record);
    }

    /**
     * Segment filter.
     */
    public static final class SegmentFilter implements Filter {
        /** Cached segment identifiers. */
        private final Set<String> segmentIds = new HashSet<String>();

        @Override
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Segment) {
                Segment segment = (Segment) record;
                segmentIds.add(segment.getId());
                return true;
            }
            else if (record instanceof Containment) {
                Containment containment = (Containment) record;
                if (!segmentIds.contains(containment.getContainer().getId())) {
                    return false;
                }
                if (!segmentIds.contains(containment.getContained().getId())) {
                    return false;
                }
                return true;
            }
            else if (record instanceof Link) {
                Link link = (Link) record;
                if (!segmentIds.contains(link.getSource().getId())) {
                    return false;
                }
                if (!segmentIds.contains(link.getTarget().getId())) {
                    return false;
                }
                return true;
            }
            else if (record instanceof Path) {
                Path path = (Path) record;
                for (Reference segment : path.getSegments()) {
                    if (!segmentIds.contains(segment.getId())) {
                        return false;
                    }
                }
                return true;
            }
            else if (record instanceof Traversal) {
                Traversal traversal = (Traversal) record;
                if (!segmentIds.contains(traversal.getSource().getId())) {
                    return false;
                }
                if (!segmentIds.contains(traversal.getTarget().getId())) {
                    return false;
                }
                return true;
            }
            return true;
        }
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
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Segment) {
                Segment segment = (Segment) record;
                if (segment.containsLength()) {
                    return segment.getLength() >= length;
                }
                // fall back to sequence length
                if (segment.hasSequence()) {
                    return segment.getSequence().length() >= length;
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Fragment count filter.
     */
    public static final class FragmentCountFilter implements Filter {
        /** Fragment count. */
        private final int fragmentCount;

        /**
         * Create a new fragment count filter with the specified fragment count.
         *
         * @param fragmentCount fragment count
         */
        public FragmentCountFilter(final int fragmentCount) {
            this.fragmentCount = fragmentCount;
        }

        @Override
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Segment) {
                Segment segment = (Segment) record;
                return !segment.containsFragmentCount() && segment.getFragmentCount() >= fragmentCount;
            }
            else if (record instanceof Link) {
                Link link = (Link) record;
                return !link.containsFragmentCount() && link.getFragmentCount() >= fragmentCount;
            }
            return true;
        }
    }

    /**
     * K-mer count filter.
     */
    public static final class KmerCountFilter implements Filter {
        /** K-mer count. */
        private final int kmerCount;

        /**
         * Create a new k-mer count filter with the specified read count.
         *
         * @param kmerCount k-mer count
         */
        public KmerCountFilter(final int kmerCount) {
            this.kmerCount = kmerCount;
        }

        @Override
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Segment) {
                Segment segment = (Segment) record;
                return !segment.containsKmerCount() && segment.getKmerCount() >= kmerCount;
            }
            else if (record instanceof Link) {
                Link link = (Link) record;
                return !link.containsKmerCount() && link.getKmerCount() >= kmerCount;
            }
            return true;
        }
    }

    /**
     * Mapping quality filter.
     */
    public static final class MappingQualityFilter implements Filter {
        /** Mapping quality. */
        private final int mappingQuality;

        /**
         * Create a new mapping quality filter with the specified mapping quality.
         *
         * @param mappingQuality mapping quality
         */
        public MappingQualityFilter(final int mappingQuality) {
            this.mappingQuality = mappingQuality;
        }

        @Override
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Link) {
                Link link = (Link) record;
                return !link.containsMappingQuality() && link.getMappingQuality() >= mappingQuality;
            }
            return true;
        }
    }

    /**
     * Mismatch count filter.
     */
    public static final class MismatchCountFilter implements Filter {
        /** Mismatch count. */
        private final int mismatchCount;

        /**
         * Create a new mismatch count filter with the specified mismatch count.
         *
         * @param mismatchCount mismatch count
         */
        public MismatchCountFilter(final int mismatchCount) {
            this.mismatchCount = mismatchCount;
        }

        @Override
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Link) {
                Link link = (Link) record;
                return !link.containsMismatchCount() && link.getMismatchCount() < mismatchCount;
            }
            return true;
        }
    }

    /**
     * Read count filter.
     */
    public static final class ReadCountFilter implements Filter {
        /** Read count. */
        private final int readCount;

        /**
         * Create a new read count filter with the specified read count.
         *
         * @param readCount read count
         */
        public ReadCountFilter(final int readCount) {
            this.readCount = readCount;
        }

        @Override
        public boolean accept(final Gfa1Record record) {
            if (record instanceof Segment) {
                Segment segment = (Segment) record;
                return !segment.containsReadCount() && segment.getReadCount() >= readCount;
            }
            else if (record instanceof Link) {
                Link link = (Link) record;
                return !link.containsReadCount() && link.getReadCount() >= readCount;
            }
            return true;
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
        public boolean accept(final Gfa1Record record) {
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
        Switch segmentFilter = new Switch("g", "invalid-segment-references", "filter containments, links, and paths that reference missing segments");
        IntegerArgument lengthFilter = new IntegerArgument("n", "length", "filter segments by length", false);
        IntegerArgument fragmentCountFilter = new IntegerArgument("f", "fragment-count", "filter segments and links by fragment count", false);
        IntegerArgument kmerCountFilter = new IntegerArgument("k", "kmer-count", "filter segments and links by k-mer count", false);
        IntegerArgument mappingQualityFilter = new IntegerArgument("m", "mapping-quality", "filter links by mapping quality", false);
        IntegerArgument mismatchCountFilter = new IntegerArgument("s", "mismatch-count", "filter links by mismatch count", false);
        IntegerArgument readCountFilter = new IntegerArgument("r", "read-count", "filter segments and links by read count", false);
        StringArgument scriptFilter = new StringArgument("e", "script", "filter by script, eval against r", false);
        PathArgument inputRgfaPath = new PathArgument("i", "input-rgfa-path", "input rGFA path, default stdin", false);
        FileArgument outputRgfaFile = new FileArgument("o", "output-rgfa-file", "output rGFA file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, segmentFilter, lengthFilter, fragmentCountFilter, kmerCountFilter, mappingQualityFilter, mismatchCountFilter, readCountFilter, scriptFilter, inputRgfaPath, outputRgfaFile);
        CommandLine commandLine = new CommandLine(args);

        FilterRgfa filterRgfa = null;
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
            if (segmentFilter.wasFound()) {
                filters.add(new SegmentFilter());
            }
            if (lengthFilter.wasFound()) {
                filters.add(new LengthFilter(lengthFilter.getValue()));
            }
            if (fragmentCountFilter.wasFound()) {
                filters.add(new FragmentCountFilter(fragmentCountFilter.getValue()));
            }
            if (kmerCountFilter.wasFound()) {
                filters.add(new KmerCountFilter(kmerCountFilter.getValue()));
            }
            if (mappingQualityFilter.wasFound()) {
                filters.add(new MappingQualityFilter(mappingQualityFilter.getValue()));
            }
            if (mismatchCountFilter.wasFound()) {
                filters.add(new MismatchCountFilter(mismatchCountFilter.getValue()));
            }
            if (readCountFilter.wasFound()) {
                filters.add(new ReadCountFilter(readCountFilter.getValue()));
            }
            if (scriptFilter.wasFound()) {
                filters.add(new ScriptFilter(scriptFilter.getValue()));
            }
            filterRgfa = new FilterRgfa(filters, inputRgfaPath.getValue(), outputRgfaFile.getValue());
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
            System.exit(filterRgfa.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
