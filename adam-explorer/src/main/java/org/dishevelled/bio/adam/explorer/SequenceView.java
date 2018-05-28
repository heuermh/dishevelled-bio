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

import org.bdgenomics.adam.models.SequenceRecord;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Sequence view.
 *
 * @author  Michael Heuer
 */
final class SequenceView extends LabelFieldPanel {

    SequenceView(final EventList<SequenceRecord> sequences) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new SequenceTable(sequences));
    }

    /**
     * Sequence table.
     */
    static class SequenceTable extends ElementsTable<SequenceRecord> {
        private static final String[] PROPERTY_NAMES = { "name", "length" };
        private static final String[] COLUMN_LABELS = { "Name", "Length" };
        private static final TableFormat<SequenceRecord> TABLE_FORMAT = GlazedLists.tableFormat(SequenceRecord.class, PROPERTY_NAMES, COLUMN_LABELS);

        SequenceTable(final EventList<SequenceRecord> sequences) {
            super("Sequences:", sequences, TABLE_FORMAT);

            getAddAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }
    }
}
