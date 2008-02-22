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
package org.netbeans.modules.j2ee.geronimo2;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.geronimo2.customiser.GeCustomizerSupport;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 * Plugin Properties Singleton class
 * @author Max Sauer
 */
public class GePluginProperties {
    
    private static final String PROP_USERNAME      = InstanceProperties.USERNAME_ATTR;
    private static final String PROP_PASSWORD      = InstanceProperties.PASSWORD_ATTR;
    public static final String PROPERTY_DISPLAY_NAME = InstanceProperties.DISPLAY_NAME_ATTR;
    public static final String PROPERTY_ADMIN_PORT = "adminPort"; //NOI18N
    public static final String PROPERTY_WEB_SITE = "webSite"; //NOI18N
    public static final String PROPERTY_GE_HOME = "geHome"; //NOI18N
    public static final String PROPERTY_HOST = "host"; //NOI18N
    public static final String PROP_JAVA_PLATFORM = "java_platform"; //NOI18N
    public static final String PROP_JAVADOCS      = "javadocs";        // NOI18N
    public static final String PLAT_PROP_ANT_NAME = "platform.ant.name"; //NOI18N
    
    private InstanceProperties ip;
    private GeDeploymentManager dm;
    
    private static final int DEBUGPORT = 8787;
    
    public GePluginProperties(GeDeploymentManager dm) {
        this.dm = dm;
        ip = InstanceProperties.getInstanceProperties(dm.getUri());
    }
    
    public String getGeHomeLocation() {
        return ip.getProperty(PROPERTY_GE_HOME);
    }
    
    public JavaPlatform getJavaPlatform() {
        String currentJvm = ip.getProperty(PROP_JAVA_PLATFORM);
        JavaPlatformManager jpm = JavaPlatformManager.getDefault();
        JavaPlatform[] installedPlatforms = jpm.getPlatforms(null, new Specification("J2SE", null)); // NOI18N
        for (int i = 0; i < installedPlatforms.length; i++) {
            String platformName = (String)installedPlatforms[i].getProperties().get(PLAT_PROP_ANT_NAME);
            if (platformName != null && platformName.equals(currentJvm)) {
                return installedPlatforms[i];
            }
        }
        // return default platform if none was set
        return jpm.getDefaultPlatform();
    }
    
    public InstanceProperties getInstanceProperties() {
        return ip;
    }
    
    /**
     * Provides server libs necessary for ie. IDE editor features
     * 
     * Example library structure:
     * <code>geronimoHomeDir/repository/org/apache/geronimo/specs/geronimo-jsp_2.1_spec/1.0/geronimo-jsp_2.1_spec-1.0.jar</code>
     * @return list of archives
     */
    public List<URL> getClasses() {
        List<URL> list = new ArrayList<URL>();
        File serverDir = new File(getGeHomeLocation());
        //structure: geHome/archiveName/version/file.jar
        try{
            for(File file:new File(serverDir, "/repository/org/apache/geronimo/specs").listFiles()) { //NOI18N
                if(file.isDirectory()) {
                    File versionDir = file.listFiles()[0];
                    if(versionDir.isDirectory()) {
                        File archiveFile = versionDir.listFiles()[0];
                        if(FileUtil.isArchiveFile(archiveFile.toURI().toURL()))
                            list.add(GePluginUtils.fileToUrl(archiveFile));
                    }
                }
            }
        } catch(MalformedURLException ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
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
                    list.add(GePluginUtils.fileToUrl(j2eeDoc));
                }
            } catch (MalformedURLException e) {
                Logger.getLogger("global").log(Level.INFO, null, e);
            }
            return list;
        }
        return GeCustomizerSupport.tokenizePath(path);
    }
    
    public void setJavadocs(List<URL> path) {
        ip.setProperty(PROP_JAVADOCS, GeCustomizerSupport.buildPath(path));
        dm.getGePlatform().notifyLibrariesChanged();
    }
    
    public int getDebugPort() {
        return DEBUGPORT;
    }
    
    public static boolean isRunning(String host, int port) {
        if(null == host)
            return false;
        
        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket socket = new Socket();
            socket.connect(isa, 1);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static boolean isRunning(String host, String port) {
        try {
            return isRunning(host, Integer.parseInt(port));
        } catch(NumberFormatException e) {
            if(GeDebug.isEnabled()) {
                GeDebug.log("org.netbeans.modules.j2ee.geronimo2.GePluginProperties", "HOST: " + host);
                GeDebug.log("org.netbeans.modules.j2ee.geronimo2.GePluginProperties", "PORT: " + port);
            }
            return false;
        }
    }
    
    //getters
    public String getUsername() {
        String val = ip.getProperty(PROP_USERNAME);
        return val != null ? val : ""; // NOI18N
    }
    
    public void setUsername(String value) {
        ip.setProperty(PROP_USERNAME, value);
    }
    
    public String getPassword() {
        String val = ip.getProperty(PROP_PASSWORD);
        return val != null ? val : ""; // NOI18N
    }
    
    public void setPassword(String value) {
        ip.setProperty(PROP_PASSWORD, value);
    }
    
    public int getServerPort() {
        String val = ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
        if (val != null) {
            try {
                int port = Integer.parseInt(val);
                if (port >= 0 && port <= 65535) {
                    return port;
                }
            } catch (NumberFormatException nfe) {
                Logger.getLogger(GePluginProperties.class.getName()).log(Level.INFO, null, nfe);
            }
        }
        return 8080;
    }
    
    public void setServerPort(int port) {
        ip.setProperty(InstanceProperties.HTTP_PORT_NUMBER, Integer.toString(port));
    }
    
    public int getAdminPort() {
        String val = ip.getProperty(PROPERTY_ADMIN_PORT);
        if (val != null) {
            try {
                int port = Integer.parseInt(val);
                if (port >= 0 && port <= 65535) {
                    return port;
                }
            } catch (NumberFormatException nfe) {
                Logger.getLogger(GePluginProperties.class.getName()).log(Level.INFO, null, nfe);
            }
        }
        return 1099;
    }
    
    public void setAdminPort(int port) {
        ip.setProperty(PROPERTY_ADMIN_PORT, Integer.toString(port));
    }
}
