/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
package org.dishevelled.bio.tools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;

import java.util.concurrent.atomic.AtomicLong;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import org.apache.commons.io.output.ProxyWriter;

/**
 * Abstract split callable.
 *
 * @author  Michael Heuer
 */
abstract class AbstractSplit implements Callable<Integer> {
    /** Input file, if any. */
    protected final File inputFile;

    /** Split the input file at the next record after each n bytes, if any. */
    protected final long bytes;

    /** Split the input file after each n records, if any. */
    protected final long records;

    /** Output file prefix. */
    private final String prefix;

    /** Left pad split index in output file name by zeros. */
    private final int leftPad;

    /** Output file suffix. */
    private final String suffix;

    /** List of writers to close. */
    private final List<CountingWriter> writers = new ArrayList<CountingWriter>();

    /** Byte string pattern. */
    private static final Pattern BYTES = Pattern.compile("^(\\d+)\\s*([a-zA-Z]*)$");


    /**
     * Abstract split callable.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param leftPad left pad split index in output file name
     * @param suffix output file suffix, must not be null
     */
    protected AbstractSplit(final File inputFile,
                            final Long bytes,
                            final Long records,
                            final String prefix,
                            final int leftPad,
                            final String suffix) {
        checkNotNull(prefix);
        checkNotNull(suffix);
        this.inputFile = inputFile;
        this.bytes = bytes == null ? Long.MAX_VALUE : bytes;
        this.records = records == null ? Long.MAX_VALUE : records;
        this.prefix = prefix;
        this.leftPad = leftPad;
        this.suffix = suffix;
    }

    /**
     * Attempt to close writers created by createCountingWriter.
     */
    protected final void closeWriters() {
        for (CountingWriter writer : writers) {
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Left pad the specified split index.
     *
     * @param n split index to left pad
     * @return the specified index left padded by zeros
     */
    protected final String leftPad(final int n) {
        return leftPad > 0 ? Strings.padStart(String.valueOf(n), leftPad, '0') : String.valueOf(n);
    }

    /**
     * Create and return a new CountingWriter for a file name consisting
     * of <code>prefix + n + suffix</code>.
     *
     * @param n n
     * @return a new CountingWriter for a file name consisting
     *    of <code>prefix + n + suffix</code>
     */
    protected final CountingWriter createCountingWriter(final int n) {
        String outputFileName = prefix + leftPad(n) + suffix;
        try {
            CountingWriter writer = new CountingWriter(writer(new File(outputFileName)));
            writers.add(writer);
            return writer;
        }
        catch (IOException e) {
            throw new RuntimeException("could not create writer for file " + outputFileName, e);
        }
    }

    /**
     * Counting writer.
     */
    protected static final class CountingWriter extends ProxyWriter {
        private final AtomicLong count = new AtomicLong(0L);
        private final PrintWriter printWriter;

        /**
         * Create a new counting writer proxying the specified writer.
         *
         * @param writer writer to proxy
         */
        private CountingWriter(final Writer writer) {
            super(writer);
            printWriter = new PrintWriter(this);
        }

        @Override
        protected void beforeWrite(final int n) {
            count.addAndGet(n);
        }

        /**
         * Return the count of <code>char</code>s written to the writer
         * proxied by this counting writer.  Note the number of <code>char</code>s
         * written to the writer is not necessarily number of bytes written to disk.
         *
         * @return the count of <code>char</code>s written to the writer
         *    proxied by this counting writer
         */
        long getCount() {
            return count.longValue();
        }

        /**
         * Return this counting writer wrapped in a PrintWriter.
         *
         * @return this counting writer wrapped in a PrintWriter
         */
        PrintWriter asPrintWriter() {
            return printWriter;
        }
    }

    /**
     * Parse the specified byte string (e.g. <code>100gb</code>) into a
     * long number of bytes.
     *
     * @param b byte string to parse, must not be null or empty
     */
    static long toBytes(final String b) {
        checkNotNull(b);
        checkArgument(!b.isEmpty(), "byte string must not be empty");

        Matcher m = BYTES.matcher(b);
        if (!m.matches()) {
            throw new IllegalArgumentException("invalid byte string '" + b + "'");
        }
        String value = m.group(1);
        String unit = m.group(2);

        int pow = 0;
        if (unit == null || unit.isEmpty() || unit.equalsIgnoreCase("b")) {
            return Long.parseLong(value);
        }
        else if (unit.equalsIgnoreCase("k") || unit.equalsIgnoreCase("kb")) {
            pow = 1;
        }
        else if (unit.equalsIgnoreCase("m") || unit.equalsIgnoreCase("mb")) {
            pow = 2;
        }
        else if (unit.equalsIgnoreCase("g") || unit.equalsIgnoreCase("gb")) {
            pow = 3;
        }
        else if (unit.equalsIgnoreCase("t") || unit.equalsIgnoreCase("tb")) {
            pow = 4;
        }
        else {
            throw new IllegalArgumentException("byte string '" + b + "' has unknown unit '" + unit + "'");
        }
        return Math.round(Double.parseDouble(value) * Math.pow(1024, pow));
    }
}
