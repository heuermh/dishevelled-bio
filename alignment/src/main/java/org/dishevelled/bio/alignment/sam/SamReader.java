/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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
package org.dishevelled.bio.alignment.sam;

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
 * SAM reader.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class SamReader {

    /**
     * Private no-arg constructor.
     */
    private SamReader() {
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
    public static void parse(final Readable readable, final SamParseListener listener) throws IOException {
        SamParser.parse(readable, listener);
    }

    /**
     * Stream the specified readable.
     *
     * @param readable readable to stream, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final SamStreamListener listener) throws IOException {
        StreamingSamParser.stream(readable, listener);
    }


    // collect methods

    /**
     * Read the SAM header from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return the SAM header read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    //public static SamHeader header(final Readable readable) throws IOException {
    //    return SamHeaderParser.header(readable);
    //}

    /**
     * Read zero or more SAM records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more SAM records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<SamRecord> records(final Readable readable) throws IOException {
        //return SamRecordParser.records(readable);
        Collect collect = new Collect();
        StreamingSamParser.stream(readable, collect);
        return collect.getRecords();
    }

    /**
     * Collect.
     */
    private static final class Collect implements SamStreamListener {
        /** Arbitrary large capacity for list of SAM records. */
        private static final int CAPACITY = 10000000;

        /** List of SAM records. */
        private final List<SamRecord> records = new ArrayList<SamRecord>(CAPACITY);

        @Override
        public void record(final SamRecord record) {
            records.add(record);
        }

        /**
         * Return the list of SAM records.
         *
         * @return the list of SAM records
         */
        List<SamRecord> getRecords() {
            return records;
        }
    }

    // convenience methods

    /**
     * Read the SAM header from the specified file.
     *
     * @param file file to read from, must not be null
     * @return the SAM header read from the specified file
     * @throws IOException if an I/O error occurs
     */
    //public static SamHeader header(final File file) throws IOException {
    //    checkNotNull(file);
    //    // could also use Files.asCharSource(file, Charsets.UTF_8).openBufferedStream()
    //    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    //        return header(reader);
    //    }
    //}

    /**
     * Read the SAM header from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return the SAM header read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    //public static SamHeader header(final URL url) throws IOException {
    //    checkNotNull(url);
    //    try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
    //        return header(reader);
    //    }
    //}

    /**
     * Read the SAM header from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return the SAM header read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    //public static SamHeader header(final InputStream inputStream) throws IOException {
    //    checkNotNull(inputStream);
    //    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
    //        return header(reader);
    //    }
    //}

    /**
     * Read zero or more SAM records from the specified file.
     *
     * @param file file to read from, must not be null
     * @return zero or more SAM records read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<SamRecord> records(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return records(reader);
        }
    }

    /**
     * Read zero or more SAM records from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return zero or more SAM records read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<SamRecord> records(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return records(reader);
        }
    }

    /**
     * Read zero or more SAM records from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more SAM records read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<SamRecord> records(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return records(reader);
        }
    }
}
