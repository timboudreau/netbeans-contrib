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
package org.netbeans.modules.selenium.maven;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.Dependency;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.PluginManagement;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jindrich Sedek, Martin Fousek
 */
final class SeleniumMavenSupport {

    private final static String SUREFIRE_GROUP_ID = "org.apache.maven.plugins";     //NOI18N
    private final static String SUREFIRE_ARTIFACT_ID = "maven-surefire-plugin";     //NOI18N
    private final static String SELENIUM_GROUP_ID = "org.seleniumhq.selenium";      //NOI18N
    private final static String SELENIUM_ARTIFACT_ID = "selenium-remote-control";   //NOI18N

    static FileObject getTestRoot(Project project) {
        NbMavenProject nbProject = project.getLookup().lookup(NbMavenProject.class);
        MavenProject mvp = nbProject.getMavenProject();
        @SuppressWarnings("unchecked")
        List<String> testRoots = mvp.getTestCompileSourceRoots();
        if (testRoots.isEmpty()) {
            return null;
        }
        File testRoot = new File(testRoots.get(0));
        FileObject result = null;
        try {
            result = FileUtil.createData(testRoot);
        } catch (IOException ex) {
            Logger.getLogger(SeleniumMavenSupport.class.getName()).log(Level.SEVERE, "Impossible to create test root file object", ex); //NOI18N
        }
        return result;
    }

    static void prepareProject(Project project) {
        assert (isMavenProject(project));
        if (isProjectReady(project)) {
            return;
        }

        NbMavenProject nbProject = project.getLookup().lookup(NbMavenProject.class);
        MavenProject mvp = nbProject.getMavenProject();

        ModelOperation<POMModel> operation = new SeleniumOperation();
        org.netbeans.modules.maven.model.Utilities.performPOMModelOperations(getPomFile(project), Collections.singletonList(operation));

    }

    private static class SeleniumOperation implements ModelOperation<POMModel> {

        public void performOperation(POMModel model) {
            Dependency dep = ModelUtils.checkModelDependency(model, SELENIUM_GROUP_ID, SELENIUM_ARTIFACT_ID, true);
            dep.setScope("test");
            dep.setVersion("2.0b3");

            org.netbeans.modules.maven.model.pom.Project p = model.getProject();
            Build bld = p.getBuild();
            if (bld == null) {
                bld = model.getFactory().createBuild();
                p.setBuild(bld);
            }

            Plugin plg = bld.findPluginById(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
            if (plg == null) {
                //how come the plugin is not there? maybe using on wrong project?
                //check plugin management first.
                PluginManagement pm = bld.getPluginManagement();
                if (pm != null) {
                    plg = pm.findPluginById(SUREFIRE_GROUP_ID, SUREFIRE_ARTIFACT_ID);
                }
                if (plg == null) { // should not happen to begin with
                    plg = model.getFactory().createPlugin();
                    bld.addPlugin(plg);
                    plg.setGroupId(SUREFIRE_GROUP_ID);
                    plg.setArtifactId(SUREFIRE_ARTIFACT_ID);
                }
            }
            Configuration cnf = plg.getConfiguration();
            if (cnf == null) {
                cnf = model.getFactory().createConfiguration();
                plg.setConfiguration(cnf);
            }
            POMExtensibilityElement excludes = model.getFactory().createPOMExtensibilityElement(new QName("excludes")); //NOI18N
            POMExtensibilityElement exclude = model.getFactory().createPOMExtensibilityElement(new QName("exclude"));   //NOI18N
            excludes.addExtensibilityElement(exclude);
            exclude.setElementText("**/*Selenese*.java");    //NOI18N
            cnf.addExtensibilityElement(excludes);
        }
    }

    public static boolean isProjectReady(Project project) {
        POMModel model = getPOMModel(project);
        return ModelUtils.hasModelDependency(model, SELENIUM_GROUP_ID, SELENIUM_ARTIFACT_ID);
    }

    public static boolean isMavenProject(Project project) {
        return project.getLookup().lookup(NbMavenProject.class) != null;
    }

    private static POMModel getPOMModel(Project project) {
        FileObject pom = getPomFile(project);
        ModelSource source = Utilities.createModelSource(pom);
        return POMModelFactory.getDefault().getModel(source);
    }

    private static FileObject getPomFile(Project project) {
        return project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
    }
}
