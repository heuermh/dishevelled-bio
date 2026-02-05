/*

    dsh-bio-protein  Protein sequences and metadata.
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
package org.dishevelled.bio.protein.uniprot;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Reader;

import javax.annotation.concurrent.Immutable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import net.sf.stax.SAX2StAXAdaptor;
import net.sf.stax.StAXContentHandlerBase;
import net.sf.stax.StAXContext;
import net.sf.stax.StAXDelegationContext;
import net.sf.stax.StringElementHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

/**
 * Streaming UniProt XML reader that extracts entry sequences.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
@Immutable
public final class UniprotEntrySequenceReader {

    /**
     * Private no-arg constructor.
     */
    private UniprotEntrySequenceReader() {
        // empty
    }


    /**
     * Stream UniProt XML from the specified reader and extract entry sequences.
     *
     * @param reader UniProt XML reader to read from, must not be null
     * @param callback entry sequence callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Reader reader, final EntrySequenceListener callback) throws IOException  {
        checkNotNull(reader);
        checkNotNull(callback);

        try {
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            InputSource inputSource = new InputSource(reader);
            UniprotHandler uniprotHandler = new UniprotHandler(callback);
            ContentHandler contentHandler = new SAX2StAXAdaptor(uniprotHandler);
            xmlReader.setContentHandler(contentHandler);
            xmlReader.parse(inputSource);
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new IOException("could not read UniProt XML", e);
        }
    }

    /**
     * Uniprot handler.
     */
    private static final class UniprotHandler extends StAXContentHandlerBase {
        private final EntryHandler entryHandler;

        /**
         * Create a new uniprot handler with the specified callback.
         *
         * @param callback callback
         */
        private UniprotHandler(final EntrySequenceListener callback) {
            entryHandler = new EntryHandler(callback);
        }

        @Override
        public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
            if ("entry".equals(qName)) {
                dctx.delegate(entryHandler);
            }
        }

        /**
         * Entry handler.
         */
        private static final class EntryHandler extends StAXContentHandlerBase {
            private String accession;
            // todo: anything else to pull from entry?
            private EntrySequence entrySequence;

            private final EntrySequenceListener callback;
            private final StringElementHandler accessionHandler = new StringElementHandler();
            private final SequenceHandler sequenceHandler = new SequenceHandler();

            /**
             * Create a new entry handler with the specified callback.
             *
             * @param callback callback
             */
            private EntryHandler(final EntrySequenceListener callback) {
                this.callback = callback;
            }

            @Override
            public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                if ("accession".equals(qName)) {
                    dctx.delegate(accessionHandler);
                }
                else if ("sequence".equals(qName)) {
                    dctx.delegate(sequenceHandler);
                }
            }

            @Override
            public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                if ("accession".equals(qName)) {
                    accession = (String) result;
                }
                if ("sequence".equals(qName)) {
                    entrySequence = (EntrySequence) result;
                }
            }

            @Override
            public Object endTree(final StAXContext context) {
                // todo: end parsing if rv is false?
                callback.entrySequence(entrySequence.withAccession(accession));
                return null;
            }

            /**
             * Sequence handler.
             */
            private static final class SequenceHandler extends StAXContentHandlerBase {
                private int length;
                private int mass;
                private String checksum;
                private String modified;
                private int version;
                private boolean precursor;
                private String fragment;
                private final StringBuffer data = new StringBuffer();

                @Override
                public void startTree(final StAXContext context) {
                    precursor = false;
                    fragment = null;
                    data.delete(0, data.length());
                }
                
                @Override
                public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) {
                    length = Integer.parseInt(attrs.getValue("length"));
                    mass = Integer.parseInt(attrs.getValue("mass"));
                    checksum = attrs.getValue("checksum");
                    modified = attrs.getValue("modified");
                    version = Integer.parseInt(attrs.getValue("version"));
                    precursor = attrs.getValue("precursor") != null && Boolean.parseBoolean(attrs.getValue("precursor"));
                    fragment = attrs.getValue("fragment");
                }

                @Override
                public void characters(final char[] ch, final int start, final int length, final StAXContext ctx) {
                    data.append(ch, start, length);
                }

                @Override
                public Object endTree(final StAXContext context) {
                    return new EntrySequence(length, mass, checksum, modified, version, precursor, fragment, data.substring(0));
                }
            }
        }
    }
}
