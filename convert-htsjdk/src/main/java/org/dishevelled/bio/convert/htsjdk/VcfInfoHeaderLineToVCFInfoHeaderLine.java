/*

    dsh-bio-convert-htsjdk  Convert between dishevelled and htsjdk data models.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;
import org.bdgenomics.convert.Converter;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;
import org.dishevelled.bio.variant.vcf.header.VcfInfoHeaderLine;

import org.slf4j.Logger;

/**
 * Convert VcfInfoHeaderLine to VCFInfoHeaderLine.
 *
 * @author  Michael Heuer
 */
final class VcfInfoHeaderLineToVCFInfoHeaderLine extends AbstractConverter<VcfInfoHeaderLine, VCFInfoHeaderLine> {
    /** Convert VcfHeaderLineNumber to VCFHeaderLineCount. */
    private final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter;

    /** Convert VcfHeaderLineType to VCFHeaderLineType. */
    private final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter;


    /**
     * Package private constructor.
     */
    VcfInfoHeaderLineToVCFInfoHeaderLine(final Converter<VcfHeaderLineNumber, VCFHeaderLineCount> numberConverter,
                                         final Converter<VcfHeaderLineType, VCFHeaderLineType> typeConverter) {

        super(VcfInfoHeaderLine.class, VCFInfoHeaderLine.class);

        checkNotNull(numberConverter);
        checkNotNull(typeConverter);
        this.numberConverter = numberConverter;
        this.typeConverter = typeConverter;
    }

    @Override
    public VCFInfoHeaderLine convert(final VcfInfoHeaderLine infoHeaderLine,
                                     final ConversionStringency stringency,
                                     final Logger logger) throws ConversionException {
        if (infoHeaderLine == null) {
            warnOrThrow(infoHeaderLine, "must not be null", null, stringency, logger);
            return null;
        }
        if (infoHeaderLine.getNumber().isNumeric()) {
            return new VCFInfoHeaderLine(infoHeaderLine.getId(),
                                         infoHeaderLine.getNumber().getValue(),
                                         typeConverter.convert(infoHeaderLine.getType(), stringency, logger),
                                         infoHeaderLine.getDescription());
        }
        return new VCFInfoHeaderLine(infoHeaderLine.getId(),
                                     numberConverter.convert(infoHeaderLine.getNumber(), stringency, logger),
                                     typeConverter.convert(infoHeaderLine.getType(), stringency, logger),
                                     infoHeaderLine.getDescription());
    }
}
