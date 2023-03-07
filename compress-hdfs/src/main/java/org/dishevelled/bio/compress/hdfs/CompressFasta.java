/*

    dsh-bio-compress-hdfs  HDFS support for dsh-compress.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
package org.dishevelled.bio.compress.hdfs;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.compress.hdfs.HdfsReaders.reader;
import static org.dishevelled.bio.compress.hdfs.HdfsWriters.writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.hadoop.conf.Configuration;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

/**
 * Compress sequences in FASTA format on HDFS to splittable bgzf or bzip2 compression codecs.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
public final class CompressFasta {
    static final String DEFAULT_ALPHABET = "dna";
    static final int DEFAULT_LINE_WIDTH = 70;
    static final String DESCRIPTION_LINE = "description_line";

    /**
     * Compress sequences in FASTA format on HDFS to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputPath input path, must not be null
     * @param outputPath output path, must not be null
     * @param conf configuration, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void compress(final String inputPath, final String outputPath, final Configuration conf) throws IOException {
        compress(inputPath, outputPath, DEFAULT_ALPHABET, DEFAULT_LINE_WIDTH, conf);
    }

    /**
     * Compress sequences in FASTA format on HDFS to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputPath input path, must not be null
     * @param outputPath output path, must not be null
     * @param alphabet FASTA alphabet { dna, protein }, if any
     * @param lineWidth line width
     * @param conf configuration, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void compress(final String inputPath,
                                final String outputPath,
                                final String alphabet,
                                final int lineWidth,
                                final Configuration conf) throws IOException {
        checkNotNull(inputPath);
        checkNotNull(outputPath);
        checkNotNull(conf);

        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputPath, conf);
            writer = writer(outputPath, conf);

            for (SequenceIterator sequences = isProteinAlphabet(alphabet) ? SeqIOTools.readFastaProtein(reader) : SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();
                writeSequence(sequence, lineWidth, writer);
            }
        }
        catch (BioException e) {
            throw new IOException("could not read input path, caught " + e.getMessage(), e);
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    static boolean isProteinAlphabet(final String alphabet) {
        return alphabet != null && (alphabet.equalsIgnoreCase("protein") || alphabet.equalsIgnoreCase("aa"));
    }

    // copied with mods from biojava-legacy FastaFormat, as it uses PrintStream not PrintWriter
    static String describeSequence(final Sequence sequence) {
        return sequence.getAnnotation().containsProperty(DESCRIPTION_LINE) ?
            (String) sequence.getAnnotation().getProperty(DESCRIPTION_LINE) : (String) sequence.getName();
    }

    static void writeSequence(final Sequence sequence, final int lineWidth, final PrintWriter writer) throws IOException {
        writer.print(">");
        writer.println(describeSequence(sequence));
        for (int i = 1, length = sequence.length(); i <= length; i += lineWidth) {
            writer.println(sequence.subStr(i, Math.min(i + lineWidth - 1, length)));
        }
    }
}
