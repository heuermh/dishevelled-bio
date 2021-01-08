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

import htsjdk.variant.vcf.VCFHeaderLineType;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for VcfHeaderLineTypeToVCFHeaderLineType.
 *
 * @author  Michael Heuer
 */
public final class VcfHeaderLineTypeToVCFHeaderLineTypeTest {
    private final Logger logger = LoggerFactory.getLogger(VcfHeaderLineTypeToVCFHeaderLineTypeTest.class);
    private Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter;

    @Before
    public void setUp() {
        typeConverter = new VcfHeaderLineTypeToVCFHeaderLineType();
    }

    @Test
    public void testConstructor() {
        assertNotNull(typeConverter);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        typeConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(typeConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(typeConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        assertEquals(VCFHeaderLineType.Character, typeConverter.convert(VcfHeaderLineType.Character, ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineType.Flag, typeConverter.convert(VcfHeaderLineType.Flag, ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineType.Float, typeConverter.convert(VcfHeaderLineType.Float, ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineType.Integer, typeConverter.convert(VcfHeaderLineType.Integer, ConversionStringency.STRICT, logger));
        assertEquals(VCFHeaderLineType.String, typeConverter.convert(VcfHeaderLineType.String, ConversionStringency.STRICT, logger));
    }
}
