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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.j2ee.geronimo2;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Max Sauer
 */
public class GeClassLoader extends URLClassLoader {
    
    private String serverRoot;
    private static Map<String, GeClassLoader> instances = new HashMap<String, GeClassLoader>();
    private ClassLoader oldLoader;
    
    /**
     * Returns instance of server for a specified server root
     * @param serverRoot 
     * @return server instance
     */
    public static GeClassLoader getInstance(String serverRoot) {
        GeClassLoader instance = instances.get(serverRoot);
        if (instance == null) {
            instance = new GeClassLoader(serverRoot);
            instances.put(serverRoot, instance);
        }
        return instance;
    }
    
    private GeClassLoader(String serverRoot) {
        super(new URL[0], GeDeploymentFactory.class.getClassLoader());
        this.serverRoot = serverRoot;
        
        //directory-load
        // add the required directories to the class path
        File[] directories = new File[] {
            new File(serverRoot + "/lib/"),                            // NOI18N
            new File(serverRoot + "/repository/org/apache/geronimo/modules/geronimo-deploy-jsr88/2.0.2/"),
            new File(serverRoot + "/repository/org/apache/geronimo/modules/geronimo-system/2.0.2/"),
            new File(serverRoot + "/repository/org/apache/geronimo/modules/geronimo-deploy-config/2.0.2/"),
            new File(serverRoot + "/repository/org/apache/geronimo/modules/geronimo-common/2.0.2/")
        };
        
        // for each directory add all the .jar files to the class path
        // and finally add the directory itself
        for (int i = 0; i < directories.length; i++) {
            File directory = directories[i];
            if (directory.exists() && directory.isDirectory()) {
                File[] children = directory.listFiles(new JarFileFilter());
                for (int j = 0; j < children.length; j++) {
                    try {
                        addURL(children[j].toURL());
                    } catch (MalformedURLException e) {
                        // do nothing just skip this jar file
                    }
                }
            }
            try {
                addURL(directory.toURL());
            } catch (MalformedURLException e) {
                // do nothing just skip this directory
            }
        }

    }
    
     public synchronized void updateLoader() {
        if (!Thread.currentThread().getContextClassLoader().equals(this)) {
            oldLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this);
        }
    }
    
    public synchronized void restoreLoader() {
        if (oldLoader != null) {
            Thread.currentThread().setContextClassLoader(oldLoader);
            oldLoader = null;
        }
    }
    
    /**
     * File filter that accepts only .jar files.
     * 
     * @author Kirill Sorokin
     */
    private static class JarFileFilter implements FileFilter {
        
        /**
         * Checks whether the supplied file complies with the filter 
         * requirements.
         * 
         * @return whether the file complies with the requirements
         */
        public boolean accept(File file) {
            // check the file's extension, if it's '.jar' then the file is ok
            if (file.getName().endsWith(".jar")) {                     // NOI18N
                return true;
            } else {
                return false;
            }
        }
    }
}
