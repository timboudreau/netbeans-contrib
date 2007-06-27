/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
class MetaInfServicesChildren extends Children.Keys {
    private List services;
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
    protected void addNotify() {
        ClassLoader systemClassLoader = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
        if (systemClassLoader != null) {
            java.util.Map/*<String, MetaInfService>*/ servicesMap = new LinkedHashMap/*<String, MetaInfService>*/();
            try {
                Enumeration/*<URL>*/ services = systemClassLoader.getResources("META-INF/services"); // NOI18N
                while (services.hasMoreElements()) {
                    URL service = (URL)services.nextElement();
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
                                    MetaInfService metaInfService = (MetaInfService) servicesMap.get(serviceClassName);
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
    protected Node[] createNodes(Object key) {
        return new Node[] {new MetaInfServiceNode((MetaInfService) key, platform)};
    }
}