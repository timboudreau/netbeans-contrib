/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;

import org.netbeans.api.queries.SharabilityQuery;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.actions.AddToGroupAction;
import org.netbeans.modules.vcscore.actions.RefreshLocalFolderAction;
//import org.netbeans.modules.vcscore.commands.CommandExecutorSupport;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;

//import org.netbeans.modules.vcscore.cmdline.CommandLineVcsDirReader;
//import org.netbeans.modules.vcscore.cmdline.CommandLineVcsDirReaderRecursive;
//import org.netbeans.modules.vcscore.cmdline.AdditionalCommandDialog;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.turbo.Turbo;

/**
 * This class provides a default implementation of VcsFactory.
 *
 * @author  Martin Entlicher
 */
public class DefaultVcsFactory extends Object implements VcsFactory {

    //private static Object fsActionAccessLock = new Object();
    //private static VcsAction fsAction = null;

    protected WeakReference fileSystem;
    
    /** Creates new DefaultVcsFactory */
    public DefaultVcsFactory(VcsFileSystem fileSystem) {
        this.fileSystem = new WeakReference(fileSystem);
    }

    /**
     * Get the provider of the VCS status information. The default provider
     * is the filesystem cache if implements <code>FileStatusProvider</code> or null otherwise.
     */
    public FileStatusProvider getFileStatusProvider() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the array of VCS actions for a collection of <code>FileObject</code>s.
     * If the collection is null, it gets the <code>FileObject</code>s from
     * currently selected nodes.
     * @param fos the collection of <code>FileObject</code>s or null.
     * @return the array of instances of <code>VcsAction</code>s, one for each
     *         child of the root command node.
     */
    public SystemAction[] getActions(Collection fos) {
        ArrayList actions = new ArrayList();
        //return new SystemAction[] { getVcsAction() };
        //ArrayList actions = new ArrayList();
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        /*
        Node commands = fileSystem.getCommands();
        Node[] commandRoots = commands.getChildren().getNodes();
        //System.out.println("DefaultVcsFactory.getActions(): commandRoots.length = "+commandRoots.length);
        //ArrayList commandsSubTrees = new ArrayList();
        int[] commandsSubTrees = new int[commandRoots.length];
        int numOfSubTrees = 0;
        for (int i = 0; i < commandRoots.length; i++) {
            VcsCommand cmd = (VcsCommand) commandRoots[i].getCookie(VcsCommand.class);
            //System.out.println("commandRoots["+i+"] = "+cmd);
            if (cmd != null &&
                VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS) == 0) {
                    //actions.add(new VcsAction(fileSystem, fos, i));
                    commandsSubTrees[numOfSubTrees++] = i;
                    //commandsSubTrees.add(new Integer(i));
            }
        }
        if (numOfSubTrees == 0) return new SystemAction[0];
        fsAction = (VcsAction) SystemAction.get(VcsAction.class);
        synchronized (fsActionAccessLock) {
            fsAction.setFileSystem(fileSystem);
            fsAction.setSelectedFileObjects(fos);
            Node[] commandNodesSubTrees = new Node[numOfSubTrees];
            for (int i = 0; i < numOfSubTrees; i++) {
                commandNodesSubTrees[i] = commandRoots[commandsSubTrees[i]];
            }
            fsAction.setCommandsSubTrees(commandNodesSubTrees);
        }
        actions.add(fsAction);
         */
        boolean sharable = true;
        if (fileSystem != null && !fileSystem.isProcessUnimportantFiles() && fos != null) {
            sharable = fos.size() == 0; // false if there are some files
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                File file = FileUtil.toFile(fo);
                int sharability = SharabilityQuery.getSharability(file);
                if (sharability != SharabilityQuery.NOT_SHARABLE) {
                    sharable = true;
                    break;
                }
            }
        }
        actions.add(SystemAction.get(RefreshLocalFolderAction.class));
        if (sharable) {
            actions.add(SystemAction.get(VcsFSCommandsAction.class));
            if (fileSystem.getVersioningFileSystem() != null) {
                actions.add(SystemAction.get(VersioningExplorerAction.class));
            }
            actions.add(SystemAction.get(AddToGroupAction.class));
        }
        //System.out.println("action[0] = "+actions.get(0)+", action[1] = "+actions.get(1)+", equals = "+actions.get(0).equals(actions.get(1)));
        //return (SystemAction[]) actions.toArray(new SystemAction[actions.size()]);
        return (SystemAction[]) actions.toArray(new SystemAction[0]);
    }
    
    /**
     * Get the command executor for the command.
     * @param command the command to get the executor for
     * @param variables the <code>Hashtable</code> of (variable name, variable value) pairs
     * @return the command executor or null when no executor is found. If command is instance of {@link UserCommand}, an instance of {@link ExecuteCommand} is returned.
     * @deprecated This method is retained for compatibility only. XXX how to replace usages? It's called from 31 places.
     */
    public VcsCommandExecutor getCommandExecutor(VcsCommand command, Hashtable variables) {
        if (command instanceof UserCommand) {
            //if (VcsCommandIO.getBooleanProperty(command, UserCommand.PROPERTY_DISPLAY)) {
            //    AdditionalCommandDialog addCommand = new org.netbeans.modules.vcscore.cmdline.AdditionalCommandDialog(fileSystem, (UserCommand) command, variables, new javax.swing.JFrame(), false);
            //    return addCommand.createCommand();
            //} else {
            VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
            if (fileSystem == null) return null;
            UserCommand ucmd = (UserCommand) command;
            CommandSupport cmdSupp = fileSystem.getCommandSupport(command.getName());
            if (cmdSupp == null) {
                cmdSupp = new UserCommandSupport(ucmd, fileSystem);
            }
            Command cmd = cmdSupp.createCommand();
            if (cmd instanceof VcsDescribedCommand) ((VcsDescribedCommand) cmd).setAdditionalVariables(variables);
            //if (!VcsManager.showCustomizer(cmd)) return null;
            ExecuteCommand executor = new ExecuteCommand(fileSystem, (UserCommand) command, variables);
            if (cmd instanceof VcsDescribedCommand) {
                executor.setDescribedCommand((VcsDescribedCommand) cmd);
                ((VcsDescribedCommand) cmd).setExecutor(executor);
            }
            return executor;
            //}
        } else {
            return null;
        }
    }
    
}
