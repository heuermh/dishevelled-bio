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

import htsjdk.variant.vcf.VCFHeaderLine;

import org.bdgenomics.adam.rdd.GenomicDataset;

import org.bdgenomics.adam.rdd.variant.VariantRDD;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Contig;
import org.bdgenomics.formats.avro.Variant;
import org.bdgenomics.formats.avro.ProcessingStep;
import org.bdgenomics.formats.avro.RecordGroup;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Variant view.
 *
 * @author  Michael Heuer
 */
final class VariantView extends LabelFieldPanel {
    private final VariantModel model;
    private final VariantTable table;

    VariantView(final VariantRDD dataset) {
        super();
        model = new VariantModel(dataset);
        table = new VariantTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Variants", layoutVariantView());
        tabbedPane.add("Sequences", new ContigView(model.getSequences()));
        tabbedPane.add("Header Lines", new HeaderLineView(model.getHeaderLines()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutVariantView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Variant count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Variants currently viewing:", new CountLabel<Variant>(model.getVariants()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Variant model.
     */
    static class VariantModel {
        private final VariantRDD dataset;
        private final EventList<Contig> sequences;
        private final EventList<VCFHeaderLine> headerLines;
        private final EventList<Variant> variants;

        VariantModel(final VariantRDD dataset) {
            this.dataset = dataset;
            variants = GlazedLists.eventList(new ArrayList<Variant>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMContig()).collect(Collectors.toList()));

            headerLines = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.headerLines()));
        }

        void take(final int take) {
            new SwingWorker<List<Variant>, Void>() {
                @Override
                public List<Variant> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Variant> result = get();

                        variants.getReadWriteLock().writeLock().lock();
                        try {
                            variants.clear();
                            variants.addAll(result);
                        }
                        finally {
                            variants.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        VariantRDD getDataset() {
            return dataset;
        }

        EventList<Variant> getVariants() {
            return variants;
        }

        EventList<Contig> getSequences() {
            return sequences;
        }

        EventList<VCFHeaderLine> getHeaderLines() {
            return headerLines;
        }
    }

    /**
     * Variant table.
     */
    static class VariantTable extends ElementsTable<Variant> {
        private final VariantModel model;
        private static final String[] PROPERTY_NAMES = { "contigName", "start", "end", "referenceAllele", "alternateAllele" };
        private static final String[] COLUMN_LABELS = { "Contig Name", "Start", "End", "Ref", "Alt" };
        private static final TableFormat<Variant> TABLE_FORMAT = GlazedLists.tableFormat(Variant.class, PROPERTY_NAMES, COLUMN_LABELS);

        VariantTable(final VariantModel model) {
            super("Variants:", model.getVariants(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getVariants().size() * 2);
        }
    }
}
