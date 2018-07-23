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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ConfigConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ResultContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.exceptions.PortletCreateException;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.filetype.jsr168.impl.NewJSR168CreatePortletComponent;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class PortletFrameworkUtil {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);

    /** Creates a new instance of PortletFrameworkUtil */
    public PortletFrameworkUtil() {
    }

    public static void createPkgAndClass(FileObject subfolder, Project prj, WebModule wm, String orgPkg, PortletContext context) {

        String pkg = orgPkg.replace(".", File.separator);
        File srcFolder = FileUtil.toFile(subfolder);
        File newPkgDir = new File(srcFolder, File.separator + pkg);

        boolean created = newPkgDir.mkdirs();

        String javaFile = new File(newPkgDir, context.getPortletClass() + ".java").getAbsolutePath();

        NewJSR168CreatePortletComponent component = new NewJSR168CreatePortletComponent(prj, wm, ConfigConstants.JSR168_TEMPLATE_NAME);
        ResultContext retVal = new ResultContext();
        try {
            component.doCreateClass(FileUtil.toFile(prj.getProjectDirectory()).getAbsolutePath(), prj.getProjectDirectory().getName(), newPkgDir.getAbsolutePath(), context.getPortletClass(), context, new AppContext(), retVal);
            if (context.getHasJsps()) {
                createJSPs(wm.getWebInf(), context);
            }
        } catch (PortletCreateException ex) {
            ex.printStackTrace();
        }
    }

    public void createEmptyPortlet10XML(FileObject wm) {
    }

    /**
     *
     * @param webInf
     * @param context
     */
    public static void createJSPs(FileObject webInf, PortletContext context) {

        FileObject templateFileObj = TemplateHelper.getTemplateFile("jsptemplate.jsp");

        FileObject jspFileObj = null;
        if (!new File(FileUtil.toFile(webInf), "jsp").exists()) {
            try {
                jspFileObj = webInf.createFolder("jsp");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            jspFileObj = FileUtil.toFileObject(new File(FileUtil.toFile(webInf), "jsp"));
        }
        String[] modes = context.getModes();
        for (int i = 0; i < modes.length; i++) {
            String mode = modes[i];
            if (modes[i].equals("VIEW")) {
                try {
                  
                    if (createJsp(jspFileObj, context.getViewJsp())) {
                        mergeJSPTemplate(templateFileObj, jspFileObj, context.getPortletName() + " - VIEW MODE",getJspName(context.getViewJsp()),context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            } else if (modes[i].equals("EDIT")) {
                try {
                    if (createJsp(jspFileObj, context.getEditJsp())) {
                        mergeJSPTemplate(templateFileObj, jspFileObj, context.getPortletName() + " - EDIT MODE", getJspName(context.getEditJsp()),context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            }
            if (modes[i].equals("HELP")) {
                try {
                    if (createJsp(jspFileObj, context.getHelpJsp())) {
                        mergeJSPTemplate(templateFileObj, jspFileObj, context.getPortletName() + " - Help MODE", getJspName(context.getHelpJsp()),context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }
    
     public static void createJSPs(FileObject webInf, String folderName, PortletContext context) {

        FileObject templateFileObj = TemplateHelper.getTemplateFile("jsptemplate.jsp");

        FileObject jspFileObj = null;
        if (!new File(FileUtil.toFile(webInf), folderName).exists()) {
            try {
                jspFileObj = webInf.createFolder(folderName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            jspFileObj = FileUtil.toFileObject(new File(FileUtil.toFile(webInf), folderName));
        }
        String[] modes = context.getModes();
        for (int i = 0; i < modes.length; i++) {
            String mode = modes[i];
            if (modes[i].equals("VIEW")) {
                try {
                  
                    if (createJsp(jspFileObj, context.getViewJsp())) {
                        mergeJSPTemplate(templateFileObj, jspFileObj, context.getPortletName() + " - VIEW MODE",getJspName(context.getViewJsp()),context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            } else if (modes[i].equals("EDIT")) {
                try {
                    if (createJsp(jspFileObj, context.getEditJsp())) {
                        mergeJSPTemplate(templateFileObj, jspFileObj, context.getPortletName() + " - EDIT MODE", getJspName(context.getEditJsp()),context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            }
            if (modes[i].equals("HELP")) {
                try {
                    if (createJsp(jspFileObj, context.getHelpJsp())) {
                        mergeJSPTemplate(templateFileObj, jspFileObj, context.getPortletName() + " - Help MODE", getJspName(context.getHelpJsp()),context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    private static String getJspName(String jspWithExt) {
        String jspName = null;
        int index = jspWithExt.lastIndexOf(".");
        if (index == -1) {
            jspName = jspWithExt;
        } else {
            jspName = jspWithExt.substring(0, index);
        }
        return jspName;
    }

    private static boolean createJsp(FileObject folder, String jspFileNameWithExt) throws IOException {
        //   FileObject jspTemplate = ClassPath.findResource( "templates/jsptemplate.jsp" ); // NOI18N
        //   if (jspTemplate == null)
        //       return; // Don't know the template
        //  DataObject mt = DataObject.find(jspTemplate);
        String jspName = getJspName(jspFileNameWithExt);
        if (new File(FileUtil.toFile(folder), jspFileNameWithExt).exists()) {
            if (!CoreUtil.checkIfFileNeedsTobeOverwritten(jspFileNameWithExt)) {
                return false;
            } else {

                FileObject jspObj = folder.getFileObject(jspName, "jsp");
                if (jspObj.existsExt("jsp")) {
                    try{
                        jspObj.delete();
                    }catch(IOException e){
                        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getBundle(PortletFrameworkUtil.class).getString("FILE_COULD_NOT_BE_OVERWRITTEN"),NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                    }
                }
                return true;
            }
        }

        /*    DataFolder jspDf = DataFolder.findFolder(folder);
        folder.createData(jspName);*/
        return true;
        // mt.createFromTemplate(webDf, jspName);
    }

    private static void mergeJSPTemplate(FileObject template, FileObject folder, String desc, String jspName,PortletContext context) {
        try {
            String version = context.getPortletVersion();
            java.util.Map values = new java.util.HashMap();
            values.put("DESC", desc);
            values.put("VERSION",version);

            org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateHelper.mergeTemplateToFile(template, folder, jspName, values);
      
        } catch (DataObjectNotFoundException ex) {
            logger.log(Level.SEVERE, "Error in merging JSP Template", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error in merging JSP Template", ex);
        }

    }
}
