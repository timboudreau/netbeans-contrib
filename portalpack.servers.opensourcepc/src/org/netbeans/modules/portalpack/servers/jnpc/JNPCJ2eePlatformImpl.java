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

package org.netbeans.modules.portalpack.servers.jnpc;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.Util;

/**
 *
 * @author satya
 */
public class JNPCJ2eePlatformImpl extends PSJ2eePlatformImpl{
    /** Creates a new instance of JNPCJ2eePlatformImpl */
    public JNPCJ2eePlatformImpl(PSConfigObject psconfig) {
        super(psconfig);
    }
    
    public Set getSupportedSpecVersions() {
        Set result = new HashSet();
        result.add(J2eeModule.J2EE_13);
        result.add(J2eeModule.J2EE_14);
        result.add(J2eeModule.JAVA_EE_5);
        return result;
    }
    protected List getCustomLibraries() {
         List classPath = new ArrayList();
         
         String[] libFiles = {"portlet-api-1.0.jar","portlettaglib-1.0.jar","portletappengine-1.0.jar"};
         //PSConfigObject psconfig = psconfig.getPSConfig();
         
         for(int i=0;i<libFiles.length;i++)
         {
            String portletJarUri = psconfig.getPSHome() + File.separator + "lib" + File.separator + libFiles[i];
            File portletJar = new File(portletJarUri);
            if(portletJar.exists())
            {
                try {
                    classPath.add(fileToUrl(portletJar));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
         }
         
         //If glassfish then add javaee.jar
         if(psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9))
         {
             String[] libs = {"javaee.jar"};
             for(int k=0;k<libs.length;k++)
             {
                 File libJar = new File(psconfig.getServerHome() + File.separator + "lib" + File.separator + libs[k]);
                 if(libJar.exists())
                 {
                    try {
                        classPath.add(fileToUrl(libJar));
                    } catch (MalformedURLException ex) {
                      ex.printStackTrace();
                    }
                 }
             } 
         }
         

         String[] encClassPaths = Util.decodeClassPath(psconfig.getClassPath());
         for(int i=0;i<encClassPaths.length;i++)
         {
             File classpathJar = new File(encClassPaths[i]);
             if(classpathJar.exists())
             {
                try {
                    classPath.add(fileToUrl(classpathJar));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
             }
         }
         return classPath;     
    }
    
    
    
}
