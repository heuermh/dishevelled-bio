/*

    dsh-bio-protein  Protein sequences and metadata.
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
package org.dishevelled.bio.protein.uniprot;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

/**
 * Entry sequence.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public final class EntrySequence {
    private String accession;
    private final int length;
    private final int mass;
    private final String checksum;
    private final String modified;
    private final int version;
    private final boolean precursor;
    private final String fragment;
    private final String sequence;


    /**
     * Create a new entry sequence.
     *
     * @param length length
     * @param mass mass
     * @param checksum checksum, must not be null
     * @param modified modified, must not be null
     * @param version version
     * @param precursor precursor
     * @param fragment fragment, if any
     * @param sequence sequence, must not be null
     */
    EntrySequence(final int length, final int mass, final String checksum, final String modified, final int version, final boolean precursor, @Nullable final String fragment, final String sequence) {
        checkNotNull(checksum);
        checkNotNull(modified);
        checkNotNull(sequence);

        this.length = length;
        this.mass = mass;
        this.checksum = checksum;
        this.modified = modified;
        this.version = version;
        this.precursor = precursor;
        this.fragment = fragment;
        this.sequence = sequence;
    }


    public String getAccession() {
        return accession;
    }

    public int getLength() {
        return length;
    }

    public int getMass() {
        return mass;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getModified() {
        return modified;
    }

    public int getVersion() {
        return version;
    }

    public boolean isPrecursor() {
        return precursor;
    }

    public String getFragment() {
        return fragment;
    }

    public String getSequence() {
        return sequence;
    }

    // late initialization, unfortunately
    EntrySequence withAccession(final String accession) {
        checkNotNull(accession);
        this.accession = accession;
        return this;
    }
}
