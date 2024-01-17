/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.sam;

import static org.dishevelled.bio.alignment.sam.SamReader.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

/**
 * Unit test for SamHeader.
 *
 * @author  Michael Heuer
 */
public final class SamHeaderTest {
    private static final String SAM = "NA12878-platinum-chr20.1-60250.sam";

    @Test
    public void testBuilder() {
        SamHeader.Builder builder = SamHeader.builder();
        assertNotNull(builder);

        SamHeader header = builder.build();
        assertNotNull(header);
        assertFalse(header.getHeaderLineOpt().isPresent());
        assertTrue(header.getSequenceHeaderLines().isEmpty());
        assertTrue(header.getReadGroupHeaderLines().isEmpty());
        assertTrue(header.getProgramHeaderLines().isEmpty());
        assertTrue(header.getCommentHeaderLines().isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderCopyCtrNullHeader() throws Exception {
        SamHeader.builder(null);
    }

    @Test
    public void testBuilderCopyCtr() throws Exception {
        SamHeader expectedHeader = header(createInputStream(SAM));
        SamHeader.Builder builder = SamHeader.builder(expectedHeader);
        SamHeader observedHeader = builder.build();

        assertTrue(observedHeader.getHeaderLineOpt().isPresent());
        assertEquals(expectedHeader.getSequenceHeaderLines().size(), observedHeader.getSequenceHeaderLines().size());
        assertEquals(expectedHeader.getReadGroupHeaderLines().size(), observedHeader.getReadGroupHeaderLines().size());
        assertEquals(expectedHeader.getProgramHeaderLines().size(), observedHeader.getProgramHeaderLines().size());
        assertEquals(expectedHeader.getCommentHeaderLines().size(), observedHeader.getCommentHeaderLines().size());        
    }

    private static InputStream createInputStream(final String name) {
        return SamHeaderTest.class.getResourceAsStream(name);
    }
}
