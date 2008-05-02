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

package org.netbeans.modules.autoproject.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * implementation of AuxiliaryConfiguration that relies on FileObject's attributes
 * @author mkleint
 */
class AuxiliaryConfigImpl implements AuxiliaryConfiguration {

    private static final String AUX_CONFIG_PRIVATE = "AuxilaryConfigurationPrivate"; //NOI18N
    private static final String AUX_CONFIG_SHARED = "AuxilaryConfigurationShared"; //NOI18N
    private final Project project;

    public AuxiliaryConfigImpl(Project proj) {
        this.project = proj;
    }

    public Element getConfigurationFragment(final String elementName, final String namespace, final boolean shared) {
        return ProjectManager.mutex().readAccess(new Mutex.Action<Element>() {
            public Element run() {
                String str = (String)project.getProjectDirectory().getAttribute(shared ? AUX_CONFIG_SHARED : AUX_CONFIG_PRIVATE);
                if (str != null) {
                    Document doc;
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                        return findElement(doc.getDocumentElement(), elementName, namespace);
                    } catch (SAXException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                return null;
            }});
    }

    public void putConfigurationFragment(final Element fragment, final boolean shared) throws IllegalArgumentException {
        ProjectManager.mutex().writeAccess(new Mutex.Action<Void>() {
            public Void run() {
                String attr = shared ? AUX_CONFIG_SHARED : AUX_CONFIG_PRIVATE;
                String str = (String) project.getProjectDirectory().getAttribute(attr);
                Document doc = null;
                if (str != null) {
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    String element = "configuration"; // NOI18N
                    doc = XMLUtil.createDocument(element, null, null, null);
                }
                if (doc != null) {
                    Element el = findElement(doc.getDocumentElement(), fragment.getNodeName(), fragment.getNamespaceURI());
                    if (el != null) {
                        doc.getDocumentElement().removeChild(el);
                    }
                    doc.getDocumentElement().appendChild(doc.importNode(fragment, true));
                }

                try {
                    ByteArrayOutputStream wr = new ByteArrayOutputStream();
                    XMLUtil.write(doc, wr, "UTF-8");
                    project.getProjectDirectory().setAttribute(attr, wr.toString("UTF-8"));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return null;
            }
        });

    }

    public boolean removeConfigurationFragment(final String elementName, final String namespace, final boolean shared) throws IllegalArgumentException {
        return ProjectManager.mutex().writeAccess(new Mutex.Action<Boolean>() {
            public Boolean run() {
                String attr = shared ? AUX_CONFIG_SHARED : AUX_CONFIG_PRIVATE;
                String str = (String) project.getProjectDirectory().getAttribute(attr);
                Document doc = null;
                if (str != null) {
                    try {
                        doc = XMLUtil.parse(new InputSource(new StringReader(str)), false, true, null, null);
                    } catch (SAXException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {
                    return false;
                }
                if (doc != null) {
                    Element el = findElement(doc.getDocumentElement(), elementName, namespace);
                    if (el != null) {
                        doc.getDocumentElement().removeChild(el);
                    }
                }
                try {
                    ByteArrayOutputStream wr = new ByteArrayOutputStream();
                    XMLUtil.write(doc, wr, "UTF-8");
                    project.getProjectDirectory().setAttribute(attr, wr.toString("UTF-8"));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return true;
            }
        });
    }


    private static Element findElement(Element parent, String name, String namespace) {
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
