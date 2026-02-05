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

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Pangenome.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
public final class Pangenome {
    /** Default delimiter, <code>#</code>. */
    static final String DEFAULT_DELIMITER = "#";

    /** Map of samples keyed by sample name. */
    private final Map<String, Sample> samples = new HashMap<String, Sample>();


    /**
     * Return the map of samples for this pangenome keyed by sample name.
     *
     * @return the map of samples for this pangenome keyed by sample name.
     */
    Map<String, Sample> getSamples() {
        return samples;
    }

    /**
     * Create and return a new pangenome builder.
     *
     * @return a new pangenome builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Pangenome builder.
     */
    static class Builder {
        /** Pangenome for this pangenome builder. */
        private Pangenome pangenome = new Pangenome();


        /**
         * Reset this pangenome builder.
         *
         * @return this pangenome builder
         */
        public Builder reset() {
            pangenome = new Pangenome();
            return this;
        }

        /**
         * Add the specified sample, haplotype, and scaffold to this pangenome builder.
         *
         * @param sample sample to add, must not be null
         * @param haplotype haplotype to add
         * @param scaffold scaffold to add, must not be null
         * @param length scaffold length, if specified
         * @return this pangenome builder
         */
        Builder add(final String sample, final int haplotype, final String scaffold, @Nullable final Long length) {
            checkNotNull(sample);
            checkNotNull(scaffold);

            if (!pangenome.getSamples().containsKey(sample)) {
                pangenome.getSamples().put(sample, new Sample(sample, pangenome));
            }
            Sample s = pangenome.getSamples().get(sample);
            if (!s.getHaplotypes().containsKey(haplotype)) {
                s.getHaplotypes().put(haplotype, new Haplotype(haplotype, s));
            }
            Haplotype h = s.getHaplotypes().get(haplotype);
            if (!h.getScaffolds().containsKey(scaffold)) {
                h.getScaffolds().put(scaffold, new Scaffold(scaffold, length, h));
            }
            return this;
        }

        /**
         * Add the specified sample, haplotype, and scaffold to this pangenome builder.
         *
         * @param sample sample to add, must not be null
         * @param haplotype haplotype to add
         * @param scaffold scaffold to add, must not be null
         * @return this pangenome builder
         */
        Builder add(final String sample, final int haplotype, final String scaffold) {
            return add(sample, haplotype, scaffold, null);
        }

        /**
         * Parse the specified line with the specified delimiter.
         *
         * @param line line to parse, must not be null
         * @param delimiter delimiter, must not be null
         * @param length scaffold length, if specified
         * @return this pangenome builder
         */
        Builder add(final String line, final String delimiter, @Nullable Long length) {
            checkNotNull(line);
            checkNotNull(delimiter);

            String[] tokens = line.split(delimiter);
            if (tokens.length != 3) {
                throw new IllegalArgumentException("invalid PanSN-spec format, expected three tokens got " + tokens.length);
            }
            String sample = tokens[0];
            int haplotype = Integer.parseInt(tokens[1]);
            String scaffold = tokens[2];

            return add(sample, haplotype, scaffold, length);
        }

        /**
         * Parse the specified line with the default delimiter.
         *
         * @param line line to parse, must not be null
         * @return this pangenome builder
         */
        Builder add(final String line) {
            return add(line, DEFAULT_DELIMITER, null);
        }

        /**
         * Parse the specified line with the default delimiter.
         *
         * @param line line to parse, must not be null
         * @param length scaffold length, if specified
         * @return this pangenome builder
         */
        Builder add(final String line, @Nullable final Long length) {
            return add(line, DEFAULT_DELIMITER, length);
        }

        /**
         * Return a new pangenome.
         *
         * @return a new pangenome
         */
        Pangenome build() {
            return pangenome;
        }
    }
}
