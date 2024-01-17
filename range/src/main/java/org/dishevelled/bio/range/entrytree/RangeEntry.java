/*

    dsh-bio-range  Guava ranges for genomics.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Range;

/**
 * Implementation of RangeTree.Entry.
 *
 * @param <C> range endpoint type
 * @param <V> value type
 * @author  Michael Heuer
 */
@Immutable
public final class RangeEntry<C extends Comparable, V> implements RangeTree.Entry<C, V>
{
    /** Range for this range entry. */
    private final Range<C> range;

    /** Value for this range entry. */
    private final V value;
    

    /**
     * Create a new range entry with the specified range and value.
     *
     * @param range range for this range entry, must not be null
     * @param value value for this range entry
     */
    public RangeEntry(final Range<C> range, final V value)
    {
        checkNotNull(range);
        this.range = range;
        this.value = value; // defensive copy or clone?
    }


    @Override
    public Range<C> getRange()
    {
        return range;
    }

    @Override
    public V getValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        return range.hashCode() ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(final Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof RangeTree.Entry))
        {
            return false;
        }
        RangeTree.Entry e = (RangeTree.Entry) o;

        return range.equals(e.getRange())
            && value == null ? e.getValue() == null : value.equals(e.getValue());
    }
}
