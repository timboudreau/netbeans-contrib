/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collection;

import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;

import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.actions.AddToGroupAction;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.CommandExecutorSupport;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.VcsFSCache;

import org.netbeans.modules.vcscore.cmdline.CommandLineVcsDirReader;
import org.netbeans.modules.vcscore.cmdline.CommandLineVcsDirReaderRecursive;
import org.netbeans.modules.vcscore.cmdline.AdditionalCommandDialog;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;

/**
 * This class provides a default implementation of VcsFactory.
 *
 * @author  Martin Entlicher
 */
public class DefaultVcsFactory extends Object implements VcsFactory {

    private static Object fsActionAccessLock = new Object();
    private static VcsAction fsAction = null;

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
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        Object cache = fileSystem.getCacheProvider();
        if (cache instanceof FileStatusProvider) return (FileStatusProvider) cache;
        else return null;
    }

    /**
     * Get the provider of the VCS cache. The default provider
     * is <code>VcsFSCache</code>.
     */
    public FileCacheProvider getFileCacheProvider() {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        return new VcsFSCache(fileSystem);
    }

    /**
     * Get the VCS directory reader.
     * @return an instance of <code>CommandLineVcsDirReader</code> or null when can not be created.
     */
    public VcsCommandExecutor getVcsDirReader(DirReaderListener listener, String path) {
        VcsCommand list;
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        if (fileSystem.isOffLine()) {
            list = fileSystem.getCommand(VcsCommand.NAME_REFRESH_OFFLINE);
        } else {
            list = fileSystem.getCommand(VcsCommand.NAME_REFRESH);
        }
        if (list == null) return null;
        Hashtable vars = fileSystem.getVariablesAsHashtable();

        if (!java.io.File.separator.equals ("/") ) { // NOI18N
            String winPath = path.replace('/', java.io.File.separatorChar);
            vars.put("DIR", winPath); // NOI18N
        } else {
            vars.put("DIR", path); // NOI18N
        }

        String exec = (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec != null && !CommandExecutorSupport.promptForVariables(fileSystem, exec, vars, list, null)) return null;
        if (list instanceof UserCommand) {
            return new CommandLineVcsDirReader(listener, fileSystem, (UserCommand) list, vars);
        } else {
            return null;
        }
    }
    
    /**
     * Get the VCS directory reader that reads the whole directory structure.
     * @return an instance of <code>CommandLineVcsDirReader</code> or null when can not be created.
     */
    public VcsCommandExecutor getVcsDirReaderRecursive(DirReaderListener listener, String path) {
        VcsCommand list;
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        if (fileSystem.isOffLine()) {
            list = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY_OFFLINE);
        } else {
            list = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY);
        }
        if (list == null) return null;
        Hashtable vars = fileSystem.getVariablesAsHashtable();

        if (!java.io.File.separator.equals ("/")) { // NOI18N
            String winPath = path.replace('/', java.io.File.separatorChar);
            vars.put("DIR", winPath); // NOI18N
        } else {
            vars.put("DIR", path); // NOI18N
        }

        String exec = (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec != null && !CommandExecutorSupport.promptForVariables(fileSystem, exec, vars, list, null)) return null;
        if (list instanceof UserCommand) {
            return new CommandLineVcsDirReaderRecursive(listener, fileSystem, (UserCommand) list, vars);
        } else {
            return null;
        }
    }
    
    /*
     * Get the VCS action.
     * @return instance of <code>VcsAction</code>
     *
    public VcsAction getVcsAction() {
        return new VcsAction (fileSystem);
    }
     */
    
    /*
     * Get the VCS action for a collection of <code>FileObject</code>s.
     * If the collection is null, it gets the <code>FileObject</code>s from
     * currently selected nodes.
     * @param fos the collection of <code>FileObject</code>s or null.
     * @return instance of <code>VcsAction</code>
     *
    public VcsAction getVcsAction(Collection fos) {
        return new VcsAction (fileSystem, fos);
    }
     */
    
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
        if (fileSystem.getVersioningFileSystem() != null) {
            actions.add(SystemAction.get(VersioningExplorerAction.class));
        }
        actions.add(SystemAction.get(AddToGroupAction.class));
        //System.out.println("action[0] = "+actions.get(0)+", action[1] = "+actions.get(1)+", equals = "+actions.get(0).equals(actions.get(1)));
        //return (SystemAction[]) actions.toArray(new SystemAction[actions.size()]);
        return (SystemAction[]) actions.toArray(new SystemAction[0]);
    }
    
    /**
     * Get the command executor for the command.
     * @param command the command to get the executor for
     * @param variables the <code>Hashtable</code> of (variable name, variable value) pairs
     * @return the command executor or null when no executor is found. If command is instance of {@link UserCommand}, an instance of {@link ExecuteCommand} is returned.
     */
    public VcsCommandExecutor getCommandExecutor(VcsCommand command, Hashtable variables) {
        if (command instanceof UserCommand) {
            //if (VcsCommandIO.getBooleanProperty(command, UserCommand.PROPERTY_DISPLAY)) {
            //    AdditionalCommandDialog addCommand = new org.netbeans.modules.vcscore.cmdline.AdditionalCommandDialog(fileSystem, (UserCommand) command, variables, new javax.swing.JFrame(), false);
            //    return addCommand.createCommand();
            //} else {
                VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
                return new ExecuteCommand(fileSystem, (UserCommand) command, variables);
            //}
        } else {
            return null;
        }
    }
    
}
