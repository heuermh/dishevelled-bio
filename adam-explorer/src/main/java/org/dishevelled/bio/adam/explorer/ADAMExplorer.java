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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.bdgenomics.adam.rdd.GenomicDataset;

import org.bdgenomics.adam.rdd.feature.FeatureRDD;
import org.bdgenomics.adam.rdd.fragment.FragmentRDD;
import org.bdgenomics.adam.rdd.read.AlignmentRecordRDD;
//import org.bdgenomics.adam.rdd.read.ReadRDD;
//import org.bdgenomics.adam.rdd.sequence.SequenceRDD;
//import org.bdgenomics.adam.rdd.sequence.SliceRDD;
import org.bdgenomics.adam.rdd.variant.GenotypeRDD;
import org.bdgenomics.adam.rdd.variant.VariantRDD;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.AlignmentRecord;
import org.bdgenomics.formats.avro.ProcessingStep;
import org.bdgenomics.formats.avro.RecordGroup;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsList;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Interactive explorer for ADAM data models.
 *
 * @author  Michael Heuer
 */
public final class ADAMExplorer {

    public static int explore(final AlignmentRecordRDD alignments) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new AlignmentExplorer(alignments).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Alignment explorer.
     */
    static class AlignmentExplorer extends JFrame {
        AlignmentExplorer(final AlignmentRecordRDD alignments) {
            super("Alignments");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new AlignmentView(alignments));
        }
    }

    public static int explore(final FeatureRDD features) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new FeatureExplorer(features).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Feature explorer.
     */
    static class FeatureExplorer extends JFrame {
        FeatureExplorer(final FeatureRDD features) {
            super("Features");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new FeatureView(features));
        }
    }

    public static int explore(final FragmentRDD fragments) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new FragmentExplorer(fragments).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Fragment explorer.
     */
    static class FragmentExplorer extends JFrame {
        FragmentExplorer(final FragmentRDD fragments) {
            super("Fragments");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new FragmentView(fragments));
        }
    }

    public static int explore(final GenotypeRDD genotypes) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new GenotypeExplorer(genotypes).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Genotype explorer.
     */
    static class GenotypeExplorer extends JFrame {
        GenotypeExplorer(final GenotypeRDD genotypes) {
            super("Genotypes");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new GenotypeView(genotypes));
        }
    }

    /*
    public static int explore(final ReadRDD reads) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ReadExplorer(reads).setVisible(true);
                }
            });
        return 0;
    }

    static class ReadExplorer extends JFrame {
        ReadExplorer(final ReadRDD reads) {
            super("Reads");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new ReadView(reads));
        }
    }

    public static int explore(final SequenceRDD sequences) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SequenceExplorer(sequences).setVisible(true);
                }
            });
        return 0;
    }

    static class SequenceExplorer extends JFrame {
        SequenceExplorer(final SequenceRDD sequences) {
            super("Sequences");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new SequenceView(sequences));
        }
    }

    public static int explore(final SliceRDD slices) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SliceExplorer(slices).setVisible(true);
                }
            });
        return 0;
    }

    static class SliceExplorer extends JFrame {
        SliceExplorer(final SliceRDD slices) {
            super("Slices");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new SliceView(slices));
        }
    }
    */

    public static int explore(final VariantRDD variants) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new VariantExplorer(variants).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Variant explorer.
     */
    static class VariantExplorer extends JFrame {
        VariantExplorer(final VariantRDD variants) {
            super("Variants");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new VariantView(variants));
        }
    }
}
