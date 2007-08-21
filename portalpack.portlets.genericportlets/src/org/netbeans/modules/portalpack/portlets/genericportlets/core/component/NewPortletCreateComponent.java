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

package org.netbeans.modules.portalpack.portlets.genericportlets.core.component;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.BaseCodeGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.CodeGenConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.metagen.WebResourceCreatorFactory;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.exceptions.PortletCreateException;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ResultContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.NewPortletDialog;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.DataContext;


/**
 * @author Satya
 */
public abstract class NewPortletCreateComponent {
    
    private Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    public Object beforeCreate(String fileName) {
        return null;
    }
    
    public void getFileContent(File folder,String className, Map paramValues) throws Exception {
        
        getCodeGenerator().generateCode(folder,className,paramValues);
        
    }
    
    
    protected abstract String getWebInfDir();
    
    
    protected abstract String getPackage(File dir);
    
    protected abstract String getModuleType();
    
    protected abstract BaseCodeGenerator getCodeGenerator();
    
    protected void doBeforeCreate(String modulePath, String moduleName,  String selectedDir, String clazzName, PortletContext context,AppContext appContext,ResultContext retMap) {
        
    }
    protected void doAfterCreate(PortletContext context, AppContext appContext, String className, String webInfDir) {
        //create portlet
        context.setPortletClass(className);
        WebResourceCreatorFactory.addPortletEntryToPortletXml(webInfDir,context);
    }
    
    public void doCreateClass(String modulePath, String moduleName,  String selectedDir, String clazzName, PortletContext context,AppContext appContext,ResultContext retMap) throws PortletCreateException {
        createNewPortletFile(selectedDir,clazzName,context,retMap);
        String className = (String)retMap.getAttribute(ResultContext.CLASS_NAME);
        doAfterCreate(context,appContext,className,getWebInfDir());
        
    }
    
    protected abstract void refreshPath(String modulePath);
    
    private String createNewPortletFile(String selectedPath, String className, PortletContext context, ResultContext returnVal) {
        
        String portletName = context.getPortletName();
        
        File psiDir = new File(selectedPath);
        if(!psiDir.exists()) {
            psiDir.mkdirs();
        }
        if (psiDir.isDirectory()) {
            
        } else {
            psiDir = psiDir.getParentFile();
        }
        
        String packageStr = "";
        if (psiDir == null) {
            logger.log(Level.FINE,"PsiDir is null ------------");
        } else {
            packageStr = getPackage(psiDir);
            
            logger.log(Level.FINE,"Package: " + packageStr);
            
            if(className == null) {
                NewPortletDialog detailUI = new NewPortletDialog();
                detailUI.open();
                
                className = detailUI.getClassName();
                portletName = detailUI.getPortletName();
            }
            
            if (className.equals(""))
                return null;
            else if (className.contains(".")) {
                JOptionPane.showMessageDialog(null, org.openide.util.NbBundle.getMessage(NewPortletCreateComponent.class, "Msg_Not_a_valid_class_name"));
                return null;
            }
            
            if(portletName == null || portletName.trim().length() == 0)
                portletName = className;
            
            
            Map values = new HashMap();
            
            if (packageStr == null || packageStr.trim().length() == 0)
                packageStr = "";
            values.put(CodeGenConstants.PACKAGE, packageStr);
            values.put(CodeGenConstants.CLASSNAME, className);
            values.put("pc",context);
            
            String fileName = className + ".java";
            
            
            File pFile;
            pFile = new File(psiDir, fileName);
            
            if(!pFile.exists() || (pFile.exists() && CoreUtil.checkIfFileNeedsTobeOverwritten(fileName))) {
              
                try {
                    getFileContent(psiDir,className, values);
                } catch (Exception e) {
                    logger.log(Level.SEVERE,org.openide.util.NbBundle.getMessage(NewPortletCreateComponent.class, "MSG_ERROR"),e);
                }
                pFile = new File(psiDir, fileName);
              
            }else{
                
            }
            
            if (!packageStr.equals("")) {
                
                returnVal.setAttribute(ResultContext.CLASS_NAME, packageStr + "." + className);
                returnVal.setAttribute(ResultContext.PORTLET_NAME, portletName);
                returnVal.setAttribute(ResultContext.FILE_PATH,pFile.getAbsolutePath());
                return packageStr + "." + className;
            } else {
                returnVal.setAttribute(ResultContext.CLASS_NAME, className);
                returnVal.setAttribute(ResultContext.PORTLET_NAME, portletName);
                returnVal.setAttribute(ResultContext.FILE_PATH,pFile.getAbsolutePath());
                return className;
            }
        }
        
        return null;
        
    }
    public String createNewClass(String selectedPath, String className,DataContext context,ResultContext returnVal) {
        
        File psiDir = new File(selectedPath);
        if(!psiDir.exists()) {
            psiDir.mkdirs();
        }
        if (psiDir.isDirectory()) {
            
        } else {
            psiDir = psiDir.getParentFile();
        }
        
        String packageStr = "";
        if (psiDir == null) {
            logger.log(Level.FINE,"PsiDir is null ------------");
        } else {
            packageStr = getPackage(psiDir);
            
            logger.log(Level.FINE,"Package: " + packageStr);
            
            if(className == null) {
                logger.severe("Class Name is null!!!!!!!!!!!! SEVERE MSG");
                return null;
            }
            
            if (className.equals(""))
                return null;
            else if (className.contains(".")) {
                JOptionPane.showMessageDialog(null, org.openide.util.NbBundle.getMessage(NewPortletCreateComponent.class, "Msg_Not_a_valid_class_name"));
                return null;
            }
            
            Map values = new HashMap();
            
            if (packageStr == null || packageStr.trim().length() == 0)
                packageStr = "";
            values.put(CodeGenConstants.PACKAGE, packageStr);
            values.put(CodeGenConstants.CLASSNAME, className);
            values.put("dc",context);
            
            String fileName = className + ".java";
            
            
            File pFile;
            pFile = new File(psiDir, fileName);
            
            FileOutputStream fout;
            if(!pFile.exists() || (pFile.exists() && CoreUtil.checkIfFileNeedsTobeOverwritten(fileName))) {
              
                try {
                    getFileContent(psiDir,className, values);
                } catch (Exception e) {
                    logger.log(Level.SEVERE,org.openide.util.NbBundle.getMessage(NewPortletCreateComponent.class, "MSG_ERROR"),e);
                }
                pFile = new File(psiDir, fileName);
               
            }else{
                
            }
            
            if (!packageStr.equals("")) {
                
                returnVal.setAttribute(ResultContext.CLASS_NAME, packageStr + "." + className);
                returnVal.setAttribute(ResultContext.FILE_PATH,pFile.getAbsolutePath());
                return packageStr + "." + className;
            } else {
                returnVal.setAttribute(ResultContext.CLASS_NAME, className);
                returnVal.setAttribute(ResultContext.FILE_PATH,pFile.getAbsolutePath());
                return className;
            }
        }
        
        return null;
        
    }
    
    private void showErrorMsg() {
        JOptionPane.showMessageDialog(null, org.openide.util.NbBundle.getMessage(NewPortletCreateComponent.class, "MSG_Invalid_WEB-INF_Directory"));
    }
    
}
