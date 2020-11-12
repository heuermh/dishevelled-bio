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
package org.dishevelled.bio.alignment.paf;

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
 * PAF (a Pairwise mApping Format) reader.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class PafReader {

    /**
     * Private no-arg constructor.
     */
    private PafReader() {
        // empty
    }


    /**
     * Read zero or more PAF records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more PAF records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<PafRecord> read(final Readable readable) throws IOException {
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.records();
    }

    /**
     * Read zero or more PAF records from the specified file.
     *
     * @param file file to read from, must not be null
     * @return zero or more PAF records read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<PafRecord> read(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return read(reader);
        }
    }

    /**
     * Read zero or more PAF records from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return zero or more PAF records read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<PafRecord> read(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return read(reader);
        }
    }

    /**
     * Read zero or more PAF records from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more PAF records read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<PafRecord> read(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return read(reader);
        }
    }

    /**
     * Stream zero or more PAF records from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final PafListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        PafLineProcessor lineProcessor = new PafLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * Stream zero or more PAF records from the specified file.
     *
     * @param file file to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final File file, final PafListener listener) throws IOException {
        checkNotNull(file);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            stream(reader, listener);
        }
    }

    /**
     * Stream zero or more PAF records from the specified URL.
     *
     * @param url URL to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final URL url, final PafListener listener) throws IOException {
        checkNotNull(url);
        checkNotNull(listener);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            stream(reader, listener);
        }
    }

    /**
     * Stream zero or more PAF records from the specified input stream.
     *
     * @param inputStream input stream to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final InputStream inputStream, final PafListener listener) throws IOException {
        checkNotNull(inputStream);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            stream(inputStream, listener);
        }
    }


    /**
     * PAF line processor.
     */
    private static final class PafLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** PAF listener. */
        private final PafListener listener;


        /**
         * Create a new PAF line processor with the specified PAF listener.
         *
         * @param listener PAF listener, must not be null
         */
        private PafLineProcessor(final PafListener listener) {
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
                if (line != null && !line.isEmpty()) {
                    return listener.record(PafRecord.valueOf(line));
                }
                // continue processing blank lines
                return true;
            }
            catch (IllegalArgumentException e) {
                throw new IOException("could not read PAF record at line " + lineNumber + ", caught exception: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Collect.
     */
    private static class Collect implements PafListener {
        /** List of collected PAF records. */
        private final List<PafRecord> records = new ArrayList<PafRecord>(100000);


        @Override
        public boolean record(final PafRecord record) {
            records.add(record);
            return true;
        }

        /**
         * Return zero or more collected PAF records.
         *
         * @return zero or more collected PAF records
         */
        Iterable<PafRecord> records() {
            return records;
        }
    }
}
