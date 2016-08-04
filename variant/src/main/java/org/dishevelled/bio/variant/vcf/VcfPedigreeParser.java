/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

/**
 * VCF pedigree parser.
 *
 * @author  Michael Heuer
 */
public final class VcfPedigreeParser {

    /**
     * Private no-arg constructor.
     */
    private VcfPedigreeParser() {
        // empty
    }


    /**
     * Read a VCF pedigree from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return a VCF pedigree read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static VcfPedigree pedigree(final Readable readable) throws IOException {
        checkNotNull(readable);
        ParseListener parseListener = new ParseListener();
        VcfParser.parse(readable, parseListener);
        return parseListener.getPedigree();
    }

    /**
     * Parse listener.
     */
    static final class ParseListener extends VcfParseAdapter {
        /** List of ##PEDIGREE meta header lines. */
        private final List<String> pedigreeMetaLines = new ArrayList<String>();

        /** VCF pedigree builder. */
        private final VcfPedigree.Builder builder = VcfPedigree.builder();

        /** VCF samples keyed by id. */
        private Map<String, VcfSample> samplesById = new HashMap<String, VcfSample>();

        @Override
        public void meta(final String meta) throws IOException {
            // copied from VcfSampleParser.ParseListener
            if (meta.startsWith("##SAMPLE=")) {
                ListMultimap<String, String> values = ArrayListMultimap.create();
                String[] tokens = meta.substring(10).split(",");
                for (String token : tokens) {
                    String[] metaTokens = token.split("=");
                    String key = metaTokens[0];
                    String[] valueTokens = metaTokens[1].split(";");
                    for (String valueToken : valueTokens) {
                        values.put(key, valueToken.replace("\"", "").replace(">", ""));
                    }
                }

                String id = values.get("ID").get(0);
                List<String> genomeIds = values.get("Genomes");
                List<String> mixtures = values.get("Mixture");
                List<String> descriptions = values.get("Description");

                List<VcfGenome> genomes = new ArrayList<VcfGenome>(genomeIds.size());
                for (int i = 0, size = genomeIds.size(); i < size; i++) {
                    genomes.add(new VcfGenome(genomeIds.get(i), Double.parseDouble(mixtures.get(i)), descriptions.get(i)));
                }
                samplesById.put(id, new VcfSample(id, genomes.toArray(new VcfGenome[genomes.size()])));
            }
            else if (meta.startsWith("##PEDIGREE=")) {
                // need to process later, after all samples have been found
                pedigreeMetaLines.add(meta);
            }
        }

        @Override
        public void samples(final String... samples) throws IOException {
            // copied from VcfSampleParser.ParseListener
            for (String sample : samples) {
                // add if missing in meta lines
                if (!samplesById.containsKey(sample)) {
                    samplesById.put(sample, new VcfSample(sample, new VcfGenome[0]));
                }
            }

            /*

              VCF 4.2 and earlier spec:

              ##PEDIGREE=<Derived=ID2,Original=ID1>

              ##PEDIGREE=<Child=CHILD-GENOME-ID,Mother=MOTHER-GENOME-ID,Father=FATHER-GENOME-ID>

              ##PEDIGREE=<Derived=PRIMARY-TUMOR-GENOME-ID,Original=GERMLINE-GENOME-ID>
              ##PEDIGREE=<Derived=SECONDARY1-TUMOR-GENOME-ID,Original=PRIMARY-TUMOR-GENOME-ID>
              ##PEDIGREE=<Derived=SECONDARY2-TUMOR-GENOME-ID,Original=PRIMARY-TUMOR-GENOME-ID>

              ##PEDIGREE=<Name_0=G0-ID,Name_1=G1-ID,...,Name_N=GN-ID>

              G0-ID is target genome, G1-ID..GN-ID are source genomes
              Name_0 is relationship target label, Name_1..Name_N are relationship source labels

              E.g.
              ID1 -- Original ---- Derived --> ID2

              MOTHER-GENOME_ID -- Mother ---- Child --> CHILD-GENOME-ID
              FATHER-GENOME_ID -- Father ---- Child --> CHILD-GENOME-ID

              GERMLINE-GENOME-ID -- Original ---- Derived --> PRIMARY-TUMOR-GENOME-ID
              PRIMARY-TUMOR-GENOME-ID -- Original ---- Derived --> SECONDARY1-TUMOR-GENOME-ID
              PRIMARY-TUMOR-GENOME-ID -- Original ---- Derived --> SECONDARY2-TUMOR-GENOME-ID

              VCF 4.3 spec:

              TBD.

             */
            for (String meta : pedigreeMetaLines) {
                // note: need to trim < and > characters
                String[] tokens = meta.substring(12, meta.length() - 1).split(",");
                if (tokens.length > 1) {
                    String[] first = tokens[0].split("=");
                    if (first.length < 2) {
                        throw new IOException("invalid ##PEDIGREE meta header line: " + meta);
                    }
                    // in VCF 4.3, targetLabel will always be "ID"
                    String targetLabel = first[0];
                    VcfSample target = samplesById.get(first[1]);
                    if (target == null) {
                        throw new IOException("VCF sample id " + first[1] + " not found in samples");
                    }

                    for (int i = 1; i < tokens.length; i++) {
                        String[] next = tokens[i].split("=");
                        if (next.length < 2) {
                            throw new IOException("invalid ##PEDIGREE meta header line: " + meta);
                        }
                        String sourceLabel = next[0];
                        VcfSample source = samplesById.get(next[1]);
                        if (source == null) {
                            throw new IOException("VCF sample id " + next[1] + " not found in samples");
                        }

                        builder.withRelationship(source, sourceLabel, target, targetLabel);
                    }
                }
            }
        }

        /**
         * Return the VCF pedigree.
         *
         * @return the VCF pedigree.
         */
        VcfPedigree getPedigree() {
            return builder.build();
        }
    }
}
