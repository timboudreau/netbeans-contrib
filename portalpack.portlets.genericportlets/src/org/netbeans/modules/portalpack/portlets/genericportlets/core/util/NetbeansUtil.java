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
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletXMLFactory;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.project.api.WebPropertyEvaluator;
import org.netbeans.modules.web.project.api.WebProjectLibrariesModifier;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

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
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if(wm != null)
            return CodeGenConstants.WEB_MODULE_TYPE;
        else
            return "UNKNOWN";
        /*
        String className = project.getClass().getName();
        if(className.indexOf("org.netbeans.modules.java.j2seproject") != -1)
            return CodeGenConstants.JAVA_MODULE_TYPE;
        else if(className.indexOf("org.netbeans.modules.web.project") != -1)
            return CodeGenConstants.WEB_MODULE_TYPE;
        else
            return "UNKNOWN";*/
    }   
    
    public static String getWebInfDir(Project prj)
    {  
        if(getModuleType(prj).equals(CodeGenConstants.WEB_MODULE_TYPE))
        {
            FileObject object = prj.getProjectDirectory();
            WebModule module = WebModule.getWebModule(object);
            FileObject webInf = module.getWebInf();
            String webInfDir = NetbeansUtil.getAbsolutePath(webInf);
            logger.log(Level.FINEST,"WEB-INF dir is : "+webInfDir);
            return webInfDir;
        }
        return null;
       // return NetbeansCreatePortletComponent.getWebInfDirForProject(project);     
    }
     
    /**
     * Add an array of library references to a web project, qualified by the type parameter.
     * @param project Project to which the library is to be added
     * @param libraries Library objects from the LibraryManager registry
     * @param type Determines whether the library is to be added to the
     *        design-time classpath (ClassPath.COMPILE) or deployed with the application (ClassPath.EXECUTE)
     * @return Returns true if the library reference was successfully added
     * @throws an IOException if there was a problem adding the reference
     */
    public static boolean addLibraryReferences(Project project, Library[] libraries, String type) throws IOException {
        WebProjectLibrariesModifier wplm = (WebProjectLibrariesModifier) project.getLookup().lookup(WebProjectLibrariesModifier.class);
        if (wplm == null) {
            // Something is wrong, shouldn't be here.
            return false;
        }

        if (ClassPath.COMPILE.equals(type)) {
            return wplm.addCompileLibraries(libraries);
        } else if (ClassPath.EXECUTE.equals(type)) {
            return wplm.addPackageLibraries(libraries, "WEB-INF/lib"); // NOI18N
        }

        return false;
    }

    /**
     * Get the readonly access to web project properties through PropertyEvaluator,
     * @param project Project to which the properties is to be searched
     * @return Returns EditableProperties if the properties was successfully found
     */
    public static EditableProperties getWebProperties(Project project) {
        WebPropertyEvaluator wpe = (WebPropertyEvaluator) project.getLookup().lookup(WebPropertyEvaluator.class);
        if (wpe == null) {
            // Something is wrong, shouldn't be here.
            return null;
        }

        PropertyEvaluator pe = wpe.evaluator();
        return new EditableProperties(pe.getProperties());
    }
    
    public static void saveBean(BaseBean bean, FileObject fileObject)
    {
        if(fileObject == null || bean == null) return;
        
        try {
            FileLock lock = fileObject.lock();
            OutputStream out = fileObject.getOutputStream(lock);
            bean.write(out);
            try{
                 out.flush();
                 out.close();
            }catch(Exception e){
                logger.log(Level.SEVERE,"Error flushing output stream during save of portletXml",e);
            }
            
            lock.releaseLock();
            
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"Error saving portlet xml file",ex);
        }
    }
    
    public static void saveBean(BaseBean bean, File file)
    {
        if(file == null)
            logger.log(Level.SEVERE,"Cann't save to Null File ");
        
        FileObject fobj = FileUtil.toFileObject(file);
        try{
             if(fobj == null)
                fobj = FileUtil.createData(file);
        }catch(Exception e){
            logger.severe(e.getMessage());
        }
        
        if(fobj != null)
            saveBean(bean, fobj);
        else
            logger.log(Level.SEVERE,"FileObject is null : "+file);
    }
    
     public static PortletApp getPortletApp(File portletXml) {
        try {
            if (!portletXml.exists()) {
                return null;
            }
            PortletApp portletApp = PortletXMLFactory.createGraph(portletXml);
            if (portletApp == null) {
                return null;
            }
            return portletApp;

        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean savePortletXML(PortletApp portletApp, File portletXML) {
        try {
            FileObject fileObject = FileUtil.toFileObject(portletXML);
            FileLock lock = fileObject.lock();
            OutputStream out = fileObject.getOutputStream(lock);

            portletApp.write(out);
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
            }

            lock.releaseLock();
            return true;
        } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(NetbeansUtil.class, "TXT_CantUpdatePortletXML")));
            return false;
        }
   }
}

