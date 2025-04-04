/*

    dsh-bio-tools  Command line tools.
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
package org.dishevelled.bio.tools;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.commons.math3.distribution.BinomialDistribution;

import org.apache.commons.math3.random.MersenneTwister;

import org.junit.Test;
import org.junit.Before;

/**
 * Unit test for DownsampleFastq.
 *
 * @author  Michael Heuer
 */
public final class DownsampleFastqTest {
    private File inputFastqFile;
    private File outputFastqFile;
    private BinomialDistribution distribution;

    @Before
    public void setUp() {
        distribution = new BinomialDistribution(new MersenneTwister(), 1, 0.5d);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullDistribution() {
        new DownsampleFastq(inputFastqFile, outputFastqFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new DownsampleFastq(inputFastqFile, outputFastqFile, distribution));
    }
}
