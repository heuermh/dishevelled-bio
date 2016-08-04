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

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Maps;

import org.dishevelled.graph.Graph;
import org.dishevelled.graph.Node;

import org.dishevelled.graph.impl.GraphUtils;

/**
 * VCF pedigree.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfPedigree {
    /** Graph of pedigree relationships between samples. */
    private final Graph<VcfSample, Relationship> graph;


    /**
     * Create a new VCF pedigree with the specified graph of relationships between samples.
     *
     * @param graph graph of relationships between samples, must not be null
     */
    private VcfPedigree(final Graph<VcfSample, Relationship> graph) {
        checkNotNull(graph);
        this.graph = GraphUtils.unmodifiableGraph(graph);
    }


    /**
     * Return the graph of relationships between samples for this VCF pedigree.
     *
     * @return the graph of relationships between samples for this VCF pedigree
     */
    public Graph<VcfSample, Relationship> getGraph() {
        return graph;
    }


    /**
     * Create and return a new VCF pedigree builder.
     *
     * @return a new VCF pedigree builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * VCF predigree relationship.
     */
    public static final class Relationship {
        /** Source label. */
        private final String sourceLabel;

        /** Target label. */
        private final String targetLabel;


        /**
         * Create a new VCF pedigree relationship with the specified source and target labels.
         *
         * @param sourceLabel source label
         * @param targetLabel target label
         */
        private Relationship(final String sourceLabel, final String targetLabel) {
            this.sourceLabel = sourceLabel;
            this.targetLabel = targetLabel;
        }


        /**
         * Return the source label for this VCF predigree relationship.
         *
         * @return the source label for this VCF predigree relationship
         */
        public String getSourceLabel() {
            return sourceLabel;
        }

        /**
         * Return the target label for this VCF predigree relationship.
         *
         * @return the target label for this VCF predigree relationship
         */
        public String getTargetLabel() {
            return targetLabel;
        }
    }

    /**
     * VCF pedigree builder.
     */
    public static final class Builder {
        /** Graph of pedigree relationships between samples. */
        private final Graph<VcfSample, Relationship> graph = GraphUtils.createGraph();

        /** Map of samples to nodes. */
        private final Map<VcfSample, Node<VcfSample, Relationship>> nodes = Maps.newHashMap();


        /**
         * Return the node for the specified sample, creating a new one if necessary.
         *
         * @param sample sample, must not be null
         * @return the node for the specified sample, creating a new one if necessary
         */
        private Node<VcfSample, Relationship> createNode(final VcfSample sample) {
            checkNotNull(sample);
            if (!nodes.containsKey(sample)) {
                nodes.put(sample, graph.createNode(sample));
            }
            return nodes.get(sample);
        }

        /**
         * Return this VCF pedigree builder configured with the specified relationship.
         *
         * @param source source sample, must not be null
         * @param sourceLabel source label
         * @param target target sample, must not be null
         * @param targetLabel target label
         */
        public Builder withRelationship(final VcfSample source,
                                        final String sourceLabel,
                                        final VcfSample target,
                                        final String targetLabel) {

            Node<VcfSample, Relationship> sourceNode = createNode(source);
            Node<VcfSample, Relationship> targetNode = createNode(target);
            graph.createEdge(sourceNode, targetNode, new Relationship(sourceLabel, targetLabel));
            return this;
        }

        /**
         * Reset this VCF pedigree builder.
         *
         * @return this VCF pedigree builder
         */
        public Builder reset() {
            graph.clear();
            nodes.clear();
            return this;
        }

        /**
         * Create and return a new VCF pedigree populated from the configuration of this VCF pedigree builder.
         *
         * @return a new VCF pedigree populated from the configuration of this VCF pedigree builder
         */
        public VcfPedigree build() {
            return new VcfPedigree(graph);
        }
    }
}
