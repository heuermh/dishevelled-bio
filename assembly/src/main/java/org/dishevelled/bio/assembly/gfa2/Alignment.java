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
package org.dishevelled.bio.assembly.gfa2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.util.regex.Pattern;

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.annotation.Annotation;

/**
 * Alignment.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Alignment {
    /** Optional cigar for this alignment. */
    private final String cigar;

    /** Optional trace for this alignment. */
    private final List<Integer> trace;

    /** Regex for cigars. */
    private static final Pattern CIGAR = Pattern.compile("^([0-9]+[MDIP])+$");


    /**
     * Create a new alignment with the specified cigar.
     *
     * @param cigar cigar, if any
     */
    public Alignment(final String cigar) {
        this(cigar, null);
    }


    /**
     * Create a new aligment with the specified trace.
     *
     * @param trace trace, if any
     */
    public Alignment(final List<Integer> trace) {
        this(null, trace);
    }

    /**
     * Create a new alignment with the specified cigar and trace.
     *
     * @param cigar cigar, if any
     * @param trace trace, if any
     */
    private Alignment(@Nullable final String cigar,
                      @Nullable final List<Integer> trace) {
        this.cigar = cigar;
        this.trace = trace == null ? null : ImmutableList.copyOf(trace);
    }

    /**
     * Return true if this alignment has a cigar.
     *
     * @return true if this alignment has a cigar
     */
    public boolean hasCigar() {
        return cigar != null;
    }

    /**
     * Return the cigar for this alignment, if any.
     *
     * @return the cigar for this alignment, if any
     */
    public String getCigar() {
        return cigar;
    }

    /**
     * Return an optional wrapping the cigar for this alignment.
     *
     * @return an optional wrapping the cigar for this alignment
     */
    public Optional<String> getCigarOpt() {
        return Optional.ofNullable(cigar);
    }

    /**
     * Return true if this alignment has a trace.
     *
     * @return true if this alignment has a trace
     */
    public boolean hasTrace() {
        return trace != null;
    }

    /**
     * Return the trace as an immutable list of Integers, or null if
     * this alignment does not have a trace.
     *
     * @return the trace as an immutable list of Integers, or null if
     *    this alignment does not have a trace
     */
    public List<Integer> getTrace() {
        return trace;
    }

    /**
     * Return an optional wrapping the trace for this alignment.
     *
     * @return an optional wrapping the trace for this alignment
     */
    public Optional<List<Integer>> getTraceOpt() {
        return Optional.ofNullable(trace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cigar, trace);
    }

    @Override
    public boolean equals(final Object o) {
         if (o == this) {
            return true;
        }
        if (!(o instanceof Alignment)) {
            return false;
        }
        Alignment a = (Alignment) o;

        return Objects.equals(cigar, a.getCigar())
            && Objects.equals(trace, a.getTrace());
    }

    @Override
    public String toString() {
        return hasCigar() ? cigar : Joiner.on(",").join(trace);
    }


    /**
     * Return true if the specified value is a cigar.
     *
     * @param value value
     * @return true if the specified value is a cigar
     */
    private static boolean isCigar(final String value) {
        return CIGAR.matcher(value).matches();
    }

    /**
     * Parse an alignment from the specified value.
     *
     * @param value value, must not be null
     * @return an alignment parsed from the specified value
     */
    public static Alignment valueOf(final String value) {
        checkNotNull(value);
        if ("*".equals(value) || value.isEmpty()) {
            return null;
        }
        if (isCigar(value)) {
            return new Alignment(value);
        }
        List<Integer> trace = Splitter
            .on(",")
            .splitToList(value)
            .stream()
            .map(Integer::valueOf)
            .collect(Collectors.toList());

        return new Alignment(trace);
    }
}
