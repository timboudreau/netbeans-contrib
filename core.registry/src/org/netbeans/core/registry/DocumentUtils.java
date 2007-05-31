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

package org.netbeans.core.registry;

import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.xml.EntityCatalog;
import org.openide.xml.XMLUtil;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;

/** 
 *
 * @author copy&pasted from core/settings
 */
public class DocumentUtils {
    
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
                    if (ErrorManager.getDefault().isLoggable(ErrorManager.INFORMATIONAL)) {
                        ErrorManager.getDefault().log(ErrorManager.WARNING, "Could not parse file [" + fo + "].\n" + e.toString());
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                if (ErrorManager.getDefault().isLoggable(ErrorManager.INFORMATIONAL)) {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "Could not parse file [" + fo + "].\n" + e.toString());
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
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
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
        }
    }
}
