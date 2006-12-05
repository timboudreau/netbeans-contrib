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

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.CodeGenConstants;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Satya
 */
public class NetbeansUtil {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    /** Creates a new instance of NetbeansUtil */
    public NetbeansUtil() {
    }
    
    public static Project getProject(FileObject fileObject)
    {
        ProjectManager manager = ProjectManager.getDefault();
        FileObject projFileObject = getProjectFileObject(fileObject,manager);
        if(projFileObject == null)
             return null;
        
        try {
            return manager.findProject(projFileObject);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE,"error",ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"error",ex);
        }
        return null;
        
    }
    
    private static FileObject getProjectFileObject(FileObject object,ProjectManager manager)
    {
        if(!object.isFolder())
        {
            return getProjectFileObject(object.getParent(),manager);
        }else if(manager.isProject(object))
             return object;
        else
        {
            FileObject pObj = object.getParent();
            if(pObj != null)
                return getProjectFileObject(pObj,manager);
            else
                return null;
        }
     }
    
    public static void showErrorMsg(String msg)
    {
        JOptionPane.showMessageDialog(null,msg);
    }
    
    public static String getProjectAbsolutePath(Project prj)
    {
        FileObject projFileObject = prj.getProjectDirectory();
        File prjSysFile = FileUtil.toFile(projFileObject);
        String projPath = prjSysFile.getAbsolutePath();
        return projPath;
    }
    
    public static String getAbsolutePath(FileObject obj)
    {
        File sysFile = FileUtil.toFile(obj);
        String filePath = sysFile.getAbsolutePath();
        return filePath;
        
    }
    
    public static String getProjectName(Project prj)
    {
       ProjectInformation prjInfo = ProjectUtils.getInformation(prj);
       
       logger.log(Level.FINE,prjInfo+ " ***");
       String projName = prjInfo.getName();
       return projName;
    }
    
   
    public static String getDocumentBase(Project project)
    {
        
        if(getModuleType(project).equals(CodeGenConstants.WEB_MODULE_TYPE))
        {
            FileObject object = project.getProjectDirectory();
            WebModule module = WebModule.getWebModule(object);
            FileObject docBase = module.getDocumentBase();
            String docBaseDir = NetbeansUtil.getAbsolutePath(docBase);
            logger.log(Level.FINE,"DocBase dir is :::::::::::::::::::::::::: "+docBaseDir);
            return docBaseDir;
        }
        return null;
   
    }
    
    public static String getModuleType(Project project) {
        String className = project.getClass().getName();
        if(className.indexOf("org.netbeans.modules.java.j2seproject") != -1)
            return CodeGenConstants.JAVA_MODULE_TYPE;
        else if(className.indexOf("org.netbeans.modules.web.project") != -1)
            return CodeGenConstants.WEB_MODULE_TYPE;
        else
            return "UNKNOWN";
    }   
     
}

