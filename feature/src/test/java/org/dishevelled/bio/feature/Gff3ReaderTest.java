/*

    dsh-bio-feature  Sequence features.
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
package org.dishevelled.bio.feature;

import static org.junit.Assert.assertNotNull;

import static org.dishevelled.bio.feature.Gff3Reader.read;
import static org.dishevelled.bio.feature.Gff3Reader.stream;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Gff3Reader.
 *
 * @author  Michael Heuer
 */
public final class Gff3ReaderTest {
    private Readable readable;

    @Before
    public void setUp() {
        readable = new StringReader("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding");
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullReadable() throws Exception {
        read(null);
    }

    @Test
    public void testRead() throws Exception {
        for (Gff3Record record : read(readable)) {
            assertNotNull(record);
        }
    }

    @Test(expected=IOException.class)
    public void testReadInvalid() throws Exception {
        read(new StringReader("invalid"));
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream(null, new Gff3Listener() {
                @Override
                public boolean record(final Gff3Record record) {
                    return true;
                }
            });
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, new Gff3Listener() {
                @Override
                public boolean record(final Gff3Record record) {
                    assertNotNull(record);
                    return true;
                }
            });
    }
}
