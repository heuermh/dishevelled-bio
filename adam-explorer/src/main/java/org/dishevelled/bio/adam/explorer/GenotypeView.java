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

import htsjdk.variant.vcf.VCFHeaderLine;

import org.bdgenomics.adam.rdd.variant.GenotypeRDD;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Contig;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.Sample;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Genotype view.
 *
 * @author  Michael Heuer
 */
final class GenotypeView extends LabelFieldPanel {
    private final GenotypeModel model;
    private final GenotypeTable table;

    GenotypeView(final GenotypeRDD dataset) {
        super();
        model = new GenotypeModel(dataset);
        table = new GenotypeTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Genotypes", layoutGenotypeView());
        tabbedPane.add("Sequences", new ContigView(model.getSequences()));
        tabbedPane.add("Samples", new SampleView(model.getSamples()));
        tabbedPane.add("Header Lines", new HeaderLineView(model.getHeaderLines()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutGenotypeView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Genotype count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Genotypes currently viewing:", new CountLabel<Genotype>(model.getGenotypes()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Genotype model.
     */
    static class GenotypeModel {
        private final GenotypeRDD dataset;
        private final EventList<Contig> sequences;
        private final EventList<Sample> samples;
        private final EventList<VCFHeaderLine> headerLines;
        private final EventList<Genotype> genotypes;

        GenotypeModel(final GenotypeRDD dataset) {
            this.dataset = dataset;
            genotypes = GlazedLists.eventList(new ArrayList<Genotype>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMContig()).collect(Collectors.toList()));

            samples = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.samples()));
            headerLines = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.headerLines()));
        }

        void take(final int take) {
            new SwingWorker<List<Genotype>, Void>() {
                @Override
                public List<Genotype> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Genotype> result = get();

                        genotypes.getReadWriteLock().writeLock().lock();
                        try {
                            genotypes.clear();
                            genotypes.addAll(result);
                        }
                        finally {
                            genotypes.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        GenotypeRDD getDataset() {
            return dataset;
        }

        EventList<Genotype> getGenotypes() {
            return genotypes;
        }

        EventList<Contig> getSequences() {
            return sequences;
        }

        EventList<Sample> getSamples() {
            return samples;
        }

        EventList<VCFHeaderLine> getHeaderLines() {
            return headerLines;
        }
    }

    /**
     * Genotype table.
     */
    static class GenotypeTable extends ElementsTable<Genotype> {
        private final GenotypeModel model;
        private static final String[] PROPERTY_NAMES = { "contigName", "start", "end", "variant.referenceAllele", "variant.alternateAllele", "alleles", "sampleId" };
        private static final String[] COLUMN_LABELS = { "Contig Name", "Start", "End", "Ref", "Alt", "Alleles", "Sample" };
        private static final TableFormat<Genotype> TABLE_FORMAT = GlazedLists.tableFormat(Genotype.class, PROPERTY_NAMES, COLUMN_LABELS);

        GenotypeTable(final GenotypeModel model) {
            super("Genotypes:", model.getGenotypes(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getGenotypes().size() * 2);
        }
    }
}
