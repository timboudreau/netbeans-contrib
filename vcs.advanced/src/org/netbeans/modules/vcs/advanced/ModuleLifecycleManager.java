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

package org.netbeans.modules.vcs.advanced;

import org.openide.modules.ModuleInstall;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;
import org.openide.xml.XMLUtil;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.xml.sax.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.*;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * Ensures that new versioning modules (New CVS, etc.) are disabled when this module installs.
 * 
 * @author Maros Sandor
 */
public class ModuleLifecycleManager extends ModuleInstall implements ErrorHandler, EntityResolver {
    
    static final String [] newModules = {
        "org.netbeans.modules.versioning.system.cvss",
    };
    
    public void restored() {
        disableNewModules();
    }

    private void disableNewModules() {
        Runnable runnable = new Runnable() {
            public void run() {
                outter: for (int i = 0; i < newModules.length; i++) {
                    FileLock lock = null;
                    OutputStream os = null;
                    try {
                        String newModule = newModules[i];
                        String newModuleXML = "Modules/" + newModule.replace('.', '-') + ".xml";
                        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(newModuleXML);
                        if (fo == null) continue;
                        Document document = readModuleDocument(fo);

                        NodeList list = document.getDocumentElement().getElementsByTagName("param");
                        int n = list.getLength();
                        for (int j = 0; j < n; j++) {
                            Element node = (Element) list.item(j);
                            if ("enabled".equals(node.getAttribute("name"))) {
                                Text text = (Text) node.getChildNodes().item(0);
                                String value = text.getNodeValue();
                                if ("true".equals(value)) {
                                    text.setNodeValue("false");
                                    break;
                                } else {
                                    continue outter;
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(null, 
                                                      NbBundle.getBundle(ModuleLifecycleManager.class).getString("MSG_Install_Warning"), 
                                                      NbBundle.getBundle(ModuleLifecycleManager.class).getString("MSG_Install_Warning_Title"), 
                                                      JOptionPane.WARNING_MESSAGE);
                        lock = fo.lock();
                        os = fo.getOutputStream(lock);

                        XMLUtil.write(document, os, "UTF-8");
                    } catch (Exception e) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    } finally {
                        if (os != null) try { os.close(); } catch (IOException ex) {}
                        if (lock != null) lock.releaseLock();
                    }
                }
            }
        };
        RequestProcessor.getDefault().post(runnable);
    }

    private Document readModuleDocument(FileObject fo) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder parser = dbf.newDocumentBuilder();
        parser.setEntityResolver(this);
        parser.setErrorHandler(this);
        InputStream is = fo.getInputStream();
        Document document = parser.parse(is);
        is.close();
        return document;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }
    
    public void error(SAXParseException exception) throws SAXException {
        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exception);
    }

    public void warning(SAXParseException exception) throws SAXException {
        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exception);
    }

    public void uninstalled() {
        ProfilesFactory.getDefault().shutdown();
    }
}
