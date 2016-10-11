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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for BdgenomicsFastqVariantToBiojavaFastqVariant.
 *
 * @author  Michael Heuer
 */
public final class BdgenomicsFastqVariantToBiojavaFastqVariantTest {
    private final Logger logger = LoggerFactory.getLogger(BdgenomicsFastqVariantToBiojavaFastqVariantTest.class);
    private Converter<org.bdgenomics.formats.avro.FastqVariant, org.biojava.bio.program.fastq.FastqVariant> fastqVariantConverter;

    @Before
    public void setUp() {
        fastqVariantConverter = new BdgenomicsFastqVariantToBiojavaFastqVariant();
    }

    @Test
    public void testConstructor() {
        assertNotNull(fastqVariantConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        fastqVariantConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(fastqVariantConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(fastqVariantConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        assertEquals(org.biojava.bio.program.fastq.FastqVariant.FASTQ_SANGER,
                     fastqVariantConverter.convert(org.bdgenomics.formats.avro.FastqVariant.SANGER, ConversionStringency.STRICT, logger));

        assertEquals(org.biojava.bio.program.fastq.FastqVariant.FASTQ_SOLEXA,
                     fastqVariantConverter.convert(org.bdgenomics.formats.avro.FastqVariant.SOLEXA, ConversionStringency.STRICT, logger));

        assertEquals(org.biojava.bio.program.fastq.FastqVariant.FASTQ_ILLUMINA,
                     fastqVariantConverter.convert(org.bdgenomics.formats.avro.FastqVariant.ILLUMINA, ConversionStringency.STRICT, logger));
    }
}
