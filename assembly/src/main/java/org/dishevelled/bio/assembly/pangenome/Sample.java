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

import java.util.HashMap;
import java.util.Map;

/**
 * Sample in a pangenome.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
public final class Sample {
    /** Name of this sample. */
    private final String name;

    /** Pangenome for this sample. */
    private final Pangenome pangenome;

    /** Map of haplotypes keyed by identifier. */
    private final Map<Integer, Haplotype> haplotypes = new HashMap<Integer, Haplotype>();


    /**
     * Create a new sample.
     *
     * @param name name of this sample, must not be null
     * @param pangenome pangenome for this sample, must not be null
     */
    Sample(final String name, final Pangenome pangenome) {
        checkNotNull(name);
        checkNotNull(pangenome);
        this.name = name;
        this.pangenome = pangenome;
    }


    /**
     * Return the name of this sample.
     *
     * @return the name of this sample
     */
    public String getName() {
        return name;
    }

    /**
     * Return the pangenome for this sample.
     *
     * @return the pangenome for this sample
     */
    public Pangenome getPangenome() {
        return pangenome;
    }

    /**
     * Return the map of haplotypes for this sample keyed by identifier.
     *
     * @return the map of haplotypes for this sample keyed by identifier
     */
    public Map<Integer, Haplotype> getHaplotypes() {
        return haplotypes;
    }

    @Override
    public String toString() {
        return name;
    }
}
