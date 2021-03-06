/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.servers.websynergy.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.portalpack.servers.core.PSModuleConfiguration;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Category;
import org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Display;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.LiferayPortletApp;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.Portlet;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.RoleMapper;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lpp.impl430.LiferayVersions;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lpp.impl430.PluginPackage;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lpp.impl430.Types;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import java.util.Properties;
import java.util.logging.Level;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.servers.websynergy.dd.lpp.impl430.Licenses;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class LiferayModuleConfiguration extends PSModuleConfiguration {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    public static String PORTLET_CATEGORY="User_Portlets";
    public LiferayModuleConfiguration(J2eeModule j2eeModule) {
        super(j2eeModule);
    }

    @Override
    public void createConfiguration() {
        File liferayPortletFile = getJ2eeModule().getDeploymentConfigurationFile("WEB-INF/liferay-portlet.xml");
        getLiferayPortletFile(liferayPortletFile);
        File liferayDisplayFile = getJ2eeModule().getDeploymentConfigurationFile("WEB-INF/liferay-display.xml");
        getLiferayPortletDisplay(liferayDisplayFile);
        File liferayPluginPackagePropFile = getJ2eeModule().getDeploymentConfigurationFile("WEB-INF/liferay-plugin-package.properties");
        getLiferayPluginPackageProperties(liferayPluginPackagePropFile);
    }

    private void getLiferayPortletFile(File liferayPortletFile) {
        if (liferayPortletFile == null) {
            return;
        }
        if (liferayPortletFile.exists()) {
            return;
        }
        
        if(!LiferayXMLUtil.createLRXMLFile(LiferayXMLUtil.LR_PORTLET_TEMPLATE, liferayPortletFile.getParent(), "liferay-portlet"))
        {
            logger.severe("liferay-portlet.xml could not be generated");
            return;
        }
        LiferayPortletApp lpApp = null;
        try {

            lpApp = LiferayPortletApp.createGraph(liferayPortletFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"Error",ex);
            return;
        }
        RoleMapper roleMapper = lpApp.newRoleMapper();
        roleMapper.setRoleName("administrator");
        roleMapper.setRoleLink("Administrator");
        lpApp.addRoleMapper(roleMapper);

        RoleMapper roleMapper1 = lpApp.newRoleMapper();
        roleMapper1.setRoleName("guest");
        roleMapper1.setRoleLink("Guest");
        lpApp.addRoleMapper(roleMapper1);

        RoleMapper roleMapper2 = lpApp.newRoleMapper();
        roleMapper2.setRoleName("power-user");
        roleMapper2.setRoleLink("Power User");
        lpApp.addRoleMapper(roleMapper2);

        RoleMapper roleMapper3 = lpApp.newRoleMapper();
        roleMapper3.setRoleName("user");
        roleMapper3.setRoleLink("User");
        lpApp.addRoleMapper(roleMapper3);

        //getPortletApp
        PortletApp pApp = getPortletApp();
        if (pApp != null) {
            PortletType[] ps = pApp.getPortlet();
            for (PortletType p : ps) {
                Portlet portlet = lpApp.newPortlet();
                portlet.setPortletName(p.getPortletName());
                portlet.setInstanceable("true");
                lpApp.addPortlet(portlet);
            }
        }
        try {
            lpApp.write(liferayPortletFile);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }

    }

    private void getLiferayPortletDisplay(File liferayDisplayFile) {
        if (liferayDisplayFile == null) {
            return;
        }
        if (liferayDisplayFile.exists()) {
            return;
        }
        
        if(!LiferayXMLUtil.createLRXMLFile(LiferayXMLUtil.LR_DISPLAY_TEMPLATE, liferayDisplayFile.getParent(), "liferay-display"))
        {
            logger.severe("liferay-display.xml could not be generated !!!");
            return;
        }
        
        Display display = null;
        try {

            display = Display.createGraph(liferayDisplayFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"Error",ex);
            return;
        }
        Category cat = display.newCategory();
        cat.setAttributeValue("name", PORTLET_CATEGORY); //NOI18N

        display.setCategory(new Category[]{cat});
        //getPortletApp
        PortletApp pApp = getPortletApp();
        if(pApp != null)
        {
             PortletType[] ps = pApp.getPortlet();
             int i = 0;
             for (PortletType p : ps) {
                cat.addPortlet(p.getPortletName());
                cat.setPortletId(i, p.getPortletName());
                i++;
             }
        }
        try {
            NetbeansUtil.saveBean(display,liferayDisplayFile);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
    
    private void getLiferayPluginPackageProperties(File pluginPackageProp)
    {
        
        if(pluginPackageProp.exists())
            return;
        Properties props = new Properties();
        props.setProperty("tags", "portlet");
        props.setProperty("portal.dependency.jars","commons-logging.jar");
        try{
            OutputStream out = new FileOutputStream(pluginPackageProp);
            props.store(out, "liferay-plugin-package.properties");
            out.flush();
            out.close();
        }catch(Exception e){
            logger.info(e.getMessage());
        }
                
    }

    private PortletApp getPortletApp() {
        File portletXml = getJ2eeModule().getDeploymentConfigurationFile("WEB-INF/portlet.xml");
        if (!portletXml.exists()) {
            return null;
        /*try{
        PortletApp portletApp = PortletXMLFactory.createGraph(portletXml);
        return portletApp;
        }catch(Exception e){
        logger.info(e.getMessage());
        }*/
        //return null;
        }
        FileObject pxObj = FileUtil.toFileObject(portletXml);
        if (pxObj != null) {
            PortletXMLDataObject dbObj = null;
            try {
                dbObj = (PortletXMLDataObject) DataObject.find(pxObj);
                return dbObj.getPortletApp();
            } catch (DataObjectNotFoundException ex) {
                logger.info(ex.getMessage());
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
            return null;
        }
        
        return null;
    }
}

