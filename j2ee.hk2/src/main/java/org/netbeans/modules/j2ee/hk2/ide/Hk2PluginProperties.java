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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.hk2.ide;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.customizer.CustomizerSupport;

import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 * Hk2PluginProperties
 */
public class Hk2PluginProperties {
    
    /**
     *
     */
    public static final String PROPERTY_DISPLAY_NAME = InstanceProperties.DISPLAY_NAME_ATTR;
    /**
     *
     */
    public static final String PROPERTY_ADMIN_PORT = "adminPort"; //NOI18N
    /**
     *
     */
    public static final String PROPERTY_PORT = "port"; //NOI18N
    /**
     *
     */
    public static final String PROPERTY_HK2_HOME = "HK2Home"; //NOI18N
    /**
     *
     */
    public static final String PROPERTY_HOST = "host"; //NOI18N
    /**
     *
     */
    public static final String PROP_JAVA_PLATFORM = "java_platform"; //NOI18N
    /**
     *
     */
    public static final String PROP_JAVADOCS      = "javadocs";        // NOI18N
    /**
     *
     */
    public static final String PLAT_PROP_ANT_NAME = "platform.ant.name"; //NOI18N
    
    private InstanceProperties ip;
    private Hk2DeploymentManager dm;
    
    private static final int DEBUGPORT = 8787;
    
    /**
     *
     * @param dm
     */
    public Hk2PluginProperties(Hk2DeploymentManager dm) {
        this.dm = dm;
        ip = InstanceProperties.getInstanceProperties(dm.getUri());
    }
    
    /**
     *
     * @return
     */
    public String getHk2JHomeLocation() {
        return ip.getProperty(PROPERTY_HK2_HOME);
    }
    
    /**
     *
     * @return
     */
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
    
    /**
     *
     * @return
     */
    public InstanceProperties getInstanceProperties() {
        return ip;
    }
    
    /**
     *
     * @return
     */
    public List<URL> getClasses() {
        List<URL> list = new ArrayList<URL>();
        File serverDir = new File(getHk2JHomeLocation());
        try {
            //get the wrapper jar (empty) that lists all the dependant ee 5 jars.
            //needed for compilation with the correct jars
            File jarDir = new File(serverDir, "modules");
            if(!jarDir.exists()) {
                // !PW Older V3 installs (pre 12/01/07) put jars in lib folder.
                jarDir = new File(serverDir, "lib");
                if(!jarDir.exists()) {
                    // jar folder does not exist, return empty list.
                    return list;
                }
            }
            File ee5lib = new File(jarDir, "javaee-5.0-SNAPSHOT.jar");  // V3 P2 M2
            if (!ee5lib.exists()) {
                ee5lib = new File(jarDir, "javaee-5.0.jar"); // V1 P2 M1 uses an older name.
                if (!ee5lib.exists()) {
                    return list;
                }
            }
            
            Manifest m = new JarFile(ee5lib).getManifest();
            String dependantJars = m.getMainAttributes().getValue("Class-Path");
            StringTokenizer token = new StringTokenizer(dependantJars, " ");
            while (token.hasMoreTokens()) {
                String jar = token.nextToken();
                File j = new File(jarDir,  jar);
                list.add(CustomizerSupport.fileToUrl(j));
            }
        } catch(Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return list;
    }
    
    /**
     *
     * @return
     */
    public List<URL> getJavadocs() {
        String path = ip.getProperty(PROP_JAVADOCS);
        if (path == null) {
            ArrayList<URL> list = new ArrayList<URL>();
            try {
                File j2eeDoc = InstalledFileLocator.getDefault().locate("docs/javaee-doc-api.jar", null, false); // NOI18N
                if (j2eeDoc != null) {
                    list.add(fileToUrl(j2eeDoc));
                }
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            return list;
        }
        return tokenizePath(path);
    }
    /**
     * Splits an Ant-style path specification into the list of URLs.  Tokenizes on
     * <code>:</code> and <code>;</code>, paying attention to DOS-style components
     * such as <samp>C:\FOO</samp>. Also removes any empty components.
     *
     * @param path An Ant-style path (elements arbitrary) using DOS or Unix separators
     *
     * @return A tokenization of the specified path into the list of URLs.
     */
    public static List<URL> tokenizePath(String path) {
        try {
            List<URL> l = new ArrayList();
            StringTokenizer tok = new StringTokenizer(path, ":;", true); // NOI18N
            char dosHack = '\0';
            char lastDelim = '\0';
            int delimCount = 0;
            while (tok.hasMoreTokens()) {
                String s = tok.nextToken();
                if (s.length() == 0) {
                    // Strip empty components.
                    continue;
                }
                if (s.length() == 1) {
                    char c = s.charAt(0);
                    if (c == ':' || c == ';') {
                        // Just a delimiter.
                        lastDelim = c;
                        delimCount++;
                        continue;
                    }
                }
                if (dosHack != '\0') {
                    // #50679 - "C:/something" is also accepted as DOS path
                    if (lastDelim == ':' && delimCount == 1 && (s.charAt(0) == '\\' || s.charAt(0) == '/')) {
                        // We had a single letter followed by ':' now followed by \something or /something
                        s = "" + dosHack + ':' + s;
                        // and use the new token with the drive prefix...
                    } else {
                        // Something else, leave alone.
                        l.add(fileToUrl(new File(Character.toString(dosHack))));
                        // and continue with this token too...
                    }
                    dosHack = '\0';
                }
                // Reset count of # of delimiters in a row.
                delimCount = 0;
                if (s.length() == 1) {
                    char c = s.charAt(0);
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                        // Probably a DOS drive letter. Leave it with the next component.
                        dosHack = c;
                        continue;
                    }
                }
                l.add(fileToUrl(new File(s)));
            }
            if (dosHack != '\0') {
                //the dosHack was the last letter in the input string (not followed by the ':')
                //so obviously not a drive letter.
                //Fix for issue #57304
                l.add(fileToUrl(new File(Character.toString(dosHack))));
            }
            return l;
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
            return new ArrayList();
        }
    }
    /**
     *
     * @param file
     * @return
     * @throws java.net.MalformedURLException
     */
    public static URL fileToUrl(File file) throws MalformedURLException {
        URL url = file.toURI().toURL();
        if (FileUtil.isArchiveFile(url)) {
            url = FileUtil.getArchiveRoot(url);
        }
        return url;
    }
    /**
     * Creates an Ant-style path specification from the specified list of URLs.
     *
     * @param The list of URLs.
     *
     * @return An Ant-style path specification.
     */
    public static String buildPath(List<URL> path) {
        String PATH_SEPARATOR = System.getProperty("path.separator"); // NOI18N
        StringBuffer sb = new StringBuffer(path.size() * 16);
        for (Iterator<URL> i = path.iterator(); i.hasNext(); ) {
            sb.append(urlToString(i.next()));
            if (i.hasNext()) {
                sb.append(PATH_SEPARATOR);
            }
        }
        return sb.toString();
    }
    /** Return string representation of the specified URL. */
    private static String urlToString(URL url) {
        if ("jar".equals(url.getProtocol())) { // NOI18N
            URL fileURL = FileUtil.getArchiveFile(url);
            if (FileUtil.getArchiveRoot(fileURL).equals(url)) {
                // really the root
                url = fileURL;
            } else {
                // some subdir, just show it as is
                return url.toExternalForm();
            }
        }
        if ("file".equals(url.getProtocol())) { // NOI18N
            File f = new File(URI.create(url.toExternalForm()));
            return f.getAbsolutePath();
        } else {
            return url.toExternalForm();
        }
    }
    
    /**
     *
     * @param path
     */
    public void setJavadocs(List<URL> path) {
        ip.setProperty(PROP_JAVADOCS, buildPath(path));
    }
    
    /**
     *
     * @return
     */
    public int getDebugPort() {
        return DEBUGPORT;
    }
    
    /**
     *
     * @param host
     * @param port
     * @return
     */
    public static boolean isRunning(String host, int port) {
        if(null == host) {
            return false;
        }
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
    
    /**
     *
     * @param host
     * @param port
     * @return
     */
    public static boolean isRunning(String host, String port) {
        try {
            return isRunning(host, Integer.parseInt(port));
        } catch(NumberFormatException e) {
            return false;
        }
    }
}