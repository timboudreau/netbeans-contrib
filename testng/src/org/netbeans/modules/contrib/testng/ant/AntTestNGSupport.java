/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng.ant;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.api.project.ant.AntBuildExtender.Extension;
import org.netbeans.modules.contrib.testng.spi.TestConfig;
import org.netbeans.modules.contrib.testng.spi.TestNGSupportImplementation;
import org.netbeans.modules.contrib.testng.spi.XMLSuiteSupport;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 *
 * @author lukas
 */
public class AntTestNGSupport extends TestNGSupportImplementation {

    private static final Logger LOGGER = Logger.getLogger(AntTestNGSupport.class.getName());

    public boolean isProjectSupported(Project p) {
        return p.getLookup().lookup(AntArtifactProvider.class) != null;
    }

    public void configureProject(FileObject createdFile) {
        try {
            addLibrary(createdFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        Project p = FileOwnerQuery.getOwner(createdFile);
        AntBuildExtender extender = p.getLookup().lookup(AntBuildExtender.class);
        if (extender != null) {
            String ID = "test-ng-1.0"; //NOI18N
            Extension extension = extender.getExtension(ID);
            if (extension == null) {
                LOGGER.finer("Extensible targets: " + extender.getExtensibleTargets());
                // create testng-build.xml
                String resource = "org-netbeans-modules-contrib-testng/testng-build.xml"; // NOI18N
                try {
                    FileObject testng = FileUtil.copyFile(Repository.getDefault().getDefaultFileSystem().findResource(resource), p.getProjectDirectory().getFileObject("nbproject"), "testng-impl"); //NOI18N
                    extension = extender.addExtension(ID, testng);
                    extension.addDependency("-pre-pre-compile", "-reinit-tasks"); //NOI18N
                    ProjectManager.getDefault().saveProject(p);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public TestExecutor createExecutor(Project p) {
        return new AntExecutor(p);
    }

    public class AntExecutor implements TestNGSupportImplementation.TestExecutor {

        private static final String failedConfPath = "build/test/results/testng-failed.xml"; //NOI18N
        private Project p;

        public AntExecutor(Project p) {
            this.p = p;
        }

        public boolean hasFailedTests() {
            FileObject projectHome = p.getProjectDirectory();
            //XXX - should rather listen on a fileobject??
            FileUtil.refreshFor(FileUtil.toFile(projectHome));
            FileObject failedTestsConfig = projectHome.getFileObject(failedConfPath);
            return failedTestsConfig != null && failedTestsConfig.isValid();
        }

        public void execute(TestConfig config) throws IOException {
            FileObject projectHome = p.getProjectDirectory();
            Properties props = new Properties();
            if (config.doRerun()) {
                FileObject failedTestsConfig = projectHome.getFileObject(failedConfPath);
                props.put("testng.config", FileUtil.getRelativePath(projectHome, failedTestsConfig));
            } else {
                try {
                    FileObject fo = FileUtil.createFolder(projectHome, "build/generated/testng"); //NOI18N
                    File f = XMLSuiteSupport.createSuiteforMethod(
                            FileUtil.toFile(fo),
                            ProjectUtils.getInformation(p).getDisplayName(),
                            config.getPackageName(),
                            config.getClassName(),
                            config.getMethodName());
                    props.put("testng.config", FileUtil.getRelativePath(projectHome, FileUtil.toFileObject(f)));
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
            try {
                ActionUtils.runTarget(projectHome.getFileObject("build.xml"), new String[]{"run-testng"}, props); //NOI18N
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}