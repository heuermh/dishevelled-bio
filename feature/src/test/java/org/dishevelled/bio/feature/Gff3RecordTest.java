/*

    dsh-bio-feature  Sequence features.
    Copyright (c) 2013-2018 held jointly by the individual authors.

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
package org.dishevelled.bio.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.dishevelled.bio.feature.Gff3Record.valueOf;

import com.google.common.collect.Range;

import org.junit.Test;

/**
 * Unit test for Gff3Record.
 *
 * @author  Michael Heuer
 */
public final class Gff3RecordTest {

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooShort() {
        valueOf("1\tEnsembl\tgene");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfStartNumberFormatException() {
        valueOf("1\tEnsembl\tgene\tnot a number\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfEndNumberFormatException() {
        valueOf("1\tEnsembl\tgene\t1335276\tnot a number\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding");
    }

    @Test
    public void testEquals() {
        Gff3Record record1 = valueOf("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding");
        Gff3Record record2 = valueOf("1\tEnsembl\tgene\t1331314\t1335306\t.\t+\t.\tID=ENSG00000169962;Name=ENSG00000169962;biotype=protein_coding");
        assertTrue(record1.equals(record1));
        assertTrue(record2.equals(record2));
        assertFalse(record1.equals(record2));
        assertFalse(record2.equals(record1));
        assertFalse(record1.equals(new Object()));
    }

    @Test
    public void testValueOfGFF3() {
        Gff3Record record = valueOf("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding");
        assertEquals("1", record.getSeqid());
        assertEquals(1335275L, record.getStart());
        assertEquals(1349350L, record.getEnd());
        assertEquals(Range.closedOpen(1335275L, 1349350L), record.toRange());
        assertEquals("1\tEnsembl\tgene\t1335276\t1349350\t.\t-\t.\tID=ENSG00000107404;Name=ENSG00000107404;biotype=protein_coding", record.toString());
    }
}
