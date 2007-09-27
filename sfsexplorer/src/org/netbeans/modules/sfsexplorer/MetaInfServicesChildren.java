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
package org.netbeans.modules.sfsexplorer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * Children of MetaInfServiceNode.
 * Sandip V. Chitale (Sandip.Chitale@Sun.Com), David Strupl
 */
class MetaInfServicesChildren extends Children.Keys<MetaInfService> {
    private String platform;

    /**
     * 
     * @param platform 
     */
    MetaInfServicesChildren(String platform) {
        this.platform = platform;
    }

    /**
     * Computes the children only when they are really needed.
     */
    @Override protected void addNotify() {
        ClassLoader systemClassLoader = Lookup.getDefault().lookup(ClassLoader.class);
        if (systemClassLoader != null) {
            java.util.Map<String, MetaInfService> servicesMap = new LinkedHashMap<String, MetaInfService>();
            try {
                Enumeration<URL> services = systemClassLoader.getResources("META-INF/services"); // NOI18N
                while (services.hasMoreElements()) {
                    URL service = services.nextElement();
                    URLConnection urlConnection = service.openConnection();
                    if (urlConnection instanceof JarURLConnection) {
                        JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                        JarEntry jarEntry = jarURLConnection.getJarEntry();
                        if (jarEntry != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            Enumeration entries = jarFile.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = (JarEntry) entries.nextElement();
                                if (entry.getName().startsWith("META-INF/services/") && !entry.isDirectory()) { // NOI18N
                                    //sb.append(entry.getName().substring("META-INF/services/".length()) + "\n");
                                    String serviceClassName = entry.getName().substring("META-INF/services/".length());
                                    MetaInfService metaInfService = servicesMap.get(serviceClassName);
                                    if (metaInfService == null) {
                                        metaInfService = new MetaInfService(serviceClassName);
                                        servicesMap.put(serviceClassName, metaInfService);
                                    }
                                    InputStream inputStream = jarFile.getInputStream(entry);
                                    if (inputStream != null) {
                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                        String aLine;
                                        while ((aLine = bufferedReader.readLine()) != null) {
                                            if (aLine.trim().length() != 0) {
                                                if (aLine.startsWith("#")) {
                                                    if (!aLine.startsWith("#position=")) {
                                                        continue;
                                                    }
                                                }
                                                metaInfService.addProvider(aLine.trim());
                                            }
                                        }
                                        bufferedReader.close();
                                    }
                                }
                            }
                        }
                    }
                }
            }  catch (IOException ex) {
            }
            setKeys(servicesMap.values());
        }
    }

    /**
     * Overriden.
     * @param key 
     * @return 
     */
    protected Node[] createNodes(MetaInfService key) {
        return new Node[] {new MetaInfServiceNode(key, platform)};
    }
}