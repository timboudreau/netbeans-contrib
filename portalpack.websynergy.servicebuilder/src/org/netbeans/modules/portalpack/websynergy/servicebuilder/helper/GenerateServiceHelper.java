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
package org.netbeans.modules.portalpack.websynergy.servicebuilder.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatConstant;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.ServiceBuilderConstant;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author satyaranjan
 */
public class GenerateServiceHelper {

    private static Logger logger = Logger.getLogger(ServiceBuilderConstant.LOGGER_NAME);
    private static String BUILD_FILE_NAME = "build-service.xml";
    private static String LR_PREFIX = "liferay";
    private static String SB_DIR = System.getProperty("netbeans.user") + File.separator + "servicebuilder";
    private static GenerateServiceHelper instance;

    private GenerateServiceHelper() {
        initBuildScript();
    }

    public static GenerateServiceHelper getInstance() {

        if (instance == null) {

            synchronized (GenerateServiceHelper.class) {
                if (instance == null) {
                    instance = new GenerateServiceHelper();
                }
            }
        }

        return instance;
    }

    private void initBuildScript() {
        InputStream input = this.getClass().getClassLoader().
                getResourceAsStream("org/netbeans/modules/portalpack/websynergy/servicebuilder/resources/build-service.xml");
        OutputStream output = null;

        File serviceBuilderDir = new File(System.getProperty("netbeans.user") + File.separator + "servicebuilder");

        File tempFile = new File(serviceBuilderDir, BUILD_FILE_NAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        if (!serviceBuilderDir.exists()) {
            serviceBuilderDir.mkdirs();
        }
        File buildFile = new File(serviceBuilderDir, BUILD_FILE_NAME);
        buildFile.deleteOnExit();

        try {
            output = new FileOutputStream(buildFile);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "error", ex);
        }

        try {
            FileUtil.copy(input, output);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "error", ex);
        }

        if (output != null) {
            try {
                output.flush();
                output.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "error", ex);
            }

        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "error", ex);
            }
        }

    }

    public static boolean generateService(FileObject serviceXml) {

        Project project = getProject(serviceXml);
        PSConfigObject psconfig = getSelectedServerProperties(project);

        final Properties props = getServerAntProperties(psconfig);
        setAdditionalProperties(project, props);
        props.setProperty("input.file", FileUtil.toFile(serviceXml).getAbsolutePath());
        String srcDir = getSourceDir(project);
        if (srcDir != null) {
            props.setProperty("src.dir", srcDir);
        } else {
            props.setProperty("src.dir", props.getProperty("docroot"));
        }

        File serviceFile = new File(System.getProperty("netbeans.user") + File.separator + "servicebuilder" + File.separator + BUILD_FILE_NAME);
        final FileObject serviceFileObj = FileUtil.toFileObject(serviceFile);

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    ExecutorTask task = ActionUtils.runTarget(serviceFileObj, new String[]{"download-jars"}, props);
                    task.addTaskListener(new TaskListener() {

                        public void taskFinished(Task task) {
                            try {
                                ActionUtils.runTarget(serviceFileObj, new String[]{"build-service"}, props);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            } catch (IllegalArgumentException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    });
                   // task.result();
                    
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });

        return true;

    }

    private static Properties getServerAntProperties(PSConfigObject psConfig) {

        Properties props = new Properties();
        String deployDir = null;
        if (psConfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)) {
            String domainDir = psConfig.getDomainDir();

            props.setProperty("app.server.dir", domainDir);
            props.setProperty("app.server.lib.global.dir", domainDir + File.separator + "lib");
            //check for glassfish V2
            File wsDeployLoc = new File(domainDir + File.separator +
                    "applications" + File.separator +
                    "j2ee-modules" + File.separator +
                    "websynergy");
            File lrDeployLoc = new File(domainDir + File.separator +
                    "applications" + File.separator +
                    "j2ee-modules" + File.separator +
                    "liferay-portal");

            if (wsDeployLoc.exists()) {
                deployDir = wsDeployLoc.getAbsolutePath();

            } else if (lrDeployLoc.exists()) {
                deployDir = lrDeployLoc.getAbsolutePath();

            } else {

                //check for glassfish V3
                wsDeployLoc = new File(domainDir + File.separator + "applications" + File.separator + "websynergy");
                lrDeployLoc = new File(domainDir + File.separator + "applications" + File.separator + "liferay-portal");
                if (wsDeployLoc.exists()) {
                    deployDir = wsDeployLoc.getAbsolutePath();

                } else if (lrDeployLoc.exists()) {
                    deployDir = lrDeployLoc.getAbsolutePath();
                }
            }
        } else if (psConfig.getServerType().equals(ServerConstants.TOMCAT_5_X)) {

            String tomcatHome = psConfig.getProperty(TomcatConstant.CATALINA_HOME);

            props.setProperty("app.server.dir", tomcatHome);

            File deployLoc = new File(tomcatHome + File.separator + "webapps" + File.separator + "ROOT");
            if (deployLoc.exists()) {
                deployDir = deployLoc.getAbsolutePath();

                props.setProperty("app.server.lib.global.dir", tomcatHome + File.separator + "common" + File.separator + "lib" + File.separator + "ext");
            }
        }

        if (deployDir != null || deployDir.trim().length() != 0) {

            props.setProperty("app.server.classes.portal.dir", deployDir + File.separator + "WEB-INF" + File.separator + "classes");
            props.setProperty("app.server.lib.portal.dir", deployDir + File.separator + "WEB-INF" + File.separator + "lib");
            props.setProperty("app.server.portal.dir", deployDir);
        }

        return props;
    }

    private static void setAdditionalProperties(Project project, Properties props) {

        props.setProperty("lib.dir", SB_DIR + File.separator + "lib");
        props.setProperty("project.dir", FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath());
        props.setProperty("project.name", project.getProjectDirectory().getName());

        WebModule wm = getWebModule(project);
        if (wm == null) {
            return;
        }
        props.setProperty("docroot", FileUtil.toFile(wm.getDocumentBase()).getAbsolutePath());

    }

    private static PSConfigObject getSelectedServerProperties(Project prj) {

        if (prj == null) {
            return null;
        }
        J2eeModuleProvider jmp =
                (J2eeModuleProvider) prj.getLookup().lookup(J2eeModuleProvider.class);

        String serverID = jmp.getServerInstanceID();

        if (serverID == null || !serverID.startsWith(LR_PREFIX)) {
            return null;
        }
        PSConfigObject pc = PSConfigObject.getPSConfigObject(serverID);
        return pc;
    }

    private static Project getProject(FileObject file) {

        return FileOwnerQuery.getOwner(file);
    }

    public static WebModule getWebModule(Project project) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());

        if (wm != null) {
            return wm;
        }

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (SourceGroup group : groups) {
            wm = WebModule.getWebModule(group.getRootFolder());
            if (wm != null) {
                return wm;
            }
        }

        return null;
    }

    private static String getSourceDir(Project project) {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        if (groups != null && groups.length != 0) {

            FileObject rootFolder = groups[0].getRootFolder();
            if (rootFolder == null) {
                return null;
            }
            File file = FileUtil.toFile(rootFolder);
            return file.getAbsolutePath();
        }

        return null;
    }
}
