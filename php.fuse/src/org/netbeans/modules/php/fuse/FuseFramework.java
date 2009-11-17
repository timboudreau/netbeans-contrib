/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.fuse;

import java.io.FileNotFoundException;
import org.netbeans.modules.php.fuse.utils.FileHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.fuse.commands.FuseCommandSupport;
import org.netbeans.modules.php.fuse.exceptions.InvalidFuseFrameworkException;
import org.netbeans.modules.php.fuse.ui.options.FuseOptions;
import org.netbeans.modules.php.fuse.utils.ArrayHelper;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * @author Martin Fousek
 */
public class FuseFramework extends PhpProgram {
    public static final String SCRIPT_NAME = "fuse_scaffold.php"; // NOI18N
    public static final String DIR_WITH_SCRIPTS = "manage"; // NOI18N
    public static final String COMMON_CONF_FILE = "config/common.conf.php";
    public static final String OPTIONS_SUB_PATH = "Fuse"; // NOI18N
    public static final String FUSE_INCLUDE_DIR = "/include/FUSE"; // NOI18N
    public static final String CMD_INIT_PROJECT = "/scripts/install/fuse_scaffold_netbeans.php"; // NOI18N
    public static final String ROUTES_CONF_FILE = "routes.conf.php"; // NOI18N


    public FuseFramework(String command) {
        super(command);
    }

    /**
     * Get the default, <b>valid only</b> Fuse framework.
     * @return the default, <b>valid only</b> Fuse framework.
     * @throws InvalidFuseFrameworkException if Fuse framework is not valid.
     */
    public static FuseFramework getDefault() throws InvalidFuseFrameworkException {
        String fuse = FuseOptions.getInstance().getFuse();
        String error = validate(fuse);
        if (error != null) {
            throw new InvalidFuseFrameworkException(error);
        }
        return new FuseFramework(fuse);
    }

    /**
     * Get the project specific, <b>valid only</b> Fuse framework. If not found, the {@link #getDefault() default} Fuse framework is returned.
     * @param phpModule PHP module for which Fuse framework is taken
     * @param warn <code>true</code> if user is warned when the {@link #getDefault() default} Fuse framework is returned.
     * @return the project specific, <b>valid only</b> Fuse framework.
     * @throws InvalidFuseFrameworkException if Fuse framework is not valid. If not found, the {@link #getDefault() default} Fuse framework is returned.
     * @see #getDefault()
     */
    public static FuseFramework forPhpModule(PhpModule phpModule, boolean warn) throws InvalidFuseFrameworkException {
        String fuseScripts = new File(FileUtil.toFile(phpModule.getSourceDirectory()), DIR_WITH_SCRIPTS).getAbsolutePath();
        String error = validateExisting(fuseScripts);
        if (error != null) {
            if (warn) {
                Message message = new NotifyDescriptor.Message(
                        NbBundle.getMessage(FuseFramework.class, "MSG_InvalidProjectFuseFramework", error),
                        NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(message);
            }
            return getDefault();
        }
        return new FuseFramework(fuseScripts);
    }

    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + getOptionsSubPath(); // NOI18N
    }

    public static String getOptionsSubPath() {
        return OPTIONS_SUB_PATH;
    }

    public static String validate(String command) {
        return new FuseFramework(command).validate();
    }

    @Override
    public String validate() {
        if (!StringUtils.hasText(getProgram())) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_NoFuse");
        }

        //check Fuse directory
        File dir = new File(getProgram());
        if (!dir.isAbsolute()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_FuseNotAbsolutePath");
        }
        if (!dir.isDirectory()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_FuseNotDirectory");
        }
        if (!dir.canRead()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_FuseCannotRead");
        }

        // check scaffold file
        File scaffoldFile = new File(dir + "/scripts/install/fuse_scaffold.php");
        if (!scaffoldFile.exists()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_NotFuseFramework");
        }
        if (scaffoldFile.isDirectory()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_NotFuseScaffoldFile");
        }

        return null;
    }

    public String validateNetBeansScaffoldFile() {
        File dir = new File(getProgram());
        // check improved scaffold file for NetBeans
        File nbScaffoldFile = new File(dir + CMD_INIT_PROJECT);
        if (!nbScaffoldFile.exists()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_NotUpdatedFuseFramework");
        }
        if (!nbScaffoldFile.isFile()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_FuseScaffoldNotFile");
        }
        if (!nbScaffoldFile.canRead()) {
            return NbBundle.getMessage(FuseFramework.class, "MSG_FuseScaffoldCannotRead");
        }
        return null;
    }

    public static String validateExisting(String sourceDir) {
        // check Fuse managing scripts
        for (String script : FuseCommandSupport.getFuseGeneratingScripts()) {
            if (!checkValidFuseScript(sourceDir + "/" + script))
                return NbBundle.getMessage(FuseFramework.class, "MSG_InvalidFuseManageScript");
        }
        return null;
    }

    public static boolean checkValidFuseScript(String fuseScript){
        // check Fuse framework
        File script = new File(fuseScript);
        if (!script.exists() || !script.isFile() || !script.canRead())
            return false;
        return true;
    }

    public boolean initProject(PhpModule phpModule, String[] params, boolean copyFrameworkIntoSources) {
        String projectName = phpModule.getDisplayName();

        // choose according FUSE to user setup
        String fusePath = "";
        fusePath = copyFrameworkIntoSources? phpModule.getSourceDirectory().getPath() + FUSE_INCLUDE_DIR : getProgram();

        // get URL from New project wizard
        String projectURL = phpModule.getProperties().getUrl();

        // create params for FUSE calling
        String[] cmdParams = ArrayHelper.mergeArrays(new String[]{phpModule.getSourceDirectory().getPath(),
            projectURL}, params, new String[]{fusePath});

        FuseCommandSupport commandSupport = FuseCommandSupport.forCreatingProject(phpModule);
        ExternalProcessBuilder processBuilder = null;
        processBuilder = commandSupport.createSilentCommand(fusePath + CMD_INIT_PROJECT, cmdParams);
        assert processBuilder != null;
        ExecutionDescriptor executionDescriptor = commandSupport.getDescriptor();
        runService(processBuilder, executionDescriptor, commandSupport.getOutputTitle(CMD_INIT_PROJECT, projectName), false);
        return FusePhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    public void copyFuseFrameworkIntoProject(PhpModule phpModule) {
        if (!new File(phpModule.getSourceDirectory().getPath() + "/include").exists()) {
            new File(phpModule.getSourceDirectory().getPath() + "/include").mkdir();
        }
        FileHelper.copyDirectory(new File(getProgram()), new File(FileUtil.toFile(phpModule.getSourceDirectory()).getAbsolutePath(),FUSE_INCLUDE_DIR));
    }

    private static void runService(ExternalProcessBuilder processBuilder, ExecutionDescriptor executionDescriptor, String title, boolean warnUser) {
        final ExecutionService service = ExecutionService.newService(
                processBuilder,
                executionDescriptor,
                title);
        final Future<Integer> result = service.run();
        try {
            result.get();
        } catch (ExecutionException ex) {
            if (warnUser) {
                UiUtils.processExecutionException(ex, FuseFramework.getOptionsSubPath());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isImproved() {
        return (validateNetBeansScaffoldFile() == null);
    }

    public void improveFuseSupport() throws FileNotFoundException, IOException {
        byte[] buf = new byte[1024];
        int len;
        InputStream in = getClass().getResourceAsStream("/org/netbeans/modules/php/fuse/resources/fuse_scaffold.php");
        OutputStream out = new FileOutputStream(getProgram() + CMD_INIT_PROJECT);
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void improveFuseSupport(String fusePath) throws FileNotFoundException, IOException {
        new FuseFramework(fusePath).improveFuseSupport();
    }
}
