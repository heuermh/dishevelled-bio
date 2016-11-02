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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.QualityScoreVariant;
import org.bdgenomics.formats.avro.Read;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqVariant;

/**
 * Guice module for the org.dishevelled.bio.convert.biojava package.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class BiojavaModule extends AbstractModule {
    @Override
    protected void configure() {
        // empty
    }

    @Provides @Singleton
    Converter<QualityScoreVariant, FastqVariant> createQualityScoreVariantToFastqVariant() {
        return new QualityScoreVariantToFastqVariant();
    }

    @Provides @Singleton
    Converter<FastqVariant, QualityScoreVariant> createFastqVariantToQualityScoreVariant() {
        return new FastqVariantToQualityScoreVariant();
    }

    @Provides @Singleton
    Converter<Fastq, Read> createFastqToRead(final Converter<FastqVariant, QualityScoreVariant> fastqVariantConverter) {
        return new FastqToRead(fastqVariantConverter);
    }

    @Provides @Singleton
    Converter<Read, Fastq> createReadToFastq(final Converter<QualityScoreVariant, FastqVariant> fastqVariantConverter) {
        return new ReadToFastq(fastqVariantConverter);
    }

    @Provides @Singleton
    Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> createBiojavaAlphabetToBdgenomicsAlphabet() {
        return new BiojavaAlphabetToBdgenomicsAlphabet();
    }

    @Provides @Singleton
    Converter<org.bdgenomics.formats.avro.Sequence, org.biojava.bio.seq.Sequence> createBdgenomicsSequenceToBiojavaSequence() {
        return new BdgenomicsSequenceToBiojavaSequence();
    }

    @Provides @Singleton
    Converter<org.biojava.bio.seq.Sequence, org.bdgenomics.formats.avro.Sequence> createBiojavaSequenceToBdgenomicsSequence(final Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> alphabetConverter) {
        return new BiojavaSequenceToBdgenomicsSequence(alphabetConverter);
    }
}
