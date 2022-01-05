/*

    dsh-bio-alignment  Aligments.
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

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

/**
 * SAM reader.
 *
 * @since 2.0
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


    // delegate to SamHeaderReader

    /**
     * Read the SAM header from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return the SAM header read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final Readable readable) throws IOException {
        return SamHeaderReader.header(readable); 
   }

    /**
     * Read the SAM header from the specified file.
     *
     * @param file file to read from, must not be null
     * @return the SAM header read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final File file) throws IOException {
        return SamHeaderReader.header(file);
    }

    /**
     * Read the SAM header from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return the SAM header read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final URL url) throws IOException {
        return SamHeaderReader.header(url);
    }

    /**
     * Read the SAM header from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return the SAM header read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final InputStream inputStream) throws IOException {
        return SamHeaderReader.header(inputStream);
    }

    // collect records-only

    /**
     * Read zero or more SAM records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more SAM records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<SamRecord> records(final Readable readable) throws IOException {
        Collect collect = new Collect();
        streamRecords(readable, collect);
        return collect.records();
    }

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

    // stream callback, header and records

    /**
     * Stream SAM header and records if any from the specified readable.
     *
     * @param readable readable to stream, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final SamListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        SamLineProcessor lineProcessor = new SamLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * Stream SAM header and records if any from the specified file.
     *
     * @param file file to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final File file, final SamListener listener) throws IOException {
        checkNotNull(file);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            stream(reader, listener);
        }
    }

    /**
     * Stream SAM header and records if any from the specified URL.
     *
     * @param url URL to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final URL url, final SamListener listener) throws IOException {
        checkNotNull(url);
        checkNotNull(listener);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            stream(reader, listener);
        }
    }

    /**
     * Stream SAM header and records if any from the specified input stream.
     *
     * @param inputStream input stream to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final InputStream inputStream, final SamListener listener) throws IOException {
        checkNotNull(inputStream);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            stream(reader, listener);
        }
    }

    // stream records-only

    /**
     * Stream SAM records if any from the specified readable.
     *
     * @param readable readable to stream, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamRecords(final Readable readable, final SamListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        SamRecordLineProcessor lineProcessor = new SamRecordLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * Stream SAM records if any from the specified file.
     *
     * @param file file to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamRecords(final File file, final SamListener listener) throws IOException {
        checkNotNull(file);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            streamRecords(reader, listener);
        }
    }

    /**
     * Stream SAM header and records if any from the specified URL.
     *
     * @param url URL to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamRecords(final URL url, final SamListener listener) throws IOException {
        checkNotNull(url);
        checkNotNull(listener);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            streamRecords(reader, listener);
        }
    }

    /**
     * Stream SAM records if any from the specified input stream.
     *
     * @param inputStream input stream to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamRecords(final InputStream inputStream, final SamListener listener) throws IOException {
        checkNotNull(inputStream);
        checkNotNull(listener);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            streamRecords(reader, listener);
        }
    }

    /**
     * SAM record line processor.
     */
    private static final class SamRecordLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** SAM listener. */
        private final SamListener listener;


        /**
         * Create a new SAM record line processor with the specified SAM listener.
         *
         * @param listener SAM listener, must not be null
         */
        private SamRecordLineProcessor(final SamListener listener) {
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
            lineNumber++;
            // process records only
            if (!line.startsWith("@")) {
                try {
                    return listener.record(SamRecord.valueOf(line));
                }
                catch (IllegalArgumentException e) {
                    throw new IOException("could not read SAM record at line " + lineNumber + ", caught exception: " + e.getMessage(), e);
                }
            }
            return true;
        }
    }

    /**
     * SAM line processor.
     */
    private static final class SamLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** True if the header is complete. */
        private boolean headerComplete = false;

        /** SAM listener. */
        private final SamListener listener;

        /** SAM header builder. */
        private final SamHeader.Builder headerBuilder = SamHeader.builder();


        /**
         * Create a new SAM line processor with the specified SAM listener.
         *
         * @param listener SAM listener, must not be null
         */
        private SamLineProcessor(final SamListener listener) {
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
            lineNumber++;

            // continue processing blank lines
            if (line.isEmpty()) {
                return true;
            }

            // process as header line
            if (line.startsWith("@")) {
                try {
                    if (line.startsWith("@HD")) {
                        headerBuilder.withHeaderLine(SamHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@SQ")) {
                        headerBuilder.withSequenceHeaderLine(SamSequenceHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@RG")) {
                        headerBuilder.withReadGroupHeaderLine(SamReadGroupHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@PG")) {
                        headerBuilder.withProgramHeaderLine(SamProgramHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@CO")) {
                        headerBuilder.withCommentHeaderLine(SamCommentHeaderLine.valueOf(line));
                    }
                    else {
                        String key = line.substring(0, Math.min(3, line.length()));
                        throw new IOException("found invalid SAM header line key " + key + " at line number " + lineNumber);
                    }
                    return true;
                }
                catch (IllegalArgumentException e) {
                    throw new IOException("could not parse SAM header line at line number " + lineNumber + ", caught " + e.getMessage(), e);
                }
            }
            // or as record
            else {
                if (!headerComplete) {
                    try {
                        if (!listener.header(headerBuilder.build())) {
                            return false;
                        }
                    }
                    catch (IllegalArgumentException e) {
                        throw new IOException("could not parse SAM header, caught " + e.getMessage(), e);
                    }
                    headerComplete = true;
                }
                try {
                    return listener.record(SamRecord.valueOf(line));
                }
                catch (IllegalArgumentException e) {
                    throw new IOException("could not read SAM record at line " + lineNumber + ", caught exception: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Collect.
     */
    private static final class Collect extends SamAdapter {
        /** List of SAM records. */
        private final List<SamRecord> records = new ArrayList<SamRecord>();

        @Override
        public boolean record(final SamRecord record) {
            records.add(record);
            return true;
        }

        /**
         * Return the list of SAM records.
         *
         * @return the list of SAM records
         */
        List<SamRecord> records() {
            return records;
        }
    }
}
