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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Satya
 */
public class TomcatConfigUtil {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER); 
    private static final String ATTR_URI_ENCODING = "URIEncoding"; // NOI18N
    private static final String ATTR_PORT = "port"; // NOI18N
    private static final String ATTR_PROTOCOL = "protocol"; // NOI18N
    private static final String ATTR_AUTO_DEPLOY = "autoDeploy";    // NOI18N
    private static final String ATTR_SCHEME = "scheme";             // NOI18N
    private static final String ATTR_SECURE = "secure";             // NOI18N
    
    private static final String PROP_CONNECTOR = "Connector"; // NOI18N
    
    private static final String HTTP    = "http";   // NOI18N
    private static final String HTTPS   = "https";  // NOI18N
    private static final String TRUE    = "true";   // NOI18N
    
    private Document doc;
    
    /** Creates a new instance of TomcatConfigUtil */
    public TomcatConfigUtil(File catalinaBase) throws SAXException,IOException {
        
        File xmlRoot;
        if(File.pathSeparatorChar == ':')
            xmlRoot = new File(catalinaBase.getAbsolutePath() + "/conf/server.xml");//NOI18N
        else
            xmlRoot = new File(catalinaBase.getAbsolutePath() + "\\conf\\server.xml");//NOI18N
       
          logger.log(Level.FINEST,"XmlRoot is :: "+xmlRoot);
          InputSource inputSource = 
              new InputSource(new FileInputStream(xmlRoot));
          
          doc = createDocumentFromXml(xmlRoot);
    }
    
      public String getDomainName()
    {
       /* commented as this is not currently being used   
        String domain = "";
        if(doc == null)
              return "";
        Attribute attr;
        try {
            attr = (Attribute) XPath.selectSingleNode(doc, "/domain/property[@name='administrative.domain.name']/@value");
        } catch (JDOMException ex) {
            ex.printStackTrace();
            return "";
        }
          if(attr == null)
              return "";
          domain = attr.getValue();
          if(domain == null)
              return "";
         // System.out.println("elm"+attr.getValue());
     
        return domain.trim();
        */
        return ""; //dummy return
    }
    
    public static Document createDocumentFromXml(File file) throws IOException
    {
        /* commented as not being used
        SAXBuilder saxBuilder = new SAXBuilder(false);
        saxBuilder.setEntityResolver(new EntityResolver(){
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new StringBufferInputStream(""));
            }
            
        });
     
        org.jdom.Document jdomDocument = saxBuilder.build(file);
        return jdomDocument;*/
        return null; //dummy return
    }
    
    
     public  String getShutdownPort() {
         /* commented as not being used currently
        String port;
        
        if(doc == null)
              return "";
        Attribute attr;
        try {
            attr = (Attribute) XPath.selectSingleNode(doc, "/Server/@port");
        } catch (JDOMException ex) {
            ex.printStackTrace();
            return "";
        }
          if(attr == null)
              return "";
          port = attr.getValue();
        
        return port;*/
         return ""; //dummy return
              
    }
     
    public String getHttpPort(){
      /* commented as not being used currently
        String port = "";
        int defCon = -1;
        if(doc == null) return "";
        Element service = doc.getRootElement().getChild("Service");
        
        if(service == null) return "";

        List list = service.getChildren("Connector");
        for (int i=0; i<list.size(); i++) {
            Element connector = (Element)list.get(i);
            String protocol = connector.getAttributeValue(ATTR_PROTOCOL);
            String scheme = connector.getAttributeValue(ATTR_SCHEME);
            String secure = connector.getAttributeValue(ATTR_SECURE);
            if (isHttpConnector(protocol, scheme, secure)) {
                defCon = i;
                break;
            }
        }
        
        if (defCon==-1 && list.size() > 0) {
            defCon=0;
        }
        
        port = ((Element)list.get(defCon)).getAttributeValue(ATTR_PORT);
      
        if(port == null) return "0";
        return port; */
       
        return ""; //dummy return
    }
    
    
    
    private static boolean isHttpConnector(String protocol, String scheme, String secure) {
        return (protocol == null || protocol.length() == 0 || protocol.toLowerCase().equals(HTTP))
                && (scheme == null || !scheme.toLowerCase().equals(HTTPS))
                && (secure == null || !secure.toLowerCase().equals(TRUE));
    }
    
     
    public static void main(String[] args) throws Exception
    {
        TomcatConfigUtil configUtil = new TomcatConfigUtil(new File(args[0]));
        System.out.println("Shutdown: "+configUtil.getShutdownPort());
        System.out.println("Port: "+configUtil.getHttpPort());
    }
}
