/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.commands;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.actions.AddCommandAction;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.actions.UpdateCommandAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.grouping.GroupUtils;
import org.netbeans.modules.vcscore.ui.*;
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
