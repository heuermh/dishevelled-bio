/*

    dsh-bio-tools  Command line tools.
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
package org.dishevelled.bio.tools;

import static org.dishevelled.bio.protein.uniprot.UniprotEntrySequenceReader.stream;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;

import org.dishevelled.bio.protein.uniprot.EntrySequence;
import org.dishevelled.bio.protein.uniprot.EntrySequenceListener;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;

/**
 * Extract protein sequences from UniProt entries in XML format.
 *
 * @since 2.5
 * @author  Michael Heuer
 */
public final class ExtractUniprotSequences implements Callable<Integer> {
    private final Path uniprotXmlPath;
    private final File sequenceFile;
    private static final String USAGE = "dsh-extract-uniprot-sequences [args]";


    /**
     * Extract protein sequences from UniProt entries in XML format.
     *
     * @param uniprotXmlPath UniProt XML path, if any
     * @param sequenceFile sequence file, if any
     */
    public ExtractUniprotSequences(final Path uniprotXmlPath, final File sequenceFile) {
        this.uniprotXmlPath = uniprotXmlPath;
        this.sequenceFile = sequenceFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(uniprotXmlPath);
            writer = writer(sequenceFile);

            final PrintWriter w = writer;
            final Joiner joiner = Joiner.on("\t");
            EntrySequenceListener callback = new EntrySequenceListener() {
                    @Override
                    public boolean entrySequence(final EntrySequence s) {
                        w.println(joiner.join(s.getAccession(),
                                              s.getLength(),
                                              s.getMass(),
                                              s.getChecksum(),
                                              s.getModified(),
                                              s.getVersion(),
                                              s.isPrecursor(),
                                              (s.getFragment() == null) ? "" : s.getFragment(),
                                              s.getSequence()));
                        return true;
                    }
                };

            stream(reader, callback);
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

        // install a signal handler to exit on SIGPIPE
        sun.misc.Signal.handle(new sun.misc.Signal("PIPE"), new sun.misc.SignalHandler() {
                @Override
                public void handle(final sun.misc.Signal signal) {
                    System.exit(0);
                }
            });

        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        PathArgument uniprotXmlPath = new PathArgument("i", "input-uniprot-xml-path", "input UniProt XML path, default stdin", false);
        FileArgument sequenceFile = new FileArgument("o", "output-sequence-file", "output sequence file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, uniprotXmlPath, sequenceFile);
        CommandLine commandLine = new CommandLine(args);

        ExtractUniprotSequences extractUniprotSequences = null;
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
            extractUniprotSequences = new ExtractUniprotSequences(uniprotXmlPath.getValue(), sequenceFile.getValue());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractUniprotSequences.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
