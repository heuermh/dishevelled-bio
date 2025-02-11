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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Haplotype in a pangenome sample.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
public final class Haplotype {
    /** Identifier for this haplotype. */
    private final int identifier;

    /** Sample for this haplotype. */
    private final Sample sample;

    /** Map of scaffolds keyed by scaffold name. */
    private final Map<String, Scaffold> scaffolds = new HashMap<String, Scaffold>();


    /**
     * Create a new haplotype.
     *
     * @param identifier identifier for this haplotype
     * @param sample sample for this haplotype, must not be null
     */
    Haplotype(final int identifier, final Sample sample) {
        checkNotNull(sample);
        this.identifier = identifier;
        this.sample = sample;
    }


    /**
     * Return the identifier for this haplotype.
     *
     * @return the identifier for this haplotype
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Return the sample for this haplotype.
     *
     * @return the sample for this haplotype
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * Return the map of scaffolds for this haplotype keyed by scaffold name.
     *
     * @return the map of scaffolds for this haplotype keyed by scaffold name
     */
    public Map<String, Scaffold> getScaffolds() {
        return scaffolds;
    }
    
    @Override
    public String toString() {
        return String.valueOf(identifier);
    }
}
