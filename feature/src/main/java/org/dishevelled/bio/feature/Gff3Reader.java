/*

    dsh-bio-feature  Sequence features.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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
package org.dishevelled.bio.feature;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * GFF3 format reader.
 *
 * @author  Michael Heuer
 */
public final class Gff3Reader {

    /**
     * Private no-arg constructor.
     */
    private Gff3Reader() {
        // empty
    }


    /**
     * Read zero or more GFF3 records from the specified readable.
     *
     * @param readable to read from, must not be null
     * @return zero or more GFF3 records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Gff3Record> read(final Readable readable) throws IOException {
        checkNotNull(readable);
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.records();
    }

    /**
     * Stream zero or more GFF3 records from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final Gff3Listener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        Gff3LineProcessor lineProcessor = new Gff3LineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * GFF3 line processor.
     */
    private static final class Gff3LineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** GFF3 listener. */
        private final Gff3Listener listener;


        /**
         * Create a new GFF3 line processor with the specified GFF3 listener.
         *
         * @param listener GFF3 listener, must not be null
         */
        private Gff3LineProcessor(final Gff3Listener listener) {
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
                return isHeader(line) ||  listener.record(Gff3Record.valueOf(line));
            }
            catch (IllegalArgumentException | NullPointerException e) {
                throw new IOException("could not read GFF3 record at line " + lineNumber + ", caught " + e.getMessage(), e);
            }
        }

        /**
         * Return true if the specified line is a header or comment line in GFF3 format.
         *
         * @param line line
         * @return true if the specified line is a header or comment line in GFF3 format
         */
        private boolean isHeader(final String line) {
            return line.startsWith("#") || "".equals(line);
        }
    }


    /**
     * Collect.
     */
    private static class Collect implements Gff3Listener {
        /** List of collected GFF3 records. */
        private final List<Gff3Record> records = new LinkedList<Gff3Record>();


        @Override
        public boolean record(final Gff3Record record) {
            records.add(record);
            return true;
        }

        /**
         * Return zero or more collected GFF3 records.
         *
         * @return zero or more collected GFF3 records
         */
        Iterable<Gff3Record> records() {
            return records;
        }
    }
}
