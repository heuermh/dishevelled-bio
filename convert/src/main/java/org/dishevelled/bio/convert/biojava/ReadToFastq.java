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

import org.bdgenomics.formats.avro.QualityScoreVariant;
import org.bdgenomics.formats.avro.Read;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;
import org.biojava.bio.program.fastq.FastqVariant;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Read to Biojava 1.x Fastq.
 *
 * @author  Michael Heuer
 */
@Immutable
final class ReadToFastq extends AbstractConverter<Read, Fastq> {

    /** Convert bdg-formats QualityScoreVariant to Biojava 1.x FastqVariant. */
    final Converter<QualityScoreVariant, FastqVariant> fastqVariantConverter;


    /**
     * Package private no-arg constructor.
     *
     * @param fastqVariantConverter convert bdg-formats QualityScoreVariant to Biojava 1.x FastqVariant, must not be null
     */
    ReadToFastq(final Converter<QualityScoreVariant, FastqVariant> fastqVariantConverter) {
        super(Read.class, Fastq.class);
        checkNotNull(fastqVariantConverter);
        this.fastqVariantConverter = fastqVariantConverter;
    }


    @Override
    public Fastq convert(final Read read,
                         final ConversionStringency stringency,
                         final Logger logger) throws ConversionException {

        if (read == null) {
            warnOrThrow(read, "must not be null", null, stringency, logger);
            return null;
        }
        Fastq fastq = null;
        try {
            fastq = new FastqBuilder()
                .withDescription(description(read.getName(), read.getDescription()))
                .withSequence(read.getSequence())
                .withQuality(read.getQualityScores())
                .withVariant(fastqVariantConverter.convert(read.getQualityScoreVariant(), stringency, logger))
                .build();
        }
        catch (NullPointerException e) {
            warnOrThrow(read, "could not convert read", e, stringency, logger); 
        }
        catch (IllegalArgumentException e) {
            warnOrThrow(read, "could not convert read", e, stringency, logger);
        }
        return fastq;
    }

    static String description(final String name, final String description) {
        if (name == null && description == null) {
            return null;
        }
        if (description == null) {
            return name;
        }
        if (name == null) {
            return description;
        }
        return name + " " + description;
    }
}
