/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import java.util.Optional;

/**
 * Scaffold in a pangenome haplotype.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
@Immutable
public final class Scaffold {
    /** Name of this scaffold. */
    private final String name;

    /** Length of this scaffold, if known. */
    private final Long length;

    // todo: md5, from sequence dictionary?

    /** Haplotype for this scaffold. */
    private final Haplotype haplotype;


    /**
     * Create a new scaffold.
     *
     * @param name name of this scaffold, must not be null
     * @param length length of this scaffold, if specified, must be at least 1
     * @param haplotype haplotype for this scaffold, must not be null
     */
    Scaffold(final String name, @Nullable final Long length, final Haplotype haplotype) {
        checkNotNull(name);
        checkNotNull(haplotype);
        if (length != null) {
            checkArgument(length > 0L, "if specified, length must be at least 1");
        }
        this.name = name;
        this.length = length;
        this.haplotype = haplotype;
    }


    /**
     * Return the name of this scaffold.
     *
     * @return the name of this scaffold
     */
    public String getName() {
        return name;
    }

    /**
     * Return the length of this scaffold, if specified. May be null.
     *
     * @return the length of this scaffold, if specified
     */
    public Long getLength() {
        return length;
    }

    /**
     * Return an optional wrapping the length of this scaffold, which may be null.
     *
     * @return an optional wrapping the length of this scaffold, which may be null
     */
    public Optional<Long> getLengthOpt() {
        return Optional.ofNullable(length);
    }
    
    /**
     * Return the haplotype for this scaffold.
     *
     * @return the haplotype for this scaffold
     */
    public Haplotype getHaplotype() {
        return haplotype;
    }

    @Override
    public String toString() {
        return name;
    }
}
