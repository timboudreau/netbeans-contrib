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
package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl;

import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.FilterMappingType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.filesystems.FileLock;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * This is a helper class to add/delete/modify portlet xml elements
 * @author Satyaranjan
 */
public class PortletXmlHelper {
    
    private PortletXMLDataObject dbObj;
    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    
    public PortletXmlHelper(PortletXMLDataObject pDObj) {
        this.dbObj = pDObj;
    }
    
    public boolean addFilter(String portletName, String filterName)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return false;
        }
        if(portletApp == null)
        {
            logger.log(Level.SEVERE,"Portlet App is null in addFilter() : PortletXmlHelper");
            return false;
        }
        
        FilterMappingType filterMapping = null;
        FilterMappingType[] filterMappings = portletApp.getFilterMapping();
        if(filterMappings == null)
        {
            
        }
        else {
            for(FilterMappingType filterMap:filterMappings)
            {
                if(filterMap.getFilterName().equals(filterName))
                {
                    filterMapping = filterMap;
                    break;
                }
            }
        }
        
        if(filterMapping == null)
        {
            filterMapping = portletApp.newFilterMappingType();
            filterMapping.setFilterName(filterName);
            portletApp.addFilterMapping(filterMapping);
        }else{
            
            if(isPortletAlreadyPresentInFilterMapping(filterMapping, portletName))
            {
                NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(PortletXmlHelper.class, "PORTLET_FILTER_MAPPING_ALREADY_PRESENT"),
                                                    NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                return false;
            }
            
        }
       
        filterMapping.addPortletName(portletName);
        
        save();
        
        return true;
    }
    
   
    public boolean removeFilterMapping(String portletName,String filterName)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return false;
        }
        if(portletApp == null)
        {
            logger.log(Level.SEVERE,"Portlet App is null in removeFilterMapping() : PortletXmlHelper");
            return false;
        }
        
        boolean isChanged = false;
        FilterMappingType[] filterMappings = portletApp.getFilterMapping();
        if(filterMappings == null)
        {
            return true;
        }
        else {
            for(FilterMappingType filterMap:filterMappings)
            {
                if(filterMap.getFilterName().equals(filterName))
                {
                    if(isPortletAlreadyPresentInFilterMapping(filterMap, portletName))
                    {
                        filterMap.removePortletName(portletName);
                        if(filterMap.getPortletName().length == 0)
                            portletApp.removeFilterMapping(filterMap);
                        isChanged = true;
                    }
                    
                }
            }
        }
        
        if(isChanged)
            save();      
        return true;
    }
    
    public boolean addPublicRenderParameterAsQName(String identifier,QName qname)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return false;
        }
        if(portletApp == null)
        {
            logger.log(Level.SEVERE,"Portlet App is null in addPublicRenderParameterAsQName() : PortletXmlHelper");
            return false;
        }
        
        PublicRenderParameterType publicRenderParameterType = portletApp.newPublicRenderParameterType();
        publicRenderParameterType.setIdentifier(identifier);
        publicRenderParameterType.setQname(qname);
        
        portletApp.addPublicRenderParameter(publicRenderParameterType);
        save();
        return true;
    }
    
    public boolean addPublicRenderParameterAsName(String identifier,String name)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return false;
        }
        if(portletApp == null)
        {
            logger.log(Level.SEVERE,"Portlet App is null in addPublicRenderParameterAsName() : PortletXmlHelper");
            return false;
        }
        
        PublicRenderParameterType publicRenderParameterType = portletApp.newPublicRenderParameterType();
        publicRenderParameterType.setIdentifier(identifier);
        publicRenderParameterType.setName(name);
        
        portletApp.addPublicRenderParameter(publicRenderParameterType);
        save();
        return true;
    }
    public boolean addSupportedPublicRenderParameter(String portletName,String identifier)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return false;
        }
        if(portletApp == null)
        {
            logger.log(Level.SEVERE,"Portlet App is null in addSupportedPublicRenderParameter() : PortletXmlHelper");
            return false;
        }
        
        PortletType portletType = null;
        PortletType[] portlets = portletApp.getPortlet();
        for(PortletType portlet:portlets)
        {
            if(portlet.getPortletName().equals(portletName))
            {
                portletType = portlet;
                break;
            }
        }
        
        if(portletType == null) 
        {
            logger.log(Level.WARNING,"No portlet found with name : "+portletName);
            return false;
        }
        
        String[] params = portletType.getSupportedPublicRenderParameter();
        
        for(String param:params)
        {
            if(param.equalsIgnoreCase(identifier))
            {
                NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(PortletXmlHelper.class, "SUPPORTED_PUBLIC_RENDER_PARAMETER_ALREADY_PRESENT"),
                                                    NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                return false;
            }
        }
        
        portletType.addSupportedPublicRenderParameter(identifier);
        
        
        save();
        
        return true;
    }
    
    public boolean removeSupportedPublicRenderParameter(String portletName,String identifier)
    {
         PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return false;
        }
        if(portletApp == null)
        {
            logger.log(Level.SEVERE,"Portlet App is null in addFilter() : PortletXmlHelper");
            return false;
        }
        
        PortletType portletType = null;
        PortletType[] portlets = portletApp.getPortlet();
        for(PortletType portlet:portlets)
        {
            if(portlet.getPortletName().equals(portletName))
            {
                portletType = portlet;
                break;
            }
        }
        
        if(portletType == null) 
        {
            logger.log(Level.WARNING,"No portlet found with name : "+portletName);
            return false;
        }
       
        portletType.removeSupportedPublicRenderParameter(identifier);
        
        save();
        
        return true;
    }
    
    private static boolean isPortletAlreadyPresentInFilterMapping(FilterMappingType filterMapping, String portletName)
    {
        String[] portlets = filterMapping.getPortletName();
        for(String portlet:portlets)
        {
            if(portlet.equals(portletName))
                return true;
        }
        return false;
    }
    
    public void save()
    {
        try {
            PortletApp portletApp = dbObj.getPortletApp();
            FileLock lock = dbObj.getPrimaryFile().lock();
            OutputStream out = dbObj.getPrimaryFile().getOutputStream(lock);
          // ((BaseBean)portletApp).write(out);
            portletApp.write(out);
            try{
                 out.flush();
                 out.close();
            }catch(Exception e){
                logger.log(Level.SEVERE,"Error",e);
            }
            
            lock.releaseLock();
            
        } catch (IOException ex) {
           logger.log(Level.SEVERE,"Error",ex);
        }
        
    }

}
