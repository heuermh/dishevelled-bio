/*

    dsh-bio-compress-hdfs  HDFS support for dsh-compress.
    Copyright (c) 2013-2022 held jointly by the individual authors.

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
package org.dishevelled.bio.compress.hdfs;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Compress.isBgzfFilename;
import static org.dishevelled.compress.Compress.isBzip2Filename;
import static org.dishevelled.compress.Compress.isGzipFilename;
import static org.dishevelled.compress.Compress.isXzFilename;
import static org.dishevelled.compress.Compress.isZstdFilename;
import static org.dishevelled.compress.Readers.bgzfInputStreamReader;
import static org.dishevelled.compress.Readers.bzip2InputStreamReader;
import static org.dishevelled.compress.Readers.gzipInputStreamReader;
import static org.dishevelled.compress.Readers.xzInputStreamReader;
import static org.dishevelled.compress.Readers.zstdInputStreamReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HDFS readers with support for bgzf, gzip, bzip2, xz, and zstd compression.
 *
 * @since 1.1
 * @author  Michael Heuer
 */
public final class HdfsReaders {

    /**
     * Private no-arg constructor.
     */
    private HdfsReaders() {
        // empty
    }


    /**
     * Create and return a new buffered reader with support for bgzf, gzip, or bzip2 compression
     * for the specified configuration and path.
     *
     * @param path path, must not be null
     * @param conf configuration, must not be null
     * @return a new buffered reader with support for bgzf, gzip, or bzip2 compression
     *    for the specified configuration and path
     * @throws IOException if an I/O error occurs
     */
    public static BufferedReader reader(final String path, final Configuration conf) throws IOException {
        checkNotNull(path);
        checkNotNull(conf);

        Path p = new Path(path);
        FileSystem fileSystem = p.getFileSystem(conf);
        InputStream inputStream = fileSystem.open(p);

        // check bgzf before gzip
        if (isBgzfFilename(path)) {
            return bgzfInputStreamReader(inputStream);
        }
        else if (isGzipFilename(path)) {
            return gzipInputStreamReader(inputStream);
        }
        else if (isBzip2Filename(path)) {
            return bzip2InputStreamReader(inputStream);
        }
        else if (isXzFilename(path)) {
            return xzInputStreamReader(inputStream);
        }
        else if (isZstdFilename(path)) {
            return zstdInputStreamReader(inputStream);
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
