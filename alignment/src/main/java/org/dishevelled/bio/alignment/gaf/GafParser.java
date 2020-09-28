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

import java.io.IOException;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * Low-level GAF (graph alignment format) parser.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class GafParser {

    /**
     * Private no-arg constructor.
     */
    private GafParser() {
        // empty
    }


    /**
     * Parse the specified readable.
     *
     * @param readable readable, must not be null
     * @param listener low-level event based parser callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void parse(final Readable readable, final GafParseListener listener) throws IOException {
        checkNotNull(readable);
        GafLineProcessor lineProcessor = new GafLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * GAF line processor.
     */
    private static final class GafLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** GAF parse listener. */
        private final GafParseListener listener;


        /**
         * Create a new GAF line processor.
         *
         * @param listener GAF parse listener
         */
        private GafLineProcessor(final GafParseListener listener) {
            checkNotNull(listener);
            this.listener = listener;
        }


        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean processLine(final String line) throws IOException {
            lineNumber++;
            listener.lineNumber(lineNumber);

            List<String> tokens = Splitter.on("\t").splitToList(line);
            if (tokens.size() < 12) {
                throw new IOException("invalid record at line number " + lineNumber + ", expected 12 or more tokens, found " + tokens.size());
            }

            String queryName = tokens.get(0);
            if (isNotMissingValue(queryName)) {
                listener.queryName(queryName);
            }

            long queryLength = Long.parseLong(tokens.get(1));
            listener.queryLength(queryLength);

            long queryStart = Long.parseLong(tokens.get(2));
            listener.queryStart(queryStart);

            long queryEnd = Long.parseLong(tokens.get(3));
            listener.queryEnd(queryEnd);

            char strand = tokens.get(4).charAt(0);
            listener.strand(strand);

            String pathName = tokens.get(5);
            if (isNotMissingValue(pathName)) {
                listener.pathName(pathName);
            }
            // todo: validate pathName follows grammar

            long pathLength = Long.parseLong(tokens.get(6));
            listener.pathLength(pathLength);

            long pathStart = Long.parseLong(tokens.get(7));
            listener.pathStart(pathStart);

            long pathEnd = Long.parseLong(tokens.get(8));
            listener.pathEnd(pathEnd);

            long matches = Long.parseLong(tokens.get(9));
            listener.matches(matches);

            long alignmentBlockLength = Long.parseLong(tokens.get(10));
            listener.alignmentBlockLength(alignmentBlockLength);

            int mappingQuality = Integer.parseInt(tokens.get(11));
            listener.mappingQuality(mappingQuality);
            
            // All optional fields follow the TAG:TYPE:VALUE format where TAG is a two-character
            // string that matches /[A-Za-z][A-Za-z0-9]/.
            for (String field : tokens.subList(12, tokens.size())) {
                List<String> fieldTokens = Splitter.on(":").splitToList(field);
                if (fieldTokens.size() < 3) {
                    throw new IOException("invalid field at line number " + lineNumber + ", expected 3 tokens, found " + fieldTokens.size());
                }
                String tag = fieldTokens.get(0);
                String type = fieldTokens.get(1);
                String value = fieldTokens.get(2);

                if (isArrayType(type)) {
                    String arrayType = value.substring(0, 1);
                    // note there should be a comma after array type, e.g. ZT:B:i,1,2,3
                    String[] values = value.substring(2).split(",");
                    listener.arrayField(tag, type, arrayType, values);
                }
                else {
                    listener.field(tag, type, value);
                }
            }
            return listener.complete();
        }

        private static boolean isArrayType(final String type) {
            return "B".equals(type);
        }

        private static boolean isNotMissingValue(final String value) {
            return !("*".equals(value));
        }
    }
}
