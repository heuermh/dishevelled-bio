/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.dishevelled.bio.variant.vcf.VcfHeader;
import org.dishevelled.bio.variant.vcf.VcfReader;
import org.dishevelled.bio.variant.vcf.VcfRecord;

import org.dishevelled.bio.variant.vcf.header.VcfHeaderLineType;
import org.dishevelled.bio.variant.vcf.header.VcfHeaderLines;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * Unit test for RemapDbSnp.
 *
 * @author  Michael Heuer
 */
public final class RemapDbSnpTest {
    private File inputVcfFile;
    private File outputVcfFile;
    private RemapDbSnp remapDbSnp;

    @Before
    public void setUp() throws Exception {
        remapDbSnp = new RemapDbSnp(inputVcfFile, outputVcfFile);
    }

    @Test
    public void testConstructor() {
        assertNotNull(remapDbSnp);
    }

    @Test
    public void testValidDb() throws Exception {
        inputVcfFile = createFile("valid-db.vcf");
        outputVcfFile = File.createTempFile("remapDbSnpTest", ".vcf");
        outputVcfFile.deleteOnExit();

        int rv = new RemapDbSnp(inputVcfFile, outputVcfFile).call();
        assertEquals(0, rv);

        VcfHeader header = VcfReader.header(outputVcfFile);
        VcfHeaderLines headerLines = VcfHeaderLines.fromHeader(header);
        assertTrue(headerLines.getInfoHeaderLines().containsKey("DB"));
        assertTrue(VcfHeaderLineType.Flag.equals(headerLines.getInfoHeaderLines().get("DB").getType()));

        for (VcfRecord record : VcfReader.records(outputVcfFile)) {
            assertTrue(record.containsDb());
            assertTrue(record.getDb());
        }
    }

    @Test
    public void testInvalidDb() throws Exception {
        inputVcfFile = createFile("invalid-db.vcf");
        outputVcfFile = File.createTempFile("remapDbSnpTest", ".vcf");
        outputVcfFile.deleteOnExit();

        int rv = new RemapDbSnp(inputVcfFile, outputVcfFile).call();
        assertEquals(0, rv);

        VcfHeader header = VcfReader.header(outputVcfFile);
        VcfHeaderLines headerLines = VcfHeaderLines.fromHeader(header);
        assertTrue(headerLines.getInfoHeaderLines().containsKey("DB"));
        assertTrue(VcfHeaderLineType.Flag.equals(headerLines.getInfoHeaderLines().get("DB").getType()));
        assertTrue(headerLines.getInfoHeaderLines().containsKey("dbsnp"));
        assertTrue(VcfHeaderLineType.String.equals(headerLines.getInfoHeaderLines().get("dbsnp").getType()));

        for (VcfRecord record : VcfReader.records(outputVcfFile)) {
            assertTrue(record.containsDb());
            assertTrue(record.getDb());
            assertTrue(record.containsInfoKey("dbsnp"));
            assertNotNull(record.getInfoString("dbsnp"));
        }
    }

    private static File createFile(final String name) throws IOException {
        File file = File.createTempFile("remapDbSnpTest", ".vcf");
        Files.write(Resources.toByteArray(RemapDbSnpTest.class.getResource(name)), file);
        file.deleteOnExit();
        return file;
    }
}
