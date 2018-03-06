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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.bio.alignment.sam.SamFields.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

/**
 * SAM record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class SamRecord {
    private final long lineNumber;
    private final String qname;
    private final int flag;
    private final String rname;
    private final int pos;
    private final int mapq;
    private final String cigar;
    private final String rnext;
    private final int pnext;
    private final int tlen;
    private final String seq;
    private final String qual;
    private final ListMultimap<String, String> fields;
    private final Map<String, String> fieldTypes;
    private final Map<String, String> fieldArrayTypes;

    private SamRecord(final long lineNumber,
                      @Nullable final String qname,
                      final int flag,
                      @Nullable final String rname,
                      final int pos,
                      final int mapq,
                      @Nullable final String cigar,
                      @Nullable final String rnext,
                      final int pnext,
                      final int tlen,
                      @Nullable final String seq,
                      @Nullable final String qual,
                      final ListMultimap<String, String> fields,
                      final Map<String, String> fieldTypes,
                      final Map<String, String> fieldArrayTypes) {

        checkNotNull(fields);
        checkNotNull(fieldTypes);
        checkNotNull(fieldArrayTypes);

        this.lineNumber = lineNumber;
        this.qname = qname;
        this.flag = flag;
        this.rname = rname;
        this.pos = pos;
        this.mapq = mapq;
        this.cigar = cigar;
        this.rnext = rnext;
        this.pnext = pnext;
        this.tlen = tlen;
        this.seq = seq;
        this.qual = qual;
        this.fields = fields;
        this.fieldTypes = fieldTypes;
        this.fieldArrayTypes = fieldArrayTypes;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getQname() {
        return qname;
    }

    public Optional<String> getQnameOpt() {
        return Optional.ofNullable(qname);
    }

    public int getFlag() {
        return flag;
    }

    public String getRname() {
        return rname;
    }

    public Optional<String> getRnameOpt() {
        return Optional.ofNullable(rname);
    }

    public int getPos() {
        return pos;
    }

    public int getMapq() {
        return mapq;
    }

    public String getCigar() {
        return cigar;
    }

    public Optional<String> getCigarOpt() {
        return Optional.ofNullable(cigar);
    }

    public String getRnext() {
        return rnext;
    }

    public Optional<String> getRnextOpt() {
        return Optional.ofNullable(rnext);
    }

    public int getPnext() {
        return pnext;
    }

    public int getTlen() {
        return tlen;
    }

    public String getSeq() {
        return seq;
    }

    public Optional<String> getSeqOpt() {
        return Optional.ofNullable(seq);
    }

    public String getQual() {
        return qual;
    }

    public Optional<String> getQualOpt() {
        return Optional.ofNullable(qual);
    }

    public ListMultimap<String, String> getFields() {
        return fields;
    }

    public Map<String, String> getFieldTypes() {
        return fieldTypes;
    }

    public Map<String, String> getFieldArrayTypes() {
        return fieldArrayTypes;
    }

    // public boolean containsAm()
    // public int getAm()
    // public Optional<Integer> getAmOpt()

    public boolean containsFieldKey(final String key) {
        return fields.containsKey(key);
    }

    public char getFieldCharacter(final String key) {
        return parseCharacter(key, fields);
    }
    
    public float getFieldFloat(final String key) {
        return parseFloat(key, fields);
    }

    public int getFieldInteger(final String key) {
        return parseInteger(key, fields);
    }

    public String getFieldString(final String key) {
        return parseString(key, fields);
    }

    public List<Float> getFieldFloats(final String key) {
        return parseFloats(key, fields);
    }

    public List<Integer> getFieldIntegers(final String key) {
        return parseIntegers(key, fields);
    }

    public Optional<Character> getFieldCharacterOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldCharacter(key) : null);
    }

    public Optional<Float> getFieldFloatOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloat(key) : null);
    }

    public Optional<Integer> getFieldIntegerOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldInteger(key) : null);
    }

    public Optional<String> getFieldStringOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldString(key) : null);
    }

    public Optional<List<Float>> getFieldFloatsOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldFloats(key) : null);
    }

    public Optional<List<Integer>> getFieldIntegersOpt(final String key) {
        return Optional.ofNullable(containsFieldKey(key) ? getFieldIntegers(key) : null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final SamRecord record) {
        checkNotNull(record);
        return new Builder()
            .withLineNumber(record.getLineNumber())
            .withQname(record.getQname())
            .withFlag(record.getFlag())
            .withRname(record.getRname())
            .withPos(record.getPos())
            .withMapq(record.getMapq())
            .withCigar(record.getCigar())
            .withRnext(record.getRnext())
            .withPnext(record.getPnext())
            .withTlen(record.getTlen())
            .withSeq(record.getSeq())
            .withQual(record.getQual())
            .withFields(record.getFields(), record.getFieldTypes(), record.getFieldArrayTypes());
    }

    public static final class Builder {
        private long lineNumber;
        private String qname;
        private int flag;
        private String rname;
        private int pos;
        private int mapq;
        private String cigar;
        private String rnext;
        private int pnext;
        private int tlen;
        private String seq;
        private String qual;
        private ImmutableListMultimap.Builder<String, String> fields = ImmutableListMultimap.builder();
        private ImmutableMap.Builder<String, String> fieldTypes = ImmutableMap.builder();
        private ImmutableMap.Builder<String, String> fieldArrayTypes = ImmutableMap.builder();

        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }

        public Builder withLineNumber(final long lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder withQname(@Nullable final String qname) {
            this.qname = qname;
            return this;
        }

        public Builder withFlag(final int flag) {
            this.flag = flag;
            return this;
        }

        public Builder withRname(@Nullable final String rname) {
            this.rname = rname;
            return this;
        }

        public Builder withPos(final int pos) {
            this.pos = pos;
            return this;
        }

        public Builder withMapq(final int mapq) {
            this.mapq = mapq;
            return this;
        }

        public Builder withCigar(@Nullable final String cigar) {
            this.cigar = cigar;
            return this;
        }

        public Builder withRnext(@Nullable final String rnext) {
            this.rnext = rnext;
            return this;
        }

        public Builder withPnext(final int pnext) {
            this.pnext = pnext;
            return this;
        }

        public Builder withTlen(final int tlen) {
            this.tlen = tlen;
            return this;
        }

        public Builder withSeq(@Nullable final String seq) {
            this.seq = seq;
            return this;
        }

        public Builder withQual(@Nullable final String qual) {
            this.qual = qual;
            return this;
        }

        public Builder withField(final String tag, final String type, final String value) {
            return withArrayField(tag, type, null, value);
        }

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

        public Builder withFields(final ListMultimap<String, String> fields, final Map<String, String> fieldTypes, @Nullable final Map<String, String> fieldArrayTypes) {
            this.fields.putAll(fields);
            this.fieldTypes.putAll(fieldTypes);
            this.fieldArrayTypes.putAll(fieldArrayTypes);
            return this;
        }

        public Builder replaceField(final String tag, final String type, final String value) {
            return replaceArrayField(tag, type, null, value);
        }

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

        public Builder replaceFields(final ListMultimap<String, String> fields, final Map<String, String> fieldTypes, final Map<String, String> fieldArrayTypes) {
            this.fields = ImmutableListMultimap.builder();
            this.fieldTypes = ImmutableMap.builder();
            this.fieldArrayTypes = ImmutableMap.builder();
            return withFields(fields, fieldTypes, fieldArrayTypes);
        }

        public Builder reset() {
            lineNumber = -1L;
            qname = null;
            flag = 0;
            rname = null;
            pos = 0;
            mapq = 255;
            cigar = null;
            rnext = null;
            pnext = 0;
            tlen = 0;
            seq = null;
            qual = null;
            fields = ImmutableListMultimap.builder();
            fieldTypes = ImmutableMap.builder();
            fieldArrayTypes = ImmutableMap.builder();
            return this;
        }

        public SamRecord build() {
            // todo: check fields and field types and field array types are consistent
            // todo: check if seq and qual are the same length
            //   etc.
            
            return new SamRecord(lineNumber, qname, flag, rname, pos, mapq, cigar, rnext, pnext, tlen, seq, qual, fields.build(), fieldTypes.build(), fieldArrayTypes.build());
        }
    }
}
