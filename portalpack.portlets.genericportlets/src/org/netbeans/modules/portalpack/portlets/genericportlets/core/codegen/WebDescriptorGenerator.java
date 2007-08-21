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

package org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.*;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.output.Format;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.FilterContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters.InitParam;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateHelper;

/**
 * @author Satya
 */
public class WebDescriptorGenerator {
   
  private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

  public File createPortletXml(String module,String webInfDir,PortletContext context, HashMap values) throws Exception{
      File portletXml = new File(webInfDir + File.separator + "portlet.xml");

      if(portletXml.exists())
        return portletXml;

      FileOutputStream outputStream = new FileOutputStream(portletXml);
      OutputStreamWriter writer = new OutputStreamWriter(outputStream,"UTF-8");
       if(context.getPortletVersion().equals(NetbeanConstants.PORTLET_2_0))
         mergeTemplate(ConfigConstants.PORTLET20_XML_TEMPLATE,values, writer); 
      else
         mergeTemplate(ConfigConstants.PORTLET_XML_TEMPLATE,values, writer);
      return portletXml;

  }
  
   public File createPortletXml(String webInfDir, PortletContext context, HashMap values) throws Exception{
      File portletXml = new File(webInfDir + File.separator + "portlet.xml");

      if(portletXml.exists())
        return portletXml;

      FileOutputStream outputStream = new FileOutputStream(portletXml);
      OutputStreamWriter writer = new OutputStreamWriter(outputStream,"UTF-8");
      if(context.getPortletVersion().equals(NetbeanConstants.PORTLET_2_0))
         mergeTemplate(ConfigConstants.PORTLET20_XML_TEMPLATE,values, writer); 
      else
         mergeTemplate(ConfigConstants.PORTLET_XML_TEMPLATE,values, writer);
      return portletXml;

  }

    private void mergeTemplate(String templateFile, HashMap values, Writer writer) throws Exception {

        FileObject template = TemplateHelper.getTemplateFile(templateFile);
        if(template == null)
            throw new IOException("Template File is null : "+templateFile);
        TemplateHelper.mergeTemplateToWriter(template, writer, values);
    }
    
       
      
  public void writeXmlDocument(Document doc,String filePath)
  {
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      try {
          OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)),"UTF-8");
          outputter.output(doc,writer);
      } catch (IOException e) {
          logger.log(Level.SEVERE,"error",e);
      }
  }
  
  public void writeXmlElement(Element elm,String filePath)
  {
      XMLOutputter outputter = new XMLOutputter();
      try {
          OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)),"UTF-8");
          outputter.output(elm,writer);
      } catch (IOException e) {
          logger.log(Level.SEVERE,"error",e);
      }
  }


  private static Element createElementFromReader(Reader reader)
  {

      SAXBuilder builder = new SAXBuilder();
      try {
          return builder.build(reader).getRootElement();
      } catch (JDOMException e) {
          logger.log(Level.SEVERE,"error",e);
      } catch (IOException e) {
          logger.log(Level.SEVERE,"error",e);
      }
      return null;
  }

  public static Document createDocFromFile(String file)
  {

      SAXBuilder builder = new SAXBuilder();
      try {
          return builder.build(new File(file));
      } catch (JDOMException e) {
          logger.log(Level.SEVERE,"error",e);
      } catch (IOException e) {
          logger.log(Level.SEVERE,"error",e);
      }
      return null;
  }

  public static String getPortletAppVersion(String portletXmlPath)
  {
       File f = new File(portletXmlPath);
       if(!f.exists()) return null;
       Document root = createDocFromFile(portletXmlPath);
        if(root == null)
        {
            logger.log(Level.SEVERE,"Could not add portlet entry in portel.xml");
            return null;
        }
        Namespace ns = root.getRootElement().getNamespace();
        if(ns.equals(Namespace.getNamespace(NetbeanConstants.PORTLET_2_0_NS)))
            return NetbeanConstants.PORTLET_2_0;
        else
            return NetbeanConstants.PORTLET_1_0;
  }
    public Element addNewPortletEntry(String portletXmlPath, PortletContext portletContext) {

        File f = new File(portletXmlPath);
        if(!f.exists())
        {
            try {
                createPortletXml(f.getParentFile().getAbsolutePath(), portletContext,new HashMap());
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"error",ex);
            }
        }
            
        Document root = createDocFromFile(portletXmlPath);
        if(root == null)
        {
            logger.log(Level.SEVERE,"Could not add portlet entry in portel.xml");
            return null;
        }

        Namespace ns = root.getRootElement().getNamespace();
        if(ns.equals(Namespace.getNamespace(NetbeanConstants.PORTLET_2_0_NS)))
            portletContext.setPortletVersion(NetbeanConstants.PORTLET_2_0);
        StringWriter writer = new StringWriter();

        HashMap map = new HashMap();
        map.put("pc",portletContext);
        try {
            if(portletContext.getPortletVersion().equals(NetbeanConstants.PORTLET_2_0))
                mergeTemplate(ConfigConstants.PORTLET20_XML_PORTLET_FRAG,map,writer);
            else
                mergeTemplate(ConfigConstants.PORTLET_XML_PORTLET_FRAG,map,writer);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"error",e);
            return null;
        }
        
        Namespace namespace = root.getRootElement().getNamespace();
        logger.log(Level.FINEST,"Root NAME SPACE IS ::::::::::::::: "+namespace.getURI());

        Element elm = createElementFromReader(new StringReader(writer.getBuffer().toString()));
        
      //  elm.removeNamespaceDeclaration(elm.getNamespace());//setNamespace(root.getRootElement().getNamespace());
        List clone = elm.cloneContent();
        
        
        root.getRootElement().addContent(clone);

        writeXmlDocument(root,portletXmlPath);
        return elm;
    }
    
     /**
      * 
      * @param portletXml 
      * @return 
      */
     public static List getPortlets(File portletXml) {

        
        if(!portletXml.exists())
        {
           
           logger.severe("Portlet XML Not Found");
           return Collections.EMPTY_LIST;
           
        }
            
        Document root = createDocFromFile(portletXml.getAbsolutePath());
        if(root == null)
        {
            logger.log(Level.SEVERE,"Could not add portlet entry in portel.xml");
            return Collections.EMPTY_LIST;
        }
        
        Element rootElm = root.getRootElement();
       
        return getPortlets(rootElm);

       
    } 
     
    public static List getPortlets(Element rootElm)
    {
         Namespace namespace = rootElm.getNamespace();
        
        List portlets = rootElm.getChildren("portlet",namespace);
        List list = new ArrayList();
        for(int i=0;i<portlets.size();i++)
        {
            Element portletName = ((Element)portlets.get(i)).getChild("portlet-name",namespace);
            if(portletName !=  null)
            {
                String name = portletName.getTextTrim();
                if(name != null)
                    list.add(name);
            }
        }
        return list;
    }
     

     public Element addNewFilter(String portletXmlPath, FilterContext filterContext) {

        File f = new File(portletXmlPath);
        if(!f.exists())
        {
           
           logger.severe("Portlet XML Not Found");
           return null;
           
        }
            
        Document root = createDocFromFile(portletXmlPath);
        if(root == null)
        {
            logger.log(Level.SEVERE,"Could not add portlet entry in portel.xml");
            return null;
        }

        Element filterElm = new Element("filter",root.getRootElement().getNamespace());//,Namespace.getNamespace("http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd"));
        Element filterName = new Element("filter-name",filterElm.getNamespace());
        filterName.setText(filterContext.getFilterName());
        filterElm.addContent(filterName);
        
        Element filterClass = new Element("filter-class",filterElm.getNamespace());
        filterClass.setText(filterContext.getFilterClassName());
        filterElm.addContent(filterClass);
        
        String[] lifeCyclePhases = filterContext.getLifeCyclePhase();
        for(int i =0;i<lifeCyclePhases.length;i++)
        {
             Element lifeCycleElm = new Element("lifecycle",filterElm.getNamespace());
             lifeCycleElm.setText(lifeCyclePhases[i]);
             filterElm.addContent(lifeCycleElm);
        }
        
        InitParam[] initParams = filterContext.getInitParams();
        for(int i=0;i<initParams.length;i++)
        {
            Element initParam = new Element("init-param",filterElm.getNamespace());
            Element name = new Element("name",filterElm.getNamespace());
            Element value = new Element("value",filterElm.getNamespace());
            String paramName = initParams[i].getName();
            String paramValue = initParams[i].getValue();
            if(paramName == null || paramName.length() == 0)
                continue;
            name.setText(paramName);
            value.setText(paramValue);
            initParam.addContent(name);
            initParam.addContent(value);
            filterElm.addContent(initParam);
        }
        Element elm =(Element) filterElm.clone();
        
      //  List clone = filterElm.cloneContent();
        root.getRootElement().addContent(elm);
        addFilterMappings(root.getRootElement(), filterContext);
        writeXmlDocument(root,portletXmlPath);
        return filterElm;
    }
     
    public void addFilterMappings(Element rootElm,FilterContext fc)
    {
        List mappingList = new ArrayList();
        FilterMappingData[] mappings = fc.getFilterMappingData();
        for(int i=0;i<mappings.length;i++)
        {
            FilterMappingData mapping = mappings[i];
            if(mappingList.contains(mapping.getName()+"::"+mapping.getPortlet()))
                    continue; //no need to add again
            if(mapping.getName() == null || mapping.getName().length()==0 || mapping.getPortlet() == null
                    || mapping.getPortlet().length() ==0)
                continue;
            Element filterElm = new Element("filter-mapping",rootElm.getNamespace());
          
            Element filterName = new Element("filter-name",rootElm.getNamespace());
            filterName.setText(mapping.getName());
            filterElm.addContent(filterName);
            
            Element portletName = new Element("portlet-name",rootElm.getNamespace());
            portletName.setText(mapping.getPortlet());
            filterElm.addContent(portletName);
            mappingList.add(mapping.getName()+"::"+mapping.getPortlet());
            rootElm.addContent(filterElm);
        }
    }
     
    public static List getFilters(File portletXml) {

        
        if(!portletXml.exists())
        {
           
           logger.severe("Portlet XML Not Found");
           return Collections.EMPTY_LIST;
           
        }
            
        Document root = createDocFromFile(portletXml.getAbsolutePath());
        if(root == null)
        {
            logger.log(Level.SEVERE,"Could not add portlet entry in portel.xml");
            return Collections.EMPTY_LIST;
        }
        
       Element rootElm = root.getRootElement();
       return getFilters(rootElm);
       
    }  
    
    public static List getFilters(Element rootElm)
    {
        
        Namespace namespace = rootElm.getNamespace();
        
        List filters = rootElm.getChildren("filter",namespace);
        List list = new ArrayList();
        for(int i=0;i<filters.size();i++)
        {
            Element filterName = ((Element)filters.get(i)).getChild("filter-name",namespace);
            if(filterName !=  null)
            {
                String name = filterName.getTextTrim();
                if(name != null)
                    list.add(name);
            }
        }
        return list;
    }
}
