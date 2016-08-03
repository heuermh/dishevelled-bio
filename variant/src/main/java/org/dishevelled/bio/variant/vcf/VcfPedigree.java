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
    /** Graph of pedigree relationships between genomes. */
    private final Graph<VcfGenome, Relationship> graph;


    /**
     * Create a new VCF pedigree with the specified graph of relationships between genomes.
     *
     * @param graph graph of relationships between genomes, must not be null
     */
    private VcfPedigree(final Graph<VcfGenome, Relationship> graph) {
        checkNotNull(graph);
        this.graph = GraphUtils.unmodifiableGraph(graph);
    }


    /**
     * Return the graph of relationships between genomes for this VCF pedigree.
     *
     * @return the graph of relationships between genomes for this VCF pedigree
     */
    public Graph<VcfGenome, Relationship> getGraph() {
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
        /** Graph of pedigree relationships between genomes. */
        private final Graph<VcfGenome, Relationship> graph = GraphUtils.createGraph();

        /** Map of genomes to nodes. */
        private final Map<VcfGenome, Node<VcfGenome, Relationship>> nodes = Maps.newHashMap();


        /**
         * Return the node for the specified genome, creating a new one if necessary.
         *
         * @param genome genome, must not be null
         * @return the node for the specified genome, creating a new one if necessary
         */
        private Node<VcfGenome, Relationship> createNode(final VcfGenome genome) {
            checkNotNull(genome);
            if (!nodes.containsKey(genome)) {
                nodes.put(genome, graph.createNode(genome));
            }
            return nodes.get(genome);
        }

        /**
         * Return this VCF pedigree builder configured with the specified relationship.
         *
         * @param source source genome, must not be null
         * @param sourceLabel source label
         * @param target target genome, must not be null
         * @param targetLabel target label
         */
        public Builder withRelationship(final VcfGenome source,
                                        final String sourceLabel,
                                        final VcfGenome target,
                                        final String targetLabel) {

            Node<VcfGenome, Relationship> sourceNode = createNode(source);
            Node<VcfGenome, Relationship> targetNode = createNode(target);
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
