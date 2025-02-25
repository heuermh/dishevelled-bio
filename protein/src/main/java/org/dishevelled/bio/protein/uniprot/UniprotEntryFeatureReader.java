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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import com.google.common.base.Joiner;

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
 * Streaming UniProt XML reader that extracts entry features.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
@Immutable
public final class UniprotEntryFeatureReader {

    /**
     * Private no-arg constructor.
     */
    private UniprotEntryFeatureReader() {
        // empty
    }


    /**
     * Stream UniProt XML from the specified reader and extract entry features.
     *
     * @param reader UniProt XML reader to read from, must not be null
     * @param callback entry feature callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Reader reader, final EntryFeatureListener callback) throws IOException  {
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
        private UniprotHandler(final EntryFeatureListener callback) {
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
            private List<EntryFeature> entryFeatures = new ArrayList<EntryFeature>();

            private final EntryFeatureListener callback;
            private final StringElementHandler accessionHandler = new StringElementHandler();
            private final FeatureHandler featureHandler = new FeatureHandler();

            /**
             * Create a new entry handler with the specified callback.
             *
             * @param callback callback
             */
            private EntryHandler(final EntryFeatureListener callback) {
                this.callback = callback;
            }

            @Override
            public void startTree(final StAXContext context) {
                entryFeatures.clear();
            }

            @Override
            public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                if ("accession".equals(qName)) {
                    dctx.delegate(accessionHandler);
                }
                else if ("feature".equals(qName)) {
                    dctx.delegate(featureHandler);
                }
            }

            @Override
            public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                if ("accession".equals(qName)) {
                    accession = (String) result;
                }
                else if ("feature".equals(qName)) {
                    entryFeatures.add((EntryFeature) result);
                }
            }

            @Override
            public Object endTree(final StAXContext context) {
                // todo: end parsing if rv is false?
                // todo: pass accession to entryFeatureHandler instead?
                //   or outer.this.accession?
                //   is element order enforced by the schema?
                for (EntryFeature entryFeature : entryFeatures) {
                    callback.entryFeature(entryFeature.withAccession(accession));
                }
                return null;
            }

            /**
             * Feature handler.
             */
            private static final class FeatureHandler extends StAXContentHandlerBase {
                private String id;
                private String description;
                private String evidence;
                private String ref;
                private String type;
                private String original;
                private List<String> variations = new ArrayList<String>();
                private Location location;
                private String ligand;
                private String ligandPart;

                private final Joiner joiner = Joiner.on("; ");
                private final StringElementHandler originalHandler = new StringElementHandler();
                private final StringElementHandler variationHandler = new StringElementHandler();
                private final LocationHandler locationHandler = new LocationHandler();
                private final LigandHandler ligandHandler = new LigandHandler();
                private final LigandPartHandler ligandPartHandler = new LigandPartHandler();

                @Override
                public void startTree(final StAXContext context) {
                    id = null;
                    description = null;
                    evidence = null;
                    ref = null;
                    original = null;
                    variations.clear();
                }

                @Override
                public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                    if ("feature".equals(qName)) {
                        id = attrs.getValue("id");
                        description = attrs.getValue("description");
                        evidence = attrs.getValue("evidence"); // .toString()?  or joiner
                        ref = attrs.getValue("ref");
                        type = attrs.getValue("type");
                    }
                    else if ("original".equals(qName)) {
                        dctx.delegate(originalHandler);
                    }
                    else if ("variation".equals(qName)) {
                        dctx.delegate(variationHandler);
                    }
                    else if ("location".equals(qName)) {
                        dctx.delegate(locationHandler);
                    }
                    else if ("ligand".equals(qName)) {
                        dctx.delegate(ligandHandler);
                    }
                    else if ("ligandPart".equals(qName)) {
                        dctx.delegate(ligandPartHandler);
                    }
                }

                @Override
                public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                    if ("original".equals(qName)) {
                        original = (String) result;
                    }
                    else if ("variation".equals(qName)) {
                        variations.add((String) result);
                    }
                    else if ("location".equals(qName)) {
                        location = (Location) result;
                    }
                    else if ("ligand".equals(qName)) {
                        ligand = (String) result;
                    }
                    else if ("ligandPart".equals(qName)) {
                        ligandPart = (String) result;
                    }
                }

                @Override
                public Object endTree(final StAXContext context) {
                    // todo: reuse one feature object?
                    return new EntryFeature(id,
                                            description,
                                            evidence,
                                            ref,
                                            type,
                                            original,
                                            variations.isEmpty() ? null : joiner.join(variations),
                                            location,
                                            ligand,
                                            ligandPart);
                }

                /**
                 * Location handler.
                 */
                private static final class LocationHandler extends StAXContentHandlerBase {
                    private String sequence;
                    private Position begin;
                    private Position end;
                    private Position position;

                    private final PositionHandler beginHandler = new PositionHandler();
                    private final PositionHandler endHandler = new PositionHandler();
                    private final PositionHandler positionHandler = new PositionHandler();
                    
                    @Override
                    public void startTree(final StAXContext context) {
                        sequence = null;
                        begin = null;
                        end = null;
                        position = null;
                    }

                    @Override
                    public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                        if ("location".equals(qName)) {
                            sequence = attrs.getValue("sequence");
                        }
                        else if ("begin".equals(qName)) {
                            dctx.delegate(beginHandler);
                        }
                        else if ("end".equals(qName)) {
                            dctx.delegate(endHandler);
                        }
                        else if ("position".equals(qName)) {
                            dctx.delegate(positionHandler);
                        }
                    }

                    @Override
                    public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                        if ("begin".equals(qName)) {
                            begin = (Position) result;
                        }
                        else if ("end".equals(qName)) {
                            end = (Position) result;
                        }
                        else if ("position".equals(qName)) {
                            position = (Position) result;
                        }
                    }

                    @Override
                    public Object endTree(final StAXContext context) {
                        if (begin != null && end != null) {
                            return new Location(begin, end, sequence);
                        }
                        else if (position != null) {
                            return new Location(position, sequence);
                        }
                        // throw exception?
                        return null;
                    }

                    /**
                     * Position handler.
                     */
                    private static final class PositionHandler extends StAXContentHandlerBase {
                        private Integer position;
                        private PositionStatus status;

                        @Override
                        public void startTree(final StAXContext context) {
                            position = null;
                            status = null;
                        }

                        @Override
                        public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                            position = (attrs.getValue("position") == null) ? null : Integer.parseInt(attrs.getValue("position"));
                            status = (attrs.getValue("status") == null) ? PositionStatus.CERTAIN : PositionStatus.fromDescription(attrs.getValue("status"));
                        }

                        @Override
                        public Object endTree(final StAXContext context) {
                            return new Position(position, status);
                        }
                    }
                }

                /**
                 * Ligand handler.
                 */
                private static final class LigandHandler extends StAXContentHandlerBase {
                    private String name;
                    // also dbReference, label, note
                    private final StringElementHandler nameHandler = new StringElementHandler();

                    @Override
                    public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                        if ("name".equals(qName)) {
                            dctx.delegate(nameHandler);
                        }
                    }

                    @Override
                    public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                        if ("name".equals(qName)) {
                            name = (String) result;
                        }
                    }

                    @Override
                    public Object endTree(final StAXContext context) {
                        return name;
                    }
                }

                /**
                 * Ligand part handler.
                 */
                private static final class LigandPartHandler extends StAXContentHandlerBase {
                    private String name;
                    // also dbReference, label, note
                    private final StringElementHandler nameHandler = new StringElementHandler();

                    @Override
                    public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                        if ("name".equals(qName)) {
                            dctx.delegate(nameHandler);
                        }
                    }

                    @Override
                    public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                        if ("name".equals(qName)) {
                            name = (String) result;
                        }
                    }

                    @Override
                    public Object endTree(final StAXContext context) {
                        return name;
                    }
                }
            }
        }
    }
}
