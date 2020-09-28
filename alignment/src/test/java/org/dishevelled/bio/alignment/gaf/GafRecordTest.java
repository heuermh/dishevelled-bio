/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.gaf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import org.junit.Test;

/**
 * Unit test for GafRecord.
 *
 * @author  Michael Heuer
 */
public final class GafRecordTest {

    @Test(expected=NullPointerException.class)
    public void testBuilderNullRecord() {
        GafRecord.builder(null);
    }

    @Test
    public void testBuilder() {
        GafRecord.Builder builder = GafRecord.builder();
        assertNotNull(builder);

        GafRecord record = builder
            .withLineNumber(1)
            .withQueryName("query")
            .withQueryLength(100L)
            .withQueryStart(10L)
            .withQueryEnd(20L)
            .withStrand('-')
            .withPathName("path")
            .withPathLength(200L)
            .withPathStart(20L)
            .withPathEnd(30L)
            .withMatches(42L)
            .withAlignmentBlockLength(10L)
            .withMappingQuality(32)
            .build();

        assertEquals(1, record.getLineNumber());
        assertEquals("query", record.getQueryName());
        assertEquals(100, record.getQueryLength());
        assertEquals(10, record.getQueryStart());
        assertEquals(20, record.getQueryEnd());
        assertEquals('-', record.getStrand());
        assertEquals("path", record.getPathName());
        assertEquals(200, record.getPathLength());
        assertEquals(20, record.getPathStart());
        assertEquals(30, record.getPathEnd());        
        assertEquals(42, record.getMatches());
        assertEquals(10, record.getAlignmentBlockLength());
        assertEquals(32, record.getMappingQuality());
    }

    @Test
    public void testBuilderReset() {
        GafRecord record = GafRecord.builder()
            .withLineNumber(1)
            .withQueryName("query")
            .withQueryLength(100L)
            .withQueryStart(10L)
            .withQueryEnd(20L)
            .withStrand('-')
            .withPathName("path")
            .withPathLength(200L)
            .withPathStart(20L)
            .withPathEnd(30L)
            .withMatches(42L)
            .withAlignmentBlockLength(10L)
            .withMappingQuality(32)
            .reset()
            .build();

        assertEquals(0, record.getLineNumber());
        assertNull(record.getQueryName());
        assertEquals(0, record.getQueryLength());
        assertEquals(0, record.getQueryStart());
        assertEquals(0, record.getQueryEnd());
        assertEquals('+', record.getStrand());
        assertNull(record.getPathName());
        assertEquals(0, record.getPathLength());
        assertEquals(0, record.getPathStart());
        assertEquals(0, record.getPathEnd());        
        assertEquals(0, record.getMatches());
        assertEquals(0, record.getAlignmentBlockLength());
        assertEquals(255, record.getMappingQuality());
    }

    @Test
    public void testBuilderCopy() {
        GafRecord record = GafRecord.builder()
            .withLineNumber(1)
            .withQueryName("query")
            .withQueryLength(100L)
            .withQueryStart(10L)
            .withQueryEnd(20L)
            .withStrand('-')
            .withPathName("path")
            .withPathLength(200L)
            .withPathStart(20L)
            .withPathEnd(30L)
            .withMatches(42L)
            .withAlignmentBlockLength(10L)
            .withMappingQuality(32)
            .build();

        GafRecord copy = GafRecord.builder(record)
            .build();

        assertEquals(record.getLineNumber(), copy.getLineNumber());
        assertEquals(record.getQueryName(), copy.getQueryName());
        assertEquals(record.getQueryLength(), copy.getQueryLength());
        assertEquals(record.getQueryStart(), copy.getQueryStart());
        assertEquals(record.getQueryEnd(), copy.getQueryEnd());
        assertEquals(record.getStrand(), copy.getStrand());
        assertEquals(record.getPathName(), copy.getPathName());
        assertEquals(record.getPathLength(), copy.getPathLength());
        assertEquals(record.getPathStart(), copy.getPathStart());
        assertEquals(record.getPathEnd(), copy.getPathEnd());
        assertEquals(record.getMatches(), copy.getMatches());
        assertEquals(record.getAlignmentBlockLength(), copy.getAlignmentBlockLength());
        assertEquals(record.getMappingQuality(), copy.getMappingQuality());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderIllegalStrand() {
        GafRecord.builder()
            .withStrand('0')
            .build();
    }
}
