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

import org.netbeans.api.convertor.ConvertorDescriptor;
import org.netbeans.api.convertor.Convertors;
import org.netbeans.spi.registry.BasicContext;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * Provides ObjectBinding.Reader and ObjectBinding.Writer for ObjectBinding implementation.
 * (copy & pasted & refactored from original org.netbeans.core.registry.ObjectBinding)
 */
final class ConvertorBinding {
    private static final String CONVERTOR_EXTENSION = "xml";//NOI18N    
    static final ObjectBinding.Reader READER = new ReaderImpl();
    static final ObjectBinding.Writer WRITER = new WriterImpl();

    private ConvertorBinding() {
    }

    private static final class ReaderImpl extends ObjectBinding.Reader {
        final boolean canRead(final FileObject fo) {
            boolean isRightExtension = fo.getExt().equalsIgnoreCase(getFileExtension());
            Document dom = (isRightExtension == true) ? DocumentUtils.DocumentRef.getDOM(fo) : null;  
            
            final Element documentElement = (dom != null) ? dom.getDocumentElement() : null;            
            return isRightExtension && documentElement != null && (documentElement.getNamespaceURI() != null);/*Convertors.canRead(documentElement)*/
        }

        final String getFileExtension() {
            return CONVERTOR_EXTENSION;
        }

        final ObjectBinding getObjectBinding(final BasicContext ctx, final FileObject fo) {
            return new ObjectBindingImpl(fo);
        }
    }

    private static final class WriterImpl extends ObjectBinding.Writer {
        final boolean canWrite(final Object obj) {
            return Convertors.canWrite(obj);
        }

        final void write(final FileObject fo, final Object obj) throws IOException {
            final Document doc = DocumentUtils.createDocument();
            final Element e = Convertors.write(doc, obj);
            doc.appendChild(e);
            DocumentUtils.writeDocument(fo, doc);
        }

        final String getFileExtension() {
            return CONVERTOR_EXTENSION;
        }
    }

    private static final class ObjectBindingImpl extends ObjectBinding {
        private final DocumentUtils.DocumentRef docRef;

        ObjectBindingImpl(final FileObject fo) {
            super(fo);
            docRef = new DocumentUtils.DocumentRef();
        }

        public final Object createInstance() throws IOException {
            return Convertors.read(docRef.getDocument(getFile()).getDocumentElement());
        }

        public final Object getModuleDescriptor() {
            return new Object() {
                public boolean equals(final Object obj) {
                    if (obj instanceof ConvertorDescriptor) {
                        final ConvertorDescriptor cd = (ConvertorDescriptor) obj;
                        return cd.getElementName().equals(getElement()) && cd.getNamespace().equals(getNamespace());
                    }
                    return false;
                }
            };
        }

        public final boolean isEnabled() {
            return Convertors.canRead(getNamespace(), getElement());
        }

        private String getNamespace() {
            return docRef.getDocument(getFile()).getDocumentElement().getNamespaceURI();
        }

        private String getElement() {
            return docRef.getDocument(getFile()).getDocumentElement().getNodeName();
        }

    }

}
