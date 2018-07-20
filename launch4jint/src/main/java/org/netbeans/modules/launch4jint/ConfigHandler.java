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

package org.netbeans.modules.launch4jint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Handles generation of ant script needed to run Launch4j and also
 * generation and updates of Launch4j config xml file, based on properties
 * of asociated project. 
 *
 * @author Dafe Simonek
 */
public class ConfigHandler {
    
    private static final String NB_PROJECT = "nbproject";
    private static final String RUN_SCRIPT = "launch4j.run";
    private static final String XML_CONFIG = "launch4jconfig.xml";
    private static final String PROJECT_PROPS = "project.properties";

    private static final String NAME = "name";
    
    private Project project;
    
    private Properties projProps;
    
    /** Creates a new instance of ConfigHandler */
    public ConfigHandler (Project project) {
        this.project = project;
    }
    
    public FileObject prepareAllConfig () {
        FileObject rootProjDir = project.getProjectDirectory();
        FileObject nbProjDir = rootProjDir.getFileObject(NB_PROJECT);

        // generate ant script
        String projDirPath = getAbsolutePath(nbProjDir);
        if (projDirPath == null) {
            return null;
        }
        Document script = createScript(projDirPath + "/" + XML_CONFIG);
        if (script == null) {
            return null;
        }

        FileObject scriptFO = null;
        // write script down
        try {
            scriptFO = nbProjDir.getFileObject(RUN_SCRIPT);
            if (scriptFO == null) {
                scriptFO = nbProjDir.createData(RUN_SCRIPT);
            }
            if (!writeXML(script, scriptFO)) {
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        
        Properties projProps = getProjectProperties();
        if (projProps == null) {
            return null;
        }
        
        // parse launch4j config
        Document xmlConfig = null;
        FileObject xmlConfigFO = nbProjDir.getFileObject(XML_CONFIG);
        if (xmlConfigFO != null) {
            try {
                xmlConfig = XMLUtil.parse(new InputSource(xmlConfigFO.getInputStream()),
                                            false, false, null, null);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            } catch (SAXException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        // update config
        xmlConfig = updateConfig(xmlConfig);
        
        // write config
        try {
            if (xmlConfigFO == null) {
                xmlConfigFO = nbProjDir.createData(XML_CONFIG);    
            }
            if (!writeXML(xmlConfig, xmlConfigFO)) {
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        
        return scriptFO;
    }
    
    private static boolean writeXML (Document doc, FileObject fo) {
        FileLock lock = null;
        OutputStream os = null;
        try {
            lock = fo.lock();
            try {
                os = fo.getOutputStream(lock);
                XMLUtil.write(doc, os, "UTF-8");
            } finally {
                if (os != null) {
                    os.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (lock != null) {
                lock.releaseLock();
            }
        }
        return true;
    }
    
    /** Getter for project archive file.
     *
     * @return Jar containing core of asociated project or null if the file
     * doesn't exist for some reason or other file error appeared
     */ 
    public FileObject getProjectJar () {
        Properties projProps = getProjectProperties();
        String distJar = projProps.getProperty("dist.jar");
        String distDir = projProps.getProperty("dist.dir");
        distJar = distJar.replaceAll("\\Q${dist.dir}\\E", distDir);
        return project.getProjectDirectory().getFileObject(distJar);
    }
    
    /** Scans the structure of the project and decides if it is possible
     * for making executable or not.
     *
     * @return true if project has expected structure, false otherwise
     */
    public boolean isTypicalProject () {
        Properties projProps = getProjectProperties();
        if (projProps == null) {
            return false;
        }
        if (projProps.getProperty("dist.jar") == null) {
            return false;
        }
        if (projProps.getProperty("dist.dir") == null) {
            return false;
        }
        return true;
    }
    
    private Properties getProjectProperties () {
        if (projProps != null) {
            return projProps;
        }
        
        FileObject propsFO = project.getProjectDirectory().
                getFileObject(NB_PROJECT + "/" + PROJECT_PROPS);
        if (propsFO == null) {
            return null;
        }
        
        Properties props = new Properties();
        try {
            props.load(propsFO.getInputStream());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        projProps = props;
        return projProps;
    }
    
    private Document createScript (String configPath) {
        String location = Launch4jFinder.getLaunch4jDir();
        if (location == null) {
            return null;
        }
        
        Document doc = XMLUtil.createDocument("project", null, null, null);
        Element root = doc.getDocumentElement();
        root.setAttribute(NAME, "Launch4jExecutor");
        root.setAttribute("default", "all");
        root.setAttribute("basedir", ".");
        Comment cmnt = doc.createComment("Launch4j executor script generated by Launch4jInt module. " +
                          "Do not modify, modifications will be overwritten anyway.");
        doc.insertBefore(cmnt, root);
        
        // launch4j.dir property
        Element elem = doc.createElement("property");
        elem.setAttribute(NAME, "launch4j.dir");
        elem.setAttribute("location", location);
        root.appendChild(elem);
        
        // launch4j.config property
        elem = doc.createElement("property");
        elem.setAttribute(NAME, "launch4j.config");
        elem.setAttribute("location", configPath);
        root.appendChild(elem);

        // main target
        Element target = doc.createElement("target");
        target.setAttribute(NAME, "all");
        
        // launch4j taskdef
        Element taskdef = doc.createElement("taskdef");
        taskdef.setAttribute(NAME, "launch4j");
        taskdef.setAttribute("classname", "net.sf.launch4j.ant.Launch4jTask");
        taskdef.setAttribute("classpath", "${launch4j.dir}/launch4j.jar:${launch4j.dir}/lib/xstream.jar");
    
        Element echo = doc.createElement("echo");
        echo.appendChild(doc.createTextNode(
                "Configuring Launch4j and generating executable for " +
                ProjectUtils.getInformation(project).getDisplayName() + " project:")
        );
        root.appendChild(echo);
        
        // launch4j task run
        Element launch4j = doc.createElement("launch4j");
        launch4j.setAttribute("configFile", "${launch4j.config}");
        
        target.appendChild(taskdef);
        target.appendChild(launch4j);
        root.appendChild(target);

        return doc;
    }
    
    private Document updateConfig (Document doc) {
        String version = getLaunch4jVersion();
        
        if (doc == null) {
            doc = XMLUtil.createDocument("launch4jConfig", null, null, null);
        }
        Element root = doc.getDocumentElement();
        Node firstCmnt = doc.getFirstChild();
        if (firstCmnt.getNodeType() != Node.COMMENT_NODE) {
            Comment cmnt = doc.createComment("Launch4j config xml generated by Launch4jInt module. " +
                              "You *can* modify, but these elements are automatically generated " +
                              "and overwritten: dontWrapJar, jar, outfile, jre/minversion, jre/args");
            doc.insertBefore(cmnt, root);
        }

        Element elem = doc.createElement("dontWrapJar");
        elem.appendChild(doc.createTextNode("true"));
        rewriteElem(root, elem);

        elem = doc.createElement("headerType");
        elem.appendChild(doc.createTextNode("0"));
        rewriteElem(root, elem);

        elem = doc.createElement("jar");
        String distDir = projProps.getProperty("dist.dir", "dist");
        FileObject distFO = project.getProjectDirectory().getFileObject(distDir);
        FileObject distJarFO = getProjectJar();
        elem.appendChild(doc.createTextNode(FileUtil.getRelativePath(distFO, distJarFO)));
        rewriteElem(root, elem);
        
        String projName = ProjectUtils.getInformation(project).getName();
        elem = doc.createElement("outfile");
        String distPath = getAbsolutePath(distFO);
        if (distPath != null) {
            elem.appendChild(doc.createTextNode(distPath + "/" + projName + ".exe"));
            rewriteElem(root, elem);
        }
        
        elem = doc.createElement("customProcName");
        elem.appendChild(doc.createTextNode(projName));
        rewriteElem(root, elem);
        
        addEmptyElem(doc, root, "errTitle");
        // handle differencies between versions of configuration
        if (version != null && version.startsWith("2")) {
            removeElem(root, "cmdLine");
            addEmptyElem(doc, root, "jarArgs");
        } else {
            removeElem(root, "jarArgs");
            addEmptyElem(doc, root, "cmdLine");
        }
        addEmptyElem(doc, root, "chdir");
        addEmptyElem(doc, root, "icon");
        addEmptyElem(doc, root, "stayAlive");

        NodeList jreList = root.getElementsByTagName("jre");
        Element jreElem = null; 
        if (jreList.getLength() == 0) {
            jreElem = doc.createElement("jre");
            root.appendChild(jreElem);
        } else {
            jreElem = (Element) jreList.item(0);
        }
        
        String minVersion = projProps.getProperty("javac.target");
        if (minVersion.matches("\\d\\.\\d")) {
            minVersion = minVersion + ".0";
        }
        elem = doc.createElement("minVersion");
        elem.appendChild(doc.createTextNode(minVersion));
        rewriteElem(jreElem, elem);
        
        addEmptyElem(doc, jreElem, "maxVersion");
        addEmptyElem(doc, jreElem, "path");
        // handle differencies between versions of configuration
        if (version != null && version.startsWith("2")) {
            addEmptyElem(doc, jreElem, "args");
        }
        
        elem = addEmptyElem(doc, jreElem, "initialHeapSize");
        if (elem.getFirstChild() == null) {
            elem.appendChild(doc.createTextNode("0"));
        }
        elem = addEmptyElem(doc, jreElem, "maxHeapSize");
        if (elem.getFirstChild() == null) {
            elem.appendChild(doc.createTextNode("0"));
        }
        
        return doc;
    }
    
    private static void rewriteElem (Element parent, Element newElem) {
        NodeList nodeList = parent.getElementsByTagName(newElem.getTagName());
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            parent.removeChild(nodeList.item(i));
        }
        parent.appendChild(newElem);
    }
    
    private static void removeElem (Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            parent.removeChild(nodeList.item(i));
        }
    }
    
    private static Element addEmptyElem (Document doc, Element parent, String tagName) {
        Element result;
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() <= 0) {
            result = doc.createElement(tagName);
            parent.appendChild(result);
        } else {
            result = (Element) nodeList.item(0);
        }
        return result;
    }

    private static String getAbsolutePath (FileObject fo) {
        File file = FileUtil.toFile(fo);
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    /** Return version of launch4j in string form or null if version 
     * can't be obtained.
     */
    private static String getLaunch4jVersion () {
        String jarPath = Launch4jFinder.getLaunch4jDir() + "/launch4j.jar";
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath, false);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        JarEntry propEntry = jarFile.getJarEntry("launch4j.properties");
        
        // versions before 3.x doesn't have properties file
        if (propEntry == null) {
            return "2.x";
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(jarFile.getInputStream(propEntry)));
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("version=")) {
                        int index = inputLine.indexOf('=');
                        if (index > 0) {
                            return inputLine.substring(index + 1);
                        } else {
                            return null;
                        }
                    }
                }
            } finally {
                in.close();        
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
       
        return null;
    }
    
}
