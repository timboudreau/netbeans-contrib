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
package org.netbeans.modules.portalpack.websynergy.portlets.php;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.NonJavaPortletConstants;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.api.NonJavaPortletBuilder;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;

/**
 *
 * @author satyaranjan
 */
public class PhpPortletBuilder extends NonJavaPortletBuilder {

    private static Logger logger = Logger.getLogger(NonJavaPortletConstants.NON_JAVA_PORTLET_LOGGER);

    public String getPortletType() {
        return "Php Portlet";
    }

    public String getPortletTypeDesc() {
        return "Php Portlet Desc";
    }

    public String getExtension() {
        return "php";
    }

    @Override
    public boolean isPortletOfType(PortletType portlet) {
        return PhpPortletDDHelper.isPhpPortlet(portlet);
    }

    @Override
    public Set handleCreate(TemplateWizard wizard, boolean newPortlet) {

        Set result = new HashSet();

        FileObject dir = Templates.getTargetFolder(wizard);
        DataFolder df = DataFolder.findFolder(dir);
        Project project = Templates.getProject(wizard);
        String targetName = Templates.getTargetName(wizard);
        FileObject template = Templates.getTemplate(wizard);
        DataObject dTemplate = null;

        FileObject viewPhpObj = dir.getFileObject(targetName, getExtension());

        try {
            dTemplate = DataObject.find(template);
        } catch (DataObjectNotFoundException ex) {
            logger.log(Level.SEVERE, "Error", ex);
        }

        if (dTemplate == null) {
            return result;
        }
        if (viewPhpObj == null) {
            Map<String, String> templateParameters = new HashMap<String, String>();
            templateParameters.put("MSG", "Hello Php Portlet !!!"); //NOI18N

            DataObject viewPhpDO = null;
            try {
                viewPhpDO = dTemplate.createFromTemplate(df, targetName, templateParameters);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error", ex);
            }

            if (viewPhpDO != null) {
                viewPhpObj = viewPhpDO.getPrimaryFile();
            }
            if (viewPhpObj != null) {
                result.add(viewPhpObj);
            } else {
                return result;
            }
        }
        //check if index.php is present
        FileObject webDocbase = PortletProjectUtils.getDocumentRoot(project);
        FileObject indexPhpObj = webDocbase.getFileObject("index", "php");
        if (indexPhpObj == null) {
            Map<String, String> templateParameters = new HashMap<String, String>();
            templateParameters.put("MSG", "Index Php Page. Do not delete this page." +
                    " If you delete this page then your portlet may not work."); //NOI18N

            DataFolder webDocDO = DataFolder.findFolder(webDocbase);
            try {
                DataObject indexObj = dTemplate.createFromTemplate(webDocDO, "index", templateParameters);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        if (!newPortlet) {
            return result;
        }
        PortletContext context = (PortletContext) wizard.getProperty("context");
        String phpRelativePath = FileUtil.getRelativePath(webDocbase, dir);
        if (phpRelativePath != null) {
            if (!phpRelativePath.startsWith("/") && !phpRelativePath.startsWith("\\")) {
                phpRelativePath = "/" + phpRelativePath;
            }
            if (!phpRelativePath.endsWith("/") && !phpRelativePath.endsWith("\\")) {
                phpRelativePath += "/";
            }
            phpRelativePath = phpRelativePath.replace("\\", "/");
        }
        String viewPhp = phpRelativePath + targetName + "." + getExtension();
        addPhpPortletToPortletXML(project, context, viewPhp);

        return result;

    }

    private void addPhpPortletToPortletXML(Project project, PortletContext pc, String viewPhp) {
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
                        System.out.println("messages.properties is created");
                    } else {
                        System.out.println("message.properties could not be created");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PortletApp portletApp = NetbeansUtil.getPortletApp(filePortlet);
        if (portletApp == null) {
            logger.log(Level.WARNING, "Invalid Portlet XML");
            return;
        }

        //if (!PortletDDHelper.isPhpPortletEntryPresent(portletApp)) {

        PortletType portletType = portletApp.newPortletType();
        portletType.addDescription(pc.getPortletDescription());
        portletType.setPortletName(pc.getPortletName());
        portletType.addDisplayName(pc.getPortletDisplayName());
        portletType.setPortletClass(PhpPortletConstants.PHP_PORTLET_CLASS); //NOI18N

        InitParamType initParam = portletType.newInitParamType();
        initParam.setDescription(new String[]{"Portlet Init View Page"});
        initParam.setName(PhpPortletConstants.VIEW_URI); //NOI18N

        initParam.setValue(viewPhp);

        portletType.addInitParam(initParam);

        InitParamType initParam1 = portletType.newInitParamType();
        initParam1.setDescription(new String[]{"Add Portlet Params"});
        initParam1.setName("add-portlet-params");
        initParam1.setValue("true");
        portletType.addInitParam(initParam1);

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

        portletApp.addPortlet(portletType);


        NetbeansUtil.savePortletXML(portletApp, filePortlet);

        //fire add portlet event

        if (webModule.getWebInf() != null) {
            String webInfPath = FileUtil.toFile(webModule.getWebInf()).getAbsolutePath();
            PortletXMLChangeEventNotificationHelper.firePortletAddEvent(pc, new AppContext(), webInfPath);
        }
//        }

    }
}
