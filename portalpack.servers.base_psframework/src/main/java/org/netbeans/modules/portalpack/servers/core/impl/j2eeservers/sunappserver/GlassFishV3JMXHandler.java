/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 *
 * @author satya
 */
public class GlassFishV3JMXHandler extends GlassFishJMXHandler {

    private static Logger logger = Logger.getLogger(GlassFishV3JMXHandler.class.getName());

    private final static String applicationMBeanName = "v3:pp=/domain/applications,type=application";

    private final static String serverMBeanName = "v3:pp=,type=DomainRoot,name=v3";

    public GlassFishV3JMXHandler(String host, String adminUser,
            String password, int adminPort){

        super(host,adminUser,password,adminPort);

    }

    private MBeanServerConnection getMBeanServerConnection()
                                                            throws Throwable{
        return getMBeanServerConnection(host,adminPort,adminUser,adminPassword);
    }

    @Override
    protected MBeanServerConnection
        getSecureMBeanServerConnection(String host,
                                       int port,
                                       String user,
                                       String password) throws Throwable{

        
        return getMBeanServerConnection(host, port, user, password);
    }

    @Override
    protected MBeanServerConnection getMBeanServerConnection
                                            (String host,
                                             int port,
                                             String user,
                                             String password) throws Throwable{

        final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" +
                                  host + ":" + port +"/jmxrmi");

        final Map env = new HashMap();

        String[] credentials = new String[]{user,password};
        env.put(JMXConnector.CREDENTIALS, credentials);

       // final String PKGS = "com.sun.enterprise.admin.jmx.remote.protocol";

        //env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, PKGS);
//        env.put(ADMIN_USER_ENV_PROPERTY_NAME, user );
//        env.put( ADMIN_PASSWORD_ENV_PROPERTY_NAME, password);
//        env.put(HTTP_AUTH_PROPERTY_NAME, DEFAULT_HTTP_AUTH_SCHEME);
        final JMXConnector conn = JMXConnectorFactory.connect(url, env);
        return conn.getMBeanServerConnection();
    }


    //Because of reload bug in V3 through MBean, reload is done through .reload file. Comment this method, once the bug is fixed.
    @Override
    public void reload(String contextRoot) throws Throwable{
 //       contextRoot = "/" + contextRoot;

        try {
            File moduleDir = getModuleDirectory(contextRoot);
            if(moduleDir != null)
                reloadThroughFile(moduleDir);
        } catch (Throwable t){
            t.printStackTrace();
        }
        System.out.println("RELOAD Successful : "+contextRoot);
     }

    @Override
     public Object getConfigDirectory() throws Throwable{
        String oName = serverMBeanName;
        ObjectName appMBean = new ObjectName(oName);
        try{
            return null;
            //return getMBeanServerConnection().getAttribute(appMBean,"ConfigDir");
        }catch (Throwable t) {
            logger.log(Level.WARNING,"error",t);
            return null;
        }
     }

    @Override
     public File getModuleDirectory(TargetModuleID module ) throws Throwable{
         String mid = module.getModuleID();
         if(mid == null || mid.trim().length() == 0)
             return null;

         String contextRoot = "/" + module;
         String oName = webModuleMBeanName + contextRoot;
         ObjectName webModuleMBean = new ObjectName(oName);

         try{
            return getModuleDirectory(webModuleMBean);
         }catch (Throwable t) {
            t.printStackTrace();
            return null;
         }

         //ObjectName aaaa = new ObjectName(applicationMBeanName + ",name="+mid);

//         try{
//             return getModuleDirectory(webModuleMBean);
//         } catch(Throwable t) {
//             t.printStackTrace();
//             return null;
//         }
     }


//     public File getModuleDirectory(String module) throws Throwable{
//
//         if(module == null || module.trim().length() == 0)
//             return null;
//         ObjectName aaaa = new ObjectName(applicationMBeanName + ",name="+module);
//         try{
//             return getModuleDirectory(aaaa);
//         } catch(Throwable t) {
//             t.printStackTrace();
//             return null;
//         }
//     }

     public File getModuleDirectory(String module) throws Throwable{

         if(module == null || module.trim().length() == 0)
             return null;
         String contextRoot = null;

         if(!module.startsWith("/"))
            contextRoot = "/" + module;
         else
            contextRoot = module;

         String oName = webModuleMBeanName + contextRoot;
         ObjectName webModuleMBean = new ObjectName(oName);

         try{
             return getModuleDirectory(webModuleMBean);
         } catch(Throwable t) {
             t.printStackTrace();
             return null;
         }
     }


    private void reloadThroughFile(File moduleDir) throws Throwable {

        File reloadFile = new File(moduleDir, ".reload");
        if(reloadFile.exists()) {
            reloadFile.setLastModified(System.currentTimeMillis());
        } else {
            if(moduleDir.exists())
                reloadFile.createNewFile();
        }
    }

    private File getModuleDirectory(ObjectName appMBean) throws Throwable {
       //String path = "" + getMBeanServerConnection().getAttribute(appMBean,"Location");
       String path = "" + getMBeanServerConnection().getAttribute(appMBean,"docBase");

       //for error in GF V3 which returns location as file:/User/... on MacOS
       if(path.startsWith("file:/") && !path.startsWith("file://")) {

           path = path.replaceFirst("file:", "");
       }

       File dirLocation =new java.io.File(path);
       return dirLocation;
    }

}
