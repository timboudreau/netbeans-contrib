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
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */


package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.impl.sun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;

/**
 *
 * @author Satyaranjan
 */
public class SunPortletXmlHandler {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private Document sunPortletXml;
    private File file;
    private long lastModifiedTime;
    private boolean parsingError = false;
    /** Creates a new instance of SunPortletXmlHandler */
    public SunPortletXmlHandler(File sunportletxml) {
        this.file = new File(sunportletxml.getAbsolutePath());
        sunPortletXml = createDocFromFile(sunportletxml);
    }
    
    public Document createDocFromFile(File file)
   {
      SAXBuilder builder = new SAXBuilder();
      try {
          lastModifiedTime = file.lastModified();
          Document doc = builder.build(file);
          parsingError = false;
          return doc;
      } catch (JDOMException e) {
          e.printStackTrace();
          parsingError = true;
      } catch (IOException e) {
         e.printStackTrace();
         parsingError = false;   
      }
      return null;
  }
    
    public Document getDocument()
    {
        return sunPortletXml;
    }
    
    private void reloadRequired() throws JDOMException
    {
        if(file.lastModified() > lastModifiedTime)
        {
            reload();
            if(parsingError) throw new JDOMException("Parsing Error");
        }else if(parsingError)
            throw new JDOMException("Parsing Error");
        
    }
    public void reload()
    {
        logger.fine("Reload Required ..........So reloading again..............");
       /// File sunFile = new File(file);
        sunPortletXml = createDocFromFile(file);
    }
    
    public boolean notExistsSunPortletXml()
    {
        if(!parsingError && sunPortletXml == null)
        {
            if(file.exists()){
                logger.fine("If file exists............later.....");
                return false;
            }
            return true;
        }
        return false;
    }
   // public String[] getPortlets(){
    //    
    //}
    public String[] getEvents(String portlet)
    {
        if(notExistsSunPortletXml()) return new String[]{};
        try{
            reloadRequired();
        }catch(JDOMException e){
            return new String[]{};
        }
        List portlets = sunPortletXml.getRootElement().getChildren("portlet",sunPortletXml.getRootElement().getNamespace());
        
        for(int i=0;i<portlets.size();i++)
        {
            try{
            Element pElm = ((Element)portlets.get(i));
            Element pNameElm = pElm.getChild("portlet-name",sunPortletXml.getRootElement().getNamespace());
            if(pNameElm.getText().equals(portlet))
            {
                Element events =pElm.getChild("events",sunPortletXml.getRootElement().getNamespace());
                if(events == null)
                    continue;
                List genEvents = events.getChildren("generates-event",sunPortletXml.getRootElement().getNamespace());
                List finalEvents = new ArrayList();
                for(int k=0;k<genEvents.size();k++)
                {
                    Element genEvent = (Element)genEvents.get(k);
                    finalEvents.add(genEvent.getText());
                }
                return (String [])finalEvents.toArray(new String[0]);
            }
            }catch(Exception e){
                
            }
        }
        return new String[]{};
    }
    
     public String[] getConsumeEvents(String portlet)
    {
        if(notExistsSunPortletXml()) return new String[]{};
        try{
            reloadRequired();
        }catch(JDOMException e){
            return new String[]{};
        }
        List portlets = sunPortletXml.getRootElement().getChildren("portlet",sunPortletXml.getRootElement().getNamespace());
        
        for(int i=0;i<portlets.size();i++)
        {
            try{
            Element pElm = ((Element)portlets.get(i));
            Element pNameElm = pElm.getChild("portlet-name",sunPortletXml.getRootElement().getNamespace());
            if(pNameElm.getText().equals(portlet))
            {
                Element events =pElm.getChild("events",sunPortletXml.getRootElement().getNamespace());
                if(events == null)
                    continue;
                List genEvents = events.getChildren("consumes-event",sunPortletXml.getRootElement().getNamespace());
                List finalEvents = new ArrayList();
                for(int k=0;k<genEvents.size();k++)
                {
                    Element genEvent = (Element)genEvents.get(k);
                    finalEvents.add(genEvent.getText());
                }
                return (String [])finalEvents.toArray(new String[0]);
            }
            }catch(Exception e){
                
            }
        }
        return new String[]{};
    }
    public boolean addConsumeEvent(String portlet,String eventName){
          if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
         Element portletElm = getPortlet(portlet);
         if(isConsumeEventExists(portletElm, eventName))
             return true;
         if(portletElm == null)
             portletElm = createPortletElement(sunPortletXml.getRootElement(), portlet);
         if(portletElm != null)
         {
             Element eventElm = portletElm.getChild("events",portletElm.getNamespace());
             if(eventElm == null)
             {
                 eventElm = createEventElement(portletElm);
             }
             addConsumeEventElement(eventElm, eventName);
             writeXmlDocument(sunPortletXml);
             return true;
             
         }
         return false;
    }
    
    public boolean addGeneratesEvent(String portlet,String eventName)
    {
         if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
         Element portletElm = getPortlet(portlet);
         if(isGeneratesEventExists(portletElm, eventName))
             return true;
         if(portletElm == null)
             portletElm = createPortletElement(sunPortletXml.getRootElement(), portlet);
         if(portletElm != null)
         {
             Element eventElm = portletElm.getChild("events",portletElm.getNamespace());
             if(eventElm == null)
             {
                 eventElm = createEventElement(portletElm);
             }
             addGeneratesEventElement(eventElm, eventName);
             writeXmlDocument(sunPortletXml);
             return true;
             
         }
         return false;
    }
    
    private Element createPortletElement(Element root,String portletName)
    {
        Element portletElm = new Element("portlet",root.getNamespace());
        Element nameElm = new Element("portlet-name",root.getNamespace());
        nameElm.setText(portletName);
        portletElm.addContent(nameElm);
        root.addContent(portletElm);
        return portletElm;
    }
    
    private Element createEventElement(Element portletElement)
    {
        Element eventElement = new Element("events",portletElement.getNamespace());
        portletElement.addContent(eventElement);
        return eventElement;
    }
    
    private Element addConsumeEventElement(Element evtElm,String evtName)
    {
        
        Element consumeEvtElm = new Element("consumes-event",evtElm.getNamespace());
        consumeEvtElm.setText(evtName);
        evtElm.addContent(consumeEvtElm);
        return evtElm;
    }
    
    private Element addGeneratesEventElement(Element evtElm,String evtName)
    {
        
        Element consumeEvtElm = new Element("generates-event",evtElm.getNamespace());
        consumeEvtElm.setText(evtName);
        evtElm.addContent(consumeEvtElm);
        return evtElm;
    }
    public boolean isConsumesEventExists(String portlet,String eventName)
    {
           if(notExistsSunPortletXml()) return false;
        try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
         Element portletElm = getPortlet(portlet);
         if(isConsumeEventExists(portletElm, eventName))
             return true;
         return false;
    }
    
     public boolean isGeneratesEventExists(String portlet,String eventName)
    {
          if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
         Element portletElm = getPortlet(portlet);
         if(isGeneratesEventExists(portletElm, eventName))
             return true;
         return false;
    }
    private boolean isConsumeEventExists(Element portletElm,String eventName)
    {
         if(portletElm != null)
         {
             Element eventElm = portletElm.getChild("events",portletElm.getNamespace());
             if(eventElm == null)
             {
                return false;
             }else{
                 List evts = eventElm.getChildren("consumes-event", portletElm.getNamespace());
                 for(int i=0;i<evts.size();i++)
                 {
                     if(((Element)evts.get(i)).getText().equals(eventName))
                             return true;
                 }
                 return false;
             }
             
         }
         else
             return false;
    }
    
    public boolean renameConsumesEvent(String portlet,String oldEvent,String newEvent)
    {
         if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
        Element portletElm = getPortlet(portlet);
        deleteConsumeEvent(portletElm, oldEvent);
        return addConsumeEvent(portlet, newEvent);
    }
    
    public boolean renameGeneratesEvent(String portlet,String oldEvent,String newEvent)
    {
         if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
        Element portletElm = getPortlet(portlet);
        deleteGeneratesEvent(portletElm, oldEvent);
        return addGeneratesEvent(portlet, newEvent);
    }
    private boolean isGeneratesEventExists(Element portletElm,String eventName)
    {
         if(portletElm != null)
         {
             Element eventElm = portletElm.getChild("events",portletElm.getNamespace());
             if(eventElm == null)
             {
                return false;
             }else{
                 List evts = eventElm.getChildren("generates-event", portletElm.getNamespace());
                 for(int i=0;i<evts.size();i++)
                 {
                     if(((Element)evts.get(i)).getText().equals(eventName))
                             return true;
                 }
                 return false;
             }
             
         }
         else
             return false;
    }
    
     private boolean deleteConsumeEvent(Element portletElm,String eventName)
    {
         if(portletElm != null)
         {
             Element eventElm = portletElm.getChild("events",portletElm.getNamespace());
             if(eventElm == null)
             {
                return false;
             }else{
                 List evts = eventElm.getChildren("consumes-event", portletElm.getNamespace());
                 for(int i=0;i<evts.size();i++)
                 {
                     if(((Element)evts.get(i)).getText().equals(eventName))
                     {
                         eventElm.removeContent((Element)evts.get(i));
                         return true;
                     }
                 }
                 return false;
             }
             
         }
         else
             return false;
    }
     
    private boolean deleteGeneratesEvent(Element portletElm,String eventName)
    {
         if(portletElm != null)
         {
             Element eventElm = portletElm.getChild("events",portletElm.getNamespace());
             if(eventElm == null)
             {
                return false;
             }else{
                 List evts = eventElm.getChildren("generates-event", portletElm.getNamespace());
                 for(int i=0;i<evts.size();i++)
                 {
                     if(((Element)evts.get(i)).getText().equals(eventName))
                     {
                         eventElm.removeContent((Element)evts.get(i));
                         return true;
                     }
                 }
                 return false;
             }
             
         }
         else
             return false;
    } 
    
    private Element getPortlet(String portlet)
    {
         if(notExistsSunPortletXml()) return null;
        List portlets = sunPortletXml.getRootElement().getChildren("portlet",sunPortletXml.getRootElement().getNamespace());
        
        for(int i=0;i<portlets.size();i++)
        {
            try{
            Element pElm = ((Element)portlets.get(i));
            Element pNameElm = pElm.getChild("portlet-name",sunPortletXml.getRootElement().getNamespace());
            if(pNameElm.getText().equals(portlet))
            {
               return pElm;
            }
            }catch(Exception e){
                
            }
        }
        return null;
    }
    
    public boolean deleteConsumeEvent(String portlet,String evtName)
    {
         if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
        Element portletElm = getPortlet(portlet);
        if(portletElm == null) return true;
        if(deleteConsumeEvent(portletElm, evtName))
        {
            writeXmlDocument(sunPortletXml);
            return true;
        }
        return false;
    }
    
    public boolean deleteGeneratesEvent(String portlet,String evtName)
    {
         if(notExistsSunPortletXml()) return false;
         try{
            reloadRequired();
        }catch(JDOMException e){
            return false;
        }
        Element portletElm = getPortlet(portlet);
        if(portletElm == null) return true;
        if(deleteGeneratesEvent(portletElm, evtName))
        {
            writeXmlDocument(sunPortletXml);
            return true;
        }
        return false;
    }
     public void writeXmlDocument(Document doc)
    {
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      try {
          OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
          outputter.output(doc,writer);
          //writer.flush();
          writer.close();
          lastModifiedTime = file.lastModified();
      } catch (IOException e) {
          //logger.log(Level.SEVERE,"error",e);
          e.printStackTrace();
      }
  }
}
