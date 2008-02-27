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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.common.VersionNotSupportedException;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
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
    
    public EventObject[] getPublishEvents(String portletName)
    {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) return new EventObject[0];
       EventDefinitionReferenceType[] eventDefinitionRefs = portlet.getSupportedPublishingEvent();
        if(eventDefinitionRefs == null)
            return  new EventObject[0];
        EventObject[] evtObject = new EventObject[eventDefinitionRefs.length];
        for(int i=0;i<eventDefinitionRefs.length;i++)
        {
            QName qn = eventDefinitionRefs[i].getQname();
            if(qn != null)
            {
                evtObject[i] = new EventObject();
                evtObject[i].setQName(qn);
                String valType = getEventType(qn);
                evtObject[i].setValueType(valType);
            }else
            {
                String name = eventDefinitionRefs[i].getName();
                evtObject[i] = new EventObject();
                evtObject[i].setName(name);
                String valType = getEventType(name);
                evtObject[i].setValueType(valType);
            }
        }
        return evtObject;
    }
    
     public EventObject[] getProcessEvents(String portletName)
    {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) return new EventObject[0];
        EventDefinitionReferenceType[] eventDefinitions = portlet.getSupportedProcessingEvent();
        if(eventDefinitions == null)
            return  new EventObject[0];
        EventObject[] events = new EventObject[eventDefinitions.length];
        for(int i=0;i<eventDefinitions.length;i++)
        {
            QName qn = eventDefinitions[i].getQname();
            if(qn != null)
            {
                events[i] = new EventObject();
                events[i].setQName(qn);
                String valType = getEventType(qn);
                events[i].setValueType(valType);
            }else
            {
                String name = eventDefinitions[i].getName();
                events[i] = new EventObject();
                events[i].setName(name);
                String valType = getEventType(name);
                events[i].setValueType(valType);
            }
        }
        return events;
    }
     
    public boolean addPublishEvent(String portletName, EventObject evtObject, Map properties)
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
            // QName qName = new QName(((BaseBean)portletApp).getDefaultNamespace(),evt.getLocalPart());
             if(evtObject.isQName())
                eventDefinitionType.setQname(evtObject.getQName());
             else
                eventDefinitionType.setName(evtObject.getName());
             if(evtObject.getValueType() != null)
                eventDefinitionType.setValueType(evtObject.getValueType());   
             portletApp.addEventDefinition(eventDefinitionType);
        }catch (Exception e){
            logger.log(Level.SEVERE,"Error in Adding Publish Events",e);
        }
        
        
        
        //QName[] list = getPublishEvents(portletName);
        EventDefinitionReferenceType eventDefinationRefType = portlet.newEventDefinitionReferenceType();
        if(evtObject.isQName())
            eventDefinationRefType.setQname(evtObject.getQName());
        else
            eventDefinationRefType.setName(evtObject.getName());
        portlet.addSupportedPublishingEvent(eventDefinationRefType);
    //    ((BaseBean)portlet).setDefaultNamespace(((BaseBean)portlet).getDefaultNamespace());
        //TODO portlet.setSupportedPublishingEvent(new QName[]{evt});
        
        save();
        return true;
        
    }
    
    public boolean addProcessEvent(String portletName, EventObject evtObject, Map properties)
    {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) 
        {
            logger.severe("Portlet : "+portletName + " not defined in portlet.xml !!!");
            return false;
        }
        
        //add EventDefinition if not present
        try{
             PortletApp portletApp = dbObj.getPortletApp();
             EventDefinitionType eventDefinitionType = portletApp.newEventDefinitionType();
            // QName qName = new QName(((BaseBean)portletApp).getDefaultNamespace(),evt.getLocalPart());
             if(evtObject.isQName())
                eventDefinitionType.setQname(evtObject.getQName());
             else
                eventDefinitionType.setName(evtObject.getName());
             EventDefinitionType[] evts = portletApp.getEventDefinition();
             if(!checkIfEventDefinitionAlreadyPresent(eventDefinitionType, evts))
                portletApp.addEventDefinition(eventDefinitionType);
        }catch (Exception e){
            logger.log(Level.SEVERE,"Error in Adding Publish Events",e);
        }
  
        //QName[] list = getPublishEvents(portletName);
        EventDefinitionReferenceType[] evts = portlet.getSupportedProcessingEvent();
        EventDefinitionReferenceType eventDefinationRefType = portlet.newEventDefinitionReferenceType();
        if(evtObject.isQName())
            eventDefinationRefType.setQname(evtObject.getQName());
        else
            eventDefinationRefType.setName(evtObject.getName());
        
        if(!checkIfEventAlreadyPresent(eventDefinationRefType, evts))
            portlet.addSupportedProcessingEvent(eventDefinationRefType);
    //    ((BaseBean)portlet).setDefaultNamespace(((BaseBean)portlet).getDefaultNamespace());
        //TODO portlet.setSupportedPublishingEvent(new QName[]{evt});
        
        save();
        return true;
    }
    
    public boolean deleteProcessEvent(String portletName, EventObject evtObject) {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) 
        {
            logger.severe("Portlet : "+portletName+ " not defined in portlet.xml !!!");
            return false;
        }
        
        EventDefinitionReferenceType eventDefinationRefType = portlet.newEventDefinitionReferenceType();
        if(evtObject.isQName())
            eventDefinationRefType.setQname(evtObject.getQName());
        else
            eventDefinationRefType.setName(evtObject.getName());
        
        portlet.removeSupportedProcessingEvent(eventDefinationRefType);
        save();
        return true;
       
    }
    
    public boolean deletePublishEvent(String portletName, EventObject evtObject) {
        PortletType portlet = getPortlet(portletName);
        if(portlet == null) 
        {
            logger.severe("Portlet : "+portletName+ " not defined in portlet.xml !!!");
            return false;
        }
        
        EventDefinitionReferenceType eventDefinationRefType = portlet.newEventDefinitionReferenceType();
        if(evtObject.isQName())
            eventDefinationRefType.setQname(evtObject.getQName());
        else
            eventDefinationRefType.setName(evtObject.getName());
        
        portlet.removeSupportedPublishingEvent(eventDefinationRefType);
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
    
    private String getEventType(Object event)
    {
        PortletApp portletApp = null;
        try{
            portletApp = dbObj.getPortletApp();
        }catch(IOException e){
            return null;
        }
        EventDefinitionType[] eventDefinitions = portletApp.getEventDefinition();
        if(event instanceof QName)
        {
            QName qname = (QName)event;
            for(EventDefinitionType eventDef:eventDefinitions)
            {
                if(eventDef.getQname() != null && eventDef.getQname().equals(qname))
                    return eventDef.getValueType();
            }

        } else {
            String name = (String)event;
            for(EventDefinitionType eventDef:eventDefinitions)
            {
                if(eventDef.getName() != null && eventDef.getName().equals(name))
                    return eventDef.getValueType();
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
    
    public boolean checkIfEventAlreadyPresent(EventDefinitionReferenceType evt,EventDefinitionReferenceType[] evts)
    {
        String name = evt.getName();
        QName qName = evt.getQname();
        if(name == null || name.length() == 0) name = null;
        
        for(int i=0;i<evts.length;i++)
        {
            if(qName == null)
            {
                String tempName = evts[i].getName();
                if(name == null) continue;
                if(name.equals(tempName)) return true;
            }else{
                QName tempQName = evts[i].getQname();
                if(qName == null) continue;
                if(qName.equals(tempQName)) return true;
            }
        }
        
        return false;
    }
    
    private boolean checkIfEventDefinitionAlreadyPresent(EventDefinitionType evt, EventDefinitionType[] evts)
    {
        String name = evt.getName();
        QName qName = evt.getQname();
        if(name == null || name.length() == 0) name = null;
        
        for(int i=0;i<evts.length;i++)
        {
            if(qName == null)
            {
                String tempName = evts[i].getName();
                if(name == null) continue;
                if(name.equals(tempName)) return true;
            }else{
                QName tempQName = evts[i].getQname();
                if(qName == null) continue;
                if(qName.equals(tempQName)) return true;
            }
        }
        
        return false;
    }
    public static boolean checkEventsNameForEqual(EventObject source, EventObject target) {
        
        if(source.isName() && target.isName())
        {
            if(source.getName().equals(target.getName()))
                return true;
            else
                return false;
        }

        if((source.isName() && target.isQName()) || (source.isQName() && target.isName()))
            return false;
        
        QName sourceQName = source.getQName();
        QName targetQName = target.getQName();
        
        if(sourceQName.equals(targetQName))
            return true;
        if(sourceQName.getNamespaceURI().equals(targetQName.getNamespaceURI())){
            
            
            if(!sourceQName.getLocalPart().endsWith(".") && !targetQName.getLocalPart().endsWith("."))
            {
                return false;
            }
            
            if(sourceQName.getLocalPart().endsWith(".") && targetQName.getLocalPart().endsWith(".")){
                
                if(sourceQName.getLocalPart().startsWith(targetQName.getLocalPart())
                        || targetQName.getLocalPart().startsWith(sourceQName.getLocalPart()))
                    return true;
                else
                    return false;
            } else {
                if(sourceQName.getLocalPart().endsWith("."))
                 {
                    String localPart = sourceQName.getLocalPart();
                    //localPart = localPart.substring(0,localPart.length()-1);
                    if(targetQName.getLocalPart().startsWith(localPart))
                        return true;
                    else
                        return false;
                 }
                else
                 {
                    String localPart = targetQName.getLocalPart();
                    //localPart = localPart.substring(0,localPart.length()-1);
                    if(sourceQName.getLocalPart().startsWith(localPart))
                        return true;
                    else
                        return false;
                 }    
                    
            }
            
        }
        
        return false;
          
    }
    
    public void save()
    {
        try {
            PortletApp portletApp = dbObj.getPortletApp();
            FileLock lock = dbObj.getPrimaryFile().lock();
            OutputStream out = dbObj.getPrimaryFile().getOutputStream(lock);
           //TODO ((BaseBean)portletApp).write(out);
            portletApp.write(out);
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
