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

package org.netbeans.modules.j2ee.jetty.ide;

import org.netbeans.modules.j2ee.jetty.customizer.JetCustomizerSupport;
import org.netbeans.modules.j2ee.jetty.ide.ui.*;
import org.netbeans.modules.j2ee.jetty.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 * Plugin Properties Singleton class responsible for
 * provide access to javadoc, server classes, java
 * platform and instance properties
 * @author novakm
 */
public class JetPluginProperties {

    public static final String PROPERTY_DISPLAY_NAME = InstanceProperties.DISPLAY_NAME_ATTR;
    public static final String PROPERTY_JET_HOME = "jetHome"; //NOI18N
    public static final String PROPERTY_HOST = "localhost"; //NOI18N
    public static final String PROP_JAVA_PLATFORM = "java_platform"; //NOI18N
    public static final String PROP_JAVADOCS = "javadocs"; // NOI18N
    public static final String PLAT_PROP_ANT_NAME = "platform.ant.name"; //NOI18N
    public static final String RMI_PORT_PROP = "rmi_port"; //NOI18N
    public static final int DEFAULT_RMI_PORT = 7887;			// only default value
    public static final int HTTP_PORT = 8080;
    private static final int DEBUGPORT = 8787;
    private static final Logger LOGGER = Logger.getLogger(JetPluginProperties.class.getName());
    private InstanceProperties ip;
    private JetDeploymentManager dm;

    public JetPluginProperties(JetDeploymentManager dm) {
        this.dm = dm;
        ip = InstanceProperties.getInstanceProperties(dm.getUri());
    }

    /**
     * @return location of jetty installation
     */
    public String getJetHomeLocation() {
        return ip.getProperty(PROPERTY_JET_HOME);
    }

    public JavaPlatform getJavaPlatform() {
        String currentJvm = ip.getProperty(PROP_JAVA_PLATFORM);
        JavaPlatformManager jpm = JavaPlatformManager.getDefault();
        JavaPlatform[] installedPlatforms = jpm.getPlatforms(null, new Specification("J2SE", null)); // NOI18N
        for (int i = 0; i < installedPlatforms.length; i++) {
            String platformName = (String) installedPlatforms[i].getProperties().get(PLAT_PROP_ANT_NAME);
            if (platformName != null && platformName.equals(currentJvm)) {
                return installedPlatforms[i];
            }
        }
        // return default platform if none was set
        return jpm.getDefaultPlatform();
    }

    /**
     * @return instance properties
     */
    public InstanceProperties getInstanceProperties() {
        return ip;
    }

    /**
     * Returns list of all .jar files URL in lib folder of jetty installation
     * location
     * @return list of classes URL
     */
    public List<URL> getClasses() {
        List<URL> list = new ArrayList<URL>();
        File serverDir = new File(getJetHomeLocation());
        try {
            for (File file : new File(serverDir, "lib").listFiles()) {
                if (file.isDirectory()) {
                    for (File file2 : new File(file, "").listFiles()) {
                        if (FileUtil.isArchiveFile(file2.toURI().toURL())) {
                            list.add(JetPluginUtils.fileToUrl(file2));
                        }
                    }
                } else if (FileUtil.isArchiveFile(file.toURI().toURL())) {
                    list.add(JetPluginUtils.fileToUrl(file));
                }
            }
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return list;
    }

    public List<URL> getJavadocs() {
        String path = ip.getProperty(PROP_JAVADOCS);
        if (path == null) {
            ArrayList<URL> list = new ArrayList<URL>();
            try {
                File j2eeDoc = InstalledFileLocator.getDefault().locate("docs/javaee5-doc-api.zip", null, false); // NOI18N
                if (j2eeDoc != null) {
                    list.add(JetPluginUtils.fileToUrl(j2eeDoc));
                }

            } catch (MalformedURLException e) {
                LOGGER.log(Level.WARNING, null, e);
            }

            return list;
        }

        return JetCustomizerSupport.tokenizePath(path);
    }

    public void setJavadocs(List<URL> path) {
        ip.setProperty(PROP_JAVADOCS, JetCustomizerSupport.buildPath(path));
        dm.getJetPlatform().notifyLibrariesChanged();
    }

    public int getDebugPort() {
        return DEBUGPORT;
    }


}
