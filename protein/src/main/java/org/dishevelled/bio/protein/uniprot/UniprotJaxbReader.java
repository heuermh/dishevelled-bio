/*

    dsh-bio-protein  Protein sequences and metadata.
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
package org.dishevelled.bio.protein.uniprot;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Reader;

import javax.annotation.concurrent.Immutable;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uniprot.uniprot.Uniprot;

/**
 * UniProt XML JAXB reader.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
@Immutable
public final class UniprotJaxbReader {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UniprotJaxbReader.class);


    /**
     * Private no-arg constructor.
     */
    private UniprotJaxbReader() {
        // empty
    }


    /**
     * Read UniProt XML from the specified reader via JAXB.
     *
     * @param reader reader to read UniProt XML from, must not be null
     * @return UniProt XML read from the specified reader via JAXB
     * @throws IOException if an I/O error occurs
     */
    public static Uniprot read(final Reader reader) throws IOException {
        checkNotNull(reader);

        LOGGER.trace("Reading UniProt XML and unmarshalling via JAXB...");
        try {
            JAXBContext context = JAXBContext.newInstance(Uniprot.class);
            Uniprot uniprot = (Uniprot) context.createUnmarshaller().unmarshal(reader);
            LOGGER.trace("Done");

            LOGGER.info("Read "  + uniprot.getEntry().size() + " UniProt entries");
            LOGGER.info(uniprot.getCopyright().trim());

            return uniprot;
        }
        catch (JAXBException e) {
            throw new IOException("could not read UniProt XML and unmarshal via JAXB", e);
        }
    }
}
