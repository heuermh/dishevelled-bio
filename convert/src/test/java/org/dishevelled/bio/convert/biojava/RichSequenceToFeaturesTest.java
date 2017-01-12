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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.List;

import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.Before;
import org.junit.Test;

import org.bdgenomics.convert.Converter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.convert.bdgenomics.BdgenomicsModule;

import org.bdgenomics.formats.avro.Dbxref;
import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.OntologyTerm;
import org.bdgenomics.formats.avro.Strand;

import org.biojava.bio.seq.StrandedFeature;

import org.biojava.bio.symbol.AlphabetManager;

import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for RichSequenceToFeatures.
 *
 * @author  Michael Heuer
 */
public final class RichSequenceToFeaturesTest {
    private final Logger logger = LoggerFactory.getLogger(RichSequenceToFeaturesTest.class);
    private RichSequence sequence;
    private Converter<String, Dbxref> dbxrefConverter;
    private Converter<String, OntologyTerm> ontologyTermConverter;
    private Converter<String, Strand> strandConverter;
    private Converter<RichSequence, List<Feature>> sequenceConverter;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        dbxrefConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, Dbxref>>() {}));
        ontologyTermConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, OntologyTerm>>() {}));
        strandConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, Strand>>() {}));
        sequenceConverter = new RichSequenceToFeatures(dbxrefConverter, ontologyTermConverter, strandConverter);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("SCU49845.gb")));
            RichSequenceIterator iterator = RichSequence.IOTools.readGenbankDNA(reader, null);
            sequence = iterator.nextRichSequence();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull(sequenceConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullDbxrefConverter() {
        new RichSequenceToFeatures(null, ontologyTermConverter, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOntologyTermConverter() {
        new RichSequenceToFeatures(dbxrefConverter, null, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrandConverter() {
        new RichSequenceToFeatures(dbxrefConverter, ontologyTermConverter, null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        sequenceConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(sequenceConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(sequenceConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        List<Feature> features = sequenceConverter.convert(sequence, ConversionStringency.STRICT, logger);
        assertNotNull(features);
        assertFalse(features.isEmpty());

        Feature source = features.get(0);
        assertEquals("source", source.getFeatureType());
        assertEquals(Long.valueOf(0L), source.getStart());
        assertEquals(Long.valueOf(5028L), source.getEnd());

        Feature mRNA = features.get(1);
        assertEquals("mRNA", mRNA.getFeatureType());
        assertEquals(Long.valueOf(0L), mRNA.getStart());
        assertEquals(Long.valueOf(206L), mRNA.getEnd());

        Feature cds = features.get(2);
        assertEquals("CDS", cds.getFeatureType());
        assertEquals(Long.valueOf(0L), cds.getStart());
        assertEquals(Long.valueOf(206L), cds.getEnd());
    }
}
