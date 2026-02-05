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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.dishevelled.bio.alignment.gaf.GafRecord;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Compress alignments in GAF format to splittable bgzf or bzip2 compression
 * codecs.
 *
 * @since 1.4
 * @author  Michael Heuer
 */
public final class CompressGaf implements Callable<Integer> {
    private final Path inputGafPath;
    private final File outputGafFile;
    private static final String USAGE = "dsh-compress-gaf [args]";

    /**
     * Compress alignments in GAF format to splittable bgzf or bzip2 compression
     * codecs.
     *
     * @deprecated will be removed in version 3.0
     * @param inputGafFile input GAF file, if any
     * @param outputGafFile output GAF file, if any
     */
    public CompressGaf(final File inputGafFile, final File outputGafFile) {
        this(inputGafFile == null ? null : inputGafFile.toPath(), outputGafFile);
    }

    /**
     * Compress alignments in GAF format to splittable bgzf or bzip2 compression
     * codecs.
     *
     * @since 2.1
     * @param inputGafPath input GAF path, if any
     * @param outputGafFile output GAF file, if any
     */
    public CompressGaf(final Path inputGafPath, final File outputGafFile) {
        this.inputGafPath = inputGafPath;
        this.outputGafFile = outputGafFile;
    }

    @Override
    public Integer call() throws Exception {
        int lineNumber = 0;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGafPath);
            writer = writer(outputGafFile);

            while (reader.ready()) {
                String line = reader.readLine();
                GafRecord record = GafRecord.valueOf(line);
                lineNumber++;
                writer.println(record.toString());
            }
            return 0;
        }
        catch (Exception e) {
            throw new Exception("could not read record at line number "
                                + lineNumber + ", caught" + e.getMessage(), e);
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument inputGafPath = new PathArgument("i", "input-gaf-path", "input GAF path, default stdin", false);
        FileArgument outputGafFile = new FileArgument("o", "output-gaf-file", "output GAF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGafPath, outputGafFile);
        CommandLine commandLine = new CommandLine(args);

        CompressGaf compressGaf = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            compressGaf = new CompressGaf(inputGafPath.getValue(), outputGafFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressGaf.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
