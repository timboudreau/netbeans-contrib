/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.blueprints.ui.projects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileLock;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.*;
//import org.xml.sax.InputSource;

/**
 * Create a sample J2EE project by unzipping a template into some directory
 *
 * @author Ludo Champenois
 */
public class J2eeSampleProjectGenerator {
    
    private J2eeSampleProjectGenerator() {}

    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/web-project/3";    //NOI18N
    public static final String PROJECT_CONFIGURATION_NS_FREE = "http://www.netbeans.org/ns/freeform-project/1";
    public static final String EJBJAR_NAMESPACE = "http://www.netbeans.org/ns/j2ee-ejbjarproject/2";
    public static final String SUNWEBDD_XMLLOC = "web/WEB-INF/sun-web.xml";

    public static FileObject createProjectFromTemplate(final FileObject template, File projectLocation, final String name) throws IOException {
        FileObject prjLoc = null;
        String slashname = "/"+name;
        if (template.getExt().endsWith("zip")) {  //NOI18N
            unzip(template.getInputStream(), projectLocation);
                String extra = (String)template.getAttribute("extrazip");
                if (extra!=null){
                    FileObject p = template.getParent().getFileObject(extra+".zip");
                    
                    if (p!=null){
                        unzip(p.getInputStream(), new File( projectLocation.getParentFile(),extra));
                    }
                }
                
            try {
                prjLoc = FileUtil.toFileObject(projectLocation);
                
                NodeList nlist = null;
                //update project.xml
                File projXml = FileUtil.toFile(prjLoc.getFileObject(AntProjectHelper.PROJECT_XML_PATH));
                Document doc = XMLUtil.parse(new InputSource(projXml.toURI().toString()), false, true, null, null);
                nlist = doc.getElementsByTagNameNS(PROJECT_CONFIGURATION_NAMESPACE, "name");      //NOI18N
                // if web is not found, try ejb
                // need to check the length too as it gets a node list contains no "name"
                // with this name space.
                if (nlist==null || nlist.getLength()==0){
                    nlist = doc.getElementsByTagNameNS(EJBJAR_NAMESPACE, "name");      //NOI18N
                    
                }
                if (nlist != null) {
                    for (int i=0; i < nlist.getLength(); i++) {
                        Node n = nlist.item(i);
                        if (n.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }
                        Element e = (Element)n;
                        
                        replaceText(e, name);
                        saveXml(doc, prjLoc, AntProjectHelper.PROJECT_XML_PATH);
                    }
                }
                
                // update sun-web.xml
                
                FileObject sunwebfile = prjLoc.getFileObject(SUNWEBDD_XMLLOC);
                // WebTier entry must have web/WEB-INF/sun-web.xml.
                // If there isn't, assuming it's in other categories like SOA.
                if (sunwebfile!=null) {
                    File sunwebXml = FileUtil.toFile(sunwebfile);
                    // XMLUtil.parse seems to try to connect the internet even validation is false.
                    // Creating entityResolver privately
                    Document swdoc = XMLUtil.parse(new InputSource(sunwebXml.toURI().toString()), false, true, null, SunWebDDResolver.getInstance());
                    NodeList swnlist = swdoc.getElementsByTagName("context-root");      //NOI18N
                    // NodeList should contain only one, but just incase
                    if (swnlist != null) {
                        for (int i=0; i < swnlist.getLength(); i++) {
                            Node n = swnlist.item(i);
                            if (n.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }
                            Element e = (Element)n;
                            replaceText(e, slashname);
                            saveXml(swdoc, prjLoc, SUNWEBDD_XMLLOC);
                        }
                    }
                }
                 
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
                   
            prjLoc.refresh(false);
        }
        return prjLoc;
    }
    
    private static void unzip(InputStream source, File targetFolder) throws IOException {
        //installation
        ZipInputStream zip=new ZipInputStream(source);
        try {
            ZipEntry ent;
            while ((ent = zip.getNextEntry()) != null) {
                File f = new File(targetFolder, ent.getName());
                if (ent.isDirectory()) {
                    f.mkdirs();
                } else {
                    f.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(f);
                    try {
                        FileUtil.copy(zip, out);
                    } finally {
                        out.close();
                    }
                }
            }
        } finally {
            zip.close();
        }
    }

    /**
     * Extract nested text from an element.
     * Currently does not handle coalescing text nodes, CDATA sections, etc.
     * @param parent a parent element
     * @return the nested text, or null if none was found
     */
    private static void replaceText(Element parent, String name) {
        NodeList l = parent.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getNodeType() == Node.TEXT_NODE) {
                Text text = (Text)l.item(i);
                text.setNodeValue(name);
                return;
            }
        }
    }
    
    /**
     * Save an XML config file to a named path.
     * If the file does not yet exist, it is created.
     */
    private static void saveXml(Document doc, FileObject dir, String path) throws IOException {
        FileObject xml = FileUtil.createData(dir, path);
        FileLock lock = xml.lock();
        try {
            OutputStream os = xml.getOutputStream(lock);
            try {
                XMLUtil.write(doc, os, "UTF-8"); // NOI18N
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
        private static class SunWebDDResolver implements EntityResolver {
        static SunWebDDResolver resolver;
        static synchronized SunWebDDResolver getInstance() {
            if (resolver==null) {
                resolver=new SunWebDDResolver();
            }
            return resolver;
        }        
        public InputSource resolveEntity (String publicId, String systemId) {
            String resource=null;
            // return a proper input source
            resource = "/org/netbeans/modules/j2ee/blueprints/ui/resources/sun-web-app_2_4-1.dtd"; //NOI18N
/***
            if ("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN".equals(publicId)) { //NOI18N
                resource="/org/netbeans/modules/j2ee/dd/impl/resources/web-app_2_3.dtd"; //NOI18N
            } else if ("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN".equals(publicId)) { //NOI18N
                resource="/org/netbeans/modules/j2ee/dd/impl/resources/web-app_2_2.dtd"; //NOI18N
            } else if ("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd".equals(systemId)) {
                resource="/org/netbeans/modules/j2ee/dd/impl/resources/web-app_2_4.xsd"; //NOI18N
            }
            if (resource==null) return null;
 ****/
            java.net.URL url = this.getClass().getResource(resource);
            return new InputSource(url.toString());
        }
    }
}
