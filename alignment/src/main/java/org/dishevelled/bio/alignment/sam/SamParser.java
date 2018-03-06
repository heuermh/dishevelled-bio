/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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

import java.io.IOException;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * Low-level SAM parser.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class SamParser {

    /**
     * Private no-arg constructor.
     */
    private SamParser() {
        // empty
    }


    /**
     * Parse the specified readable.
     *
     * @param readable readable, must not be null
     * @param listener low-level event based parser callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void parse(final Readable readable, final SamParseListener listener) throws IOException {
        checkNotNull(readable);
        SamLineProcessor lineProcessor = new SamLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * SAM line processor.
     */
    private static final class SamLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** SAM parse listener. */
        private final SamParseListener listener;


        /**
         * Create a new SAM line processor.
         *
         * @param listener SAM parse listener
         */
        private SamLineProcessor(final SamParseListener listener) {
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

            if (line.startsWith("@")) {
                // todo: header
            }
            else {
                listener.lineNumber(lineNumber);

                List<String> tokens = Splitter.on("\t").splitToList(line);
                if (tokens.size() < 11) {
                    throw new IOException("invalid record at line number " + lineNumber + ", expected 11 or more tokens, found " + tokens.size());
                }

                // QNAME String [!-?A-~]{1,254} Query template NAME
                String qname = tokens.get(0);
                if (isNotMissingValue(qname)) {
                    listener.qname(qname);
                }

                // FLAG Int [0,2^16-1] bitwise FLAG
                int flag = Integer.parseInt(tokens.get(1));
                listener.flag(flag);

                // RNAME String \*|[!-()+-<>-~][!-~]* Reference sequence NAME
                String rname = tokens.get(2);
                if (isNotMissingValue(rname)) {
                    listener.rname(rname);
                }

                // POS Int [0,2^31-1] 1-based leftmost mapping POSition
                int pos = Integer.parseInt(tokens.get(3));
                listener.pos(pos);

                // MAPQ Int [0,28-1] MAPping Quality
                int mapq = Integer.parseInt(tokens.get(4));
                listener.mapq(mapq);

                // CIGAR String \*|([0-9]+[MIDNSHPX=])+ CIGAR string
                String cigar = tokens.get(5);
                if (isNotMissingValue(cigar)) {
                    listener.cigar(cigar);
                }

                // RNEXT String \*|=|[!-()+-<>-~][!-~]* Ref. name of the mate/next read
                String rnext = tokens.get(6);
                if (isNotMissingValue(rnext)) {
                    listener.rnext(rnext);
                }

                // PNEXT Int [0,2^31-1] Position of the mate/next read
                int pnext = Integer.parseInt(tokens.get(7));
                listener.pnext(pnext);

                // TLEN Int [-2^31+1,2^31-1] observed Template LENgth
                int tlen = Integer.parseInt(tokens.get(8));
                listener.tlen(tlen);

                // SEQ String \*|[A-Za-z=.]+ segment SEQuence
                String seq = tokens.get(9);
                if (isNotMissingValue(seq)) {
                    listener.seq(seq);
                }

                // QUAL String [!-~]+ ASCII of Phred-scaled base QUALity+33
                String qual = tokens.get(10);
                if (isNotMissingValue(qual)) {
                    listener.qual(qual);
                }

                // All optional fields follow the TAG:TYPE:VALUE format where TAG is a two-character
                // string that matches /[A-Za-z][A-Za-z0-9]/.
                for (String field : tokens.subList(11, tokens.size() + 1)) {
                    List<String> fieldTokens = Splitter.on(":").splitToList(field);
                    if (fieldTokens.size() < 3) {
                        throw new IOException("invalid field at line number " + lineNumber + ", expected 3 tokens, found " + fieldTokens.size());
                    }
                    String tag = fieldTokens.get(0);
                    String type = fieldTokens.get(1);
                    String value = fieldTokens.get(2);

                    if (isArrayType(type)) {
                        String arrayType = value.substring(0, 1);
                        String[] values = value.substring(1).split(",");
                        listener.arrayField(tag, type, arrayType, values);
                    }
                    else {
                        listener.field(tag, type, value);
                    }
                }
                return listener.complete();
            }
            return true;
        }

        private static boolean isArrayType(final String type) {
            return "B".equals(type);
        }

        private static boolean isNotMissingValue(final String value) {
            return !("*".equals(value));
        }
    }
}
