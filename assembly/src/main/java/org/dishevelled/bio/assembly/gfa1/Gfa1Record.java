/*

    dsh-bio-assembly  Assemblies.
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
package org.dishevelled.bio.assembly.gfa1;

import java.util.Map;

import org.dishevelled.bio.assembly.gfa.GfaRecord;
import org.dishevelled.bio.assembly.gfa.Tag;

/**
 * Graphical Fragment Assembly (GFA) 1.0 record.
 *
 * @author  Michael Heuer
 */
public abstract class Gfa1Record extends GfaRecord {

    /**
     * Create a new GFA 1.0 record with the specified tags.
     *
     * @param tags tags, must not be null
     */
    protected Gfa1Record(final Map<String, Tag> tags) {
        super(tags);
    }
}
