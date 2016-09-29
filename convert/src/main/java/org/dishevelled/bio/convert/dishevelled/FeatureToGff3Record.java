/*

    dsh-convert  Convert between various data models.
    Copyright (c) 2013-2016 held jointly by the individual authors.

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
package org.dishevelled.bio.convert.dishevelled;

import javax.annotation.concurrent.Immutable;

import org.bdgenomics.convert.AbstractConverter;
import org.bdgenomics.convert.ConversionException;
import org.bdgenomics.convert.ConversionStringency;

import org.bdgenomics.formats.avro.Feature;

import org.dishevelled.bio.feature.Gff3Record;

import org.slf4j.Logger;

/**
 * Convert bdg-formats Feature to dishevelled Gff3Record.
 *
 * @author  Michael Heuer
 */
@Immutable
public final class FeatureToGff3Record extends AbstractConverter<Feature, Gff3Record> {

    /**
     * Package private no-arg constructor.
     */
    FeatureToGff3Record() {
        super(Feature.class, Gff3Record.class);
    }


    @Override
    public Gff3Record convert(final Feature feature,
                              final ConversionStringency stringency,
                              final Logger logger) throws ConversionException {

        if (feature == null) {
            warnOrThrow(feature, "must not be null", null, stringency, logger);
            return null;
        }
        Gff3Record gff3Record = null;
        try {
            // todo:  need to convert feature to GFF3 String format ??
            gff3Record = Gff3Record.valueOf(feature.toString());
        }
        catch (NumberFormatException e) {
            warnOrThrow(feature, "caught NumberFormatException", e, stringency, logger);
        }
        catch (IllegalArgumentException e) {
            warnOrThrow(feature, "caught IllegalArgumentException", e, stringency, logger);
        }
        return gff3Record;
    }
}
