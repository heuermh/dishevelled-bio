/*

    dsh-bio-convert-htsjdk  Convert between dishevelled and htsjdk data models.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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

import static org.junit.Assert.assertNotNull;

import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Guice;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.dishevelled.bio.variant.vcf.header.VcfFilterHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfFormatHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;
import org.dishevelled.bio.variant.vcf.header.VcfInfoHeaderLine;

/**
 * Unit test for HtsjdkModule.
 *
 * @author  Michael Heuer
 */
public final class HtsjdkModuleTest {
    private HtsjdkModule module;

    @Before
    public void setUp() {
        module = new HtsjdkModule();
    }

    @Test
    public void testConstructor() {
        assertNotNull(module);
    }

    @Test
    public void testDishevelledModule() {
        Injector injector = Guice.createInjector(module, new BdgenomicsModule(), new TestModule());
        Target target = injector.getInstance(Target.class);
        assertNotNull(target.getVcfHeaderLineNumberToVCFHeaderLineCount());
        assertNotNull(target.getVcfHeaderLineTypeToVCFHeaderLineType());
        assertNotNull(target.getVcfFilterHeaderLineToVCFFilterHeaderLine());
        assertNotNull(target.getVcfFormatHeaderLineToVCFFormatHeaderLine());
        assertNotNull(target.getVcfInfoHeaderLineToVCFInfoHeaderLine());
    }

    /**
     * Injection target.
     */
    static class Target {
        final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter;
        final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter;
        final Converter<VcfFilterHeaderLine, VCFFilterHeaderLine> filterConverter;
        final Converter<VcfFormatHeaderLine, VCFFormatHeaderLine> formatConverter;
        final Converter<VcfInfoHeaderLine, VCFInfoHeaderLine> infoConverter;

        @Inject
        Target(final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter,
               final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter,
               final Converter<VcfFilterHeaderLine, VCFFilterHeaderLine> filterConverter,
               final Converter<VcfFormatHeaderLine, VCFFormatHeaderLine> formatConverter,
               final Converter<VcfInfoHeaderLine, VCFInfoHeaderLine> infoConverter) {
            this.numberConverter = numberConverter;
            this.typeConverter = typeConverter;
            this.filterConverter = filterConverter;
            this.formatConverter = formatConverter;
            this.infoConverter = infoConverter;
        }

        Converter<VcfHeaderLineNumber, VCFHeaderLineCount> getVcfHeaderLineNumberToVCFHeaderLineCount() {
            return numberConverter;
        }

        Converter<VcfHeaderLineType, VCFHeaderLineType> getVcfHeaderLineTypeToVCFHeaderLineType() {
            return typeConverter;
        }

        Converter<VcfFilterHeaderLine, VCFFilterHeaderLine> getVcfFilterHeaderLineToVCFFilterHeaderLine() {
            return filterConverter;
        }

        Converter<VcfFormatHeaderLine, VCFFormatHeaderLine> getVcfFormatHeaderLineToVCFFormatHeaderLine() {
            return formatConverter;
        }

        Converter<VcfInfoHeaderLine, VCFInfoHeaderLine> getVcfInfoHeaderLineToVCFInfoHeaderLine() {
            return infoConverter;
        }
    }

    /**
     * Test module.
     */
    class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Target.class);
        }
    }
}
