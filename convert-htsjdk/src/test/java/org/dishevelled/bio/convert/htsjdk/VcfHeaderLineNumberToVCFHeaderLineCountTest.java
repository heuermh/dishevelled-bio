/*

    dsh-bio-convert-htsjdk  Convert between dishevelled and htsjdk data models.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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
package org.dishevelled.bio.convert.htsjdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import htsjdk.variant.vcf.VCFHeaderLineCount;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VcfHeaderLineNumberToVCFHeaderLineCount.
 *
 * @author  Michael Heuer
 */
public final class VcfHeaderLineNumberToVCFHeaderLineCountTest {
    private final Logger logger = LoggerFactory.getLogger(VcfHeaderLineNumberToVCFHeaderLineCountTest.class);
    private Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter;

    @Before
    public void setUp() {
        numberConverter = new VcfHeaderLineNumberToVCFHeaderLineCount();
    }

    @Test
    public void testConstructor() {
        assertNotNull(numberConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        numberConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(numberConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(numberConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test(expected=ConversionException.class)
    public void testConvertNumericStrict() {
        numberConverter.convert(VcfHeaderLineNumber.valueOf("1"), ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNumericLenient() {
        assertNull(numberConverter.convert(VcfHeaderLineNumber.valueOf("1"), ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNumericSilent() {
        assertNull(numberConverter.convert(VcfHeaderLineNumber.valueOf("1"), ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        assertEquals(VCFHeaderLineCount.A, numberConverter.convert(VcfHeaderLineNumber.valueOf("A"), ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineCount.R, numberConverter.convert(VcfHeaderLineNumber.valueOf("R"), ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineCount.G, numberConverter.convert(VcfHeaderLineNumber.valueOf("G"), ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineCount.UNBOUNDED, numberConverter.convert(VcfHeaderLineNumber.valueOf("."), ConversionStringency.STRICT, logger));
    }
}
