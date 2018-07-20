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

package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.jsr168.impl;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.BaseCodeGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.CodeGenConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.component.NewPortletCreateComponent;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ConfigConstants;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Satya
 */
public class NewJSR168CreatePortletComponent extends NewPortletCreateComponent {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private Project project;
    private String templateName;
    private WebModule wm;
    
    /** Creates a new instance of NetbeansCreatePortletComponent */
    public NewJSR168CreatePortletComponent(Project prj) {
         
         this(prj,ConfigConstants.JSR168_TEMPLATE_NAME);
    }
    
    public NewJSR168CreatePortletComponent(Project prj, String template)
    {
        this.project = prj;
        this.templateName = template;
    }
    
    public NewJSR168CreatePortletComponent(Project prj, WebModule wm, String template)
    {
        this.project = prj;
        this.templateName = template;
        this.wm = wm;
    }
    
    public String getWebInfDir()
    {
        if(wm != null)
        {
           FileObject webInf = wm.getWebInf();
           String webInfDir = NetbeansUtil.getAbsolutePath(webInf);
           logger.log(Level.FINEST,"WEB-INF dir is : "+webInfDir);
           return webInfDir;
        }
        if(getModuleType().equals(CodeGenConstants.WEB_MODULE_TYPE))
        {
            FileObject object = project.getProjectDirectory();
            WebModule module = WebModule.getWebModule(object);
            FileObject webInf = module.getWebInf();
            String webInfDir = NetbeansUtil.getAbsolutePath(webInf);
            logger.log(Level.FINEST,"WEB-INF dir is : "+webInfDir);
            return webInfDir;
        }
        return null;
       // return NetbeansCreatePortletComponent.getWebInfDirForProject(project);     
    }

        
    protected String getModuleType() {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if(wm != null)
            return CodeGenConstants.WEB_MODULE_TYPE;
        else
            return "UNKNOWN";
       /* String className = project.getClass().getName();
        if(className.indexOf("org.netbeans.modules.java.j2seproject") != -1)
            return CodeGenConstants.JAVA_MODULE_TYPE;
        else if(className.indexOf("org.netbeans.modules.web.project") != -1)
            return CodeGenConstants.WEB_MODULE_TYPE;
        else
            return "UNKNOWN";*/
    }   



    protected String getPackage(File dir) {
        
        File file = FileUtil.normalizeFile(dir);
        logger.log(Level.FINEST,"File is ...."+file+"              "+dir);
        FileObject fileObj = FileUtil.toFileObject(file);
        if(fileObj == null)
        {
            logger.log(Level.FINEST,"File Object is null.");
            return null;
        }
        
        ClassPath classPath = ClassPath.getClassPath(fileObj,ClassPath.SOURCE);
        String pkg = classPath.getResourceName(fileObj,'.',false);
        logger.log(Level.FINEST,"ClassPath:::::::::::: "+ pkg);
        
        if(pkg == null) return "";
        else
            return pkg;
    }

    protected BaseCodeGenerator getCodeGenerator() {
        BaseCodeGenerator bcode = new BaseCodeGenerator();
        bcode.setTemplateFileName(templateName);
        return bcode;
    }

    protected void refreshPath(String modulePath) {
    }

   
}
