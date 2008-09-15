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
package org.netbeans.modules.portalpack.portlets.spring.util;

import org.netbeans.modules.portalpack.portlets.spring.api.ControllerTypeHandler;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.j2ee.dd.api.common.CommonDDBean;
import org.netbeans.modules.j2ee.dd.api.common.CreateCapability;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.ServletMapping;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeEventNotificationHelper;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class SpringProjectHelper {

    private Logger logger = Logger.getLogger(SpringPortletConstants.LOGGER);
    public static final String CONTEXT_LOADER = "org.springframework.web.context.ContextLoaderListener"; // NOI18N

    public static final String VIEWRENDER_SERVLET = "org.springframework.web.servlet.ViewRendererServlet"; // NOI18N

    public static final String VIEWRENDER_SERVLET_NAME = "ViewRendererServlet";
    public static final String DISPATCHER_SERVLET = "org.springframework.web.servlet.DispatcherServlet"; // NOI18N


    public void addSpringPortletConfig(WebModule webModule) {
        CreateSpringConfig createSpringConfig = new CreateSpringConfig(webModule);
        FileObject webInf = webModule.getWebInf();
        if (webInf != null) {
            try {
                FileSystem fs = webInf.getFileSystem();
                fs.runAtomicAction(createSpringConfig);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }

        //Add spring-portlet library
        addSpringPortletLibrary(webModule);

    }

    public void addSpringPortletContext(ControllerTypeHandler controllerType, WebModule webModule, String controllerClass, String configFolder, String contextFileName, PortletContext pc, Map val, TemplateWizard wizard) {

        //String portletName = pc.getPortletName();
        //String contextFileName = portletName + "-portlet";

        FileObject folder = webModule.getWebInf();

        //check if file is already there...............
        TemplateUtil templateUtil = new TemplateUtil(SpringPortletConstants.TEMPLATE_FOLDER);

        try {

            Map values = new HashMap();
            values.put("CONTROLLER_CLASS", controllerClass);
            values.put("pc", pc);
            FileObject template = templateUtil.getTemplateFile("portlet-context.xml");
            FileObject newFile = templateUtil.mergeTemplateToFile(template, folder, contextFileName, values);

            String beanID = "portletController";
            controllerType.addBeanProperties(newFile, beanID, val, wizard);

        } catch (TemplateNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private void addSpringPortletLibrary(WebModule wm) {

        final FileObject documentBase = wm.getDocumentBase();
        Project project = FileOwnerQuery.getOwner(documentBase);
        Sources sources = project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        if (groups.length > 0) {

            ClassPath cp = ClassPath.getClassPath(wm.getDocumentBase(), ClassPath.COMPILE);
            if (cp == null || cp.findResource("org/springframework/web/portlet/DispatcherPortlet.class") == null) { //NOI18N

                Library spLibrary = LibraryManager.getDefault().getLibrary("spring-portlet-lib");

                for (int i = 0; i < groups.length; i++) {
                    try {
                        ProjectClassPathModifier.addLibraries(new Library[]{spLibrary}, groups[i].getRootFolder(), ClassPath.COMPILE);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (UnsupportedOperationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    private class CreateSpringConfig implements FileSystem.AtomicAction {

        private WebModule webModule;

        public CreateSpringConfig(WebModule webModule) {

            this.webModule = webModule;

        }

        public void run() throws IOException {
            FileObject dd = webModule.getDeploymentDescriptor();
            WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
            //addContextParam(ddRoot, "contextConfigLocation", "/WEB-INF/applicationContext.xml"); // NOI18N
            // addListener(ddRoot, CONTEXT_LOADER);
            Servlet[] servlets = ddRoot.getServlet();
            boolean servletExists = false;
            for (Servlet servlet : servlets) {
                if (servlet.getServletClass().equals(VIEWRENDER_SERVLET)) {
                    servletExists = true;
                    break;
                }
            }

            if (!servletExists) {
                addServlet(ddRoot, VIEWRENDER_SERVLET_NAME, VIEWRENDER_SERVLET, "/WEB-INF/servlet/view", null); // NOI18N

                ddRoot.write(dd);
            }

            //get applicationContext

            String applicationContext = null;
            InitParam[] initParams = ddRoot.getContextParam();

            for (InitParam initParam : initParams) {

                if (initParam.getParamName().equals("contextConfigLocation")) {

                    applicationContext = initParam.getParamValue();
                    break;
                }
            }

            if (applicationContext == null || applicationContext.trim().length() == 0) {
                applicationContext = "/WEB-INF/applicationContext.xml";
            }
            if (applicationContext != null && applicationContext.startsWith("/")) {
                applicationContext = applicationContext.substring(1);
            //applicationContext = "applicationContext.xml";
            }
            addViewResolver(webModule, applicationContext);


        }

        protected Servlet addServlet(WebApp webApp, String name, String classname, String pattern, String loadOnStartup) throws IOException {

            Servlet servlet = (Servlet) createBean(webApp, "Servlet"); // NOI18N

            servlet.setServletName(name);
            servlet.setServletClass(classname);
            if (loadOnStartup != null) {
                servlet.setLoadOnStartup(new BigInteger(loadOnStartup));
            }
            webApp.addServlet(servlet);
            if (pattern != null) {
                addServletMapping(webApp, name, pattern);
            }
            return servlet;
        }

        protected ServletMapping addServletMapping(WebApp webApp, String name, String pattern) throws IOException {
            ServletMapping mapping = (ServletMapping) createBean(webApp, "ServletMapping"); // NOI18N

            mapping.setServletName(name);
            mapping.setUrlPattern(pattern);
            webApp.addServletMapping(mapping);
            return mapping;
        }

        protected CommonDDBean createBean(CreateCapability creator, String beanName) throws IOException {
            CommonDDBean bean = null;
            try {
                bean = creator.createBean(beanName);
            } catch (ClassNotFoundException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
                throw new IOException("Error creating bean with name:" + beanName); // NOI18N

            }
            return bean;
        }

        private void addViewResolver(WebModule webModule, String applicationContext) {

            FileObject rootDir = null;

            String webInfDirName = null;
            if (applicationContext.startsWith("WEB-INF")) {
                webInfDirName = "WEB-INF";
                applicationContext = applicationContext.replaceFirst("WEB-INF/", "");
            } else if (applicationContext.startsWith("web-inf")) {
                webInfDirName = "web-inf";
                applicationContext = applicationContext.replaceFirst("web-inf/", "");
            }

            if (webInfDirName != null) {
                rootDir = webModule.getWebInf();
            } else {
                rootDir = webModule.getDocumentBase();
            }

            FileObject applicationContextObj = rootDir.getFileObject(applicationContext);
            if (applicationContextObj == null) {
                applicationContextObj = addApplicationContext(rootDir);
                
                if(applicationContextObj == null)
                    return;
            }

            BeanXMLUtil beanUtil = new BeanXMLUtil(applicationContextObj);
            beanUtil.addViewResolverBean();
            beanUtil.store();

        }

        private FileObject addApplicationContext(FileObject folder) {

            TemplateUtil templateUtil = new TemplateUtil(SpringPortletConstants.TEMPLATE_FOLDER);
            try {
                return templateUtil.createFileFromTemplate("applicationContext.xml", folder, "applicationContext", "xml");
            } catch (TemplateNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }
    }

    public void addPortletToPortletXML(Project project, PortletContext pc) {

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
        portletType.setPortletClass(SpringPortletConstants.SPRING_PORTLET); //NOI18N

        portletType.setExpirationCache(0);

        SupportsType support = portletType.newSupportsType();
        support.setMimeType("text/html"); //NOI18N

        support.addPortletMode("VIEW");   //NOI18N

        if (pc.isEditMode()) {
            support.addPortletMode("EDIT");
        }
        if (pc.isHelpMode()) {
            support.addPortletMode("HELP");
        }
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
            pc.setPortletClass(SpringPortletConstants.SPRING_PORTLET);
            PortletXMLChangeEventNotificationHelper.firePortletAddEvent(pc, new AppContext(), webInfPath);
        }
//        }

    }
}
