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
 * Satyaranjan
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.jboss;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.servers.core.WizardPropertyReader;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.JEEServerLibraries;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;

/**
 *
 * @author satyaranjan
 */
public class JBossJEELabraries implements JEEServerLibraries {

	public List<File> getJEEServerLibraries(PSConfigObject psconfig) {
		List<File> jars = new ArrayList();
		String serverInstanceDir =
				psconfig.getProperty(JBConstant.SERVER_DIR);

		String jbosswebSarDir =
				serverInstanceDir + File.separator + "deploy" + File.separator + "jbossweb.sar";
		String[] jbosswebLibs = {"jstl.jar",
			"jsf-libs" + File.separator + "jsf-api.jar"};
		for (int i = 0; i < jbosswebLibs.length; i++) {
			File libJar = new File(jbosswebSarDir +
					File.separator + jbosswebLibs[i]);
			if (libJar.exists()) {
				jars.add(libJar);
			}
		}

		String jbossRootDir =
                    psconfig.getProperty(JBConstant.ROOT_DIR);

            String commonLibDir =
                    jbossRootDir +
                    File.separator + "common" + File.separator + "lib";
            String[] commonLibs = {"jboss-javaee.jar", "servlet-api.jar",
            "jsp-api.jar", "el-api.jar", "mail.jar", "jboss-jsr77.jar",
            "ejb3-persistence.jar", "jbossws-native-jaxws.jar",
            "jbossws-native-jaxws-ext.jar", "jbossws-native-jaxrpc.jar",
            "jbossws-native-saaj.jar"};
            for(int i = 0; i < commonLibs.length; i++) {
                File libJar = new File(commonLibDir +
                        File.separator + commonLibs[i]);
                if (libJar.exists()) {
                    jars.add(libJar);
                }
            }

			String endorsedLibDir =
                    jbossRootDir +
                    File.separator + "lib" + File.separator + "endorsed";
            String[] endorsedLibs = {"activation.jar", "jaxb-api.jar",
            "stax-api.jar"};
            for(int i = 0; i < endorsedLibs.length; i++) {
                File libJar = new File(endorsedLibDir +
                        File.separator + endorsedLibs[i]);
                if (libJar.exists()) {
                    jars.add(libJar);
                }
            }

		return jars;
	}

	public String getPortalServerLibraryLocation(WizardPropertyReader wr) {
		String serverInstanceDir =
                    wr.getProperty(JBConstant.SERVER_DIR);
		return _getPortalServerLibraryLocation(serverInstanceDir);

	}

	public String getPortalServerLibraryLocation(PSConfigObject pc) {
		String serverInstanceDir =
                    pc.getProperty(JBConstant.SERVER_DIR);
		return _getPortalServerLibraryLocation(serverInstanceDir);
	}

	private String _getPortalServerLibraryLocation(String serverInstanceDir) {
		return serverInstanceDir + File.separator + "lib";
	}

	public String getWebAppInstallDirectory(WizardPropertyReader wr) {
		String serverInstanceDir =
                    wr.getProperty(JBConstant.SERVER_DIR);
		return serverInstanceDir + File.separator + "deploy";
	}

	public String getWebAppInstallDirectory(PSConfigObject pc) {
		String serverInstanceDir =
                    pc.getProperty(JBConstant.SERVER_DIR);
		return serverInstanceDir + File.separator + "deploy";
	}

	public String getJEELibraryLocation(WizardPropertyReader wr) {
		String jbossRootDir =
                    wr.getProperty(JBConstant.ROOT_DIR);
		return _getJEELibraryLocation(jbossRootDir);
	}

	public String getJEELibraryLocation(PSConfigObject pc) {
		String jbossRootDir =
                    pc.getProperty(JBConstant.ROOT_DIR);
		return _getJEELibraryLocation(jbossRootDir);
	}

	private String _getJEELibraryLocation(String jbossRootDir) {
            String commonLibDir =
                    jbossRootDir +
                    File.separator + "common" + File.separator + "lib";
			return commonLibDir;
	}

	public String getAppServerLibraryLocation(WizardPropertyReader wr) {
		return getJEELibraryLocation(wr);
	}

	public String getAppServerLibraryLocation(PSConfigObject pc) {
		return getJEELibraryLocation(pc);
	}

    public boolean isToolSupported(String toolName, PSConfigObject psconfig) {
        return false;
    }

    public File[] getToolClasspathEntries(String toolName, PSConfigObject psconfig) {
        return new File[0];
    }
}
