/*

    dsh-bio-tools2  Command line tools.
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
package org.dishevelled.bio.tools2;

import java.util.List;

import picocli.AutoComplete.GenerateCompletion;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ScopeType;

/**
 * Command line tools.
 *
 * @author  Michael Heuer
 */
@Command(
  name = "dsh-bio",
  scope = ScopeType.INHERIT,
  subcommands = { CompressBed.class, HelpCommand.class, GenerateCompletion.class },
  mixinStandardHelpOptions = true,
  sortOptions = false,
  usageHelpAutoWidth = true,
  resourceBundle = "org.dishevelled.bio.tools2.Messages",
  versionProvider = org.dishevelled.bio.tools2.About.class
)
public final class Tools {

    @Parameters(hidden = true)
    private List<String> ignored;

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        CommandLine commandLine = new CommandLine(new Tools());
        commandLine.setUsageHelpLongOptionsMaxWidth(42);
        System.exit(commandLine.execute(args));
    }
}
