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
package org.netbeans.modules.portalpack.servers.websynergy;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatConstant;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.Util;

/**
 *
 * @author satya
 */
public class LiferayJ2eePlatformImpl extends PSJ2eePlatformImpl {

    /** Creates a new instance of LifeRayJ2eePlatformImpl */
    public LiferayJ2eePlatformImpl(PSConfigObject psconfig) {
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

        String[] libFiles = {"portal-service.jar", "portal-kernel.jar"};
        //PSConfigObject psconfig = psconfig.getPSConfig();

        //If glassfish then add javaee.jar
        if (psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)) {
            String portalLibDir = psconfig.getDomainDir() + File.separator + "lib";
            for (int i = 0; i < libFiles.length; i++) {
                String portletJarUri = portalLibDir + File.separator + libFiles[i];
                File portletJar = new File(portletJarUri);
                if (portletJar.exists()) {
                    try {
                        classPath.add(fileToUrl(portletJar));
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
            String[] libs = {"javaee.jar"};
            for (int k = 0; k < libs.length; k++) {
                File libJar = new File(psconfig.getServerHome() + File.separator + "lib" + File.separator + libs[k]);
                if (libJar.exists()) {
                    try {
                        classPath.add(fileToUrl(libJar));
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        if(psconfig.getServerType().equals(ServerConstants.TOMCAT_5_X)) {
            
            String portalLibDir = psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator +
                                        "common" + File.separator + "lib" + File.separator + "ext";
            
            for (int i = 0; i < libFiles.length; i++) {
                String portalJarUri = portalLibDir + File.separator + libFiles[i];
                File portletJar = new File(portalJarUri);
                if (portletJar.exists()) {
                    try {
                        classPath.add(fileToUrl(portletJar));
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
            String[] libs = {"servlet-api.jar","jsp-api.jar"};
            String serverLibDir = psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator +
                                        "common" + File.separator + "lib";
            for (int k = 0; k < libs.length; k++) {
                File libJar = new File(serverLibDir + File.separator + libs[k]);
                if (libJar.exists()) {
                    try {
                        classPath.add(fileToUrl(libJar));
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }


        String[] encClassPaths = Util.decodeClassPath(psconfig.getClassPath());
        for (int i = 0; i < encClassPaths.length; i++) {
            File classpathJar = new File(encClassPaths[i]);
            if (classpathJar.exists()) {
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
