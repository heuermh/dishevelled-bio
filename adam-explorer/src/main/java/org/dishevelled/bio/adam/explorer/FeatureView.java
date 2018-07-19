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

import org.bdgenomics.adam.rdd.feature.FeatureRDD;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Contig;
import org.bdgenomics.formats.avro.Feature;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Feature view.
 *
 * @author  Michael Heuer
 */
final class FeatureView extends LabelFieldPanel {
    private final FeatureModel model;
    private final FeatureTable table;

    /**
     * Create a new feature view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    FeatureView(final FeatureRDD dataset) {
        super();
        model = new FeatureModel(dataset);
        table = new FeatureTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Features", layoutFeatureView());
        tabbedPane.add("Sequences", new ContigView(model.getSequences()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutFeatureView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Feature count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Features currently viewing:", new CountLabel<Feature>(model.getFeatures()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Feature model.
     */
    static class FeatureModel {
        private final FeatureRDD dataset;
        private final EventList<Contig> sequences;
        private final EventList<Feature> features;

        /**
         * Create a new feature model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        FeatureModel(final FeatureRDD dataset) {
            this.dataset = dataset;
            features = GlazedLists.eventList(new ArrayList<Feature>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMContig()).collect(Collectors.toList()));
        }

        void take(final int take) {
            new SwingWorker<List<Feature>, Void>() {
                @Override
                public List<Feature> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Feature> result = get();

                        features.getReadWriteLock().writeLock().lock();
                        try {
                            features.clear();
                            features.addAll(result);
                        }
                        finally {
                            features.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        FeatureRDD getDataset() {
            return dataset;
        }

        EventList<Feature> getFeatures() {
            return features;
        }

        EventList<Contig> getSequences() {
            return sequences;
        }
    }

    /**
     * Feature table.
     */
    static class FeatureTable extends ElementsTable<Feature> {
        private final FeatureModel model;
        private static final String[] PROPERTY_NAMES = { "contigName", "start", "end", "strand", "name", "featureId", "featureType", "score" };
        private static final String[] COLUMN_LABELS = { "Contig Name", "Start", "End", "Strand", "Name", "Identifier", "Type", "Score" };
        private static final TableFormat<Feature> TABLE_FORMAT = GlazedLists.tableFormat(Feature.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new feature table with the specified model.
         *
         * @param model model, must not be null
         */
        FeatureTable(final FeatureModel model) {
            super("Features:", model.getFeatures(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getFeatures().size() * 2);
        }
    }
}
