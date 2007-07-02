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

package org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.util;

import org.netbeans.modules.portalpack.portlets.genericportlets.apptype.jsr168.NewJSR168CreatePortletComponent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VTResourceLoader;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.apptype.jsr168.NewJSR168CreatePortletComponent;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ConfigConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ResultContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.exceptions.PortletCreateException;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;

/**
 *
 * @author Satyaranjan
 */
public class PortletProjectUtil {
     private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    /** Creates a new instance of PortletProjectUtil */
    public PortletProjectUtil() {
    }
    
     public static void createPkgAndClass(FileObject subfolder,Project prj,WebModule wm,String orgPkg,PortletContext context) {
        
        String pkg = orgPkg.replace(".",File.separator);
        File srcFolder = FileUtil.toFile(subfolder);
        File newPkgDir = new File(srcFolder,File.separator + pkg);
        
        boolean created = newPkgDir.mkdirs();
        
        String javaFile = new File(newPkgDir,context.getPortletClass()+".java").getAbsolutePath();
        
        NewJSR168CreatePortletComponent component = new NewJSR168CreatePortletComponent(prj,wm,ConfigConstants.JSR168_TEMPLATE_NAME);
        ResultContext retVal = new ResultContext();
        try {
            component.doCreateClass(FileUtil.toFile(prj.getProjectDirectory()).getAbsolutePath(),prj.getProjectDirectory().getName(),newPkgDir.getAbsolutePath(),context.getPortletClass(),context,new AppContext(),retVal);
            if(context.getHasJsps())
            {
                createJSPs(wm.getWebInf(),context);
            }
        } catch (PortletCreateException ex) {
            ex.printStackTrace();
        }     
      
    }
     
    public void createEmptyPortlet10XML(FileObject wm)
    {
        
    }
     
   /**
    * 
    * @param webInf 
    * @param context 
    */
   public static void createJSPs(FileObject webInf,PortletContext context) {
        Template template = null;
        try {
            template = Velocity.getTemplate("jsptemplate.jsp","UTF-8");
        } catch (ParseErrorException ex) {
            ex.printStackTrace();
        } catch (ResourceNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        FileObject jspFileObj = null;
        if(!new File(FileUtil.toFile(webInf),"jsp").exists()) {
            try{
                jspFileObj = webInf.createFolder("jsp");
            }catch(IOException ex){
                ex.printStackTrace();
            }
        } else
            jspFileObj = FileUtil.toFileObject(new File(FileUtil.toFile(webInf),"jsp"));
        
        String[] modes = context.getModes();
        for(int i=0; i<modes.length; i++) {
            String mode = modes[i];
            if(modes[i].equals("VIEW")) {
                try{
                    if(createJsp(jspFileObj, context.getViewJsp()))
                        mergeJSPTemplate(template,jspFileObj,context.getPortletName() + " - VIEW MODE", context.getViewJsp());
                }catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE,ex.getMessage(),ex);
                }
                
            } else if(modes[i].equals("EDIT")) {
                try{
                    if(createJsp(jspFileObj, context.getEditJsp()))
                        mergeJSPTemplate(template,jspFileObj,context.getPortletName() + " - EDIT MODE", context.getEditJsp());
                }catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE,ex.getMessage(),ex);
                }
                
            }
            if(modes[i].equals("HELP")) {
                try{
                    if(createJsp(jspFileObj, context.getHelpJsp()))
                        mergeJSPTemplate(template,jspFileObj,context.getPortletName() + " - Help MODE", context.getHelpJsp());
                }catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE,ex.getMessage(),ex);
                }
                
            }
        }
        
    }
    
    private static boolean createJsp(FileObject folder,String jspName) throws IOException {
        //   FileObject jspTemplate = ClassPath.findResource( "templates/jsptemplate.jsp" ); // NOI18N
        
        //    if (jspTemplate == null)
        //       return; // Don't know the template
        
        //  DataObject mt = DataObject.find(jspTemplate);
        if(new File(FileUtil.toFile(folder),jspName).exists()) {
            if(!CoreUtil.checkIfFileNeedsTobeOverwritten(jspName))
                return false;
        }
        
        DataFolder jspDf = DataFolder.findFolder(folder);
        folder.createData(jspName);
        return true;
        // mt.createFromTemplate(webDf, jspName);
    }
    
    private static void mergeJSPTemplate(Template template,FileObject folder, String desc,String jspName) {
        FileObject jspFileObj =folder.getFileObject(jspName);
        FileLock lock = null;
        try {
            lock = jspFileObj.lock();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        VelocityContext context = VTResourceLoader.getContext(new HashMap());
        context.put("DESC",desc);
        OutputStream output = null;
        try {
            output = jspFileObj.getOutputStream(lock);
            OutputStreamWriter writer = new OutputStreamWriter(output,"UTF-8");
            template.merge(context,writer);
            writer.flush();
        } catch (MethodInvocationException ex) {
            ex.printStackTrace();
        } catch (ParseErrorException ex) {
            ex.printStackTrace();
        } catch (ResourceNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{
            try{
                output.flush();
                output.close();
                lock.releaseLock();
            }catch(Exception e){e.printStackTrace();}
        }
    }
    
}
