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

import java.io.File;

import java.util.concurrent.Callable;

import java.util.regex.Pattern;

/**
 * Abstract rename references callable.
 *
 * @author  Michael Heuer
 */
abstract class AbstractRenameReferences implements Callable<Integer> {
    private final boolean chr;
    protected final File inputFile;
    protected final File outputFile;
    private static final Pattern AUTOSOMAL = Pattern.compile("^([0-9]+)$");
    private static final Pattern SEX = Pattern.compile("^([XYZW])$");
    private static final Pattern MITOCHONDRIAL = Pattern.compile("^[chrM,MT]$");
    private static final Pattern CHR = Pattern.compile("^chr(.+)$");
    private static final Pattern CHRUN_ = Pattern.compile("^chrUn_(.+)$");
    private static final Pattern V = Pattern.compile("([0-9]+)v([0-9]+)");


    /**
     * Create a new rename references callable.
     *
     * @param chr true to add "chr" to chromosome names
     * @param inputFile input file, if any
     * @param outputFile output file, if any
     */
    protected AbstractRenameReferences(final boolean chr, final File inputFile, final File outputFile) {
        this.chr = chr;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }


    /**
     * Add "chr" to the specified reference name.
     *
     * @param referenceName reference name
     * @return the specified reference name with "chr" added, with various workarounds
     *    for sex and mitochondrial chromosomes
     */
    protected static String addChr(final String referenceName) {
        String result = referenceName;

        // 1 --> chr1
        result = AUTOSOMAL.matcher(result).replaceAll("chr$1");
        // X --> chrX
        result = SEX.matcher(result).replaceAll("chr$1");
        // MT --> chrM
        result = MITOCHONDRIAL.matcher(result).replaceAll("chrM");

        return result;
    }

    /**
     * Remove "chr" from the specified reference name.
     *
     * @param referenceName reference name
     * @return the specified reference name with "chr" removed, with various workarounds
     *    for sex and autosomal chromosomes, unplaced contigs, and misversioned reference
     *    names
     */
    protected static String removeChr(final String referenceName) {
        String result = referenceName;

        // 123v1 --> 123.1
        result = V.matcher(result).replaceAll("$1.$2");
        // chrUn_GL00 --> GL00
        result = CHRUN_.matcher(result).replaceAll("$1");
        // chrM --> MT
        result = MITOCHONDRIAL.matcher(result).replaceAll("MT");
        // chr1 --> 1
        result = CHR.matcher(result).replaceAll("$1");

        return result;
    }

    /**
     * Rename the specified reference name.
     *
     * @param referenceName reference name, if any
     * @return the specified reference name with "chr" added or removed
     */
    protected String rename(final String referenceName) {
        if (referenceName == null) {
            return null;
        }
        return chr ? addChr(referenceName) : removeChr(referenceName);
    }
}
