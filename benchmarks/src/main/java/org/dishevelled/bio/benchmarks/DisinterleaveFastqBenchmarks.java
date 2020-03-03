/*

    dsh-bio-benchmarks.  Benchmarks.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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

import org.dishevelled.bio.tools.DisinterleaveFastq;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * DisinterleaveFastq benchmarks.
 *
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class DisinterleaveFastqBenchmarks {
    private File pairedFile;
    private File firstFastqFile;
    private File secondFastqFile;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        pairedFile = File.createTempFile("disinterleaveFastqBenchmarks", ".ifq.gz");
        firstFastqFile = File.createTempFile("disinterleaveFastqBenchmarks", ".fq.gz");
        secondFastqFile = File.createTempFile("disinterleaveFastqBenchmarks", ".fq.gz");

        copyResource("NIST7035_TAAGGCGA_L002.20000.0.ifq.gz", pairedFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        pairedFile.delete();
        firstFastqFile.delete();
        secondFastqFile.delete();
    }

    @Benchmark
    public void disinterleaveFastq() throws Exception {
        new DisinterleaveFastq(pairedFile, null, firstFastqFile, secondFastqFile).call();
    }
}
