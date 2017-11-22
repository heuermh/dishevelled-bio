/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Tag.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Tag {
    private final String tag;
    private final String type;
    private final String value;

    public Tag(final String tag, final String type, final String value) {
        checkNotNull(tag);
        checkNotNull(type);
        checkNotNull(value);

        this.tag = tag;
        this.type = type;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Joiner.on(":").join(tag, type, value);
    }

    public static Tag valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on(":").splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("value must have at least three tokens, was " + tokens.size());
        }
        return new Tag(tokens.get(0), tokens.get(1), tokens.get(2));
    }
}
