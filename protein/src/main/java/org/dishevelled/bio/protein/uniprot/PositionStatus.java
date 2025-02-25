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

import com.google.common.collect.ImmutableMap;

/**
 * Position status.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public enum PositionStatus {

    /** Certain position status, <code>=</code>. */
    CERTAIN("=", "certain"),

    /** Uncertain position status, <code>~</code>. */
    UNCERTAIN("~", "uncertain"), // ≈, if unicode is ok

    /** Less than position status, <code>&lt;</code>. */
    LESS_THAN("<", "less than"),

    /** Greater than position status, <code>&gt;</code>. */
    GREATER_THAN(">", "greater than"),

    /** Unknown position status, <code>?</code>. */
    UNKNOWN("?", "unknown");


    /** Symbol for this position status. */
    private final String symbol;

    /** Description for this position status. */
    private final String description;


    /** Immutable map of position statuses keyed by symbol. */
    private static final ImmutableMap<String, PositionStatus> BY_SYMBOL;

    /** Immutable map of position statuses keyed by description. */
    private static final ImmutableMap<String, PositionStatus> BY_DESCRIPTION;

    static {
        ImmutableMap.Builder<String, PositionStatus> symbolBuilder = new ImmutableMap.Builder<String, PositionStatus>();
        ImmutableMap.Builder<String, PositionStatus> descriptionBuilder = new ImmutableMap.Builder<String, PositionStatus>();

        for (PositionStatus status : values()) {
            symbolBuilder.put(status.getSymbol(), status);
            descriptionBuilder.put(status.getDescription(), status);
        }

        BY_SYMBOL = symbolBuilder.buildOrThrow();
        BY_DESCRIPTION = descriptionBuilder.buildOrThrow();
    }


    /**
     * Create a new position status.
     *
     * @param symbol symbol, must not be null
     * @param description description, must not be null
     */
    private PositionStatus(final String symbol, final String description) {
        checkNotNull(symbol);
        checkNotNull(description);
        this.symbol = symbol;
        this.description = description;
    }

    /**
     * Return the symbol for this position status.
     *
     * @return the symbol for this position status
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Return the description for this position status.
     *
     * @return the description for this position status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the position status with the specified symbol.
     *
     * @param symbol symbol, must not be null
     * @return the position status with the specified symbol
     */
    public static PositionStatus fromSymbol(final String symbol) {
        checkNotNull(symbol);
        return BY_SYMBOL.get(symbol);
    }

    /**
     * Return the position status with the specified description.
     *
     * @param description description, must not be null
     * @return the position status with the specified description
     */
    public static PositionStatus fromDescription(final String description) {
        checkNotNull(description);
        return BY_DESCRIPTION.get(description);
    }
}
