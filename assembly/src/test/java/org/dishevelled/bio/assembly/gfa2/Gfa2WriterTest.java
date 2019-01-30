/*

    dsh-bio-assembly  Assemblies.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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
package org.dishevelled.bio.assembly.gfa2;

import static org.junit.Assert.assertEquals;

import static org.dishevelled.bio.assembly.gfa2.Gfa2Reader.read;
import static org.dishevelled.bio.assembly.gfa2.Gfa2Writer.write;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Gfa2Writer.
 *
 * @author  Michael Heuer
 */
public class Gfa2WriterTest {
    private Gfa2Record record;
    private Iterable<Gfa2Record> records;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void setUp() throws Exception {
        records = read(new StringReader("H\tVN:Z:2.0\nS\t1\t6871\t*\tRC:i:2200067"));
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
        write((Gfa2Record) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullWriter() throws Exception {
        write(record, null);
    }

    @Test
    public void testWrite() throws Exception {
        write(record, writer);
        assertEquals("H\tVN:Z:2.0", stringWriter.toString().trim());
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullRecords() throws Exception {
        write((Iterable<Gfa2Record>) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullWriter() throws Exception {
        write(records, null);
    }

    @Test
    public void testWriteIterable() throws Exception {
        write(records, writer);
        assertEquals("H\tVN:Z:2.0\nS\t1\t6871\t*\tRC:i:2200067", stringWriter.toString().trim());
    }
}
