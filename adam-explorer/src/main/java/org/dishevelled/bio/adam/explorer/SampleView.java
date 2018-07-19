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

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.bdgenomics.formats.avro.Sample;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Sample view.
 *
 * @author  Michael Heuer
 */
final class SampleView extends LabelFieldPanel {

    /**
     * Create a new sample view with the specified samples.
     *
     * @param samples samples, must not be null
     */
    SampleView(final EventList<Sample> samples) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new SampleTable(samples));
    }

    /**
     * Sample table.
     */
    static class SampleTable extends ElementsTable<Sample> {
        private static final String[] PROPERTY_NAMES = { "name", "sampleId" };
        private static final String[] COLUMN_LABELS = { "Name", "Identifier" }; // todo: include processing step table?
        private static final TableFormat<Sample> TABLE_FORMAT = GlazedLists.tableFormat(Sample.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new sample table with the specified samples.
         *
         * @param samples samples, must not be null
         */
        SampleTable(final EventList<Sample> samples) {
            super("Samples:", samples, TABLE_FORMAT);

            getAddAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }
    }
}
