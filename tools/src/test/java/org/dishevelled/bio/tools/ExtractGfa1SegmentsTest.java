/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2021 held jointly by the individual authors.

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
package org.dishevelled.bio.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import java.nio.charset.Charset;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for ExtractGfa1Segments.
 *
 * @author  Michael Heuer
 */
public final class ExtractGfa1SegmentsTest {
    private File inputGfa1File;
    private File outputFastaFile;

    @Before
    public void setUp() throws IOException {
        inputGfa1File = File.createTempFile("extractGfa1SegmentsTest", ".gfa");
        outputFastaFile = File.createTempFile("extractGfa1SegmentsTest", "fa");
    }

    @After
    public void tearDown() {
        inputGfa1File.delete();
        outputFastaFile.delete();
    }

    @Test
    public void testConstructor() {
        assertNotNull(new ExtractGfa1Segments(inputGfa1File, outputFastaFile, ExtractGfa1Segments.DEFAULT_LINE_WIDTH));
    }

    @Test
    public void testExtractGfa1Segments() throws Exception {
        copyResource("segments.gfa", inputGfa1File);
        new ExtractGfa1Segments(inputGfa1File, outputFastaFile, ExtractGfa1Segments.DEFAULT_LINE_WIDTH).call();

        String description = Files.asCharSource(outputFastaFile, Charset.forName("UTF-8")).readFirstLine();
        assertEquals(">2 LN:i:2\tRC:i:50\tFC:i:100\tKC:i:0\tzz:Z:Test", description);
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(ExtractGfa1SegmentsTest.class.getResource(name)), file);
    }
}
