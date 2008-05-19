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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PublicRenderParameterType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.openide.DialogDescriptor;
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
        
        PublicRenderParameterType prType = getPublicRenderParameterForId(identifier);
        if(prType != null)
        {
            Object[] param = {identifier};
            NotifyDescriptor nd = 
                    new NotifyDescriptor.Message(NbBundle.getMessage(PortletXmlHelper.class, "MSG_IDENTIFIER_EXISTS",param), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return false;
            
        }
        
        PublicRenderParameterType publicRenderParameterType = portletApp.newPublicRenderParameterType();
        publicRenderParameterType.setIdentifier(identifier);
        publicRenderParameterType.setQname(qname);
        
      ///  if(isPublicRenderParamAlreadyExists(publicRenderParameterType, portletApp.getPublicRenderParameter()))
      ///      return true;
        
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
        
        PublicRenderParameterType prType = getPublicRenderParameterForId(identifier);
        if(prType != null)
        {
            Object[] param = {identifier};
            NotifyDescriptor nd = 
                    new NotifyDescriptor.Message(NbBundle.getMessage(PortletXmlHelper.class, "MSG_IDENTIFIER_EXISTS",param), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return false;
            
        }
        
        PublicRenderParameterType publicRenderParameterType = portletApp.newPublicRenderParameterType();
        publicRenderParameterType.setIdentifier(identifier);
        publicRenderParameterType.setName(name);
        
      ///  if(isPublicRenderParamAlreadyExists(publicRenderParameterType, portletApp.getPublicRenderParameter()))
      ///      return true;
        
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
        
        if(!isPublicRenderParameterIDIsUsedByAnyPortlet(identifier))
        {
            Object[] params = {identifier};
            NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(NbBundle.getMessage(PortletXmlHelper.class,
                    "MSG_PRP_IS_NOT_USED_WANT_TO_DELETE", params), NotifyDescriptor.YES_NO_OPTION);
            Object retVal = DialogDisplayer.getDefault().notify(nd);
            if(retVal == NotifyDescriptor.YES_OPTION)
            {
                PublicRenderParameterType paramType = getPublicRenderParameterForId(identifier);
                portletApp.removePublicRenderParameter(paramType);
            }
        }
        
        save();
        
        return true;
    }
    
    private boolean isPublicRenderParameterIDIsUsedByAnyPortlet(String identifier)
    {
        PortletApp portletApp = getPortletApp();
        PortletType[] portlets = portletApp.getPortlet();
        for(int i=0;i<portlets.length; i++)
        {
            String[] ids = portlets[i].getSupportedPublicRenderParameter();
            for(String id:ids)
            {
                if(id.equals(identifier))
                    return true;
            }
        }
        return false;
    }
    
    public List<EventObject> getSupportedPublicRenderParameters(String portletName)
    {
        
        PortletApp portletApp = getPortletApp();
        if(portletApp == null)
            return Collections.EMPTY_LIST;
        
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) return Collections.EMPTY_LIST;
        
        List<EventObject> prpObjs = new ArrayList();
        String[] sprp = portlet.getSupportedPublicRenderParameter();
        for(int i=0;i<sprp.length;i++)
        {
            PublicRenderParameterType prp = getPublicRenderParameterForId(sprp[i]);
            if(prp == null)
                continue;
            EventObject evt = new EventObject();
            evt.setPublicRenderParamId(sprp[i]);
            evt.setType(EventObject.PUBLIC_RENDER_PARAMETER_TYPE);
            if(prp.getQname() != null)
                evt.setQName(prp.getQname());
            else
                evt.setName(prp.getName());
            
            evt.setDefaultNameSpace(portletApp.getPortletDefaultNamespace());
            evt.setAlias(prp.getAlias());
            prpObjs.add(evt);
        }
        return prpObjs;
    }
    
    //Called by storyboard
    public EventObject addSupportedPublicRenderParameter(String portletName,EventObject prp)
    {
        boolean addPRP = true;
        PortletApp portletApp = getPortletApp();
        if(portletApp == null)
            return null;
        String id = prp.getPublicRenderParamId();
        PublicRenderParameterType prpType = getPublicRenderParameterForId(id);
        if(prpType == null) //prp entry needs to be added first
        {
            //Check if a render parameter with same name but different Id exists
            String existingId = getPublicRenderParamIDWithSameValue(prp);
            if(existingId != null)
            {
                    Object[] param ={existingId};
                    NotifyDescriptor nd = 
                            new NotifyDescriptor.Confirmation(NbBundle.getMessage(PortletXmlHelper.class,"MSG_PRP_WITH_SAME_VALUE_EXIST_FOR_ID",param),NotifyDescriptor.Confirmation.YES_NO_OPTION);
                    Object selectedVal = DialogDisplayer.getDefault().notify(nd);
                    if(selectedVal == NotifyDescriptor.NO_OPTION){
                        //do nothing
                    }else {
                        id = existingId;
                        addPRP = false;
                    }
            }
            
            if(addPRP){
                if(prp.isName())
                    addPublicRenderParameterAsName(id, prp.getName());
                else
                    addPublicRenderParameterAsQName(id, prp.getQName());
            }
        } else {
            //Check if the name/QName is actually same
            if(!isEqual(prpType, prp))
            {
                String existingId = getPublicRenderParamIDWithSameValue(prp);
                if(existingId != null)
                {
                    Object[] param ={existingId};
                    NotifyDescriptor nd = 
                            new NotifyDescriptor.Confirmation(NbBundle.getMessage(PortletXmlHelper.class,"MSG_PRP_WITH_SAME_VALUE_EXIST_FOR_ID",param),NotifyDescriptor.Confirmation.YES_NO_OPTION);
                    Object selectedVal = DialogDisplayer.getDefault().notify(nd);
                    if(selectedVal == NotifyDescriptor.NO_OPTION)
                        id = getAFreeIdentifier(id);
                    else {
                        id = existingId;
                        addPRP = false;
                    }
                } else {
                    id = getAFreeIdentifier(id);
                }
                
                if(addPRP)
                {
                     if(prp.isName())
                        addPublicRenderParameterAsName(id, prp.getName());
                     else
                        addPublicRenderParameterAsQName(id, prp.getQName());
                }
            }
        }
        //crate a new EventObject and return
        EventObject returnEvent = new EventObject();
        returnEvent.setQName(prp.getQName());
        returnEvent.setName(prp.getName());
        returnEvent.setPublicRenderParamId(id);
        returnEvent.setType(EventObject.PUBLIC_RENDER_PARAMETER_TYPE);
        
        addSupportedPublicRenderParameter(portletName, id);
        return returnEvent;
    }
    
    private String getAFreeIdentifier(String id)
    {
        String identifier = id;
        PublicRenderParameterType p = getPublicRenderParameterForId(identifier);
        int i = 0;
        while(p != null)
        {
            i++;
            identifier = id + i;
            p = getPublicRenderParameterForId(identifier);
        }
        return identifier;
    }
    private boolean isEqual(PublicRenderParameterType prp,EventObject evt)
    {
        if(evt.isName())
        {
            if(evt.getName().equals(prp.getName()))
                return true;
        }else {
            if(evt.getQName().equals(prp.getQname()))
                return true;
        }
        return false;
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
    
    public PublicRenderParameterType getPublicRenderParameterForId(String id)
    {
        PortletApp portletApp = getPortletApp();
        PublicRenderParameterType[] prpTypes = portletApp.getPublicRenderParameter();
        
        for(PublicRenderParameterType prp:prpTypes)
        {
            if(id.equals(prp.getIdentifier()))
                return prp;
        }
        return null;
    }
    
    private String getPublicRenderParamIDWithSameValue(EventObject evt)
    {
        String name = evt.getName();
        QName qName = evt.getQName();
        if (name == null || name.length() == 0) {
            name = null;
        }
        
        PublicRenderParameterType[] evts = getPortletApp().getPublicRenderParameter();
        for (int i = 0; i < evts.length; i++) {
         
            if (qName == null) {
                String tempName = evts[i].getName();
                if (name == null) {
                    continue;
                }
                if (name.equals(tempName)) {
                    return evts[i].getIdentifier();
                }
            } else {
                QName tempQName = evts[i].getQname();
                if (qName == null) {
                    continue;
                }
                if (qName.equals(tempQName)) {
                    return evts[i].getIdentifier();
                }
            }
        }

        return null;
    }
    private boolean isPublicRenderParamAlreadyExists(PublicRenderParameterType evt, PublicRenderParameterType[] evts) {
        String name = evt.getName();
        QName qName = evt.getQname();
        if (name == null || name.length() == 0) {
            name = null;
        }
        for (int i = 0; i < evts.length; i++) {
         
            if (qName == null) {
                String tempName = evts[i].getName();
                if (name == null) {
                    continue;
                }
                if (name.equals(tempName)) {
                    return true;
                }
            } else {
                QName tempQName = evts[i].getQname();
                if (qName == null) {
                    continue;
                }
                if (qName.equals(tempQName)) {
                    return true;
                }
            }
        }

        return false;
    }
    

    
     public PortletType getPortlet(String portletName) {
        PortletApp portletApp = getPortletApp();
        if (portletApp == null) {
            return null;
        }
        PortletType[] portlets = portletApp.getPortlet();
        if (portlets == null) {
            return null;
        }
        for (int i = 0; i < portlets.length; i++) {
            if (portlets[i].getPortletName().equals(portletName)) {
                return portlets[i];
            }
        }
        return null;
    }
     
    public PortletApp getPortletApp()
    {         
       try{
            return dbObj.getPortletApp();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting PortletApp : ",e);
            return null;
        }
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
