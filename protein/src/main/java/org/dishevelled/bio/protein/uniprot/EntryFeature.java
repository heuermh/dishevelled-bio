/*

    dsh-bio-protein  Protein sequences and metadata.
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
package org.dishevelled.bio.protein.uniprot;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

/**
 * Entry feature.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public final class EntryFeature {
    private String accession;
    private final String id;
    private final String description;
    private final String evidence;
    private final String ref;
    private final String type;
    private final String original;
    private final String variations;
    private final Location location;
    private final String ligand;
    private final String ligandPart;


    /**
     * Create a new entry feature.
     *
     * @param id id, if any
     * @param description description, if any
     * @param evidence evidence, if any
     * @param ref ref, if any
     * @param type type, must not be null
     * @param original original, if any
     * @param variations variations, if any
     * @param location location, must not be null
     * @param ligand ligand, if any
     * @param ligandPart ligand part, if any
     */
    EntryFeature(@Nullable final String id,
                 @Nullable final String description,
                 @Nullable final String evidence,
                 @Nullable final String ref,
                 final String type,
                 @Nullable final String original,
                 @Nullable final String variations,
                 final Location location,
                 @Nullable final String ligand,
                 @Nullable final String ligandPart) {

        checkNotNull(type);
        this.id = id;
        this.description = description;
        this.evidence = evidence;
        this.ref = ref;
        this.type = type;
        this.original = original;
        this.variations = variations;
        this.location = location;
        this.ligand = ligand;
        this.ligandPart = ligandPart;
    }


    /**
     * Return the accession for this entry feature.
     *
     * @return the accession for this entry feature
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Return the id for this entry feature, may be null.
     *
     * @return the id for this entry feature, may be null
     */
    public String getId() { // featureId?
        return id;
    }

    /**
     * Return the description for this entry feature, may be null.
     *
     * @return the description for this entry feature, may be null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the evidence for this entry feature, may be null.
     *
     * @return the evidence for this entry feature, may be null
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * Return the ref for this entry feature, may be null.
     *
     * @return the ref for this entry feature, may be null
     */
    public String getRef() {
        return ref;
    }

    /**
     * Return the type for this entry feature.
     *
     * @return the type for this entry feature
     */
    public String getType() {
        return type;
    }

    /**
     * Return the original sequence for this entry feature, may be null.
     *
     * @return the original sequence for this entry feature, may be null
     */
    public String getOriginal() {
        return original;
    }

    /**
     * Return the sequence variations for this entry feature, may be null.
     *
     * @return the sequence variations for this entry feature, may be null
     */
    public String getVariations() {
        return variations;
    }

    /**
     * Return the location for this entry feature.
     *
     * @return the location for this entry feature
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Return the ligand for this entry feature, may be null.
     *
     * @return the ligand for this entry feature, may be null
     */
    public String getLigand() {
        return ligand;
    }

    /**
     * Return the ligand part for this entry feature, may be null.
     *
     * @return the ligand part for this entry feature, may be null
     */
    public String getLigandPart() {
        return ligandPart;
    }

    /**
     * Return this entry feature with the specified accession.
     *
     * @param accession accession, must not be null
     * @return this entry feature with the specified accession
     */
    // late initialization, unfortunately
    EntryFeature withAccession(final String accession) {
        checkNotNull(accession);
        this.accession = accession;
        return this;
    }
}
