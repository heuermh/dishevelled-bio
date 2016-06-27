/*

    dsh-bio-range  Guava ranges for genomics.
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
package org.dishevelled.bio.range.entrytree;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Unit test for CenteredRangeTree.
 *
 * @author  Michael Heuer
 */
public final class CenteredRangeTreeTest extends AbstractRangeTreeTest {

    @Override
    protected <C extends Comparable> RangeTree<C, String> create(final Range<C>... ranges) {
        return create(ImmutableList.copyOf(ranges));
    }

    @Override
    protected <C extends Comparable> RangeTree<C, String> create(final List<Range<C>> ranges) {
        int size = ranges.size();
        List<String> values = Lists.newArrayListWithExpectedSize(ranges.size());
        for (int i = 0; i < size; i++) {
            values.add("value" + i);
        }
        return CenteredRangeTree.create(ranges, values);
    }
}
