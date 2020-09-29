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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.alignment.gaf.GafFields.parseByteArray;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseBytes;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseCharacter;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseFloat;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseFloats;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseInteger;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseIntegers;
import static org.dishevelled.bio.alignment.gaf.GafFields.parseString;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

/**
 * GAF (graph alignment format) record.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
@Immutable
public final class GafRecord {
    /** Line number. */
    private final long lineNumber;

    /** Query name. */
    private final String queryName;

    /** Query length. */
    private final long queryLength;

    /** Query start. */
    private final long queryStart;

    /** Query end. */
    private final long queryEnd;

    /** Relative strand. */
    private final char strand;

    /** Path name. */
    /*

A path on column 6 is defined by the following grammar:

 <path>       <- <stableId> | <orientIntv>+
<orientIntv> <- ('>' | '<') (<segId> | <stableIntv>)
<stableIntv> <- <stableId> ':' <start> '-' <end>

where <segId> is the second column on an S-line in GFA and <stableId>
is an identifier on an SN tag in rGFA. With <segId>, GAF can encode
alignments against an ordinary GFA. <stableId> is preferred for alignments
against rGFA. Notably, if a path consists of a single interval on a stable
sequence, we can simplify the entire path to <stableId> without <start>
and <end>, and assume the orientation to be forward >. Such a GAF line is
reduced to PAF.

The following example shows the read mapping for GTGGCT and CGTTTCC against
the example rGFA above. In the unstable segment coordinate, the GAF is:

read1  6  0  6  +  >s2>s3>s4           12  2   8  6  6  60  cg:Z:6M
read2  7  0  7  +  >s2>s5>s6           11  1   8  7  7  60  cg:Z:7M

In the stable coordinate, the GAF becomes:

read1  6  0  6  +  chr1                17  7  13  6  6  60  cg:Z:6M
read2  7  0  7  +  >chr1:5-8>foo:8-16  11  1   8  7  7  60  cg:Z:7M

In the stable form, GAF remains the same as long as bases are not removed
from rGFA.

     */
    private final String pathName;

    /** Path length. */
    private final long pathLength;

    /** Path start. */
    private final long pathStart;

    /** Path end. */
    private final long pathEnd;

    /** Number of residue matches. */
    private final long matches;

    /** Alignment block length. */
    private final long alignmentBlockLength;

    /** Mapping quality. */
    private final int mappingQuality;

    /** Optional field values keyed by tag. */
    private final ListMultimap<String, String> fields;

    /** Optional field types keyed by tag. */
    private final Map<String, String> fieldTypes;

    /** Optional field array types keyed by tag. */
    private final Map<String, String> fieldArrayTypes;


    /**
     * Create a new GAF (graph alignment format) record.
     *
     * @param lineNumber line number
     * @param queryName query name
     * @param queryLength query length
     * @param queryStart query start
     * @param queryEnd query end
     * @param strand relative strand, must be '+' or '-'
     * @param pathName path name
     * @param pathLength path length
     * @param pathStart path start
     * @param pathEnd path end
     * @param matches number of residue matches
     * @param alignmentBlockLength alignment block length
     * @param mappingQuality mapping quality
     * @param fields optional field values keyed by tag, must not be null
     * @param fieldTypes optional field types keyed by tag, must not be null
     * @param fieldArrayTypes optional field array types keyed by tag, must not be null
     */
    private GafRecord(final long lineNumber,
                      @Nullable final String queryName,
                      final long queryLength,
                      final long queryStart,
                      final long queryEnd,
                      final char strand,
                      @Nullable final String pathName,
                      final long pathLength,
                      final long pathStart,
                      final long pathEnd,
                      final long matches,
                      final long alignmentBlockLength,
                      final int mappingQuality,
                      final ListMultimap<String, String> fields,
                      final Map<String, String> fieldTypes,
                      final Map<String, String> fieldArrayTypes) {

        checkArgument('+' == strand || '-' == strand, "strand must be one of { '+', '-' }");
        checkNotNull(fields);
        checkNotNull(fieldTypes);
        checkNotNull(fieldArrayTypes);

        this.lineNumber = lineNumber;
        this.queryName = queryName;
        this.queryLength = queryLength;
        this.queryStart = queryStart;
        this.queryEnd = queryEnd;
        this.strand = strand;
        this.pathName = pathName;
        this.pathLength = pathLength;
        this.pathStart = pathStart;
        this.pathEnd = pathEnd;
        this.matches = matches;
        this.alignmentBlockLength = alignmentBlockLength;
        this.mappingQuality = mappingQuality;
        this.fields = fields;
        this.fieldTypes = fieldTypes;
        this.fieldArrayTypes = fieldArrayTypes;
    }


    /**
     * Return the line number for this GAF record.
     *
     * @return the line number for this GAF record
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * Return the query name for this GAF record. May be null.
     *
     * @return the query name for this GAF record
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Return an optional wrapping the query name for this GAF record.
     *
     * @return an optional wrapping the query name for this GAF record
     */
    public Optional<String> getQueryNameOpt() {
        return Optional.ofNullable(queryName);
    }

    /**
     * Return the query length for this GAF record.
     *
     * @return the query length for this GAF record
     */
    public long getQueryLength() {
        return queryLength;
    }

    /**
     * Return the query start for this GAF record.
     *
     * @return the query start for this GAF record
     */
    public long getQueryStart() {
        return queryStart;
    }

    /**
     * Return the query end for this GAF record.
     *
     * @return the query end for this GAF record
     */
    public long getQueryEnd() {
        return queryEnd;
    }

    /**
     * Return the relative strand for this GAF record.
     *
     * @return the relative strand for this GAF record
     */
    public char getStrand() {
        return strand;
    }

    /**
     * Return the path name for this GAF record. May be null.
     *
     * @return the path name for this GAF record.
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Return an optional wrapping the path name for this GAF record.
     *
     * @return an optional wrapping the path name for this GAF record
     */
    public Optional<String> getPathNameOpt() {
        return Optional.ofNullable(pathName);
    }

    /**
     * Return the path length for this GAF record.
     *
     * @return the path length for this GAF record
     */
    public long getPathLength() {
        return pathLength;
    }

    /**
     * Return the path start for this GAF record.
     *
     * @return the path start for this GAF record
     */
    public long getPathStart() {
        return pathStart;
    }

    /**
     * Return the path end for this GAF record.
     *
     * @return the path end for this GAF record
     */
    public long getPathEnd() {
        return pathEnd;
    }

    /**
     * Return the number of residue matches for this GAF record.
     *
     * @return the number of residue matches for this GAF record
     */
    public long getMatches() {
        return matches;
    }

    /**
     * Return the alignment block length for this GAF record.
     *
     * @return the alignment block length for this GAF record
     */
    public long getAlignmentBlockLength() {
        return alignmentBlockLength;
    }

    /**
     * Return the mapping quality for this GAF record.
     *
     * @return the mapping quality for this GAF record
     */
    public int getMappingQuality() {
        return mappingQuality;
    }

    /**
     * Return the optional field values for this GAF record keyed by tag.
     *
     * @return the optional field values for this GAF record keyed by tag
     */
    public ListMultimap<String, String> getFields() {
        return fields;
    }

    /**
     * Return the optional field types for this GAF record keyed by tag.
     *
     * @return the optional field types for this GAF record keyed by tag
     */
    public Map<String, String> getFieldTypes() {
        return fieldTypes;
    }

    /**
     * Return the optional field array types for this GAF record keyed by tag.
     *
     * @return the optional field array types for this GAF record keyed by tag
     */
    public Map<String, String> getFieldArrayTypes() {
        return fieldArrayTypes;
    }

    /**
     * Return true if this GAF record contains the specified optional field key.
     *
     * @param key key
     * @return true if this GAF record contains the specified optional field key
     */
    public boolean containsFieldKey(final String key) {
        return fields.containsKey(key);
    }

    /**
     * Return the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return the Type=A field value for the specified key parsed into a character
     */
    public char getFieldCharacter(final String key) {
        return parseCharacter(key, fields);
    }
    
    /**
     * Return the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return the Type=f field value for the specified key parsed into a float
     */
    public float getFieldFloat(final String key) {
        return parseFloat(key, fields);
    }

    /**
     * Return the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return the Type=i field value for the specified key parsed into an integer
     */
    public int getFieldInteger(final String key) {
        return parseInteger(key, fields);
    }

    /**
     * Return the Type=H field value for the specified key parsed into a byte array.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into a byte array
     */
    public byte[] getFieldByteArray(final String key) {
        return parseByteArray(key, fields);
    }

    /**
     * Return the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public List<Byte> getFieldBytes(final String key) {
        return parseBytes(key, fields);
    }

    /**
     * Return the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return the Type=Z field value for the specified key parsed into a string
     */
    public String getFieldString(final String key) {
        return parseString(key, fields);
    }

    /**
     * Return the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public List<Float> getFieldFloats(final String key) {
        return parseFloats(key, fields);
    }

    /**
     * Return the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public List<Integer> getFieldIntegers(final String key) {
        return parseIntegers(key, fields);
    }

    /**
     * Return an optional wrapping the Type=A field value for the specified key parsed into a character.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=A field value for the specified key parsed into a character
     */
    public Optional<Character> getFieldCharacterOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldCharacter(key) : null);
    }

    /**
     * Return an optional wrapping the Type=f field value for the specified key parsed into a float.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=f field value for the specified key parsed into a float
     */
    public Optional<Float> getFieldFloatOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloat(key) : null);
    }

    /**
     * Return an optional wrapping the Type=i field value for the specified key parsed into an integer.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=i field value for the specified key parsed into an integer
     */
    public Optional<Integer> getFieldIntegerOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldInteger(key) : null);
    }

    /**
     * Return an optional wrapping the Type=Z field value for the specified key parsed into a string.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=Z field value for the specified key parsed into a string
     */
    public Optional<String> getFieldStringOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldString(key) : null);
    }

    /**
     * Return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=H field value for the specified key parsed into an immutable list of bytes
     */
    public Optional<List<Byte>> getFieldBytesOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldBytes(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter f field value for the specified key parsed
     * into an immutable list of floats.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter f field value for the specified key parsed
     *    into an immutable list of floats
     */
    public Optional<List<Float>> getFieldFloatsOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloats(key) : null);
    }

    /**
     * Return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     * into an immutable list of integers.
     *
     * @param key key, must not be null
     * @return an optional wrapping the Type=B first letter [cCsSiI] field value for the specified key parsed
     *    into an immutable list of integers
     */
    public Optional<List<Integer>> getFieldIntegersOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldIntegers(key) : null);
    }


    /**
     * Return a new GAF record builder.
     *
     * @return a new GAF record builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Return a new GAF record builder populated with the fields in the specified GAF record.
     *
     * @param record GAF record, must not be null
     * @return a new GAF record builder populated with the fields in the specified GAF record
     */
    public static Builder builder(final GafRecord record) {
        checkNotNull(record);
        return new Builder()
            .withLineNumber(record.getLineNumber())
            .withQueryName(record.getQueryName())
            .withQueryLength(record.getQueryLength())
            .withQueryStart(record.getQueryStart())
            .withQueryEnd(record.getQueryEnd())
            .withStrand(record.getStrand())
            .withPathName(record.getPathName())
            .withPathLength(record.getPathLength())
            .withPathStart(record.getPathStart())
            .withPathEnd(record.getPathEnd())
            .withMatches(record.getMatches())
            .withAlignmentBlockLength(record.getAlignmentBlockLength())
            .withMappingQuality(record.getMappingQuality())
            .withFields(record.getFields(), record.getFieldTypes(), record.getFieldArrayTypes());                            
    }


    /**
     * GAF record builder.
     */
    public static final class Builder {
        /** Line number. */
        private long lineNumber;

        /** Query name. */
        private String queryName;

        /** Query length. */
        private long queryLength;

        /** Query start. */
        private long queryStart;

        /** Query end. */
        private long queryEnd;

        /** Relative strand. */
        private char strand = '+';

        /** Path name. */
        private String pathName;

        /** Path length. */
        private long pathLength;

        /** Path start. */
        private long pathStart;

        /** Path end. */
        private long pathEnd;

        /** Number of residue matches. */
        private long matches;

        /** Alignment block length. */
        private long alignmentBlockLength;

        /** Mapping quality. */
        private int mappingQuality = 255;

        /** Optional field values keyed by tag. */
        private ImmutableListMultimap.Builder<String, String> fields = ImmutableListMultimap.builder();

        /** Optional field types keyed by tag. */
        private ImmutableMap.Builder<String, String> fieldTypes = ImmutableMap.builder();

        /** Optional field array types keyed by tag. */
        private ImmutableMap.Builder<String, String> fieldArrayTypes = ImmutableMap.builder();

        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this GAF record builder configured with the specified line number.
         *
         * @param lineNumber line number
         * @return this GAF record builder configured with the specified line number
         */
        public Builder withLineNumber(final long lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified query name.
         *
         * @param queryName query name
         * @return this GAF record builder configured with the specified query name
         */
        public Builder withQueryName(final String queryName) {
            this.queryName = queryName;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified query length.
         *
         * @param queryLength query length
         * @return this GAF record builder configured with the specified query length
         */
        public Builder withQueryLength(final long queryLength) {
            this.queryLength = queryLength;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified query start.
         *
         * @param queryStart query start
         * @return this GAF record builder configured with the specified query start
         */
        public Builder withQueryStart(final long queryStart) {
            this.queryStart = queryStart;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified query end.
         *
         * @param queryEnd query end
         * @return this GAF record builder configured with the specified query end
         */
        public Builder withQueryEnd(final long queryEnd) {
            this.queryEnd = queryEnd;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified relative strand.
         *
         * @param strand relative strand
         * @return this GAF record builder configured with the specified relative strand
         */
        public Builder withStrand(final char strand) {
            this.strand = strand;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified path name.
         *
         * @param pathName path name
         * @return this GAF record builder configured with the specified path name
         */
        public Builder withPathName(final String pathName) {
            this.pathName = pathName;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified path length.
         *
         * @param pathLength path length
         * @return this GAF record builder configured with the specified path length
         */
        public Builder withPathLength(final long pathLength) {
            this.pathLength = pathLength;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified path start.
         *
         * @param pathStart path start
         * @return this GAF record builder configured with the specified path start
         */
        public Builder withPathStart(final long pathStart) {
            this.pathStart = pathStart;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified path end.
         *
         * @param pathEnd path end
         * @return this GAF record builder configured with the specified path end
         */
        public Builder withPathEnd(final long pathEnd) {
            this.pathEnd = pathEnd;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified number of residue matches.
         *
         * @param matches number of residue matches
         * @return this GAF record builder configured with the specified number of residue matches
         */
        public Builder withMatches(final long matches) {
            this.matches = matches;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified alignment block length
         *
         * @param alignmentBlockLength alignment block length
         * @return this GAF record builder configured with the specified alignment block length
         */
        public Builder withAlignmentBlockLength(final long alignmentBlockLength) {
            this.alignmentBlockLength = alignmentBlockLength;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified mapping quality.
         *
         * @param mappingQuality mapping quality
         * @return this GAF record builder configured with the specified mapping quality
         */
        public Builder withMappingQuality(final int mappingQuality) {
            this.mappingQuality = mappingQuality;
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified optional field.
         *
         * @param tag optional field tag
         * @param type optional field type
         * @param value optional field value
         * @return this GAF record builder configured with the specified optional field
         */
        public Builder withField(final String tag, final String type, final String value) {
            return withArrayField(tag, type, null, value);
        }

        /**
         * Return this GAF record builder configured with the specified optional array field.
         *
         * @param tag optional array field tag
         * @param type optional array field type
         * @param arrayType optional array field arra type
         * @param values variable number of optional array field values
         * @return this GAF record builder configured with the specified optional array field
         */
        public Builder withArrayField(final String tag, final String type, @Nullable final String arrayType, final String... values) {
            checkNotNull(values);

            fieldTypes.put(tag, type);
            if (arrayType != null) {
                fieldArrayTypes.put(tag, arrayType);
            }
            for (String value : values) {
                fields.put(tag, value);
            }
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified optional fields.
         *
         * @param fields optional field values keyed by tag
         * @param fieldTypes optional field types keyed by tag
         * @param fieldArrayTypes optional field array types keyed by tag
         * @return this GAF record builder configured with the specified optional fields
         */
        public Builder withFields(final ListMultimap<String, String> fields, final Map<String, String> fieldTypes, @Nullable final Map<String, String> fieldArrayTypes) {
            this.fields.putAll(fields);
            this.fieldTypes.putAll(fieldTypes);
            if (fieldArrayTypes != null) {
                this.fieldArrayTypes.putAll(fieldArrayTypes);
            }
            return this;
        }

        /**
         * Return this GAF record builder configured with the specified optional field replacing
         * the previously configured value. Use sparingly, more expensive than <code>withField</code>.
         *
         * @param tag optional field tag
         * @param type optional field type
         * @param value optional field value
         * @return this GAF record builder configured with the specified optional field replacing
         *    the previously configured value
         */
        public Builder replaceField(final String tag, final String type, final String value) {
            return replaceArrayField(tag, type, null, value);
        }

        /**
         * Return this GAF record builder configured with the specified optional array field replacing
         * the previously configured value(s). Use sparingly, more expensive than <code>withArrayField</code>.
         *
         * @param tag optional array field tag
         * @param type optional array field type
         * @param arrayType optional array field array type
         * @param values variable number of optional field values
         * @return this GAF record builder configured with the specified optional array field replacing
         *    the previously configured value(s)
         */
        public Builder replaceArrayField(final String tag, final String type, @Nullable final String arrayType, final String... values) {
            checkNotNull(values);

            // copy old field values except tag
            ListMultimap<String, String> oldFields = this.fields.build();
            this.fields = ImmutableListMultimap.builder();
            for (String key : oldFields.keys()) {
                if (!key.equals(tag)) {
                    this.fields.putAll(key, oldFields.get(key));
                }
            }
            Map<String, String> oldFieldTypes = this.fieldTypes.build();
            this.fieldTypes = ImmutableMap.builder();
            for (String key : oldFieldTypes.keySet()) {
                if (!key.equals(tag)) {
                    this.fieldTypes.put(key, oldFieldTypes.get(key));
                }
            }
            Map<String, String> oldFieldArrayTypes = this.fieldArrayTypes.build();
            this.fieldArrayTypes = ImmutableMap.builder();
            for (String key : oldFieldArrayTypes.keySet()) {
                if (!key.equals(tag)) {
                    this.fieldArrayTypes.put(key, oldFieldArrayTypes.get(key));
                }
            }

            // add new value(s)
            return withArrayField(tag, type, arrayType, values);
        }

        /**
         * Return this GAF record builder configured with the specified optional fields replacing
         * the previously configured field(s). Use sparingly, more expensive than <code>withFields</code>.
         *
         * @param fields optional field values keyed by tag
         * @param fieldTypes optional field types keyed by tag
         * @param fieldArrayTypes optional field array types keyed by tag
         * @return this GAF record builder configured with the specified optional fields replacing
         *    the previously configured field(s)
         */
        public Builder replaceFields(final ListMultimap<String, String> fields, final Map<String, String> fieldTypes, final Map<String, String> fieldArrayTypes) {
            this.fields = ImmutableListMultimap.builder();
            this.fieldTypes = ImmutableMap.builder();
            this.fieldArrayTypes = ImmutableMap.builder();
            return withFields(fields, fieldTypes, fieldArrayTypes);
        }

        /**
         * Reset this GAF record builder.
         *
         * @return this GAF record builder
         */
        public Builder reset() {
            lineNumber = 0L;
            queryName = null;
            queryLength = 0L;
            queryStart = 0L;
            queryEnd = 0L;
            strand = '+';
            pathName = null;
            pathLength = 0L;
            pathStart = 0L;
            pathEnd = 0L;
            matches = 0L;
            alignmentBlockLength = 0L;
            mappingQuality = 255;
            fields = ImmutableListMultimap.builder();
            fieldTypes = ImmutableMap.builder();
            fieldArrayTypes = ImmutableMap.builder();
            return this;
        }

        /**
         * Create and return a new GAF record populated from the configuration of this GAF record builder.
         *
         * @return a new GAF record populated from the configuration of this GAF record builder
         */
        public GafRecord build() {
            return new GafRecord(lineNumber,
                                 queryName,
                                 queryLength,
                                 queryStart,
                                 queryEnd,
                                 strand,
                                 pathName,
                                 pathLength,
                                 pathStart,
                                 pathEnd,
                                 matches,
                                 alignmentBlockLength,
                                 mappingQuality,
                                 fields.build(),
                                 fieldTypes.build(),
                                 fieldArrayTypes.build());
        }
    }
}
