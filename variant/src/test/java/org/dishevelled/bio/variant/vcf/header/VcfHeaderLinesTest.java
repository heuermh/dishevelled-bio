/*

    dsh-bio-variant  Variants.
    Copyright (c) 2013-2024 held jointly by the individual authors.

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
package org.dishevelled.bio.variant.vcf.header;

import static org.junit.Assert.assertNotNull;

import static org.dishevelled.bio.variant.vcf.VcfReader.header;

import java.io.File;
import java.io.IOException;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.dishevelled.bio.variant.vcf.VcfHeader;

import org.junit.Test;

/**
 * Unit test for VcfHeaderLines.
 *
 * @author  Michael Heuer
 */
public final class VcfHeaderLinesTest {
    private static final List<String> VCF_FILES = ImmutableList.of
    (
        "../ALL.chr22.phase1_release_v3.20101123.snps_indels_svs.genotypes-2-indv-thin-20000bp-trim.vcf",
        "../ceph-bwa-j-gatk-haplotype-joint.excerpt.vcf",
        "../hapmap-info.vcf",
        "../gatk-example.gvcf",
        "../gatk-2.6-example.eff.vcf",
        "../pedigree.vcf",
        "../samples.vcf"
     );

    @Test
    public void testVcfHeaderLines() throws Exception {
        for (String file : VCF_FILES) {
            VcfHeader header = header(createFile(file));
            VcfHeaderLines headerLines = VcfHeaderLines.fromHeader(header);
            assertNotNull(headerLines);
        }
    }

    private static File createFile(final String name) throws IOException {
        File file = File.createTempFile("vcfHeaderLinesTest", ".vcf");
        Files.write(Resources.toByteArray(VcfHeaderLinesTest.class.getResource(name)), file);
        file.deleteOnExit();
        return file;
    }
}
