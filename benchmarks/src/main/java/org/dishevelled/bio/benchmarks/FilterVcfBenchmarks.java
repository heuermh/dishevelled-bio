/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import static org.dishevelled.bio.benchmarks.Utils.copyResource;

import java.io.File;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.tools.FilterVcf;
import org.dishevelled.bio.tools.FilterVcf.QualFilter;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * FilterVcf benchmarks.
 *
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class FilterVcfBenchmarks {
    private File inputVcfFile;
    private File outputVcfFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        inputVcfFile = File.createTempFile("filterVcfBenchmarks", ".vcf.gz");
        outputVcfFile = File.createTempFile("filterVcfBenchmarks", ".vcf.gz");

        copyResource("HG001_GRCh38_GIAB_highconf_CG-IllFB-IllGATKHC-Ion-10X-SOLID_CHROM1-X_v.3.3.2_highconf_PGandRTGphasetransfer.10k.0.vcf.gz", inputVcfFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        inputVcfFile.delete();
        outputVcfFile.delete();
    }

    @Benchmark
    public void filterVcfByQualityScore() throws Exception {
        new FilterVcf(ImmutableList.of(new QualFilter(30.0d)), inputVcfFile, outputVcfFile).call();
    }
}
