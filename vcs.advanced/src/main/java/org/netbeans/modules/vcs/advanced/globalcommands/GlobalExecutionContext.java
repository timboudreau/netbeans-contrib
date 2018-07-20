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
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
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
import org.netbeans.modules.vcs.advanced.variables.Condition;
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
    private Vector variables;
    private String[] environmentVars;
    private CommandsTree commandsRoot;
    private Map commandsByName;
    private boolean commandNotification = true;
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
        profile.addPropertyChangeListener(WeakListeners.propertyChange(this, profile));
        defaultVariables = getDefaultVariables();
        variablesByNames = new Hashtable(Variables.getDefaultVariablesMap());
        variablesByNames.putAll(defaultVariables);
        varValueAdjustment = new VariableValueAdjustment();
        updateEnvironmentVars();
        varValueAdjustment.setAdjust(variablesByNames);
        setPasswordDescription(variablesByNames);
    }
    
    private Map getDefaultVariables() {
        Map map = new HashMap();
        map.put("DYNAMIC_ENVIRONMENT_VARS", "true");
        return map;
    }
    
    private void setPasswordDescription(Map varValuesByNames) {
        passwordDescription = (String) varValuesByNames.get(CommandLineVcsFileSystem.VAR_PASSWORD_DESCRIPTION);
        if (passwordDescription != null) {
            passwordDescription = Variables.expand(variablesByNames, passwordDescription, false);
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
        File file = new File(path);
        GlobalCommandsProvider.getInstance().fireFilesStructureModified(file);
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
    
    public synchronized CommandsTree getCommands() {
        if (commandsRoot == null) {
            Profile profile = (Profile) profileRef.get();
            if (profile != null) {
                if (variables == null) {
                    // It's too expensive to load all variables, obtaining of commands must be fast,
                    // because a GUI menu is created from them. Therefore conditions used in
                    // global commands are resolved according to defined conditions, not variables.
                    // This should be sufficient anyway.
                    profile.preLoadContent(true, false, false, true);
                    Condition[] conditions = profile.getConditions();
                    if (conditions != null) {
                        for (int i = 0; i < conditions.length; i++) {
                            String name = conditions[i].getName();
                            if (conditions[i].isSatisfied(variablesByNames)) {
                                variablesByNames.put(name, Boolean.TRUE.toString());
                            } else {
                                variablesByNames.remove(name);
                            }
                        }
                    }
                } else {
                    profile.preLoadContent(false, false, false, true);
                }
                setCommands(copySharedCommands(profile.getGlobalCommands().getCommands(variablesByNames)));
            }
        }
        return commandsRoot;
    }
    
    private synchronized void setCommands(CommandsTree commands) {
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
        Map env = VcsUtilities.addEnvVars(systemEnv, variablesByNames,
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
        if (variables == null) {
            getVariables();
        }
        return new Hashtable(variablesByNames);
    }
    
    public synchronized Vector getVariables() {
        Profile profile = (Profile) profileRef.get();
        if (profile != null) {
            if (variables == null) {
                ConditionedVariables cvariables = profile.getVariables();
                if (cvariables != null) {
                    profile.preLoadContent(true, true, false, false);
                    Map variablesMap = profile.getVariables().getSelfConditionedVariableMap(profile.getConditions(), Variables.getDefaultVariablesMap());
                    variablesByNames.putAll(variablesMap);
                    varValueAdjustment = new VariableValueAdjustment();
                    updateEnvironmentVars();
                    varValueAdjustment.setAdjust(variablesByNames);
                    setPasswordDescription(variablesByNames);
                    Map allVarsMap = new HashMap(Variables.getDefaultVariablesMap());
                    allVarsMap.putAll(variablesMap);
                    variables = new Vector(cvariables.getVariables(allVarsMap));
                } else{
                    variables = new Vector();
                }
            }
            return variables;
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
