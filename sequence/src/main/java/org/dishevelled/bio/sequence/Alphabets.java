/*

    dsh-bio-sequence  Sequences.
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
package org.dishevelled.bio.sequence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Utility methods on sequence alphabets.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public final class Alphabets {

    /**
     * Translate the specified source string using the specified translation table.
     *
     * @param source source string, must not be null
     * @param table translation table, must not be null
     * @return the specified source string translated using the specified translation table
     */
    static String translate(final String source, final Map<Character, Character> table) {
        checkNotNull(source);
        checkNotNull(table);

        StringBuilder target = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (table.containsKey(c)) {
                target.append(table.get(c));
            }
        }
        return target.toString();
    }

    /**
     * Create and return a new translation table for the specified source and target strings.
     *
     * @param source source string, must not be null
     * @param target target string, must not be null, and must be the same length as the source string
     * @return a new translation table for the specified source and target strings
     */
    static Map<Character, Character> createTranslationTable(final String source, final String target) {
        checkNotNull(source);
        checkNotNull(target);
        checkArgument(source.length() == target.length(), "source and target must be the same length");

        ImmutableMap.Builder<Character, Character> builder = new ImmutableMap.Builder<Character, Character>();
        for (int i = 0; i < source.length(); i++) {
            builder = builder.put(source.charAt(i), target.charAt(i));
        }
        return builder.build();
    }


    // refs
    //
    // UNIPROT18, WAS14
    // Ieremie et al., Protein language models meet reduced amino acid alphabets.
    // https://doi.org/10.1093/bioinformatics/btae061
    //
    // WWMJ5
    // Wang J, Wang W. A computational approach to simplifying the protein folding alphabet.
    //
    // GBMR4, GBMR7
    // Solis AD, Rackovsky S. Optimized representations and maximal information in proteins.
    //
    // MMSEQS12
    // Steinegger M, Soding J. Mmseqs2 enables sensitive protein sequence searching for the analysis of massive data sets.
    //

    // todo: J ambiguity symbol, ILE+LEU ?

    /** hsdm17 translation table, <code>A D KE R N T S Q Y F LIV M C W H G P</code>. */
    static final Map<Character, Character> HSDM17 = createTranslationTable("ADKERNTSQYFLIVMCWHGPUOBZX", "ADKKRNTSQYFLLLMCWHGPCKXXX");

    /** gbmr4 translation table, <code>ADKERNTSQ YFLIVMCWH G P</code>. */
    static final Map<Character, Character> GBMR4 = createTranslationTable("ADKERNTSQYFLIVMCWHGPUOBZX", "AAAAAAAAAYYYYYYYYYGPYAXXX");

    /** gbmr7 translation table, <code>DN AEFIKLMQRVWY CH T S G P</code>. */
    static final Map<Character, Character> GBMR7 = createTranslationTable("DNAEFIKLMQRVWYCHTSGPUOBZX", "DDAAAAAAAAAAAACCTSGPCAXXX");

    /** mmseqs12 translation table, <code>AST LM IV KR EQ ND FY C G H P W</code>. */
    static final Map<Character, Character> MMSEQS12 = createTranslationTable("ASTLMIVKREQNDFYCGHPWUOBZX", "AAALLIIKKEENNFFCGHPWCKXXX");

    /** sdm12 translation table, <code>A D KER N TSQ YF LIVM C W H G P</code>. */
    static final Map<Character, Character> SDM12 = createTranslationTable("ADKERNTSQYFLIVMCWHGPUOBZX", "ADKKKNTTTYYLLLLCWHGPCKXXX");

    /** Uniprot18 translation table, <code>A R N D C Q EP G HL I K M F S T W Y V</code>. */
    static final Map<Character, Character> UNIPROT18 = createTranslationTable("ARNDCQEGHILKMFPSTWYVUOBZX", "ARNDCQEGHIHKMFESTWYVCKXXX");

    /** Uniprot20 translation table, <code>A R N D C Q E G H I L K M F P S T W Y V</code>. */
    static final Map<Character, Character> UNIPROT20 = createTranslationTable("ARNDCQEGHILKMFPSTWYVUOBZX", "ARNDCQEGHILKMFPSTWYVCKXXX");

    /** wass14 translation table, <code>WM DI P C AV K T RE G L Y SH F NQ</code>. */
    static final Map<Character, Character> WASS14 = createTranslationTable("WMDIPCAVKTREGLYSHFNQUOBZX", "WWDDPCAAKTRRGLYSSFNNCKXXX");

    /** wwmj5 translation table, <code>CMFILVWY ATH GP DE SNQRK</code>. */
    static final Map<Character, Character> WWMJ5 = createTranslationTable("CMFILVWYATHGPDESNQRKUOBZX", "CCCCCCCCAAAGGDDSSSSSCSXXX");


    /**
     * Return the specified protein sequence translated to the <code>gbmr4</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>gbmr4</code> reduced amino acid alphabet
     */
    public static String gbmr4(final String protein) {
        return translate(protein, GBMR4);
    }

    /**
     * Return the specified protein sequence translated to the <code>gbmr7</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>gbmr7</code> reduced amino acid alphabet
     */
    public static String gbmr7(final String protein) {
        return translate(protein, GBMR7);
    }

    /**
     * Return the specified protein sequence translated to the <code>hsdm17</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>hsdm17</code> reduced amino acid alphabet
     */
    public static String hsdm17(final String protein) {
        return translate(protein, HSDM17);
    }

    /**
     * Return the specified protein sequence translated to the <code>mmseqs12</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>mmseqs12</code> reduced amino acid alphabet
     */
    public static String mmseqs12(final String protein) {
        return translate(protein, MMSEQS12);
    }

    /**
     * Return the specified protein sequence translated to the <code>uniprot18</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>uniprot18</code> reduced amino acid alphabet
     */
    public static String uniprot18(final String protein) {
        return translate(protein, UNIPROT18);
    }

    /**
     * Return the specified protein sequence translated to the <code>uniprot20</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>uniprot20</code> reduced amino acid alphabet
     */
    public static String uniprot20(final String protein) {
        return translate(protein, UNIPROT20);
    }

    /**
     * Return the specified protein sequence translated to the <code>sdm12</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>sdm12</code> reduced amino acid alphabet
     */
    public static String sdm12(final String protein) {
        return translate(protein, SDM12);
    }

    /**
     * Return the specified protein sequence translated to the <code>wass14</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>wass14</code> reduced amino acid alphabet
     */
    public static String wass14(final String protein) {
        return translate(protein, WASS14);
    }

    /**
     * Return the specified protein sequence translated to the <code>wwmj5</code> reduced amino acid alphabet.
     *
     * @param protein protein sequence, must not be null
     * @return the specified protein sequence translated to the <code>wwmj5</code> reduced amino acid alphabet
     */
    public static String wwmj5(final String protein) {
        return translate(protein, WWMJ5);
    }
}
