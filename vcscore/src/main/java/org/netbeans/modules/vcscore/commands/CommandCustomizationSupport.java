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

package org.netbeans.modules.vcscore.commands;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import org.netbeans.api.queries.SharabilityQuery;

import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.UserCancelException;

import org.netbeans.modules.vcscore.VcsProvider;
//import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.util.VariableInputDescriptor;
import org.netbeans.modules.vcscore.util.VariableInputComponent;
import org.netbeans.modules.vcscore.util.VariableInputDialog;
import org.netbeans.modules.vcscore.util.VariableInputFormatException;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.NotifyDescriptorInputPassword;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

/**
 * This class contains a support for VCS commands customization.
 *
 * @author  Martin Entlicher
 */
public class CommandCustomizationSupport extends Object {

    /**
     * The name of the variable, where the global input descriptor is stored.
     */
    public static final String GLOBAL_INPUT_DESCRIPTOR = "GLOBAL_INPUT_DESCRIPTOR";
    /**
     * The name of the variable, where the global input expression is stored. This expression
     * will be inserted to the execution string instead of ${USER_GLOBAL_PARAM}
     */
    public static final String GLOBAL_INPUT_EXPRESSION = "GLOBAL_INPUT_EXPRESSION";

    /**
     * The name of the variable for the global additional parameters.
     */
    private static final String USER_GLOBAL_PARAM = "USER_GLOBAL_PARAM";
    /**
     * The name of the variable for the local additional parameters.
     */
    private static final String USER_PARAM = "USER_PARAM";

    public static final String INPUT_DESCRIPTOR_PARSED = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "_INPUT_DESCRIPTOR_PARSED";

    public static final String INPUT_DESCRIPTOR_RESOURCE_BUNDLES = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "_INPUT_DESCRIPTOR_RESOURCE_BUNDLES";

    private static final String VAR_INPUT_MULTIPLE_FILES_TITLE_APPEND = " ...";
    private static final String VAR_INPUT_FILE_SEPARATOR = " - ";

    /** Creates new CommandCustomizationSupport */
    private CommandCustomizationSupport() {
    }

    /**
     * Find out, the number of important files among these paths.
     * @param paths the files paths delimited by double File.separator
     * @param ps the path separator
     * @return the number of important files
     */
    private static int numImportant(VcsProvider provider, String paths, String ps) {
        //System.out.println("numImportant("+paths+", "+ps+")");
        if (paths == null) return 0; // Just for robustness
        int num = 0;
        String delim;
        if (ps != null) {
            delim = ps+ps;
        } else {
            delim = java.io.File.separator + java.io.File.separator;
        }
        VariableValueAdjustment varValueAdjust = provider.getVarValueAdjustment();
        int begin = 0;
        int end = paths.indexOf(delim);
        if (end < 0) end = paths.length();
        while (true) {
            String path = paths.substring(begin, end);
            //System.out.println("  path = "+path);
            path = varValueAdjust.revertAdjustedVarValue(path);
            //System.out.println("  rev. = "+path);
            int sharability = SharabilityQuery.getSharability(provider.getFile(path));
            if (sharability != SharabilityQuery.NOT_SHARABLE) num++;
            begin = end + delim.length();
            if (begin > paths.length()) break;
            end = paths.indexOf(delim, begin);
            if (end < 0) end = paths.length();
        }
        return num;
    }

    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     * @param provider the vcs provider
     * @param doNotTestFS if true, FileObjects will not be tested whether they belong to VcsProvider
     */
    public static void addImportantFiles(Collection fos, Table res, boolean all, VcsProvider provider, boolean doNotTestFS) {
        for(Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject ff = (FileObject) it.next();
            if (!doNotTestFS && VcsProvider.getProvider(ff) != provider)
                continue;
            String fileName = ff.getPath();
            //VcsFile file = fileSystem.getCache().getFile(fileName);
            //D.deb("file = "+file+" for "+fileName);
            //if (file == null || file.isImportant()) {
            File file = FileUtil.toFile(ff);
            if (all || file == null || SharabilityQuery.getSharability(file) != SharabilityQuery.NOT_SHARABLE) {
                //D.deb(fileName+" is important");
                res.put(fileName, ff);
            }
            /*
            Set[] scheduled = (Set[]) ff.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            if (scheduled != null && scheduled[0] != null) {
                for (Iterator sit = scheduled[0].iterator(); sit.hasNext(); ) {
                    String name = (String) sit.next();
                    res.put(name, null);
                }
            }
             */
            //else D.deb(fileName+" is NOT important");
        }
    }

    /** Remove the files for which the command is disabled */
    private static Table removeDisabled(VcsProvider provider,
                                        Table files, VcsCommand cmd) {

        FileStatusProvider statusProvider = null;
        String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
        if (disabledStatus != null) {
            Table remaining = new Table();
            for (Enumeration en = files.keys(); en.hasMoreElements(); ) {
                String name = (String) en.nextElement();
                FileObject fo = provider.findResource(name);
                FileProperties fprops = Turbo.getMeta(fo);
                String status = FileProperties.getStatus(fprops);
                boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                    disabledStatus, Collections.singleton(status));
                if (!disabled) {
                    remaining.put(name, files.get(name));
                }
            }
            files = remaining;
        }

        boolean disabledWhenNotLocked = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_DISABLED_WHEN_NOT_LOCKED);
        String disabledWhenNotLockedConditionedStr = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_WHEN_NOT_LOCKED+"Conditioned");
        if (disabledWhenNotLocked || disabledWhenNotLockedConditionedStr != null) {
            Table remaining = new Table();
            Map vars = provider.getVariableValuesMap();
            String currentLocker = (String) vars.get(Variables.VAR_LOCKER_USER_NAME);
            if (currentLocker != null) {
                currentLocker = Variables.expand(vars, currentLocker, false);
            }
            if (disabledWhenNotLockedConditionedStr != null) {
                for (Enumeration enm = files.keys(); enm.hasMoreElements(); ) {
                    String name = (String) enm.nextElement();
                    Table varFiles = new Table();
                    varFiles.put(name, files.get(name));
                    Hashtable vvars = new Hashtable(vars);

                    // cache provider is not necessary in turbo mode
                    UserCommandSupport.setVariables(cmd, varFiles, vvars, provider.getVarValueAdjustment(),
                                                    "", true);
                    String disabledWhenNotLockedConditionedExp = Variables.expand(vvars, disabledWhenNotLockedConditionedStr, false);
                    disabledWhenNotLocked = "true".equalsIgnoreCase(disabledWhenNotLockedConditionedExp);
                    if (disabledWhenNotLocked) {
                        FileObject fo = provider.findResource(name);
                        FileProperties fprops = Turbo.getMeta(fo);
                        String locker = fprops != null ? fprops.getLocker() : null;
                        if (VcsUtilities.lockerMatch(locker, currentLocker)) {
                            remaining.put(name, files.get(name));
                        }
                    } else {
                        remaining.put(name, files.get(name));
                    }
                }
            } else {
                for (Enumeration enu = files.keys(); enu.hasMoreElements(); ) {
                    String name = (String) enu.nextElement();
                    FileObject fo = provider.findResource(name);
                    FileProperties fprops = Turbo.getMeta(fo);
                    String locker = fprops != null ? fprops.getLocker() : null;
                    if (VcsUtilities.lockerMatch(locker, currentLocker)) {
                        remaining.put(name, files.get(name));
                    }
                }
            }
            files = remaining;
        }
        return files;
    }

    public static FileObject[] getApplicableFiles(CommandExecutionContext executionContext, VcsCommand cmd, FileObject[] files) {
        VcsProvider provider;
        if (executionContext instanceof VcsProvider) {
            provider = (VcsProvider) executionContext;
        } else {
            provider = null;
        }
        boolean processAll = provider != null && (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES)
                                                  /*  || provider.isProcessUnimportantFiles() TODO expecting false*/);
        Collection fileObjects = new ArrayList();
        boolean isOnFiles = false;
        boolean isOnDirs = false;
        boolean isOnRoot = false;
        for (int i = 0; i < files.length; i++) {
            fileObjects.add(files[i]);
            if (files[i].isFolder()) isOnDirs = true;
            else isOnFiles = true;
            if (provider != null) {
                if (provider.getRoot().equals(files[i])) isOnRoot = true;
            } else {
                if (files[i].getParent() == null) isOnRoot = true;
            }
        }
        if (isOnRoot) isOnDirs = false;
        if (isOnDirs && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFolder()) fileObjects.remove(files[i]);
            }
        }
        if (isOnFiles && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isFolder()) fileObjects.remove(files[i]);
            }
        }
        if (isOnRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)) {
            for (int i = 0; i < files.length; i++) {
                if (provider != null) {
                    if (provider.getRoot().equals(files[i])) {
                        fileObjects.remove(files[i]);
                    }
                } else {
                    if (files[i].getParent() == null) {
                        fileObjects.remove(files[i]);
                    }
                }
            }
        }
        if (fileObjects.size() > 0) {
            if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)) {
                fileObjects.clear();
            }
            String hiddenTestExpression = (String) cmd.getProperty(VcsCommand.PROPERTY_HIDDEN_TEST_EXPRESSION);
            if (hiddenTestExpression != null) {
                Map variables = executionContext.getVariableValuesMap();
                if (Variables.expand(variables, hiddenTestExpression, false).trim().length() > 0) {
                    fileObjects.clear();
                }
            }
        }
        if (fileObjects.size() == 0) return (provider != null) ? null : new FileObject[0];
        //boolean refreshDone = false;
        Table filesTable = new Table();
        addImportantFiles(fileObjects, filesTable, processAll, provider, false);
        if (provider != null) {
            filesTable = removeDisabled(provider, filesTable, cmd);
        }
        if (filesTable.size() == 0) return (provider != null) ? null : new FileObject[0];
        FileObject[] applFiles = new FileObject[filesTable.size()];
        int i = 0;
        for (Iterator it = filesTable.keySet().iterator(); it.hasNext(); ) {
            String path = (String) it.next();
            FileObject file = (FileObject) filesTable.get(path);
            if (file == null) {
                file = new NonExistentFileObject(provider, path);
            }
            applFiles[i++] = file;
        }
        return applFiles;
    }

    private static String processConfirmation(String confirmation, Map vars,
                                              CommandExecutionContext executionContext) throws UserCancelException {
        confirmation = Variables.expand(vars, confirmation, true);
        PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
        ArrayList confCommandsOutput = new ArrayList();
        confirmation = cmdPerf.process(confirmation, confCommandsOutput);
        vars.put("CONFIRMATION_MSG", confirmation);
        boolean confCommandsSuccess = true;
        if (confCommandsOutput.size() > 0) {
            for (Iterator it = confCommandsOutput.iterator(); it.hasNext(); ) {
                confCommandsSuccess = confCommandsSuccess && ((Boolean) it.next()).booleanValue();
            }
        }
        if (confCommandsSuccess) {
            vars.put("CONFIRMATION_SUCCESS", Boolean.TRUE.toString());
        } else {
            vars.remove("CONFIRMATION_SUCCESS");
        }
        return confirmation;
    }

    /**
     * Perform the pre-customization of a command. After this it's necessary to
     * call {@link #preCustomizeExec} or {@link #preCustomizeStructuredExec}
     * @return <code>false</code> when the precustomization was cancelled,
     *         <code>true</code> otherwise.
     */
    public static boolean preCustomize(CommandExecutionContext executionContext, VcsCommand cmd, Map vars) {
        Object confObj = cmd.getProperty(VcsCommand.PROPERTY_CONFIRMATION_MSG);
        String confirmation = (confObj == null) ? "" : (String) confObj; //cmd.getConfirmationMsg();
        String fullName = (String) vars.get("PATH");
        String paths = (String) vars.get("PATHS");
        boolean confirmed = false;
        String pathSeparator = (String) vars.get("PS");
        if (pathSeparator != null) {
            pathSeparator = Variables.expand(vars, pathSeparator, false);
        } else {
            pathSeparator = java.io.File.separator;
        }
        VcsProvider provider;
        if (executionContext instanceof VcsProvider) {
            provider = (VcsProvider) executionContext;
        } else {
            provider = null;
        }
        if ((provider != null &&
             (fullName == null || SharabilityQuery.getSharability(provider.getFile(fullName)) != SharabilityQuery.NOT_SHARABLE))
            || executionContext != null) {

            if (provider != null) vars.put("NUM_IMPORTANT_FILES",
                                             ""+numImportant(provider, paths, pathSeparator));
            try {
                confirmation = processConfirmation(confirmation, vars, executionContext);
            } catch (UserCancelException cancelExc) {
                return false;
            }
            confirmed = true;
        //} else {
        //    confirmation = null;
        }
        if (confirmed && confirmation.length() > 0) {
            if (!NotifyDescriptor.Confirmation.YES_OPTION.equals (
                    DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Confirmation (
                        confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                return false; // The command is cancelled for that file
            }
        }
        // II. Then filll output from pre commands
        //     preCustomizeExec() or preCustomizeStructuredExec should be called
        return true;
    }

    /**
     * Perform the pre-customization of a command's execution string. Should be
     * called after {@link #preCustomize}.
     * @return the new execution string of the command or <code>null</code>
     *         when the precustomization was cancelled.
     */
    public static String preCustomizeExec(CommandExecutionContext executionContext, VcsCommand cmd, Hashtable vars) {
        String exec;
        if (executionContext != null) {
            PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
            try {
                exec = cmdPerf.process((String) cmd.getProperty(VcsCommand.PROPERTY_EXEC));
            } catch (UserCancelException cancelExc) {
                return null;
            }
        } else {
            exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        return exec;
    }

    /**
     * Perform the pre-customization of a command's structured execution string.
     * Should be called after {@link #preCustomize}.
     * @return the new structured execution property of the command or <code>null</code>
     *         when the precustomization was cancelled.
     */
    public static StructuredExec preCustomizeStructuredExec(CommandExecutionContext executionContext, VcsCommand cmd, Hashtable vars) {
        StructuredExec exec = (StructuredExec) cmd.getProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED);
        if (exec == null) return null;
        if (executionContext != null) {
            PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
            StructuredExec.Argument[] args = exec.getArguments();
            String w = null;
            String exe;
            StructuredExec.Argument[] as = new StructuredExec.Argument[args.length];
            try {
                if (exec.getWorking() != null) {
                    w = cmdPerf.process(exec.getWorking().getPath());
                }
                exe = cmdPerf.process(exec.getExecutable());
                for (int i = 0; i < args.length; i++) {
                    String arg = cmdPerf.process(args[i].getArgument());
                    as[i] = new StructuredExec.Argument(arg, args[i].isLine());
                }
            } catch (UserCancelException cancelExc) {
                return null;
            }
            exec = new StructuredExec((w != null) ? new java.io.File(w) : null, exe, as);
        }
        return exec;
    }

    private static final String GLOBAL_VARS_DEFINED_MARK = "Global variables defined internal mark."; // NOI18N

    /**
     * Insert the global options into the map of variables.
     */
    public static void defineGlobalOptions(Map vars, CommandExecutionContext executionContext,
                                           VcsCommand cmd) {
        //System.out.println("defineGlobalOptions("+cmd.getName()+")");
        String[] resourceBundles = (String[]) cmd.getProperty(INPUT_DESCRIPTOR_RESOURCE_BUNDLES);
        VariableInputDescriptor globalInputDescriptor =
                getGlobalVariableInputDescriptor(vars, executionContext, resourceBundles);
        //System.out.println("  globalInputDescriptor = "+globalInputDescriptor);
        if (globalInputDescriptor != null) {
            String glInput = (String) vars.get(GLOBAL_INPUT_EXPRESSION);
            //System.out.println("  GLOBAL_INPUT_EXPRESSION = "+glInput);
            VariableInputComponent[] components = globalInputDescriptor.components();
            synchronized (vars) {
                if (vars.get(GLOBAL_VARS_DEFINED_MARK) == null) { // Not to re-define customized values in sub-commands.
                    for (int i = 0; i < components.length; i++) {
                        String var = components[i].getVariable();
                        String value = components[i].getDefaultValue();
                        if (value != null && components[i].getComponent() != VariableInputDescriptor.INPUT_TEXT) {
                            //System.out.println("  PUTTING: "+var+"="+value+", component = "+components[i]);
                            vars.put(var, value);
                        }
                    }
                    if (glInput != null) {
                        vars.put(USER_GLOBAL_PARAM, glInput); // Put it as a variable, so that it can be used in sub-commands.
                        //System.out.println("  USER_GLOBAL_PARAM="+glInput);
                    }
                    vars.put(GLOBAL_VARS_DEFINED_MARK, GLOBAL_VARS_DEFINED_MARK);
                }
            }
        }
    }

    private static Component createNotificationDesign(final String text,
                                                      final JCheckBox checkBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(java.awt.Color.black);
        panel.add(textLabel, BorderLayout.CENTER);
        if (checkBox != null) panel.add(checkBox, BorderLayout.SOUTH);
        panel.getAccessibleContext().setAccessibleDescription(g("DLG_Notification_acsd"));
        return panel;
    }

    public static void commandNotification(final VcsCommandExecutor vce,
                                           String notification,
                                           final CommandExecutionContext executionContext) {
        final String notification1 = Variables.expand(vce.getVariables(), notification, false);
        if (notification1 == null || notification1.trim().length() == 0) return ;
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                if (executionContext.isCommandNotification() == false) return;
                NotifyDescriptor msg = new NotifyDescriptor.Message(notification1);
                JCheckBox checkBox;
                if (executionContext != null) {
                    checkBox = new JCheckBox(g("DLG_DoNotNotify"));
                    checkBox.setMnemonic(g("DLG_DoNotNotify_mnc").charAt(0));
                    checkBox.getAccessibleContext().setAccessibleDescription(g("DLG_DoNotNotify_acsd"));
                } else checkBox = null;
                msg.setMessage(createNotificationDesign(notification1, checkBox));
                DialogDisplayer.getDefault().notify(msg);
                if (checkBox != null && checkBox.isSelected()) {
                    executionContext.setCommandNotification(false);
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        g("DLG_CanBeEnabled")));
                }
            }
        });
    }

    private static boolean needPromptForPR(String name, String exec, Map vars){
        //D.deb("needPromptFor('"+name+"','"+exec+"')"); // NOI18N
        boolean result=false;
        String oldPassword= (String) vars.get("PASSWORD"); // NOI18N
        vars.put("PASSWORD", ""); // NOI18N
        String oldReason= (String) vars.get("REASON"); // NOI18N
        vars.put("REASON", ""); // NOI18N

        String test="variable_must_be_prompt_for"; // NOI18N
        vars.put(name,test);
        String s = Variables.expand(vars, exec, false);
        result = (s.indexOf(test) >= 0) ? true : false ;

        if (oldPassword != null) {
            vars.put("PASSWORD", oldPassword); // NOI18N
        } else {
            vars.remove("PASSWORD"); // NOI18N
        }
        if (oldReason != null) {
            vars.put("REASON", oldReason); // NOI18N
        } else {
            vars.remove("REASON"); // NOI18N
        }

        return result ;
    }

    private static void addComponentsWithPrecommands(VariableInputComponent component,
                                                     ArrayList componentsWithPrecommands) {
        if (component.needsPreCommandPerform()) componentsWithPrecommands.add(component);
        VariableInputComponent[] components = component.subComponents();
        if (components != null) {
            for (int i = 0; i < components.length; i++) {
                addComponentsWithPrecommands(components[i], componentsWithPrecommands);
            }
        }
    }

    private static void processPrecommands(CommandExecutionContext executionContext, Hashtable vars,
                                           VariableInputDescriptor inputDescriptor) throws UserCancelException {
        VariableInputComponent[] components = inputDescriptor.components();
        ArrayList componentsWithPrecommands = new ArrayList();
        for (int i = 0; i < components.length; i++) {
            addComponentsWithPrecommands(components[i], componentsWithPrecommands);
        }
        if (componentsWithPrecommands.size() > 0) {
            String[] defVals = new String[componentsWithPrecommands.size()];
            for (int i = 0; i < defVals.length; i++) {
                defVals[i] = ((VariableInputComponent) componentsWithPrecommands.get(i)).getDefaultValue();
            }
            PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
            String[] values = cmdPerf.process(defVals);
            for (int i = 0; i < values.length; i++) {
                ((VariableInputComponent) componentsWithPrecommands.get(i)).setValue(values[i]);
            }
        }
    }

    private static java.util.List getComponentsToPreprocess(VariableInputDescriptor inputDescriptor) {
        VariableInputComponent[] components = inputDescriptor.components();
        ArrayList componentsWithPrecommands = new ArrayList();
        for (int i = 0; i < components.length; i++) {
            addComponentsWithPrecommands(components[i], componentsWithPrecommands);
        }
        return componentsWithPrecommands;
    }

    private static void doPromptForPasswordIfNecessary(final CommandExecutionContext executionContext,
                                                       final String exec,
                                                       final Map vars) throws UserCancelException {
        synchronized (vars) {
            if (exec != null && needPromptForPR("PASSWORD", exec, vars)) { // NOI18N
                String password;
                synchronized (promptLock) { // disable the possibility, that the user
                    // will be prompted multiple times at once by concurrenly running commands
                    password = executionContext.getPassword();
                    if (password == null) {
                        String description = executionContext.getPasswordDescription();
                        NotifyDescriptorInputPassword nd;
                        if (description == null) {
                            nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
                        } else {
                            nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("TITL_Password"), description); // NOI18N
                        }
                        if (NotifyDescriptor.OK_OPTION.equals (DialogDisplayer.getDefault ().notify (nd))) {
                            password = nd.getInputText ();
                        } else {
                            executionContext.setPassword(null);
                            throw new UserCancelException();
                        }
                        executionContext.setPassword(password);
                    }
                }
                vars.put("PASSWORD", password); // NOI18N
            /* Do not change forEachFile, if the command is successful it will not ask any more */
            }
        }
    }

    /**
     * Setup some necessary variables, but do not present any GUI - the command
     * does not wish to be customized. The only exception is a prompt for password.
     * This method just sets the password (and prompt for it if it's not set).
     */
    public static void setupUncustomizedCommand(final CommandExecutionContext executionContext,
                                                final String exec, final Map vars,
                                                final VcsCommand cmd) throws UserCancelException {
        doPromptForPasswordIfNecessary(executionContext, exec, vars);
    }

    /** The table of FS and its global descriptor string. */
    private static Map globalInputStrs = Collections.synchronizedMap(new WeakHashMap());
    /** The table of FS and its parsed global descriptor */
    private static Map globalInputDescrs = Collections.synchronizedMap(new WeakHashMap());

    private static final Object promptLock = new Object();

    public static VariableInputDialog createInputDialog(final CommandExecutionContext executionContext,
                                                        String exec, final Map vars,
                                                        final VcsDescribedCommand dcmd,
                                                        boolean[] forEachFile,
                                                        StringBuffer retTitle) throws UserCancelException {
        final VcsCommand cmd = dcmd.getVcsCommand();
        VariableInputDescriptor inputDescriptor = (VariableInputDescriptor) cmd.getProperty(INPUT_DESCRIPTOR_PARSED);
        String[] resourceBundles = (String[]) cmd.getProperty(INPUT_DESCRIPTOR_RESOURCE_BUNDLES);
        if (inputDescriptor == null) {
            String inputDescriptorStr = (String) cmd.getProperty(VcsCommand.PROPERTY_INPUT_DESCRIPTOR);
            if (inputDescriptorStr != null) {
                // Perform the variable expansion to be able to use variables there
                //System.out.println("promptForVariables(): inputDescriptorStr = "+inputDescriptorStr);
                //inputDescriptorStr = Variables.expand(vars, inputDescriptorStr, true);
                //System.out.println("FILES_IS_FOLDER = '"+vars.get("FILES_IS_FOLDER")+"'");
                //System.out.println("promptForVariables(): after expand: inputDescriptorStr = "+inputDescriptorStr);
                //PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
                //inputDescriptorStr = cmdPerf.process(inputDescriptorStr);
                try {
                    inputDescriptor = VariableInputDescriptor.parseItems(inputDescriptorStr, resourceBundles);
                } catch (VariableInputFormatException exc) {
                    ErrorManager.getDefault().notify(exc);
                    throw new UserCancelException();
                }
                String type = executionContext.getCommandsProvider().getType();
                inputDescriptor.loadDefaults(dcmd.getName(), type, executionContext.isExpertMode());
                inputDescriptor.setValuesAsDefault();
                cmd.setProperty(INPUT_DESCRIPTOR_PARSED, inputDescriptor);
            }
        }
        synchronized (vars) {
            doPromptForPasswordIfNecessary(executionContext, exec, vars);
            if (forEachFile == null || forEachFile[0] == true) {
                /*
                final String[] userParams = executionContext.getUserParams();
                final Hashtable userParamsVarNames = new Hashtable(); // Variable names of prompt for additional parameters
                final Hashtable userParamsIndexes = new Hashtable();
                 */

                String ctrlDown = (String) vars.get(Variables.VAR_CTRL_DOWN_IN_ACTION);
                boolean expertCondition = /*executionContext.isExpertMode() || */(ctrlDown != null && ctrlDown.length() > 0);
                /*
                boolean acceptUserParams = executionContext.isAcceptUserParams() || (ctrlDown != null && ctrlDown.length() > 0);
                Table userParamsPromptLabels;
                if (exec == null) {
                    userParamsPromptLabels = new Table();
                } else {
                    userParamsPromptLabels = needPromptForUserParams(executionContext, exec, vars, userParamsVarNames,
                                                                     userParamsIndexes, cmd, acceptUserParams);
                }
                 */
                /*
                createTempPromptFiles(promptFile);
                if (prompt != null && prompt.size() > 0 || ask != null && ask.size() > 0 ||
                promptFile.size() > 0 || userParamsPromptLabels.size() > 0) {
                    */
                if (inputDescriptor != null && showInputDescriptor(inputDescriptor, expertCondition, vars)) {

                    VariableValueAdjustment varValueAdjust = executionContext.getVarValueAdjustment();
                    String file = varValueAdjust.revertAdjustedVarValue((String) vars.get("FILE")); // NOI18N
                    // provide a copy of variables for easy use and modification,
                    // since I have the original variables locked.
                    final Hashtable dlgVars = new Hashtable(vars);
                    final VariableInputDialog dlg = new VariableInputDialog(dcmd, inputDescriptor, expertCondition, dlgVars);
                    if (inputDescriptor != null && inputDescriptor.getHelpID() != null) {
                        dlg.putClientProperty("helpID", inputDescriptor.getHelpID());
                    }
                    dlg.setExecutionContext(executionContext, dlgVars);
                    if (cmd.getDisplayName() != null) {
                        dlg.setCmdName(cmd.getDisplayName());
                    } else {
                        dlg.setCmdName(cmd.getName());
                    }
                    if (expertCondition) {
                        if (exec != null) dlg.setExec(exec);
                    }
                    if (inputDescriptor != null) {
                        dlg.setComponentsToPreprocess(getComponentsToPreprocess(inputDescriptor));
                    }

                    VariableInputDescriptor globalInputDescriptor =
                            getGlobalVariableInputDescriptor(vars, executionContext, resourceBundles);
                    dlg.setGlobalInput(globalInputDescriptor);
                    //dlg.setUserParamsPromptLabels(userParamsPromptLabels, (String) cmd.getProperty(VcsCommand.PROPERTY_ADVANCED_NAME));
                    if (executionContext instanceof VariableInputDialog.FilePromptDocumentListener) {
                        dlg.setFilePromptDocumentListener((VariableInputDialog.FilePromptDocumentListener) executionContext, cmd);
                    }
                    if (forEachFile == null) dlg.showPromptEach(false);
                    else dlg.setPromptEach(executionContext.isPromptForVarsForEachFile());
                    String title = (inputDescriptor != null) ? inputDescriptor.getLabel() : null;
                    if (title == null) {
                        /*
                        title = java.text.MessageFormat.format(
                            org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.titleWithName"),
                            new Object[] { cmd.getDisplayName() }
                        );
                         */
                        title = cmd.getDisplayName();
                    }
                    if (file != null) title += VAR_INPUT_FILE_SEPARATOR + file;
                    String multipleFiles = (String) vars.get("MULTIPLE_FILES");
                    if (multipleFiles != null && multipleFiles.length() > 0) title += VAR_INPUT_MULTIPLE_FILES_TITLE_APPEND;
                    //if (retTitle.length() > 0) {
                        retTitle.replace(0, retTitle.length(), title);
                    //} else {
                    //    retTitle.append(title);
                    //}
                    final VariableInputDescriptor dlgInputDescriptor = inputDescriptor;
                    final VariableInputDescriptor dlgGlobalInputDescriptor = globalInputDescriptor;
                    dlg.addCloseListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (dlg.isValidInput()) {
                                dlg.processActions();
                                // put the dialog's variables back with all necessary modifications done.
                                vars.clear();
                                vars.putAll(dlgVars);
                                if (dlgInputDescriptor != null) {
                                    dlgInputDescriptor.addValuesToHistory();
                                }
                                if (dlgGlobalInputDescriptor != null) {
                                    dlgGlobalInputDescriptor.addValuesToHistory();
                                    // We do not remember them as default values
                                    // An explicit Set As Default is for that purpose
                                }
                                /*
                                Hashtable valuesTable = dlg.getUserParamsValuesTable();
                                for (Enumeration en = userParamsVarNames.keys(); en.hasMoreElements(); ) {
                                    String varName = (String) en.nextElement();
                                    //System.out.println("varName = "+varName+", label = "+userParamsVarNames.get(varName));
                                    String value = (String) valuesTable.get(userParamsVarNames.get(varName));
                                    vars.put(varName, value);
                                    int index = ((Integer) userParamsIndexes.get(varName)).intValue();
                                    if (index >= 0) userParams[index] = value;
                                    else {
                                        String[] cmdUserParams = (String[]) cmd.getProperty(VcsCommand.PROPERTY_USER_PARAMS);
                                        cmdUserParams[-index - 1] = value;
                                        cmd.setProperty(VcsCommand.PROPERTY_USER_PARAMS, cmdUserParams);
                                    }
                                    //D.deb("put("+varName+", "+valuesTable.get(userParamsVarNames.get(varName))+")");
                                }
                                executionContext.setUserParams(userParams);
                                 */
                                /*
                                if (forEachFile != null) {
                                    forEachFile[0] = dlg.getPromptForEachFile();
                                    fileSystem.setPromptForVarsForEachFile(forEachFile[0]);
                                }
                                 */
                            }
                        }
                    });
                    return dlg;
                } else {
                    if (inputDescriptor != null && showInputDescriptor(inputDescriptor, true, vars)) {
                        VariableInputComponent[] components = inputDescriptor.components();
                        for (int i = 0; i < components.length; i++) {
                            String var = components[i].getVariable();
                            String value = components[i].getDefaultValue();
                            if (value != null) vars.put(var, value);
                        }

                        VariableInputDescriptor globalInputDescriptor =
                                getGlobalVariableInputDescriptor(vars, executionContext, resourceBundles);
                        if (globalInputDescriptor != null) {
                            components = globalInputDescriptor.components();
                            for (int i = 0; i < components.length; i++) {
                                String var = components[i].getVariable();
                                String value = components[i].getDefaultValue();
                                if (value != null) vars.put(var, value);
                            }
                        }
                    }
                    if (forEachFile != null) {
                        forEachFile[0] = false;
                    }
                }
            }
            return null;
        }
    }

    private static VariableInputDescriptor getGlobalVariableInputDescriptor(Map vars,
            CommandExecutionContext executionContext, String[] resourceBundles) {

        String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
        Object ID = executionContext.getCommandsProvider().getType();
        if (ID == null) ID = executionContext;
        String globalInputStrStored = (String) globalInputStrs.get(ID);
        if (globalInputStr != null) {
            VariableInputDescriptor globalInputDescriptor;
            if (!globalInputStr.equals(globalInputStrStored)) {
                try {
                    globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr, resourceBundles);
                } catch (VariableInputFormatException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return null;
                }
                globalInputStrs.put(ID, globalInputStr);
                globalInputDescrs.put(ID, globalInputDescriptor);
                String type = executionContext.getCommandsProvider().getType();
                globalInputDescriptor.loadDefaults("common-command-options", type, executionContext.isExpertMode());  // NOI18N
                globalInputDescriptor.setValuesAsDefault();
            } else {
                globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(ID);
            }
            //System.out.println("Global Input Descriptor '"+executionContext.getCommandsProvider().getType()+"' = "+globalInputDescriptor+((globalInputDescriptor != null) ? " hash code = "+globalInputDescriptor.hashCode() : ""));
            return globalInputDescriptor;
        } else {
            return null;
        }
    }

    /** Do not show if all options are for experts and I not in expert mode. */
    private static boolean showInputDescriptor(VariableInputDescriptor inputDescriptor, boolean isExpertMode, Map vars) {
        VariableInputComponent[] inputComponents = inputDescriptor.components();
        if (inputComponents.length == 0) return false;
        for (int i = 0; i < inputComponents.length; i++) {
            if (VariableInputComponent.isVarConditionMatch(inputComponents[i].getVarConditions(), vars) &&
                (inputComponents[i].isExpert() && isExpertMode ||
                 (!inputComponents[i].isExpert()
                  && inputComponents[i].getComponent() != VariableInputDescriptor.INPUT_GLOBAL)))
                return true;
        }
        return false;
    }


    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandCustomizationSupport.class).getString(s);
    }

    /**
     * A dummy FileObject, that represents a non-existent FileObject
     * -- FileObject, that does not exist in the FileSystem !!!
     * This FO can not be find in any FS through findResource() or any other method.
     * The only purpose of this FO is to hold the path, that is used for VCS
     * commands execution.
     */
    private static final class NonExistentFileObject extends FileObject {

        private String path;
        private String name;
        private org.openide.filesystems.FileSystem fileSystem;
        private FileStateInvalidException fsiex;

        public NonExistentFileObject(VcsProvider provider, String path) {
            try {
                this.fileSystem = provider.findResource("").getFileSystem();//getAssociatedFileSystem();
            } catch (FileStateInvalidException ex) {
                this.fsiex = ex;
            }
            this.path = path;
            int i = path.lastIndexOf('/');
            if (i >= 0) {
                name = path.substring(i);
            } else {
                name = path;
            }
        }

        public void addFileChangeListener(org.openide.filesystems.FileChangeListener fcl) {
            // It's not possible to listen on non-existent FileObject
        }

        public FileObject createData(String name, String ext) throws java.io.IOException {
            throw new java.io.IOException("It's not possible to create data inside non-existent file object.");
        }

        public FileObject createFolder(String name) throws java.io.IOException {
            throw new java.io.IOException("It's not possible to create folder inside non-existent file object.");
        }

        public void delete(org.openide.filesystems.FileLock lock) throws java.io.IOException {
            // non-existing file is already deleted
        }

        public Object getAttribute(String attrName) {
            return null; // no attributes
        }

        public Enumeration getAttributes() {
            return java.util.Collections.enumeration(java.util.Collections.EMPTY_SET);
        }

        public FileObject[] getChildren() {
            return new FileObject[0];
        }

        public String getExt() {
            int i = name.lastIndexOf ('.') + 1;
            /** period at first position is not considered as extension-separator */
            return i <= 1 || i == name.length ()  ? "" : name.substring (i); // NOI18N
        }

        public FileObject getFileObject(String name, String ext) {
            return null;
        }

        public org.openide.filesystems.FileSystem getFileSystem() throws org.openide.filesystems.FileStateInvalidException {
            if (fileSystem == null) {
                throw fsiex;
            }
            return fileSystem;
        }

        public java.io.InputStream getInputStream() throws java.io.FileNotFoundException {
            throw new java.io.FileNotFoundException("File "+getPath()+" does not exist.");
        }

        public String getName() {
            int i = name.lastIndexOf ('.');
            /** period at first position is not considered as extension-separator */
            return i <= 0 ? name : name.substring (0, i);
        }

        public java.io.OutputStream getOutputStream(org.openide.filesystems.FileLock lock) throws java.io.IOException {
            throw new java.io.FileNotFoundException("File "+getPath()+" does not exist.");
        }

        public FileObject getParent() {
            return null;
        }

        public String getPath() {
            return path;
        }

        public long getSize() {
            return 0;
        }

        public boolean isData() {
            return true;
        }

        public boolean isFolder() {
            return false;
        }

        public boolean isReadOnly() {
            return true;
        }

        public boolean isRoot() {
            return false;
        }

        public boolean isValid() {
            return true;
        }

        public java.util.Date lastModified() {
            return new java.util.Date(0);
        }

        public org.openide.filesystems.FileLock lock() throws java.io.IOException {
            throw new java.io.FileNotFoundException("File "+getPath()+" does not exist.");
        }

        public void removeFileChangeListener(org.openide.filesystems.FileChangeListener fcl) {
            // It's not possible to listen on non-existent FileObject
        }

        public void rename(org.openide.filesystems.FileLock lock, String name, String ext) throws java.io.IOException {
            throw new java.io.IOException("Non-existent file can not be renamed.");
        }

        public void setAttribute(String attrName, Object value) throws java.io.IOException {
            // silently ignore
        }

        public void setImportant(boolean b) {
            // silently ignore
        }

    }
}
