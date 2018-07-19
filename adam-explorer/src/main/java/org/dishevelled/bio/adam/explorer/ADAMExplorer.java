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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.bdgenomics.adam.rdd.feature.FeatureRDD;
import org.bdgenomics.adam.rdd.fragment.FragmentRDD;
import org.bdgenomics.adam.rdd.read.AlignmentRecordRDD;
//import org.bdgenomics.adam.rdd.read.ReadRDD;
//import org.bdgenomics.adam.rdd.sequence.SequenceRDD;
//import org.bdgenomics.adam.rdd.sequence.SliceRDD;
import org.bdgenomics.adam.rdd.variant.GenotypeRDD;
import org.bdgenomics.adam.rdd.variant.VariantRDD;

/**
 * Interactive explorer for ADAM data models.
 *
 * @author  Michael Heuer
 */
public final class ADAMExplorer {

    /**
     * Explore the specified alignments.
     *
     * @param alignments alignments to expore, must not be null
     * @return an exit code
     */
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

        /**
         * Create a new alignment explorer.
         *
         * @param alignments alignments to explore, must not be null
         */
        AlignmentExplorer(final AlignmentRecordRDD alignments) {
            super("Alignments");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new AlignmentView(alignments));
        }
    }

    /**
     * Explore the specified features.
     *
     * @param features features to expore, must not be null
     * @return an exit code
     */
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

        /**
         * Create a new feature explorer.
         *
         * @param features features to explore, must not be null
         */
        FeatureExplorer(final FeatureRDD features) {
            super("Features");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new FeatureView(features));
        }
    }

    /**
     * Explore the specified fragments.
     *
     * @param fragments fragments to expore, must not be null
     * @return an exit code
     */
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

        /**
         * Create a new fragment explorer.
         *
         * @param fragments fragments to explore, must not be null
         */
        FragmentExplorer(final FragmentRDD fragments) {
            super("Fragments");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new FragmentView(fragments));
        }
    }

    /**
     * Explore the specified genotypes.
     *
     * @param genotypes genotypes to expore, must not be null
     * @return an exit code
     */
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

        /**
         * Create a new genotype explorer.
         *
         * @param genotypes genotypes to explore, must not be null
         */
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

    /**
     * Explore the specified variants.
     *
     * @param variants variants to expore, must not be null
     * @return an exit code
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

        /**
         * Create a new variant explorer.
         *
         * @param variants variants to explore, must not be null
         */
        VariantExplorer(final VariantRDD variants) {
            super("Variants");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new VariantView(variants));
        }
    }
}
