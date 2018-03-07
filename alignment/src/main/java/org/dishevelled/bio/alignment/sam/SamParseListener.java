/*

    dsh-bio-alignment  Aligments.
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
package org.dishevelled.bio.alignment.sam;

import java.io.IOException;

/**
 * SAM parse listener.
 *
 * @author  Michael Heuer
 */
public interface SamParseListener {

    /**
     * Notify this parse listener of the line number.
     *
     * @param lineNumber line number
     * @throws IOException if an I/O error occurs
     */
    void lineNumber(long lineNumber) throws IOException;

    /**
     * Notify this parse listener of a QNAME mandatory field.
     *
     * @param qname QNAME mandatory field
     * @throws IOException if an I/O error occurs
     */
    void qname(String qname) throws IOException;

    /**
     * Notify this parse listener of a FLAG mandatory field.
     *
     * @param flag FLAG mandatory field
     * @throws IOException if an I/O error occurs
     */
    void flag(int flag) throws IOException;

    /**
     * Notify this parse listener of a RNAME mandatory field.
     *
     * @param rname RNAME mandatory field
     * @throws IOException if an I/O error occurs
     */
    void rname(String rname) throws IOException;

    /**
     * Notify this parse listener of a POS mandatory field.
     *
     * @param pos POS mandatory field
     * @throws IOException if an I/O error occurs
     */
    void pos(int pos) throws IOException;

    /**
     * Notify this parse listener of a MAPQ mandatory field.
     *
     * @param mapq MAPQ mandatory field
     * @throws IOException if an I/O error occurs
     */
    void mapq(int mapq) throws IOException;

    /**
     * Notify this parse listener of a CIGAR mandatory field.
     *
     * @param cigar CIGAR mandatory field
     * @throws IOException if an I/O error occurs
     */
    void cigar(String cigar) throws IOException;

    /**
     * Notify this parse listener of a RNEXT mandatory field.
     *
     * @param rnext RNEXT mandatory field
     * @throws IOException if an I/O error occurs
     */
    void rnext(String rnext) throws IOException;

    /**
     * Notify this parse listener of a PNEXT mandatory field.
     *
     * @param pnext PNEXT mandatory field
     * @throws IOException if an I/O error occurs
     */
    void pnext(int pnext) throws IOException;

    /**
     * Notify this parse listener of a TLEN mandatory field.
     *
     * @param tlen TLEN mandatory field
     * @throws IOException if an I/O error occurs
     */
    void tlen(int tlen) throws IOException;

    /**
     * Notify this parse listener of a SEQ mandatory field.
     *
     * @param seq SEQ mandatory field
     * @throws IOException if an I/O error occurs
     */
    void seq(String seq) throws IOException;

    /**
     * Notify this parse listener of a QUAL mandatory field.
     *
     * @param qual QUAL mandatory field
     * @throws IOException if an I/O error occurs
     */
    void qual(String qual) throws IOException;

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
