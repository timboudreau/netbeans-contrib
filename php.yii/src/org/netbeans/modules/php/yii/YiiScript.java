/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.yii;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.yii.commands.YiiCommandSupport;
import org.netbeans.modules.php.yii.extensions.api.YiiExtensionProvider;
import org.netbeans.modules.php.yii.extensions.api.YiiExtensions;
import org.netbeans.modules.php.yii.ui.options.YiiOptions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Gevik Babakhani <gevik@netbeans.org>
 */
public class YiiScript extends PhpProgram {

    public static final String SCRIPT_NAME = "yiic"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + FileUtils.getScriptExtension(true);
    public static final String OPTIONS_SUB_PATH = "Yii"; // NOI18N
    public static final String CMD_INIT_PROJECT = "nbwebapp";
    public static final String[] CMD_INIT_PROJECT_ARGS = new String[]{"."}; // NOI18N
    public static final String[] CMD_INIT_PROJECT_ARGS_TITLE = new String[]{"project"}; // NOI18N
    private static final String NB_WEBAPP_COMMAND = "NbWebAppCommand";
    private static final String NB_UTIL_COMMAND = "NbUtilCommand";
    private static final String NB_WEBAPP_COMMAND_FILE = NB_WEBAPP_COMMAND + ".php";
    private static final String NB_UTIL_COMMAND_FILE = NB_UTIL_COMMAND + ".php";
    private static final String WEBAPP_PROVIDER_REL_PATH = "yii/" + NB_WEBAPP_COMMAND_FILE; // NOI18N
    private static final String UTIL_PROVIDER_REL_PATH = "yii/" + NB_UTIL_COMMAND_FILE; // NOI18N
    private static final File WEBAPP_PROVIDER;
    private static final File UTIL_PROVIDER;

    static {
        File commandsProvider = null;
        try {
            commandsProvider = FileUtil.normalizeFile(
                    InstalledFileLocator.getDefault().locate(WEBAPP_PROVIDER_REL_PATH, "org.netbeans.modules.php.yii", false).getCanonicalFile()); // NOI18N
            if (commandsProvider == null || !commandsProvider.isFile()) {
                throw new IllegalStateException("Could not locate file " + WEBAPP_PROVIDER_REL_PATH);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not locate file " + WEBAPP_PROVIDER_REL_PATH, ex);
        }
        WEBAPP_PROVIDER = commandsProvider;
    }

    static {
        File commandsProvider = null;
        try {
            commandsProvider = FileUtil.normalizeFile(
                    InstalledFileLocator.getDefault().locate(UTIL_PROVIDER_REL_PATH, "org.netbeans.modules.php.yii", false).getCanonicalFile()); // NOI18N
            if (commandsProvider == null || !commandsProvider.isFile()) {
                throw new IllegalStateException("Could not locate file " + UTIL_PROVIDER_REL_PATH);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not locate file " + UTIL_PROVIDER_REL_PATH, ex);
        }
        UTIL_PROVIDER = commandsProvider;
    }

    private YiiScript(String command) {
        super(command);
    }

    public static YiiScript getDefault() throws InvalidPhpProgramException {
        return getCustom(YiiOptions.getInstance().getYii());
    }

    private static YiiScript getCustom(String command) throws InvalidPhpProgramException {
        String error = validate(command);
        if (error != null) {
            throw new InvalidPhpProgramException(error);
        }
        return new YiiScript(command);
    }

    public boolean initProject(PhpModule phpModule) {
        YiiCommandSupport commandSupport = YiiPhpFrameworkProvider.getInstance().getFrameworkCommandSupport(phpModule);
        ExternalProcessBuilder processBuilder = commandSupport.createSilentCommand(CMD_INIT_PROJECT, CMD_INIT_PROJECT_ARGS);
        if (processBuilder == null) {
            return false;
        }
        ExecutionDescriptor executionDescriptor = commandSupport.getDescriptor();


        runService(processBuilder, executionDescriptor, commandSupport.getOutputTitle(CMD_INIT_PROJECT, CMD_INIT_PROJECT_ARGS_TITLE), false);
        return YiiPhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    @Override
    public String validate() {
        String script = getProgram();
        String result = FileUtils.validateScript(script, NbBundle.getMessage(YiiScript.class, "LBL_YiiScript"));
        if (result == null) {
            File file = new File(script);
            if (!file.canExecute()) {
                return NbBundle.getMessage(YiiScript.class, "MSG_ScriptCannotExecute");
            } else {
                // now we check to extend the Yii Framework with netbeans support
                return extendYiiToSupportNetBeans(file.getParent());
            }
        } else {
            return result;
        }
    }

    private void callInstallConsoleCommands(FileObject cmdPath) {
        for (YiiExtensionProvider extension : YiiExtensions.getExtensions()) {
            extension.installConsoleCommands(cmdPath);
        }
    }

    private String extendYiiToSupportNetBeans(String frameworkPath) {

        if (YiiOptions.getInstance().getYiiExtended()) {
            return null;
        } else {
            FileObject path = FileUtil.toFileObject(new File(frameworkPath));
            FileObject cmdPath = path.getFileObject("cli/commands/");
            if (cmdPath == null) {
                return NbBundle.getMessage(YiiScript.class, "MSG_NoClientCommandsPath");
            } else if (cmdPath != null && !cmdPath.canWrite()) {
                return NbBundle.getMessage(YiiScript.class, "MSG_CommandsNotWritable", cmdPath.getPath());
            } else if (cmdPath.getFileObject(NB_WEBAPP_COMMAND_FILE) != null) {
                YiiOptions.getInstance().setYiiExtended(true);
                callInstallConsoleCommands(cmdPath);
                return null;
            } else {
                try {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(YiiScript.class, "MSG_YiiExtendWithNB"));
                    DialogDisplayer.getDefault().notify(nd);
                    YiiOptions.getInstance().setYiiExtended(true);
                    FileUtil.copyFile(FileUtil.toFileObject(WEBAPP_PROVIDER), cmdPath, NB_WEBAPP_COMMAND);
                    FileUtil.copyFile(FileUtil.toFileObject(UTIL_PROVIDER), cmdPath, NB_UTIL_COMMAND);
                    callInstallConsoleCommands(cmdPath);
                    return null;
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    return ex.getMessage();
                }
            }
        }
    }

    public static String validate(String command) {
        return new YiiScript(command).validate();
    }

    /**
     * @return IDE options Yii subpath
     */
    public static String getOptionsSubPath() {
        return OPTIONS_SUB_PATH;
    }

    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + getOptionsSubPath(); // NOI18N
    }

    public static void runService(ExternalProcessBuilder processBuilder, ExecutionDescriptor executionDescriptor, String title, boolean warnUser) {
        try {
            executeAndWait(processBuilder, executionDescriptor, title);
        } catch (CancellationException ex) {
            // canceled
        } catch (ExecutionException ex) {
            if (warnUser) {
                UiUtils.processExecutionException(ex, getOptionsSubPath());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
