/*

    dsh-bio-benchmarks.  Benchmarks.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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

import org.dishevelled.bio.tools.InterleaveFastq;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * InterleaveFastq benchmarks.
 *
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class InterleaveFastqBenchmarks {
    private File firstFastqFile;
    private File secondFastqFile;
    private File pairedFile;
    private File unpairedFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        firstFastqFile = File.createTempFile("interleaveFastqBenchmarks", ".fq.gz");
        secondFastqFile = File.createTempFile("interleaveFastqBenchmarks", ".fq.gz");
        pairedFile = File.createTempFile("interleaveFastqBenchmarks", ".ifq.gz");
        unpairedFile = File.createTempFile("interleaveFastqBenchmarks", ".fq.gz");

        copyResource("NIST7035_TAAGGCGA_L002_R1_001_trimmed.10000.0.fq.gz", firstFastqFile);
        copyResource("NIST7035_TAAGGCGA_L002_R2_001_trimmed.10000.0.fq.gz", secondFastqFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        firstFastqFile.delete();
        secondFastqFile.delete();
        pairedFile.delete();
        unpairedFile.delete();
    }

    @Benchmark
    public void interleaveFastq() throws Exception {
        new InterleaveFastq(firstFastqFile, secondFastqFile, pairedFile, unpairedFile).call();
    }
}
