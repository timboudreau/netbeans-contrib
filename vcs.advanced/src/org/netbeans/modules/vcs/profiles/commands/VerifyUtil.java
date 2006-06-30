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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;

import org.openide.filesystems.FileObject;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.Table;

/**
 * The utilities for verification of files in a group.
 *
 * @author  Martin Entlicher
 */
public class VerifyUtil extends java.lang.Object {
    
    private VerifyUtil() {}
    
    public static List getFOs(VcsFileSystem fileSystem, Hashtable vars) {
        Collection files = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        ArrayList fos = new ArrayList(files.size());
        for (Iterator fileIt = files.iterator(); fileIt.hasNext(); ) {
            String file = (String) fileIt.next();
            FileObject fo = fileSystem.findFileObject(file);
            if (fo != null) {
                fos.add(fo);
            }
        }
        return fos;
    }

    public static void refreshFilesState(String cmdName, VcsFileSystem fileSystem, Hashtable vars) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES)) {
            VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            fileSystem.getCommandsPool().preprocessCommand(vce, new Hashtable(vars), fileSystem);
            fileSystem.getCommandsPool().startExecutor(vce);
            try {
                fileSystem.getCommandsPool().waitToFinish(vce);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(vce);
                throw iexc;
            }
        } else {
            List fos = getFOs(fileSystem, vars);
            Table files = new Table();
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                files.put(fo.getPath(), fo);
            }
            VcsCommandExecutor[] execs = VcsAction.doCommand(files, cmd, null, fileSystem, null, null, null, null, true);
            CommandsPool cpool = fileSystem.getCommandsPool();
            for (int i = 0; i < execs.length; i++) {
                try {
                    cpool.waitToFinish(execs[i]);
                } catch (InterruptedException iexc) {
                    for (int j = i; j < execs.length; j++) {
                        cpool.kill(execs[j]);
                    }
                    throw iexc;
                }
            }
        }
    }

}
