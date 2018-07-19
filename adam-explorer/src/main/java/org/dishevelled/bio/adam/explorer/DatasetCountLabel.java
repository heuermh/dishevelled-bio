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

import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.bdgenomics.adam.rdd.GenomicDataset;

/**
 * Dataset count label.
 *
 * @author  Michael Heuer
 */
class DatasetCountLabel extends JLabel {

    /**
     * Create a new dataset count label for the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    DatasetCountLabel(final GenomicDataset dataset) {
        super("");
        new SwingWorker<Long, Void>() {
            @Override
            public Long doInBackground() {
                return dataset.jrdd().count();
            }

            @Override
            public void done() {
                try {
                    setText(get().toString());
                }
                catch (InterruptedException | ExecutionException e) {
                    // ignore
                }
            }
        }.execute();
    }
}
