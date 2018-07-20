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

package org.netbeans.modules.j2ee.jetty.ide.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.jetty.ide.JetPluginProperties;
import org.openide.filesystems.FileUtil;

/**
 * Class providing some helper methods
 * @author novakm
 */
@SuppressWarnings("unchecked")
public class JetPluginUtils {

    public static final String CONFIG_DIR = File.separator + "j2ee" + File.separator +
            "home" + File.separator + "config"; // NOI18N


    /** Creates a new instance of JetPluginUtils */
    public JetPluginUtils() {
    }
    //--------------- checking for possible server directory -------------
    private static Collection fileColl = new java.util.ArrayList();
    //initialize necessary elements contained in a typicall Jetty installation (according to README)
    
   
    static {
        fileColl.add("contexts");
        fileColl.add("etc/jetty.xml");
        fileColl.add("etc/jetty-jmx.xml");
        fileColl.add("lib");
        fileColl.add("start.jar");
        fileColl.add("webapps");
    }

    /**
     * Returns http port for given instanceURL
     * @param instanceURL
     * @return port number
     */
    public static String getHttpPort(String instanceURL) {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(instanceURL);
        if (ip != null) {
            return ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
        } else {
            return "8080";
        }
    }

    /**
     * 
     * @param serverLocation
     * @return Hashtable
     * key = server name
     * value = server folder full path
     */
    @SuppressWarnings("unchecked")   
    public static Hashtable getRegisteredDomains(String serverLocation) {
        Hashtable result = new Hashtable();

        if (isValidJetServerLocation(new File(serverLocation))) {
            File file = new File(serverLocation);
            result.put(file.getName(), file.getAbsolutePath());
        }
        return result;
    }

    /**
     * Verifies server instace location
     * @return true if it is, false otherwise
     */
    public static boolean isValidJetServerLocation(File candidate) {
        if (null == candidate ||
                !candidate.exists() ||
                !candidate.canRead() ||
                !candidate.isDirectory() ||
                !hasRequiredChildren(candidate, fileColl)) {
            return false;
        }
        return true;
    }

    private static boolean hasRequiredChildren(File candidate, Collection requiredChildren) {
        if (null == candidate) {
            return false;
        }
        String[] children = candidate.list();
        if (null == children) {
            return false;
        }
        if (null == requiredChildren) {
            return true;
        }
        Iterator iter = requiredChildren.iterator();
        while (iter.hasNext()) {
            String next = (String) iter.next();
            File test = new File(candidate.getPath() + File.separator + next);
            if (!test.exists()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param ip - instance properties
     * @return true if it is local serevr
     */
    public static boolean isLocalServer(InstanceProperties ip) {
        String host = ip.getProperty(JetPluginProperties.PROPERTY_HOST);

        if (host != null && host.equals("localhost")) {
            return true;
        }
        return false;
    }

    /**
     * @param file
     * @return String with URL of the file
     * @throws java.net.MalformedURLException if the URL isn't correct
     */
    public static URL fileToUrl(File file) throws MalformedURLException {
        URL url = file.toURI().toURL();
        if (FileUtil.isArchiveFile(url)) {
            url = FileUtil.getArchiveRoot(url);
        }
        return url;
    }
}
