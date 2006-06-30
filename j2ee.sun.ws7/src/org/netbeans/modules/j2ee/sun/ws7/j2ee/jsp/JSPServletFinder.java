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

package org.netbeans.modules.j2ee.sun.ws7.j2ee.jsp;

import java.io.File;
import java.lang.reflect.Method;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import org.netbeans.modules.j2ee.deployment.plugins.api.FindJSPServlet;

import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentFactory;

/**
 *
 * @author Petr Jiricka, adapted by Mukesh Garg for WS70
 */
public class JSPServletFinder implements FindJSPServlet {

    private WS70SunDeploymentManager manager;
    
    /**
     * Creates a new instance of JSPServletFinder
     */
    public JSPServletFinder(DeploymentManager dm) {
        
        String uri = ((WS70SunDeploymentManager)dm).getUri();
        manager = WS70SunDeploymentFactory.getConnectedCachedDeploymentManager(uri);
    }
    
    public File getServletTempDirectory(String moduleContextPath) {
        Target defaultTarget = manager.getDefaultTarget();
        
        if(defaultTarget == null){
            // could not find target information
            return null;
        }
        String configName = null;
        String vsName = null;
        try{
            Method getConfigName = defaultTarget.getClass().getDeclaredMethod("getConfigName", new Class[]{});
            configName = (String)getConfigName.invoke(defaultTarget, new Object[]{});
            Method getVSName = defaultTarget.getClass().getDeclaredMethod("getVSName", new Class[]{});
            vsName = (String)getVSName.invoke(defaultTarget, new Object[]{});            
        }catch(Exception e){
            // could not find location 
            return null;
        }
                   
        String location = manager.getServerLocation()+File.separator+"https-"+configName+
                           File.separator+"ClassCache"+File.separator+vsName+File.separator;
        File workDir = new File(location, getContextRootString(moduleContextPath));
        return workDir;
    }
    
    private String getContextRootString(String moduleContextPath) {
        String contextRootPath = moduleContextPath;
        if (contextRootPath.startsWith("/")) {
            contextRootPath = contextRootPath.substring(1);
        }
        return contextRootPath;

    }
    
    public String getServletResourcePath(String moduleContextPath, String jspResourcePath) {        
        String path= getServletPackageName(jspResourcePath).replace('.', '/') + '/' +
        getServletClassName(jspResourcePath) + ".java";
        return path;
    }
    public String getServletEncoding(String moduleContextPath, String jspResourcePath) {
        return "UTF8"; // NOI18N
    }
    // copied from org.apache.jasper.JspCompilationContext
    private String getServletPackageName(String jspUri) {
        String dPackageName = getDerivedPackageName(jspUri);
        if (dPackageName.length() == 0) {
            return JspNameUtil.JSP_PACKAGE_NAME;
        }
        return JspNameUtil.JSP_PACKAGE_NAME + '.' + getDerivedPackageName(jspUri);
    }
    
    // copied from org.apache.jasper.JspCompilationContext
    private String getDerivedPackageName(String jspUri) {
        int iSep = jspUri.lastIndexOf('/');
        return (iSep > 0) ? JspNameUtil.makeJavaPackage(jspUri.substring(0,iSep)) : "";
    }
    
    // copied from org.apache.jasper.JspCompilationContext
    private String getServletClassName(String jspUri) {
        int iSep = jspUri.lastIndexOf('/') + 1;
        return JspNameUtil.makeJavaIdentifier(jspUri.substring(iSep));
    }
}
