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

import java.util.Hashtable;
import java.util.ArrayList;

import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
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

    protected VcsFileSystem fileSystem;
    
    /** Creates new DefaultVcsFactory */
    public DefaultVcsFactory(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Get the provider of the VCS status information. The default provider
     * is the filesystem cache if implements <code>FileStatusProvider</code> or null otherwise.
     */
    public FileStatusProvider getFileStatusProvider() {
        Object cache = fileSystem.getCacheProvider();
        if (cache instanceof FileStatusProvider) return (FileStatusProvider) cache;
        else return null;
    }

    /**
     * Get the provider of the VCS cache. The default provider
     * is <code>VcsFSCache</code>.
     */
    public FileCacheProvider getFileCacheProvider() {
        return new VcsFSCache(fileSystem);
    }

    /**
     * Get the VCS directory reader.
     * @return an instance of <code>CommandLineVcsDirReader</code> or null when can not be created.
     */
    public VcsCommandExecutor getVcsDirReader(DirReaderListener listener, String path) {
        VcsCommand list = fileSystem.getCommand(VcsCommand.NAME_REFRESH); // NOI18N
        if (list == null) return null;
        Hashtable vars = fileSystem.getVariablesAsHashtable();

        if (!java.io.File.separator.equals ("/") ) { // NOI18N
            String winPath = path.replace('/', java.io.File.separatorChar);
            vars.put("DIR", winPath); // NOI18N
        } else {
            vars.put("DIR", path); // NOI18N
        }

        String exec = (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec != null && !fileSystem.promptForVariables(exec, vars, list, null)) return null;
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
        VcsCommand list = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY); // NOI18N
        if (list == null) return null;
        Hashtable vars = fileSystem.getVariablesAsHashtable();

        if (!java.io.File.separator.equals ("/")) { // NOI18N
            String winPath = path.replace('/', java.io.File.separatorChar);
            vars.put("DIR", winPath); // NOI18N
        } else {
            vars.put("DIR", path); // NOI18N
        }

        String exec = (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec != null && !fileSystem.promptForVariables(exec, vars, list, null)) return null;
        if (list instanceof UserCommand) {
            return new CommandLineVcsDirReaderRecursive(listener, fileSystem, (UserCommand) list, vars);
        } else {
            return null;
        }
    }
    
    /**
     * Get the VCS action.
     * @return instance of <code>VcsAction</code>
     */
    public VcsAction getVcsAction() {
        return new VcsAction (fileSystem);
    }
    
    /**
     * Get the VCS action for a specified <code>FileObject</code>.
     * @return instance of <code>VcsAction</code>
     */
    public VcsAction getVcsAction(org.openide.filesystems.FileObject fo) {
        return new VcsAction (fileSystem, fo);
    }
    
    /**
     * Get the VCS actions.
     * @return null by default
     */
    public SystemAction[] getActions() {
        //return new SystemAction[] { getVcsAction() };
        ArrayList actions = new ArrayList();
        Node commands = fileSystem.getCommands();
        Node[] commandRoots = commands.getChildren().getNodes();
        for (int i = 0; i < commandRoots.length; i++) {
            VcsCommand cmd = (VcsCommand) commandRoots[i].getCookie(VcsCommand.class);
            if (cmd != null &&
                VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS) == 0) {
                    actions.add(new VcsAction(fileSystem, i));
            }
        }
        if (actions.size() == 0) return null;
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
            if (VcsCommandIO.getBooleanProperty(command, UserCommand.PROPERTY_DISPLAY)) {
                AdditionalCommandDialog addCommand = new org.netbeans.modules.vcscore.cmdline.AdditionalCommandDialog(fileSystem, (UserCommand) command, variables, new javax.swing.JFrame(), false);
                return addCommand.createCommand();
            } else {
                return new ExecuteCommand(fileSystem, (UserCommand) command, variables);
            }
        } else {
            return null;
        }
    }
    
}
