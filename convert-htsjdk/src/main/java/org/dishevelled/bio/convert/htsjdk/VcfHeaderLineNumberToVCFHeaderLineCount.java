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

import htsjdk.variant.vcf.VCFHeaderLineCount;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineNumber;

import org.slf4j.Logger;

/**
 * Convert VcfHeaderLineNumber to VCFHeaderLineCount.
 *
 * @author  Michael Heuer
 */
final class VcfHeaderLineNumberToVCFHeaderLineCount extends AbstractConverter<VcfHeaderLineNumber, VCFHeaderLineCount> {

    /**
     * Package private constructor.
     */
    VcfHeaderLineNumberToVCFHeaderLineCount() {
        super(VcfHeaderLineNumber.class, VCFHeaderLineCount.class);
    }

    @Override
    public VCFHeaderLineCount convert(final VcfHeaderLineNumber headerLineNumber,
                                      final ConversionStringency stringency,
                                      final Logger logger) throws ConversionException {
        if (headerLineNumber == null) {
            warnOrThrow(headerLineNumber, "must not be null", null, stringency, logger);
            return null;
        }
        if (headerLineNumber.isNumeric()) {
            warnOrThrow(headerLineNumber, "must not be numeric", null, stringency, logger);
            return null;
        }
        if ("A".equals(headerLineNumber.getName())) {
            return VCFHeaderLineCount.A;
        }
        else if ("R".equals(headerLineNumber.getName())) {
            return VCFHeaderLineCount.R;
        }
        else if ("G".equals(headerLineNumber.getName())) {
            return VCFHeaderLineCount.G;
        }
        return VCFHeaderLineCount.UNBOUNDED;
    }
}
