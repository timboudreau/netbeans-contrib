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

package org.netbeans.modules.j2ee.oc4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michal Mocnak
 */
public class OC4JClassLoader extends URLClassLoader {
    
    private ClassLoader oldLoader;
    
    private static Map<String, OC4JClassLoader> instances = new HashMap<String, OC4JClassLoader>();
    
    /**
     * 
     * @param serverRoot 
     * @return 
     */
    public static OC4JClassLoader getInstance(String serverRoot) {
        OC4JClassLoader instance = instances.get(serverRoot);
        if (instance == null) {
            instance = new OC4JClassLoader(serverRoot);
            instances.put(serverRoot, instance);
        }
        return instance;
    }
    
    private OC4JClassLoader(String serverRoot) {
        super(new URL[0], OC4JDeploymentFactory.class.getClassLoader());
        
        try{
            URL[] urls = new URL[] {
                new File(serverRoot + "/j2ee/home/lib/adminclient.jar").toURI().toURL(),             // NOI18N
                new File(serverRoot + "/j2ee/home/oc4jclient.jar").toURI().toURL(),             // NOI18N
                new File(serverRoot + "/webservices/lib/wsserver.jar").toURI().toURL(),                   // NOI18N
                new File(serverRoot + "/j2ee/home/lib/ejb.jar").toURI().toURL(),                   // NOI18N
                new File(serverRoot + "/j2ee/home/lib/mx4j-jmx.jar").toURI().toURL(),                   // NOI18N
                new File(serverRoot + "/j2ee/home/lib/jmxri.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/j2ee/home/lib/jmx_remote_api.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/j2ee/home/lib/jaas.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/lib/xmlparserv2.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/oracle/lib/xmlparserv2.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/j2ee/home/lib/javax77.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/j2ee/home/lib/javax88.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/diagnostics/lib/ojdl.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/oracle/lib/dms.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/oracle/jlib/dms.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/lib/dms.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/jlib/dms.jar").toURI().toURL(),           // NOI18N
                new File(serverRoot + "/j2ee/home/lib/jta.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/j2ee/home/lib/jms.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/j2ee/home/lib/connector.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/opmn/lib/optic.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/oracle/jlib/oraclepki.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/jlib/oraclepki.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/oracle/jlib/ojpse.jar").toURI().toURL(),
                new File(serverRoot + "/oracle/jdbc/lib/ojdbc14d ms.jar").toURI().toURL(),        // NOI18N// NOI18N
                new File(serverRoot + "/oracle/jdbc/lib/ocrs12.jar").toURI().toURL(),        // NOI18N
                new File(serverRoot + "/oracle/rdbms/jlib/aqapi.jar").toURI().toURL(),        // NOI18N                
                new File(serverRoot + "/jlib/ojpse.jar").toURI().toURL()           // NOI18N
            };
            for (int i = 0; i < urls.length; i++) {
                addURL(urls[i]);
            }
        }catch(Exception e) {
            Logger.getLogger("global").log(Level.WARNING, null, e);
        }
    }
    
    public Enumeration<URL> getResources(String name) throws IOException {
        // get rid of annoying warnings
        if (name.indexOf("jndi.properties") != -1) { // NOI18N
            return Collections.enumeration(Collections.<URL>emptyList());
        }
        
        return super.getResources(name);
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
}