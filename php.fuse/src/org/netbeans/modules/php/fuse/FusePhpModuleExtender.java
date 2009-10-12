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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.fuse.exceptions.InvalidFuseFrameworkException;
import org.netbeans.modules.php.fuse.other.InitialFuseSetup;
import org.netbeans.modules.php.fuse.ui.wizards.NewProjectConfigurationPanel;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author Martin Fousek
 */
public class FusePhpModuleExtender extends PhpModuleExtender {
    //@GuardedBy(this)
    private NewProjectConfigurationPanel panel = null;

    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        FuseFramework fuseFramework = null;
        try {
            fuseFramework = FuseFramework.getDefault();
        } catch (InvalidFuseFrameworkException ex) {
            // should not happen, must be handled in the wizard
            Exceptions.printStackTrace(ex);
        }
        assert fuseFramework.isValid() : "Fuse framework has to be valid!";

        // generate apps - load stored data
        InitialFuseSetup initialFuseSetup = getPanel().getSettings();

        // copy FUSE framework into new project
        if (initialFuseSetup.isCopyFuseFrameworkIntoProject())
            fuseFramework.copyFuseFrameworkIntoProject(phpModule);

        if (!fuseFramework.initProject(phpModule, initialFuseSetup.getParams(), initialFuseSetup.isCopyFuseFrameworkIntoProject())) {
            // can happen if fuse framework was not chosen
            Logger.getLogger(FusePhpModuleExtender.class.getName())
                    .info("Framework Fuse not found in newly created project " + phpModule.getDisplayName());
            throw new ExtendingException(NbBundle.getMessage(FusePhpModuleExtender.class, "MSG_NotExtended"));
        }

        // prefetch commands
        FusePhpFrameworkProvider.getInstance().getFrameworkCommandSupport(phpModule).refreshFrameworkCommandsLater(null);

        // return files
        Set<FileObject> files = new HashSet<FileObject>();
        FileObject common_conf = phpModule.getSourceDirectory().getFileObject(FuseFramework.COMMON_CONF_FILE); // NOI18N
        assert common_conf != null;
        files.add(common_conf);
        return files;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getPanel().removeChangeListener(listener);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

     @Override
    public String getWarningMessage() {
        return getPanel().getWarningMessage();
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("org.netbeans.modules.php.fuse.about");
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        try {
            FuseFramework fuseFramework = FuseFramework.getDefault();
            String error = fuseFramework.validateNetBeansScaffoldFile();
            if (error != null)
                return error;
        } catch (InvalidFuseFrameworkException ex) {
            return NbBundle.getMessage(FusePhpModuleExtender.class, "MSG_CannotExtend", ex.getMessage());
        }
        return null;
    }

    private synchronized NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }
        return panel;
    }
}
