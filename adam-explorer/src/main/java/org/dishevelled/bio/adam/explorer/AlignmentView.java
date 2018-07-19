/*

    dsh-bio-adam-explorer  Interactive explorer for ADAM data models.
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
package org.dishevelled.bio.adam.explorer;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;

import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.bdgenomics.adam.rdd.GenomicDataset;

import org.bdgenomics.adam.rdd.read.AlignmentRecordRDD;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Contig;
import org.bdgenomics.formats.avro.AlignmentRecord;
import org.bdgenomics.formats.avro.ProcessingStep;
import org.bdgenomics.formats.avro.RecordGroup;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Alignment view.
 *
 * @author  Michael Heuer
 */
final class AlignmentView extends LabelFieldPanel {
    private final AlignmentModel model;
    private final AlignmentTable table;

    /**
     * Create a new alignment view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    AlignmentView(final AlignmentRecordRDD dataset) {
        super();
        model = new AlignmentModel(dataset);
        table = new AlignmentTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Alignments", layoutAlignmentView());
        tabbedPane.add("Sequences", new ContigView(model.getSequences()));
        tabbedPane.add("Record Groups", new RecordGroupView(model.getRecordGroups()));
        tabbedPane.add("Processing Steps", new ProcessingStepView(model.getProcessingSteps()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutAlignmentView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Alignment count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Alignments currently viewing:", new CountLabel<AlignmentRecord>(model.getAlignments()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Alignment model.
     */
    static class AlignmentModel {
        private final AlignmentRecordRDD dataset;
        private final EventList<Contig> sequences;
        private final EventList<RecordGroup> recordGroups;
        private final EventList<ProcessingStep> processingSteps;
        private final EventList<AlignmentRecord> alignments;

        /**
         * Create a new alignment model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        AlignmentModel(final AlignmentRecordRDD dataset) {
            this.dataset = dataset;
            alignments = GlazedLists.eventList(new ArrayList<AlignmentRecord>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMContig()).collect(Collectors.toList()));

            List<org.bdgenomics.adam.models.RecordGroup> rg = JavaConversions.seqAsJavaList(dataset.recordGroups().recordGroups());
            recordGroups = GlazedLists.eventList(rg.stream().map(v -> v.toMetadata()).collect(Collectors.toList()));
            processingSteps = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.processingSteps()));
        }

        void take(final int take) {
            new SwingWorker<List<AlignmentRecord>, Void>() {
                @Override
                public List<AlignmentRecord> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<AlignmentRecord> result = get();

                        alignments.getReadWriteLock().writeLock().lock();
                        try {
                            alignments.clear();
                            alignments.addAll(result);
                        }
                        finally {
                            alignments.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        AlignmentRecordRDD getDataset() {
            return dataset;
        }

        EventList<AlignmentRecord> getAlignments() {
            return alignments;
        }

        EventList<Contig> getSequences() {
            return sequences;
        }

        EventList<RecordGroup> getRecordGroups() {
            return recordGroups;
        }

        EventList<ProcessingStep> getProcessingSteps() {
            return processingSteps;
        }
    }

    /**
     * Alignment table.
     */
    static class AlignmentTable extends ElementsTable<AlignmentRecord> {
        private final AlignmentModel model;
        private static final String[] PROPERTY_NAMES = { "contigName", "start", "end", "readName", "recordGroupSample", "recordGroupName" };
        private static final String[] COLUMN_LABELS = { "Contig Name", "Start", "End", "Read Name", "Sample", "Read Group" };
        private static final TableFormat<AlignmentRecord> TABLE_FORMAT = GlazedLists.tableFormat(AlignmentRecord.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new alignment table with the specified model.
         *
         * @param model model, must not be null
         */
        AlignmentTable(final AlignmentModel model) {
            super("Alignments:", model.getAlignments(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getAlignments().size() * 2);
        }
    }
}
