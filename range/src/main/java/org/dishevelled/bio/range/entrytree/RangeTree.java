/*

    dsh-bio-range  Guava ranges for genomics.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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

import com.google.common.collect.Range;

/**
 * Range tree composed of entries.
 *
 * @param <C> range endpoint type
 * @param <V> value type
 * @author  Michael Heuer
 */
public interface RangeTree<C extends Comparable, V> {

    /**
     * Return the number of range entries in this range tree.
     *
     * @return the number of range entries in this range tree
     */
    int size();

    /**
     * Return true if the number of range entries in this range tree is zero.
     *
     * @return true if the number of range entries in this range tree is zero
     */
    boolean isEmpty();

    /**
     * Return true if the specified location intersects with any range entries in this range tree.
     *
     * @param location location to intersect
     * @return true if the specified location intersects with any range entries in this range tree
     */
    boolean contains(C location);

    /**
     * Return the number of range entries in this range tree at the specified location.
     *
     * @param location location
     * @return the number of range entries in this range tree at the specified location
     */
    int count(C location);

    /**
     * Return the range entries in this range tree at the specified location, if any.
     *
     * @param location location
     * @return the range entries in this range tree at the specified location, if any
     */
    Iterable<Entry<C, V>> query(C location);

    /**
     * Return the number of range entries in this range tree that intersect the specified query range.
     *
     * @param query range to intersect, must not be null
     * @return the number of range entries in this range tree that intersect the specified query range
     */
    int count(Range<C> query);

    /**
     * Return the range entries in this range tree that intersect the specified query range, if any.
     *
     * @param query range to intersect, must not be null
     * @return the range entries in this range tree that intersect the specified query range, if any
     */
    Iterable<Entry<C, V>> intersect(Range<C> query);

    /**
     * Return true if the specified query range intersects with any range entries in this range tree.
     *
     * @param query range to intersect, must not be null
     * @return true if the specified query range intersects with any range entries in this range tree
     */
    boolean intersects(Range<C> query);

    /**
     * Return true if any range in the specified query list of ranges intersects
     * with any range entries in this range tree.
     *
     * @param query list of ranges to intersect, must not be null
     * @return true if any range in the specified query list of ranges intersects
     *    with any range entries in this range tree
     */
    boolean intersects(Iterable<Range<C>> query);

    /**
     * Entry in a range tree.
     *
     * @param <C> range endpoint type
     * @param <V> value type
     */
    interface Entry<C extends Comparable, V>
    {
        /**
         * Return the range for this range tree entry.
         *
         * @return the range for this range tree entry
         */
        Range<C> getRange();

        /**
         * Return the value for this range tree entry.
         *
         * @return the value for this range tree entry
         */
        V getValue();
    }
}
