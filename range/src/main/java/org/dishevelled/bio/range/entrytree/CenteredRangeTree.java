/*

    dsh-bio-range  Guava ranges for genomics.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
package org.dishevelled.bio.range.entrytree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import org.dishevelled.bio.range.Ranges;

/**
 * Centered range tree.
 *
 * @param <C> range endpoint type
 * @param <V> value type
 * @author  Michael Heuer
 */
public final class CenteredRangeTree<C extends Comparable, V> extends AbstractRangeTree<C, V> {
    /** Cached size. */
    private final int size;

    /** Root node, if any. */
    private final Node root;


    /**
     * Create a new centered range tree with the specified range entries.
     *
     * @param entries range entries, must not be null
     */
    private CenteredRangeTree(final Iterable<Entry<C, V>> entries) {
        checkNotNull(entries);
        size = Iterables.size(entries);
        root = createNode(entries);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterable<Entry<C, V>> intersect(final Range<C> range) {
        checkNotNull(range);
        List<Entry<C, V>> result = Lists.newLinkedList();
        Set<Node> visited = Sets.newHashSet();
        depthFirstSearch(range, root, result, visited);
        return result;
    }

    /**
     * Create and return a new node for the specified range entries.
     *
     * @param entries range entries
     * @return a new node for the specified range entries
     */
    private Node createNode(final Iterable<Entry<C, V>> entries) {
        Entry<C, V> first = Iterables.getFirst(entries, null);
        if (first == null) {
            return null;
        }
        Range<C> span = first.getRange();

        for (Entry<C, V> entry : entries) {
            Range<C> range = entry.getRange();
            span = range.span(span);
        }
        if (span.isEmpty()) {
            return null;
        }
        C center = Ranges.center(span);
        List<Entry<C, V>> left = Lists.newArrayList();
        List<Entry<C, V>> right = Lists.newArrayList();
        List<Entry<C, V>> overlap = Lists.newArrayList();

        for (Entry<C, V> entry : entries) {
            Range<C> range = entry.getRange();
            if (Ranges.isLessThan(range, center)) {
                left.add(entry);
            }
            else if (Ranges.isGreaterThan(range, center)) {
                right.add(entry);
            }
            else {
                overlap.add(entry);
            }
        }
        return new Node(center, createNode(left), createNode(right), overlap);
    }

    /**
     * Depth first search.
     *
     * @param query query range
     * @param node node
     * @param result list of matching ranges
     * @param visited set of visited nodes
     */
    private void depthFirstSearch(final Range<C> query, final Node node, final List<Entry<C, V>> result, final Set<Node> visited) {
        if (node == null || visited.contains(node) || query.isEmpty()) {
            return;
        }
        if (node.left() != null && Ranges.isLessThan(query, node.center())) {
            depthFirstSearch(query, node.left(), result, visited);
        }
        else if (node.right() != null && Ranges.isGreaterThan(query, node.center())) {
            depthFirstSearch(query, node.right(), result, visited);
        }
        if (Ranges.isGreaterThan(query, node.center())) {
            for (Entry<C, V> entry : node.overlapByUpperEndpoint()) {
                Range<C> range = entry.getRange();
                if (Ranges.intersect(range, query)) {
                    result.add(entry);
                }
                if (Ranges.isGreaterThan(query, range.upperEndpoint())) {
                    break;
                }
            }
        }
        else if (Ranges.isLessThan(query, node.center())) {
            for (Entry<C, V> entry : node.overlapByLowerEndpoint()) {
                Range<C> range = entry.getRange();
                if (Ranges.intersect(range, query)) {
                    result.add(entry);
                }
                if (Ranges.isLessThan(query, range.lowerEndpoint())) {
                    break;
                }
            }
        }
        else {
            result.addAll(node.overlapByLowerEndpoint());
        }
        visited.add(node);
    }

    /**
     * Node.
     */
    private class Node {
        /** Center. */
        private final C center;

        /** Left node, if any. */
        private final Node left;

        /** Right node, if any. */
        private final Node right;

        /** List of overlapping range entries ordered by lower endpoint. */
        private final List<Entry<C, V>> overlapByLowerEndpoint;

        /** List of overlapping range entries ordered by upper endpoint. */
        private final List<Entry<C, V>> overlapByUpperEndpoint;


        /**
         * Create a new node.
         *
         * @param center center
         * @param left left node, if any
         * @param right right node, if any
         * @param overlap list of overlapping nodes
         */
        Node(final C center, final Node left, final Node right, final List<Entry<C, V>> overlap) {
            this.center = center;
            this.left = left;
            this.right = right;
            overlapByLowerEndpoint = Lists.newArrayList(overlap);
            overlapByUpperEndpoint = Lists.newArrayList(overlap);
            Ordering<Range<C>> orderingByLowerEndpoint = Ranges.orderingByLowerEndpoint();
            Ordering<Range<C>> reverseOrderingByUpperEndpoint = Ranges.reverseOrderingByUpperEndpoint();
            Ordering<Entry<C, V>> entryOrderingByLowerEndpoint = new EntryOrdering(orderingByLowerEndpoint);
            Ordering<Entry<C, V>> entryReverseOrderingByUpperEndpoint = new EntryOrdering(reverseOrderingByUpperEndpoint);
            overlapByLowerEndpoint.sort(entryOrderingByLowerEndpoint);
            overlapByUpperEndpoint.sort(entryReverseOrderingByUpperEndpoint);
        }


        /**
         * Return the center.
         *
         * @return the center
         */
        C center() {
            return center;
        }

        /**
         * Return the left node, if any.
         *
         * @return the left node or <code>null</code> if no such node exists
         */
        Node left() {
            return left;
        }

        /**
         * Return the right node, if any.
         *
         * @return the right node or <code>null</code> if no such node exists
         */
        Node right() {
            return right;
        }

        /**
         * Return the list of overlapping range entries ordered by lower endpoint.
         *
         * @return the list of overlapping range entries ordered by lower endpoint
         */
        List<Entry<C, V>> overlapByLowerEndpoint() {
            return overlapByLowerEndpoint;
        }

        /**
         * Return the list of overlapping range entries ordered by upper endpoint.
         *
         * @return the list of overlapping range entries ordered by upper endpoint
         */
        List<Entry<C, V>> overlapByUpperEndpoint() {
            return overlapByUpperEndpoint;
        }
    }

    /**
     * All equal ordering.
     */
    private class AllEqualOrdering extends Ordering<V> {
        @Override
        public int compare(final V left, final V right) {
            return 0;
        }
    }

    /**
     * Entry ordering.
     */
    private class EntryOrdering extends Ordering<Entry<C, V>> {
        /** Range ordering. */
        private final Ordering<Range<C>> rangeOrdering;

        /** Value ordering. */
        private final Ordering<V> valueOrdering;


        /**
         * Create a new entry ordering with the specified range ordering.
         *
         * @param rangeOrdering range ordering, must not be null
         */
        private EntryOrdering(final Ordering<Range<C>> rangeOrdering)
        {
            checkNotNull(rangeOrdering);
            this.rangeOrdering = rangeOrdering;
            this.valueOrdering = new AllEqualOrdering();
        }


        @Override
        public int compare(final Entry<C, V> left, final Entry<C, V> right) {
            return ComparisonChain.start()
                .compare(left.getRange(), right.getRange(), rangeOrdering)
                .compare(left.getValue(), right.getValue(), valueOrdering)
                .result();
        }
    }


    /**
     * Create and return a new range tree from the specified range entries.
     *
     * @param <C> range endpoint type
     * @param <V> value type
     * @param entries range entries, must not be null
     * @return a new range tree from the specified range entries
     */
    public static <C extends Comparable, V> RangeTree<C, V> create(final Iterable<Entry<C, V>> entries) {
        return new CenteredRangeTree<C, V>(entries);
    }

    /**
     * Create and return a new range tree from the specified ranges and values.
     *
     * @param <C> range endpoint type
     * @param <V> value type
     * @param ranges ranges, must not be null and must be equal in size to <code>values</code>
     * @param values values, must not be null and must be equal in size to <code>ranges</code>
     * @return a new range tree from the specified ranges and values
     */
    public static <C extends Comparable, V> RangeTree<C, V> create(final List<Range<C>> ranges, final List<V> values) {
        checkNotNull(ranges);
        checkNotNull(values);
        checkArgument(ranges.size() == values.size(), "entries and values must be equal size");

        int size = ranges.size();
        List<Entry<C, V>> entries = Lists.newArrayListWithExpectedSize(size);
        for (int i = 0; i < size; i++)
        {
            entries.add(new RangeEntry<C, V>(ranges.get(i), values.get(i)));
        }
        return new CenteredRangeTree<C, V>(entries);
    }
}
