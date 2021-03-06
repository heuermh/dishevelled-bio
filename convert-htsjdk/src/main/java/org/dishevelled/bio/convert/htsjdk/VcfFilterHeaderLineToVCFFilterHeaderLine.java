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

import htsjdk.variant.vcf.VCFFilterHeaderLine;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.dishevelled.bio.variant.vcf.header.VcfFilterHeaderLine;

import org.slf4j.Logger;

/**
 * Convert VcfFilterHeaderLine to VCFFilterHeaderLine.
 *
 * @author  Michael Heuer
 */
final class VcfFilterHeaderLineToVCFFilterHeaderLine extends AbstractConverter<VcfFilterHeaderLine, VCFFilterHeaderLine> {

    /**
     * Package private constructor.
     */
    VcfFilterHeaderLineToVCFFilterHeaderLine() {
        super(VcfFilterHeaderLine.class, VCFFilterHeaderLine.class);
    }

    @Override
    public VCFFilterHeaderLine convert(final VcfFilterHeaderLine filterHeaderLine,
                                       final ConversionStringency stringency,
                                       final Logger logger) throws ConversionException {
        if (filterHeaderLine == null) {
            warnOrThrow(filterHeaderLine, "must not be null", null, stringency, logger);
            return null;
        }
        return new VCFFilterHeaderLine(filterHeaderLine.getId(), filterHeaderLine.getDescription());
    }
}
