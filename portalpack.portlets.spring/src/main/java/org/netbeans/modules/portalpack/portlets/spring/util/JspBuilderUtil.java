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
package org.netbeans.modules.portalpack.portlets.spring.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateHelper;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class JspBuilderUtil {

    private static Logger logger = Logger.getLogger(SpringPortletConstants.LOGGER);

    /** Creates a new instance of JspBuilderUtil */
    public JspBuilderUtil() {
    }

    /**
     *
     * @param webInf
     * @param context
     */
    public static void createJSPs(String templateName, FileObject webInf, PortletContext context, Map values) {

        TemplateUtil templateUtil = new TemplateUtil(SpringPortletConstants.TEMPLATE_FOLDER);

        FileObject templateFileObj = null;

        FileObject jspFileObj = null;
        try {
            templateFileObj = templateUtil.getTemplateFile(templateName);
        } catch (TemplateNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

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
                        values.put("MODE", "VIEW");
                        mergeJSPTemplate(templateFileObj, jspFileObj, values, getJspName(context.getViewJsp()), context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            } else if (modes[i].equals("EDIT")) {
                try {
                    if (createJsp(jspFileObj, context.getEditJsp())) {
                        values.put("MODE", "EDIT");
                        mergeJSPTemplate(templateFileObj, jspFileObj, values, getJspName(context.getEditJsp()), context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            }
            if (modes[i].equals("HELP")) {
                try {
                    if (createJsp(jspFileObj, context.getHelpJsp())) {
                        values.put("MODE", "HELP");
                        mergeJSPTemplate(templateFileObj, jspFileObj, values, getJspName(context.getHelpJsp()), context);
                    }
                } catch (IOException ex) {
                    logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    public static String getJspName(String jspWithExt) {
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
                    try {
                        jspObj.delete();
                    } catch (IOException e) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getBundle(JspBuilderUtil.class).getString("FILE_COULD_NOT_BE_OVERWRITTEN"), NotifyDescriptor.WARNING_MESSAGE);
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

    private static void mergeJSPTemplate(FileObject template, FileObject folder, Map values, String jspName, PortletContext context) {
        try {

            org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateHelper.mergeTemplateToFile(template, folder, jspName, values);

        } catch (DataObjectNotFoundException ex) {
            logger.log(Level.SEVERE, "Error in merging JSP Template", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error in merging JSP Template", ex);
        }

    }

    public static void createJspFromTemplate(String templateName, FileObject webInf, String newJsp, Map values) {

        TemplateUtil templateUtil = new TemplateUtil(SpringPortletConstants.TEMPLATE_FOLDER);

        FileObject templateFileObj = null;

        FileObject jspFileObj = null;
        try {
            templateFileObj = templateUtil.getTemplateFile(templateName);
        } catch (TemplateNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        if (!new File(FileUtil.toFile(webInf), "jsp").exists()) {
            try {
                jspFileObj = webInf.createFolder("jsp");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            jspFileObj = FileUtil.toFileObject(new File(FileUtil.toFile(webInf), "jsp"));
        }


        try {

            if (createJsp(jspFileObj, newJsp)) {
                mergeJSPTemplate(templateFileObj, jspFileObj, values, getJspName(newJsp), null);
            }
        } catch (IOException ex) {
            logger.log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
