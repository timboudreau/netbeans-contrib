/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.servers.websynergy.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.servers.websynergy.common.WebSpacePropertiesUtil;
import org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Category;
import org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Display;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.LiferayPortletApp;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.Portlet;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class LiferayPortletXMLListener implements PortletXMLChangeListener {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    public static String SPRING_PORTLET = "org.springframework.web.portlet.DispatcherPortlet";
    public static String JSF_PORTLET = "com.sun.faces.portlet.FacesPortlet";
    public static String RUBY_PORTLET = "com.liferay.util.bridges.ruby.RubyPortlet";

    public void addPortlet(PortletContext portletContext, AppContext appContext, String webInfDir) {

        addPortletToLifeRayPortletFile(portletContext, appContext, webInfDir);
        addPortletToLifeRayDisplayFile(portletContext, appContext, webInfDir);

        Properties pluginPackageProperties = getPluginPackageProperties(webInfDir);

        boolean addJars = addDependencyJars(pluginPackageProperties, portletContext, webInfDir);
        boolean addProps = addPluginPackageProperties(pluginPackageProperties, portletContext, webInfDir);

        if (addJars || addProps) {
            storePluginPackageProperties(pluginPackageProperties, webInfDir);
        }

        FileObject webInfFO = FileUtil.toFileObject(new File(webInfDir));
        if(webInfFO != null) {
            Project prj = FileOwnerQuery.getOwner(webInfFO);
            if(prj != null) {
                //check if Liferay/WebSpace
                boolean isLiferay = WebSpacePropertiesUtil.isWebSynergyServer(prj);
                if(isLiferay) {
                    String prjName = ProjectUtils.getInformation(prj).getName();
                    if(prjName != null && prjName.endsWith("-hook")) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message(
                                NbBundle.getMessage(LiferayPortletXMLListener.class,
                                "MSG_PORTLET_NOT_ALLOWED_IN_HOOK_PROJECT"),NotifyDescriptor.WARNING_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                    }
                }
            }

        }
    }

    private void addPortletToLifeRayDisplayFile(PortletContext portletContext, AppContext appContext, String webInfDir) {
        File liferayDisplayXml = new File(webInfDir + File.separator + "liferay-display.xml"); //NOI18N

        if (!liferayDisplayXml.exists()) {
            return;
        }
        try {
            Display display = Display.createGraph(liferayDisplayXml);
            Category[] cats = display.getCategory();
            Category cat = null;
            if (cats.length == 0) {
                cat = display.newCategory();
                cat.setAttributeValue("name", LiferayModuleConfiguration.PORTLET_CATEGORY); //NOI18N

                display.setCategory(new Category[]{cat});
            } else {
                cat = cats[0];
            }

            cat.addPortlet(portletContext.getPortletName());
            int index = cat.getPortlet().length;
            index--;
            if (index >= 0) {
                cat.setPortletId(index, portletContext.getPortletName());
            }
            NetbeansUtil.saveBean(display, liferayDisplayXml);

        } catch (Exception e) {
            logger.info(e.getMessage());
        //do nothing
        }
    }

    private void addPortletToLifeRayPortletFile(PortletContext portletContext, AppContext appContext, String webInfDir) {
        File liferayPortletXml = new File(webInfDir + File.separator + "liferay-portlet.xml");
        if (!liferayPortletXml.exists()) {
            return;
        }
        try {

            LiferayPortletApp lpApp = LiferayPortletApp.createGraph(liferayPortletXml);

            String portletName = portletContext.getPortletName();
            Portlet p = lpApp.newPortlet();
            p.setPortletName(portletName);
            p.setInstanceable("true");
            lpApp.addPortlet(p);
            NetbeansUtil.saveBean(lpApp, liferayPortletXml);
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }

    }

    private boolean addDependencyJars(Properties pluginPackageProp, PortletContext pc, String webInfDir) {

        String className = pc.getPortletClass();
        if (className != null && className.equals(SPRING_PORTLET)) {

            try {

                String depJar = pluginPackageProp.getProperty("portal.dependency.jars");

                if (depJar == null || depJar.trim().length() == 0) {
                    pluginPackageProp.setProperty("portal.dependency.jars", "commons-fileupload.jar");
                } else {

                    if (depJar.indexOf("commons-fileupload.jar") != -1) {
                        return false;
                    }
                    if (depJar.endsWith(",")) {
                        depJar += "commons-fileupload.jar";
                    } else {
                        depJar += "," + "commons-fileupload.jar";
                    }
                    pluginPackageProp.setProperty("portal.dependency.jars", depJar);
                }

            } catch (Exception e) {
            }
            return true;
        } else if (className != null && className.equals(RUBY_PORTLET)) {

            
            try {

                String depJar = pluginPackageProp.getProperty("portal.dependency.jars");

                if (depJar == null || depJar.trim().length() == 0) {
                    pluginPackageProp.setProperty("portal.dependency.jars", "bsf.jar");
                } else {

                    if (depJar.indexOf("bsf.jar") != -1) {
                        return false;
                    }
                    if (depJar.endsWith(",")) {
                        depJar += "bsf.jar";
                    } else {
                        depJar += "," + "bsf.jar";
                    }
                    pluginPackageProp.setProperty("portal.dependency.jars", depJar);
                }

            } catch (Exception e) {
            }
            return true;
        }

        return false;
    }

    private boolean addPluginPackageProperties(Properties pluginPackage, PortletContext portletContext, String webInfDir) {
        String className = portletContext.getPortletClass();
        if (className != null && className.equals(JSF_PORTLET)) {

            String speedFilterEnabled =
                    pluginPackage.getProperty("speed-filters-enabled");
            if (speedFilterEnabled == null || speedFilterEnabled.trim().length() == 0) {

                pluginPackage.setProperty("speed-filters-enabled", "false");
                return true;
            }

            return false;

        }

        return false;
    }

    private Properties getPluginPackageProperties(String webInfDir) {
        InputStream in = null;
        try {
            File file = new File(webInfDir + File.separator + "liferay-plugin-package.properties");

            if (!file.exists()) {
                return new Properties();
            }
            in = new FileInputStream(file);

            Properties pluginPackageProp = new Properties();
            pluginPackageProp.load(in);

            return pluginPackageProp;
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        }
        return new Properties();
    }

    private void storePluginPackageProperties(Properties pluginPackage, String webInfDir) {
        File file = new File(webInfDir + File.separator + "liferay-plugin-package.properties");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            pluginPackage.store(out, "");
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }

    }
}
