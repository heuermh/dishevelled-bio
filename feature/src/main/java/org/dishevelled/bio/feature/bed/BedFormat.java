/*

    dsh-bio-feature  Sequence features.
    Copyright (c) 2013-2025 held jointly by the individual authors.

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
package org.dishevelled.bio.feature.bed;

/**
 * BED formats.
 *
 * @author  Michael Heuer
 */
public enum BedFormat
{
    /** BED3 format, chrom start end. */
    BED3,

    /** BED4 format, chrom start end name. */
    BED4,

    /** BED5 format, chrom start end name score. */
    BED5,

    /** BED6 format, chrom start end name score strand. */
    BED6,

    /** BED12 format, chrom start end name score strand thickStart thickEnd itemRgb blockCount blockSizes blockStarts. */
    BED12;

    /**
     * Return true if this BED format is BED3.
     *
     * @return true if this BED format is BED3
     */
    public boolean isBED3() {
        return this == BED3;
    }

    /**
     * Return true if this BED format is at least BED3.
     *
     * @return true if this BED format is at least BED3
     */
    public boolean isAtLeastBED3() {
        return true;
    }

    /**
     * Return true if this BED format is BED4.
     *
     * @return true if this BED format is BED4
     */
    public boolean isBED4() {
        return this == BED4;
    }

    /**
     * Return true if this BED format is at least BED4.
     *
     * @return true if this BED format is at least BED4
     */
    public boolean isAtLeastBED4() {
        return isBED4() || isBED5() || isBED6() || isBED12();
    }

    /**
     * Return true if this BED format is BED5.
     *
     * @return true if this BED format is BED5
     */
    public boolean isBED5() {
        return this == BED5;
    }

    /**
     * Return true if this BED format is at least BED5.
     *
     * @return true if this BED format is at least BED5
     */
    public boolean isAtLeastBED5() {
        return isBED5() || isBED6() || isBED12();
    }

    /**
     * Return true if this BED format is BED6.
     *
     * @return true if this BED format is BED6
     */
    public boolean isBED6() {
        return this == BED6;
    }

    /**
     * Return true if this BED format is at least BED6.
     *
     * @return true if this BED format is at least BED6
     */
    public boolean isAtLeastBED6() {
        return isBED6() || isBED12();
    }

    /**
     * Return true if this BED format is BED12.
     *
     * @return true if this BED format is BED12
     */
    public boolean isBED12() {
        return this == BED12;
    }

    /**
     * Return true if this BED format is at least BED12.
     *
     * @return true if this BED format is at least BED12
     */
    public boolean isAtLeastBED12() {
        return isBED12();
    }
}
