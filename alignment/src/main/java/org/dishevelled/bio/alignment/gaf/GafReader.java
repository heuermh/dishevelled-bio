/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.gaf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Charsets;

import com.google.common.io.Resources;

/**
 * GAF (graph alignment format) reader.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class GafReader {

    /**
     * Private no-arg constructor.
     */
    private GafReader() {
        // empty
    }


    // callback methods

    /**
     * Parse the specified readable.
     *
     * @param readable readable to parse, must not be null
     * @param listener low-level event based parser callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void parse(final Readable readable, final GafParseListener listener) throws IOException {
        GafParser.parse(readable, listener);
    }

    /**
     * Stream the specified readable.
     *
     * @param readable readable to stream, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final GafStreamListener listener) throws IOException {
        StreamingGafParser.stream(readable, listener);
    }

    /**
     * Read zero or more GAF records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more GAF records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> records(final Readable readable) throws IOException {
        Collect collect = new Collect();
        StreamingGafParser.stream(readable, collect);
        return collect.getRecords();
    }

    /**
     * Collect.
     */
    private static final class Collect extends GafStreamAdapter {
        /** Arbitrary large capacity for list of GAF records. */
        private static final int CAPACITY = 10000000;

        /** List of GAF records. */
        private final List<GafRecord> records = new ArrayList<GafRecord>(CAPACITY);

        @Override
        public void record(final GafRecord record) {
            records.add(record);
        }

        /**
         * Return the list of GAF records.
         *
         * @return the list of GAF records
         */
        List<GafRecord> getRecords() {
            return records;
        }
    }

    /**
     * Read zero or more GAF records from the specified file.
     *
     * @param file file to read from, must not be null
     * @return zero or more GAF records read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> records(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return records(reader);
        }
    }

    /**
     * Read zero or more GAF records from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return zero or more GAF records read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> records(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return records(reader);
        }
    }

    /**
     * Read zero or more GAF records from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more GAF records read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> records(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return records(reader);
        }
    }
}