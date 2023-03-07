/*

    dsh-bio-tools  Command line tools.
    Copyright (c) 2013-2023 held jointly by the individual authors.

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

import java.util.concurrent.Callable;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Abstract filter callable.
 *
 * @author  Michael Heuer
 */
abstract class AbstractFilter implements Callable<Integer> {

    /**
     * Create and return a new script engine.
     *
     * @return a new script engine
     */
    protected static ScriptEngine createScriptEngine() {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.nashorn-compat", true);
        //bindings.put("polyglot.js.allowAllAccess", true);
        //bindings.put("polyglot.js.allowHostAccess", true);
        //bindings.put("polyglot.js.allowNativeAccess", true);
        //bindings.put("polyglot.js.allowHostClassLookup", true);
        //bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
        //bindings.put("polyglot.js.allowHostClassLoading", true);
        //bindings.put("js.nashorn-compat", true);
        return engine;
    }
}
