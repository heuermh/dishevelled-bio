/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * Graphical Fragment Assembly (GFA) 2.0 reader.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Gfa2Reader {

    /**
     * Private no-arg constructor.
     */
    private Gfa2Reader() {
        // empty
    }


    /**
     * Read zero or more GFA 2.0 records from the specified readable.
     *
     * @param readable to read from, must not be null
     * @return zero or more GFA 2.0 records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Gfa2Record> read(final Readable readable) throws IOException {
        checkNotNull(readable);
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.records();
    }

    /**
     * Stream GFA 2.0 records if any from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final Gfa2Listener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        Gfa2LineProcessor lineProcessor = new Gfa2LineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * GFA 2.0 line processor.
     */
    private static final class Gfa2LineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** Identifier cache. */
        private final java.util.Set<String> identifiers = new HashSet<String>();

        /** GFA 2.0 listener. */
        private final Gfa2Listener listener;


        /**
         * Create a new GFA 2.0 line processor with the specified GFA 2.0 listener.
         *
         * @param listener GFA 2.0 listener, must not be null
         */
        private Gfa2LineProcessor(final Gfa2Listener listener) {
            checkNotNull(listener);
            this.listener = listener;
        }


        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean processLine(final String line) throws IOException
        {
            try {
                lineNumber++;
                if (!line.isEmpty()) {
                    char c = line.charAt(0);
                    if ('E' == c) {
                        Edge edge = Edge.valueOf(line);
                        if (edge.hasId() && !identifiers.add(edge.getId())) {
                            throw new IllegalArgumentException("duplicate identifier " + edge.getId());
                        }
                        return listener.record(edge);
                    }
                    else if ('F' == c) {
                        return listener.record(Fragment.valueOf(line));
                    }
                    else if ('G' == c) {
                        Gap gap = Gap.valueOf(line);
                        if (gap.hasId() && !identifiers.add(gap.getId())) {
                            throw new IllegalArgumentException("duplicate identifier " + gap.getId());
                        }
                        return listener.record(gap);
                    }
                    else if ('H' == c) {
                        return listener.record(Header.valueOf(line));
                    }
                    else if ('O' == c) {
                        Path path = Path.valueOf(line);
                        if (path.hasId() && !identifiers.add(path.getId())) {
                            throw new IllegalArgumentException("duplicate identifier " + path.getId());
                        }
                        return listener.record(path);
                    }
                    else if ('S' == c) {
                        Segment segment = Segment.valueOf(line);
                        if (!identifiers.add(segment.getId())) {
                            throw new IllegalArgumentException("duplicate identifier " + segment.getId());
                        }
                        return listener.record(segment);
                    }
                    else if ('U' == c) {
                        Set set = Set.valueOf(line);
                        if (set.hasId() && !identifiers.add(set.getId())) {
                            throw new IllegalArgumentException("duplicate identifier " + set.getId());
                        }
                        return listener.record(set);
                    }
                }
                // continue processing blank or unrecognized lines
                return true;
            }
            catch (IllegalArgumentException e) {
                throw new IOException("could not read GFA 2.0 record at line " + lineNumber + ", caught exception: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Collect.
     */
    private static class Collect implements Gfa2Listener {
        /** List of collected GFA 2.0 records. */
        private final List<Gfa2Record> records = new LinkedList<Gfa2Record>();


        @Override
        public boolean record(final Gfa2Record record) {
            records.add(record);
            return true;
        }

        /**
         * Return zero or more collected GFA 2.0 records.
         *
         * @return zero or more collected GFA 2.0 records
         */
        Iterable<Gfa2Record> records() {
            return records;
        }
    }
}
