/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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

import static org.dishevelled.bio.tools.About.about;

import java.io.File;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Unit test for About.
 *
 * @author  Michael Heuer
 */
public final class AboutTest {

    @Test
    public void testToString() {
        assertNotNull(new About().toString());
    }

    @Test(expected=NullPointerException.class)
    public void testAboutNullPrintStream() {
        about(null);
    }

    @Test
    public void testAbout() throws Exception {
        File tmp = File.createTempFile("aboutTest", ".out");
        try (PrintStream out = new PrintStream(tmp)) {
            about(out);
        }
        tmp.delete();
    }
}
