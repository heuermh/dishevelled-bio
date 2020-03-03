/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2020 held jointly by the individual authors.

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

import org.dishevelled.bio.assembly.gfa1.Gfa1Listener;
import org.dishevelled.bio.assembly.gfa1.Gfa1Reader;
import org.dishevelled.bio.assembly.gfa1.Gfa1Record;
import org.dishevelled.bio.assembly.gfa1.Gfa1Writer;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Compress assembly in GFA 1.0 format to splittable bgzf or bzip2 compression codecs.
 *
 * @since 1.3
 * @author  Michael Heuer
 */
public final class CompressGfa1 implements Callable<Integer> {
    private final File inputGfa1File;
    private final File outputGfa1File;
    private static final String USAGE = "dsh-compress-gfa1 [args]";

    /**
     * Compress assembly in GFA 1.0 format to splittable bgzf or bzip2 compression codecs.
     *
     * @param inputGfa1File input GFA 1.0 file, if any
     * @param outputGfa1File output GFA 1.0 file, if any
     */
    public CompressGfa1(final File inputGfa1File, final File outputGfa1File) {
        this.inputGfa1File = inputGfa1File;
        this.outputGfa1File = outputGfa1File;
    }

    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputGfa1File);
            writer = writer(outputGfa1File);
            
            final PrintWriter w = writer;
            Gfa1Reader.stream(reader, new Gfa1Listener() {
                    @Override
                    public boolean record(final Gfa1Record gfa1Record) {
                        Gfa1Writer.write(gfa1Record, w);
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
        FileArgument inputGfa1File = new FileArgument("i", "input-gfa1-file", "input GFA 1.0 file, default stdin", false);
        FileArgument outputGfa1File = new FileArgument("o", "output-gfa1-file", "output GFA 1.0 file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputGfa1File, outputGfa1File);
        CommandLine commandLine = new CommandLine(args);

        CompressGfa1 compressGfa1 = null;
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
            compressGfa1 = new CompressGfa1(inputGfa1File.getValue(), outputGfa1File.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(compressGfa1.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
