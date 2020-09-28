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

import java.io.IOException;

/**
 * GAF (graph alignment format) parse listener.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
public interface GafParseListener {

    /**
     * Notify this parse listener of the line number.
     *
     * @param lineNumber line number
     * @throws IOException if an I/O error occurs
     */
    void lineNumber(long lineNumber) throws IOException;

    /**
     * Notify this parse listener of a query name.
     *
     * @param queryName query name
     * @throws IOException if an I/O error occurs
     */
    void queryName(String queryName) throws IOException;

    /**
     * Notify this parse listener of a query length.
     *
     * @param queryLength query length
     * @throws IOException if an I/O error occurs
     */
    void queryLength(long queryLength) throws IOException;

    /**
     * Notify this parse listener of a query start
     *
     * @param queryStart query start
     * @throws IOException if an I/O error occurs
     */
    void queryStart(long queryStart) throws IOException;

    /**
     * Notify this parse listener of a query end.
     *
     * @param queryEnd query end
     * @throws IOException if an I/O error occurs
     */
    void queryEnd(long queryEnd) throws IOException;

    /**
     * Notify this parse listener of a strand.
     *
     * @param strand strand
     * @throws IOException if an I/O error occurs
     */
    void strand(char strand) throws IOException;

    /**
     * Notify this parse listener of a path name.
     *
     * @param pathName path name
     * @throws IOException if an I/O error occurs
     */
    void pathName(String pathName) throws IOException;

    /**
     * Notify this parse listener of a path length.
     *
     * @param pathLength path length
     * @throws IOException if an I/O error occurs
     */
    void pathLength(long pathLength) throws IOException;

    /**
     * Notify this parse listener of a path start
     *
     * @param pathStart path start
     * @throws IOException if an I/O error occurs
     */
    void pathStart(long pathStart) throws IOException;

    /**
     * Notify this parse listener of a path end.
     *
     * @param pathEnd path end
     * @throws IOException if an I/O error occurs
     */
    void pathEnd(long pathEnd) throws IOException;

    /**
     * Notify this parse listener of a matches.
     *
     * @param matches matches
     * @throws IOException if an I/O error occurs
     */
    void matches(long matches) throws IOException;

    /**
     * Notify this parse listener of an alignment block length.
     *
     * @param alignmentBlockLength alignment block length
     * @throws IOException if an I/O error occurs
     */
    void alignmentBlockLength(long alignmentBlockLength) throws IOException;
    
    /**
     * Notify this parse listener of a mapping quality.
     *
     * @param mappingQuality mapping quality
     * @throws IOException if an I/O error occurs
     */
    void mappingQuality(int mappingQuality) throws IOException;

    /**
     * Notify this parse listener of an optional field.
     *
     * @param tag optional field tag
     * @param type optional field type
     * @param value optional field value
     * @throws IOException if an I/O error occurs
     */
    void field(String tag, String type, String value) throws IOException;

    /**
     * Notify this parse listener of an optional array field.
     *
     * @param tag optional array field tag
     * @param type optional array field type
     * @param arrayType optional array type
     * @param values one or more optional array field values
     * @throws IOException if an I/O error occurs
     */
    void arrayField(String tag, String type, String arrayType, String... values) throws IOException;

    /**
     * Notify this parse listener a record is complete.
     *
     * @return true to continue parsing
     * @throws IOException if an I/O error occurs
     */
    boolean complete() throws IOException;
}
