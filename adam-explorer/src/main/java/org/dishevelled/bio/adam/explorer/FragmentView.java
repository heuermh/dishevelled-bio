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

import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.bdgenomics.adam.rdd.fragment.FragmentRDD;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Contig;
import org.bdgenomics.formats.avro.Fragment;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Fragment view.
 *
 * @author  Michael Heuer
 */
final class FragmentView extends LabelFieldPanel {
    private final FragmentModel model;
    private final FragmentTable table;

    FragmentView(final FragmentRDD dataset) {
        super();
        model = new FragmentModel(dataset);
        table = new FragmentTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Fragments", layoutFragmentView());
        tabbedPane.add("Sequences", new ContigView(model.getSequences()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutFragmentView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Fragment count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Fragments currently viewing:", new CountLabel<Fragment>(model.getFragments()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Fragment model.
     */
    static class FragmentModel {
        private final FragmentRDD dataset;
        private final EventList<Contig> sequences;
        private final EventList<Fragment> fragments;

        FragmentModel(final FragmentRDD dataset) {
            this.dataset = dataset;
            fragments = GlazedLists.eventList(new ArrayList<Fragment>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMContig()).collect(Collectors.toList()));
        }

        void take(final int take) {
            new SwingWorker<List<Fragment>, Void>() {
                @Override
                public List<Fragment> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Fragment> result = get();

                        fragments.getReadWriteLock().writeLock().lock();
                        try {
                            fragments.clear();
                            fragments.addAll(result);
                        }
                        finally {
                            fragments.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        FragmentRDD getDataset() {
            return dataset;
        }

        EventList<Fragment> getFragments() {
            return fragments;
        }

        EventList<Contig> getSequences() {
            return sequences;
        }
    }

    /**
     * Fragment table.
     */
    static class FragmentTable extends ElementsTable<Fragment> {
        private final FragmentModel model;
        private static final String[] PROPERTY_NAMES = { "readName", "fragmentSize", "runId", "instrument", "alignments" };
        private static final String[] COLUMN_LABELS = { "Name", "Insert Size", "Run", "Instrument", "Alignments" };
        private static final TableFormat<Fragment> TABLE_FORMAT = GlazedLists.tableFormat(Fragment.class, PROPERTY_NAMES, COLUMN_LABELS);

        FragmentTable(final FragmentModel model) {
            super("Fragments:", model.getFragments(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getFragments().size() * 2);
        }
    }
}
