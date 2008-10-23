/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.wizard.components.sequences;


import java.io.File;
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.env.CheckStatus;
import org.netbeans.installer.utils.env.EnvironmentInfo;
import org.netbeans.installer.utils.env.EnvironmentInfoFactory;
import org.netbeans.installer.utils.env.ExistingSunStudioChecker;
import org.netbeans.installer.utils.env.SystemCheckCategory;
import org.netbeans.installer.utils.helper.ExecutionMode;
import org.netbeans.installer.utils.silent.SilentLogManager;
import org.netbeans.installer.wizard.Utils;
import org.netbeans.installer.wizard.components.WizardSequence;
import org.netbeans.installer.wizard.components.panels.sunstudio.ExistingSunStudioPanel;
import org.netbeans.installer.wizard.components.panels.sunstudio.SystemCheckPanel;

public class SystemCheckSequence extends WizardSequence {
    
    private final String CRITICAL_ERROR_MESSAGE = ResourceUtils.getString(SystemCheckSequence.class, "SCS.error.message"); // NOI18N
    
    private SystemCheckPanel systemCheckPanel = null;
    private ExistingSunStudioPanel existingSunStudioPanel = null;

    public SystemCheckSequence() {
        systemCheckPanel = new SystemCheckPanel();
        existingSunStudioPanel = new ExistingSunStudioPanel();
    }

    @Override
    public void executeForward() {
        if (ExecutionMode.getCurrentExecutionMode().equals(ExecutionMode.CREATE_BUNDLE)) {
            super.executeForward();
            return;
        } else {
            if (SystemUtils.isLinux()) {
                final List<Product> toInstall = Registry.getInstance().getProductsToInstall();
                final String wrongBaseDir = "/usr/local";
                for(Product product: toInstall) {
                    String path = product.getInstallationLocation().getAbsolutePath();
                    if (path.startsWith(wrongBaseDir)) {
                        product.setInstallationLocation(new File(path.replace(wrongBaseDir, "/opt/sun")));
                    }
                }
            }
        }
        if (SilentLogManager.isLogManagerActive()) {
            for(SystemCheckCategory problem: SystemCheckCategory.getProblemCategories()) {
                String shortMessage = problem.getShortErrorMessage();
                SilentLogManager.forceLog(problem.check(),  ((shortMessage.length() > 0)? shortMessage + ". ": "") + problem.getLongErrorMessage());
            }
            if (SystemCheckCategory.hasErrorCategories()) {
                SilentLogManager.forceLog(CheckStatus.ERROR, CRITICAL_ERROR_MESSAGE);
                getWizard().getFinishHandler().cancel();
            }
            ExistingSunStudioChecker checker = ExistingSunStudioChecker.getInstance();
            if (checker.isSunStudioInstallationFound()) {                
                for (String version : checker.getInstalledVersions()) {
                    SilentLogManager.forceLog(
                            checker.getResolutionForVersion(version) == checker.INSTALLATION_BLOCKED ?
                                CheckStatus.ERROR : CheckStatus.WARNING, "Sun Studio " + version
                            + " was found in " + StringUtils.asString(checker.getBaseDirsForVersion(version)));
                }
                if (!checker.isInstallationPossible()) {
                    SilentLogManager.forceLog(CheckStatus.ERROR, "Installation is not possible");
                    getWizard().getFinishHandler().cancel();
                }
                File defaultDirectory = Utils.getSSBase().getInstallationLocation();
                if (checker.getAllowedDirectory() != null && !defaultDirectory.equals(new File(checker.getAllowedDirectory()))) {
                    SilentLogManager.forceLog(CheckStatus.ERROR, "Installation is not possible. Sun Studio could" +
                            " be installed only in " + checker.getAllowedDirectory() + " However installation location is " + defaultDirectory.getAbsolutePath());
                    getWizard().getFinishHandler().cancel();
                }
                
                if (checker.getRestrictedDirectories() != null) {
                    for (String dir : checker.getRestrictedDirectories()) {
                        if (defaultDirectory.equals(new File(dir)))  {
                            SilentLogManager.forceLog(CheckStatus.ERROR, "Installation is not possible. Sun Studio could not "
                            + " be installed in defualt folder " + dir);
                            getWizard().getFinishHandler().cancel();
                        }
                    }                    
                }
            }            
        } else {
            if (Registry.getInstance().getProductsToInstall().size() > 0) {
                getChildren().clear();
                if (SystemCheckCategory.hasProblemCategories()) {
                    addChild(systemCheckPanel);
                }
                if (!SystemCheckCategory.hasErrorCategories() && ExistingSunStudioChecker.getInstance().isSunStudioInstallationFound()) {
                    addChild(existingSunStudioPanel);
                }
            }            
        }
        super.executeForward();
    }
   
    @Override
    public boolean canExecuteForward() {        
        return true;
    }
    
    @Override
    public boolean canExecuteBackward() {
        return true;
    }
    
}
