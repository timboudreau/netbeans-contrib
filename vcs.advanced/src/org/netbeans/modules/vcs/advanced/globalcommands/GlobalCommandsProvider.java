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

package org.netbeans.modules.vcs.advanced.globalcommands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.openide.util.WeakListener;

import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.runtime.RuntimeFolderNode;

import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;

/**
 * Provider of global VCS commands.
 *
 * @author  Martin Entlicher
 */
public class GlobalCommandsProvider extends VcsCommandsProvider implements CommandsTree.Provider,
                                                                           PropertyChangeListener {
    
    private static GlobalCommandsProvider instance;
    
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private Map profilesByNames = new HashMap();
    private Map commandSupportsByNames = new HashMap();
    private String[] commandNames;
    private CommandsTree commands;
    private int numberOfFinishedCmdsToCollect = RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    private boolean runtimeCreated = false;
    private boolean expertMode = false;
    
    /** Creates a new instance of GlobalCommandsProvider */
    private GlobalCommandsProvider() {
        ProfilesFactory factory = ProfilesFactory.getDefault();
        factory.addPropertyChangeListener(WeakListener.propertyChange(this, factory));
        synchronized (this) {
            String names[] = factory.getProfilesNames();
            for (int i = 0; i < names.length; i++) {
                Profile profile = factory.getProfile(names[i]);
                if (profile == null || !factory.isOSCompatibleProfile(names[i])) continue;
                // TODO when a profile is not OS-compatible, we should listen
                //      for changes of it. It may become OS-compatible later.
                profilesByNames.put(names[i], profile);
                profile.addPropertyChangeListener(this);
            }
            collectCommands();
        }
    }
    
    /**
     * Get the instance of GlobalCommandsProvider.
     */
    public static final synchronized GlobalCommandsProvider getInstance() {
        if (instance == null) {
            instance = new GlobalCommandsProvider();
        }
        return instance;
    }
    
    /** Create a new VCS command of the given class type.
     * @return The command or <code>null</code> when the command of the given
     * class type does not exist.
     *
     */
    public Command createCommand(Class cmdClass) {
        return null;
    }
    
    /** Create a new VCS command of the given name.
     * @return The command or <code>null</code> when the command of the given
     * name does not exist.
     */
    public Command createCommand(String cmdName) {
        CommandSupport support = (CommandSupport) commandSupportsByNames.get(cmdName);
        if (support != null) {
            return support.createCommand();
        } else {
            return null;
        }
    }
    
    /** Get the list of VCS command names.
     */
    public String[] getCommandNames() {
        return commandNames;
    }
    
    /** Get the commands.
     * @return The root of the commands tree.
     */
    public CommandsTree getCommands() {
        if (!runtimeCreated) {
            new GlobalRuntimeCommandsProvider(); // Create new runtime commands provider, it will do itself what is necessary.
            runtimeCreated = true;
        }
        return commands;
    }
    
    private void collectCommands() {
        Profile[] profiles = (Profile[]) profilesByNames.values().toArray(new Profile[0]);
        commands = createCommandsFromProfiles(profiles);
        fillCommands(commands);
    }
    
    private static CommandsTree createCommandsFromProfiles(Profile[] profiles) {
        CommandsTree commands = new CommandsTree(null);
        //System.out.println("GlobalCommandsProvider.createCommandsFromProfiles()");
        for (int i = 0; i < profiles.length; i++) {
            GlobalExecutionContext globalContext = new GlobalExecutionContext(profiles[i]);
            //CommandsTree profileCommands = profiles[i].getGlobalCommands();
            CommandsTree profileCommands = globalContext.getCommands();
            CommandsTree[] children = profileCommands.children();
            for (int j = 0; j < children.length; j++) {
                commands.add(children[j]);
                //System.out.println("  add "+children[j]+
                //    ((children[j].getCommandSupport() != null) ?
                //        ", name = "+children[j].getCommandSupport().getName() :
                //        ", null support"));
            }
            if (children.length > 0) {
                //System.out.println("  had "+children.length+" children, adding separator.");
                commands.add(CommandsTree.EMPTY);
            }
        }
        return commands;
    }
    
    private void fillCommands(CommandsTree commands) {
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            CommandSupport support = subCommands[i].getCommandSupport();
            if (support != null) {
                commandSupportsByNames.put(support.getName(), support);
            }
            if (subCommands[i].hasChildren()) fillCommands(subCommands[i]);
        }
        commandNames = (String[]) commandSupportsByNames.keySet().toArray(new String[commandSupportsByNames.size()]);
    }
    
    public int getNumberOfFinishedCmdsToCollect() {
        return numberOfFinishedCmdsToCollect;
    }
    
    public void setNumberOfFinishedCmdsToCollect(int numberOfFinishedCmdsToCollect) {
        this.numberOfFinishedCmdsToCollect = numberOfFinishedCmdsToCollect;
        firePropertyChange(RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, null, null);
    }
    
    /**
     * Get the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public boolean isExpertMode() {
        return expertMode;
    }

    /**
     * Set the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public void setExpertMode(boolean expertMode) {
        this.expertMode = expertMode;
    }
        
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /** This method gets called when a ProfilesFactory property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (ProfilesFactory.PROP_PROFILE_ADDED.equals(propertyName)) {
            synchronized (this) {
                String name = (String) evt.getNewValue();
                Profile profile = ProfilesFactory.getDefault().getProfile(name);
                if (profile == null || !ProfilesFactory.getDefault().isOSCompatibleProfile(name)) {
                    return ;
                }
                profilesByNames.put(name, profile);
                profile.addPropertyChangeListener(WeakListener.propertyChange(this, profile));
                collectCommands();
            }
            firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
        } else if (ProfilesFactory.PROP_PROFILE_REMOVED.equals(propertyName)) {
            synchronized (this) {
                String name = (String) evt.getOldValue();
                Object old = profilesByNames.remove(name);
                if (old == null) return ;
                collectCommands();
            }
            firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
        } else if (Profile.PROP_GLOBAL_COMMANDS.equals(propertyName)) {
            synchronized (this) {
                collectCommands();
            }
            firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
        }
    }
    
}
