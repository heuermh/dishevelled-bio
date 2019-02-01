/*

    dsh-bio-alignment  Aligments.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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

import java.io.IOException;

/**
 * Abstract implementation of SamParseListener.
 *
 * @author  Michael Heuer
 */
public class SamParseAdapter implements SamParseListener {

    @Override
    public void lineNumber(final long lineNumber) throws IOException {
        // empty
    }

    @Override
    public void headerLine(final String headerLine) throws IOException {
        // empty
    }

    @Override
    public void qname(final String qname) throws IOException {
        // empty
    }

    @Override
    public void flag(final int flag) throws IOException {
        // empty
    }

    @Override
    public void rname(final String rname) throws IOException {
        // empty
    }

    @Override
    public void pos(final int pos) throws IOException {
        // empty
    }

    @Override
    public void mapq(final int mapq) throws IOException {
        // empty
    }

    @Override
    public void cigar(final String cigar) throws IOException {
        // empty
    }

    @Override
    public void rnext(final String rnext) throws IOException {
        // empty
    }

    @Override
    public void pnext(final int pnext) throws IOException {
        // empty
    }

    @Override
    public void tlen(final int tlen) throws IOException {
        // empty
    }

    @Override
    public void seq(final String seq) throws IOException {
        // empty
    }

    @Override
    public void qual(final String qual) throws IOException {
        // empty
    }

    @Override
    public void field(final String tag, final String type, final String value) throws IOException {
        // empty
    }

    @Override
    public void arrayField(final String tag, final String type, final String arrayType, final String... values) throws IOException {
        // empty
    }

    @Override
    public boolean complete() throws IOException {
        return true;
    }
}
