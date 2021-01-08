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

import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFFormatHeaderLine;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;
import org.bdgenomics.convert.Converter;

import org.dishevelled.bio.variant.vcf.header.VcfFormatHeaderLine;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;

import org.slf4j.Logger;

/**
 * Convert VcfFormatHeaderLine to VCFFormatHeaderLine.
 *
 * @author  Michael Heuer
 */
final class VcfFormatHeaderLineToVCFFormatHeaderLine extends AbstractConverter<VcfFormatHeaderLine, VCFFormatHeaderLine> {

    /** Convert VcfHeaderLineNumber to VCFHeaderLineCount. */
    private final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter;

    /** Convert VcfHeaderLineType to VCFHeaderLineType. */
    private final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter;


    /**
     * Package private constructor.
     */
    VcfFormatHeaderLineToVCFFormatHeaderLine(final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter,
                                             final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter) {

        super(VcfFormatHeaderLine.class, VCFFormatHeaderLine.class);

        checkNotNull(numberConverter);
        checkNotNull(typeConverter);
        this.numberConverter = numberConverter;
        this.typeConverter = typeConverter;
    }

    @Override
    public VCFFormatHeaderLine convert(final VcfFormatHeaderLine formatHeaderLine,
                                       final ConversionStringency stringency,
                                       final Logger logger) throws ConversionException {
        if (formatHeaderLine == null) {
            warnOrThrow(formatHeaderLine, "must not be null", null, stringency, logger);
            return null;
        }
        if (formatHeaderLine.getNumber().isNumeric()) {
            return new VCFFormatHeaderLine(formatHeaderLine.getId(),
                                           formatHeaderLine.getNumber().getValue(),
                                           typeConverter.convert(formatHeaderLine.getType(), stringency, logger),
                                           formatHeaderLine.getDescription());
        }
        return new VCFFormatHeaderLine(formatHeaderLine.getId(),
                                       numberConverter.convert(formatHeaderLine.getNumber(), stringency, logger),
                                       typeConverter.convert(formatHeaderLine.getType(), stringency, logger),
                                       formatHeaderLine.getDescription());
    }
}
