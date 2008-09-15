/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.portlets.spring.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author satyaranjan
 */
public class BeanXMLUtil {

    public static String VIEW_RESOLVER = "viewResolver";
    public static String VIEW_RESOLVER_CLASS = "org.springframework.web.servlet.view.InternalResourceViewResolver";
    
    public static String PORTLET_MULTIPART_RESOLVER = "portletMultipartResolver";
    public static String PORTLET_MULTIPART_RESOLVER_CLASS = "org.springframework.web.portlet.multipart.CommonsPortletMultipartResolver";
   
    private Document doc;
    FileObject fileObject;
    

    public BeanXMLUtil(FileObject fileObject) {
        
        this.fileObject = fileObject;
        File docFile = FileUtil.toFile(fileObject);
        doc = createDocument(docFile);
        
    }
    
    private Document createDocument(File file) {
        
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            return docBuilder.parse(file);
        } catch (SAXException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return null;
    }
    
    public void addViewResolverBean() {
        
        if(doc == null) return;
        
        NodeList beanNL = doc.getElementsByTagName("bean");
        
        for(int i=0;i<beanNL.getLength();i++) {
            
            Element beanElm = (Element)beanNL.item(i);
            String id = beanElm.getAttribute("id");
            String className = beanElm.getAttribute("class");
            
            if(id == null || className == null)
                continue;
            if(id.equals(VIEW_RESOLVER) && className.equals(VIEW_RESOLVER_CLASS))
                return;
        }
        
        Element beanElm = doc.createElement("bean");
        beanElm.setAttribute("id", VIEW_RESOLVER);
        beanElm.setAttribute("class", VIEW_RESOLVER_CLASS);
        beanElm.setAttribute("p:prefix", "/WEB-INF/jsp/");
        beanElm.setAttribute("p:suffix", ".jsp");
        
        doc.getDocumentElement().appendChild(beanElm);
    }
    
    public void addMultipartResolverBean() {
        if(doc == null) return;
        
        NodeList beanNL = doc.getElementsByTagName("bean");
        
        for(int i=0;i<beanNL.getLength();i++) {
            
            Element beanElm = (Element)beanNL.item(i);
            String id = beanElm.getAttribute("id");
            String className = beanElm.getAttribute("class");
            
            if(id == null || className == null)
                continue;
            if(id.equals(PORTLET_MULTIPART_RESOLVER) && className.equals(PORTLET_MULTIPART_RESOLVER_CLASS))
                return;
        }
        
        Element beanElm = doc.createElement("bean");
        beanElm.setAttribute("id", PORTLET_MULTIPART_RESOLVER);
        beanElm.setAttribute("class", PORTLET_MULTIPART_RESOLVER_CLASS);
        
        doc.getDocumentElement().appendChild(beanElm);
    }
    
    public boolean addProperty(String beanID,Properties properties) {
        
        if(doc == null) return false;
        
        NodeList beanNL = doc.getElementsByTagName("bean");
        
        Element bean = null;
        for(int i=0;i<beanNL.getLength();i++) {
            
            Element beanElm = (Element)beanNL.item(i);
            String id = beanElm.getAttribute("id");
            String className = beanElm.getAttribute("class");
            
            if(id == null || className == null)
                continue;
            if(id.equals(beanID)) {
                bean = beanElm;
                break;
            }
        }
        
        if(bean == null) 
            return false;
        
        Set set = properties.keySet();
        Iterator it = set.iterator();
        
        while(it.hasNext()) {
            
            String propName = (String) it.next();
            String value = (String) properties.get(propName);
            
            Element p = doc.createElement("property");
            p.setAttribute("name",propName);
            p.setAttribute("value", value);
            bean.appendChild(p);
        }
        return true;
    }
    
    public void store() {
        
        if(fileObject == null)
            return;
        
        FileLock lock = null;
        OutputStream out = null;
        try {
            
            lock = fileObject.lock();
            out = fileObject.getOutputStream(lock);
            XMLUtil.write(doc, out,"UTF-8");
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }finally {
            
            if(lock != null)
                lock.releaseLock();
            if(out != null)
                try {
                out.close();
            } catch (IOException ex) {
                //do nothing.
            }
        }
    }
    
}
