/*

    dsh-bio-convert-htsjdk  Convert between dishevelled and htsjdk data models.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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

import javax.annotation.concurrent.Immutable;

import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.bdgenomics.convert.Converter;

import org.dishevelled.bio.variant.vcf.header.VcfFilterHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfFormatHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;
import org.dishevelled.bio.variant.vcf.header.VcfInfoHeaderLine;

/**
 * Guice module for the org.dishevelled.bio.convert.htsjdk package.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class HtsjdkModule extends AbstractModule {
    @Override
    protected void configure() {
        // empty
    }

    @Provides @Singleton
    Converter<VcfHeaderLineNumber, VCFHeaderLineCount> createVcfHeaderLineNumberToVCFHeaderLineCount() {
        return new VcfHeaderLineNumberToVCFHeaderLineCount();
    }

    @Provides @Singleton
    Converter<VcfHeaderLineType, VCFHeaderLineType> createVcfHeaderLineTypeToVCFHeaderLineType() {
        return new VcfHeaderLineTypeToVCFHeaderLineType();
    }

    @Provides @Singleton
    Converter<VcfFilterHeaderLine, VCFFilterHeaderLine> createVcfFilterHeaderLineToVCFFilterHeaderLine() {
        return new VcfFilterHeaderLineToVCFFilterHeaderLine();
    }

    @Provides @Singleton
    Converter<VcfFormatHeaderLine, VCFFormatHeaderLine> createVcfFormatHeaderLineToVCFFormatHeaderLine(final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter,
                                                                                                       final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter) {
        return new VcfFormatHeaderLineToVCFFormatHeaderLine(numberConverter, typeConverter);
    }

    @Provides @Singleton
    Converter<VcfInfoHeaderLine, VCFInfoHeaderLine> createVcfInfoHeaderLineToVCFInfoHeaderLine(final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter,
                                                                                               final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter) {
        return new VcfInfoHeaderLineToVCFInfoHeaderLine(numberConverter, typeConverter);
    }
}
