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

import static org.junit.Assert.assertNotNull;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Guice;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Read;

import org.biojava.bio.program.fastq.Fastq;

/**
 * Unit test for BiojavaModule.
 *
 * @author  Michael Heuer
 */
public final class BiojavaModuleTest {
    private BiojavaModule module;

    @Before
    public void setUp() {
        module = new BiojavaModule();
    }

    @Test
    public void testConstructor() {
        assertNotNull(module);
    }

    @Test
    public void testBiojavaModule() {
        Injector injector = Guice.createInjector(module, new TestModule());
        Target target = injector.getInstance(Target.class);
        assertNotNull(target.getBdgenomicsFastqVariantToBiojavaFastqVariant());
        assertNotNull(target.getBiojavaFastqVariantToBdgenomicsFastqVariant());
        assertNotNull(target.getBiojavaFastqToBdgenomicsRead());
        assertNotNull(target.getBdgenomicsReadToBiojavaFastq());
        assertNotNull(target.getBiojavaAlphabetToBdgenomicsAlphabet());
        assertNotNull(target.getBiojavaSequenceToBdgenomicsSequence());
        assertNotNull(target.getBdgenomicsSequenceToBiojavaSequence());
    }

    /**
     * Injection target.
     */
    static class Target {
        Converter<org.bdgenomics.formats.avro.FastqVariant, org.biojava.bio.program.fastq.FastqVariant> bdgenomicsFastqVariantToBiojavaFastqVariant;
        Converter<org.biojava.bio.program.fastq.FastqVariant, org.bdgenomics.formats.avro.FastqVariant> biojavaFastqVariantToBdgenomicsFastqVariant;
        Converter<Fastq, Read> biojavaFastqToBdgenomicsRead;
        Converter<Read, Fastq> bdgenomicsReadToBiojavaFastq;
        Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> biojavaAlphabetToBdgenomicsAlphabet;
        Converter<org.bdgenomics.formats.avro.Sequence, org.biojava.bio.seq.Sequence> bdgenomicsSequenceToBiojavaSequence;
        Converter<org.biojava.bio.seq.Sequence, org.bdgenomics.formats.avro.Sequence> biojavaSequenceToBdgenomicsSequence;

        @Inject
        Target(final Converter<org.bdgenomics.formats.avro.FastqVariant, org.biojava.bio.program.fastq.FastqVariant> bdgenomicsFastqVariantToBiojavaFastqVariant,
               final Converter<org.biojava.bio.program.fastq.FastqVariant, org.bdgenomics.formats.avro.FastqVariant> biojavaFastqVariantToBdgenomicsFastqVariant,
               final Converter<Fastq, Read> biojavaFastqToBdgenomicsRead,
               final Converter<Read, Fastq> bdgenomicsReadToBiojavaFastq,
               final Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> biojavaAlphabetToBdgenomicsAlphabet,
               final Converter<org.bdgenomics.formats.avro.Sequence, org.biojava.bio.seq.Sequence> bdgenomicsSequenceToBiojavaSequence,
               final Converter<org.biojava.bio.seq.Sequence, org.bdgenomics.formats.avro.Sequence> biojavaSequenceToBdgenomicsSequence) {

            this.bdgenomicsFastqVariantToBiojavaFastqVariant = bdgenomicsFastqVariantToBiojavaFastqVariant;
            this.biojavaFastqVariantToBdgenomicsFastqVariant = biojavaFastqVariantToBdgenomicsFastqVariant;
            this.biojavaFastqToBdgenomicsRead = biojavaFastqToBdgenomicsRead;
            this.bdgenomicsReadToBiojavaFastq = bdgenomicsReadToBiojavaFastq;
            this.biojavaAlphabetToBdgenomicsAlphabet = biojavaAlphabetToBdgenomicsAlphabet;
            this.biojavaSequenceToBdgenomicsSequence = biojavaSequenceToBdgenomicsSequence;
            this.bdgenomicsSequenceToBiojavaSequence = bdgenomicsSequenceToBiojavaSequence;
        }

        Converter<org.bdgenomics.formats.avro.FastqVariant, org.biojava.bio.program.fastq.FastqVariant> getBdgenomicsFastqVariantToBiojavaFastqVariant() {
            return bdgenomicsFastqVariantToBiojavaFastqVariant;
        }

        Converter<org.biojava.bio.program.fastq.FastqVariant, org.bdgenomics.formats.avro.FastqVariant> getBiojavaFastqVariantToBdgenomicsFastqVariant() {
            return biojavaFastqVariantToBdgenomicsFastqVariant;
        }

        Converter<Fastq, Read> getBiojavaFastqToBdgenomicsRead() {
            return biojavaFastqToBdgenomicsRead;
        }

        Converter<Read, Fastq> getBdgenomicsReadToBiojavaFastq() {
            return bdgenomicsReadToBiojavaFastq;
        }

        Converter<org.biojava.bio.symbol.Alphabet, org.bdgenomics.formats.avro.Alphabet> getBiojavaAlphabetToBdgenomicsAlphabet() {
            return biojavaAlphabetToBdgenomicsAlphabet;
        }

        Converter<org.bdgenomics.formats.avro.Sequence, org.biojava.bio.seq.Sequence> getBdgenomicsSequenceToBiojavaSequence() {
            return bdgenomicsSequenceToBiojavaSequence;
        }

        Converter<org.biojava.bio.seq.Sequence, org.bdgenomics.formats.avro.Sequence> getBiojavaSequenceToBdgenomicsSequence() {
            return biojavaSequenceToBdgenomicsSequence;
        }
    }

    /**
     * Test module.
     */
    class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Target.class);
        }
    }
}
