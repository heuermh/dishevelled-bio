/*

    dsh-bio-variant  Variants.
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
package org.dishevelled.bio.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.variant.vcf.VcfHeaderLineParser.isStructured;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * VCF header lines.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class VcfHeaderLines {
    /**
     * VCF ALT header lines keyed by ID.
     */
    private final Map<String, VcfAltHeaderLine> altHeaderLines;

    /**
     * VCF contig header lines keyed by ID.
     */
    private final Map<String, VcfContigHeaderLine> contigHeaderLines;

    /**
     * VCF FILTER header lines keyed by ID.
     */
    private final Map<String, VcfFilterHeaderLine> filterHeaderLines;

    /**
     * VCF FORMAT header lines keyed by ID.
     */
    private final Map<String, VcfFormatHeaderLine> formatHeaderLines;

    /**
     * VCF INFO header lines keyed by ID.
     */
    private final Map<String, VcfInfoHeaderLine> infoHeaderLines;

    /**
     * VCF META header lines keyed by ID.
     */
    private final Map<String, VcfMetaHeaderLine> metaHeaderLines;

    /**
     * VCF PEDIGREE header lines.
     */
    private final Set<VcfPedigreeHeaderLine> pedigreeHeaderLines;

    /**
     * VCF SAMPLE header lines keyed by ID.
     */
    private final Map<String, VcfSampleHeaderLine> sampleHeaderLines;

    /**
     * VCF file format header line.
     */
    private final VcfHeaderLine fileFormat;

    /**
     * VCF key-value header lines.
     */
    private final Set<VcfHeaderLine> headerLines;

    /**
     * Structured VCF header lines.
     */
    private final Set<VcfStructuredHeaderLine> structuredHeaderLines;


    /**
     * Create a new VCF header lines.
     *
     * @param altHeaderLines VCF ALT header lines keyed by ID, must not be null
     * @param contigHeaderLines VCF contig header lines keyed by ID, must not be null
     * @param filterHeaderLines VCF FILTER header lines keyed by ID, must not be null
     * @param formatHeaderLines VCF FORMAT header lines keyed by ID, must not be null
     * @param infoHeaderLines VCF INFO header lines keyed by ID, must not be null
     * @param metaHeaderLines VCF META header lines keyed by ID, must not be null
     * @param pedigreeHeaderLines VCF PEDIGREE header lines, must not be null
     * @param sampleHeaderLines VCF SAMPLE header lines keyed by ID, must not be null
     * @param fileFormat VCF file format header line, must not be null
     * @param headerLines VCF key-value header lines, must not be null
     * @param structuredHeaderLines structured VCF header lines, must not be null
     */
    private VcfHeaderLines(final Map<String, VcfAltHeaderLine> altHeaderLines,
                           final Map<String, VcfContigHeaderLine> contigHeaderLines,
                           final Map<String, VcfFilterHeaderLine> filterHeaderLines,
                           final Map<String, VcfFormatHeaderLine> formatHeaderLines,
                           final Map<String, VcfInfoHeaderLine> infoHeaderLines,
                           final Map<String, VcfMetaHeaderLine> metaHeaderLines,
                           final Set<VcfPedigreeHeaderLine> pedigreeHeaderLines,
                           final Map<String, VcfSampleHeaderLine> sampleHeaderLines,
                           final VcfHeaderLine fileFormat,
                           final Set<VcfHeaderLine> headerLines,
                           final Set<VcfStructuredHeaderLine> structuredHeaderLines) {
        checkNotNull(altHeaderLines);
        checkNotNull(contigHeaderLines);
        checkNotNull(filterHeaderLines);
        checkNotNull(formatHeaderLines);
        checkNotNull(infoHeaderLines);
        checkNotNull(metaHeaderLines);
        checkNotNull(pedigreeHeaderLines);
        checkNotNull(sampleHeaderLines);
        checkNotNull(fileFormat);
        checkNotNull(headerLines);
        checkNotNull(structuredHeaderLines);

        this.altHeaderLines = ImmutableMap.copyOf(altHeaderLines);
        this.contigHeaderLines = ImmutableMap.copyOf(contigHeaderLines);
        this.filterHeaderLines = ImmutableMap.copyOf(filterHeaderLines);
        this.formatHeaderLines = ImmutableMap.copyOf(formatHeaderLines);
        this.infoHeaderLines = ImmutableMap.copyOf(infoHeaderLines);
        this.metaHeaderLines = ImmutableMap.copyOf(metaHeaderLines);
        this.pedigreeHeaderLines = ImmutableSet.copyOf(pedigreeHeaderLines);
        this.sampleHeaderLines = ImmutableMap.copyOf(sampleHeaderLines);
        this.fileFormat = fileFormat;
        this.headerLines = ImmutableSet.copyOf(headerLines);
        this.structuredHeaderLines = ImmutableSet.copyOf(structuredHeaderLines);
    }


    /**
     * Return VCF ALT header lines keyed by ID.
     *
     * @return VCF ALT header lines keyed by ID
     */
    public Map<String, VcfAltHeaderLine> getAltHeaderLines() {
        return altHeaderLines;
    }

    /**
     * Return VCF contig header lines keyed by ID.
     *
     * @return VCF contig header lines keyed by ID
     */
    public Map<String, VcfContigHeaderLine> getContigHeaderLines() {
        return contigHeaderLines;
    }

    /**
     * Return VCF FILTER header lines keyed by ID.
     *
     * @return VCF FILTER header lines keyed by ID
     */
    public Map<String, VcfFilterHeaderLine> getFilterHeaderLines() {
        return filterHeaderLines;
    }

    /**
     * Return VCF FORMAT header lines keyed by ID.
     *
     * @return VCF FORMAT header lines keyed by ID
     */
    public Map<String, VcfFormatHeaderLine> getFormatHeaderLines() {
        return formatHeaderLines;
    }

    /**
     * Return VCF INFO header lines keyed by ID.
     *
     * @return VCF INFO header lines keyed by ID
     */
    public Map<String, VcfInfoHeaderLine> getInfoHeaderLines() {
        return infoHeaderLines;
    }

    /**
     * Return VCF META header lines keyed by ID.
     *
     * @return VCF META header lines keyed by ID
     */
    public Map<String, VcfMetaHeaderLine> getMetaHeaderLines() {
        return metaHeaderLines;
    }

    /**
     * Return VCF PEDIGREE header lines.
     *
     * @return VCF PEDIGREE header lines
     */
    public Set<VcfPedigreeHeaderLine> getPedigreeHeaderLines() {
        return pedigreeHeaderLines;
    }

    /**
     * Return VCF SAMPLE header lines keyed by ID.
     *
     * @return VCF SAMPLE header lines keyed by ID
     */
    public Map<String, VcfSampleHeaderLine> getSampleHeaderLines() {
        return sampleHeaderLines;
    }

    /**
     * Return the VCF file format header line.
     *
     * @return the VCF file format header line
     */
    public VcfHeaderLine getFileFormat() {
        return fileFormat;
    }
    /**
     * Return VCF key=value header lines.
     *
     * @return VCF key=value header lines
     */
    public Set<VcfHeaderLine> getHeaderLines() {
        return headerLines;
    }

    /**
     * Return structured VCF ALT header lines.
     *
     * @return structured VCF header lines
     */
    public Set<VcfStructuredHeaderLine> getStructuredHeaderLines() {
        return structuredHeaderLines;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fileFormat.toString());
        sb.append("\n");
        for (VcfHeaderLine line : headerLines) {
            sb.append(line.toString());
            sb.append("\n");
        }
        for (VcfAltHeaderLine altHeaderLine : altHeaderLines.values()) {
            sb.append(altHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfContigHeaderLine contigHeaderLine : contigHeaderLines.values()) {
            sb.append(contigHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfFilterHeaderLine filterHeaderLine : filterHeaderLines.values()) {
            sb.append(filterHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfFormatHeaderLine formatHeaderLine : formatHeaderLines.values()) {
            sb.append(formatHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfInfoHeaderLine infoHeaderLine : infoHeaderLines.values()) {
            sb.append(infoHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfMetaHeaderLine metaHeaderLine : metaHeaderLines.values()) {
            sb.append(metaHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfPedigreeHeaderLine pedigreeHeaderLine : pedigreeHeaderLines) {
            sb.append(pedigreeHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfSampleHeaderLine sampleHeaderLine : sampleHeaderLines.values()) {
            sb.append(sampleHeaderLine.toString());
            sb.append("\n");
        }
        for (VcfStructuredHeaderLine structuredHeaderLine : structuredHeaderLines) {
            sb.append(structuredHeaderLine);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Create a new VcfHeaderLines from the specified VcfHeader.
     *
     * @param header VcfHeader, must not be null
     * @return a new VcfHeaderLines created from the specified VcfHeader
     */
    public static VcfHeaderLines fromHeader(final VcfHeader header) {
        checkNotNull(header);

        Map<String, VcfAltHeaderLine> altHeaderLines = new HashMap<String, VcfAltHeaderLine>();
        Map<String, VcfContigHeaderLine> contigHeaderLines = new HashMap<String, VcfContigHeaderLine>();
        Map<String, VcfFilterHeaderLine> filterHeaderLines = new HashMap<String, VcfFilterHeaderLine>();
        Map<String, VcfFormatHeaderLine> formatHeaderLines = new HashMap<String, VcfFormatHeaderLine>();
        Map<String, VcfInfoHeaderLine> infoHeaderLines = new HashMap<String, VcfInfoHeaderLine>();
        Map<String, VcfMetaHeaderLine> metaHeaderLines = new HashMap<String, VcfMetaHeaderLine>();
        Set<VcfPedigreeHeaderLine> pedigreeHeaderLines = new HashSet<VcfPedigreeHeaderLine>();
        Map<String, VcfSampleHeaderLine> sampleHeaderLines = new HashMap<String, VcfSampleHeaderLine>();
        VcfHeaderLine fileFormat = VcfHeaderLine.valueOf("##fileformat=" + header.getFileFormat());
        Set<VcfHeaderLine> headerLines = new HashSet<VcfHeaderLine>();
        Set<VcfStructuredHeaderLine> structuredHeaderLines = new HashSet<VcfStructuredHeaderLine>();

        for (String meta : header.getMeta()) {
            if (meta.startsWith("##ALT=")) {
                VcfAltHeaderLine altHeaderLine = VcfAltHeaderLine.valueOf(meta);
                altHeaderLines.put(altHeaderLine.getId(), altHeaderLine);
            }
            else if (meta.startsWith("##contig=")) {
                VcfContigHeaderLine contigHeaderLine = VcfContigHeaderLine.valueOf(meta);
                contigHeaderLines.put(contigHeaderLine.getId(), contigHeaderLine);
            }
            else if (meta.startsWith("##FILTER=")) {
                VcfFilterHeaderLine filterHeaderLine = VcfFilterHeaderLine.valueOf(meta);
                filterHeaderLines.put(filterHeaderLine.getId(), filterHeaderLine);
            }
            else if (meta.startsWith("##FORMAT=")) {
                VcfFormatHeaderLine formatHeaderLine = VcfFormatHeaderLine.valueOf(meta);
                formatHeaderLines.put(formatHeaderLine.getId(), formatHeaderLine);
            }
            else if (meta.startsWith("##INFO=")) {
                VcfInfoHeaderLine infoHeaderLine = VcfInfoHeaderLine.valueOf(meta);
                infoHeaderLines.put(infoHeaderLine.getId(), infoHeaderLine);
            }
            else if (meta.startsWith("##META=")) {
                VcfMetaHeaderLine metaHeaderLine = VcfMetaHeaderLine.valueOf(meta);
                metaHeaderLines.put(metaHeaderLine.getId(), metaHeaderLine);
            }
            else if (meta.startsWith("##PEDIGREE=")) {
                VcfPedigreeHeaderLine pedigreeHeaderLine = VcfPedigreeHeaderLine.valueOf(meta);
                pedigreeHeaderLines.add(pedigreeHeaderLine);
            }
            else if (meta.startsWith("##SAMPLE=")) {
                VcfSampleHeaderLine sampleHeaderLine = VcfSampleHeaderLine.valueOf(meta);
                sampleHeaderLines.put(sampleHeaderLine.getId(), sampleHeaderLine);
            }
            else if (isStructured(meta)) {
                VcfStructuredHeaderLine structuredHeaderLine = VcfStructuredHeaderLine.valueOf(meta);
                structuredHeaderLines.add(structuredHeaderLine);
            }
            else if (!meta.startsWith("##fileformat=")) {
                VcfHeaderLine headerLine = VcfHeaderLine.valueOf(meta);
                headerLines.add(headerLine);
            }
        }

        return new VcfHeaderLines(altHeaderLines,
                                  contigHeaderLines,
                                  filterHeaderLines,
                                  formatHeaderLines,
                                  infoHeaderLines,
                                  metaHeaderLines,
                                  pedigreeHeaderLines,
                                  sampleHeaderLines,
                                  fileFormat,
                                  headerLines,
                                  structuredHeaderLines);
    }

    // todo: builder?
}
