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

import org.netbeans.api.registry.ObjectRef;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Provides ObjectBinding.Reader and ObjectBinding.Writer for ObjectBinding implementation.
 * (copy & pasted & refactored from original org.netbeans.core.registry.ObjectBinding)
 */
final class ObjectRefBinding {
    private static final String OBJECTREF_EXTENSION = "xml";//NOI18N
    private static final String OBJECTREF_NAMESPACE = "http://www.netbeans.org/ns/registry";
    private static final String OBJECTREF_ELEMENT = "link";

    static final ObjectBinding.Reader READER = new ReaderImpl();
    static final ObjectBinding.Writer WRITER = new WriterImpl();

    private ObjectRefBinding() {
    }

    private static class ReaderImpl extends ObjectBinding.Reader {
        boolean canRead(FileObject fo) {
            boolean isRightExtension = fo.getExt().equalsIgnoreCase(OBJECTREF_EXTENSION);
            Document d = (isRightExtension == true) ? DocumentUtils.DocumentRef.getDOM(fo) : null;  
            
            return (isRightExtension && d != null && d.getDocumentElement() != null && d.getDocumentElement().getNamespaceURI() != null
                    && d.getDocumentElement().getNamespaceURI().equals(OBJECTREF_NAMESPACE) &&
                    d.getDocumentElement().getNodeName().equals(OBJECTREF_ELEMENT));
        }

        String getFileExtension() {
            return OBJECTREF_EXTENSION;
        }

        ObjectBinding getObjectBinding(BasicContext ctx, FileObject fo) {
            return new ObjectBindingImpl(ctx, fo);
        }
    }

    private static class WriterImpl extends ObjectBinding.Writer {
        boolean canWrite(Object obj) {
            return (obj instanceof ObjectRef);
        }

        void write(FileObject fo, Object obj) throws IOException {
            Document doc = org.netbeans.core.registry.DocumentUtils.createDocument();
            Element e = doc.createElementNS(OBJECTREF_NAMESPACE, OBJECTREF_ELEMENT);
            Text t = doc.createTextNode(((ObjectRef) obj).getContextAbsoluteName() + "/" + ((ObjectRef) obj).getBindingName());
            e.appendChild(t);
            doc.appendChild(e);
            org.netbeans.core.registry.DocumentUtils.writeDocument(fo, doc);
        }

        String getFileExtension() {
            return OBJECTREF_EXTENSION;
        }
    }

    private static final class ObjectBindingImpl extends ObjectBinding {
        private DocumentUtils.DocumentRef docRef;
        private BasicContext context;

        ObjectBindingImpl(BasicContext context, FileObject fo) {
            super(fo);
            this.context = context;
            docRef = new DocumentUtils.DocumentRef();
        }

        public Object createInstance() throws IOException {
            Object retVal = null;
            Document d = docRef.getDocument(getFile());
            String target = DocumentUtils.getTextValue(d.getDocumentElement());
            target = target.trim();
            int i = target.lastIndexOf('/');
            String contextName = target.substring(0, i);
            String bindingName = target.substring(i + 1);
            retVal = SpiUtils.createObjectRef(((ContextImpl) context).getRootContextImpl(), contextName, bindingName);
            return retVal;
        }

        public boolean isEnabled() {
            return (getFile() != null && getFile().isValid());
        }
    }

}
