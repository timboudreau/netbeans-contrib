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

import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ConfigConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.VTResourceLoader;
import org.apache.velocity.app.Velocity;

/**
 * @author Satya
 */
public class WebDescriptorGenerator {

  private Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);

  public File createPortletXml(String module,String webInfDir, HashMap values) throws Exception{
      File portletXml = new File(webInfDir + File.separator + "portlet.xml");

      if(portletXml.exists())
        return portletXml;

      FileWriter writer = new FileWriter(portletXml);
      mergeTemplate(ConfigConstants.PORTLET_XML_TEMPLATE,values, writer);
      return portletXml;

  }
  
   public File createPortletXml(String webInfDir, HashMap values) throws Exception{
      File portletXml = new File(webInfDir + File.separator + "portlet.xml");

      if(portletXml.exists())
        return portletXml;

      FileWriter writer = new FileWriter(portletXml);
      mergeTemplate(ConfigConstants.PORTLET_XML_TEMPLATE,values, writer);
      return portletXml;

  }

    private void mergeTemplate(String templateFile, HashMap values, Writer writer) throws Exception {


        VelocityContext context = VTResourceLoader.getContext(values);
             
            Template template = Velocity.getTemplate(templateFile);
            if (template == null) {
                throw new IllegalStateException(" no template defined ");
            }
        
            template.merge(context, writer);
            
        writer.close();
    }

  private void writeXmlDocument(Document doc,String filePath)
  {
      XMLOutputter outputter = new XMLOutputter();
      try {
          Writer writer = new FileWriter(new File(filePath));
          outputter.output(doc,writer);
      } catch (IOException e) {
          logger.log(Level.SEVERE,"error",e);
      }
  }


  private Element createElementFromReader(Reader reader)
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

  private Document createDocFromFile(String file)
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

    public void addNewPortletEntry(String portletXmlPath, PortletContext portletContext) {

        File f = new File(portletXmlPath);
        if(!f.exists())
        {
            try {
                createPortletXml(f.getParentFile().getAbsolutePath(), new HashMap());
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"error",ex);
            }
        }
            
        Document root = createDocFromFile(portletXmlPath);
        if(root == null)
        {
            logger.log(Level.SEVERE,"Could not add portlet entry in portel.xml");
            return;
        }

        StringWriter writer = new StringWriter();

        HashMap map = new HashMap();
        map.put("pc",portletContext);
        try {
            mergeTemplate(ConfigConstants.PORTLET_XML_PORTLET_FRAG,map,writer);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"error",e);
            return;
        }
        
        Namespace namespace = root.getRootElement().getNamespace();
        logger.log(Level.FINEST,"Root NAME SPACE IS ::::::::::::::: "+namespace.getURI());

        Element elm = createElementFromReader(new StringReader(writer.getBuffer().toString()));
        
        List clone = elm.cloneContent();
        
        
        root.getRootElement().addContent(clone);

        writeXmlDocument(root,portletXmlPath);
    }

}
