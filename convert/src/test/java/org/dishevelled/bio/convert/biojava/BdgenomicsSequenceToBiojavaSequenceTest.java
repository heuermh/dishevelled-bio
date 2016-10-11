/*

    dsh-convert  Convert between various data models.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.convert.biojava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for BdgenomicsSequenceToBiojavaSequence.
 *
 * @author  Michael Heuer
 */
public final class BdgenomicsSequenceToBiojavaSequenceTest {
    private final Logger logger = LoggerFactory.getLogger(BdgenomicsSequenceToBiojavaSequenceTest.class);
    private org.bdgenomics.formats.avro.Sequence dnaSequence;
    private org.bdgenomics.formats.avro.Sequence rnaSequence;
    private org.bdgenomics.formats.avro.Sequence proteinSequence;
    private Converter<org.bdgenomics.formats.avro.Sequence, org.biojava.bio.seq.Sequence> sequenceConverter;

    @Before
    public void setUp() {
        sequenceConverter = new BdgenomicsSequenceToBiojavaSequence();

        dnaSequence = org.bdgenomics.formats.avro.Sequence.newBuilder()
            .setName("DNA sequence")
            .setSequence("actg")
            .setAlphabet(org.bdgenomics.formats.avro.Alphabet.DNA)
            .build();

        rnaSequence = org.bdgenomics.formats.avro.Sequence.newBuilder()
            .setName("RNA sequence")
            .setSequence("acug")
            .setAlphabet(org.bdgenomics.formats.avro.Alphabet.RNA)
            .build();

        proteinSequence = org.bdgenomics.formats.avro.Sequence.newBuilder()
            .setName("Protein sequence")
            .setSequence("mgws")
            .setAlphabet(org.bdgenomics.formats.avro.Alphabet.PROTEIN)
            .build();
    }

    @Test
    public void testConstructor() {
        assertNotNull(sequenceConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        sequenceConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(sequenceConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(sequenceConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvertDnaSequence() {
        org.biojava.bio.seq.Sequence sequence = sequenceConverter.convert(dnaSequence, ConversionStringency.STRICT, logger);
        assertEquals(DNATools.getDNA(), sequence.getAlphabet());
    }

    @Test
    public void testConvertRnaSequence() {
        org.biojava.bio.seq.Sequence sequence = sequenceConverter.convert(rnaSequence, ConversionStringency.STRICT, logger);
        assertEquals(RNATools.getRNA(), sequence.getAlphabet());
    }

    @Test
    public void testConvertProteinSequence() {
        org.biojava.bio.seq.Sequence sequence = sequenceConverter.convert(proteinSequence, ConversionStringency.STRICT, logger);
        assertEquals(ProteinTools.getTAlphabet(), sequence.getAlphabet());
    }
}
