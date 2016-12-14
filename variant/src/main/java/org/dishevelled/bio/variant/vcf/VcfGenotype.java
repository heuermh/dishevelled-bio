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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF genotype.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfGenotype {
    /** Genotype fields. */
    private final ListMultimap<String, String> fields;


    /**
     * Create a new VCF genotype with the specified genotype fields.
     *
     * @param fields genotype fields, must not be null and must contain strictly one GT genotype field
     */
    private VcfGenotype(final ListMultimap<String, String> fields) {
        // check GT cardinality constraint
        checkArgument(fields.containsKey("GT"), "GT genotype field is required");
        checkArgument(fields.get("GT").size() == 1, "GT genotype field cardinality is strictly one, found " + fields.get("GT").size());

        this.fields = fields;
    }


    /**
     * Return the value of the GT genotype field for this VCF genotype.
     *
     * @return the value of the GT genotype field for this VCF genotype.
     */
    public String getGt() {
        return fields.get("GT").get(0);
    }

    /**
     * Return the genotype fields for this VCF genotype.
     *
     * @return the genotype fields for this VCF genotype
     */
    public ListMultimap<String, String> getFields() {
        return fields;
    }

    /**
     * Create and return a new VCF genotype builder.
     *
     * @return a new VCF genotype builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * VCF genotype builder.
     */
    public static final class Builder {
        /** Genotype fields. */
        private ImmutableListMultimap.Builder<String, String> fields = ImmutableListMultimap.builder();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF genotype builder configured with the specified genotype field.
         *
         * @param id genotype field id, must not be null
         * @param values genotype field values, must not be null
         * @return this VCF record builder configured with the specified genotype field
         */
        public Builder withField(final String id, final String... values) {
            checkNotNull(values);
            for (String value : values) {
                fields.put(id, value);
            }
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype fields.
         *
         * @param fields genotype fields, must not be null
         * @return this VCF record builder configured with the specified genotype fields
         */
        public Builder withFields(final ListMultimap<String, String> fields) {
            this.fields.putAll(fields);
            return this;
        }

        /**
         * Reset this VCF genotype builder.
         *
         * @return this VCF genotype builder
         */
        public Builder reset() {
            fields = ImmutableListMultimap.builder();
            return this;
        }

        /**
         * Create and return a new VCF genotype populated from the configuration of this VCF genotype builder.
         *
         * @return a new VCF genotype populated from the configuration of this VCF genotype builder
         */
        public VcfGenotype build() {
            return new VcfGenotype(fields.build());
        }
    }
}
