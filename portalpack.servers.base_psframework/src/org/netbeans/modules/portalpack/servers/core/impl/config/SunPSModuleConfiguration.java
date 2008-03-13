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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.portalpack.servers.core.impl.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.portalpack.servers.core.PSModuleConfiguration;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;

/**
 *
 * @author Satyaranjan
 */
public class SunPSModuleConfiguration extends PSModuleConfiguration{

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    public SunPSModuleConfiguration(J2eeModule j2eeModule) {
        super(j2eeModule);
    }

    @Override
    public void createConfiguration() {
        File file = getJ2eeModule().getDeploymentConfigurationFile("WEB-INF/sun-portlet.xml");
        getSunPortletXml(file);
    }
    
    private void getSunPortletXml(File file)  {
        //super.initConfiguration(config, files, resourceDir, keepUpdated);
      
            if(file.getName().equals("sun-portlet.xml")) {
                FileWriter writer = null;
                
                    if (file.exists())
                        return;
                    String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                +"<portlet-app-extension xmlns=\"http://www.sun.com/software/xml/ns/portal_server\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:sunportal=\"http://www.sun.com/software/xml/ns/portal_server\" xsi:noNamespaceSchemaLocation=\"http://www.sun.com/software/xml/ns/portal_server\" version=\"1.0\">\n"
                                +"</portlet-app-extension>";
                    try{
                        writer = new java.io.FileWriter(file);
                        writer.write(text);
                        writer.flush();
                        return;
                    } catch (IOException ex) {
                        //do nothing...
                        logger.log(Level.WARNING,"Error",ex);
                    } finally {
                        try {
                            if(writer != null)
                               writer.close();
                        } catch (IOException ex) {
                             logger.log(Level.WARNING,"Error",ex);
                        }
                    }
                
            } 
          
    }

}
