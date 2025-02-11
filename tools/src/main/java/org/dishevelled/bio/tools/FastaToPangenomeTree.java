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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import static org.dishevelled.bio.assembly.pangenome.PangenomeReader.readFasta;
import static org.dishevelled.bio.assembly.pangenome.PangenomeWriter.writeTree;
import static org.dishevelled.bio.assembly.pangenome.PangenomeWriter.writeSortedTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.nio.file.Path;

import java.util.concurrent.Callable;

import org.dishevelled.bio.assembly.pangenome.Pangenome;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.PathArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Convert DNA sequences in FASTA format to pangenome samples, haplotypes, and
 * scaffolds in ASCII tree format.
 *
 * @since 3.0
 * @author  Michael Heuer
 */
public final class FastaToPangenomeTree implements Callable<Integer> {
    private final Path fastaPath;
    private final File pangenomeFile;
    private final boolean sort;
    private static final String USAGE = "dsh-fasta-to-pangenome-tree [args]";


    /**
     * Convert DNA sequences in FASTA format to pangenome samples, haplotypes, and
     * scaffolds in ASCII tree format.
     *
     * @param fastaPath input FASTA path, if any
     * @param pangenomeFile output pangenome tree file, if any
     * @param sort true to sort pangenome samples, haplotypes, and scaffolds before writing
     */
    public FastaToPangenomeTree(final Path fastaPath, final File pangenomeFile, final boolean sort) {
        this.fastaPath = fastaPath;
        this.pangenomeFile = pangenomeFile;
        this.sort = sort;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(fastaPath);
            writer = writer(pangenomeFile);

            Pangenome pangenome = readFasta(reader);
            if (sort) {
                writeSortedTree(pangenome, writer);
            }
            else {
                writeTree(pangenome, writer);
            }

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
        PathArgument fastaPath = new PathArgument("i", "input-fasta-path", "input FASTA path, default stdin", false);
        FileArgument pangenomeFile = new FileArgument("o", "output-pangenome-file", "output pangenome tree file, default stdout", false);
        Switch sort = new Switch("s", "sort", "sort pangenome samples, haplotypes, and scaffolds before writing");

        ArgumentList arguments = new ArgumentList(about, help, fastaPath, pangenomeFile, sort);
        CommandLine commandLine = new CommandLine(args);

        FastaToPangenomeTree fastaToPangenomeTree = null;
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
            fastaToPangenomeTree = new FastaToPangenomeTree(fastaPath.getValue(), pangenomeFile.getValue(), sort.wasFound());
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(fastaToPangenomeTree.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
