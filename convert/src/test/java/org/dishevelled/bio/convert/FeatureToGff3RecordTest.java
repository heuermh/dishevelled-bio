/*

    dsh-bio-convert  Convert between dishevelled and bdg-formats data models.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

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

import org.dishevelled.bio.feature.Gff3Record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for FeatureToGff3Record.
 *
 * @author  Michael Heuer
 */
public final class FeatureToGff3RecordTest {
    private final Logger logger = LoggerFactory.getLogger(FeatureToGff3RecordTest.class);
    private Converter<Dbxref, String> dbxrefConverter;
    private Converter<OntologyTerm, String> ontologyTermConverter;
    private Converter<Strand, String> strandConverter;
    private Converter<Feature, Gff3Record> featureConverter;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new BdgenomicsModule());
        dbxrefConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<Dbxref, String>>() {}));
        ontologyTermConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<OntologyTerm, String>>() {}));
        strandConverter = injector.getInstance(Key.get(new TypeLiteral<Converter<Strand, String>>() {}));
        featureConverter = new FeatureToGff3Record(dbxrefConverter, ontologyTermConverter, strandConverter);
    }

    @Test
    public void testConstructor() {
        assertNotNull(featureConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullDbxrefConverter() {
        new FeatureToGff3Record(null, ontologyTermConverter, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOntologyTermConverter() {
        new FeatureToGff3Record(dbxrefConverter, null, strandConverter);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrandConverter() {
        new FeatureToGff3Record(dbxrefConverter, ontologyTermConverter, null);
    }

    @Test(expected=ConversionException.class)
    public void testConvertNullStrict() {
        featureConverter.convert(null, ConversionStringency.STRICT, logger);
    }

    @Test
    public void testConvertNullLenient() {
        assertNull(featureConverter.convert(null, ConversionStringency.LENIENT, logger));
    }

    @Test
    public void testConvertNullSilent() {
        assertNull(featureConverter.convert(null, ConversionStringency.SILENT, logger));
    }

    @Test
    public void testConvert() {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("biotype", "protein_coding");

        Feature feature = Feature.newBuilder()
            .setContigName("1")
            .setStart(1335275L)
            .setEnd(1349350L)
            .setName("ENSG00000107404")
            .setFeatureId("ENSG00000107404")
            .setFeatureType("gene")
            .setSource("Ensembl")
            .setStrand(Strand.REVERSE)
            .setAttributes(attributes)
            .build();

        Gff3Record gff3Record = featureConverter.convert(feature, ConversionStringency.STRICT, logger);
        assertEquals("1", gff3Record.getSeqid());
        assertEquals(1335275L, gff3Record.getStart());
        assertEquals(1349350L, gff3Record.getEnd());
        assertEquals("gene", gff3Record.getFeatureType());
        assertEquals("Ensembl", gff3Record.getSource());
        assertEquals("-", gff3Record.getStrand());
        assertTrue(gff3Record.getAttributes().get("ID").contains("ENSG00000107404"));
        assertTrue(gff3Record.getAttributes().get("Name").contains("ENSG00000107404"));
        assertTrue(gff3Record.getAttributes().get("biotype").contains("protein_coding"));
    }
}
