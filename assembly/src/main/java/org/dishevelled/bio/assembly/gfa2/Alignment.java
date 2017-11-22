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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import java.util.regex.Pattern;

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Alignment.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class Alignment {
    private final String cigar;
    private final List<Integer> trace;
    private static final Pattern CIGAR = Pattern.compile("^([0-9]+[MDIP])+$");

    public Alignment(final String cigar) {
        this(cigar, null);
    }

    public Alignment(final List<Integer> trace) {
        this(null, trace);
    }

    private Alignment(@Nullable final String cigar,
                      @Nullable final List<Integer> trace) {
        this.cigar = cigar;
        this.trace = trace;
    }

    public boolean hasCigar() {
        return cigar != null;
    }

    public String getCigar() {
        return cigar;
    }

    public boolean hasTrace() {
        return trace != null;
    }

    public List<Integer> getTrace() {
        return trace;
    }

    @Override
    public String toString() {
        return hasCigar() ? cigar : Joiner.on(",").join(trace);
    }

    static boolean isCigar(final String value) {
        return CIGAR.matcher(value).matches();
    }

    public static Alignment valueOf(final String value) {
        checkNotNull(value);
        if ("*".equals(value)) {
            return null;
        }
        if (isCigar(value)) {
            return new Alignment(value);
        }
        List<Integer> trace = Splitter
            .on(",")
            .splitToList(value)
            .stream()
            .map(v -> Integer.valueOf(v))
            .collect(Collectors.toList());

        return new Alignment(trace);
    }
}
