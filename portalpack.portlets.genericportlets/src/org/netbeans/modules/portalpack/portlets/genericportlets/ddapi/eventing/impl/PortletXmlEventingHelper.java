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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.common.VersionNotSupportedException;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.filesystems.FileLock;
import org.openide.util.Exceptions;

/**
 * Helper class for portlet eventing
 * @author Satyaranjan
 */
public class PortletXmlEventingHelper {
    private PortletXMLDataObject dbObj;
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
   
    public PortletXmlEventingHelper(PortletXMLDataObject dbObj) {
        this.dbObj = dbObj;
    }
    
    public QName[] getPublishEvents(String portletName)
    {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) return new QName[0];
        QName[] qNames = portlet.getSupportedPublishingEvent();
        if(qNames == null)
            return new QName[0];
        return qNames;
    }
    
     public QName[] getProcessEvents(String portletName)
    {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) return new QName[0];
        QName[] qNames = portlet.getSupportedProcessingEvent();
        if(qNames == null)
            return new QName[0];
        return qNames;
    }
     
    public boolean addPublishEvent(String portletName, QName evt, Map properties)
    {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) 
        {
            logger.severe("Portlet : "+portletName + " not defined in portlet.xml !!!");
            return false;
        }
        try{
             PortletApp portletApp = dbObj.getPortletApp();
             EventDefinitionType eventDefinitionType = portletApp.newEventDefinitionType();
             QName qName = new QName(((BaseBean)portletApp).getDefaultNamespace(),evt.getLocalPart());
             eventDefinitionType.setQname(qName);
             portletApp.addEventDefinition(eventDefinitionType);
        }catch (Exception e){
            logger.log(Level.SEVERE,"Error in Adding Publish Events",e);
        }
        
        
        
        QName[] list = getPublishEvents(portletName);
        portlet.addSupportedPublishingEvent(evt);
        save();
        return true;
        
    }
    
    public PortletType getPortlet(String portletName)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(IOException e){
            return null;
        }
        PortletType[] portlets = portletApp.getPortlet();
        if(portlets == null) return null;
        for(int i=0;i<portlets.length;i++)
        {
            if(portlets[i].getPortletName().equals(portletName))
            {
                return portlets[i];
            }
        }
        return null;
    }
    
    public boolean isEventingSupported()
    {
        try {
            org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp portletApp = dbObj.getPortletApp();
            try {
                portletApp.getEventDefinition();
            } catch (VersionNotSupportedException e) {
                return false;
            }
            return true;
        } catch (IOException ex) {
            
        }
        return false;
    }
    
    public void save()
    {
        try {
            PortletApp portletApp = dbObj.getPortletApp();
            FileLock lock = dbObj.getPrimaryFile().lock();
            OutputStream out = dbObj.getPrimaryFile().getOutputStream(lock);
            ((BaseBean)portletApp).write(out);
            try{
                 out.flush();
                 out.close();
            }catch(Exception e){
                logger.log(Level.SEVERE,"Error",e);
            }
            
            lock.releaseLock();
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }

}
