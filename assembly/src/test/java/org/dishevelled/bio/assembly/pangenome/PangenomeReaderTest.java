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

import static org.dishevelled.bio.assembly.pangenome.PangenomeReader.readFasta;
import static org.dishevelled.bio.assembly.pangenome.PangenomeReader.readFastaIndex;
import static org.dishevelled.bio.assembly.pangenome.PangenomeReader.readSequenceDictionary;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;

/**
 * Unit test for PangenomeReader.
 *
 * @author  Michael Heuer
 */
public final class PangenomeReaderTest {

    static final String FASTA = ">sample#1#scaffold\nACTG\n";
    static final String FASTA_INDEX = "sample#1#scaffold\t4\t21\t4\t5";
    static final String SEQUENCE_DICTIONARY = "@HD\tVN:1.6\n@SQ\tSN:sample#1#scaffold\tLN:4\tM5:6063921c8960cb385f9476a94357c9cf\tUR:file:///src/test/resources/org/dishevelled/bio/assembly/pangenome/test.fa";

    @Test(expected=NullPointerException.class)
    public void testReadFastaNullReadable() throws Exception {
        readFasta(null);
    }

    @Test
    public void testReadFasta() throws Exception {
        Pangenome pangenome = readFasta(new StringReader(FASTA));
        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertEquals("scaffold", scaffold.getName());
        assertEquals(haplotype, scaffold.getHaplotype());
        assertEquals(sample, scaffold.getHaplotype().getSample());
        assertEquals(pangenome, scaffold.getHaplotype().getSample().getPangenome());
    }

    @Test
    public void testReadFastaFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("test.fa")))) {
            Pangenome pangenome = readFasta(reader);
            assertEquals(2, pangenome.getSamples().size());
        }
    }

    @Test(expected=NullPointerException.class)
    public void testReadFastaIndexNullReadable() throws Exception {
        readFastaIndex(null);
    }

    @Test
    public void testReadFastaIndex() throws Exception {
        Pangenome pangenome = readFastaIndex(new StringReader(FASTA_INDEX));
        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertEquals("scaffold", scaffold.getName());
        assertEquals(new Long(4L), scaffold.getLength());
        assertEquals(haplotype, scaffold.getHaplotype());
        assertEquals(sample, scaffold.getHaplotype().getSample());
        assertEquals(pangenome, scaffold.getHaplotype().getSample().getPangenome());
    }

    @Test
    public void testReadFastaIndexFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("test.fa.fai")))) {
            Pangenome pangenome = readFastaIndex(reader);
            assertEquals(2, pangenome.getSamples().size());
        }
    }

    @Test(expected=NullPointerException.class)
    public void testReadSequenceDictionaryNullReadable() throws Exception {
        readSequenceDictionary(null);
    }

    @Test
    public void testReadSequenceDictionary() throws Exception {
        Pangenome pangenome = readSequenceDictionary(new StringReader(SEQUENCE_DICTIONARY));
        assertEquals(1, pangenome.getSamples().size());
        Sample sample = pangenome.getSamples().get("sample");
        assertEquals(1, sample.getHaplotypes().size());
        Haplotype haplotype = sample.getHaplotypes().get(1);
        assertEquals(1, haplotype.getScaffolds().size());
        Scaffold scaffold = haplotype.getScaffolds().get("scaffold");
        assertEquals("scaffold", scaffold.getName());
        assertEquals(new Long(4L), scaffold.getLength());
        assertEquals(haplotype, scaffold.getHaplotype());
        assertEquals(sample, scaffold.getHaplotype().getSample());
        assertEquals(pangenome, scaffold.getHaplotype().getSample().getPangenome());
    }

    @Test
    public void testReadSequenceDictionaryFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("test.fa.dict")))) {
            Pangenome pangenome = readSequenceDictionary(reader);
            assertEquals(2, pangenome.getSamples().size());
        }
    }
}
