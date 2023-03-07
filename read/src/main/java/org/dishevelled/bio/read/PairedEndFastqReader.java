/*

    dsh-bio-reads  Reads.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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
package org.dishevelled.bio.read;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.StreamListener;

/**
 * Paired end FASTQ reads reader.
 *
 * @author  Michael Heuer
 */
public final class PairedEndFastqReader {
    /** Pattern for the left or first read of a paired end read, relies on conventions around "<code>1</code>" in the description line. */
    static final Pattern LEFT = Pattern.compile("^.*[/ +_\\\\]1.*$|^.* 1:[YN]:[02468]+:[0-9]+$");

    /** Pattern for the right or second read of a paired end read, relies on conventions around "<code>2</code>" in the description line. */
    static final Pattern RIGHT = Pattern.compile("^.*[/ +_\\\\]2.*$|^.* 2:[YN]:[02468]+:[0-9]+$");

    /** Pattern to split the prefix of a paired end read name, relies on conventions around "<code>1</code>" or "<code>2</code>" in the description line. */
    static final Pattern PREFIX = Pattern.compile("[/ +_\\\\]+[12]");


    /**
     * Private no-arg constructor.
     */
    private PairedEndFastqReader() {
        // empty
    }


    /**
     * Stream the specified paired end reads.  RAM usage is minimal if the paired end reads are sorted.
     *
     * @param firstReadable first readable, must not be null
     * @param secondReadable second readable, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamPaired(final Readable firstReadable,
                                    final Readable secondReadable,
                                    final PairedEndListener listener) throws IOException {

        checkNotNull(firstReadable);
        checkNotNull(secondReadable);
        checkNotNull(listener);

        final ConcurrentMap<String, Fastq> keyedByPrefix = new ConcurrentHashMap<>();

        final StreamListener streamListener = new StreamListener() {
                @Override
                public void fastq(final Fastq fastq) {
                    String prefix = prefix(fastq);
                    Fastq other = keyedByPrefix.putIfAbsent(prefix, fastq);
                    if ((other != null) && !fastq.equals(other)) {
                        if (isLeft(other) && isRight(fastq)) {
                            listener.paired(other, fastq);
                        }
                        else if (isRight(other) && isLeft(fastq)) {
                            listener.paired(fastq, other);
                        }
                        else {
                            throw new PairedEndFastqReaderException("could not determine left and right of pair, fastq " + fastq.getDescription() + " other " + other.getDescription());
                        }
                        keyedByPrefix.remove(prefix);
                    }
                }
            };

        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Callable<Void> task1 = new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    new SangerFastqReader().stream(firstReadable, streamListener);
                    return null;
                }
            };
            Callable<Void> task2 = new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    new SangerFastqReader().stream(secondReadable, streamListener);
                    return null;
                }
            };

            for (Future<Void> future : executor.invokeAll(ImmutableList.of(task1, task2))) {
                future.get();
            }
            executor.shutdown();
        }
        catch (ExecutionException e) {
            throw new IOException(e.getCause());
        }
        catch (InterruptedException e) {
            // ignore
        }
        catch (PairedEndFastqReaderException e) {
            throw new IOException("could not read paired end FASTQ reads", e);
        }

        for (Fastq unpaired : keyedByPrefix.values()) {
            listener.unpaired(unpaired);
        }
    }

    /**
     * Stream the specified interleaved paired end reads.  Per the interleaved format, all reads must be sorted and paired.
     *
     * @param readable readable, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamInterleaved(final Readable readable, final PairedEndListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        StreamListener streamListener = new StreamListener() {
                private Fastq left;

                @Override
                public void fastq(final Fastq fastq) {
                    if (isLeft(fastq) && (left == null)) {
                        left = fastq;
                    }
                    else if (isRight(fastq) && (left != null) && (prefix(left).equals(prefix(fastq)))) {
                        Fastq right = fastq;
                        listener.paired(left, right);
                        left = null;
                    }
                    else {
                        throw new PairedEndFastqReaderException("invalid interleaved FASTQ format, left " + (left == null ? "null" : left.getDescription()) + " right " + (fastq == null ? "null" : fastq.getDescription()));
                    }
                }
            };

        try {
            new SangerFastqReader().stream(readable, streamListener);
        }
        catch (PairedEndFastqReaderException e) {
            throw new IOException("could not stream interleaved paired end FASTQ reads", e);
        }
    }

    /**
     * Return true if the specified fastq is the left or first read of a paired end read.
     *
     * @param fastq fastq, must not be null
     * @return true if the specified fastq is the left or first read of a paired end read
     */
    public static boolean isLeft(final Fastq fastq) {
        checkNotNull(fastq);
        return LEFT.matcher(fastq.getDescription()).matches();
    }

    /**
     * Return true if the specified fastq is the right or second read of a paired end read.
     *
     * @param fastq fastq, must not be null
     * @return true if the specified fastq is the right or second read of a paired end read
     */
    public static boolean isRight(final Fastq fastq) {
        checkNotNull(fastq);
        return RIGHT.matcher(fastq.getDescription()).matches();
    }

    /**
     * Return the prefix of the paired end read name of the specified fastq.
     *
     * @param fastq fastq, must not be null
     * @return the prefix of the paired end read name of the specified fastq
     */
    public static String prefix(final Fastq fastq) {
        checkNotNull(fastq);
        String[] tokens = PREFIX.split(fastq.getDescription());
        return tokens[0];
    }
}
