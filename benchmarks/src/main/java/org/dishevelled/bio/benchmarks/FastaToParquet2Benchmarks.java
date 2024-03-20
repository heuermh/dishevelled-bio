/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.dishevelled.bio.tools.FastaToParquet2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * FastaToParquet2 benchmarks.
 *
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class FastaToParquet2Benchmarks {
    private Path fastaPath;
    private File parquetFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        //fastaPath = Paths.get("..", "mgy_proteins_1.fa.gz");
        fastaPath = Paths.get("..", "RefSeq.complete.1012.protein.faa.gz");
        parquetFile = File.createTempFile("fastaToParquetBenchmarks2", ".parquet");
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        parquetFile.delete();
    }

    @Benchmark
    public void fastaToParquetShortTransactionSize() throws Exception {
        new FastaToParquet2(fastaPath, parquetFile, "aa", 100000, 204800L).call();
    }

    @Benchmark
    public void fastaToParquetLongTransactionSize() throws Exception {
        new FastaToParquet2(fastaPath, parquetFile, "aa", 100000, 2048000L).call();
    }
}
