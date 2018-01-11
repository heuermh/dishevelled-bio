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

/**
 * Abstract implementation of a Graphical Fragment Assembly (GFA) 1.0 listener.
 *
 * @author  Michael Heuer
 */
public abstract class Gfa1Adapter implements Gfa1Listener {

    @Override
    public final boolean record(final Gfa1Record record) {
        if (record instanceof Containment) {
            return containment((Containment) record);
        }
        else if (record instanceof Header) {
            return header((Header) record);
        }
        else if (record instanceof Path) {
            return path((Path) record);
        }
        else if (record instanceof Link) {
            return link((Link) record);
        }
        else if (record instanceof Segment) {
            return segment((Segment) record);
        }
        throw new IllegalStateException("unrecognized subclass of Gfa1Record, " + record.getClass());
    }

    /**
     * Notify this abstract GFA 1.0 listener of a GFA 1.0 containment.
     *
     * @param containment GFA 1.0 containment
     * @return true to continue processing, false to stop
     */
    protected boolean containment(final Containment containment) {
        return true;
    }

    /**
     * Notify this abstract GFA 1.0 listener of a GFA 1.0 header.
     *
     * @param header GFA 1.0 header
     * @return true to continue processing, false to stop
     */
    protected boolean header(final Header header) {
        return true;
    }

    /**
     * Notify this abstract GFA 1.0 listener of a GFA 1.0 path.
     *
     * @param path GFA 1.0 path
     * @return true to continue processing, false to stop
     */
    protected boolean path(final Path path) {
        return true;
    }

    /**
     * Notify this abstract GFA 1.0 listener of a GFA 1.0 link.
     *
     * @param link GFA 1.0 link
     * @return true to continue processing, false to stop
     */
    protected boolean link(final Link link) {
        return true;
    }

    /**
     * Notify this abstract GFA 1.0 listener of a GFA 1.0 segment.
     *
     * @param segment GFA 1.0 segment
     * @return true to continue processing, false to stop
     */
    protected boolean segment(final Segment segment) {
        return true;
    }
}
