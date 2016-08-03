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

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList;

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
     * @param genomes variable number of VCF genomes, must not be null
     * @return a VCF pedigree read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static VcfPedigree pedigree(final Readable readable, final VcfGenome... genomes) throws IOException {
        checkNotNull(genomes);
        return pedigree(readable, ImmutableList.copyOf(genomes));
    }

    /**
     * Read a VCF pedigree from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @param genomes zero or more VCF genomes, must not be null
     * @return a VCF pedigree read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static VcfPedigree pedigree(final Readable readable, final Iterable<VcfGenome> genomes) throws IOException {
        checkNotNull(readable);
        checkNotNull(genomes);
        ParseListener parseListener = new ParseListener(genomes);
        VcfParser.parse(readable, parseListener);
        return parseListener.getPedigree();
    }

    /**
     * Parse listener.
     */
    static final class ParseListener extends VcfParseAdapter {
        /** VCF pedigree builder. */
        private final VcfPedigree.Builder builder = VcfPedigree.builder();

        /** VCF genomes keyed by id. */
        private final Map<String, VcfGenome> genomesById = Maps.newHashMap();

        /**
         * Create a new parse listener with the specified VCF genomes.
         *
         * @param genomes zero or more VCF genomes, must not be null
         */
        private ParseListener(final Iterable<VcfGenome> genomes) {
            checkNotNull(genomes);
            for (VcfGenome genome : genomes) {
                genomesById.put(genome.getId(), genome);
            }
        }

        @Override
        public void meta(final String meta) throws IOException {
            /*
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

             */
            if (meta.startsWith("##PEDIGREE=")) {
                // note: need to trim < and > characters
                String[] tokens = meta.substring(12, meta.length() - 1).split(",");
                if (tokens.length > 1) {
                    String[] first = tokens[0].split("=");
                    if (first.length < 2) {
                        throw new IOException("invalid ##PEDIGREE meta header line: " + meta);
                    }
                    String targetLabel = first[0];
                    VcfGenome target = genomesById.get(first[1]);
                    if (target == null) {
                        throw new IOException("VCF genome id " + first[1] + " not found in genomes");
                    }

                    for (int i = 1; i < tokens.length; i++) {
                        String[] next = tokens[i].split("=");
                        if (next.length < 2) {
                            throw new IOException("invalid ##PEDIGREE meta header line: " + meta);
                        }
                        String sourceLabel = next[0];
                        VcfGenome source = genomesById.get(next[1]);
                        if (source == null) {
                            throw new IOException("VCF genome id " + next[1] + " not found in genomes");
                        }

                        builder.withRelationship(source, sourceLabel, target, targetLabel);
                    }
                }
            }
        }

        @Override
        public boolean complete() throws IOException {
            return false;
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
