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
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;

/**
 * The execution context of global commands.
 *
 * @author  Martin Entlicher
 */
public class GlobalExecutionContext extends Object implements CommandExecutionContext, PropertyChangeListener {
    
    private Reference profileRef;
    private Hashtable variablesByNames;
    private Map defaultVariables;
    private VariableValueAdjustment varValueAdjustment;
    private String[] environmentVars;
    private CommandsTree commandsRoot;
    private Map commandsByName;
    private boolean commandNotification = false;
    private boolean promptForVarsForEachFile = false;
    private String password = null;
    private String passwordDescription = null;
    private InputOutput cmdIO;
    private String profileName;
    /**
     * Additional user parameters to the command. These are global parameters to all commands.
     * Parameters local to each command are stored in UserCommand.userParams.
     * The user is asked for them when acceptUserParams = true
     */
    private volatile String[] userParams = null;
    /**
     * Labels to additional user parameters.
     */
    private volatile String[] userParamsLabels = null;
    /**
     * Labels to local additional user parameters.
     */
    private volatile String[] userLocalParamsLabels = null;
    
    private volatile boolean acceptUserParams = false;
    
    /** The expert mode. When true, the user might be prompted for other options.
     */
    private boolean expertMode = false;
    
    
    /** Creates a new instance of GlobalExecutionContext */
    public GlobalExecutionContext(Profile profile) {
        profileName = profile.getName();
        profileRef = new WeakReference(profile);
        profile.addPropertyChangeListener(WeakListener.propertyChange(this, profile));
        defaultVariables = getDefaultVariables();
        variablesByNames = new Hashtable(Variables.getDefaultVariablesMap());
        variablesByNames.putAll(defaultVariables);
        Map variablesMap = profile.getVariables().getSelfConditionedVariableMap(profile.getConditions(), Variables.getDefaultVariablesMap());
        variablesByNames.putAll(variablesMap);
        varValueAdjustment = new VariableValueAdjustment();
        updateEnvironmentVars();
        varValueAdjustment.setAdjust(variablesByNames);
        setPasswordDescription(variablesByNames);
        setCommands(copySharedCommands(profile.getGlobalCommands().getCommands(variablesByNames)));
    }
    
    private Map getDefaultVariables() {
        Map map = new HashMap();
        map.put("DYNAMIC_ENVIRONMENT_VARS", "true");
        return map;
    }
    
    private void setPasswordDescription(Map varValuesByNames) {
        passwordDescription = (String) varValuesByNames.get(CommandLineVcsFileSystem.VAR_PASSWORD_DESCRIPTION);
        if (passwordDescription != null) {
            passwordDescription = Variables.expand(getVariablesAsHashtable(), passwordDescription, false);
            if (passwordDescription.trim().length() == 0) passwordDescription = null;
        }
    }
    
    private final CommandsTree copySharedCommands(CommandsTree sharedCommands) {
        CommandsTree commands = new CommandsTree(copySharedCommandSupport(sharedCommands.getCommandSupport()));
        if (sharedCommands.hasChildren()) {
            CommandsTree sharedSubCommands[] = sharedCommands.children();
            for (int i = 0; i < sharedSubCommands.length; i++) {
                CommandsTree subCommand;
                if (sharedSubCommands[i].hasChildren()) {
                    subCommand = copySharedCommands(sharedSubCommands[i]);
                } else {
                    subCommand = new CommandsTree(copySharedCommandSupport(sharedSubCommands[i].getCommandSupport()));
                }
                commands.add(subCommand);
            }
        }
        return commands;
    }
    
    private final CommandSupport copySharedCommandSupport(CommandSupport sharedCmd) {
        if (!(sharedCmd instanceof UserCommandSupport)) return sharedCmd;
        CommandSupport cmd = new UserCommandSupport(((UserCommandSupport) sharedCmd).getVcsCommand(), this);
        return cmd;
    }
    
    /** Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    public void checkForModifications(String path) {
    }
    
    /** Print a debug output. If the debug property is true, the message
     * is printed to the Output Window.
     * @param msg The message to print out.
     */
    public void debug(String msg) {
    }
    
    /** Print an error output. Force the message to print to the Output Window.
     * The debug property is not considered.
     * @param msg the message to print out.
     */
    public void debugErr(String msg) {
        InputOutput out = getCommandsIO();
        out.getErr().println(msg);
        out.select();
    }
    
    protected InputOutput getCommandsIO () {
        if (cmdIO == null) {
            cmdIO = IOProvider.getDefault().getIO(
                        NbBundle.getMessage(GlobalExecutionContext.class, "LBL_VCS_Output"),
                        false);
            cmdIO.setErrSeparated(false);
        }
        return cmdIO;
    }

    public VcsCommand getCommand(String name) {
        CommandSupport support = getCommandSupport(name);
        if (support instanceof UserCommandSupport) {
            return ((UserCommandSupport) support).getVcsCommand();
        } else {
            return null;
        }
    }
    
    public CommandSupport getCommandSupport(String name) {
        if (commandsByName == null) {
            CommandsTree commands = getCommands();
            if (commands == null) return null;
            setCommands(commands);
        }
        return (CommandSupport) commandsByName.get(name);
    }
    
    public CommandsTree getCommands() {
        return commandsRoot;
    }
    
    private void setCommands(CommandsTree commands) {
        this.commandsRoot = commands;
        commandsByName = new Hashtable();
        addCommandsToHashTable(commands);
    }
    
    private void addCommandsToHashTable(CommandsTree root) {
        CommandsTree[] children = root.children();
        for (int i = 0; i < children.length; i++) {
            CommandSupport cmdSupp = children[i].getCommandSupport();
            if (cmdSupp != null) {
                commandsByName.put(cmdSupp.getName(), cmdSupp);
            }
            if (children[i].hasChildren()) addCommandsToHashTable(children[i]);
        }
    }
    
    public VcsCommandsProvider getCommandsProvider() {
        return GlobalCommandsProvider.getInstance();
    }
    
    public String[] getEnvironmentVars() {
        return environmentVars;
    }
    
    private void updateEnvironmentVars() {
        Map systemEnv = VcsUtilities.getSystemEnvVars();
        Map env = VcsUtilities.addEnvVars(systemEnv, getVariablesAsHashtable(),
                                          VcsFileSystem.VAR_ENVIRONMENT_PREFIX,
                                          VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX);
        environmentVars = VcsUtilities.getEnvString(env);
    }

    public VariableValueAdjustment getVarValueAdjustment() {
        return varValueAdjustment;
    }
    
    /** If this is FileSystem, take it from there! If not, get abolute paths.
     * If the execution context is a filesystem, it may wish to
     *
     *     String convertFileToPath(java.io.File file);
     *
     *     String convertFileToPath(FileObject file);
     *
     */
    public Hashtable getVariablesAsHashtable() {
        return new Hashtable(variablesByNames);
    }
    
    public Vector getVariables() {
        Profile profile = (Profile) profileRef.get();
        if (profile != null) {
            ConditionedVariables cvariables = profile.getVariables();
            if (cvariables != null) {
                Map variableMap = cvariables.getVariableMap(Variables.getDefaultVariablesMap());
                Map allVarsMap = new HashMap(Variables.getDefaultVariablesMap());
                allVarsMap.putAll(variableMap);
                Collection variables = cvariables.getVariables(allVarsMap);
                return new Vector(variables);
            } else{
                return new Vector();
            }
        } else {
            return new Vector();
        }
    }
    
    public String getProfileName(){
        return profileName;
    }
    
    public void setVariables(Vector variables) {
        // Unimplemented. Variables can not be set.
    }
    
    /**
     * Return an empty map, no file status information is considered.
     */
    public Map getPossibleFileStatusInfoMap() {
        return Collections.EMPTY_MAP;
    }
    
    public boolean isCommandNotification() {
        return commandNotification;
    }
    
    public boolean isExpertMode() {
        return expertMode;
    }
    
    public boolean isOffLine() {
        return false;
    }
    
    public boolean isPromptForVarsForEachFile() {
        return promptForVarsForEachFile;
    }
    
    public void setCommandNotification(boolean commandNotification) {
        this.commandNotification = commandNotification;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Get the description of the password, typically the name of the service
     * that requests the password.
     * @return The description or <code>null</code> when no description is available.
     */
    public String getPasswordDescription() {
        return passwordDescription;
    }
    
    public void setPromptForVarsForEachFile(boolean promptForVarsForEachFile) {
        this.promptForVarsForEachFile = promptForVarsForEachFile;
    }
    
    public void setAcceptUserParams(boolean acceptUserParams) {
        this.acceptUserParams = acceptUserParams;
    }
    
    public boolean isAcceptUserParams() {
        return acceptUserParams;
    }

    public void setUserLocalParamsLabels(String[] labels) {
        userLocalParamsLabels = labels;
    }
    
    public String[] getUserLocalParamsLabels() {
        return userLocalParamsLabels;
    }
    
    public void setUserParams(String[] userParams) {
        this.userParams = userParams;
    }
    
    public String[] getUserParams() {
        return userParams;
    }
    
    public void setUserParamsLabels(String[] labels) {
        userParamsLabels = labels;
        userParams = new String[labels.length];
    }
    
    public String[] getUserParamsLabels() {
        return userParamsLabels;
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (Profile.PROP_VARIABLES.equals(evt.getPropertyName())) {
            Profile profile = (Profile) profileRef.get();
            if (profile != null) {
                Map variablesMap = new Hashtable(Variables.getDefaultVariablesMap());
                variablesMap.putAll(profile.getVariables().getVariableMap(Variables.getDefaultVariablesMap()));
                variablesByNames = new Hashtable(Variables.getDefaultVariablesMap());
                variablesByNames.putAll(defaultVariables);
                variablesByNames.putAll(profile.getVariables().getVariableMap(variablesMap));
                updateEnvironmentVars();
                varValueAdjustment.setAdjust(variablesByNames);
                setPasswordDescription(variablesByNames);
            }
        } else if (Profile.PROP_GLOBAL_COMMANDS.equals(evt.getPropertyName())) {
            Profile profile = (Profile) profileRef.get();
            if (profile != null) {
                setCommands(copySharedCommands(profile.getGlobalCommands().getCommands(variablesByNames)));
            }
        }
    }
    
}
