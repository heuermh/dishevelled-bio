/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2026 held jointly by the individual authors.

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
package org.dishevelled.bio.tools;

import java.util.List;
import java.util.Optional;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import htsjdk.samtools.SAMReadGroupRecord;

import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;
import org.dishevelled.commandline.argument.StringListArgument;

/**
 * Abstract callable with read group.
 *
 * @since 2.1
 * @author  Michael Heuer
 */
abstract class WithReadGroup implements Callable<Integer> {
    private final String readGroupId;
    private final String readGroupSample;
    private final String readGroupLibrary;
    private final String readGroupPlatformUnit;
    private final Integer readGroupInsertSize;
    private final List<String> readGroupBarcodes;

    /**
     * Create a new callable with a read group with the specified annotations.
     *
     * @param readGroupId read group id, if any
     * @param readGroupSample read group sample, if any
     * @param readGroupLibrary read group library, if any
     * @param readGroupPlatformUnit read group platform unit, if any
     * @param readGroupInsertSize read group insert size, if any
     * @param readGroupBarcodes read group barcodes, if any
     */
    protected WithReadGroup(@Nullable final String readGroupId,
                            @Nullable final String readGroupSample,
                            @Nullable final String readGroupLibrary,
                            @Nullable final String readGroupPlatformUnit,
                            @Nullable final Integer readGroupInsertSize,
                            @Nullable final List<String> readGroupBarcodes) {

        this.readGroupId = readGroupId;
        this.readGroupSample = readGroupSample;
        this.readGroupLibrary = readGroupLibrary;
        this.readGroupPlatformUnit = readGroupPlatformUnit;
        this.readGroupInsertSize = readGroupInsertSize;
        this.readGroupBarcodes = readGroupBarcodes;
    }


    /**
     * Create and return a new read group.
     *
     * @return a new read group
     */
    protected final SAMReadGroupRecord toReadGroup() {
        if (readGroupId == null) {
            return null;
        }
        SAMReadGroupRecord readGroup = new SAMReadGroupRecord(readGroupId);
        if (readGroupSample != null) {
            readGroup.setSample(readGroupSample);
        }
        if (readGroupLibrary != null) {
            readGroup.setLibrary(readGroupLibrary);
        }
        if (readGroupPlatformUnit != null) {
            readGroup.setPlatformUnit(readGroupPlatformUnit);
        }
        if (readGroupInsertSize != null) {
            readGroup.setPredictedMedianInsertSize(readGroupInsertSize);
        }
        if (readGroupBarcodes != null) {
            readGroup.setBarcodes(readGroupBarcodes);
        }
        return readGroup;
    }

    /**
     * Create and return an optional wrapping the read group, if any.
     *
     * @return an optional wrapping the read group, if any
     */
    protected final Optional<SAMReadGroupRecord> toReadGroupOpt() {
        return Optional.ofNullable(toReadGroup());
    }


    /**
     * Return the read group id, if any.
     *
     * @return the read group id, if any
     */
    protected final String getReadGroupId() {
        return readGroupId;
    }

    /**
     * Return the read group sample, if any.
     *
     * @return the read group sample, if any
     */
    protected final String getReadGroupSample() {
        return readGroupSample;
    }

    /**
     * Return the read group library, if any.
     *
     * @return the read group library, if any
     */
    protected final String getReadGroupLibrary() {
        return readGroupLibrary;
    }

    /**
     * Return the read group platform unit, if any.
     *
     * @return the read group platform unit, if any
     */
    protected final String getReadGroupPlatformUnit() {
        return readGroupPlatformUnit;
    }

    /**
     * Return the read group insert size, if any.
     *
     * @return the read group insert size, if any
     */
    protected final Integer getReadGroupInsertSize() {
        return readGroupInsertSize;
    }

    /**
     * Return the list of read group barcodes, if any.
     *
     * @return the list of read group barcodes, if any
     */
    protected final List<String> getReadGroupBarcodes() {
        return readGroupBarcodes;
    }


    /**
     * Create and return a new read group id commandline argument.
     *
     * @return a new read group id commandline argument
     */
    static StringArgument createReadGroupIdArgument() {
        return new StringArgument("r", "read-group-id", "read group id", false);
    }

    /**
     * Create and return a new read group sample commandline argument.
     *
     * @return a new read group sample commandline argument
     */
    static StringArgument createReadGroupSampleArgument() {
        return new StringArgument("s", "read-group-sample", "read group sample", false);
    }

    /**
     * Create and return a new read group library commandline argument.
     *
     * @return a new read group library commandline argument
     */
    static StringArgument createReadGroupLibraryArgument() {
        return new StringArgument("y", "read-group-library", "read group library", false);
    }

    /**
     * Create and return a new read group platform unit commandline argument.
     *
     * @return a new read group platform unit commandline argument
     */
    static StringArgument createReadGroupPlatformUnitArgument() {
        return new StringArgument("p", "read-group-platform-unit", "read group platform unit", false);
    }

    /**
     * Create and return a new read group insert size commandline argument.
     *
     * @return a new read group insert size commandline argument
     */
    static IntegerArgument createReadGroupInsertSizeArgument() {
        return new IntegerArgument("z", "read-group-insert-size", "read group predicted median insert size", false);
    }

    /**
     * Create and return a new read group barcodes commandline argument.
     *
     * @return a new read group barcodes commandline argument
     */
    static StringListArgument createReadGroupBarcodesArgument() {
        return new StringListArgument("b", "read-group-barcodes", "read group barcodes", false);
    }
}
