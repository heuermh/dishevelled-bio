/*

    dsh-bio-compress-hdfs  HDFS support for dsh-compress.
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
package org.dishevelled.bio.compress.hdfs;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.compress.hdfs.HdfsReaders.reader;
import static org.dishevelled.bio.compress.hdfs.HdfsWriters.writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

import org.dishevelled.bio.alignment.sam.SamHeader;
import org.dishevelled.bio.alignment.sam.SamReader;
import org.dishevelled.bio.alignment.sam.SamRecord;
import org.dishevelled.bio.alignment.sam.SamWriter;
import org.dishevelled.bio.alignment.sam.SamStreamAdapter;

/**
 * Compress alignments in SAM format on HDFS to splittable bgzf or bzip2 compression codecs.
 *
 * @author  Michael Heuer
 */
public final class CompressSam {

    /**
     * Compress alignments in SAM format on HDFS to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputPath input path, must not be null
     * @param outputPath output path, must not be null
     * @param conf configuration, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void compress(final String inputPath, final String outputPath, final Configuration conf) throws IOException {
        checkNotNull(inputPath);
        checkNotNull(outputPath);
        checkNotNull(conf);

        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputPath, conf);
            writer = writer(outputPath, conf);
            
            final PrintWriter w = writer;
            SamReader.stream(reader, new SamStreamAdapter() {
                    @Override
                    public void header(final SamHeader header) {
                        SamWriter.writeHeader(header, w);
                    }

                    @Override
                    public void record(final SamRecord record) {
                        SamWriter.writeRecord(record, w);
                    }
                });
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
}
