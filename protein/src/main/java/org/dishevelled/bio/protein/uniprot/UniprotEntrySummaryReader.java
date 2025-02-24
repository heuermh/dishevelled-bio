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
 * Streaming UniProt XML reader that extracts entry summaries.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
@Immutable
public final class UniprotEntrySummaryReader {

    /**
     * Private no-arg constructor.
     */
    private UniprotEntrySummaryReader() {
        // empty
    }


    /**
     * Stream UniProt XML from the specified reader and extract entry summaries.
     *
     * @param reader UniProt XML reader to read from, must not be null
     * @param callback entry summary callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Reader reader, final EntrySummaryListener callback) throws IOException  {
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
     * Classify an organism type from the specified lineage.
     *
     * @param lineage lineage, must not be null
     * @return an organism type from the specified lineage
     */
    static String classifyOrganism(final String lineage) {
        checkNotNull(lineage);

        if (lineage.contains("Viridiplantae")) {
            return "Plant";
        }
        else if (lineage.contains("Metazoa")) {
            return "Animal";
        }
        else if (lineage.contains("Fungi")) {
            return "Fungi";
        }
        else if (lineage.contains("Eukaryota")) {
            return "other Eukaryota";
        }
        else if (lineage.contains("Bacteria")) {
            return "Bacteria";
        }
        else if (lineage.contains("Archaea")) {
            return "Archaea";
        }
        else if (lineage.contains("Viruses")) {
            return "Viruses";
        }
        return "other";
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
        private UniprotHandler(final EntrySummaryListener callback) {
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
            private String organism;
            private String organismId;
            private String lineage;
            private String type;
            private boolean reviewed;
            private boolean unreviewed;
            private boolean hasStructure;

            private final EntrySummaryListener callback;
            private final LineageHandler lineageHandler = new LineageHandler();
            private final DbReferenceHandler dbReferenceHandler = new DbReferenceHandler();
            private final ScientificNameHandler scientificNameHandler = new ScientificNameHandler();

            /**
             * Create a new entry handler with the specified callback.
             *
             * @param callback callback
             */
            private EntryHandler(final EntrySummaryListener callback) {
                this.callback = callback;
            }

            @Override
            public void startTree(final StAXContext context) {
                reviewed = false;
                unreviewed = false;
                hasStructure = false;
            }

            @Override
            public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                if ("entry".equals(qName)) {
                    reviewed = "Swiss-Prot".equals(attrs.getValue("dataset"));
                    unreviewed = "TrEMBL".equals(attrs.getValue("dataset"));
                }
                else if ("lineage".equals(qName)) {
                    dctx.delegate(lineageHandler);
                }
                else if ("dbReference".equals(qName)) {
                    dctx.delegate(dbReferenceHandler);
                }
                else if ("name".equals(qName)) {
                    dctx.delegate(scientificNameHandler);
                }
            }

            @Override
            public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                if ("lineage".equals(qName)) {
                    lineage = (String) result;
                    type = classifyOrganism(lineage);
                }
                else if ("dbReference".equals(qName)) {
                    if (result != null) {
                        organismId = (String) result;
                    }
                    hasStructure |= dbReferenceHandler.hasStructure();
                }
                else if ("name".equals(qName)) {
                    if (result != null) {
                        organism = (String) result;
                    }
                }
            }

            @Override
            public Object endTree(final StAXContext context) {
                // todo: end parsing if rv is false?
                callback.entrySummary(new EntrySummary(organism, organismId, lineage, type, reviewed, unreviewed, hasStructure));
                return null;
            }

            /**
             * Lineage handler.
             */
            private static final class LineageHandler extends StAXContentHandlerBase {
                private final List<String> lineage = new ArrayList<String>();
                private final StringElementHandler taxonHandler = new StringElementHandler();

                @Override
                public void startTree(final StAXContext context) {
                    lineage.clear();
                }
                
                @Override
                public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) throws SAXException {
                    if ("taxon".equals(qName)) {
                        dctx.delegate(taxonHandler);
                    }
                }

                @Override
                public void endElement(final String nsURI, final String localName, final String qName, final Object result, final StAXContext context) {
                    if ("taxon".equals(qName)) {
                        lineage.add((String) result);
                    }
                }

                @Override
                public Object endTree(final StAXContext context) {
                    return Joiner.on("; ").join(lineage);
                }
            }

            /**
             * Database reference handler.
             */
            private static final class DbReferenceHandler extends StAXContentHandlerBase {
                private String taxonId;
                private boolean hasStructure;

                @Override
                public void startTree(final StAXContext context) {
                    taxonId = null;
                    hasStructure = false;
                }
                
                @Override
                public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) {
                    if ("PDB".equals(attrs.getValue("type"))) {
                        hasStructure = true;
                    }
                    else if ("NCBI Taxonomy".equals(attrs.getValue("type"))) {
                        taxonId = attrs.getValue("id");
                    }
                }

                @Override
                public Object endTree(final StAXContext context) {
                    return taxonId;
                }

                boolean hasStructure() {
                    return hasStructure;
                }
            }

            /**
             * Scientific name handler.
             */
            private static final class ScientificNameHandler extends StAXContentHandlerBase {
                private boolean scientificName;
                private final StringBuffer data = new StringBuffer();

                @Override
                public void startTree(final StAXContext context) {
                    scientificName = false;
                    data.delete(0, data.length());
                }
                
                @Override
                public void startElement(final String nsURI, final String localName, final String qName, final Attributes attrs, final StAXDelegationContext dctx) {
                    if ("scientific".equals(attrs.getValue("type"))) {
                        scientificName = true;
                    }
                }

                @Override
                public void characters(final char[] ch, final int start, final int length, final StAXContext ctx) {
                    data.append(ch, start, length);
                }

                @Override
                public Object endTree(final StAXContext context) {
                    if (scientificName) {
                        return data.substring(0);
                    }
                    else {
                        return null;
                    }
                }
            }
        }
    }
}
