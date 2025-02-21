/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa1;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * Graphical Fragment Assembly (GFA) 1.0 reader.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Gfa1Reader {

    /**
     * Private no-arg constructor.
     */
    private Gfa1Reader() {
        // empty
    }


    /**
     * Read zero or more GFA 1.0 records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more GFA 1.0 records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Gfa1Record> read(final Readable readable) throws IOException {
        checkNotNull(readable);
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.records();
    }

    /**
     * Read zero or more GFA 1.0 header records from the specified readable. Only header
     * records at the head of the readable (i.e. those before any containment, link, path,
     * segment, or traversal records) will be returned.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more GFA 1.0 header records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Header> header(final Readable readable) throws IOException {
        checkNotNull(readable);
        Headers headers = new Headers();
        stream(readable, headers);
        return headers.headers();
    }

    /**
     * Read zero or more GFA 1.0 segment records from the specified readable. Only segment
     * records at the head of the readable (i.e. those before any containment, link, path,
     * or traversal records) will be returned.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more GFA 1.0 segment records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Segment> segments(final Readable readable) throws IOException {
        checkNotNull(readable);
        Segments segments = new Segments();
        stream(readable, segments);
        return segments.segments();
    }

    /**
     * Stream GFA 1.0 records if any from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final Gfa1Listener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        Gfa1LineProcessor lineProcessor = new Gfa1LineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }


    /**
     * GFA 1.0 line processor.
     */
    private static final class Gfa1LineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** GFA 1.0 listener. */
        private final Gfa1Listener listener;


        /**
         * Create a new GFA 1.0 line processor with the specified GFA 1.0 listener.
         *
         * @param listener GFA 1.0 listener, must not be null
         */
        private Gfa1LineProcessor(final Gfa1Listener listener) {
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
                    if ('C' == c) {
                        return listener.record(Containment.valueOf(line));
                    }
                    else if ('H' == c) {
                        return listener.record(Header.valueOf(line));
                    }
                    else if ('L' == c) {
                        return listener.record(Link.valueOf(line));
                    }
                    else if ('P' == c) {
                        return listener.record(Path.valueOf(line));
                    }
                    else if ('S' == c) {
                        return listener.record(Segment.valueOf(line));
                    }
                    else if ('t' == c) {
                        return listener.record(Traversal.valueOf(line));
                    }
                }
                // continue processing blank or unrecognized lines
                return true;
            }
            catch (IllegalArgumentException e) {
                throw new IOException("could not read GFA 1.0 record at line " + lineNumber + ", caught exception: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Collect.
     */
    private static class Collect implements Gfa1Listener {
        /** List of collected GFA 1.0 records. */
        private final List<Gfa1Record> records = new ArrayList<Gfa1Record>(100000);


        @Override
        public boolean record(final Gfa1Record record) {
            records.add(record);
            return true;
        }

        /**
         * Return zero or more collected GFA 1.0 records.
         *
         * @return zero or more collected GFA 1.0 records
         */
        Iterable<Gfa1Record> records() {
            return records;
        }
    }

    /**
     * Headers.
     */
    private static class Headers extends Gfa1Adapter {
        /** List of collected GFA 1.0 header records. */
        private final List<Header> headers = new ArrayList<Header>();

        @Override
        protected boolean containment(final Containment containment) {
            return false;
        }

        @Override
        protected boolean header(final Header header) {
            headers.add(header);
            return true;
        }

        @Override
        protected boolean path(final Path path) {
            return false;
        }

        @Override
        protected boolean link(final Link link) {
            return false;
        }

        @Override
        protected boolean segment(final Segment segment) {
            return false;
        }

        @Override
        protected boolean traversal(final Traversal traversal) {
            return false;
        }

        /**
         * Return zero or more collected GFA 1.0 header records.
         *
         * @return zero or more collected GFA 1.0 header records
         */
        Iterable<Header> headers() {
            return headers;
        }
    }

    /**
     * Segments.
     */
    private static class Segments extends Gfa1Adapter {
        /** List of collected GFA 1.0 segment records. */
        private final List<Segment> segments = new ArrayList<Segment>();

        @Override
        protected boolean containment(final Containment containment) {
            return false;
        }

        @Override
        protected boolean path(final Path path) {
            return false;
        }

        @Override
        protected boolean link(final Link link) {
            return false;
        }

        @Override
        protected boolean segment(final Segment segment) {
            segments.add(segment);
            return true;
        }

        @Override
        protected boolean traversal(final Traversal traversal) {
            return false;
        }

        /**
         * Return zero or more collected GFA 1.0 segment records.
         *
         * @return zero or more collected GFA 1.0 segment records
         */
        Iterable<Segment> segments() {
            return segments;
        }
    }
}
