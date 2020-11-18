/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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
package org.dishevelled.bio.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

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

import org.dishevelled.bio.feature.gff3.Gff3Record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for Gff3RecordToFeature.
 *
 * @author  Michael Heuer
 */
public final class Gff3RecordToFeatureTest {
    private final Logger logger = LoggerFactory.getLogger(Gff3RecordToFeatureTest.class);
    private Converter<String, Dbxref> dbxrefConverter;
    private Converter<String, OntologyTerm> ontologyTermConverter;
    private Converter<String, Strand> strandConverter;
    private Converter<Gff3Record, Feature> gff3RecordConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        dbxrefConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, Dbxref>>() {}));
        ontologyTermConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, OntologyTerm>>() {}));
        strandConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<String, Strand>>() {}));
        gff3RecordConverter = new Gff3RecordToFeature(dbxrefConverter, ontologyTermConverter, strandConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(gff3RecordConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullDbxrefConverter() {
        new Gff3RecordToFeature(null, ontologyTermConverter, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOntologyTermConverter() {
        new Gff3RecordToFeature(dbxrefConverter, null, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrandConverter() {
        new Gff3RecordToFeature(dbxrefConverter, ontologyTermConverter, null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        gff3RecordConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(gff3RecordConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(gff3RecordConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        Gff3Record gff3Record = Gff3Record.valueOf("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding");
        Feature feature = gff3RecordConverter.convert(gff3Record, ConversionStringency.STRICT, logger);
        assertEquals("1", feature.getReferenceName());
        assertEquals(Long.valueOf(1335275L), feature.getStart());
        assertEquals(Long.valueOf(1349350L), feature.getEnd());
        assertEquals("ENSG00000107404", feature.getName());
        assertEquals("ENSG00000107404", feature.getFeatureId());
        assertEquals("gene", feature.getFeatureType());
        assertEquals("Ensembl", feature.getSource());
        assertNull(feature.getScore());
        assertEquals(Strand.REVERSE, feature.getStrand());
        assertEquals("protein_coding", feature.getAttributes().get("biotype"));
    }
}
