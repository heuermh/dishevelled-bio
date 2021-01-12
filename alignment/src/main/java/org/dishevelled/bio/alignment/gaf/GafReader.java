/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
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


    /**
     * Read zero or more GAF records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more GAF records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> read(final Readable readable) throws IOException {
        checkNotNull(readable);
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.records();
    }

    /**
     * Read zero or more GAF records from the specified file.
     *
     * @param file file to read from, must not be null
     * @return zero or more GAF records read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> read(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return read(reader);
        }
    }

    /**
     * Read zero or more GAF records from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return zero or more GAF records read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> read(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return read(reader);
        }
    }

    /**
     * Read zero or more GAF records from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more GAF records read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GafRecord> read(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return read(reader);
        }
    }

    /**
     * Stream GAF records if any from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final GafListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        GafLineProcessor lineProcessor = new GafLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * Stream GAF records if any from the specified file.
     *
     * @param file file to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final File file, final GafListener listener) throws IOException {
        checkNotNull(file);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            stream(reader, listener);
        }
    }

    /**
     * Stream GAF records if any from the specified URL.
     *
     * @param url URL to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final URL url, final GafListener listener) throws IOException {
        checkNotNull(url);
        checkNotNull(listener);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            stream(reader, listener);
        }
    }

    /**
     * Stream GAF records if any from the specified input stream.
     *
     * @param inputStream input stream to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final InputStream inputStream, final GafListener listener) throws IOException {
        checkNotNull(inputStream);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            stream(reader, listener);
        }
    }


    /**
     * GAF line processor.
     */
    private static final class GafLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** GAF listener. */
        private final GafListener listener;


        /**
         * Create a new GAF line processor with the specified GAF listener.
         *
         * @param listener GAF listener, must not be null
         */
        private GafLineProcessor(final GafListener listener) {
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
                    return listener.record(GafRecord.valueOf(line));
                }
                // continue processing blank lines
                return true;
            }
            catch (IllegalArgumentException e) {
                throw new IOException("could not read GAF record at line " + lineNumber + ", caught exception: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Collect.
     */
    private static class Collect implements GafListener {
        /** List of collected GAF records. */
        private final List<GafRecord> records = new ArrayList<GafRecord>();


        @Override
        public boolean record(final GafRecord record) {
            records.add(record);
            return true;
        }

        /**
         * Return zero or more collected GAF records.
         *
         * @return zero or more collected GAF records
         */
        Iterable<GafRecord> records() {
            return records;
        }
    }
}
