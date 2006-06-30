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

package org.netbeans.modules.vcs.profiles.commands;

import java.util.Hashtable;
import java.util.regex.Pattern;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

/**
 * A simple echo command, which prints it's arguments to standard output
 *
 * @author  Martin Entlicher
 */
public class Echo implements VcsAdditionalCommand {
    
    /** Creates a new instance of Echo */
    public Echo() {
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener,
                        CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            out.append(args[i]);
        }
        stdoutListener.outputLine(out.toString());
        if (dataRegex == null) {
            dataRegex = ExecuteCommand.DEFAULT_REGEX;
        }
        String[] sa = ExternalCommand.matchToStringArray(Pattern.compile(dataRegex), out.toString());
        if (sa != null && sa.length > 0) stdoutDataListener.outputData(sa);
        return true;
    }
    
}
