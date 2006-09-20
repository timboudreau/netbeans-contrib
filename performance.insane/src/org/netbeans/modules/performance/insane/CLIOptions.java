/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.performance.insane;

import java.util.Map;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.util.Lookup;
import java.io.File;

/**
 * Accepts requests to dump heap.
 */
public class CLIOptions extends OptionProcessor {
    private Option dump;
    
    public CLIOptions () {
        String bundle = "org.netbeans.modules.performance.insane.Bundle"; // NOI18N
        dump = Option.displayName(
            Option.shortDescription(
                Option.requiredArgument(Option.NO_SHORT_NAME, "dumpheap"), // NOI18N
                bundle,
                "MSG_DumpHeapDescr" // NOI18N
            ),
            bundle,
            "MSG_DumpHeapName" // NOI18N
        );
    }

    protected Set<Option> getOptions() {
        return java.util.Collections.singleton(dump);
    }

    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        String[] argv = optionValues.get(dump);
        String s = argv[0];
        
        File curDir = env.getCurrentDirectory ();
        File f = new File(s);
        if (!f.isAbsolute()) {
            f = new File(curDir, s);
        }
        try {
            DumpAction.dump(f);
        } catch (Exception e) {
            CommandException ex = new CommandException(2, e.getLocalizedMessage());
            ex.initCause(e);
            throw ex;
        }
    }
}
