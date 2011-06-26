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
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.portalpack.servers.core.WizardPropertyReader;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.JEEServerLibraries;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.WizardDescriptor;

/**
 *
 * @author satyaranjan
 */
public class SunAppServerJEELibraries implements JEEServerLibraries {

    public List<File> getJEEServerLibraries(PSConfigObject psconfig) {

        String version = getGlassFishVersion(psconfig.getServerHome());

        List<File> jars = new ArrayList();

        if (version.equals(SunAppServerConstants.GLASSFISH_V2)) {
            String[] libs = {"javaee.jar", "appserv-jstl.jar"};

            for (int k = 0; k < libs.length; k++) {
                File libJar = new File(psconfig.getServerHome() + File.separator + "lib" + File.separator + libs[k]);
                if (libJar.exists()) {
                    jars.add(libJar);
                }
            }
        } else {

            File modulesFolder = new File(psconfig.getServerHome() + File.separator + "modules");
            File[] files = modulesFolder.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {

                    if (name.startsWith("javax.")) {
                        return true;
                    }
                    return false;
                }
            });

            if (files != null && files.length != 0) {
                for (File f : files) {
                    jars.add(f);
                }
            }

            File webFolder = new File(modulesFolder, "web");
            File jstlJar = new File(webFolder, "jstl-impl.jar");
            if (jstlJar.exists()) {

                jars.add(jstlJar);
            }
        }

        return jars;

    }

    public String getPortalServerLibraryLocation(WizardPropertyReader wr) {
        String domainDir = wr.getDomainDir();
        return domainDir + File.separator + "lib";
    }

    public String getPortalServerLibraryLocation(PSConfigObject pc) {
        String domainDir = pc.getDomainDir();
        return domainDir + File.separator + "lib";
    }

    public String getWebAppInstallDirectory(WizardPropertyReader wr) {
        return _getGlassfishApplicationDeployDir(wr.getDomainDir(), wr.getServerHome());
    }

    public String getWebAppInstallDirectory(PSConfigObject pc) {
        return _getGlassfishApplicationDeployDir(pc.getDomainDir(), pc.getServerHome());
    }

    public String getJEELibraryLocation(WizardPropertyReader wr) {
        return _getGlassfishJavaEELibraryLocation(wr.getServerHome());
    }

    public String getJEELibraryLocation(PSConfigObject pc) {
        return _getGlassfishJavaEELibraryLocation(pc.getServerHome());
    }

    public String getAppServerLibraryLocation(WizardPropertyReader wr) {
        return _getGlassfishJavaEELibraryLocation(wr.getServerHome());
    }

    public String getAppServerLibraryLocation(PSConfigObject pc) {
        return _getGlassfishJavaEELibraryLocation(pc.getServerHome());
    }

    public static String getGlassFishVersion(String glassfishHome) {

        File javaeeFile = new File(glassfishHome + File.separator + "lib" + File.separator + "javaee.jar");
        File module = new File(glassfishHome + File.separator + "modules");

        if (!javaeeFile.exists() && module.exists()) {
            return SunAppServerConstants.GLASSFISH_V3;
        }

        if(javaeeFile.exists() && module.exists()) {
            File ejbJar = new File(module,"javax.servlet.jar");
            if(ejbJar.exists()) {
                return SunAppServerConstants.GLASSFISH_V3;
            }
        }

        return SunAppServerConstants.GLASSFISH_V2;
    }

    private static String _getGlassfishApplicationDeployDir(String domainDir, String serverHome) {

        String version = getGlassFishVersion(serverHome);
        String deployDir = null;

        if (version.equals(SunAppServerConstants.GLASSFISH_V2)) {
            deployDir = domainDir + File.separator +
                    "applications" + File.separator +
                    "j2ee-modules";
        } else {
            deployDir = domainDir + File.separator +
                    "applications";
        }

        return deployDir;
    }

    private static String _getGlassfishJavaEELibraryLocation(String serverHome) {

        String version = getGlassFishVersion(serverHome);

        if (version.equals(SunAppServerConstants.GLASSFISH_V2)) {
            return serverHome + File.separator + "lib";

        } else {
            return serverHome + File.separator + "modules";
        }

    }

    public boolean isToolSupported(String toolName, PSConfigObject psconfig) {
         if (J2eePlatform.TOOL_WSCOMPILE.equals(toolName)) {
             return true;
         }
         return false;
    }

    private static final String APPSERV_WS_JAR = "lib/appserv-ws.jar"; //NOI18N
     // wsit jars
    private static final String WEBSERVICES_RT_JAR = "lib/webservices-rt.jar"; //NOI18N
    private static final String WEBSERVICES_TOOLS_JAR = "lib/webservices-tools.jar"; //NOI18N

    public File[] getToolClasspathEntries(String toolName, PSConfigObject psconfig) {
         if (J2eePlatform.TOOL_WSCOMPILE.equals(toolName)) {

            String serverHome = psconfig.getServerHome();
            String serverLib = serverHome + File.separator + "lib";
            String version = serverHome;
            
                return new File[] {
                    new File(serverLib, "j2ee.jar"),             //NOI18N
                    new File(serverLib, "saaj-api.jar"),         //NOI18N
                    new File(serverLib, "saaj-impl.jar"),        //NOI18N
                    new File(serverLib, "jaxrpc-api.jar"),       //NOI18N
                    new File(serverLib, "jaxrpc-impl.jar"),      //NOI18N
                    new File(serverLib, "endorsed/jaxp-api.jar"),//NOI18N
                    new File(serverHome, APPSERV_WS_JAR),        // possibly for AS 9
                    new File(serverHome, WEBSERVICES_TOOLS_JAR), // possibly for AS 9.1
                    new File(serverHome, WEBSERVICES_RT_JAR),    // possibly for AS 9.1
                };
            
        }

       return new File[0];
    }
}
