/*

    dsh-bio-convert-htsjdk  Convert between dishevelled and htsjdk data models.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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

import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.dishevelled.bio.variant.vcf.header.VcfFormatHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VcfFormatHeaderLineToVCFFormatHeaderLine.
 *
 * @author  Michael Heuer
 */
public final class VcfFormatHeaderLineToVCFFormatHeaderLineTest {
    private final Logger logger = LoggerFactory.getLogger(VcfFormatHeaderLineToVCFFormatHeaderLineTest.class);
    private Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter;
    private Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter;
    private Converter<VcfFormatHeaderLine, VCFFormatHeaderLine> formatConverter;

    @Before
    public void setUp() {
        numberConverter = new VcfHeaderLineNumberToVCFHeaderLineCount();
        typeConverter = new VcfHeaderLineTypeToVCFHeaderLineType();
        formatConverter = new VcfFormatHeaderLineToVCFFormatHeaderLine(numberConverter, typeConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(formatConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullNumberConverter() {
        new VcfFormatHeaderLineToVCFFormatHeaderLine(null, typeConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullTypeConverter() {
        new VcfFormatHeaderLineToVCFFormatHeaderLine(numberConverter, null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        formatConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(formatConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(formatConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        VCFFormatHeaderLine expected = new VCFFormatHeaderLine("GQ", VCFHeaderLineCount.A, VCFHeaderLineType.Integer, "Genotype Quality");
        assertEquals(expected, formatConverter.convert(VcfFormatHeaderLine.valueOf("##FORMAT=<ID=GQ,Number=A,Type=Integer,Description=\"Genotype Quality\">"), ConversionStringency.STRICT, logger));
    }

    @Test
    public void testConvertNumeric() {
        VCFFormatHeaderLine expected = new VCFFormatHeaderLine("GQ", 1, VCFHeaderLineType.Integer, "Genotype Quality");
        assertEquals(expected, formatConverter.convert(VcfFormatHeaderLine.valueOf("##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">"), ConversionStringency.STRICT, logger));
    }
}
