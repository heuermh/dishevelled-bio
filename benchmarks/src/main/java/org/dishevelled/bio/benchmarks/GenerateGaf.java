/*

    dsh-bio-benchmarks.  Benchmarks.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
package org.dishevelled.bio.benchmarks;

import static org.dishevelled.compress.Writers.writer;

import java.io.PrintWriter;

import com.google.common.collect.ImmutableMap;

import org.dishevelled.bio.annotation.Annotation;

import org.dishevelled.bio.alignment.gaf.GafRecord;

/**
 * Generate GAF for benchmarking.
 *
 * @author  Michael Heuer
 */
public class GenerateGaf {
    public static void main(final String args[]) throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(null);
            for (int i = 0; i < 10_000_000; i++) {

                String queryName = "query";
                long queryLength = 1_000_000L;
                long queryStart = 20_000L;
                long queryEnd = 40_000L;
                char strand = '+';
                String pathName = "path";
                long pathLength = 20_000L;
                long pathStart = 2_000L;
                long pathEnd = 18_000L;
                long matches = 16_000L;
                long alignmentBlockLength = 16_000L;
                int mappingQuality = 60;
                ImmutableMap.Builder<String, Annotation> annotations = ImmutableMap.builder();
                annotations.put("cs", Annotation.valueOf("cs:Z:16000M"));

                writer.println(new GafRecord(queryName,
                                             queryLength,
                                             queryStart,
                                             queryEnd,
                                             strand,
                                             pathName,
                                             pathLength,
                                             pathStart,
                                             pathEnd,
                                             matches,
                                             alignmentBlockLength,
                                             mappingQuality,
                                             annotations.build()).toString());
            }
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
            }
        }
    }
}
