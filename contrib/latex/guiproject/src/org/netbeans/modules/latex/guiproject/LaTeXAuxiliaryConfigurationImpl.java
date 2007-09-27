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

package org.netbeans.modules.latex.guiproject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.UserQuestionException;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**Mostly copied from ant/project's AntProjectHelper and modified for LaTeX project by Jan Lahoda.
 *
 */
public class LaTeXAuxiliaryConfigurationImpl implements AuxiliaryConfiguration {
    
    /**
     * XML namespace of Ant projects.
     */
    static final String PROJECT_NS = "http://www.netbeans.org/ns/project/1"; // NOI18N
    
    /**
     * XML namespace of private component of Ant projects.
     */
    static final String PRIVATE_NS = "http://www.netbeans.org/ns/project-private/1"; // NOI18N
    
    private LaTeXGUIProject project;
    
    /** Creates a new instance of LaTeXAuxiliaryConfigurationImpl */
    public LaTeXAuxiliaryConfigurationImpl(LaTeXGUIProject project) {
        this.project = project;
    }
    
    private String getPath(boolean shared) {
        return shared ? "public.xml" : "private.xml";
    }
    
    /**
     * Retrieve project.xml or private.xml, loading from disk as needed.
     * private.xml is created as a skeleton on demand.
     */
    private Document getConfigurationXml(boolean shared) {
        assert ProjectManager.mutex().isReadAccess() || ProjectManager.mutex().isWriteAccess();
        synchronized (project) { // can be in read lock only; can project be used as lock?
            String path = getPath(shared);
            Document xml = loadXml(path);
            if (xml == null) {
                // Missing or broken; create a skeleton.
                String element = shared ? "project" : "project-private"; // NOI18N
                String ns = shared ? PROJECT_NS : PRIVATE_NS;
                xml = XMLUtil.createDocument(element, ns, null, null);
                if (shared) {
                    // #46048: need to generate minimal compliant XML skeleton.
                    Element typeEl = xml.createElementNS(PROJECT_NS, "type"); // NOI18N
                    typeEl.appendChild(xml.createTextNode("LaTeX GUI Project"));
                    xml.getDocumentElement().appendChild(typeEl);
                    xml.getDocumentElement().appendChild(xml.createElementNS(PROJECT_NS, "configuration")); // NOI18N
                }
            }
            assert xml != null;
            return xml;
        }
    }
    
    /**
     * If true, do not report XML load errors.
     * For use only by unit tests.
     */
    static boolean QUIETLY_SWALLOW_XML_LOAD_ERRORS = false;
    
    /**
     * Try to load a config XML file from a named path.
     * If the file does not exist, or there is any load error, return null.
     */
    private Document loadXml(String path) {
        assert ProjectManager.mutex().isReadAccess() || ProjectManager.mutex().isWriteAccess();
        FileObject xml = project.getProjectDirectory().getFileObject(path);
        if (xml == null || !xml.isData()) {
            return null;
        }
        File f = FileUtil.toFile(xml);
        assert f != null;
        try {
            return XMLUtil.parse(new InputSource(f.toURI().toString()), false, true, null, null);
        } catch (IOException e) {
            if (!QUIETLY_SWALLOW_XML_LOAD_ERRORS) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        } catch (SAXException e) {
            if (!QUIETLY_SWALLOW_XML_LOAD_ERRORS) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        }
        return null;
    }
    
    private boolean writingXML = false;
    
    /**
     * Save an XML config file to a named path.
     * If the file does not yet exist, it is created.
     */
    private void saveXml(final Document doc, final String path) /*throws IOException*/ {
        assert ProjectManager.mutex().isWriteAccess();
        assert !writingXML;
        writingXML = true;
        try {
            project.getProjectDirectory().getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws IOException {
                    final FileObject xml = FileUtil.createData(project.getProjectDirectory(), path);
                    final FileSystem.AtomicAction body = new FileSystem.AtomicAction() {
                        public void run() throws IOException {
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
                    };
                    try {
                        body.run();
                    } catch (UserQuestionException uqe) { // #46089
                        UserQuestionHandler.handle(uqe, new UserQuestionHandler.Callback() {
                            public void accepted() {
                                // Try again.
                                try {
                                    body.run();
                                } catch (IOException e) {
                                    // Oh well.
                                    ErrorManager.getDefault().notify(e);
                                    reload();
                                }
                            }
                            public void denied() {
                                reload();
                            }
                            public void error(IOException e) {
                                ErrorManager.getDefault().notify(e);
                                reload();
                            }
                            private void reload() {
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            writingXML = false;
        }
    }
    
    /**
     * Get the <code>&lt;configuration&gt;</code> element of project.xml
     * or the document element of private.xml.
     * Beneath this point you can load and store configuration fragments.
     * @param shared if true, use project.xml, else private.xml
     * @return the data root
     */
    private Element getConfigurationDataRoot(boolean shared) {
        assert ProjectManager.mutex().isReadAccess() || ProjectManager.mutex().isWriteAccess();
        Document doc = getConfigurationXml(shared);
        if (shared) {
            Element project = doc.getDocumentElement();
            Element config = findElement(project, "configuration", PROJECT_NS); // NOI18N
            assert config != null;
            return config;
        } else {
            return doc.getDocumentElement();
        }
    }
    
    /**
     * Get a piece of the configuration subtree by name.
     * @param elementName the simple XML element name expected
     * @param namespace the XML namespace expected
     * @param shared to use project.xml vs. private.xml
     * @return (a clone of) the named configuration fragment, or null if it does not exist
     */
    public Element getConfigurationFragment(final String elementName, final String namespace, final boolean shared) {
        return (Element) ProjectManager.mutex().readAccess(new Mutex.Action() {
            public Object run() {
                Element root = getConfigurationDataRoot(shared);
                Element data = findElement(root, elementName, namespace);
                if (data != null) {
                    // XXX should also perhaps set ownerDocument to a dummy empty document?
                    // or create a new document with this as a root?
                    return (Element) data.cloneNode(true);
                } else {
                    return null;
                }
            }
        });
    }
    
    /**
     * Store a piece of the configuration subtree by name.
     * @param fragment a piece of the subtree to store (overwrite or add)
     * @param shared to use project.xml vs. private.xml
     */
    public void putConfigurationFragment(final Element fragment, final boolean shared) {
        ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                Element root = getConfigurationDataRoot(shared);
                Element existing = findElement(root, fragment.getLocalName(), fragment.getNamespaceURI());
                // XXX first compare to existing and return if the same
                if (existing != null) {
                    root.removeChild(existing);
                }
                // the children are alphabetize: find correct place to insert new node
                Node ref = null;
                NodeList list = root.getChildNodes();
                for (int i=0; i<list.getLength(); i++) {
                    Node node  = list.item(i);
                    if (node.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    int comparison = node.getNodeName().compareTo(fragment.getNodeName());
                    if (comparison == 0) {
                        comparison = node.getNamespaceURI().compareTo(fragment.getNamespaceURI());
                    }
                    if (comparison > 0) {
                        ref = node;
                        break;
                    }
                }
                root.insertBefore(root.getOwnerDocument().importNode(fragment, true), ref);
                saveXml(root.getOwnerDocument(), getPath(shared));
                return null;
            }
        });
    }
    
    /**
     * Remove a piece of the configuration subtree by name.
     * @param elementName the simple XML element name expected
     * @param namespace the XML namespace expected
     * @param shared to use project.xml vs. private.xml
     * @return true if anything was actually removed
     */
    public boolean removeConfigurationFragment(final String elementName, final String namespace, final boolean shared) {
        return ((Boolean) ProjectManager.mutex().writeAccess(new Mutex.Action() {
            public Object run() {
                Element root = getConfigurationDataRoot(shared);
                Element data = findElement(root, elementName, namespace);
                if (data != null) {
                    root.removeChild(data);
                    saveXml(root.getOwnerDocument(), getPath(shared));
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        })).booleanValue();
    }
    
    /**
     * Search for an XML element in the direct children of a parent.
     * DOM provides a similar method but it does a recursive search
     * which we do not want. It also gives a node list and we want
     * only one result.
     * @param parent a parent element
     * @param name the intended local name
     * @param namespace the intended namespace
     * @return the one child element with that name, or null if none or more than one
     */
    public static Element findElement(Element parent, String name, String namespace) {
        Element result = null;
        NodeList l = parent.getChildNodes();
        int len = l.getLength();
        for (int i = 0; i < len; i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element)l.item(i);
                if (name.equals(el.getLocalName()) && namespace.equals(el.getNamespaceURI())) {
                    if (result == null) {
                        result = el;
                    } else {
                        return null;
                    }
                }
            }
        }
        return result;
    }
}
