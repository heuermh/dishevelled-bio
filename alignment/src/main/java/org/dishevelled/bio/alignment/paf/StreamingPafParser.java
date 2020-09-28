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

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

/**
 * Streaming PAF (a Pairwise mApping Format) parser.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class StreamingPafParser {

    /**
     * Private no-arg constructor.
     */
    private StreamingPafParser() {
        // empty
    }

    /**
     * Stream the specified readable.
     *
     * @param readable readable, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final PafStreamListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        PafParser.parse(readable, new PafParseAdapter() {
                /** Line number. */
                private long lineNumber = 0L;

                /** PAF record builder. */
                private final PafRecord.Builder recordBuilder = PafRecord.builder();

                @Override
                public void lineNumber(final long lineNumber) {
                    this.lineNumber = lineNumber;
                    recordBuilder.withLineNumber(lineNumber);
                }

                @Override
                public void queryName(final String queryName) throws IOException {
                    recordBuilder.withQueryName(queryName);
                }

                @Override
                public void queryLength(final long queryLength) throws IOException {
                    recordBuilder.withQueryLength(queryLength);
                }

                @Override
                public void queryStart(final long queryStart) throws IOException {
                    recordBuilder.withQueryStart(queryStart);
                }

                @Override
                public void queryEnd(final long queryEnd) throws IOException {
                    recordBuilder.withQueryEnd(queryEnd);
                }

                @Override
                public void strand(final char strand) throws IOException {
                    recordBuilder.withStrand(strand);
                }

                @Override
                public void targetName(final String targetName) throws IOException {
                    recordBuilder.withTargetName(targetName);
                }

                @Override
                public void targetLength(final long targetLength) throws IOException {
                    recordBuilder.withTargetLength(targetLength);
                }

                @Override
                public void targetStart(final long targetStart) throws IOException {
                    recordBuilder.withTargetStart(targetStart);
                }

                @Override
                public void targetEnd(final long targetEnd) throws IOException {
                    recordBuilder.withTargetEnd(targetEnd);
                }

                @Override
                public void matches(final long matches) throws IOException {
                    recordBuilder.withMatches(matches);
                }

                @Override
                public void alignmentBlockLength(final long alignmentBlockLength) throws IOException {
                    recordBuilder.withAlignmentBlockLength(alignmentBlockLength);
                }
    
                @Override
                public void mappingQuality(final int mappingQuality) throws IOException {
                    recordBuilder.withMappingQuality(mappingQuality);
                }

                @Override
                public void field(final String tag, final String type, final String value) throws IOException {
                    recordBuilder.withField(tag, type, value);
                }

                @Override
                public void arrayField(final String tag, final String type, final String arrayType, final String... values) throws IOException {
                    recordBuilder.withArrayField(tag, type, arrayType, values);
                }

                @Override
                public boolean complete() throws IOException {
                    listener.record(recordBuilder.build());
                    recordBuilder.reset();
                    return true;
                }
            });
    }
}
