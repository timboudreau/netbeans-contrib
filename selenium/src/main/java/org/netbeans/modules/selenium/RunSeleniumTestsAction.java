/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.selenium;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Jindrich Sedek, Martin Fousek
 */
public final class RunSeleniumTestsAction extends ExtendedAction {

    protected void performAction(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            Project project = getProjectForNode(node);
            //ActionProvider provider = project.getLookup().lookup(ActionProvider.class);
            FileObject buildXML = findBuildXml(project);
            Properties p = new Properties();
            p.setProperty("forceRedeploy", "false"); //NOI18N
            if (buildXML == null){
                NotifyDescriptor desc = new NotifyDescriptor.Message(NbBundle.getMessage(RunSeleniumTestsAction.class, "No_Build_XML"), NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notifyLater(desc);
                return;
            }
            // if project dir doesn't contain test dir, create it
            File testFolder = new File(project.getProjectDirectory().getPath(), "test");  // NOI18N
            if (FileUtil.toFileObject(testFolder) == null) {
                try {
                    FileUtil.createFolder(testFolder);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            try {
                ExecutorTask task = ActionUtils.runTarget(buildXML, new String[]{"run-deploy"}, p);
                //wait deployment finished
                task.result();

                FileObject seleniumSources = SeleniumSupport.getSeleniumDir(project);
                String includes = listAllTestIncludes(seleniumSources);
                if (includes == null){
                    NotifyDescriptor desc = new NotifyDescriptor.Message(NbBundle.getMessage(RunSeleniumTestsAction.class, "No_Selenium_Tests"), NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notifyLater(desc);
                    return;
                }
                p.setProperty("test.includes", includes);
                p.setProperty("javac.includes", ActionUtils.antIncludesList(seleniumSources.getChildren(), seleniumSources));
                ActionUtils.runTarget(buildXML, new String[]{"test-single"}, p);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(RunSeleniumTestsAction.class, "CTL_RunSeleniumTestsAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }
}

