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

package org.netbeans.modules.vcscore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;

/**
 * The default implementation of VcsCommandsProvider based on commands provided
 * by CommandsTree.
 *
 * @author  Martin Entlicher
 */
public class DefaultVcsCommandsProvider extends VcsCommandsProvider implements CommandsTree.Provider {
    
    private CommandsTree commands;
    private Map commandSupportsByNames;
    private Map commandSupportsByClasses;
    private String[] commandNames;
    private boolean expertMode;
    private PropertyChangeSupport changeSupport;
    
    /** Creates a new instance of DefaultVcsCommandsProvider */
    public DefaultVcsCommandsProvider(CommandsTree commands) {
        changeSupport = new PropertyChangeSupport(this);
        setCommands(commands);
    }
    
    /**
     * Set the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public void setExpertMode(boolean expertMode) {
        this.expertMode = expertMode;
    }
    
    /**
     * Get the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public boolean isExpertMode() {
        return expertMode;
    }
    
    /**
     * Create a new VCS command of the given class type.
     * @return The command or <code>null</code> when the command of the given
     * class type does not exist.
     */
    public Command createCommand(Class cmdClass) {
        CommandSupport support = (CommandSupport) commandSupportsByClasses.get(cmdClass);
        if (support != null) {
            return support.createCommand();
        } else {
            return null;
        }
    }
    
    /** Create a new VCS command of the given name.
     * @return The command or <code>null</code> when the command of the given
     * name does not exist.
     *
     */
    public Command createCommand(String cmdName) {
        CommandSupport support = (CommandSupport) commandSupportsByNames.get(cmdName);
        if (support != null) {
            return support.createCommand();
        } else {
            return null;
        }
    }
    
    /**
     * Get the list of VCS command names.
     */
    public String[] getCommandNames() {
        return commandNames;
    }
    
    public CommandsTree getCommands() {
        return commands;
    }
    
    public void setCommands(CommandsTree commands) {
        this.commands = commands;
        commandSupportsByNames = new HashMap();
        commandSupportsByClasses = new HashMap();
        fillCommands(commands);
        commandNames = (String[]) commandSupportsByNames.keySet().toArray(new String[commandSupportsByNames.size()]);
        changeSupport.firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
    }
    
    private void fillCommands(CommandsTree commands) {
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            CommandSupport support = subCommands[i].getCommandSupport();
            if (support != null) {
                commandSupportsByNames.put(support.getName(), support);
                if (support instanceof UserCommandSupport) {
                    Class commandClass = ((UserCommandSupport) support).getImplementedCommandClass();
                    commandSupportsByClasses.put(commandClass, support);
                }
            }
            if (subCommands[i].hasChildren()) fillCommands(subCommands[i]);
        }
    }
    
    /** Add a property change listener to this provider.
     * The listener is called whenever the provided commands change.
     *
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    /** Remove the property change listener, that is attached to this provider.
     *
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
}
