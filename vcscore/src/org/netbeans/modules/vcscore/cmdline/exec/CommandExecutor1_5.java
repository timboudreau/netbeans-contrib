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
