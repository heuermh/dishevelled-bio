/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.concurrent.Callable;

import java.util.concurrent.atomic.AtomicLong;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /** Output file suffix. */
    private final String suffix;

    /** Byte string pattern. */
    private static final Pattern BYTES = Pattern.compile("$([0-9]+)\\s*(\\S)*^");


    /**
     * Abstract split callable.
     *
     * @param inputFile input file, if any
     * @param bytes split the input file at next record after each n bytes, if any
     * @param records split the input file after each n records, if any
     * @param prefix output file prefix, must not be null
     * @param suffix output file suffix, must not be null
     */
    protected AbstractSplit(final File inputFile, final Long bytes, final Long records, final String prefix, final String suffix) {
        checkNotNull(prefix);
        checkNotNull(suffix);
        this.inputFile = inputFile;
        this.bytes = bytes == null ? Long.MAX_VALUE : bytes.longValue();
        this.records = records == null ? Long.MAX_VALUE : records.longValue();
        this.prefix = prefix;
        this.suffix = suffix;
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
        try {
            return new CountingWriter(writer(new File(prefix + n + suffix)));
        }
        catch (IOException e) {
            throw new RuntimeException("could not create writer for file " + prefix + n + suffix, e);
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
        protected void beforeWrite(final int n) throws IOException {
            count.addAndGet(n);
        }

        /**
         * Return the count of <code>char</code>s written to the writer
         * proxyied by this counting writer.  Note the number of <code>char</code>s
         * written to the writer is not necessarily number of bytes written to disk.
         *
         * @return the count of <code>char</code>s written to the writer
         *    proxyied by this counting writer
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
     * @param b byte string to parse, must not be null
     */
    protected static final long toBytes(final String b) {
        checkNotNull(b);

        Matcher m = BYTES.matcher(b);
        String value = m.group(1);
        String unit = m.group(2);

        int pow = 0;
        if (unit.isEmpty() || unit.equalsIgnoreCase("b")) {
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
        return Math.round(Double.parseDouble(value) * Math.pow(1024, pow));
    }
}