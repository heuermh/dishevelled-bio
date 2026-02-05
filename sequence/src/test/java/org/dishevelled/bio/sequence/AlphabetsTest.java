/*

    dsh-bio-sequence  Sequences.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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

import static org.dishevelled.bio.sequence.Alphabets.dayhoff ;
import static org.dishevelled.bio.sequence.Alphabets.dayhoff6;
import static org.dishevelled.bio.sequence.Alphabets.gbmr4;
import static org.dishevelled.bio.sequence.Alphabets.gbmr7;
import static org.dishevelled.bio.sequence.Alphabets.hp ;
import static org.dishevelled.bio.sequence.Alphabets.hp2;
import static org.dishevelled.bio.sequence.Alphabets.hsdm17;
import static org.dishevelled.bio.sequence.Alphabets.mmseqs12;
import static org.dishevelled.bio.sequence.Alphabets.sdm12;
import static org.dishevelled.bio.sequence.Alphabets.uniprot20;
import static org.dishevelled.bio.sequence.Alphabets.uniprot18;
import static org.dishevelled.bio.sequence.Alphabets.wass14;
import static org.dishevelled.bio.sequence.Alphabets.wwmj5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for Alphabets.
 *
 * @author  Michael Heuer
 */
public final class AlphabetsTest {

    static final String DVL1_HUMAN = "MAETKIIYHMDEEETPYLVKLPVAPERVTLADFKNVLSNRPVHAYKFFFKSMDQDFGVVK";

    @Test(expected=NullPointerException.class)
    public void testUniprot20NullProtein() {
        uniprot20(null);
    }

    @Test
    public void testUniprot20EmptyProtein() {
        assertEquals("", uniprot20(""));
    }

    @Test
    public void testUniprot20() {
        assertEquals("ARNDCQEGHILKMFPSTWYVCKXXX", uniprot20("ARNDCQEGHILKMFPSTWYVUOBZX"));
    }

    @Test
    public void testUniprot20NotInTranslationTable() {
        assertEquals("ARNDCQEGHILKMFPSTWYVCKXXX", uniprot20("ARNDCQEGHILKMFPSTWYVUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testUniprot20Dvl1() {
        assertEquals(DVL1_HUMAN, uniprot20(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testUniprot18NullProtein() {
        uniprot18(null);
    }

    @Test
    public void testUniprot18EmptyProtein() {
        assertEquals("", uniprot18(""));
    }

    @Test
    public void testUniprot18() {
        assertEquals("ARNDCQEGHIHKMFESTWYVCKXXX", uniprot18("ARNDCQEGHILKMFPSTWYVUOBZX"));
    }

    @Test
    public void testUniprot18NotInTranslationTable() {
        assertEquals("ARNDCQEGHIHKMFESTWYVCKXXX", uniprot18("ARNDCQEGHILKMFPSTWYVUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testUniprot18Dvl1() {
        assertEquals("MAETKIIYHMDEEETEYHVKHEVAEERVTHADFKNVHSNREVHAYKFFFKSMDQDFGVVK", uniprot18(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testHsdm17NullProtein() {
        hsdm17(null);
    }

    @Test
    public void testHsdm17EmptyProtein() {
        assertEquals("", hsdm17(""));
    }

    @Test
    public void testHsdm17() {
        assertEquals("ARNDCQKGHLLKMFPSTWYLCKXXX", hsdm17("ARNDCQEGHILKMFPSTWYVUOBZX"));
    }

    @Test
    public void testHsdm17NotInTranslationTable() {
        assertEquals("ARNDCQKGHLLKMFPSTWYLCKXXX", hsdm17("ARNDCQEGHILKMFPSTWYVUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testHsdm17Dvl1() {
        assertEquals("MAKTKLLYHMDKKKTPYLLKLPLAPKRLTLADFKNLLSNRPLHAYKFFFKSMDQDFGLLK", hsdm17(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testMmseqs12NullProtein() {
        mmseqs12(null);
    }

    @Test
    public void testMmseqs12EmptyProtein() {
        assertEquals("", mmseqs12(""));
    }

    @Test
    public void testMmseqs12() {
        assertEquals("AAALLIIKKEENNFFCGHPWCKXXX", mmseqs12("ASTLMIVKREQNDFYCGHPWUOBZX"));
    }

    @Test
    public void testMmseqs12NotInTranslationTable() {
        assertEquals("AAALLIIKKEENNFFCGHPWCKXXX", mmseqs12("ASTLMIVKREQNDFYCGHPWUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testMmseqs12Dvl1() {
        assertEquals("LAEAKIIFHLNEEEAPFLIKLPIAPEKIALANFKNILANKPIHAFKFFFKALNENFGIIK", mmseqs12(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testWass14NullProtein() {
        wass14(null);
    }

    @Test
    public void testWass14EmptyProtein() {
        assertEquals("", wass14(""));
    }

    @Test
    public void testWass14() {
        assertEquals("WWDDPCAAKTRRGLYSSFNNCKXXX", wass14("WMDIPCAVKTREGLYSHFNQUOBZX"));
    }

    @Test
    public void testWass14NotInTranslationTable() {
        assertEquals("WWDDPCAAKTRRGLYSSFNNCKXXX", wass14("WMDIPCAVKTREGLYSHFNQUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testWass14Dvl1() {
        assertEquals("WARTKDDYSWDRRRTPYLAKLPAAPRRATLADFKNALSNRPASAYKFFFKSWDNDFGAAK", wass14(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testSdm12NullProtein() {
        sdm12(null);
    }

    @Test
    public void testSdm12EmptyProtein() {
        assertEquals("", sdm12(""));
    }

    @Test
    public void testSdm12() {
        assertEquals("ADKKKNTTTYYLLLLCWHGPCKXXX", sdm12("ADKERNTSQYFLIVMCWHGPUOBZX"));
    }

    @Test
    public void testSdm12NotInTranslationTable() {
        assertEquals("ADKKKNTTTYYLLLLCWHGPCKXXX", sdm12("ADKERNTSQYFLIVMCWHGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testSdm12Dvl1() {
        assertEquals("LAKTKLLYHLDKKKTPYLLKLPLAPKKLTLADYKNLLTNKPLHAYKYYYKTLDTDYGLLK", sdm12(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testGbmr7NullProtein() {
        gbmr7(null);
    }

    @Test
    public void testGbmr7EmptyProtein() {
        assertEquals("", gbmr7(""));
    }

    @Test
    public void testGbmr7() {
        assertEquals("DDAAAAAAAAAAAACCTSGPCAXXX", gbmr7("DNAEFIKLMQRVWYCHTSGPUOBZX"));
    }

    @Test
    public void testGbmr7NotInTranslationTable() {
        assertEquals("DDAAAAAAAAAAAACCTSGPCAXXX", gbmr7("DNAEFIKLMQRVWYCHTSGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testGbmr7Dvl1() {
        assertEquals("AAATAAAACADAAATPAAAAAPAAPAAATAADAADAASDAPACAAAAAAASADADAGAAA", gbmr7(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testWwmj5NullProtein() {
        wwmj5(null);
    }

    @Test
    public void testWwmj5EmptyProtein() {
        assertEquals("", wwmj5(""));
    }

    @Test
    public void testWwmj5() {
        assertEquals("CCCCCCCCAAAGGDDSSSSSCSXXX", wwmj5("CMFILVWYATHGPDESNQRKUOBZX"));
    }

    @Test
    public void testWwmj5NotInTranslationTable() {
        assertEquals("CCCCCCCCAAAGGDDSSSSSCSXXX", wwmj5("CMFILVWYATHGPDESNQRKUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testWwmj5Dvl1() {
        assertEquals("CADASCCCACDDDDAGCCCSCGCAGDSCACADCSSCCSSSGCAACSCCCSSCDSDCGCCS", wwmj5(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testGbmr4NullProtein() {
        gbmr4(null);
    }

    @Test
    public void testGbmr4EmptyProtein() {
        assertEquals("", gbmr4(""));
    }

    @Test
    public void testGbmr4() {
        assertEquals("AAAAAAAAAYYYYYYYYYGPYAXXX", gbmr4("ADKERNTSQYFLIVMCWHGPUOBZX"));
    }

    @Test
    public void testGbmr4NotInTranslationTable() {
        assertEquals("AAAAAAAAAYYYYYYYYYGPYAXXX", gbmr4("ADKERNTSQYFLIVMCWHGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testGbmr4Dvl1() {
        assertEquals("YAAAAYYYYYAAAAAPYYYAYPYAPAAYAYAAYAAYYAAAPYYAYAYYYAAYAAAYGYYA", gbmr4(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testHp2NullProtein() {
        hp2(null);
    }

    @Test
    public void testHp2EmptyProtein() {
        assertEquals("", hp2(""));
    }

    @Test
    public void testHp2() {
        assertEquals("ANNNNNNNNAAAAAANANAANNXXX", hp2("ADKERNTSQYFLIVMCWHGPUOBZX"));
    }

    @Test
    public void testHp2NotInTranslationTable() {
        assertEquals("ANNNNNNNNAAAAAANANAANNXXX", hp2("ADKERNTSQYFLIVMCWHGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testHp2Dvl1() {
        assertEquals("AANNNAAANANNNNNAAAANAAAAANNANAANANNAANNNAANAANAAANNANNNAAAAN", hp2(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testDayhoff6NullProtein() {
        dayhoff6(null);
    }

    @Test
    public void testDayhoff6EmptyProtein() {
        assertEquals("", dayhoff6(""));
    }

    @Test
    public void testDayhoff6() {
        assertEquals("ADHDHDAADFFIIIICFHAACHXXX", dayhoff6("ADKERNTSQYFLIVMCWHGPUOBZX"));
    }

    @Test
    public void testDayhoff6NotInTranslationTable() {
        assertEquals("ADHDHDAADFFIIIICFHAACHXXX", dayhoff6("ADKERNTSQYFLIVMCWHGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testDayhoff6Dvl1() {
        assertEquals("IADAHIIFHIDDDDAAFIIHIAIAADHIAIADFHDIIADHAIHAFHFFFHAIDDDFAIIH", dayhoff6(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testHpNullProtein() {
        hp(null);
    }

    @Test
    public void testHpEmptyProtein() {
        assertEquals("", hp(""));
    }

    @Test
    public void testHp() {
        assertEquals("hpppppppphhhhhhphphhppxxx", hp("ADKERNTSQYFLIVMCWHGPUOBZX"));
    }

    @Test
    public void testHpNotInTranslationTable() {
        assertEquals("hpppppppphhhhhhphphhppxxx", hp("ADKERNTSQYFLIVMCWHGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testHpDvl1() {
        assertEquals("hhppphhhphppppphhhhphhhhhpphphhphpphhppphhphhphhhpphppphhhhp", hp(DVL1_HUMAN));
    }

    @Test(expected=NullPointerException.class)
    public void testDayhoffNullProtein() {
        dayhoff(null);
    }

    @Test
    public void testDayhoffEmptyProtein() {
        assertEquals("", dayhoff(""));
    }

    @Test
    public void testDayhoff() {
        assertEquals("bcdcdcbbcffeeeeafdbbadxxx", dayhoff("ADKERNTSQYFLIVMCWHGPUOBZX"));
    }

    @Test
    public void testDayhoffNotInTranslationTable() {
        assertEquals("bcdcdcbbcffeeeeafdbbadxxx", dayhoff("ADKERNTSQYFLIVMCWHGPUOBZX1234567890abcdefghij-._"));
    }

    @Test
    public void testDayhoffDvl1() {
        assertEquals("ebcbdeefdeccccbbfeedebebbcdebebcfdceebcdbedbfdfffdbecccfbeed", dayhoff(DVL1_HUMAN));
    }
}
