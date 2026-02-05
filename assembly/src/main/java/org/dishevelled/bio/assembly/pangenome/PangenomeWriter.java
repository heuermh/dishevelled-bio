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

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

/**
 * Pangenome writer.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
@Immutable
public final class PangenomeWriter {

    /**
     * Private no-arg constructor.
     */
    private PangenomeWriter() {
        // empty
    }


    /**
     * Write the specified pangenome using the specified writer.
     *
     * @param pangenome pangenome to write, must not be null
     * @param writer writer, must not be null
     */
    public static void write(final Pangenome pangenome, final PrintWriter writer) {
        checkNotNull(pangenome);
        checkNotNull(writer);

        for (Sample s : pangenome.getSamples().values()) {
            for (Haplotype h : s.getHaplotypes().values()) {
                for (Scaffold f : h.getScaffolds().values()) {
                    writer.println(Joiner.on("\t").join(s.toString(), h.toString(), f.toString()));
                }
            }
        }
    }

    private static <T> List<T> copyAndSort(final Collection<T> c) {
        ArrayList<T> copy = new ArrayList<T>(c);
        copy.sort(new Comparator<T>() {
                @Override
                public int compare(final T o1, final T o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
        return copy;
    }

    /**
     * Write the specified pangenome after sorting using the specified writer.
     *
     * @param pangenome pangenome to write after sorting, must not be null
     * @param writer writer, must not be null
     */
    public static void writeSorted(final Pangenome pangenome, final PrintWriter writer) {
        checkNotNull(pangenome);
        checkNotNull(writer);

        for (Sample s : copyAndSort(pangenome.getSamples().values())) {
            for (Haplotype h : copyAndSort(s.getHaplotypes().values())) {
                for (Scaffold f : copyAndSort(h.getScaffolds().values())) {
                    writer.println(Joiner.on("\t").join(s.toString(), h.toString(), f.toString()));
                }
            }
        }
    }

    /**
     * Write the specified pangenome as a tree using the specified writer.
     *
     * @param pangenome pangenome to write as a tree, must not be null
     * @param writer writer, must not be null
     */
    public static void writeTree(final Pangenome pangenome, final PrintWriter writer) {
        checkNotNull(pangenome);
        checkNotNull(writer);

        // todo: e.g. 1 sample
        writer.println();
        writer.println(String.format("   + (%d samples)", pangenome.getSamples().size()));
        writer.println("   |");

        for (Iterator<Sample> i = pangenome.getSamples().values().iterator(); i.hasNext();) {

            Sample s = i.next();
            writer.println(String.format("   +---+ %s (%d haplotypes)", s.getName(), s.getHaplotypes().size()));

            for (Iterator<Haplotype> j = s.getHaplotypes().values().iterator(); j.hasNext();) {

                Haplotype h = j.next();
                writer.println("   |   |");
                writer.println(String.format("   |   +---+ %s (%d scaffolds)", h.getIdentifier(), h.getScaffolds().size()));;

                if (j.hasNext()) {
                    writer.println("   |   |   |");
                }

                for (Iterator<Scaffold> k = h.getScaffolds().values().iterator(); k.hasNext();) {

                    Scaffold f = k.next();
                    writer.println("   |   |   |");
                    if (f.getLength() == null) {
                        writer.println(String.format("   |   |   +---+ %s", f.getName()));
                    }
                    else {
                        writer.println(String.format("   |   |   +---+ %s (%d bp)", f.getName(), f.getLength()));
                    }
                }
            }

            if (i.hasNext()) {
                writer.println("   |   |");
                writer.println("   |");
            }
        }
        writer.println("   |   |");
        writer.println("   |");
        writer.println();
    }

    /**
     * Write the specified pangenome as a tree after sorting using the specified writer.
     *
     * @param pangenome pangenome to write as a tree after sorting, must not be null
     * @param writer writer, must not be null
     */
    public static void writeSortedTree(final Pangenome pangenome, final PrintWriter writer) {
        checkNotNull(pangenome);
        checkNotNull(writer);

        writer.println();
        writer.println(String.format("   + (%d samples)", pangenome.getSamples().size()));
        writer.println("   |");

        for (Iterator<Sample> i = copyAndSort(pangenome.getSamples().values()).iterator(); i.hasNext();) {

            Sample s = i.next();
            writer.println(String.format("   +---+ %s (%d haplotypes)", s.getName(), s.getHaplotypes().size()));

            for (Iterator<Haplotype> j = copyAndSort(s.getHaplotypes().values()).iterator(); j.hasNext();) {

                Haplotype h = j.next();
                writer.println("   |   |");
                writer.println(String.format("   |   +---+ %s (%d scaffolds)", h.getIdentifier(), h.getScaffolds().size()));;

                if (j.hasNext()) {
                    writer.println("   |   |   |");
                }

                for (Iterator<Scaffold> k = copyAndSort(h.getScaffolds().values()).iterator(); k.hasNext();) {

                    Scaffold f = k.next();
                    writer.println("   |   |   |");
                    if (f.getLength() == null) {
                        writer.println(String.format("   |   |   +---+ %s", f.getName()));
                    }
                    else {
                        writer.println(String.format("   |   |   +---+ %s (%d bp)", f.getName(), f.getLength()));
                    }
                }
            }

            if (i.hasNext()) {
                writer.println("   |   |");
                writer.println("   |");
            }
        }
        writer.println("   |   |");
        writer.println("   |");
        writer.println();
    }
}
