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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

/**
 * SAM header.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class SamHeader {
    /** SAM header line, if any. */
    private final Optional<SamHeaderLine> optHeaderLine;

    /** List of SAM sequence header lines. */
    private final List<SamSequenceHeaderLine> sequenceHeaderLines;

    /** List of SAM read group header lines. */
    private final List<SamReadGroupHeaderLine> readGroupHeaderLines;

    /** List of SAM program header lines. */
    private final List<SamProgramHeaderLine> programHeaderLines;

    /** List of SAM comment header lines. */
    private final List<SamCommentHeaderLine> commentHeaderLines;


    /**
     * Create a new SAM header.
     *
     * @param headerLine SAM header line, if any
     * @param sequenceHeaderLines list of SAM sequence header lines, must not be null
     * @param readGroupHeaderLines list of SAM read group header lines, must not be null
     * @param programHeaderLines list of SAM program header lines, must not be null
     * @param commentHeaderLines list of SAM comment header lines, must not be null
     */
    private SamHeader(@Nullable final SamHeaderLine headerLine,
                      final List<SamSequenceHeaderLine> sequenceHeaderLines,
                      final List<SamReadGroupHeaderLine> readGroupHeaderLines,
                      final List<SamProgramHeaderLine> programHeaderLines,
                      final List<SamCommentHeaderLine> commentHeaderLines) {

        checkNotNull(sequenceHeaderLines);
        checkNotNull(readGroupHeaderLines);
        checkNotNull(programHeaderLines);
        checkNotNull(commentHeaderLines);

        optHeaderLine = Optional.ofNullable(headerLine);
        this.sequenceHeaderLines = ImmutableList.copyOf(sequenceHeaderLines);
        this.readGroupHeaderLines = ImmutableList.copyOf(readGroupHeaderLines);
        this.programHeaderLines = ImmutableList.copyOf(programHeaderLines);
        this.commentHeaderLines = ImmutableList.copyOf(commentHeaderLines);
    }


    /**
     * Return an optional wrapping the SAM header line for this SAM header, if any.
     *
     * @return an optional wrapping the SAM header line for this SAM header, if any
     */
    public Optional<SamHeaderLine> getHeaderLineOpt() {
        return optHeaderLine;
    }

    /**
     * Return the list of SAM sequence header lines for this SAM header.
     *
     * @return the list of SAM sequence header lines for this SAM header.
     */
    public List<SamSequenceHeaderLine> getSequenceHeaderLines() {
        return sequenceHeaderLines;
    }

    /**
     * Return the list of SAM read group header lines for this SAM header.
     *
     * @return the list of SAM read group header lines for this SAM header.
     */
    public List<SamReadGroupHeaderLine> getReadGroupHeaderLines() {
        return readGroupHeaderLines;
    }

    /**
     * Return the list of SAM program header lines for this SAM header.
     *
     * @return the list of SAM program header lines for this SAM header.
     */
    public List<SamProgramHeaderLine> getProgramHeaderLines() {
        return programHeaderLines;
    }

    /**
     * Return the list of SAM comment header lines for this SAM header.
     *
     * @return the list of SAM comment header lines for this SAM header.
     */
    public List<SamCommentHeaderLine> getCommentHeaderLines() {
        return commentHeaderLines;
    }

    /**
     * Return a new SAM header builder.
     *
     * @return a new SAM header builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Return a new SAM header builder populated with the fields in the specified SAM header.
     *
     * @param header SAM header, must not be null
     * @return a new SAM header builder populated with the fields in the specified SAM header
     */
    public static Builder builder(final SamHeader header) {
        checkNotNull(header);
        final Builder builder = new Builder();
        header.getHeaderLineOpt().ifPresent(hl -> builder.withHeaderLine(hl));
        return builder
            .withSequenceHeaderLines(header.getSequenceHeaderLines())
            .withReadGroupHeaderLines(header.getReadGroupHeaderLines())
            .withProgramHeaderLines(header.getProgramHeaderLines())
            .withCommentHeaderLines(header.getCommentHeaderLines());
    }

    /**
     * SAM header builder.
     */
    public static final class Builder {
        /** SAM header line. */
        private SamHeaderLine headerLine;

        /** List of SAM sequence header lines. */
        private List<SamSequenceHeaderLine> sequenceHeaderLines = new ArrayList<SamSequenceHeaderLine>();

        /** List of SAM read group header lines. */
        private List<SamReadGroupHeaderLine> readGroupHeaderLines = new ArrayList<SamReadGroupHeaderLine>();

        /** List of SAM program header lines. */
        private List<SamProgramHeaderLine> programHeaderLines = new ArrayList<SamProgramHeaderLine>();

        /** List of SAM comment header lines. */
        private List<SamCommentHeaderLine> commentHeaderLines = new ArrayList<SamCommentHeaderLine>();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this SAM header builder configured with the specified header line.
         *
         * @param headerLine SAM header line
         * @return this SAM header builder configured with the specified header line
         */
        public Builder withHeaderLine(final SamHeaderLine headerLine) {
            this.headerLine = headerLine;
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified sequence header line.
         *
         * @param sequenceHeaderLine SAM sequence header line
         * @return this SAM header builder configured with the specified sequence header line
         */
        public Builder withSequenceHeaderLine(final SamSequenceHeaderLine sequenceHeaderLine) {
            checkNotNull(sequenceHeaderLine);
            sequenceHeaderLines.add(sequenceHeaderLine);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified sequence header lines.
         *
         * @param sequenceHeaderLines variable number of SAM header lines
         * @return this SAM header builder configured with the specified sequence header lines
         */
        public Builder withSequenceHeaderLines(final SamSequenceHeaderLine... sequenceHeaderLines) {
            checkNotNull(sequenceHeaderLines);
            for (SamSequenceHeaderLine sequenceHeaderLine : sequenceHeaderLines) {
                this.sequenceHeaderLines.add(sequenceHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified sequence header lines.
         *
         * @param sequenceHeaderLines SAM header lines
         * @return this SAM header builder configured with the specified sequence header lines
         */
        public Builder withSequenceHeaderLines(final Iterable<SamSequenceHeaderLine> sequenceHeaderLines) {
            checkNotNull(sequenceHeaderLines);
            for (SamSequenceHeaderLine sequenceHeaderLine : sequenceHeaderLines) {
                this.sequenceHeaderLines.add(sequenceHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured by replacing the sequence header lines
         * with the specified sequence header lines.
         *
         * @param sequenceHeaderLines SAM header lines
         * @return this SAM header builder configured by replacing the sequence header lines
         *    with the specified sequence header lines
         */
        public Builder replaceSequenceHeaderLines(final List<SamSequenceHeaderLine> sequenceHeaderLines) {
            checkNotNull(sequenceHeaderLines);
            this.sequenceHeaderLines.clear();
            this.sequenceHeaderLines.addAll(sequenceHeaderLines);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified read group header line.
         *
         * @param readGroupHeaderLine SAM read group header line
         * @return this SAM header builder configured with the specified read group header line
         */
        public Builder withReadGroupHeaderLine(final SamReadGroupHeaderLine readGroupHeaderLine) {
            checkNotNull(readGroupHeaderLine);
            readGroupHeaderLines.add(readGroupHeaderLine);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified read group header lines.
         *
         * @param readGroupHeaderLines variable number of SAM header lines
         * @return this SAM header builder configured with the specified read group header lines
         */
        public Builder withReadGroupHeaderLines(final SamReadGroupHeaderLine... readGroupHeaderLines) {
            checkNotNull(readGroupHeaderLines);
            for (SamReadGroupHeaderLine readGroupHeaderLine : readGroupHeaderLines) {
                this.readGroupHeaderLines.add(readGroupHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified read group header lines.
         *
         * @param readGroupHeaderLines SAM header lines
         * @return this SAM header builder configured with the specified read group header lines
         */
        public Builder withReadGroupHeaderLines(final Iterable<SamReadGroupHeaderLine> readGroupHeaderLines) {
            checkNotNull(readGroupHeaderLines);
            for (SamReadGroupHeaderLine readGroupHeaderLine : readGroupHeaderLines) {
                this.readGroupHeaderLines.add(readGroupHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured by replacing the read group header lines
         * with the specified read group header lines.
         *
         * @param readGroupHeaderLines SAM header lines
         * @return this SAM header builder configured by replacing the read group header lines
         *    with the specified read group header lines
         */
        public Builder replaceReadGroupHeaderLines(final List<SamReadGroupHeaderLine> readGroupHeaderLines) {
            checkNotNull(readGroupHeaderLines);
            this.readGroupHeaderLines.clear();
            this.readGroupHeaderLines.addAll(readGroupHeaderLines);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified program header line.
         *
         * @param programHeaderLine SAM program header line
         * @return this SAM header builder configured with the specified program header line
         */
        public Builder withProgramHeaderLine(final SamProgramHeaderLine programHeaderLine) {
            checkNotNull(programHeaderLine);
            programHeaderLines.add(programHeaderLine);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified program header lines.
         *
         * @param programHeaderLines variable number of SAM header lines
         * @return this SAM header builder configured with the specified program header lines
         */
        public Builder withProgramHeaderLines(final SamProgramHeaderLine... programHeaderLines) {
            checkNotNull(programHeaderLines);
            for (SamProgramHeaderLine programHeaderLine : programHeaderLines) {
                this.programHeaderLines.add(programHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified program header lines.
         *
         * @param programHeaderLines SAM header lines
         * @return this SAM header builder configured with the specified program header lines
         */
        public Builder withProgramHeaderLines(final Iterable<SamProgramHeaderLine> programHeaderLines) {
            checkNotNull(programHeaderLines);
            for (SamProgramHeaderLine programHeaderLine : programHeaderLines) {
                this.programHeaderLines.add(programHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured by replacing the program header lines
         * with the specified program header lines.
         *
         * @param programHeaderLines SAM header lines
         * @return this SAM header builder configured by replacing the program header lines
         *    with the specified program header lines
         */
        public Builder replaceProgramHeaderLines(final List<SamProgramHeaderLine> programHeaderLines) {
            checkNotNull(programHeaderLines);
            this.programHeaderLines.clear();
            this.programHeaderLines.addAll(programHeaderLines);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified comment header line.
         *
         * @param commentHeaderLine SAM comment header line
         * @return this SAM header builder configured with the specified comment header line
         */
        public Builder withCommentHeaderLine(final SamCommentHeaderLine commentHeaderLine) {
            checkNotNull(commentHeaderLine);
            commentHeaderLines.add(commentHeaderLine);
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified comment header lines.
         *
         * @param commentHeaderLines variable number of SAM header lines
         * @return this SAM header builder configured with the specified comment header lines
         */
        public Builder withCommentHeaderLines(final SamCommentHeaderLine... commentHeaderLines) {
            checkNotNull(commentHeaderLines);
            for (SamCommentHeaderLine commentHeaderLine : commentHeaderLines) {
                this.commentHeaderLines.add(commentHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured with the specified comment header lines.
         *
         * @param commentHeaderLines SAM header lines
         * @return this SAM header builder configured with the specified comment header lines
         */
        public Builder withCommentHeaderLines(final Iterable<SamCommentHeaderLine> commentHeaderLines) {
            checkNotNull(commentHeaderLines);
            for (SamCommentHeaderLine commentHeaderLine : commentHeaderLines) {
                this.commentHeaderLines.add(commentHeaderLine);
            }
            return this;
        }

        /**
         * Return this SAM header builder configured by replacing the comment header lines
         * with the specified comment header lines.
         *
         * @param commentHeaderLines SAM header lines
         * @return this SAM header builder configured by replacing the comment header lines
         *    with the specified comment header lines
         */
        public Builder replaceCommentHeaderLines(final List<SamCommentHeaderLine> commentHeaderLines) {
            checkNotNull(commentHeaderLines);
            this.commentHeaderLines.clear();
            this.commentHeaderLines.addAll(commentHeaderLines);
            return this;
        }

        /**
         * Reset this SAM header builder.
         *
         * @return this SAM header builder
         */
        public Builder reset() {
            headerLine = null;
            sequenceHeaderLines.clear();
            readGroupHeaderLines.clear();
            programHeaderLines.clear();
            commentHeaderLines.clear();
            return this;
        }

        /**
         * Create and return a new SAM header populated from the configuration of this SAM header builder.
         *
         * @return a new SAM header populated from the configuration of this SAM header builder
         */
        public SamHeader build() {
            if (headerLine == null &&
                (!sequenceHeaderLines.isEmpty() ||
                 !readGroupHeaderLines.isEmpty() ||
                 !programHeaderLines.isEmpty() ||
                 !commentHeaderLines.isEmpty())) {
                throw new IllegalArgumentException("headerLine must be specified if other header lines are present");
            }
            return new SamHeader(headerLine,
                                 sequenceHeaderLines,
                                 readGroupHeaderLines,
                                 programHeaderLines,
                                 commentHeaderLines);
        }
    }
}
