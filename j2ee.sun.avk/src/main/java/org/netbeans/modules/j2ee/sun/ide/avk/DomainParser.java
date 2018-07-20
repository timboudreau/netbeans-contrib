/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
/*
 * DomainParser.java
 *
 * Created on September 8, 2005, 10:14 PM
 *
 */

package org.netbeans.modules.j2ee.sun.ide.avk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.w3c.dom.Element;

import org.openide.filesystems.FileUtil;

/**
 *
 * @author Nitya Doraisamy
 */
public class DomainParser {
    
    private static String CLASSPATH_PREFIX = "classpath-prefix"; //NOI18N
    private static String JVMOPTION_PREFIX = "-Dj2ee.appverification.home="; //NOI18N
    private static final String FILE_BACKUP_EXTENSION = "backup";
    private static final String FILE_BACKUP_NAME = "policybackup";
    
    /** Creates a new instance of DomainParser */
    public DomainParser() {
    }
 
    public static boolean editSupportInDomain(String domainScriptFilePath, String avkJarLoc, String avkHome, boolean onOff) {
        File domainScriptFile = new File(domainScriptFilePath);
        
        // Load domain.xml
        Document domainScriptDocument = loadDomainScriptFile(domainScriptFilePath);
        if (domainScriptDocument == null) 
            return false;
        
        NodeList javaConfigNodeList = domainScriptDocument.getElementsByTagName("java-config");
        if (javaConfigNodeList == null || javaConfigNodeList.getLength() == 0) {
            return false;
        }
        Node javaConfigNode = javaConfigNodeList.item(0);
        if (avkJarLoc != null && avkHome != null) {
            if(onOff){
                addClassPathPrefix(domainScriptDocument, javaConfigNode, avkJarLoc);
                addJVMOption(domainScriptDocument, javaConfigNode, avkHome);
            }else{
                removeClassPathPrefix(domainScriptDocument, javaConfigNode, avkJarLoc);
                removeJVMOption(domainScriptDocument, javaConfigNode, avkHome);
            }    
        }

        // Save domain.xml
        return saveDomainScriptFile(domainScriptDocument, domainScriptFilePath);
    }
    
    
    // creates Document instance from domain.xml
    private static Document loadDomainScriptFile(String domainScriptFilePath) {
        
        Document document = null;
        
        try {
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(false);
            
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            dBuilder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    StringReader reader = new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); // NOI18N
                    InputSource source = new InputSource(reader);
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    return source;
                }
            });
            
            return dBuilder.parse(new File(domainScriptFilePath));
            
        } catch (Exception e) {
            return null;
        }
        
    }
    
    // saves Document to domain.xml
    private static boolean saveDomainScriptFile(Document domainScriptDocument, String domainScriptFilePath) {
        boolean result = false;
        
        FileWriter domainScriptFileWriter = null;
        
        try {
            
            domainScriptFileWriter = new FileWriter(domainScriptFilePath);
            
            try {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, domainScriptDocument.getDoctype().getPublicId());
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, domainScriptDocument.getDoctype().getSystemId());
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                
                DOMSource domSource = new DOMSource(domainScriptDocument);
                StreamResult streamResult = new StreamResult(domainScriptFileWriter);
                
                transformer.transform(domSource, streamResult);
                result = true;
            } catch (Exception e) {
                result = false;
            }
        } catch (IOException ioex) {
            result = false;
        } finally {
            try { 
                if (domainScriptFileWriter != null) 
                    domainScriptFileWriter.close(); 
            } catch (IOException ioex2) { 
                //System.err.println("SunAS8IntegrationProvider: cannot close output stream for " + domainScriptFilePath); 
            };
        }
        
        return result;
    }
        
    private static void addClassPathPrefix(Document domainScriptDocument, Node javaConfigNode, String avkJarLoc){
        NamedNodeMap map = javaConfigNode.getAttributes();
        Node cpPrefix = map.getNamedItem(CLASSPATH_PREFIX); 
        if(cpPrefix == null){
            map = setAttribute(domainScriptDocument, CLASSPATH_PREFIX, avkJarLoc, map);
        }else{
            String prefixNodeValue = cpPrefix.getNodeValue();
            if(prefixNodeValue.indexOf(avkJarLoc) == -1){
                prefixNodeValue = removeDuplicateClassPath(avkJarLoc + File.pathSeparatorChar + prefixNodeValue);
                map = setAttribute(domainScriptDocument, CLASSPATH_PREFIX, prefixNodeValue, map);
            }
        }
    }
    
    private static void removeClassPathPrefix(Document domainScriptDocument, Node javaConfigNode, String avkJarLoc){
        NamedNodeMap map = javaConfigNode.getAttributes();
        Node cpPrefix = map.getNamedItem(CLASSPATH_PREFIX); 
        if(cpPrefix != null){
            String prefixNodeValue = cpPrefix.getNodeValue();
            if(prefixNodeValue.indexOf(avkJarLoc) != -1){
                prefixNodeValue = removeClassPath(prefixNodeValue, avkJarLoc);
                map = setAttribute(domainScriptDocument, CLASSPATH_PREFIX, prefixNodeValue, map);
            }
        }
    }
    
    private static void addJVMOption(Document domainScriptDocument, Node javaConfigNode, String avkHome){
        avkHome = JVMOPTION_PREFIX + avkHome;
        removeJVMOption(domainScriptDocument, javaConfigNode, avkHome);
        Element jvmOptionsElement = domainScriptDocument.createElement("jvm-options");
        Text avkOption = domainScriptDocument.createTextNode(avkHome);
        jvmOptionsElement.appendChild(avkOption);
        javaConfigNode.appendChild(jvmOptionsElement);
    }
    
    private static void removeJVMOption(Document domainScriptDocument, Node javaConfigNode, String avkHome){
        avkHome = JVMOPTION_PREFIX + avkHome;
        NodeList jvmOptionNodeList = domainScriptDocument.getElementsByTagName("jvm-options");
        boolean removeNode = false;
        for(int i=0; i<jvmOptionNodeList.getLength(); i++){
            Node nd = jvmOptionNodeList.item(i);
            if(nd.hasChildNodes())  {
                Node childNode = nd.getFirstChild();
                if(childNode.getNodeValue().indexOf(JVMOPTION_PREFIX) != -1)
                    removeNode = true;
            }
            if(removeNode){
                javaConfigNode.removeChild(nd);
                removeNode = false;
            }
        }
    }
    
    private static NamedNodeMap setAttribute(Document domainScriptDocument, String attrName, String attrVal, NamedNodeMap map){
        Attr cpPrefixAttr = domainScriptDocument.createAttribute(attrName);
        cpPrefixAttr.setValue(attrVal);
        map.setNamedItem(cpPrefixAttr);
        return map;
    }
    
    private static String removeClassPath(String cp, String removeValue){
        int begin = cp.lastIndexOf(removeValue);
        if(begin !=- 1){
            cp = removeDuplicateClassPath(cp.substring(0, begin) + cp.substring(begin + removeValue.length()));
        }  
        return cp;
    }
    
    private static String removeDuplicateClassPath(String cp){
        ArrayList tokens = getTokenArray(cp);            
        StringBuffer result = new StringBuffer();
        for(int j=0; j<tokens.size(); ++j){
            if(j != 0){ 
                 result.append(File.pathSeparator);
            }    
            result.append(tokens.get(j));
        }
          return result.toString();
    }
    
    private static ArrayList getTokenArray(String value){
        ArrayList tokens = new ArrayList();
        for(StringTokenizer st = new StringTokenizer(value, File.pathSeparator);st.hasMoreTokens();){
            String next = st.nextToken();
            if(!tokens.contains(next))
                tokens.add(next);
        }
        return tokens;
    }
    
    public static boolean backupFile(String filename) {
        File source = new File(filename);
        String target = source.getAbsolutePath();
        
        File parentObj = source.getParentFile();
        File targetFile = new File(parentObj.getAbsolutePath() + File.separator + source.getName() + "." + FILE_BACKUP_EXTENSION);
        
        if (!source.exists()) {
            return false;
        }
        
        if (targetFile.exists()) if (!targetFile.delete()) {
            return false;
        }
        
        try {
            FileUtil.copyFile(FileUtil.toFileObject(source), FileUtil.toFileObject(parentObj), source.getName(), FILE_BACKUP_EXTENSION);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean restoreFile(String filename) {
        File target = new File(filename);
        File source = new File(filename + "." + FILE_BACKUP_EXTENSION); //NOI18N
        
        if (!source.exists()) {
            return false;
        }
        
        if (target.exists()) if (!target.delete()) {
            return false;
        }
        
        if (!source.renameTo(target)) {
            return false;
        }
        return true;
    }
            
    
}
