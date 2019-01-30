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

import htsjdk.variant.vcf.VCFHeaderLineType;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;

import org.slf4j.Logger;

/**
 * Convert VcfHeaderLineType to VCFHeaderLineType.
 *
 * @author  Michael Heuer
 */
final class VcfHeaderLineTypeToVCFHeaderLineType extends AbstractConverter<VcfHeaderLineType, VCFHeaderLineType> {

    /**
     * Package private constructor.
     */
    VcfHeaderLineTypeToVCFHeaderLineType() {
        super(VcfHeaderLineType.class, VCFHeaderLineType.class);
    }

    @Override
    public VCFHeaderLineType convert(final VcfHeaderLineType headerLineType,
                                     final ConversionStringency stringency,
                                     final Logger logger) throws ConversionException {
        if (headerLineType == null) {
            warnOrThrow(headerLineType, "must not be null", null, stringency, logger);
            return null;
        }
        if (headerLineType.equals(VcfHeaderLineType.Character)) {
            return VCFHeaderLineType.Character;
        }
        else if (headerLineType.equals(VcfHeaderLineType.Flag)) {
            return VCFHeaderLineType.Flag;
        }
        else if (headerLineType.equals(VcfHeaderLineType.Float)) {
            return VCFHeaderLineType.Float;
        }
        else if (headerLineType.equals(VcfHeaderLineType.Integer)) {
            return VCFHeaderLineType.Integer;
        }
        return VCFHeaderLineType.String;
    }
}
