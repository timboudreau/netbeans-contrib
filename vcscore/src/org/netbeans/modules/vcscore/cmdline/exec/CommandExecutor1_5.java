/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline.exec;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * The command executor that uses ProcessBuilder.
 * Can be used only on JDK 1.5 and higher.
 *
 * @author Martin Entlicher
 */
public class CommandExecutor1_5 extends CommandExecutor {
    
    /** Creates a new instance of CommandExecutor1_5 */
    public CommandExecutor1_5() {
    }
    
    public Process createProcess(String[] cmdArr, String[] envp, File work,
                                 boolean mergeStreams) throws IOException {
        
        ProcessBuilder pb = new ProcessBuilder(java.util.Arrays.asList(cmdArr));
        if (envp != null) {
            Map env = pb.environment();
            env.clear();
            for (int i = 0; i < envp.length; i++) {
                int index = envp[i].indexOf('=');
                if (index > 0) {
                    String name = envp[i].substring(0, index);
                    String value;
                    index++;
                    if (index < envp[i].length()) {
                        value = envp[i].substring(index);
                    } else {
                        value = ""; // NOI18N
                    }
                    env.put(name, value);
                }
            }
        }
        pb.directory(work);
        pb.redirectErrorStream(mergeStreams);
        return pb.start();
    }
    
}
