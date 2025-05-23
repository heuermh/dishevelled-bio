/*

    dsh-bio-alignment  Aligments.
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
 * SAM header reader.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@Immutable
public final class SamHeaderReader {

    /**
     * Private no-arg constructor.
     */
    private SamHeaderReader() {
        // empty
    }


    /**
     * Read the SAM header from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return the SAM header read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final Readable readable) throws IOException {
        checkNotNull(readable);
        try {
            SamHeaderLineProcessor lineProcessor = new SamHeaderLineProcessor();
            CharStreams.readLines(readable, lineProcessor);
            return lineProcessor.getResult();
        }
        catch (IllegalArgumentException e) {
            throw new IOException("could not parse SAM header, caught " + e.getMessage(), e);
        }
    }

    /**
     * Read the SAM header from the specified file.
     *
     * @param file file to read from, must not be null
     * @return the SAM header read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return header(reader);
        }
    }

    /**
     * Read the SAM header from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return the SAM header read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return header(reader);
        }
    }

    /**
     * Read the SAM header from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return the SAM header read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static SamHeader header(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return header(reader);
        }
    }

    /**
     * SAM header line processor.
     */
    private static final class SamHeaderLineProcessor implements LineProcessor<SamHeader> {
        /** Line number. */
        private long lineNumber = 0;

        /** SAM header builder. */
        private final SamHeader.Builder builder = SamHeader.builder();

        @Override
        public SamHeader getResult() {
            return builder.build();
        }

        @Override
        public boolean processLine(final String line) throws IOException
        {
            lineNumber++;
            if (line.startsWith("@")) {
                try {
                    if (line.startsWith("@HD")) {
                        builder.withHeaderLine(SamHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@SQ")) {
                        builder.withSequenceHeaderLine(SamSequenceHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@RG")) {
                        builder.withReadGroupHeaderLine(SamReadGroupHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@PG")) {
                        builder.withProgramHeaderLine(SamProgramHeaderLine.valueOf(line));
                    }
                    else if (line.startsWith("@CO")) {
                        builder.withCommentHeaderLine(SamCommentHeaderLine.valueOf(line));
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
            return false;
        }
    }
}
