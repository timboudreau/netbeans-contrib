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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class SunAppConfigUtil {
    
    private Document doc;
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    static final String DEBUG_OPTIONS_ADDRESS = "address="; //NOI18N
    /**
     * Creates a new instance of SunAppConfigUtil
     */
    public SunAppConfigUtil(File domainDir) throws IOException, SAXException, ParserConfigurationException,ReadAccessDeniedException {
        
        File xmlRoot;
        if(File.pathSeparatorChar == ':')
            xmlRoot = new File(domainDir.getAbsolutePath() + "/config/domain.xml");//NOI18N
        else
            xmlRoot = new File(domainDir.getAbsolutePath() + "\\config\\domain.xml");//NOI18N
        String port = null;
       
         if(xmlRoot.exists())
         {
            if(!xmlRoot.canRead())
            {
                throw new ReadAccessDeniedException();
            }
         }
          logger.log(Level.FINEST,"XmlRoot is :: "+xmlRoot);
          InputSource inputSource = 
              new InputSource(new FileInputStream(xmlRoot));
          
          doc = createDocumentFromXml(xmlRoot);   
          
    }
      
    static File[] getRegisterableDefaultDomains(File location) {
        File[] noneRegisterable = new File[0];
        File domainsDir = new File(location,"domains");
        if (!domainsDir.exists() && location.getAbsolutePath().startsWith("/opt/SUNWappserver")) {
            domainsDir = new File("/var/opt/SUNWappserver/domains");
        }
        if (!domainsDir.exists())
            return noneRegisterable;
        
        File[] possibles = domainsDir.listFiles(new java.io.FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory() && pathname.canWrite())
                    return true;
                return false;
            }
        });
        if (null == possibles)
            return noneRegisterable;

        // prune out unusable entries...
        int realCount = 0;
        for (int i = 0; i < possibles.length; i++) {
            if (rootOfUsableDomain(possibles[i])) {
                realCount++;
            } else {
                possibles[i] = null;
            }
        }
        File[] retVal = new File[realCount];
        int nextSlot = 0;
        for (int i = 0; i < possibles.length; i++) {
            if (possibles[i] != null) {
                retVal[nextSlot] = possibles[i];
                nextSlot++;
            }
        }
        return retVal;
    }
    
    /** 
     */
    public static boolean rootOfUsableDomain(File f) {
        File testFile = new File(f,"logs");
        if (!testFile.exists() || !testFile.isDirectory() || !testFile.canWrite())
            return false;
        testFile = new File(f,"config");
        if (!testFile.exists() || !testFile.isDirectory() || !testFile.canWrite())
            return false;
        testFile = new File(testFile,"domain.xml");
        if (!testFile.exists() || !testFile.canWrite())
            return false;
        return true;
    }
    
    public String getPort(){
        
        String port = "";
        if(doc == null)
              return "";
        try {
            
            port = getXPath().evaluate("/domain/configs/config[@name='server-config']/http-service/http-listener[@security-enabled='false'][@default-virtual-server='server']/@port",doc);
        } catch (XPathExpressionException ex) {
            logger.log(Level.SEVERE,"ParseError",ex);
                 
        }
        
        //For GV3
        if(port == null || port.length() == 0) {
            
            try {
                port = getXPath().evaluate("/domain/configs/config[@name='server-config']/http-service/http-listener[@default-virtual-server='server']/@port", doc);
            } catch (XPathExpressionException ex) {
                logger.log(Level.SEVERE,"ParseError",ex);
            }
        }
     
        if(port == null)
              return "";
         
        return port.trim();
    }
    
    public String getAdminPort(){
        
        String port = "";
        if(doc == null)
              return "";
        
         try {
            port = getXPath().evaluate("/domain/configs/config[@name='server-config']/http-service/http-listener[@security-enabled='false'][@default-virtual-server='__asadmin']/@port",doc);
         } catch (XPathExpressionException ex) {
            logger.log(Level.SEVERE,"ParseError",ex);
        }
        
        //Check for V3
        if(port == null || port.length() == 0) {
            try {

                port = getXPath().evaluate("/domain/configs/config[@name='server-config']/http-service/http-listener[@default-virtual-server='__asadmin']/@port", doc);
            } catch (XPathExpressionException ex) {
                logger.log(Level.SEVERE,"ParseError",ex);
            }
        }
        
         if(port == null)
              return "";
         return port.trim();
    }
    
    public static File domainFile(File domainDir) {
        if (File.pathSeparatorChar == ';')
            return new File(domainDir+"\\config\\domain.xml");
        else
            return new File(domainDir+"/config/domain.xml");
    }
    
    public String getDomainName()
    {
        String domain = "";
        if(doc == null)
              return "";
        try {        
            domain = getXPath().evaluate("/domain/property[@name='administrative.domain.name']/@value",doc);
        } catch (XPathExpressionException ex) {
            logger.log(Level.SEVERE,"ParseError",ex);
        }
       
         if(domain == null)
              return "";
         
        return domain.trim();
    }
    
    public String getDebugAddress()
    {
        String debugOptionsVal = null;
        if(doc == null)
            return "0";
        try{
            debugOptionsVal = getXPath().evaluate("/domain/configs/config[@name='server-config']/java-config/@debug-options",doc);
        }catch(XPathExpressionException ex){
            logger.log(Level.SEVERE,"ParseError",ex);
            debugOptionsVal = null;
        }
        if(debugOptionsVal != null){
            debugOptionsVal = debugOptionsVal.substring(debugOptionsVal.indexOf(DEBUG_OPTIONS_ADDRESS)+DEBUG_OPTIONS_ADDRESS.length(), debugOptionsVal.length());
            int hasMore = debugOptionsVal.indexOf(","); //NOI18N
            if(hasMore != -1){ 
                debugOptionsVal = debugOptionsVal.substring(0, hasMore);
            }
        }
        else
            return "0";
        
        return debugOptionsVal;
    }
    private XPath getXPath()
    {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        return xpath;
    }
    public static Document createDocumentFromXml(File file) throws SAXException, IOException, ParserConfigurationException
    {
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        builder.setEntityResolver( new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException,IOException {
                return new InputSource(new StringBufferInputStream(""));
            }
        });
            
        Document document = builder.parse(file);
        return document;
    }
    
    
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException
    {    
       // System.out.println(new SunAppConfigUtil(new  File(args[0])).getPort());
       // System.out.println(new SunAppConfigUtil(new  File(args[0])).getAdminPort());
    }
    
    class ReadAccessDeniedException extends Exception
    {
        
    }
    
}

