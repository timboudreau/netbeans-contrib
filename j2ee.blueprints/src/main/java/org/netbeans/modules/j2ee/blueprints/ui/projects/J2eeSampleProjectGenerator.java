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

package org.netbeans.modules.j2ee.blueprints.ui.projects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.netbeans.modules.j2ee.blueprints.catalog.SolutionsCatalog;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
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
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Nbcatalog;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Nbcategory;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Nbexample;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Nbsolution;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Nbwriteup;
import org.netbeans.modules.j2ee.blueprints.ui.BpcatalogLocalizedResource;
import org.netbeans.modules.j2ee.blueprints.ui.overview.OverviewPageTopComponent;
import org.openide.modules.InstalledFileLocator;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Create a sample J2EE project by unzipping a template into some directory
 *
 * @author Ludo Champenois
 */
public class J2eeSampleProjectGenerator {
    
    private J2eeSampleProjectGenerator() {}

    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://www.netbeans.org/ns/web-project/3";    //NOI18N
    public static final String PROJECT_CONFIGURATION_NS_FREE = "http://www.netbeans.org/ns/freeform-project/1";
    public static final String EJBJAR_NAMESPACE = "http://www.netbeans.org/ns/j2ee-ejbjarproject/3";
    public static final String PROJECT_CONFIGURATION_J2SE = "http://www.netbeans.org/ns/j2se-project/2";
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
                if (nlist == null || nlist.getLength() == 0) {
                    nlist = doc.getElementsByTagNameNS(PROJECT_CONFIGURATION_J2SE, "name");
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
                
                // Change props in project.properties
                replaceProperties(prjLoc, name);
                
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
                   
            prjLoc.refresh(false);
        }
        return prjLoc;
    }
    
    private static void replaceProperties(FileObject dir, String prjName) throws IOException {
        
        // heuristic hack process for each project type.
        // should use AntProjectHelper, if possible.
        final String DIST_JAR = "dist.jar";  // NOI18N
        final String DIST_DIR = "${dist.dir}/";  // NOI18N
        final String DIST_EAR_JAR = "dist.ear.jar";  // NOI18N
        final String WAR_NAME = "war.name";  // NOI18N
        final String WAR_EAR_NAME = "war.ear.name";  // NOI18N
        
        String BP_LIB_DIR = "bp-lib.dir"; //NOI18N
        String BP_UI5_JAR = "file.reference.bp-ui-5.jar"; //NOI18N
        
        String currentProp;
        String suffix;
        
        EditableProperties props = new EditableProperties(true);
        FileObject prjProp = FileUtil.createData(dir, AntProjectHelper.PROJECT_PROPERTIES_PATH);
        
        try {
            InputStream in = prjProp.getInputStream();
            props.load(in);
            in.close();
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
        
        String distJar = props.getProperty(DIST_JAR);
        // 1. for J2SE/EAR type : should be "dist.jar=${dist.dir}/PROJECT.[jar, ear]"
        if (distJar != null) {
            props.setProperty(DIST_JAR, DIST_DIR + prjName + 
                    distJar.substring(distJar.lastIndexOf('.')));  // NOI18N
            //2. for EJB type : should be "dist.ear.jar=${dist.dir}/PROJECT.[jar, ear]"
            currentProp = props.getProperty(DIST_EAR_JAR);
            if (currentProp != null) {
                props.setProperty(DIST_EAR_JAR,
                        DIST_DIR + prjName + currentProp.substring(currentProp.indexOf(".")));  // NOI18N
            }
        // 3. for Web app : should be "war.name=PROJECT.war"
        } else if (props.getProperty(WAR_NAME) != null) {
            props.setProperty(WAR_NAME, prjName + ".war");  // NOI18N
            // Web app should have this as well. Just in case...
            // this should be "war.ear.name=PROJECT.[war, ear]"
            currentProp = props.getProperty(WAR_EAR_NAME);
            if (currentProp != null) {
                props.setProperty(WAR_EAR_NAME,
                        prjName + currentProp.substring(currentProp.indexOf("."))); // NOI18N
            }
        }
        
        //Replace library location
        String bpLibsLocation = getBlueprintsLibsLocation();
        props.setProperty(BP_LIB_DIR, bpLibsLocation);
        if(props.getProperty(BP_UI5_JAR) != null){
            props.setProperty(BP_UI5_JAR, bpLibsLocation + File.separator + "bp-ui-5.jar"); //NOI18N
        }
        
        //helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
        FileLock lock = prjProp.lock();
        try {
            OutputStream os = prjProp.getOutputStream(lock);
            try {
                props.store(os);
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
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
        
    private static String getBlueprintsLibsLocation(){
        String location = "";
        File f = InstalledFileLocator.getDefault().locate("modules/ext/blueprints/bp-ui-14.jar", null, true);        
        if(f != null)
            location = f.getParentFile().getAbsolutePath();
        return location;
    }    
    
    public static void getOverviewPage(org.openide.loaders.TemplateWizard templateWizard){
        TopComponent tc = WindowManager.getDefault().findTopComponent("BluePrints");
        if(tc.isOpened()){
            return;
        }   
        
        FileObject template = templateWizard.getTemplate().getPrimaryFile();
        String currentModName = template.getName();
        SolutionsCatalog solutionsCatalog = SolutionsCatalog.getInstance();
        
        Nbcatalog nbcatalog = solutionsCatalog.getCatalogXml();
        HashMap overviewFiles = populateEntries(nbcatalog);
        String pagePath = (String)overviewFiles.get(currentModName);
        String overviewPage = getLocalizedPath(pagePath);

        TopComponent win = OverviewPageTopComponent.findInstance();
        OverviewPageTopComponent comp = (OverviewPageTopComponent)win;
        if(comp.isOpened())
            comp.close();
        comp.setOverviewFile(overviewPage);
        comp.open();
        comp.requestActive();
    }
       
    private static HashMap populateEntries(Nbcatalog nbcatalog){ 
        HashMap overviewFiles = new HashMap();
        List cats = nbcatalog.fetchNbcategoryList();
        for(int catNum=0; catNum<cats.size(); catNum++){
            Nbcategory category = (Nbcategory)cats.get(catNum);
            List sols = category.fetchNbsolutionList();
            for(int solNum=0; solNum<sols.size(); solNum++){
                Nbsolution sol = (Nbsolution)sols.get(solNum);
                Nbwriteup write = sol.getNbwriteup();
                String categoryID = category.getId();
                String articlePath = write.getArticlePath();
                if(categoryID.equals("Ajax")) { //NOI18N
                    articlePath = "docs/ajax/overview-Ajax.html";
                }else if(categoryID.equals("JavaPersistence")) { //NOI18N
                    articlePath = "docs/persistence/overview-JavaPersistence.html";
                }
                overviewFiles.put(sol.getExampleId(), articlePath);
            }
        }
        return overviewFiles;
    }
    
    private static String getLocalizedPath(String articlePath){
        String CATALOG_RESOURCES_URL = "/org/netbeans/modules/j2ee/blueprints/catalog/resources"; // NOI18N
        String path = CATALOG_RESOURCES_URL  + "/" + articlePath; // NOI18N
        BpcatalogLocalizedResource htmlrsc =
                    new BpcatalogLocalizedResource(path, "html"); // NOI18N
        String localizedPath = htmlrsc.getResourcePath();
        return localizedPath;
    }    
}
