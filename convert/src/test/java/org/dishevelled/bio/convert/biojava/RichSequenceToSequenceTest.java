/*

    dsh-convert  Convert between various data models.
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
package org.dishevelled.bio.convert.biojava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Sequence;

import org.biojava.bio.symbol.AlphabetManager;

import org.biojavax.bio.seq.RichSequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for RichSequenceToSequence.
 *
 * @author  Michael Heuer
 */
public final class RichSequenceToSequenceTest {
    private final Logger logger = LoggerFactory.getLogger(RichSequenceToSequenceTest.class);
    private RichSequence dnaSequence;
    private RichSequence rnaSequence;
    private RichSequence proteinSequence;
    private Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter;
    private Converter<RichSequence, Sequence> sequenceConverter;

    @Before
    public void setUp() throws Exception {
        alphabetConverter = new BiojavaAlphabetToBdgenomicsAlphabet();
        sequenceConverter = new RichSequenceToSequence(alphabetConverter);

        dnaSequence = RichSequence.Tools.createRichSequence("dna", "actg", AlphabetManager.alphabetForName("DNA"));
        dnaSequence.setDescription("dna description");

        rnaSequence = RichSequence.Tools.createRichSequence("rna", "acug", AlphabetManager.alphabetForName("RNA"));
        proteinSequence = RichSequence.Tools.createRichSequence("protein", "mgws", AlphabetManager.alphabetForName("PROTEIN"));
    }

    @Test
    public void testConstructor() {
        assertNotNull(sequenceConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullAlphabetConverter() {
        new RichSequenceToSequence(null);
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
        Sequence sequence = sequenceConverter.convert(dnaSequence, ConversionStringency.STRICT, logger);

        assertEquals("dna", sequence.getName());
        assertEquals("actg", sequence.getSequence());
        assertEquals(Long.valueOf(4L), sequence.getLength());
        assertEquals(org.bdgenomics.formats.avro.Alphabet.DNA, sequence.getAlphabet());
        assertEquals("dna description", sequence.getDescription());
    }

    @Test
    public void testConvertRnaSequence() {
        Sequence sequence = sequenceConverter.convert(rnaSequence, ConversionStringency.STRICT, logger);
        assertEquals(org.bdgenomics.formats.avro.Alphabet.RNA, sequence.getAlphabet());
    }

    @Test
    public void testConvertProteinSequence() {
        Sequence sequence = sequenceConverter.convert(proteinSequence, ConversionStringency.STRICT, logger);
        assertEquals(org.bdgenomics.formats.avro.Alphabet.PROTEIN, sequence.getAlphabet());
    }
}
