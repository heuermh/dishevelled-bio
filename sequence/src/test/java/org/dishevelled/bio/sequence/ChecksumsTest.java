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

import static org.dishevelled.bio.sequence.Checksums.crc64;
import static org.dishevelled.bio.sequence.Checksums.improvedCrc64;
import static org.dishevelled.bio.sequence.Checksums.sha256;
import static org.dishevelled.bio.sequence.Checksums.sha512t24u;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Checksums.
 *
 * @author  Michael Heuer
 */
public final class ChecksumsTest {
    // O14640|DVL1_HUMAN Segment polarity protein dishevelled homolog DVL-1
    static final String DVL1_HUMAN_AA = "MAETKIIYHMDEEETPYLVKLPVAPERVTLADFKNVLSNRPVHAYKFFFKSMDQDFGVVKEEIFDDNAKLPCFNGRVVSWLVLAEGAHSDAGSQGTDSHTDLPPPLERTGGIGDSRPPSFHPNVASSRDGMDNETGTESMVSHRRERARRRNREEAARTNGHPRGDRRRDVGLPPDSASTALSSELESSSFVDSDEDGSTSRLSSSTEQSTSSRLIRKHKRRRRKQRLRQADRASSFSSITDSTMSLNIVTVTLNMERHHFLGISIVGQSNDRGDGGIYIGSIMKGGAVAADGRIEPGDMLLQVNDVNFENMSNDDAVRVLREIVSQTGPISLTVAKCWDPTPRSYFTVPRADPVRPIDPAAWLSHTAALTGALPRYGTSPCSSAVTRTSSSSLTSSVPGAPQLEEAPLTVKSDMSAVVRVMQLPDSGLEIRDRMWLKITIANAVIGADVVDWLYTHVEGFKERREARKYASSLLKHGFLRHTVNKITFSEQCYYVFGDLCSNLATLNLNSGSSGTSDQDTLAPLPHPAAPWPLGQGYPYQYPGPPPCFPPAYQDPGFSYGSGSTGSQQSEGSKSSGSTRSSRRAPGREKERRAAGAGGSGSESDHTAPSGVGSSWRERPAGQLSRGSSPRSQASATAPGLPPPHPTTKAYTVVGGPPGGPPVRELAAVPPELTGSRQSFQKAMGNPCEFFVDIM";

    // Q9WVB9|DVL1_RAT Segment polarity protein dishevelled homolog DVL-1
    static final String DVL1_RAT_AA = "MAETKIIYHMDEEETPYLVKLPVAPERVTLADFKNVLSNRPVHAYKFFFKSMDQDFGVVKEEIFDDNAKLPCFNGRVVSWLVLAEGAHSDAGSQGTDSHTDLPPPLERTGGIGDSRPPSFHPNVASSRDGMDNETGTESMVSHRRERARRRNRDEAARTNGHPRGDRRRELGLPPDSASTVLSSELESSSFIDSDEEDNTSRLSSSTEQSTSSRLIRKHKCRRRKQRLRQTDRASSFSSITDSTMSLNIITVTLNMERHHFLGISIVGQSNDRGDGGIYIGSIMKGGAVAADGRIEPGDMLLQVNDVNFENMSNDDAVRVLREIVSQTGPISLTVAKCWDPTPRSYFTIPRADPVRPIDPAAWLSHTAALTGALPRYGTSPCSSAITRTSSSSLTSSVPGAPQLEEAPLTVKSDMSAIVRVMQLPDSGLEIRDRMWLKITIANAVIGADVVDWLYTHVEGFKERREARKYASSMLKHGFLRHTVNKITFSEQCYYVFGDLCSNLASLNLNSGSSGASDQDTLAPLPHPSVPWPLGQGYPYQYPGPPPCFPPAYQDPGFSYGSGSAGSQQSEGSKSSGSTRSSHRTPGREERRATGAGGSGSESDHTVPSGSGSTGWWERPVSQLSRGSSPRSQASAVAPGLPPLHPLTKAYAVVGGPPGGPPVRELAAVPPELTGSRQSFQKAMGNPCEFFVDIM";

    // P51141|DVL1_MOUSE Segment polarity protein dishevelled homolog DVL-1
    static final String DVL1_MOUSE_AA = "MAETKIIYHMDEEETPYLVKLPVAPERVTLADFKNVLSNRPVHAYKFFFKSMDQDFGVVKEEIFDDNAKLPCFNGRVVSWLVLAEGAHSDAGSQGTDSHTDLPPPLERTGGIGDSRPPSFHPNVASSRDGMDNETGTESMVSHRRERARRRNRDEAARTNGHPRGDRRRDLGLPPDSASTVLSSELESSSFIDSDEEDNTSRLSSSTEQSTSSRLVRKHKCRRRKQRLRQTDRASSFSSITDSTMSLNIITVTLNMERHHFLGISIVGQSNDRGDGGIYIGSIMKGGAVAADGRIEPGDMLLQVNDVNFENMSNDDAVRVLREIVSQTGPISLTVAKCWDPTPRSYFTIPRADPVRPIDPAAWLSHTAALTGALPRYGTSPCSSAITRTSSSSLTSSVPGAPQLEEAPLTVKSDMSAIVRVMQLPDSGLEIRDRMWLKITIANAVIGADVVDWLYTHVEGFKERREARKYASSMLKHGFLRHTVNKITFSEQCYYVFGDLCSNLASLNLNSGSSGASDQDTLAPLPHPSVPWPLGQGYPYQYPGPPPCFPPAYQDPGFSCGSGSAGSQQSEGSKSSGSTRSSHRTPGREERRATGAGGSGSESDHTVPSGSGSTGWWERPVSQLSRGSSPRSQASAVAPGLPPLHPLTKAYAVVGGPPGGPPVRELAAVPPELTGSRQSFQKAMGNPCEFFVDIM";

    @Test(expected=NullPointerException.class)
    public void testCrc64NullSequence() {
        crc64(null);
    }

    @Test
    public void testCrc64EmptySequence() {
        crc64("");
    }

    @Test
    public void testCrc64DnaSequence() {
        assertEquals("54A6662539F3F595", crc64("ATCGATCGTAGCTAGCTGATCGATGCTAGCTGA"));
    }

    @Test
    public void testCrc64ProteinSequence() {
        assertEquals("BEF57BAAD18E104E", crc64("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSG"));
    }

    @Test
    public void testCrc64ProteinSequenceUniprot() {
        // example records from Uniprot
        assertEquals("B009BDBCC57BD562", crc64(DVL1_HUMAN_AA));
        assertEquals("EEC4AA99A117D22A", crc64(DVL1_RAT_AA));
        assertEquals("FA21ACAC48FF71E0", crc64(DVL1_MOUSE_AA));
    }

    @Test
    public void testCrc64ProteinSequenceCollision() {
        // examples from Jones crc64.c
        assertEquals("76CB2729A6B0FBBB", crc64("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSGAEDALLRHGVDTNDIDVAWVPGAFEIPFAAKKMAETKKYDAIITLGTVIRGATTSYDYVCNEAAKGIAQAANTTGVPVIFGIVTTENIEQAIERAGTKAGNKGVDCAVSAIEMANLNRSFE"));
        assertEquals("76CB2729A6B0FBBB", crc64("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSGAEDALLRHGVDTNDIDVAWVPGAFEIPFAAKKMAETKKYDAIITLGDVIRGATTHYDYVCNEAAKGIAQAANTTGVPVIFGIVTTENIEQAIERAGTKAGNKGVDCAVSAIEMANLNRSFE"));
    }

    @Test(expected=NullPointerException.class)
    public void testImprovedCrc64NullSequence() {
        improvedCrc64(null);
    }

    @Test
    public void testImprovedCrc64EmptySequence() {
        improvedCrc64("");
    }

    @Test
    public void testImprovedCrc64DnaSequence() {
        assertEquals("FC2F6E8925FC95D5", improvedCrc64("ATCGATCGTAGCTAGCTGATCGATGCTAGCTGA"));
    }

    @Test
    public void testImprovedCrc64ProteinSequence() {
        assertEquals("846D466ED40035C2", improvedCrc64("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSG"));
    }

    @Test
    public void testImprovedCrc64ProteinSequenceUniprot() {
        // example records from Uniprot
        assertEquals("7DC3BEAA4B358AB7", improvedCrc64(DVL1_HUMAN_AA));
        assertEquals("9E8909466C3E77C2", improvedCrc64(DVL1_RAT_AA));
        assertEquals("9C1E0C7379E61717", improvedCrc64(DVL1_MOUSE_AA));
    }

    @Test
    public void testImprovedCrc64ProteinSequenceCollisionAvoided() {
        // examples from Jones crc64.c
        assertEquals("C874767DA8254746", improvedCrc64("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSGAEDALLRHGVDTNDIDVAWVPGAFEIPFAAKKMAETKKYDAIITLGTVIRGATTSYDYVCNEAAKGIAQAANTTGVPVIFGIVTTENIEQAIERAGTKAGNKGVDCAVSAIEMANLNRSFE"));
        assertEquals("2DF1AA17420FCA3F", improvedCrc64("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSGAEDALLRHGVDTNDIDVAWVPGAFEIPFAAKKMAETKKYDAIITLGDVIRGATTHYDYVCNEAAKGIAQAANTTGVPVIFGIVTTENIEQAIERAGTKAGNKGVDCAVSAIEMANLNRSFE"));
    }

    @Test(expected=NullPointerException.class)
    public void testSha256NullSequence() {
        sha256(null);
    }

    @Test
    public void testSha256EmptySequence() {
        // import hashlib
        // hashlib.sha256(sequence.encode("utf-8")).hexdigest()
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256(""));
    }

    @Test
    public void testSha256DnaSequence() {
        assertEquals("eeee4546f261946fb2addd6e0d44064fee766a87ab4b751f3ccaf0f69901fcb7", sha256("ATCGATCGTAGCTAGCTGATCGATGCTAGCTGA"));
    }

    @Test
    public void testSha256ProteinSequence() {
        assertEquals("8b3688d7ba48556743a37beba01125e08c5c7a51d6106557e032d75b5b542f40", sha256("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSG"));
    }

    @Test
    public void testSha256ProteinSequenceUniprot() {
        assertEquals("875f5b0a497b8b7dc79b102cd2acc0ae643c880466ae452abb152c6ae37c33d9", sha256(DVL1_HUMAN_AA));
        assertEquals("b20f76ec30e1d2a56b244a3d0424f39e9640a0aaca6c6908819b4f36f7d1b903", sha256(DVL1_RAT_AA));
        assertEquals("7b74704d9116b1c757f60ccff1f8e966ceb722b67eccfd01a31f730924a412ef", sha256(DVL1_MOUSE_AA));
    }

    @Test
    public void testSha256ProteinSequenceImg() {
        assertEquals("1bd40d2f2dd8eecc1ed9f2944ed4fcb267cce4fa93aed10fb8caf62f56ef2fd3", sha256("MSIDARYAVIIPISLLGAVLPRISYPPIAFLPHYRK"));
        assertEquals("f7c06cf5742eb784bf6aa3093e451a37f4933c6964d71dc076a9088b61f76c5d", sha256("MPNRTTPGVDSFSRLIERWHLEQRTISKFMEVGPEPVIEGEVTGSVPLEG"));
        assertEquals("02a2b22dd8656ff69a87554a8e1e0445b0d7b47cef749b065f09c1307a75775d", sha256("VVYERLTTSFGQLASVKDYRTLSADETLELEISIDCGLGYNFFLEKIQTLISSTTFHG"));
        assertEquals("0aefb632fd33e15a6ac04e440782891c216d0f508b5ac6554ffe883fcca6bf22", sha256("VEASEMPRQSSSSSCVLFLLRHVHTAEDWAIATENRHGHGDQEEDVEEIHFSRHVDGEEKETATSSVYHAPLHPQLGVVG"));
    }

    @Test(expected=NullPointerException.class)
    public void testSha512t24uNullSequence() {
        sha512t24u(null);
    }

    @Test
    public void testSha512t24uEmptySequence() {
        // from https://bioutils.readthedocs.io/en/latest/reference/bioutils.digests.html
        assertEquals("z4PhNX7vuL3xVChQ1m2AB9Yg5AULVxXc", sha512t24u(""));
    }

    @Test
    public void testSha512t24uDnaSequence() {
        // from bioutils.digests.seq_seqhash method
        assertEquals("MrnwsorK9E_prQy3Gf5vYDqvGG6DFs2N", sha512t24u("ATCGATCGTAGCTAGCTGATCGATGCTAGCTGA"));
    }

    @Test
    public void testSha512t24uProteinSequence() {
        // from bioutils.digests.seq_seqhash method
        assertEquals("Rk8gsTujFJOwI-lJFuVrc35RhrKB7E7Q", sha512t24u("MNIIQGNLVGTGLKIGIVVGRFNDFITSKLLSG"));
    }

    @Test
    public void testSha512t24uMixedCase() {
        // from https://bioutils.readthedocs.io/en/latest/reference/bioutils.digests.html
        assertEquals("aKF498dAxcJAqme6QYQ7EZ07-fiw8Kw2", sha512t24u("ACGT"));
        assertEquals("eFwawHHdibaZBDcs9kW3gm31h1NNJcQe", sha512t24u("acgt"));
    }

    @Test
    public void testSha512t24uProteinSequenceUniprot() {
        // example records from Uniprot
        // from bioutils.digests.seq_seqhash method
        assertEquals("p_hwLAPAAaDPk7TM_K4MxKYBWZS7-rqg", sha512t24u(DVL1_HUMAN_AA));
        assertEquals("dxr_CSxf3dLFzVFR05wlOAR8cFtEh3_M", sha512t24u(DVL1_RAT_AA));
        assertEquals("aNhhcxP-XlUbVPOzZtEnVJZbnX3xwMUl", sha512t24u(DVL1_MOUSE_AA));
    }
}
