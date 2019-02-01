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

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

/**
 * Streaming SAM parser.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class StreamingSamParser {

    /**
     * Private no-arg constructor.
     */
    private StreamingSamParser() {
        // empty
    }

    /**
     * Stream the specified readable.
     *
     * @param readable readable, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final SamStreamListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        SamParser.parse(readable, new SamParseAdapter() {
                /** Line number. */
                private long lineNumber = 0L;

                /** True if the header is complete. */
                private boolean headerComplete = false;

                /** SAM header builder. */
                private final SamHeader.Builder headerBuilder = SamHeader.builder();

                /** SAM record builder. */
                private final SamRecord.Builder recordBuilder = SamRecord.builder();

                @Override
                public void lineNumber(final long lineNumber) {
                    this.lineNumber = lineNumber;
                    recordBuilder.withLineNumber(lineNumber);
                }

                @Override
                public void headerLine(final String headerLine) throws IOException {
                    try {
                        if (headerLine.startsWith("@HD")) {
                            headerBuilder.withHeaderLine(SamHeaderLine.valueOf(headerLine));
                        }
                        else if (headerLine.startsWith("@SQ")) {
                            headerBuilder.withSequenceHeaderLine(SamSequenceHeaderLine.valueOf(headerLine));
                        }
                        else if (headerLine.startsWith("@RG")) {
                            headerBuilder.withReadGroupHeaderLine(SamReadGroupHeaderLine.valueOf(headerLine));
                        }
                        else if (headerLine.startsWith("@PG")) {
                            headerBuilder.withProgramHeaderLine(SamProgramHeaderLine.valueOf(headerLine));
                        }
                        else if (headerLine.startsWith("@CO")) {
                            headerBuilder.withCommentHeaderLine(SamCommentHeaderLine.valueOf(headerLine));
                        }
                        else {
                            String tag = headerLine.substring(0, Math.min(3, headerLine.length()));
                            throw new IOException("found invalid SAM header line tag " + tag + " at line number " + lineNumber);
                        }
                    }
                    catch (IllegalArgumentException e) {
                        throw new IOException("could not parse SAM header line at line number " + lineNumber + ", caught " + e.getMessage(), e);
                    }
                }

                @Override
                public void qname(final String qname) {
                    recordBuilder.withQname(qname);
                }

                @Override
                public void flag(final int flag) {
                    recordBuilder.withFlag(flag);
                }

                @Override
                public void rname(final String rname) {
                    recordBuilder.withRname(rname);
                }

                @Override
                public void pos(final int pos) {
                    recordBuilder.withPos(pos);
                }

                @Override
                public void mapq(final int mapq) {
                    recordBuilder.withMapq(mapq);
                }

                @Override
                public void cigar(final String cigar) {
                    recordBuilder.withCigar(cigar);
                }

                @Override
                public void rnext(final String rnext) {
                    recordBuilder.withRnext(rnext);
                }

                @Override
                public void pnext(final int pnext) {
                    recordBuilder.withPnext(pnext);
                }

                @Override
                public void tlen(final int tlen) {
                    recordBuilder.withTlen(tlen);
                }

                @Override
                public void seq(final String seq) {
                    recordBuilder.withSeq(seq);
                }

                @Override
                public void qual(final String qual) {
                    recordBuilder.withQual(qual);
                }

                @Override
                public void field(final String tag, final String type, final String value) {
                    recordBuilder.withField(tag, type, value);
                }

                @Override
                public void arrayField(final String tag, final String type, final String arrayType, final String... values) {
                    recordBuilder.withArrayField(tag, type, arrayType, values);
                }

                @Override
                public boolean complete() throws IOException {
                    if (!headerComplete) {
                        try {
                            listener.header(headerBuilder.build());
                        }
                        catch (IllegalArgumentException e) {
                            throw new IOException("could not parse SAM header, caught " + e.getMessage(), e);
                        }
                        headerComplete = true;
                    }

                    listener.record(recordBuilder.build());
                    recordBuilder.reset();
                    return true;
                }
            });
    }
}
