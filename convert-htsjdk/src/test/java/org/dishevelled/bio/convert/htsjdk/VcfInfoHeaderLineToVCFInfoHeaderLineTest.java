/*

    dsh-bio-convert-htsjdk  Convert between dishevelled and htsjdk data models.
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
package org.dishevelled.bio.convert.htsjdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.dishevelled.bio.variant.vcf.header.VcfFormatHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;
import org.dishevelled.bio.variant.vcf.header.VcfInfoHeaderLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VcfInfoHeaderLineToVCFInfoHeaderLine.
 *
 * @author  Michael Heuer
 */
public final class VcfInfoHeaderLineToVCFInfoHeaderLineTest {
    private final Logger logger = LoggerFactory.getLogger(VcfInfoHeaderLineToVCFInfoHeaderLineTest.class);
    private Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter;
    private Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter;
    private Converter<VcfInfoHeaderLine, VCFInfoHeaderLine> infoConverter;

    @Before
    public void setUp() {
        numberConverter = new VcfHeaderLineNumberToVCFHeaderLineCount();
        typeConverter = new VcfHeaderLineTypeToVCFHeaderLineType();
        infoConverter = new VcfInfoHeaderLineToVCFInfoHeaderLine(numberConverter, typeConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(infoConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullNumberConverter() {
        new VcfInfoHeaderLineToVCFInfoHeaderLine(null, typeConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullTypeConverter() {
        new VcfInfoHeaderLineToVCFInfoHeaderLine(numberConverter, null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        infoConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(infoConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(infoConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        VCFInfoHeaderLine expected = new VCFInfoHeaderLine("AC", VCFHeaderLineCount.A, VCFHeaderLineType.Integer, "Allele count in genotypes, for each ALT allele, in the same order as listed");
        assertEquals(expected, infoConverter.convert(VcfInfoHeaderLine.valueOf("##INFO=<ID=AC,Number=A,Type=Integer,Description=\"Allele count in genotypes, for each ALT allele, in the same order as listed\">"), ConversionStringency.STRICT, logger));
    }

    @Test
    public void testConvertNumeric() {
        VCFInfoHeaderLine expected = new VCFInfoHeaderLine("AC", 1, VCFHeaderLineType.Integer, "Allele count in genotypes, for each ALT allele, in the same order as listed");
        assertEquals(expected, infoConverter.convert(VcfInfoHeaderLine.valueOf("##INFO=<ID=AC,Number=1,Type=Integer,Description=\"Allele count in genotypes, for each ALT allele, in the same order as listed\">"), ConversionStringency.STRICT, logger));
    }
}
