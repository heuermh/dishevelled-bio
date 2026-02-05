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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unit test for Pangenome.
 *
 * @author  Michael Heuer
 */
public final class PangenomeTest {

    @Test
    public void testEmptyPangenome() {
        Pangenome pangenome = new Pangenome();
        assertEquals(0, pangenome.getSamples().size());
    }

    @Test
    public void testEmptyBuilder() {
        Pangenome pangenome = Pangenome.builder().build();
        assertEquals(0, pangenome.getSamples().size());
    }

    @Test
    public void testBuilderAddLine() {
        Pangenome pangenome = Pangenome.builder()
            .add("sample#1#scaffold")
            .build();

        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertNull(scaffold.getLength());
    }

    @Test
    public void testBuilderAddLineWithLength() {
        Pangenome pangenome = Pangenome.builder()
            .add("sample#1#scaffold", 100L)
            .build();

        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertEquals(new Long(100L), scaffold.getLength());
    }

    @Test
    public void testBuilderAddLineDelimiterWithLength() {
        Pangenome pangenome = Pangenome.builder()
            .add("sample,1,scaffold", ",", 100L)
            .build();

        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertEquals(new Long(100L), scaffold.getLength());
    }

    @Test
    public void testBuilderAdd() {
        Pangenome pangenome = Pangenome.builder()
            .add("sample", 1, "scaffold")
            .build();

        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertNull(scaffold.getLength());
    }

    @Test
    public void testBuilderAddMultiple() {
        Pangenome pangenome = Pangenome.builder()
            .add("sample1", 1, "scaffold1")
            .add("sample1", 1, "scaffold2")
            .add("sample1", 2, "scaffold3")
            .add("sample2", 1, "scaffold4")
            .build();

        assertEquals(2, pangenome.getSamples().size());
        Sample sample1 = pangenome.getSamples().get("sample1");
        assertEquals(2, sample1.getHaplotypes().size());
        Sample sample2 = pangenome.getSamples().get("sample2");
        assertEquals(1, sample2.getHaplotypes().size());
        Haplotype haplotype1 = sample1.getHaplotypes().get(1);
        assertEquals(2, haplotype1.getScaffolds().size());
        Scaffold scaffold = haplotype1.getScaffolds().get("scaffold1");
        assertNull(scaffold.getLength());
    }

    @Test
    public void testBuilderAddWithLength() {
        Pangenome pangenome = Pangenome.builder()
            .add("sample", 1, "scaffold", 100L)
            .build();

        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertEquals(new Long(100L), scaffold.getLength());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderAddTooFewTokens() {
        Pangenome.builder().add("sample#scaffold");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderAddTooManyTokens() {
        Pangenome.builder().add("sample#1#scaffold#more");
    }

    @Test(expected=NumberFormatException.class)
    public void testBuilderAddHaplotypeInvalidNumberFormat() {
        Pangenome.builder().add("sample#invalid#scaffold");
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderAddNullLine() {
        Pangenome.builder().add(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderAddNullDelimiter() {
        Pangenome.builder().add("sample#1#scaffold", null, 100L);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderAddNullSample() {
        Pangenome.builder().add(null, 1, "scaffold");
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderAddNullScaffold() {
        Pangenome.builder().add("sample", 1, null);
    }
}
