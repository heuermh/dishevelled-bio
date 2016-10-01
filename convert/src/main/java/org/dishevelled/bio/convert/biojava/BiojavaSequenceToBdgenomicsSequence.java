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

import org.slf4j.Logger;

/**
 * Convert Biojava 1.x Sequence to bdg-formats Sequence.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class BiojavaSequenceToBdgenomicsSequence extends AbstractConverter<org.biojava.bio.seq.Sequence, org.bdgenomics.formats.avro.Sequence> {

    /** Convert Biojava 1.x Alphabet to bdg-formats Alphabet. */
    private final Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter;


    /**
     * Convert Biojava 1.x Sequence to bdg-formats Sequence.
     *
     * @param alphabetConverter convert Biojava 1.x Alphabet to bdg-formats Alphabet, must not be null
     */
    BiojavaSequenceToBdgenomicsSequence(final Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter) {
        super(org.biojava.bio.seq.Sequence.class, org.bdgenomics.formats.avro.Sequence.class);
        checkNotNull(alphabetConverter);
        this.alphabetConverter = alphabetConverter;
    }


    @Override
    public org.bdgenomics.formats.avro.Sequence convert(final org.biojava.bio.seq.Sequence sequence,
                                                        final ConversionStringency stringency,
                                                        final Logger logger) throws ConversionException {

        if (sequence == null) {
            warnOrThrow(sequence, "must not be null", null, stringency, logger);
            return null;
        }

        org.bdgenomics.formats.avro.Sequence.Builder sb = org.bdgenomics.formats.avro.Sequence.newBuilder()
            .setName(sequence.getName())
            .setSequence(sequence.seqString())
            .setLength(Long.valueOf(sequence.length()));

        org.bdgenomics.formats.avro.Alphabet alphabet = alphabetConverter.convert(sequence.getAlphabet(), stringency, logger);
        if (alphabet != null) {
            sb.setAlphabet(alphabet);
        }

        String description = descriptionFor(sequence);
        if (description != null) {
            sb.setDescription(description);
        }

        return sb.build();
    }

    static String descriptionFor(final org.biojava.bio.seq.Sequence sequence) {
        if (sequence.getAnnotation().containsProperty(FastaFormat.PROPERTY_DESCRIPTIONLINE)) {
            return (String) sequence.getAnnotation().getProperty(FastaFormat.PROPERTY_DESCRIPTIONLINE);
        }
        // todo: where is description stored in EMBL/Genbank formats?
        return null;
    }
}
