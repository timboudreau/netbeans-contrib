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

package org.netbeans.modules.vcs.advanced.globalcommands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.netbeans.modules.vcscore.commands.CommandProcessListener;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.CommandTaskInfo;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.netbeans.modules.vcscore.runtime.RuntimeCommandTask;
import org.netbeans.modules.vcscore.runtime.RuntimeCommandsProvider;
import org.netbeans.modules.vcscore.runtime.RuntimeFolderNode;

/**
 * The provider of VCS commands for the representation on the Runtime Tab.
 *
 * @author  Martin Entlicher
 */
public class GlobalRuntimeCommandsProvider extends RuntimeCommandsProvider {
    
    private RuntimeCommandsListener rcl;
    private int numOfCommandsToKeep = RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    //private List runningCommands = new ArrayList();//Collections.synchronizedList(new ArrayList());
    private List finishedCommands = new ArrayList();//Collections.synchronizedList(new ArrayList());
    private List commands = new ArrayList();
    private Map runtimeCommandsForExecutors = new HashMap();//new Hashtable();
    private Map finishedExecutorsForCommands = new HashMap();
    private CommandProcessor processor = CommandProcessor.getInstance();
    private boolean registered = false;
    
    public GlobalRuntimeCommandsProvider() {
        rcl = new RuntimeCommandsListener();
        processor.addCommandProcessListener(rcl);
        //processor.removeFinishedCommandsUponRequest(true, fs);
        GlobalCommandsProvider provider = GlobalCommandsProvider.getInstance();
        provider.addPropertyChangeListener(WeakListeners.propertyChange(rcl, provider));
        numOfCommandsToKeep = provider.getNumberOfFinishedCmdsToCollect();
    }
    
    protected Node createNodeDelegate() {
        RuntimeFolderNode globalRuntime = new RuntimeFolderNode(new RuntimeFolderChildren(this));
        globalRuntime.setName("Global Commands");
        globalRuntime.setDisplayName(NbBundle.getMessage(GlobalRuntimeCommandsProvider.class, "LBL_GlobalRuntime"));
        globalRuntime.setNumOfFinishedCmdsToCollect(numOfCommandsToKeep);
        /*
        java.beans.BeanDescriptor bd = getFsBeanDescriptor(fs);
        if (bd != null) {
            String str = (String)bd.getValue(org.netbeans.modules.vcscore.VcsFileSystem.VCS_FILESYSTEM_ICON_BASE);
            if (str != null) {
                fsRuntime.setIconBase(str);
            }
        }
         */
        globalRuntime.addPropertyChangeListener(WeakListeners.propertyChange(rcl, globalRuntime));
        //attachListeners(fsRuntime);
        return globalRuntime;
    }
    
    /*
    private static java.beans.BeanDescriptor getFsBeanDescriptor(VcsFileSystem fs) {
        java.beans.BeanInfo info;
        try {
            info = org.openide.util.Utilities.getBeanInfo(fs.getClass());
        } catch (java.beans.IntrospectionException intrExc) {
            return null;
        }
        if (info != null) {
            return info.getBeanDescriptor();
        }
        return null;
    }
     */
    
    public RuntimeCommand[] children() {
        return (RuntimeCommand[]) commands.toArray(new RuntimeCommand[commands.size()]);
    }
    
    public boolean cutCommandsToMaxToKeep() {
        boolean cutted = false;
        //ArrayList finishedToRemove = new ArrayList();
        synchronized (this) {
            int current = commands.size();
            while (current > numOfCommandsToKeep && finishedCommands.size() > 0) {
                RuntimeCommand cmd = (RuntimeCommand) finishedCommands.remove(0);
                commands.remove(cmd);
                CommandTaskInfo vce = (CommandTaskInfo) finishedExecutorsForCommands.remove(cmd);
                //finishedToRemove.add(vce);
                current--;
                cutted = true;
            }
        }
        /*
        for (Iterator it = finishedToRemove.iterator(); it.hasNext(); ) {
            VcsCommandExecutor vce = (VcsCommandExecutor) it.next();
            processor.removeFinishedCommand(vce);
        }
         */
        return cutted;
    }
    
    protected void notifyRemoved() {
        processor.removeCommandProcessListener(rcl);
        //processor.removeFinishedCommandsUponRequest(false, fs);
    }
    
    private class RuntimeCommandsListener extends Object implements CommandProcessListener, PropertyChangeListener {
        
        /**
         * Get the commands provider. The listener gets events only from commands,
         * that are instances of ProvidedCommand and their provider equals to this
         * provider.
         */
        public VcsCommandsProvider getProvider() {
            return GlobalCommandsProvider.getInstance();
        }
        
        /**
         * Called when the command is just to be preprocessed.
         */
        public void commandPreprocessing(Command command) {
            /* Do not create runtime commands for command customization by default.
            VcsRuntimeCommand cmd = new VcsRuntimeCommand(command);
            cmd.setState(RuntimeCommand.STATE_WAITING);
            synchronized (VcsRuntimeCommandsProvider.this) {
                commands.add(cmd);
                runtimeCommandsForExecutors.put(command, cmd);
                cutCommandsToMaxToKeep();
            }
            firePropertyChange(PROP_CHILDREN, null, null);
             */
        }
        
        /**
         * Called when the preprocessing of the command finished.
         * @param cmd The command which was preprocessed.
         * @param status The status of preprocessing. If false, the command is not executed.
         */
        public void commandPreprocessed(Command command, boolean status) {
            /*
            if (status == false) {
                VcsRuntimeCommand cmd = (VcsRuntimeCommand) runtimeCommandsForExecutors.get(command);
                synchronized (VcsRuntimeCommandsProvider.this) {
                    commands.remove(cmd);
                    runtimeCommandsForExecutors.remove(command);
                }
                firePropertyChange(PROP_CHILDREN, null, null);
            }
             */
        }
        
        /**
         * This method is called when the command is just to be started.
         */
        public void commandStarting(CommandTaskInfo info) {
            if (!registered) {
                register();
                registered = true;
            }
            RuntimeCommand cmd = (RuntimeCommand) runtimeCommandsForExecutors.get(info);
            if (cmd == null) {
                CommandTask task = info.getTask();
                if (!(task instanceof RuntimeCommandTask)) return ;
                cmd = ((RuntimeCommandTask) task).getRuntimeCommand(info);
                if (cmd == null) return ;
                //cmd = new VcsRuntimeCommand(info);
                synchronized (GlobalRuntimeCommandsProvider.this) {
                    commands.add(cmd);
                    runtimeCommandsForExecutors.put(info, cmd);
                    cutCommandsToMaxToKeep();
                }
                firePropertyChange(PROP_CHILDREN, null, null);
            }
            cmd.setState(RuntimeCommand.STATE_RUNNING);
        }
        
        /**
         * This method is called when the command is done.
         */
        public void commandDone(CommandTaskInfo info) {
            RuntimeCommand cmd = (RuntimeCommand) runtimeCommandsForExecutors.get(info);
            if (cmd != null) {
                synchronized (GlobalRuntimeCommandsProvider.this) {
                    //commands.remove(cmd);
                    runtimeCommandsForExecutors.remove(info);
                    finishedExecutorsForCommands.put(cmd, info);
                    finishedCommands.add(cmd);
                    cutCommandsToMaxToKeep();
                }
                cmd.setState(RuntimeCommand.STATE_DONE);
                firePropertyChange(PROP_CHILDREN, null, null);
            }
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            Object source = propertyChangeEvent.getSource();
            String propertyName = propertyChangeEvent.getPropertyName();
            if (source == GlobalCommandsProvider.getInstance()) { // The change is comming from the global provider
                if (RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT.equals(propertyName)) {
                    int newNumOfCommandsToKeep = GlobalCommandsProvider.getInstance().getNumberOfFinishedCmdsToCollect();
                    //System.out.println("newNumOfCommandsToKeep = "+newNumOfCommandsToKeep+", numOfCommandsToKeep = "+numOfCommandsToKeep);
                    if (numOfCommandsToKeep == newNumOfCommandsToKeep) return ;
                    numOfCommandsToKeep = newNumOfCommandsToKeep;
                    RuntimeFolderNode node = (RuntimeFolderNode) getExistingNodeDelegate();
                    if (node != null) {
                        node.setNumOfFinishedCmdsToCollect(numOfCommandsToKeep);
                    }
                    if (cutCommandsToMaxToKeep()) firePropertyChange(PROP_CHILDREN, null, null);
                }/* else if (VcsFileSystem.PROP_DISPLAY_NAME.equals(propertyName)) {
                    RuntimeFolderNode node = (RuntimeFolderNode) getExistingNodeDelegate();
                    if (node != null) {
                        node.setDisplayName(fs.getDisplayName());
                    }
                }*/
            } else if (source instanceof RuntimeFolderNode) { // The change is comming from the Node
                RuntimeFolderNode node = (RuntimeFolderNode) source;
                if (RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT.equals(propertyName)) {
                    int newNumOfCommandsToKeep = node.getNumOfFinishedCmdsToCollect();
                    //System.out.println("newNumOfCommandsToKeep = "+newNumOfCommandsToKeep+", numOfCommandsToKeep = "+numOfCommandsToKeep);
                    if (numOfCommandsToKeep == newNumOfCommandsToKeep) return ;
                    numOfCommandsToKeep = newNumOfCommandsToKeep;
                    GlobalCommandsProvider.getInstance().setNumberOfFinishedCmdsToCollect(numOfCommandsToKeep);
                    if (cutCommandsToMaxToKeep()) firePropertyChange(PROP_CHILDREN, null, null);
                }
            }
        }
        
    }
}
