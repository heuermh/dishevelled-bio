/*

    dsh-bio-adam  Adapt dsh-bio models to ADAM.
    Copyright (c) 2013-2017 held jointly by the individual authors.

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
package org.dishevelled.bio.adam;

import com.google.common.collect.ImmutableListMultimap;

import com.esotericsoftware.kryo.Kryo;

import com.esotericsoftware.kryo.serializers.JavaSerializer;

import org.bdgenomics.adam.serialization.ADAMKryoRegistrator;

/**
 * Dishevelled kryo registrator.
 *
 * @author  Michael Heuer
 */
public class DishevelledKryoRegistrator extends ADAMKryoRegistrator {

    @Override
    public void registerClasses(final Kryo kryo) {
        super.registerClasses(kryo);

        kryo.register(ImmutableListMultimap.class, new JavaSerializer());
    }
}
