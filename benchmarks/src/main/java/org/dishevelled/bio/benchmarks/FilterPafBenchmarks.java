/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import static org.dishevelled.bio.benchmarks.Utils.copyResource;

import java.io.File;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.tools.FilterPaf;
import org.dishevelled.bio.tools.FilterPaf.MappingQualityFilter;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * FilterPaf benchmarks.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class FilterPafBenchmarks {
    private File inputPafFile;
    private File outputPafFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        inputPafFile = File.createTempFile("filterPafBenchmarks", ".paf.gz");
        outputPafFile = File.createTempFile("filterPafBenchmarks", ".paf.gz");

        copyResource("A-3105.fa.gz.pggb-s3000-p70-n10-a70-K11-k8-w10000-j5000-W0-e5000.paf.gz", inputPafFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        inputPafFile.delete();
        outputPafFile.delete();
    }

    @Benchmark
    public void filterPafByMappingQuality() throws Exception {
        new FilterPaf(ImmutableList.of(new MappingQualityFilter(30)), inputPafFile, outputPafFile).call();
    }
}
