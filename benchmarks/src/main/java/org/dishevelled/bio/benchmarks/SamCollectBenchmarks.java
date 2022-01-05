/*

    dsh-bio-benchmarks.  Benchmarks.
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
package org.dishevelled.bio.benchmarks;

import static org.dishevelled.bio.alignment.sam.SamReader.streamRecords;

import static org.dishevelled.bio.benchmarks.Utils.copyResource;

import java.io.File;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bio.alignment.sam.SamAdapter;
import org.dishevelled.bio.alignment.sam.SamRecord;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * SAM collect benchmarks.
 *
 * @since 2.0
 * @author  Michael Heuer
 */
@State(Scope.Thread)
public class SamCollectBenchmarks {
    private File inputSamFile;
    private final int EXPECTED_COUNT = 10000;

    @Setup(Level.Invocation)
    public void setUp() throws Exception {
        inputSamFile = File.createTempFile("samCollectBenchmarks", ".sam");

        copyResource("CEUTrio.HiSeq.WGS.b37.NA12878.20.21.10k.sam", inputSamFile);
        //copyResource("CEUTrio.HiSeq.WGS.b37.NA12878.20.21.sam", inputSamFile);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        inputSamFile.delete();
    }

    @Benchmark
    public void collectSamSmallCapacityArrayList() throws Exception {
        Collect collect = new Collect(new ArrayList<SamRecord>());
        streamRecords(inputSamFile, collect);

        int count = 0;
        for (SamRecord record : collect.records()) {
            count++;
        }
        if (count < EXPECTED_COUNT) {
            throw new Exception("incorrect count, " + count);
        }
    }

    @Benchmark
    public void collectSamLargeCapacityArrayList() throws Exception {
        Collect collect = new Collect(new ArrayList<SamRecord>(10_000_000));
        streamRecords(inputSamFile, collect);

        int count = 0;
        for (SamRecord record : collect.records()) {
            count++;
        }
        if (count < EXPECTED_COUNT) {
            throw new Exception("incorrect count, " + count);
        }
    }

    @Benchmark
    public void collectSamLinkedList() throws Exception {
        Collect collect = new Collect(new LinkedList<SamRecord>());
        streamRecords(inputSamFile, collect);

        int count = 0;
        for (SamRecord record : collect.records()) {
            count++;
        }
        if (count < EXPECTED_COUNT) {
            throw new Exception("incorrect count, " + count);
        }
    }

    @Benchmark
    public void collectSamAppendOnlyLinkedList() throws Exception {
        Collect collect = new Collect(new AppendOnlyLinkedList<SamRecord>());
        streamRecords(inputSamFile, collect);

        int count = 0;
        for (SamRecord record : collect.records()) {
            count++;
        }
        if (count < EXPECTED_COUNT) {
            throw new Exception("incorrect count, " + count);
        }
    }

    /**
     * Collect.
     */
    private static final class Collect extends SamAdapter {
        /** List of SAM records. */
        private final Collection<SamRecord> records;

        /**
         * Create a new collect adapter with the specified collection implementation.
         *
         * @param records list of SAM records
         */
        private Collect(final Collection<SamRecord> records) {
            this.records = records;
        }


        @Override
        public boolean record(final SamRecord record) {
            records.add(record);
            return true;
        }

        /**
         * Return the list of SAM records.
         *
         * @return the list of SAM records
         */
        Collection<SamRecord> records() {
            return records;
        }
    }

    /**
     * Append only linked list.
     *
     * @param E element type
     */
    private static class AppendOnlyLinkedList<E> extends AbstractCollection<E> {
        /** Head node, if any. */
        private Node<E> head = null;

        /** Tail node, if any. */
        private Node<E> tail = null;

        @Override
        public boolean add(final E value) {
            if (head == null) {
                head = new Node<E>(value);
            }
            else {
                if (tail == null) {
                    tail = new Node<E>(value);
                    head.setNext(tail);
                }
                else {
                    Node<E> current = new Node<E>(value);
                    tail.setNext(current);
                    tail = current;
                }
            }
            return true;
        }

        @Override
        public Iterator<E> iterator() {
            return new AppendOnlyIterator();
        }

        @Override
        public int size() {
            int size = 0;
            for (Iterator<E> it = iterator(); it.hasNext(); ) {
                it.next();
                size++;
            }
            return size;
        }

        /**
         * Append only linked list node.
         */
        private class Node<E> {
            /** Value. */
            private final E value;

            /** Next node, if any. */
            private Node<E> next;

            /**
             * Create a new node with the specified value.
             *
             * @param value value
             */
            Node(final E value) {
                this.value = value;
            }

            /**
             * Return the value for this node.
             *
             * @return the value for this node
             */
            E value() {
                return value;
            }

            /**
             * Return the next node for this node, if any.
             *
             * @return the next node for this node, if any
             */
            Node<E> next() {
                return next;
            }

            /**
             * Set the next node for this node to the specified node.
             *
             * @param next the next node for this node
             */
            void setNext(final Node<E> next) {
                this.next = next;
            }
        }

        /**
         * Append only linked list iterator.
         */
        private class AppendOnlyIterator implements Iterator<E> {
            /** Current node, if any. */
            private Node<E> current;

            /**
             * Create a new append only linked list iterator.
             */
            AppendOnlyIterator() {
                current = head;
            }

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (current == null) {
                    return null;
                }
                E value = current.value();
                current = current.next();
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
