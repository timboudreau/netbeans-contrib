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

package org.netbeans.modules.vcscore.cmdline.exec;

import java.io.File;
import java.io.IOException;
import org.openide.ErrorManager;

/**
 * The command executor.
 *
 * @author Martin Entlicher
 */
public class CommandExecutor {

    private static CommandExecutor executor;

    /** Creates a new instance of CommandExecutor */
    protected CommandExecutor() {
    }

    /**
     * Get the default instance of command executor.
     */
    public static synchronized CommandExecutor getDefault() {
        if (executor == null) {
            String version = System.getProperty("java.version"); // NOI18N
            if (version.startsWith("1.4")) {                     // NOI18N
                executor = new CommandExecutor();
            } else {
                try {
                    Class execClass = Class.forName("org.netbeans.modules.vcscore.cmdline.exec.CommandExecutor1_5"); // NOI18N
                    executor = (CommandExecutor) execClass.newInstance();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                    executor = new CommandExecutor();
                }
            }
        }
        return executor;
    }
    
    /**
     * Create the process that is executing the command.
     * @param cmdArr array containing the command to call and it's arguments
     * @param envp environment - array of strings of the format <code>name=value</code>.
     * @param work the working directory
     * @param mergeStreams whether to merge error output with the standard output.
     *                     This should not be set when running on JDK 1.4.x (a warning is provided).
     * @return The process.
     */
    public Process createProcess(String[] cmdArr, String[] envp, File work,
                                 boolean mergeStreams) throws IOException {
        if (mergeStreams && Boolean.getBoolean("netbeans.vcsdebug")) { // NOI18N
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ErrorManager.getDefault().annotate(
                    new IllegalArgumentException(java.util.Arrays.asList(cmdArr).toString()),
                    "Warning: can not merge standard and error streams on JDK 1.4.x"));
        }
        return Runtime.getRuntime().exec(cmdArr, envp, work);
    }
    
}
