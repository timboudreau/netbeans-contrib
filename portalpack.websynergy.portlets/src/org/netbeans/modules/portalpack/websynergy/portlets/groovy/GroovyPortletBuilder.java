/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.portlets.groovy;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeEventNotificationHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.NonJavaPortletConstants;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.api.NonJavaPortletBuilder;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateNotFoundException;
import org.netbeans.modules.portalpack.websynergy.portlets.util.TemplateUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class GroovyPortletBuilder extends NonJavaPortletBuilder implements GroovyPortletConstants{
    
    public static Logger logger = Logger.getLogger(NonJavaPortletConstants.NON_JAVA_PORTLET_LOGGER);

    public String getPortletType() {
        return NbBundle.getMessage(GroovyPortletBuilder.class, "GROOVY_PORTLET_TYPE");
    }

    public String getPortletTypeDesc() {
        return NbBundle.getMessage(GroovyPortletBuilder.class, "GROOVY_PORTLET_TYPE_DESC");
    }

    public String getExtension() {
        return "groovy";
    }

    @Override
    public String getRelativePageRoot() {
        return "WEB-INF" + File.separator + "groovy";
    }
    
    @Override
    public boolean isPortletOfType(PortletType portlet) {
        return GroovyPortletDDHelper.isGroovyPortlet(portlet);
    }
    
    public Set handleCreate(TemplateWizard wizard,boolean isNewPortlet) {
        
        Set result = new HashSet();
        
        FileObject dir = Templates.getTargetFolder(wizard);
        ///FileObject template = Templates.getTemplate(wizard);
        Project project = Templates.getProject(wizard);
       //// DataObject dTemplate = DataObject.find(template);
        String targetName = Templates.getTargetName(wizard);

        TemplateUtil templateUtil = new TemplateUtil(TEMPLATE_PATH);
        
        //create portlet rb file
        FileObject portletrb = dir.getFileObject(targetName, getExtension());
        if(portletrb == null) {
            try {
                portletrb = templateUtil.createFileFromTemplate("View.template", dir, targetName, getExtension());
            } catch (TemplateNotFoundException ex) {
                logger.log(Level.SEVERE,"",ex);
            }
        }
        
        if(!isNewPortlet) {
            result.add(portletrb);
            return result;
        }
            
        
        //crate action.rb 
        String actionRbName = targetName+"_action";
        FileObject actionTemplate = dir.getFileObject(actionRbName,getExtension());
        if(actionTemplate == null) {
            try {
                templateUtil.createFileFromTemplate("Action.template", dir, actionRbName, getExtension());
            } catch (TemplateNotFoundException ex) {
                logger.log(Level.SEVERE,"",ex);
            }
        }

        //Create global ruby files if required
        WebModule wm = PortletProjectUtils.getWebModule(project);
        String[] globalfiles = GroovyPortletProjectUtil.createGroovyFiles(wm,dir);


        if (result == Collections.EMPTY_SET) {
            if(portletrb != null)
                result = Collections.singleton(portletrb);
        } else {
            if(portletrb != null)
                result.add(portletrb);
        }
        
        String rubyFolderRelativePath = FileUtil.getRelativePath(wm.getDocumentBase(), dir);
        if(rubyFolderRelativePath != null)
        {
            if(!rubyFolderRelativePath.startsWith("/") && !rubyFolderRelativePath.startsWith("\\"))
                rubyFolderRelativePath = "/" + rubyFolderRelativePath;
            if(!rubyFolderRelativePath.endsWith("/") && !rubyFolderRelativePath.endsWith("\\"))
                rubyFolderRelativePath += "/";
            rubyFolderRelativePath = rubyFolderRelativePath.replace("\\", "/");
        }

        PortletContext context = (PortletContext) wizard.getProperty("context");
        String viewRuby = targetName + "." + getExtension();
        addRubyPortlet(project,context, rubyFolderRelativePath + viewRuby,rubyFolderRelativePath + actionRbName + ".groovy",globalfiles);
        // Open the new document
        /*OpenCookie open = (OpenCookie) portletrb.getCookie(OpenCookie.class);
        if (open != null) {
            open.open();
        }*/
        return result;
    }
    
    private void addRubyPortlet(Project project, PortletContext pc, String viewPhp,String actionRb,String[] globalfiles) {
        WebModule webModule = PortletProjectUtils.getWebModule(project);
        // Create portlet.xml if not exist
        File filePortlet = new File(FileUtil.toFile(webModule.getWebInf()), "portlet.xml"); // NOI18N

        if (!filePortlet.exists()) {
            logger.log(Level.INFO, "No Portlet.xml found ");
            return;
        }

        //create messages.properties if doesn't exis
        FileObject sourceRoot = PortletProjectUtils.getSourceRoot(project);
        if (sourceRoot != null) {
            try {
                FileObject mObj = sourceRoot.getFileObject("messages.properties");
                if (mObj == null) {
                    FileObject data = sourceRoot.createData("messages", "properties");
                    if (data != null) {
                        logger.log(Level.FINE,"messages.properties is created");
                    } else {
                        logger.info("message.properties could not be created");
                    }
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error", ex);
            }
        }
        PortletApp portletApp = NetbeansUtil.getPortletApp(filePortlet);
        if (portletApp == null) {
            logger.log(Level.WARNING, "Invalid Portlet XML");
            return;
        }

        //if (!RubyPortletDDHelper.isRubyPortletEntryPresent(portletApp)) {
            
            PortletType portletType = portletApp.newPortletType();
            portletType.addDescription(pc.getPortletDescription());
            portletType.setPortletName(pc.getPortletName());
            portletType.addDisplayName(pc.getPortletDisplayName());
            
           
            portletType.setPortletClass(GroovyPortletConstants.GROOVY_PORTLET_CLASS); //NOI18N

            InitParamType initParam = portletType.newInitParamType();
            initParam.setDescription(new String[]{"Portlet Init View Page"});
            initParam.setName(VIEW_URI); //NOI18N

            initParam.setValue(viewPhp);

            portletType.addInitParam(initParam);
            
            
            InitParamType initParam1 = portletType.newInitParamType();
            initParam1.setDescription(new String[]{"Portlet Groovy Action Page"});
            initParam1.setName(ACTION_URI);
            initParam1.setValue(actionRb);
            
            portletType.addInitParam(initParam1);
            
            String gfiles = "";
            for(int i=0;i<globalfiles.length;i++)
            {
                gfiles += globalfiles[i];
                if(i != globalfiles.length -1)
                    gfiles += ",";
            }
            
            if(gfiles.length() > 0)
            {
                InitParamType initParam2 = portletType.newInitParamType();
                initParam2.setDescription(new String[]{"Global groovy files"});
                initParam2.setName(GLOBAL_FILES);
                initParam2.setValue(gfiles);
                portletType.addInitParam(initParam2);
            }
            
            portletType.setExpirationCache(0);

            SupportsType support = portletType.newSupportsType();
            support.setMimeType("text/html"); //NOI18N

            support.addPortletMode("VIEW");   //NOI18N

            portletType.addSupports(support);
            portletType.setSupportedLocale(new String[]{"en"}); //NOI18N

            PortletInfoType portletInfo = portletType.newPortletInfoType();
            portletInfo.setTitle(pc.getPortletTitle());
            portletInfo.setShortTitle(pc.getPortletShortTitle());

            portletType.setPortletInfo(portletInfo);
            portletType.setResourceBundle("messages");//NOI18N

            //add VisualJSFPortlet page as the first portlet entry in portlet.xml
            PortletType[] portletTypes = portletApp.getPortlet();
            if (portletTypes.length == 0) {
                portletApp.addPortlet(portletType);
            } else {

                PortletType firstPortlet = portletApp.getPortlet(0);
                portletApp.setPortlet(0, portletType);
                portletApp.addPortlet(firstPortlet);
            }

            NetbeansUtil.savePortletXML(portletApp, filePortlet);
            
            GroovyPortletProjectUtil.addGroovyLibrary(webModule);

            //fire add portlet event

            if (webModule.getWebInf() != null) {
                String webInfPath = FileUtil.toFile(webModule.getWebInf()).getAbsolutePath();
                PortletXMLChangeEventNotificationHelper.firePortletAddEvent(pc, new AppContext(), webInfPath);
            }
        //}

    }
    
}
