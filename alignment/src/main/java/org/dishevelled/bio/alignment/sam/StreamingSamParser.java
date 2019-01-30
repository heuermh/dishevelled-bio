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
                /** SAM record builder. */
                private final SamRecord.Builder builder = SamRecord.builder();

                @Override
                public void lineNumber(final long lineNumber) {
                    builder.withLineNumber(lineNumber);
                }

                @Override
                public void qname(final String qname) {
                    builder.withQname(qname);
                }

                @Override
                public void flag(final int flag) {
                    builder.withFlag(flag);
                }

                @Override
                public void rname(final String rname) {
                    builder.withRname(rname);
                }

                @Override
                public void pos(final int pos) {
                    builder.withPos(pos);
                }

                @Override
                public void mapq(final int mapq) {
                    builder.withMapq(mapq);
                }

                @Override
                public void cigar(final String cigar) {
                    builder.withCigar(cigar);
                }

                @Override
                public void rnext(final String rnext) {
                    builder.withRnext(rnext);
                }

                @Override
                public void pnext(final int pnext) {
                    builder.withPnext(pnext);
                }

                @Override
                public void tlen(final int tlen) {
                    builder.withTlen(tlen);
                }

                @Override
                public void seq(final String seq) {
                    builder.withSeq(seq);
                }

                @Override
                public void qual(final String qual) {
                    builder.withQual(qual);
                }

                @Override
                public void field(final String tag, final String type, final String value) {
                    builder.withField(tag, type, value);
                }

                @Override
                public void arrayField(final String tag, final String type, final String arrayType, final String... values) {
                    builder.withArrayField(tag, type, arrayType, values);
                }

                @Override
                public boolean complete() {
                    listener.record(builder.build());
                    builder.reset();
                    return true;
                }
            });
    }
}
