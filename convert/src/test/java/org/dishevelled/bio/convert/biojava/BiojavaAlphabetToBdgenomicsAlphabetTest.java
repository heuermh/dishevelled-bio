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

import org.biojava.bio.symbol.SimpleAlphabet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for BiojavaAlphabetToBdgenomicsAlphabet.
 *
 * @author  Michael Heuer
 */
public final class BiojavaAlphabetToBdgenomicsAlphabetTest {
    private final Logger logger = LoggerFactory.getLogger(BiojavaAlphabetToBdgenomicsAlphabetTest.class);
    private Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter;

    @Before
    public void setUp() {
        alphabetConverter = new BiojavaAlphabetToBdgenomicsAlphabet();
    }

    @Test
    public void testConstructor() {
        assertNotNull(alphabetConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        alphabetConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(alphabetConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(alphabetConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertUnknownAlphabetStrict() {
        alphabetConverter.convert(new SimpleAlphabet(), ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertUnknownAlphabetLenient() {
        assertNull(alphabetConverter.convert(new SimpleAlphabet(), ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertUnknownAlphabetSilent() {
        assertNull(alphabetConverter.convert(new SimpleAlphabet(), ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        assertEquals(org.bdgenomics.formats.avro.Alphabet.DNA,
                     alphabetConverter.convert(DNATools.getDNA(), ConversionStringency.STRICT, logger));

        assertEquals(org.bdgenomics.formats.avro.Alphabet.RNA,
                     alphabetConverter.convert(RNATools.getRNA(), ConversionStringency.STRICT, logger));

        assertEquals(org.bdgenomics.formats.avro.Alphabet.PROTEIN,
                     alphabetConverter.convert(ProteinTools.getAlphabet(), ConversionStringency.STRICT, logger));

        assertEquals(org.bdgenomics.formats.avro.Alphabet.PROTEIN,
                     alphabetConverter.convert(ProteinTools.getTAlphabet(), ConversionStringency.STRICT, logger));
    }
}
