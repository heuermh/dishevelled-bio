/*

    dsh-bio-tools  Command line tools.
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
package org.dishevelled.bio.tools;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import org.dishevelled.bio.assembly.gfa2.Gfa2Listener;
import org.dishevelled.bio.assembly.gfa2.Gfa2Reader;
import org.dishevelled.bio.assembly.gfa2.Gfa2Record;
import org.dishevelled.bio.assembly.gfa2.Gfa2Writer;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Compress features in GFA 2.0 format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 1.3
 * @author  Michael Heuer
 */
public final class CompressGfa2 implements Callable<Integer> {
    private final File inputGfa2File;
    private final File outputGfa2File;
    private static final String USAGE = "dsh-compress-gfa2 [args]";

    /**
     * Compress features in GFA 2.0 format to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputGfa1File input GFA 2.0 file, if any
     * @param outputGfa1File output GFA 2.0 file, if any
     */
    public CompressGfa2(final File inputGfa2File, final File outputGfa2File) {
        this.inputGfa2File = inputGfa2File;
        this.outputGfa2File = outputGfa2File;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGfa2File);
            writer = writer(outputGfa2File);
            final PrintWriter w = writer;
            Gfa2Reader.stream(reader, new Gfa2Listener() {
                    @Override
                    public boolean record(final Gfa2Record gfa2Record) {
                        Gfa2Writer.write(gfa2Record, w);
                        return true;
                    }
                });

            return 0;
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
        FileArgument inputGfa2File = new FileArgument("i", "input-gfa2-file", "input GFA 2.0 file, default stdin", false);
        FileArgument outputGfa2File = new FileArgument("o", "output-gfa2-file", "output GFA 2.0 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa2File, outputGfa2File);
        CommandLine commandLine = new CommandLine(args);

        CompressGfa2 compressGfa2 = null;
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
            compressGfa2 = new CompressGfa2(inputGfa2File.getValue(), outputGfa2File.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressGfa2.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
