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

import java.awt.Component;
import java.awt.Color;

import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

import javax.swing.border.EmptyBorder;

/**
 * Stripe list cell renderer.
 *
 * @author  Michael Heuer
 */
final class StripeListCellRenderer extends DefaultListCellRenderer {
    static final Color EVEN_COLOR = new Color(42, 87, 3, 12); // 2a5703, 5% alpha

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, boolean isSelected, boolean hasFocus)
    {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

        if (isSelected) {
            label.setForeground(UIManager.getColor("List.selectionForeground"));
            label.setBackground(UIManager.getColor("List.selectionBackground"));
        }
        else {
            label.setForeground(UIManager.getColor("List.foreground"));

            if (index % 2 == 0) {
                label.setBackground(EVEN_COLOR);
            }
            else {
                label.setBackground(UIManager.getColor("List.background"));
            }
        }
        return label;
    }

    static void install(final JList list) {
        StripeListCellRenderer renderer = new StripeListCellRenderer();
        list.setCellRenderer(renderer);
    }
}
