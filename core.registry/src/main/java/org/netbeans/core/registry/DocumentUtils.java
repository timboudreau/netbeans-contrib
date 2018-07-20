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

package org.netbeans.core.registry;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.xml.EntityCatalog;
import org.openide.xml.XMLUtil;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 *
 * @author copy&pasted from core/settings
 */
public class DocumentUtils {

    private static final Logger log = Logger.getLogger(DocumentUtils.class.getName());
    private static final boolean LOGABLE = log.isLoggable(Level.FINE);
    
    private DocumentUtils() {
    }

    static Document createDocument() {
        Document doc = null;
        doc = XMLUtil.createDocument("registry", null, null, null);
        doc.removeChild(doc.getFirstChild());
        return doc;
    }

    static void writeDocument(FileObject fo, Document doc) throws IOException {
        FileLock lock = fo.lock();
        OutputStream os = fo.getOutputStream(lock);
        boolean ok = false;
        try {
            XMLUtil.write(doc, os, "UTF-8"); // NOI18N
            ok = true;
        } finally {
            os.close();
            lock.releaseLock();
            if (!ok) {
                // writing failed. kill the file
                fo.delete();
            }
        }
    }

    public static String getTextValue(Element element) {
        StringBuffer sb = new StringBuffer();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                sb.append(((Text)node).getData());
            }
            if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
                sb.append(((CDATASection)node).getData());
            }
        }
        return sb.toString();
    }
    
    final static class DocumentRef {
        private Reference docRef;
        private static final Object[] document = new Object[2];

        Document getDocument(FileObject fo) {
            assert fo != null;

            Document d = null;
            synchronized (this) {
                if (docRef == null || (d = (Document) docRef.get()) == null) {
                    d = getDOM(fo);
                    docRef = new WeakReference(d);
                }
            }

            // Check whether the resulting document is Ok:
            if (d.getDocumentElement() == null)  {
                docRef = null;
                document[0] = null;
                document[1] = null;
                // Try to parse it again:
                synchronized (this) {
                    if (docRef == null || (d = (Document) docRef.get()) == null) {
                        d = getDOM(fo);
                        docRef = new WeakReference(d);
                    }
                }
                if (d.getDocumentElement() == null) {
                    printFile(fo);
                    throw new IllegalStateException("Cannot parse file " + fo);
                }
                
            }
            
            return d;
        }


        static synchronized Document getDOM(FileObject fo) {
            Document retVal = (Document) ((document[0] == null) ? null : ((Reference) (document[0])).get());
            if (retVal != null) {
                FileObject foRef = (FileObject) ((document[1] == null) ? null : ((Reference) (document[1])).get());
                if (foRef != null && foRef == fo) {
                    return retVal;
                }
            }
            try {
                if (fo.getSize() == 0) {
                    return null;
                }
                InputStream is = fo.getInputStream();
                try {
                    InputSource iss = new InputSource(is);
                    retVal = XMLUtil.parse(iss, false, true, null, new EntityResolver() {
                        public InputSource resolveEntity(String publicId,String systemId)
                                throws SAXException, IOException {
                            InputSource retVal = EntityCatalog.getDefault().resolveEntity(publicId,systemId);
                            return (retVal != null) ? retVal : new InputSource(new ByteArrayInputStream(new byte[0]));
                        }
                    });
                    if (retVal != null) {
                        document[0] = new WeakReference(retVal);
                        document[1] = new WeakReference(fo);
                    }
                    return retVal;
                } catch (Exception e) {
                    if (LOGABLE) {
                        log.log(Level.FINE, 
                            "Could not parse file [" + fo + "].\n" + e.toString(), e);
                    }
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                if (LOGABLE) {
                    log.log(Level.FINE, 
                        "Could not parse file [" + fo + "].\n" + e.toString(),
                        e);
                }
            }
            return null;
        }
    }
    
    /** Debugging printout of the file */
    private static void printFile(FileObject f) {
        try {
            System.err.println("Printing problem file: " + f); // NOI18N
            java.io.InputStream in = f.getInputStream();
            int ch = 0;
            while ( (ch = in.read()) != -1) {
                System.err.write(ch);
            }
            in.close();
        } catch (java.io.IOException ioe) {
            log.log(Level.FINE, "", ioe);
        }
    }
}
