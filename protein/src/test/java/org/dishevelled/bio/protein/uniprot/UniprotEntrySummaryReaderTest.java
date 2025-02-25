/*

    dsh-bio-protein  Protein sequences and metadata.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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
package org.dishevelled.bio.protein.uniprot;

import static org.dishevelled.bio.protein.uniprot.UniprotEntrySummaryReader.stream;

import static org.dishevelled.compress.Readers.reader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for UniprotEntrySummaryReader.
 *
 * @author  Michael Heuer
 */
public final class UniprotEntrySummaryReaderTest {
    private File uniprotXmlFile;
    private Callback callback;

    @Before
    public void setUp() throws IOException {
        uniprotXmlFile = File.createTempFile("uniprotEntrySummaryReaderTest", ".xml");
        callback = new Callback();
    }

    @After
    public void tearDown() {
        uniprotXmlFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReader() throws Exception {
        stream(null, callback);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullCallback() throws Exception {
        copyResource("uniprot_sprot_empty.xml", uniprotXmlFile);
        stream(reader(uniprotXmlFile), null);
    }

    @Test
    public void testStreamEmpty() throws Exception {
        copyResource("uniprot_sprot_empty.xml", uniprotXmlFile);
        stream(reader(uniprotXmlFile), callback);
        assertNull(callback.getEntrySummary());
    }

    @Test
    public void testStreamOneEntry() throws Exception {
        copyResource("uniprot_sprot_entry.xml", uniprotXmlFile);
        stream(reader(uniprotXmlFile), callback);
        assertNotNull(callback.getEntrySummary());
    }

    @Test(expected=IOException.class)
    public void testStreamError() throws Exception {
        copyResource("uniprot_sprot_error.xml", uniprotXmlFile);
        stream(reader(uniprotXmlFile), callback);
    }

    /**
     * Callback.
     */
    static final class Callback implements EntrySummaryListener {
        private EntrySummary entrySummary;

        @Override
        public boolean entrySummary(final EntrySummary entrySummary) {
            this.entrySummary = entrySummary;
            return true;
        }

        EntrySummary getEntrySummary() {
            return entrySummary;
        }
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(UniprotEntrySummaryReaderTest.class.getResource(name)), file);
    }
}
