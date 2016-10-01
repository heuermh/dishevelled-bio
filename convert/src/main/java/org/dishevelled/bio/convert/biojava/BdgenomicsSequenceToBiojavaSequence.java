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
import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.biojava.bio.seq.io.FastaFormat;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;

import org.biojava.bio.symbol.IllegalSymbolException;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Sequence to Biojava 1.x Sequence.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class BdgenomicsSequenceToBiojavaSequence extends AbstractConverter<org.bdgenomics.formats.avro.Sequence, org.biojava.bio.seq.Sequence> {

    /**
     * Convert bdg-formats Sequence to Biojava 1.x Sequence.
     */
    BdgenomicsSequenceToBiojavaSequence() {
        super(org.bdgenomics.formats.avro.Sequence.class, org.biojava.bio.seq.Sequence.class);
    }


    @Override
    public org.biojava.bio.seq.Sequence convert(final org.bdgenomics.formats.avro.Sequence bdgenomicsSequence,
                                                final ConversionStringency stringency,
                                                final Logger logger) throws ConversionException {

        if (bdgenomicsSequence == null) {
            warnOrThrow(bdgenomicsSequence, "must not be null", null, stringency, logger);
            return null;
        }

        org.biojava.bio.seq.Sequence biojavaSequence = null;
        try {
            switch (bdgenomicsSequence.getAlphabet()) {
            case DNA:
                biojavaSequence = DNATools.createDNASequence(bdgenomicsSequence.getSequence(), bdgenomicsSequence.getName());
                break;
            case RNA:
                biojavaSequence = RNATools.createRNASequence(bdgenomicsSequence.getSequence(), bdgenomicsSequence.getName());
                break;
            case PROTEIN:
                biojavaSequence = ProteinTools.createProteinSequence(bdgenomicsSequence.getSequence(), bdgenomicsSequence.getName());
                break;
            default:
                break;
            }

            if (biojavaSequence != null && bdgenomicsSequence.getDescription() != null) {
                biojavaSequence.getAnnotation().setProperty(FastaFormat.PROPERTY_DESCRIPTIONLINE, bdgenomicsSequence.getDescription());
            }
        }
        catch (IllegalSymbolException e) {
            warnOrThrow(bdgenomicsSequence, "", e, stringency, logger);
        }
        return biojavaSequence;
    }
}
