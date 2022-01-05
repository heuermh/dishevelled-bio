/*

    dsh-bio-feature  Sequence features.
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
package org.dishevelled.bio.feature.bed;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * BED format reader.
 *
 * @author  Michael Heuer
 */
public final class BedReader {

    /**
     * Private no-arg constructor.
     */
    private BedReader() {
        // empty
    }


    /**
     * Read zero or more BED records from the specified readable.
     *
     * @param readable to read from, must not be null
     * @return zero or more BED records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<BedRecord> read(final Readable readable) throws IOException {
        checkNotNull(readable);
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.records();
    }

    /**
     * Stream zero or more BED records from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final BedListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        BedLineProcessor lineProcessor = new BedLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * BED line processor.
     */
    private static final class BedLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** BED listener. */
        private final BedListener listener;


        /**
         * Create a new BED line processor with the specified BED listener.
         *
         * @param listener BED listener, must not be null
         */
        private BedLineProcessor(final BedListener listener) {
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
                return isHeader(line) || listener.record(BedRecord.valueOf(line));
            }
            catch (IllegalArgumentException | NullPointerException e) {
                throw new IOException("could not read BED record at line " + lineNumber + ", caught " + e.getMessage(), e);
            }
        }

        /**
         * Return true if the specified line is a header or comment line in BED format.
         *
         * @param line line
         * @return true if the specified line is a header or comment line in BED format
         */
        private boolean isHeader(final String line) {
            return line.startsWith("#") || line.startsWith("browser") || line.startsWith("track") || "".equals(line);
        }
    }


    /**
     * Collect.
     */
    private static class Collect implements BedListener {
        /** List of collected BED records. */
        private final List<BedRecord> records = new ArrayList<BedRecord>();


        @Override
        public boolean record(final BedRecord record) {
            records.add(record);
            return true;
        }

        /**
         * Return zero or more collected BED records.
         *
         * @return zero or more collected BED records
         */
        Iterable<BedRecord> records() {
            return records;
        }
    }
}
