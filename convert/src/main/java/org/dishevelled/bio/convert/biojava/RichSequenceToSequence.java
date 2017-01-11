/*

    dsh-convert  Convert between various data models.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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

import org.biojavax.bio.seq.RichSequence;

import org.bdgenomics.formats.avro.Sequence;

import org.slf4j.Logger;

/**
 * Convert Biojava 1.x RichSequence to bdg-formats Sequence.
 *
 * @author  Michael Heuer
 */
@Immutable
final class RichSequenceToSequence extends AbstractConverter<RichSequence, Sequence> {

    /** Convert Biojava 1.x Alphabet to bdg-formats Alphabet. */
    private final Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter;


    /**
     * Convert Biojava 1.x RichSequence to bdg-formats Sequence.
     *
     * @param alphabetConverter convert Biojava 1.x Alphabet to bdg-formats Alphabet, must not be null
     */
    RichSequenceToSequence(final Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter) {
        super(RichSequence.class, Sequence.class);
        checkNotNull(alphabetConverter);
        this.alphabetConverter = alphabetConverter;
    }


    @Override
    public Sequence convert(final RichSequence richSequence,
                            final ConversionStringency stringency,
                            final Logger logger) throws ConversionException {

        if (richSequence == null) {
            warnOrThrow(richSequence, "must not be null", null, stringency, logger);
            return null;
        }

        Sequence.Builder sb = Sequence.newBuilder()
            .setName(richSequence.getName())
            .setSequence(richSequence.seqString())
            .setLength(Long.valueOf(richSequence.length()));

        org.bdgenomics.formats.avro.Alphabet alphabet = alphabetConverter.convert(richSequence.getAlphabet(), stringency, logger);
        if (alphabet != null) {
            sb.setAlphabet(alphabet);
        }

        String description = descriptionFor(richSequence);
        if (description != null) {
            sb.setDescription(description);
        }

        return sb.build();
    }

    static String descriptionFor(final RichSequence richSequence) {
        return richSequence.getDescription();
    }
}
