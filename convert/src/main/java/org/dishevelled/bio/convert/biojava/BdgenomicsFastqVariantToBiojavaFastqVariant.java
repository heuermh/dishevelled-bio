/*

    dsh-convert  Convert between various data models.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.convert.biojava;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.slf4j.Logger;

/**
 * Convert bdg-formats FastqVariant to Biojava 1.x FastqVariant.
 *
 * @author  Michael Heuer
 */
@Immutable
final class BdgenomicsFastqVariantToBiojavaFastqVariant extends AbstractConverter<org.bdgenomics.formats.avro.FastqVariant, org.biojava.bio.program.fastq.FastqVariant> {

    /**
     * Package private no-arg constructor.
     */
    BdgenomicsFastqVariantToBiojavaFastqVariant() {
        super(org.bdgenomics.formats.avro.FastqVariant.class, org.biojava.bio.program.fastq.FastqVariant.class);
    }


    @Override
    public org.biojava.bio.program.fastq.FastqVariant convert(final org.bdgenomics.formats.avro.FastqVariant fastqVariant,
                                                              final ConversionStringency stringency,
                                                              final Logger logger) throws ConversionException {

        if (fastqVariant == null) {
            warnOrThrow(fastqVariant, "must not be null", null, stringency, logger);
            return null;
        }
        if (fastqVariant == org.bdgenomics.formats.avro.FastqVariant.ILLUMINA) {
            return org.biojava.bio.program.fastq.FastqVariant.FASTQ_ILLUMINA;
        }
        else if (fastqVariant == org.bdgenomics.formats.avro.FastqVariant.SOLEXA) {
            return org.biojava.bio.program.fastq.FastqVariant.FASTQ_SOLEXA;
        }
        return org.biojava.bio.program.fastq.FastqVariant.FASTQ_SANGER;
    }
}
