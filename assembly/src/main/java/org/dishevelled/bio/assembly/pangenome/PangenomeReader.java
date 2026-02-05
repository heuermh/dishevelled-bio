/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.pangenome;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * Pangenome reader.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
@Immutable
public final class PangenomeReader {

    /**
     * Private no-arg constructor.
     */
    private PangenomeReader() {
        // empty
    }


    /**
     * Read a pangeome from the specified readable in FASTA format.
     *
     * @param readable readable in FASTA format, must not be null
     * @return a pangeome read from the specified readable in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Pangenome readFasta(final Readable readable) throws IOException {
        checkNotNull(readable);
        FastaLineProcessor fastaLineProcessor = new FastaLineProcessor();
        CharStreams.readLines(readable, fastaLineProcessor);
        return fastaLineProcessor.getPangenome();
    }

    /**
     * FASTA format line processor.
     */
    private static class FastaLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** Pangenome builder. */
        private final Pangenome.Builder builder = Pangenome.builder();


        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean processLine(final String line) throws IOException
        {
            lineNumber++;
            if (line.startsWith(">")) {
                try {
                    builder.add(line.substring(1).trim());
                }
                catch (IllegalArgumentException e) {
                    throw new IOException("could not read line number " + lineNumber + ", caught " + e.getMessage());
                }
            }
            return true;
        }

        /**
         * Return the pangenome created by this FASTA format line processor.
         *
         * @return the pangenome created by this FASTA format line processor
         */
        Pangenome getPangenome() {
            return builder.build();
        }
    }


    /**
     * Read a pangeome from the specified readable in FASTA index (.fai) format.
     *
     * @param readable readable in FASTA index (.fai) format, must not be null
     * @return a pangeome read from the specified readable in FASTA index (.fai) format
     * @throws IOException if an I/O error occurs
     */
    public static Pangenome readFastaIndex(final Readable readable) throws IOException {
        checkNotNull(readable);
        FastaIndexLineProcessor fastaIndexLineProcessor = new FastaIndexLineProcessor();
        CharStreams.readLines(readable, fastaIndexLineProcessor);
        return fastaIndexLineProcessor.getPangenome();
    }

    /**
     * FASTA index format line processor.
     */
    private static class FastaIndexLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** Pangenome builder. */
        private final Pangenome.Builder builder = Pangenome.builder();


        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean processLine(final String line) throws IOException
        {
            lineNumber++;

            /*
              An fai index file is a text file consisting of lines each with five TAB-delimited
              columns for a FASTA file and six for FASTQ:

              NAME      Name of this reference sequence
              LENGTH    Total length of this reference sequence, in bases
              OFFSET    Offset in the FASTA/FASTQ file of this sequence's first base
              LINEBASES The number of bases on each line
              LINEWIDTH The number of bytes in each line, including the newline

             */
            String[] tokens = line.split("\t");
            if (tokens.length < 5) {
                throw new IOException("could not read line number " + lineNumber + ", expected at least 5 tokens got " + tokens.length);
            }
            try {
                String name = tokens[0];
                long length = Long.parseLong(tokens[1]);
                builder.add(name, length);
            }
            catch (IllegalArgumentException e) {
                throw new IOException("could not read line number " + lineNumber + ", caught " + e.getMessage());
            }
            return true;
        }

        /**
         * Return the pangenome created by this FASTA index format line processor.
         *
         * @return the pangenome created by this FASTA index format line processor
         */
        Pangenome getPangenome() {
            return builder.build();
        }
    }


    /**
     * Read a pangeome from the specified readable in sequence dictionary (.dict) format.
     *
     * @param readable readable in sequence dictionary (.dict) format, must not be null
     * @return a pangeome read from the specified readable in sequence dictionary (.dict) format
     * @throws IOException if an I/O error occurs
     */
    public static Pangenome readSequenceDictionary(final Readable readable) throws IOException {
        checkNotNull(readable);
        SequenceDictionaryLineProcessor sequenceDictionaryLineProcessor = new SequenceDictionaryLineProcessor();
        CharStreams.readLines(readable, sequenceDictionaryLineProcessor);
        return sequenceDictionaryLineProcessor.getPangenome();
    }

    /**
     * Sequence dictionary format line processor.
     */
    private static class SequenceDictionaryLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** Pangenome builder. */
        private final Pangenome.Builder builder = Pangenome.builder();


        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean processLine(final String line) throws IOException
        {
            lineNumber++;

            /*
              Sequence dictionary (.dict) format contains a header but no SAMRecords,
              and the header contains only sequence records.
             */
            if (line.startsWith("@SQ")) {

                String[] tokens = line.split("\t");
                if (tokens.length < 3) {
                    throw new IOException("could not read line number " + lineNumber + ", expected at least 3 tokens got " + tokens.length);
                }
                try {
                    // starts with SN:
                    String name = tokens[1].substring(3);
                    // starts with LN:
                    long length = Long.parseLong(tokens[2].substring(3));

                    builder.add(name, length);
                }
                catch (IllegalArgumentException e) {
                    throw new IOException("could not read line number " + lineNumber + ", caught " + e.getMessage());
                }
            }
            return true;
        }

        /**
         * Return the pangenome created by this sequence dictionary format line processor.
         *
         * @return the pangenome created by this sequence dictionary format line processor
         */
        Pangenome getPangenome() {
            return builder.build();
        }
    }
}
