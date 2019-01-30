/*

    dsh-bio-compress-hdfs  HDFS support for dsh-compress.
    Copyright (c) 2013-2019 held jointly by the individual authors.

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
import static org.dishevelled.compress.Writers.bgzfOutputStreamWriter;
import static org.dishevelled.compress.Writers.bzip2OutputStreamWriter;
import static org.dishevelled.compress.Writers.gzipOutputStreamWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HDFS writers with support for bgzf, gzip, and bzip2 compression.
 *
 * @author  Michael Heuer
 */
public final class HdfsWriters {

    /**
     * Private no-arg constructor.
     */
    private HdfsWriters() {
        // empty
    }


    /**
     * Create and return a new buffered print writer with support for bgzf, gzip, or bzip2 compression
     * for the specified path and configuration.
     *
     * @param path path, must not be null
     * @param conf configuration, must not be null
     * @return a new buffered print writer with support for bgzf, gzip, or bzip2 compression
     *    for the specified configuration and path
     * @throws IOException if an I/O error occurs
     */
    public static PrintWriter writer(final String path, final Configuration conf) throws IOException {
        checkNotNull(path);
        checkNotNull(conf);

        Path p = new Path(path);
        FileSystem fileSystem = p.getFileSystem(conf);
        OutputStream outputStream = fileSystem.create(p);

        if (isBgzfFilename(path)) {
            return bgzfOutputStreamWriter(outputStream);
        }
        else if (isGzipFilename(path)) {
            return gzipOutputStreamWriter(outputStream);
        }
        else if (isBzip2Filename(path)) {
            return bzip2OutputStreamWriter(outputStream);
        }
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);
    }
}
