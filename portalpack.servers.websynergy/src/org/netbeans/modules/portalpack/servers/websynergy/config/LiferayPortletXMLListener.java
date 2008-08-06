/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.servers.websynergy.config;

import java.io.File;
import java.io.IOException;
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

/**
 *
 * @author satyaranjan
 */
public class LiferayPortletXMLListener implements PortletXMLChangeListener{

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    public void addPortlet(PortletContext portletContext, AppContext appContext, String webInfDir) {
        
        addPortletToLifeRayPortletFile(portletContext, appContext, webInfDir);
        addPortletToLifeRayDisplayFile(portletContext, appContext, webInfDir);
    }

    private void addPortletToLifeRayDisplayFile(PortletContext portletContext, AppContext appContext, String webInfDir) {
        File liferayDisplayXml = new File(webInfDir + File.separator + "liferay-display.xml"); //NOI18N
        if(!liferayDisplayXml.exists())
            return;
        try{
            Display display = Display.createGraph(liferayDisplayXml);
            Category[] cats = display.getCategory();
            Category cat = null;
            if(cats.length == 0){
                cat = display.newCategory();
                cat.setAttributeValue("name", LiferayModuleConfiguration.PORTLET_CATEGORY); //NOI18N
                display.setCategory(new Category[]{cat});
            } else {
                cat = cats[0];
            }
            
            cat.addPortlet(portletContext.getPortletName());
            int index = cat.getPortlet().length;
            index --;
            if(index >= 0)
                cat.setPortletId(index, portletContext.getPortletName());
            
            NetbeansUtil.saveBean(display, liferayDisplayXml);
            
        }catch(Exception e){
            logger.info(e.getMessage());
            //do nothing
        }
    }
    
    private void addPortletToLifeRayPortletFile(PortletContext portletContext, AppContext appContext, String webInfDir)
    {
        File liferayPortletXml = new File(webInfDir + File.separator + "liferay-portlet.xml");
        if(!liferayPortletXml.exists())
            return;
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
    
}
