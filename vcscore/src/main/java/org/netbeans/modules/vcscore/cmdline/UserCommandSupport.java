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

package org.netbeans.modules.vcscore.cmdline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;

import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.api.vcs.commands.Command;

import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.CheckInCommand;
import org.netbeans.api.vcs.commands.CheckOutCommand;
//import org.netbeans.api.vcs.commands.DiffCommand;
//import org.netbeans.api.vcs.commands.HistoryCommand;
import org.netbeans.api.vcs.commands.RemoveCommand;

import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.spi.vcs.commands.CommandTaskSupport;

import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsProvider;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.ActionCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandCustomizationSupport;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.RecursionAwareCommand;
import org.netbeans.modules.vcscore.commands.RecursionAwareCommandSupport;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.RegexErrorListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VariableInputDialog;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The adapter from UserCommand to CommandSupport. This class is used to
 * transfer the VcsCommand approach to the new org.netbeans.api.vcs.commands.Command
 * approach.
 * @author  Martin Entlicher
 */
public class UserCommandSupport extends CommandSupport implements java.security.PrivilegedAction,
                                                                  ActionCommandSupport,
                                                                  RecursionAwareCommandSupport {
    /**
     * The list of variables, that contains file paths which will be clonned
     * when a next command is created.
     */
    public static final String VAR_ARGUMENT_FILES_TO_CLONE = "ARGUMENT_FILES_TO_CLONE"; // NOI18N
    
    private static final String PROPERTY_PARSED_ATTR_NAMES = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "FOAttributesNamesParsed"; // NOI18N
    private static final String PROPERTY_PARSED_ATTR_NEMPTY_VARS = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "FOAttributesNotEmptyVars"; // NOI18N
    private static final String PROPERTY_PARSED_ATTR_VALUES_VARS = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "FOAttributesValuesVars"; // NOI18N
    
    private UserCommand cmd;
    private CommandExecutionContext executionContext;
    //private Hashtable variableMap;
    private String displayName;
    private boolean displayNameDefined;
    private Class[] implementedCommandClasses = null;
    
    /** A flag of whether the command associated with this clon of
     * UserCommandSupport was already customized or not, so that we know whether
     * a customization needs to be done before the execution. */
    //private boolean isCommandCustomized = false;
    
    /** Creates a new instance of UserCommandSupport */
    public UserCommandSupport(UserCommand cmd, CommandExecutionContext executionContext) {
        super(getClassesForCommand(cmd));
        this.cmd = cmd;
        this.executionContext = executionContext;
        if (executionContext != null) {
            String label = cmd.getDisplayName();
            if (label != null && label.indexOf('$') >= 0) {
                this.displayNameDefined = false; // must be dynamically resolved
            } else {
                this.displayName = getDisplayName(cmd);
                this.displayNameDefined = true;
            }
        } else {
            this.displayNameDefined = false; // must be dynamically resolved
        }
        this.implementedCommandClasses = findImplementedCommandClasses(cmd);
    }
    
    private static Class[] getClassesForCommand(UserCommand cmd) {
        List classes = new ArrayList();
        classes.add(VcsDescribedCommand.class);
        classes.add(CustomizationStatus.class);
        classes.add(RecursionAwareCommand.class);
        Class[] cmdClasses = findImplementedCommandClasses(cmd);
        for (int i = 0; i < cmdClasses.length; i++) {
            classes.add(cmdClasses[i]);
        }
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }
    
    private static Class[] findImplementedCommandClasses(UserCommand cmd) {
        String cmdClassNames = (String) cmd.getProperty(VcsCommand.PROPERTY_ASSOCIATED_COMMAND_INTERFACE_NAME);
        //System.out.println("getClassesForCommand("+cmd+"), cmdClassNames = "+cmdClassNames);
        if (cmdClassNames == null) return new Class[0];
        cmdClassNames = cmdClassNames.trim();
        List classesList = new ArrayList();
        int l = cmdClassNames.length();
        int spaceIndex = cmdClassNames.indexOf(' ');
        if (spaceIndex < 0) spaceIndex = l;
        int lastIndex = 0;
        while (spaceIndex > 0) {
            String className = cmdClassNames.substring(lastIndex, spaceIndex).trim();
            try {
                ClassLoader systemClassLoader = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
                Class cmdClass = Class.forName(className, false, systemClassLoader);
                //System.out.println("Got class "+cmdClass+" for command "+cmd.getName());
                classesList.add(cmdClass);
            } catch (ClassNotFoundException cnfex) {
                org.openide.ErrorManager.getDefault().notify(cnfex);
            }
            lastIndex = spaceIndex;
            while (lastIndex < l && Character.isWhitespace(cmdClassNames.charAt(lastIndex))) {
                lastIndex++;
            }
            if (lastIndex < l) {
                spaceIndex = cmdClassNames.indexOf(' ', lastIndex);
                if (spaceIndex < 0) spaceIndex = l;
            } else {
                spaceIndex = -1; // The END.
            }
        }
        return (Class[]) classesList.toArray(new Class[0]);
    }
    
    private String getDisplayName(UserCommand cmd) {
        String label = cmd.getDisplayName();
        if (label != null) {
            if (Variables.needFurtherExpansion(label)) {
                Map variableMap = executionContext.getVariableValuesMap();
                label = Variables.expand(variableMap, label, false);
            }
            String mnemonic = (String) cmd.getProperty(VcsCommand.PROPERTY_LABEL_MNEMONIC);
            if (mnemonic != null && mnemonic.length() > 0) {
                char mnemonicChar = mnemonic.charAt(0);
                int mnemonicIndex = label.indexOf(mnemonicChar);
                if (mnemonicIndex >= 0) {
                    label = label.substring(0, mnemonicIndex) + "&" + label.substring(mnemonicIndex);
                }
            }
        }
        return label;
    }
    
    /**
     * Get the UserCommand associated with this support.
     */
    public UserCommand getVcsCommand() {
        return cmd;
    }
    
    /**
     * Get the execution context, that is associated with this support.
     */
    public CommandExecutionContext getExecutionContext() {
        return executionContext;
    }
    
    /**
     * Get the name of the command.
     */
    public String getName() {
        return cmd.getName();
    }

    /** For debug purposes only. */
    public String toString() {
        return "UserCommandSupport[" + getName() + "]";  // NOI18N
    }

    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName() {
        if (displayNameDefined) {
            return displayName;
        } else {
            if (executionContext == null) return null;
            return getDisplayName(cmd);
        }
    }
    
    public Class[] getImplementedCommandClasses() {
        return implementedCommandClasses;
    }
    
    /*
     * Get the mnemonic displayed for that command. The mnemonic will be set
     * on the menu item of this command.
     * @return The mnemonic character or <code>null</code> when no mnemonic is needed.
     *
    public char getDisplayedMnemonic() {
        String mnemonic = (String) cmd.getProperty(VcsCommand.PROPERTY_LABEL_MNEMONIC);
        if (mnemonic != null && mnemonic.length() > 0) {
            return mnemonic.charAt(0);
        } else {
            return '\0';
        }
    }
     */
    
    /**
     * Whether the command supports an expert mode. The command should provide
     * a more complex customizer and/or output if in expert mode. If the
     * command does not differentiate expert mode, it should declare, that
     * it does not have an expert mode.
     * @return true If the command differentiate expert mode, false otherwise
     */
    public boolean hasExpertMode() {
        return VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_SUPPORTS_ADVANCED_MODE);
    }
    
    protected CommandTaskSupport createTask(Command command) {
        if (!(command instanceof VcsDescribedCommand)) return null;
        //if (!isCommandCustomized) doCustomization(false, command);
        if (((CustomizationStatus) command).isCustomizationFailed()) {
            throw new IllegalStateException("Attempt to create a task whose customization did not finish successfully.");
        }
        if (!((CustomizationStatus) command).isAlreadyCustomizedUserCommand()) {
            doCustomization(false, (VcsDescribedCommand) command);
        }
        VcsDescribedCommand dCommand = (VcsDescribedCommand) command;
        if (dCommand.getNextCommand() != null) {
            WrappingCommandTask wt = new WrappingCommandTask(this, dCommand);
            wt.runTasks();
            return wt;
        } else {
            return new UserCommandTask(this, dCommand);//, ec);
        }
    }
    
    DirReaderListener getAttachedDirReaderListener(Command command) {
        EventListener[] listeners = getListeners(DirReaderListener.class, command);
        return (listeners != null && listeners.length > 0) ? (DirReaderListener) listeners[0] : null;
    }
    
    /* implemented in UserCommandTask instead!
    protected boolean canExecute(CommandTask task) {
    }
     */
    
    /**
     * Perform the actual execution of the command from the provided info.
     */
    protected int execute(CommandTask task) {
        if (task instanceof WrappingCommandTask) return ((WrappingCommandTask) task).waitForTasks();
        Command command = getCommand(task);
        if (!(command instanceof VcsDescribedCommand)) return task.STATUS_FAILED;
        if (!(task instanceof UserCommandTask)) return task.STATUS_FAILED;
        UserCommandTask userTask = (UserCommandTask) task;
        if (userTask.willSpawnRefresh()) {
            userTask.spawnRefresh((VcsProvider) executionContext);
            return task.STATUS_SUCCEEDED;
        }
        VcsDescribedCommand dCommand = (VcsDescribedCommand) command;
        VcsCommandExecutor ec = userTask.getExecutor();
        //if (ec == null) return task.STATUS_FAILED; ec must NOT be null !
        java.util.EventListener[] listeners = getListeners(TextOutputListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addTextOutputListener((TextOutputListener) listeners[i]);
            }
        }
        listeners = getListeners(TextErrorListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addTextErrorListener((TextErrorListener) listeners[i]);
            }
        }
        listeners = getListeners(RegexOutputListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addRegexOutputListener((RegexOutputListener) listeners[i]);
            }
        }
        listeners = getListeners(RegexErrorListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addRegexErrorListener((RegexErrorListener) listeners[i]);
            }
        }
        listeners = getListeners(FileReaderListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addFileReaderListener((FileReaderListener) listeners[i]);
            }
        }
        Map additionalVariables = dCommand.getAdditionalVariables();
        Map vars = ec.getVariables();
        if (additionalVariables != null) {
            vars.putAll(additionalVariables);
        }
        ec.preprocessCommand(cmd, vars, dCommand.getPreferredExec(), dCommand.getPreferredStructuredExec());
        VcsCommandVisualizer visualizer = userTask.getVisualizerGUI(false, false);
        final VcsCommandVisualizer.Wrapper wrapper = dCommand.getVisualizerWrapper();
        if (wrapper != null) wrapper.setTask(task);
        if (visualizer == null) {
            if (VcsCommandIO.getBooleanProperty(ec.getCommand(), VcsCommand.PROPERTY_DISPLAY_INTERACTIVE_OUTPUT)) {
                visualizer = userTask.getVisualizerGUI(true, true);
            } else if (VcsCommandIO.getBooleanProperty(ec.getCommand(), VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT)) {
                visualizer = userTask.getVisualizerGUI(true, false);
            }
        }
        if (!command.isGUIMode()) {
            visualizer = null; // No visualization in non-GUI mode.
        }
        if (visualizer != null && !visualizer.openAfterCommandFinish()) {
            final VcsCommandVisualizer fvisualizer = visualizer;
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fvisualizer.open(wrapper);
                }
            });
        }
        try {
            ec.run();
        } finally {
            if (visualizer != null) {
                if (visualizer.openAfterCommandFinish() && (visualizer.doesDisplayError() || ec.getExitStatus() == ec.SUCCEEDED)) {
                    final VcsCommandVisualizer fvisualizer = visualizer;
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            fvisualizer.open(wrapper);
                        }
                    });
                }
            }
        }
        return ec.getExitStatus();
    }
    
    /**
     * Find, whether this command can act on a set of files.
     * @param files The array of files to inspect
     * @return an array of files the command can act on or <code>null</code> when
     * it can not act on any file listed.
     */
    public FileObject[] getApplicableFiles(FileObject[] files) {
        // If the FS is offline, the command must end with _OFFLINE
        // If the FS is not offline, search for the corresponding offline command
        if (executionContext == null) return null; // I can not execute without a context
        boolean offLine;
        if ((offLine = executionContext.isOffLine()) != getName().endsWith(VcsCommand.NAME_SUFFIX_OFFLINE)) {
            if (offLine && executionContext.getCommand(getName() + VcsCommand.NAME_SUFFIX_OFFLINE) != null) {
                return null;
            }
            if (!offLine) return null;
        }
        // If the command can run only on some number of revisions, return null
        // if no revision is specified.
        if (VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS) != 0) {
            Command cmd = getCommand();
            if (cmd != null) {
                Map vars = ((VcsDescribedCommand) cmd).getAdditionalVariables();
                if (vars == null || vars.get("REVISION") == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        Command command = getCommand();
        if (command != null) {
            if (((RecursionAwareCommand) command).isRecursionBanned() &&
                VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_NON_RECURSIVE_DIR) == false) {

                // We have disabled recursion, but the command can not run on non-recursive folders.
                return null;
            }
        }
        //if (executionContext instanceof VcsProvider) {
        //    files = VcsUtilities.convertFileObjects(files);
        //}
        FileObject[] appFiles = CommandCustomizationSupport.getApplicableFiles(executionContext, cmd, files);
        //System.out.println("getApplicableFiles("+cmd+", "+new ArrayList(Arrays.asList(files))+") = "+
        //                   ((appFiles == null) ? null : new ArrayList(Arrays.asList(appFiles))));
        //Thread.dumpStack();
        return appFiles;
        //return files;
    }
    
    public boolean canProcessFoldersNonRecursively() {
        return VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_NON_RECURSIVE_DIR);
    }
    
    protected Object clone() throws CloneNotSupportedException {
        UserCommandSupport clone = new UserCommandSupport(cmd, executionContext);
        return clone;
    }
    
    /**
     * Initialize the command after it's created. This allows the implementator
     * to preset some initial values to the command before it's customized.
     * When sb. is just creates a command through VcsManager.createCommand(..)
     * they will get this customized command.
     * @param cmd The command to be customized.
     */
    protected void initializeCommand(Command cmd) {
        super.initializeCommand(cmd);
        VcsDescribedCommand vcmd = (VcsDescribedCommand) cmd;
        vcmd.setVcsCommand(this.cmd);
        cmd.setExpertMode(executionContext.isExpertMode());
        if (executionContext instanceof VcsProvider) {
            vcmd.addFileReaderListener(TurboUtil.fileReaderListener((VcsProvider)executionContext));
        }
    }
    
    /**
     * Create the customizer for the command. This uses a hack through the PrivilegedAction.
     * If the returned object is UserCancelException, the command is canceled.
     */
    public Object run() {
        Command cmd = getCommand();
        if (!(cmd instanceof VcsDescribedCommand)) {
            throw new IllegalArgumentException("Command "+cmd+" is not an instance of VcsDescribedCommand!");
        }
        Object ret = null;
        boolean customizationBroken = true;
        try {
            ret = doCustomization(cmd.isGUIMode(), (VcsDescribedCommand) cmd);
            customizationBroken = false;
        } finally {
            if (customizationBroken || (ret instanceof UserCancelException)) {
                ((CustomizationStatus) cmd).setCustomizationFailed(true);
            }
        }
        return ret;
    }

    private static final String QUOTING = "${QUOTE}"; // NOI18N

    /**
     * Perform the basic customization and preprocessing of the command.
     * This needs to be done even if a GUI customizer is not desired.
     */
    private Object doCustomization(boolean doCreateCustomizer, VcsDescribedCommand cmd) {
        //isCommandCustomized = true;
        Table files = getFilesToActOn(cmd);
        //System.out.println("\ndoCustomization("+doCreateCustomizer+", "+cmd.getVcsCommand()+"), files = "+files);
        if (files.size() == 0) {
            // In case, that sb. executes a command, that is either partially customized
            // or does not need any files. No variables will be set from files then.
            // This will happen when e.g.:
            // VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(logCmd, vars);
            // fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
            files = null;
        }
        VcsProvider provider = null;
        if (executionContext instanceof VcsProvider) provider = (VcsProvider) executionContext;
        if ("LIST".equals(cmd.getName()) && provider != null && provider.isOffLine() &&
            provider.getCommand(org.netbeans.modules.vcscore.commands.VcsCommand.NAME_REFRESH +
                                org.netbeans.modules.vcscore.commands.VcsCommand.NAME_SUFFIX_OFFLINE) == null) {

            // TODO move to VcsUtilities, DLG_RefreshCommandDisabled is used from two packages
            if (doCreateCustomizer && NotifyDescriptor.Confirmation.YES_OPTION.equals (
                DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Confirmation (
                    NbBundle.getMessage(UserCommandSupport.class,
                                        "DLG_RefreshCommandDisabled"), NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                provider.setOffLine(false);
            } else {
                return new UserCancelException();
            }
        }
        if (files != null && VcsCommandIO.getBooleanPropertyAssumeDefault(this.cmd, VcsCommand.PROPERTY_NEEDS_HIERARCHICAL_ORDER)) {
            files = createHierarchicalOrder(files);
        }
        boolean cmdCanRunOnMultipleFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(this.cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES);
        boolean cmdCanRunOnMultipleFilesInFolder = VcsCommandIO.getBooleanPropertyAssumeDefault(this.cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER);
        VariableValueAdjustment valueAdjustment = executionContext.getVarValueAdjustment();

        Object obj = doCustomization(doCreateCustomizer, null, cmd, files,
                                     valueAdjustment, cmdCanRunOnMultipleFiles,
                                     cmdCanRunOnMultipleFilesInFolder);
        if (obj == null) {
            commandCustomizedAndWillRun(cmd, executionContext);
        }
        //System.out.println("AFTER doCustomization("+doCreateCustomizer+", "+cmd.getVcsCommand()+"), files = "+files+", MODULE = "+cmd.getAdditionalVariables().get("MODULE")+", DIR = "+cmd.getAdditionalVariables().get("DIR"));
        return obj;
    }

    private Object doCustomization(boolean doCreateCustomizer,
                                   UserCommandCustomizer customizer,
                                   VcsDescribedCommand cmd,
                                   Table files,
                                   VariableValueAdjustment valueAdjustment,
                                   boolean cmdCanRunOnMultipleFiles,
                                   boolean cmdCanRunOnMultipleFilesInFolder) {
        //System.out.println("\ndoCustomization("+doCreateCustomizer+", "+customizer+", "+cmd+", "+files+", "+cmdCanRunOnMultipleFiles+", "+cmdCanRunOnMultipleFilesInFolder+")");
        boolean forEachFile[] = null;
        Map vars = executionContext.getVariableValuesMap();
        Map additionalVars = cmd.getAdditionalVariables();
        if (additionalVars != null) vars.putAll(additionalVars);
        setVariablesFromCommandInterfaces(cmd, vars);
        if (cmd.isExpertMode()) {
            vars.put(Variables.VAR_CTRL_DOWN_IN_ACTION, Boolean.TRUE.toString());
        }
        if (files != null && files.size() > 1) {
            forEachFile = new boolean[] { true };
        }
        return doCustomizationWithVars(doCreateCustomizer, customizer, cmd, files,
                                       valueAdjustment, vars, forEachFile,
                                       cmdCanRunOnMultipleFiles, cmdCanRunOnMultipleFilesInFolder);
    }

    private Object doCustomizationWithVars(boolean doCreateCustomizer,
                                   UserCommandCustomizer customizer,
                                   VcsDescribedCommand cmd,
                                   Table files,
                                   VariableValueAdjustment valueAdjustment,
                                   Map vars, boolean[] forEachFile,
                                   boolean cmdCanRunOnMultipleFiles,
                                   boolean cmdCanRunOnMultipleFilesInFolder) {
        //System.out.println("\ndoCustomization("+doCreateCustomizer+", "+customizer+", "+cmd+", "+files+", "+forEachFile+", "+cmdCanRunOnMultipleFiles+", "+cmdCanRunOnMultipleFilesInFolder+")");
        //Object customizer = null;
        VcsCommand vcsCmd = cmd.getVcsCommand();
        Table subFiles;
        if (files != null) {
            subFiles = setupRestrictedFileMap(files, vars, vcsCmd);
            setVariables(cmd.getVcsCommand(), subFiles, vars, valueAdjustment,
                         "", true);
        } else {
            subFiles = null;
        }
        //System.out.println("subFiles = "+subFiles+", files = "+files+", MODULE = "+vars.get("MODULE")+", DIR = "+vars.get("DIR"));
        //System.out.println("\nVARS for cmd = "+cmd+" ARE:"+vars+"\n");
        String commandExec = (String) vcsCmd.getProperty(VcsCommand.PROPERTY_EXEC);
        StructuredExec structuredExec = (StructuredExec) vcsCmd.getProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED);
        boolean success;
        if (doCreateCustomizer) {
            // Just a confirmation dialog is displayed if there is any
            // Skip this in non-GUI mode
            success = CommandCustomizationSupport.preCustomize(executionContext, vcsCmd, vars);
        } else {
            success = true;
        }
        if (success) {
            // Have the global options defined
            CommandCustomizationSupport.defineGlobalOptions(vars, executionContext, vcsCmd);
        }
        if (!success) return new UserCancelException();
        Object finalCustomizer = null;
        if (commandExec != null || structuredExec != null) {
            if (doCreateCustomizer) {
                finalCustomizer = createCustomizer(customizer, commandExec, vars, forEachFile,
                                                   cmd, files, valueAdjustment,
                                                   cmdCanRunOnMultipleFiles,
                                                   cmdCanRunOnMultipleFilesInFolder);
                if (finalCustomizer instanceof UserCommandCustomizer) {
                    customizer = (UserCommandCustomizer) finalCustomizer;
                } else customizer = null;
            } else {
                try {
                    if (structuredExec != null) {
                        StructuredExec.Argument[] args = structuredExec.getArguments();
                        for (int i = 0; i < args.length; i++) {
                            CommandCustomizationSupport.setupUncustomizedCommand(executionContext, args[i].getArgument(), vars, vcsCmd);
                        }
                    } else {
                        CommandCustomizationSupport.setupUncustomizedCommand(executionContext, commandExec, vars, vcsCmd);
                    }
                } catch (UserCancelException ucex) {
                    return ucex;
                }
            }
        }
        //if (newExec != null) cmd.setPreferredExec(newExec);
        cmd.setAdditionalVariables(vars);
        //System.out.println("subFiles = "+subFiles+", files = "+files+", MODULE = "+cmd.getAdditionalVariables().get("MODULE")+", DIR = "+cmd.getAdditionalVariables().get("DIR"));
        if (finalCustomizer == null && files != null) {
            VcsDescribedCommand lastCmd = cmd;
            if (!cmdCanRunOnMultipleFiles || cmdCanRunOnMultipleFilesInFolder) {
                lastCmd = createNextCustomizedCommand(cmd, subFiles,
                                                      valueAdjustment, vars,
                                                      cmdCanRunOnMultipleFiles,
                                                      cmdCanRunOnMultipleFilesInFolder);
            }

            if (files == subFiles) {
                files.clear();
            } else {
                synchronized(subFiles) {
                    Set keys = subFiles.keySet();
                    synchronized(keys) {
                        for (Iterator it = keys.iterator(); it.hasNext(); ) {
                            files.remove(it.next());
                        }
                    }
                }
            }

            // If there is no customizer, so let's continue with the rest of the files
            if (files.size() > 0 && lastCmd != null) {
                VcsDescribedCommand nextCmd = createNextCommand(files, lastCmd);
                // Do not attempt to create a customizer again if it was already null
                doCustomizationWithVars(false, null, nextCmd, files,   // recursion
                                valueAdjustment, new Hashtable(vars), forEachFile,
                                cmdCanRunOnMultipleFiles, cmdCanRunOnMultipleFilesInFolder);
            }
        }
        ((CustomizationStatus) cmd).setAlreadyCustomizedUserCommand(true);
        //System.out.println("\nUserCommandSupport.doCustomization("+doCreateCustomizer+") = "+customizer);
        return finalCustomizer;
    }

    private VcsDescribedCommand createNextCustomizedCommand(VcsDescribedCommand cmd,
                                                            Table files,
                                                            VariableValueAdjustment valueAdjustment,
                                                            Map vars,
                                                            boolean cmdCanRunOnMultipleFiles,
                                                            boolean cmdCanRunOnMultipleFilesInFolder) {
        //System.out.println("createNextCustomizedCommand("+cmd+", "+files+")");
        Table subFiles = setupRestrictedFileMap(files, cmdCanRunOnMultipleFiles,
                                                cmdCanRunOnMultipleFilesInFolder);
        setVariables(cmd.getVcsCommand(), subFiles, vars, valueAdjustment,
                     "", true);
        cmd.setAdditionalVariables(vars);
        boolean anyWereSet = setCommandFilesFromTable(cmd, subFiles, executionContext);
        if (!anyWereSet) return cmd;
        // Suppose, that the command is already preprocessed.
        //System.out.println("RestrictedFileMap = "+subFiles+", files = "+files+", MODULE = "+cmd.getAdditionalVariables().get("MODULE")+", DIR = "+cmd.getAdditionalVariables().get("DIR"));
        //System.out.println("\nVARS for cmd = "+cmd+" ARE:"+vars+"\n");
        if (files.size() != subFiles.size()) {
            Table remaining = new Table();
            for (Enumeration en = files.keys(); en.hasMoreElements(); ) {
                Object singleFile = en.nextElement();
                if (!subFiles.containsKey(singleFile)) {
                    remaining.put(singleFile, files.get(singleFile));
                }
            }
            VcsDescribedCommand nextCmd = createNextCommand(remaining, cmd);
            Hashtable newVars = new Hashtable(vars);
            VcsDescribedCommand nextCustomizedCommand =
                   createNextCustomizedCommand(nextCmd, remaining,   // recursion
                                               valueAdjustment, newVars,
                                               cmdCanRunOnMultipleFiles,
                                               cmdCanRunOnMultipleFilesInFolder);
            VcsCommand vcsCmd = nextCmd.getVcsCommand();
            //String newExec = CommandCustomizationSupport.preCustomize(executionContext, vcsCmd, newVars);
            //if (newExec != null) nextCmd.setPreferredExec(newExec);
            String commandExec = (String) vcsCmd.getProperty(VcsCommand.PROPERTY_EXEC);
            StructuredExec structuredExec = (StructuredExec) vcsCmd.getProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED);
            boolean success = CommandCustomizationSupport.preCustomize(executionContext, vcsCmd, vars);
            if (!success) return null;
            nextCmd.setAdditionalVariables(newVars);
            return nextCustomizedCommand;
        }
        //System.out.println("  return "+cmd);
        return cmd;
    }
    
    /**
     * Create a new command acting on the given files with the basic properties
     * inherited from the old command. It also sets the new command as the next
     * for the old one.
     */
    private VcsDescribedCommand createNextCommand(Table files, VcsDescribedCommand oldCommand) {
        VcsDescribedCommand command = (VcsDescribedCommand) oldCommand.clone();//createCommand();
        command.setAdditionalVariables(cloneFileArgs(command.getAdditionalVariables()));
        setCommandFilesFromTable(command, files, executionContext);
        command.setExpertMode(oldCommand.isExpertMode());
        command.setGUIMode(oldCommand.isGUIMode());
        //command.setAdditionalVariables(null); // re-set the map of additional variables
        oldCommand.setNextCommand(command);
        return command;
    }
    
    /**
     * Clone the file arguments for the next command, so that the same file is not reused.
     * Separate file argument is necessary for individual commands.
     */
    private static Map cloneFileArgs(Map vars) {
        String filesToCloneStr = (String) vars.get(VAR_ARGUMENT_FILES_TO_CLONE);
        if (filesToCloneStr == null) return vars;
        String[] filesToClone = VcsUtilities.getQuotedStrings(filesToCloneStr);
        for (int i = 0; i < filesToClone.length; i++) {
            String filePath = (String) vars.get(filesToClone[i]);
            if (filePath != null) {
                vars.put(filesToClone[i], cloneFile(filePath));
            }
        }
        return vars;
    }
    
    private static String cloneFile(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        File file2;
        java.io.InputStream in = null;
        java.io.OutputStream out = null;
        try {
            String name = file.getName();
            int dotpos = name.lastIndexOf('.');
            if (dotpos > 0) {
                name = name.substring(0, dotpos);
            }
            if (name.length() > 10) name = name.substring(0, 10); // To prevent from too long names (see issue #40269).
            file2 = File.createTempFile(name, null, parent);
            file2.deleteOnExit();
            in = new java.io.BufferedInputStream(new java.io.FileInputStream(file));
            out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file2));
            FileUtil.copy(in, out);
        } catch (java.io.IOException ioex) {
            file2 = file; // We did not succeed, leave the original file there.
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (java.io.IOException ioex) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (java.io.IOException ioex) {}
            }
        }
        return file2.getAbsolutePath();
    }
    
    /** Return the table of file names relative to provider root and associated
     * FileObjects.
     */
    private Table getFilesToActOn(Command cmd) {
        Table files = new Table();
        FileObject[] fos = cmd.getFiles();
        if (fos != null) {
            if (executionContext instanceof VcsProvider) {
                for (int i = 0; i < fos.length; i++) {
                    files.put(fos[i].getPath(), fos[i]);
                }
            } else {
                for (int i = 0; i < fos.length; i++) {
                    java.io.File diskFile = FileUtil.toFile(fos[i]);
                    if (diskFile != null) {
                        files.put(diskFile.getAbsolutePath(), fos[i]);
                    }
                }
            }
        }
        if (cmd instanceof VcsDescribedCommand) {
            java.io.File[] diskFiles = ((VcsDescribedCommand) cmd).getDiskFiles();
            if (diskFiles != null) {
                if (executionContext instanceof VcsProvider) {
                    String root = ((VcsProvider) executionContext).getRootDirectory().getAbsolutePath();
                    for (int i = 0; i < diskFiles.length; i++) {
                        String path = FileUtil.normalizeFile(diskFiles[0]).getAbsolutePath();
                        if (path.indexOf(root) == 0) {
                            path = path.substring(root.length());
                            while (path.startsWith(java.io.File.separator)) path = path.substring(1);
                        }
                        files.put(path.replace(java.io.File.separatorChar, '/'), null);
                    }
                } else {
                    for (int i = 0; i < diskFiles.length; i++) {
                        String path = diskFiles[0].getAbsolutePath();
                        files.put(path, null);
                    }
                }
            }
        }
        return files;
    }

    /**
     * @return Panel or Exception
     */
    private Object createCustomizer(UserCommandCustomizer customizer,
                                    String newExec, final Map vars,
                                    final boolean[] forEachFile,
                                    final VcsDescribedCommand command,
                                    final Table files,
                                    final VariableValueAdjustment valueAdjustment,
                                    final boolean cmdCanRunOnMultipleFiles,
                                    final boolean cmdCanRunOnMultipleFilesInFolder) {
        StringBuffer title = new StringBuffer();
        final VariableInputDialog dlg;
        try {
            dlg = CommandCustomizationSupport.createInputDialog(executionContext, newExec, vars, command, forEachFile, title);
        } catch (UserCancelException ucex) {
            return ucex;
        }
        //System.out.println("\ncreateCustomizer("+customizer+", "+files+", "+forEachFile+"), dlg = "+dlg);
        if (dlg == null) return null;
        if (customizer == null) customizer = new UserCommandCustomizer(executionContext);
        final UserCommandCustomizer finalCustomizer = customizer;
        customizer.setCommand(command, dlg, title.toString());
        dlg.addCloseListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean isPromptForEachFile = dlg.getPromptForEachFile();
                if (dlg.isValidInput() && forEachFile != null) {
                    forEachFile[0] = isPromptForEachFile;
                    executionContext.setPromptForVarsForEachFile(forEachFile[0]);
                }
                //System.out.println("\n!!close listener: isPromptForEachFile = "+isPromptForEachFile);
                if (files == null) return ;
                if (isPromptForEachFile) {
                    Object singleFile = files.keys().nextElement();
                    Table subFiles = new Table();
                    subFiles.put(singleFile, files.get(singleFile));
                    setVariables(command.getVcsCommand(), subFiles, vars, valueAdjustment,
                                 "", true);
                    command.setAdditionalVariables(vars);
                    //System.out.println("RestrictedFileMap = "+subFiles+", files = "+files+", MODULE = "+command.getAdditionalVariables().get("MODULE")+", DIR = "+command.getAdditionalVariables().get("DIR"));
                    //System.out.println("\nVARS for cmd = "+command+" ARE:"+vars+"\n");
                    VcsDescribedCommand nextCmd = createNextCommand(files, command);
                    files.remove(singleFile);
                    doCustomization(true, finalCustomizer, nextCmd, files,
                                    valueAdjustment, cmdCanRunOnMultipleFiles,
                                    cmdCanRunOnMultipleFilesInFolder);
                } else {
                    VcsCommand vcsCmd = command.getVcsCommand();
                    Table subFiles = setupRestrictedFileMap(files, vars, vcsCmd);
                    VcsDescribedCommand lastCmd = command;
                    if (!cmdCanRunOnMultipleFiles || cmdCanRunOnMultipleFilesInFolder) {
                        lastCmd = createNextCustomizedCommand(command, subFiles,
                                                              valueAdjustment, vars,
                                                              cmdCanRunOnMultipleFiles,
                                                              cmdCanRunOnMultipleFilesInFolder);
                    }

                    if (files == subFiles) {
                        files.clear();
                    } else {
                        synchronized(subFiles) {
                            Set keys = subFiles.keySet();
                            synchronized(keys) {
                                for (Iterator it = keys.iterator(); it.hasNext(); ) {
                                    files.remove(it.next());
                                }
                            }
                        }
                    }
                    // I'm customized, but I have to setup commands for the rest of the files.
                    if (files.size() > 0 && lastCmd != null) {
                        VcsDescribedCommand nextCmd = createNextCommand(files, lastCmd);
                        // Do not attempt to create a customizer again
                        doCustomizationWithVars(false, null, nextCmd, files,
                                        valueAdjustment, new Hashtable(vars), forEachFile,
                                        cmdCanRunOnMultipleFiles, cmdCanRunOnMultipleFilesInFolder);
                    }
                    //commandCustomizedAndWillRun(finalCustomizer.getOriginalCommand());
                }
            }
        });
        //System.out.println("createCustomizer() = "+customizer);
        return customizer;
    }
    
    /**
     * Called <b>after</b> the customizer is finished and the command is going
     * to be scheduled for execution.
     */
    static boolean commandCustomizedAndWillRun(VcsDescribedCommand cmd, CommandExecutionContext executionContext) {
        boolean successfull = true;
        do {
            //System.out.println("commandCustomizedAndWillRun("+cmd.getName()+")");
            VcsCommand vcsCmd = cmd.getVcsCommand();
            String commandExec = (String) vcsCmd.getProperty(VcsCommand.PROPERTY_EXEC);
            StructuredExec structuredExec = (StructuredExec) vcsCmd.getProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED);
            boolean success = true;
            if (structuredExec != null) {
                Hashtable vars = new Hashtable(cmd.getAdditionalVariables());
                structuredExec = CommandCustomizationSupport.preCustomizeStructuredExec(executionContext, vcsCmd, vars);
                success = structuredExec != null;
                if (success) {
                    cmd.setPreferredStructuredExec(structuredExec);
                    cmd.setAdditionalVariables(vars);
                }
            } else if (commandExec != null) {
                Hashtable vars = new Hashtable(cmd.getAdditionalVariables());
                commandExec = CommandCustomizationSupport.preCustomizeExec(executionContext, vcsCmd, vars);
                success = commandExec != null;
                if (success) {
                    cmd.setPreferredExec(commandExec);
                    cmd.setAdditionalVariables(vars);
                }
            }
            //String newExec = CommandCustomizationSupport.preCustomize(executionContext, vcsCmd, vars);
            //if (commandExec != null && newExec == null) return new UserCancelException();
            if (!success) successfull = false;
        } while((cmd = (VcsDescribedCommand) cmd.getNextCommand()) != null);
        return successfull;
    }
    
    /**
     * Get the class of the action.
     */
    public Class getActionClass() {
        Class actionClass = (Class) cmd.getProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass"); // NOI18N
        if (actionClass == null) {
            Object actionClassNameObj = cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_CLASS_NAME);
            if (actionClassNameObj instanceof String) {
                String actionClassName = (String) actionClassNameObj;
                try {
                    actionClass = Class.forName(actionClassName, false,
                                                VcsUtilities.getSFSClassLoader());
                } catch (ClassNotFoundException e) {
                    ErrorManager.getDefault().notify(
                        ErrorManager.getDefault().annotate(e,
                            NbBundle.getMessage(UserCommandSupport.class, "EXC_CouldNotFindAction",
                            actionClassName)));
                }
            }
            if (actionClass != null) cmd.setProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass", actionClass); // NOI18N
        }
        return actionClass;
    }
    
    /**
     * Get the display name of the action.
     */
    public String getActionDisplayName() {
        return (String) cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_DISPLAY_NAME);
    }
    
    
    // Setup stuff:
    
    /**
     * Set the files as arguments to the command. All FileObjects from the table
     * are set via setFiles() method, all other are converted to java.io.Files
     * through provider.getFile(name) and set to the command via setDiskFiles()
     * method.
     * @param command The command to set the files on.
     * @param files The table of file names and associated FileObjects.
     * @param provider The provider to get the java.io.Files from.
     *        Can be <code>null</code>, in which case java.io.Files are created
     *        directly from the file name.
     * @return true when the files were successfully set, false when there are no
     *         applicable files to be set.
     */
    public static boolean setCommandFilesFromTable(Command command, Table files, CommandExecutionContext executionContext) {
        ArrayList diskFiles = new ArrayList();
        ArrayList foFiles = new ArrayList();
        for (Iterator it = files.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            FileObject fo = (FileObject) files.get(name);
            if (fo != null) {
                foFiles.add(fo);
            } else {
                if (executionContext instanceof VcsProvider) {
                    diskFiles.add(((VcsProvider) executionContext).getFile(name));
                } else {
                    diskFiles.add(new java.io.File(name));
                }
            }
        }
        FileObject[] fos = (FileObject[]) foFiles.toArray(new FileObject[foFiles.size()]);
        fos = command.getApplicableFiles(fos);
        if (fos != null) command.setFiles(fos);
        if (command instanceof VcsDescribedCommand) {
            VcsDescribedCommand dcmd = (VcsDescribedCommand) command;
            if (diskFiles.size() > 0) {
                dcmd.setDiskFiles((java.io.File[]) diskFiles.toArray(new java.io.File[diskFiles.size()]));
            }
        }
        return fos != null || diskFiles.size() > 0;
    }
    
    /** Reorder the table of files by the path hierarchical order.
     * @param files the table of pairs of files and file objects
     * @return the reordered table
     */
    private static Table createHierarchicalOrder(Table files) {
        TreeMap sorted = new TreeMap(files);
        Table sortedFiles = new Table();
        for (Iterator it = sorted.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            sortedFiles.put(key, files.get(key));
        }
        return sortedFiles;
    }
    
    private static Table setupRestrictedFileMap(Table files, boolean cmdCanRunOnMultipleFiles,
                                                boolean cmdCanRunOnMultipleFilesInFolder) {
        Table subFiles = new Table();
        if (!cmdCanRunOnMultipleFiles && !cmdCanRunOnMultipleFilesInFolder) {
            Object singleFile = files.keys().nextElement();
            subFiles.put(singleFile, files.get(singleFile));
        } else if (cmdCanRunOnMultipleFilesInFolder) {
            Enumeration keys = files.keys();
            String file = (String) keys.nextElement();
            subFiles.put(file, files.get(file));
            //String folder = file.getPackageName('/');
            String folder = ""; // NOI18N
            int index = file.lastIndexOf('/');
            if (index >= 0) folder = file.substring(0, index);
            while (keys.hasMoreElements()) {
                file = (String) keys.nextElement();
                String testFolder = ""; // NOI18N
                index = file.lastIndexOf('/');
                if (index >= 0) testFolder = file.substring(0, index);
                if (folder.equals(testFolder)) {
                    subFiles.put(file, files.get(file));
                }
            }
        }
        return subFiles;
    }

    private static Table setupRestrictedFileMap(Table files, Map vars, VcsCommand cmd) {
        String[] attrsToVars = (String[]) cmd.getProperty(VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS);
        if (attrsToVars != null) {
            files = getAttributeRestrictedFileMap(files, vars, cmd, attrsToVars);
        }
        if (Boolean.TRUE.equals(cmd.getProperty(VcsCommand.PROPERTY_DISTINGUISH_BINARY_FILES))) {
            files = getBinaryRestrictedFileMap(files, vars, cmd);
        }
        return files;
    }
    
    private static Table getAttributeRestrictedFileMap(Table files, Map vars, VcsCommand cmd, String[] attrsToVars) {
        String[] attrNames;
        Map attrNonNullVars = null;
        Map attrValueVars = null;
        attrNames = (String[]) cmd.getProperty(PROPERTY_PARSED_ATTR_NAMES);
        if (attrNames != null) {
            attrNonNullVars = (Map) cmd.getProperty(PROPERTY_PARSED_ATTR_NEMPTY_VARS);
            attrValueVars = (Map) cmd.getProperty(PROPERTY_PARSED_ATTR_VALUES_VARS);
        } else {
            attrNonNullVars = new HashMap();
            attrValueVars = new HashMap();
            attrNames = getAttrNamesAndVars(attrNonNullVars, attrValueVars, attrsToVars);
            cmd.setProperty(PROPERTY_PARSED_ATTR_NAMES, attrNames);
            cmd.setProperty(PROPERTY_PARSED_ATTR_NEMPTY_VARS, attrNonNullVars);
            cmd.setProperty(PROPERTY_PARSED_ATTR_VALUES_VARS, attrValueVars);
        }
        Table subFiles;
        if (attrNames != null) {
            subFiles = setVarsFromAttrs(files, vars, attrNames, attrNonNullVars, attrValueVars);
        } else {
            subFiles = files;
        }
        return subFiles;
    }
    
    /**
     * Parse the array of attribute names and variable names into the array
     * of attribute names and two maps with variable names.
     * @param attrNonNullVars this map is filled with pairs of attribute name
     *        and the variable name, that is set when the attribute value is
     *        <code>null</code>.
     * @param attrValueVars this map is filled with pairs of attribute name
     *        and the variable name, that is set to the string representation
     *        of the attribute value.
     * @param attrsToVars the array of attribute names and variable names
     *        as described at {@link VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS}.
     *        This array can be <code>null</code>.
     */
    private static String[] getAttrNamesAndVars(Map attrNonNullVars, Map attrValueVars, String[] attrsToVars) {
        String[] attrNames = new String[attrsToVars.length / 3];
        for (int i = 0; i < attrsToVars.length - 2; i += 3) {
            String attrName = attrsToVars[i];
            attrNames[i/3] = attrName;
            attrNonNullVars.put(attrName, attrsToVars[i + 1]);
            attrValueVars.put(attrName, attrsToVars[ i + 2]);
        }
        return attrNames;
    }
    
    private static Table getBinaryRestrictedFileMap(Table files, Map vars, VcsCommand cmd) {
        Table restrictedFiles = new Table();
        boolean isBinary;
        Iterator it = files.keySet().iterator();
        if (!it.hasNext()) return files;
        String name = (String) it.next();
        FileObject fo = (FileObject) files.get(name);
        if (fo == null) {
            isBinary = false;
        } else {
            isBinary = isFOBinary(fo);
        }
        restrictedFiles.put(name, fo);
        while (it.hasNext()) {
            name = (String) it.next();
            fo = (FileObject) files.get(name);
            if (fo == null) {
                if (isBinary) continue;
            } else {
                if (isBinary != isFOBinary(fo)) continue;
            }
            restrictedFiles.put(name, fo);
        }
        vars.put("PROCESSING_BINARY_FILES", isBinary ? Boolean.TRUE.toString() : ""); // NOI18N
        return restrictedFiles;
    }
    
    private static boolean isFOBinary(FileObject fo) {
        String mimeType = fo.getMIMEType();
        return !mimeType.startsWith("text") && !"content/unknown".equals(mimeType); // NOI18N
    }
    
    /**
     * Set the variables from files attributes.
     * @param files the table of files
     * @param vars the map of variables, where the new variables will be filled
     * @param attrsToVars the array of attribute names and variable names
     *        as described at {@link VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS}.
     *        This array can be <code>null</code>.
     * @return the files for which the attributes were successfully converted
     * to variable values and the values were the same for all of them.
     */
    private static Table setVarsFromAttrs(Table files, Map vars, String[] attrNames,
                                          Map attrNonNullVars, Map attrValueVars) {
        if (attrNames.length == 0) return files;
        Table result = new Table();
        Object[] attrs = getAttributes(result, files, attrNames);
        for (int i = 0; i < attrs.length; i++) {
            vars.put(attrNonNullVars.get(attrNames[i]), (attrs[i] != null) ? Boolean.TRUE.toString() : ""); // NOI18N
            if (attrs[i] != null) vars.put(attrValueVars.get(attrNames[i]), attrs[i].toString());
            else vars.remove(attrValueVars.get(attrNames[i]));
        }
        return result;
    }
    
    /**
     * Get the file attribute values.
     * @param result the table filled with files, which have the same attribute values
     * @param files the table of files
     * @param attrNames the array of file attribute names
     * @return the array of attribute values
     */
    private static Object[] getAttributes(Table result, Table files, String[] attrNames) {
        ArrayList values = new ArrayList();
        Iterator it = files.keySet().iterator();
        if (!it.hasNext()) return values.toArray();
        String name = (String) it.next();
        FileObject fo = (FileObject) files.get(name);
        if (fo == null) {
            values.addAll(Collections.nCopies(attrNames.length, null));
        } else {
            for (int i = 0; i < attrNames.length; i++) {
                values.add(fo.getAttribute(attrNames[i]));
            }
        }
        result.put(name, fo);
        while (it.hasNext()) {
            name = (String) it.next();
            fo = (FileObject) files.get(name);
            if (fo == null) {
                if (!Arrays.equals(values.toArray(), Collections.nCopies(attrNames.length, null).toArray()))
                    continue;
            } else {
                int i;
                for (i = 0; i < attrNames.length; i++) {
                    Object value = values.get(i);
                    Object attr = fo.getAttribute(attrNames[i]);
                    if (!(value == null && attr == null ||
                          value != null && value.equals(attr)))
                        break;
                }
                if (i < attrNames.length) continue;
            }
            result.put(name, fo);
        }
        return values.toArray();
    }

    /**
     * Add files specific variables. Turbo compatible.
     * The following variables are added:
     * <br>PATH - the full path to the first file from the provider root
     * <br>ABSPATH - the absolute path to the first file
     * <br>DIR - the directory of the first file from the provider root
     * <br>FILE - the first file
     * <br>QFILE - the first file quoted
     * <br>MIMETYPE - the MIME type of the first file
     * <br>
     * <br>FILES - all files delimeted by the system file separator
     * <br>QFILES - all files quoted by provider quotation string and delimeted by spaces
     * <br>PATHS - full paths to all files delimeted by two system file separators
     * <br>QPATHS - full paths to all files quoted by provider quotation string and delimeted by spaces
     * <br>QABSPATHS - absolute paths to all files quoted by provider quotation string and delimeted by spaces
     * <br>NUM_FILES - the number of files
     * <br>MULTIPLE_FILES - "true" when more than one file is to be processed, "" otherwise
     * <br>CACHED_ISLOCAL - "true" when file is local
     * <br>CACHED_SIZE
     * <br>CACHED_ATTR
     * <br>COMMON_PARENT - the greatest common parent of provided files. If defined,
     *                     all file paths change to be relative to this common parent
     *
     * @param command variables target command
     * @param files the table of files
     * @param vars the table of variables to extend
     * @param valueAdjustment the variable value adjustment utility object
     * @param useGreatestParentPaths whether to define COMMON_PARENT variable and
     *        change the file paths to be relative to this greatest common parent
     */
    public static void setVariables(VcsCommand command, Table files, Map vars,
                                    VariableValueAdjustment valueAdjustment,
                                    String relativeMountPoint,
                                    boolean useGreatestParentPaths) {
        // At first, find the greatest parent
        String greatestParent;
        if (useGreatestParentPaths) {
            greatestParent = findGreatestParent(files);
            if (greatestParent != null && greatestParent.length() == 0) {
                greatestParent = null;
            }
        } else {
            greatestParent = null;
        }
        // Then, find the first file and set the variables
        String rootDir = (String) vars.get("ROOTDIR");
        String separator = (String) vars.get("PS"); // NOI18N
        char separatorChar = (separator != null && separator.length() == 1) ? separator.charAt(0) : java.io.File.separatorChar;
        String fullName = (String) files.keys().nextElement();
        String absFullName = (rootDir != null) ? rootDir + ((fullName.length() > 0) ? separatorChar + fullName : "") : fullName;
        FileObject fo = (FileObject) files.get(fullName);
        boolean isFileFolder = (fo != null && fo.isFolder());
        String origFullName = fullName;
        if (greatestParent != null) {
            fullName = fullName.substring(greatestParent.length());
            while (fullName.startsWith("/")) fullName = fullName.substring(1);
        }
        String path = VcsUtilities.getDirNamePart(fullName);
        String file = VcsUtilities.getFileNamePart(fullName);
        path = path.replace('/', separatorChar);
        fullName = fullName.replace('/', separatorChar);
        absFullName = absFullName.replace('/', separatorChar);
        file = valueAdjustment.adjustVarValue(file);
        path = valueAdjustment.adjustVarValue(path);
        fullName = valueAdjustment.adjustVarValue(fullName);
        absFullName = valueAdjustment.adjustVarValue(absFullName);
        if (fullName.length() == 0) fullName = "."; // NOI18N
        String module = relativeMountPoint;//(String) vars.get("MODULE"); // NOI18N
        if (module == null) module = ""; // NOI18N
        if (greatestParent != null) {
            if (module.length() > 0) module += separatorChar;
            module += greatestParent;
            //vars.put("MODULE", module);
        }
        if (module.length() > 0) {
            module = module.replace('/', separatorChar);
            module = valueAdjustment.adjustVarValue(module);
        }
        String quoting = QUOTING;
        vars.put("MODULE", module);
        vars.put("PATH", fullName); // NOI18N
        vars.put("QPATH", (fullName.length() > 0) ? quoting+fullName+quoting : fullName); // NOI18N
        vars.put("ABSPATH", absFullName); // NOI18N
        vars.put("QABSPATH", (absFullName.length() > 0) ? quoting+absFullName+quoting : absFullName); // NOI18N
        vars.put("DIR", path); // NOI18N
        if (path.length() == 0 && file.length() > 0 && file.charAt(0) == '/') file = file.substring (1, file.length ());
        vars.put("FILE", file); // NOI18N
        vars.put("QFILE", quoting+file+quoting); // NOI18N
        if (fo != null) {
            vars.put("MIMETYPE", fo.getMIMEType()); // NOI18N
        } else {
            int extIndex = file.lastIndexOf('.');
            String ext = (extIndex >= 0 && extIndex < file.length() - 1) ? file.substring(extIndex + 1) : ""; // NOI18N
            String mime = FileUtil.getMIMEType(ext);
            if (mime != null) vars.put("MIMETYPE", mime); // NOI18N
        }

        // XXX following properties are not for sure needed for list commands
        if (command.getName().startsWith("LIST") == false) {  // NOI18N
            FileProperties fprops = Turbo.getMeta(fo);
            if (fprops != null) {
                if (fprops.getAttr() != null) {
                    vars.put("CACHED_ATTR", fprops.getAttr());
                } else {
                    vars.remove("CACHED_ATTR");
                }
                vars.put("CACHED_SIZE", Long.toString(fprops.getSize()));
                vars.put("CACHED_ISLOCAL", fprops.isLocal() ? "true" : "");
                String revision = fprops.getRevision();
                if (revision != null) vars.put("CACHED_REVISION", revision);
            } else {
                vars.remove("CACHED_ATTR");
                vars.remove("CACHED_SIZE");
                vars.remove("CACHED_ISLOCAL");
                vars.remove("CACHED_REVISION");
            }
        }

        vars.put("FILE_IS_FOLDER", (isFileFolder) ? Boolean.TRUE.toString() : "");// the FILE is a folder // NOI18N
        // Second, set the multifiles variables
        StringBuffer qpaths = new StringBuffer();
        StringBuffer qabspaths = new StringBuffer();
        StringBuffer paths = new StringBuffer();
        StringBuffer mpaths = new StringBuffer();
        StringBuffer qmpaths = new StringBuffer();
        StringBuffer vfiles = new StringBuffer();
        StringBuffer qfiles = new StringBuffer();
        int nVARS = 6;
        int nFILES = files.size();
        int[][] fileIndexes = new int[6][nFILES];
        // The array is automatically initialized with zeros.
        int iFile = 0;
        for (Enumeration en = files.keys(); en.hasMoreElements(); iFile++) {
            fullName = (String) en.nextElement();
            absFullName = (rootDir != null) ? rootDir + ((fullName.length() > 0) ? separatorChar + fullName : "") : fullName;
            fo = (FileObject) files.get(fullName);
            origFullName = fullName;
            if (greatestParent != null) {
                fullName = fullName.substring(greatestParent.length());
                while (fullName.startsWith("/")) fullName = fullName.substring(1);
            }
            if (fullName.length() == 0) fullName = "."; // NOI18N
            isFileFolder |= (fo != null && fo.isFolder());
            file = VcsUtilities.getFileNamePart(fullName);
            fullName = fullName.replace('/', separatorChar);
            absFullName = absFullName.replace('/', separatorChar);
            file = valueAdjustment.adjustVarValue(file);
            fullName = valueAdjustment.adjustVarValue(fullName);
            absFullName = valueAdjustment.adjustVarValue(absFullName);
            fileIndexes[0][iFile] = vfiles.length();
            fileIndexes[1][iFile] = qfiles.length();
            fileIndexes[2][iFile] = paths.length();
            fileIndexes[3][iFile] = qpaths.length();
            fileIndexes[4][iFile] = mpaths.length();
            fileIndexes[5][iFile] = qmpaths.length();
            vfiles.append(file);
            vfiles.append(separatorChar);
            qfiles.append(quoting);
            qfiles.append(file);
            qfiles.append(quoting);
            qfiles.append(" "); // NOI18N
            paths.append(fullName);
            paths.append(""+separatorChar+separatorChar); // NOI18N
            qpaths.append(quoting);
            qpaths.append(fullName);
            qpaths.append(quoting);
            qpaths.append(" "); // NOI18N
            qabspaths.append(quoting);
            qabspaths.append(absFullName);
            qabspaths.append(quoting);
            qabspaths.append(" "); // NOI18N
            String mpath;
            if (module == null || module.length() == 0) {
                mpath = fullName;
            } else {
                if (".".equals(fullName)) {
                    mpath = module;
                } else {
                    mpath = module + separatorChar + fullName;
                }
            }
            mpaths.append(mpath);
            mpaths.append(" "); // NOI18N
            qmpaths.append(quoting);
            qmpaths.append(mpath);
            qmpaths.append(quoting);
            qmpaths.append(" "); // NOI18N
        }
        vars.put("FILES", vfiles.delete(vfiles.length() - 1, vfiles.length()).toString()); // NOI18N
        vars.put("QFILES", qfiles.toString().trim()); // NOI18N
        vars.put("PATHS", paths.delete(paths.length() - 2, paths.length()).toString()); // NOI18N
        vars.put("QPATHS", qpaths.toString().trim()); // NOI18N
        vars.put("QABSPATHS", qabspaths.toString().trim()); // NOI18N
        vars.put("MPATHS", mpaths.toString().trim()); // NOI18N
        vars.put("QMPATHS", qmpaths.toString().trim()); // NOI18N
        try {
            vars.put("FILES_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[0]));
            vars.put("QFILES_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[1]));
            vars.put("PATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[2]));
            vars.put("QPATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[3]));
            vars.put("MPATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[4]));
            vars.put("QMPATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[5]));
        } catch (java.io.IOException ioex) {}
        vars.put("NUM_FILES", ""+files.size()); // NOI18N
        vars.put("MULTIPLE_FILES", (files.size() > 1) ? Boolean.TRUE.toString() : ""); // NOI18N
        vars.put("FILES_IS_FOLDER", (isFileFolder) ? Boolean.TRUE.toString() : "");// among FILES there is a folder // NOI18N
        if (greatestParent != null) {
            greatestParent = greatestParent.replace('/', separatorChar);
            greatestParent = valueAdjustment.adjustVarValue(greatestParent);
            vars.put("COMMON_PARENT", greatestParent);
        } else {
            vars.remove("COMMON_PARENT");
        }
        /*
        System.out.println("VARIABLES set after setVariables() (greatestParent = "+greatestParent+")");
        for (Iterator it = new java.util.TreeSet(vars.keySet()).iterator(); it.hasNext(); ) {
            String varName = (String) it.next();
            System.out.println("  "+varName+" = '"+vars.get(varName)+"'");
        }
        System.out.println("");
         */
    }
    
    /**
     * Find the greatest parent folder of files.
     */
    private static String findGreatestParent(Table files) {
        String greatestParent = null;
        for (Enumeration en = files.keys(); en.hasMoreElements(); ) {
            String fullName = (String) en.nextElement();
            String parent = VcsUtilities.getDirNamePart(fullName);
            //System.out.println("findGreatestParent: fullName = '"+fullName+"', parent = '"+parent+"', prev greatestParent = '"+greatestParent+"'");
            if (greatestParent == null) {
                greatestParent = parent;
            } else {
                if (!parent.startsWith(greatestParent) || (parent.length() > greatestParent.length() && parent.charAt(greatestParent.length()) != '/')) {
                    StringBuffer commonParent = new StringBuffer();
                    for (int i = 0; i < parent.length() && i < greatestParent.length(); i++) {
                        char c = parent.charAt(i);
                        if (greatestParent.charAt(i) != c) break;
                        commonParent.append(c);
                    }
                    // It can happen, that I end up in a middle of a folder name
                    // (e.g. "mySources" and "myLibraries" will end up with "my" which is not a folder name)
                    int end = commonParent.length();
                    if (!((parent.length() == end || parent.charAt(end) == '/') &&
                          (greatestParent.length() == end || greatestParent.charAt(end) == '/'))) {
                        // I'm somewhere in the middle of a folder name!
                        for (int i = end - 1; i >= 0; i--) {
                            char c = commonParent.charAt(i);
                            commonParent.deleteCharAt(i);
                            if (c == '/') break;
                        }
                    }
                    greatestParent = commonParent.toString();
                }
            }
            //System.out.println("findGreatestParent: new greatestParent = '"+greatestParent+"'");
        }
        if (greatestParent != null) {
            while (greatestParent.endsWith("/")) {
                greatestParent = greatestParent.substring(0, greatestParent.length() - 1);
            }
        }
        return greatestParent;
    }
    
    private static void setVariablesFromCommandInterfaces(Command cmd, Map vars) {
        Class[] interfaces = cmd.getClass().getInterfaces();
        setVariablesFromCommandInterfaces(cmd, interfaces, vars);
    }
    
private static void setVariablesFromCommandInterfaces(Command cmd, Class[] interfaces, Map vars) {
        for (int i = 0; i < interfaces.length; i++) {
            if (Command.class.equals(interfaces[i]) ||
                VcsDescribedCommand.class.equals(interfaces[i]) ||
                CustomizationStatus.class.equals(interfaces[i])) continue;
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(interfaces[i]);
                PropertyDescriptor[] propDescrs = beanInfo.getPropertyDescriptors();
                for (int j = 0; j < propDescrs.length; j++) {
                    String name = propDescrs[j].getName();
                    if (vars.get(name) != null) {
                        // The var is already defined - ignoring, do not want to reset
                        continue;
                    }
                    Object value = propDescrs[j].getReadMethod().invoke(cmd, new Object[0]);
                    if (value != null) {
                        if (value instanceof Boolean) {
                            vars.put(name, ((Boolean) value).booleanValue() ? "true" : "");
                        } else {
                            vars.put(name, value.toString());
                        }
                    }
                }
            } catch (IntrospectionException iex) {
            } catch (IllegalAccessException iaex) {
            } catch (IllegalArgumentException iarex) {
            } catch (InvocationTargetException itex) {
            }
            Class[] subinterfaces = interfaces[i].getInterfaces();
            setVariablesFromCommandInterfaces(cmd, subinterfaces, vars);
        }
    }
    
    /**
     * This interface represents the customization status of the command.
     */
    private static interface CustomizationStatus extends Command {
        
        public boolean isAlreadyCustomizedUserCommand();
        
        public void setAlreadyCustomizedUserCommand(boolean customized);
        
        public boolean isCustomizationFailed();
        
        public void setCustomizationFailed(boolean failed);
        
    }
}
