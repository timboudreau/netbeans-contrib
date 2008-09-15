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
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.AppContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.listeners.PortletXMLChangeListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Category;
import org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Display;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.LiferayPortletApp;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.Portlet;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class LiferayPortletXMLListener implements PortletXMLChangeListener {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    public static String SPRING_PORTLET = "org.springframework.web.portlet.DispatcherPortlet";

    public void addPortlet(PortletContext portletContext, AppContext appContext, String webInfDir) {

        addPortletToLifeRayPortletFile(portletContext, appContext, webInfDir);
        addPortletToLifeRayDisplayFile(portletContext, appContext, webInfDir);
        addDependencyJars(portletContext, webInfDir);
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

    private void addDependencyJars(PortletContext pc, String webInfDir) {

        String className = pc.getPortletClass();
        if (className != null && className.equals(SPRING_PORTLET)) {

            InputStream in = null;
            OutputStream out = null;
            try {
                File file = new File(webInfDir + File.separator + "liferay-plugin-package.properties");

                if (!file.exists()) {
                    return;
                }
                in = new FileInputStream(file);

                Properties pluginPackageProp = new Properties();
                pluginPackageProp.load(in);

                String depJar = pluginPackageProp.getProperty("portal.dependency.jars");

                if (depJar == null || depJar.trim().length() == 0) {
                    pluginPackageProp.setProperty("portal.dependency.jars", "commons-fileupload.jar");
                } else {

                    if (depJar.indexOf("commons-fileupload.jar") != -1) {
                        return;
                    }
                    if (depJar.endsWith(",")) {
                        depJar += "commons-fileupload.jar";
                    } else {
                        depJar += "," + "commons-fileupload.jar";
                    }
                    pluginPackageProp.setProperty("portal.dependency.jars", depJar);

                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            //Exceptions.printStackTrace(ex);
                        }
                    }
                    try {
                        out = new FileOutputStream(file);
                        pluginPackageProp.store(out, "");
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }

                }
            } catch (Exception e) {
            }finally {
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        
                    }
                }
            }

        }
    }
}
