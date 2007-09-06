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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.util.JspNameUtil;


public class SunAppFindJSPServletImpl implements FindJSPServlet {
    
    private PSDeploymentManager tm;
    
    /** Creates a new instance of FindJSPServletImpl */
    public SunAppFindJSPServletImpl(PSDeploymentManager dm) {
        tm =dm;
    }
    
    public File getServletTempDirectory(String moduleContextPath) {

        //System.out.println(moduleContextPath);
        moduleContextPath = getContextRootString(moduleContextPath);
        //modName may be null, but this does not impact to following logic: in this case, the file will not exist as well.
         File workDir = new File(tm.getPSConfig().getDomainDir()+"/generated/jsp/j2ee-modules/".replace('/',File.separatorChar) +moduleContextPath);// NOI18N
       // File workDir = new File(tm.getPSConfig().getDomainDir()+File.separator +"generated"+File.separator + "jsp" + File.separator + "j2ee-modules" + File.separator +moduleContextPath);// NOI18N
      /*  if (!workDir.exists()){ //check for ear file gen area:
         workDir = new File(domainDir, "/"+domain+"/generated/jsp/j2ee-apps/" +modName);// NOI18N
            
        }*/
        //System.out.println("returning servlet root " + workDir.getAbsolutePath());
        return workDir;
    }
        
    private String getContextRootString(String moduleContextPath) {
        String contextRootPath = moduleContextPath;
        if (contextRootPath.startsWith("/")) {// NOI18N
            contextRootPath = contextRootPath.substring(1);
        }
            return contextRootPath;
    }
    
    public String getServletResourcePath(String moduleContextPath, String jspResourcePath) {
        //String path = module.getWebURL();
        String s= getServletPackageName(jspResourcePath).replace('.', '/') + '/' +
            getServletClassName(jspResourcePath) + ".java";// NOI18N
    //    System.out.println("in jsp  "+s);
        return s;
        //int lastDot = jspResourcePath.lastIndexOf('.');
        //return jspResourcePath.substring(0, lastDot) + "$jsp.java"; // NOI18N
    }

    // copied from org.apache.jasper.JspCompilationContext
    public String getServletPackageName(String jspUri) {
        String dPackageName = getDerivedPackageName(jspUri);
        if (dPackageName.length() == 0) {
            return JspNameUtil.JSP_PACKAGE_NAME;
        }
        return JspNameUtil.JSP_PACKAGE_NAME + '.' + getDerivedPackageName(jspUri);
    }
    
    // copied from org.apache.jasper.JspCompilationContext
    private String getDerivedPackageName(String jspUri) {
        int iSep = jspUri.lastIndexOf('/');
        return (iSep > 0) ? JspNameUtil.makeJavaPackage(jspUri.substring(0,iSep)) : "";// NOI18N
    }
    
    // copied from org.apache.jasper.JspCompilationContext
    public String getServletClassName(String jspUri) {
        int iSep = jspUri.lastIndexOf('/') + 1;
        return JspNameUtil.makeJavaIdentifier(jspUri.substring(iSep));
    }
    
    public String getServletEncoding(String moduleContextPath, String jspResourcePath) {
        return "UTF8"; // NOI18N
    }
    
    public void setDeploymentManager(PSDeploymentManager manager) {
        tm = manager;
    }
 
}


