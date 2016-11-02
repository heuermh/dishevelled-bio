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

import org.bdgenomics.formats.avro.Read;
import org.bdgenomics.formats.avro.QualityScoreVariant;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;
import org.biojava.bio.program.fastq.FastqVariant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for FastqToRead.
 *
 * @author  Michael Heuer
 */
public final class FastqToReadTest {
    private final Logger logger = LoggerFactory.getLogger(FastqToReadTest.class);
    private Converter<FastqVariant, QualityScoreVariant> fastqVariantConverter;
    private Converter<Fastq, Read> fastqConverter;

    @Before
    public void setUp() throws Exception {
        fastqVariantConverter = new FastqVariantToQualityScoreVariant();
        fastqConverter = new FastqToRead(fastqVariantConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(fastqConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFastqVariantConverter() {
        new FastqToRead(null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        fastqConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(fastqConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(fastqConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        Fastq fastq = new FastqBuilder()
            .withDescription("read/1")
            .withSequence("actg")
            .withQuality("e896")
            .withVariant(FastqVariant.FASTQ_SANGER)
            .build();

        Read read = fastqConverter.convert(fastq, ConversionStringency.STRICT, logger);
        assertEquals(fastq.getDescription(), read.getName());
        assertEquals(fastq.getSequence(), read.getSequence());
        assertEquals(fastq.getQuality(), read.getQualityScores());
        assertEquals(org.bdgenomics.formats.avro.Alphabet.DNA, read.getAlphabet());
        assertEquals(QualityScoreVariant.FASTQ_SANGER, read.getQualityScoreVariant());
    }
}
