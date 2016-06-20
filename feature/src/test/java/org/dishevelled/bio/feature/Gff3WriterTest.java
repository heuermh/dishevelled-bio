/*

    dsh-bio-feature  Sequence features.
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
package org.dishevelled.bio.feature;

import static org.junit.Assert.assertEquals;

import static org.dishevelled.bio.feature.Gff3Reader.read;
import static org.dishevelled.bio.feature.Gff3Writer.write;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Gff3Writer.
 */
public final class Gff3WriterTest {
    private Gff3Record record;
    private Iterable<Gff3Record> records;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void setUp() throws Exception {
        records = read(new StringReader("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding"));
        record = records.iterator().next();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @After
    public void tearDown() throws Exception {
        writer.close();
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullRecord() throws Exception {
        write((Gff3Record) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullWriter() throws Exception {
        write(record, null);
    }

    @Test
    public void testWrite() throws Exception {
        write(record, writer);
        assertEquals("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding", stringWriter.toString().trim());
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullRecords() throws Exception {
        write((Iterable<Gff3Record>) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullWriter() throws Exception {
        write(records, null);
    }

    @Test
    public void testWriteIterable() throws Exception {
        write(records, writer);
        assertEquals("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding", stringWriter.toString().trim());
    }
}
