/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.runtime;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openide.nodes.Node;
import org.openide.nodes.Index;
import org.openide.util.WeakListener;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandProcessListener;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.CommandTaskInfo;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

/**
 * The provider of VCS commands for the representation on the Runtime Tab.
 *
 * @author  Martin Entlicher
 */
public class VcsRuntimeCommandsProvider extends RuntimeCommandsProvider {
    
    private VcsFileSystem fs;
    private RuntimeCommandsListener rcl;
    private int numOfCommandsToKeep = RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    //private List runningCommands = new ArrayList();//Collections.synchronizedList(new ArrayList());
    private List finishedCommands = new ArrayList();//Collections.synchronizedList(new ArrayList());
    private List commands = new ArrayList();
    private Map runtimeCommandsForExecutors = new HashMap();//new Hashtable();
    private Map finishedExecutorsForCommands = new HashMap();
    private CommandProcessor processor = CommandProcessor.getInstance();
    
    public VcsRuntimeCommandsProvider(VcsFileSystem fs) {
        this.fs = fs;
        rcl = new RuntimeCommandsListener();
        processor.addCommandProcessListener(rcl);
        //processor.removeFinishedCommandsUponRequest(true, fs);
        fs.addPropertyChangeListener(WeakListener.propertyChange(rcl, fs));
        numOfCommandsToKeep = fs.getNumberOfFinishedCmdsToCollect();
    }
    
    protected Node createNodeDelegate() {
        RuntimeFolderNode fsRuntime = new RuntimeFolderNode(new RuntimeFolderChildren(this));
        fsRuntime.setName(fs.getSystemName());
        fsRuntime.setDisplayName(fs.getDisplayName());
        fsRuntime.setNumOfFinishedCmdsToCollect(numOfCommandsToKeep);
        java.beans.BeanDescriptor bd = getFsBeanDescriptor(fs);
        if (bd != null) {
            String str = (String)bd.getValue(org.netbeans.modules.vcscore.VcsFileSystem.VCS_FILESYSTEM_ICON_BASE);
            if (str != null) {
                fsRuntime.setIconBase(str);
            }
        }
        fsRuntime.addPropertyChangeListener(WeakListener.propertyChange(rcl, fsRuntime));
        //attachListeners(fsRuntime);
        return fsRuntime;
    }
    
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
    
    protected void notifyAdded() {
        processor.addCommandProcessListener(rcl);
    }
    
    protected void notifyRemoved() {
        processor.removeCommandProcessListener(rcl);
        //processor.removeFinishedCommandsUponRequest(false, fs);
    }
    
    private class RuntimeCommandsListener extends Object implements CommandProcessListener, PropertyChangeListener {
        
        private VcsCommandsProvider provider = fs.getCommandsProvider();
        
        /**
         * Get the commands provider. The listener gets events only from commands,
         * that are instances of ProvidedCommand and their provider equals to this
         * provider.
         */
        public VcsCommandsProvider getProvider() {
            return provider;
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
            RuntimeCommand cmd = (RuntimeCommand) runtimeCommandsForExecutors.get(info);
            if (cmd == null) {
                CommandTask task = info.getTask();
                if (!(task instanceof RuntimeCommandTask)) return ;
                cmd = ((RuntimeCommandTask) task).getRuntimeCommand(info);
                if (cmd == null) return ;
                //cmd = new VcsRuntimeCommand(info);
                synchronized (VcsRuntimeCommandsProvider.this) {
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
                synchronized (VcsRuntimeCommandsProvider.this) {
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
            if (source == fs) { // The change is comming from the FileSystem
                if (RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT.equals(propertyName)) {
                    int newNumOfCommandsToKeep = fs.getNumberOfFinishedCmdsToCollect();
                    //System.out.println("newNumOfCommandsToKeep = "+newNumOfCommandsToKeep+", numOfCommandsToKeep = "+numOfCommandsToKeep);
                    if (numOfCommandsToKeep == newNumOfCommandsToKeep) return ;
                    numOfCommandsToKeep = newNumOfCommandsToKeep;
                    RuntimeFolderNode node = (RuntimeFolderNode) getExistingNodeDelegate();
                    if (node != null) {
                        node.setNumOfFinishedCmdsToCollect(numOfCommandsToKeep);
                    }
                    if (cutCommandsToMaxToKeep()) firePropertyChange(PROP_CHILDREN, null, null);
                } else if (VcsFileSystem.PROP_DISPLAY_NAME.equals(propertyName)) {
                    RuntimeFolderNode node = (RuntimeFolderNode) getExistingNodeDelegate();
                    if (node != null) {
                        node.setDisplayName(fs.getDisplayName());
                    }
                }
            } else if (source instanceof RuntimeFolderNode) { // The change is comming from the Node
                RuntimeFolderNode node = (RuntimeFolderNode) source;
                if (RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT.equals(propertyName)) {
                    int newNumOfCommandsToKeep = node.getNumOfFinishedCmdsToCollect();
                    //System.out.println("newNumOfCommandsToKeep = "+newNumOfCommandsToKeep+", numOfCommandsToKeep = "+numOfCommandsToKeep);
                    if (numOfCommandsToKeep == newNumOfCommandsToKeep) return ;
                    numOfCommandsToKeep = newNumOfCommandsToKeep;
                    fs.setNumberOfFinishedCmdsToCollect(numOfCommandsToKeep);
                    if (cutCommandsToMaxToKeep()) firePropertyChange(PROP_CHILDREN, null, null);
                }
            }
        }
        
    }
}
