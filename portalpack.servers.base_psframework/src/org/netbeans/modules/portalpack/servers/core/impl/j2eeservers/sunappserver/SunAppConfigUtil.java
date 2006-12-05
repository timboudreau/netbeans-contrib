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

import org.jdom.Attribute;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SunAppConfigUtil {
    
    private Document doc;
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    /**
     * Creates a new instance of SunAppConfigUtil
     */
    public SunAppConfigUtil(File domainDir) throws JDOMException, IOException {
        
        File xmlRoot;
        if(File.pathSeparatorChar == ':')
            xmlRoot = new File(domainDir.getAbsolutePath() + "/config/domain.xml");//NOI18N
        else
            xmlRoot = new File(domainDir.getAbsolutePath() + "\\config\\domain.xml");//NOI18N
        String port = null;
       
         
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
        Attribute attr;
        try {
            attr = (Attribute) XPath.selectSingleNode(doc, "/domain/configs/config[@name='server-config']/http-service/http-listener[@security-enabled='false'][@default-virtual-server='server']/@port");
        } catch (JDOMException ex) {
            ex.printStackTrace();
            return "";
        }
          if(attr == null)
              return "";
          port = attr.getValue();
          if(port == null)
              return "";
         
        return port.trim();
    }
    
    public String getAdminPort(){
        
        String port = "";
        if(doc == null)
              return "";
        Attribute attr;
        try {
            attr = (Attribute) XPath.selectSingleNode(doc, "/domain/configs/config[@name='server-config']/http-service/http-listener[@security-enabled='false'][@default-virtual-server='__asadmin']/@port");
        } catch (JDOMException ex) {
            ex.printStackTrace();
            return "";
        }
          if(attr == null)
              return "";
          port = attr.getValue();
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
         
        return domain.trim();
    }
    
    public static Document createDocumentFromXml(File file) throws JDOMException, IOException
    {
        
        SAXBuilder saxBuilder = new SAXBuilder(false);
        saxBuilder.setEntityResolver(new EntityResolver(){
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new StringBufferInputStream(""));
            }
            
        });
     
        org.jdom.Document jdomDocument = saxBuilder.build(file);
        return jdomDocument;
    }
    
    public static void main(String[] args) throws JDOMException, IOException
    {    
        System.out.println(new SunAppConfigUtil(new  File(args[0])).getPort());
        System.out.println(new SunAppConfigUtil(new  File(args[0])).getAdminPort());
    }
    
}

