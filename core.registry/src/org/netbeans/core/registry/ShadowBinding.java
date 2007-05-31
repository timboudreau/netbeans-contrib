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

import org.netbeans.core.registry.ContextImpl;
import org.netbeans.core.registry.ObjectBinding;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;

import java.io.IOException;

/**
 * Provides ObjectBinding.Reader for ObjectBinding implementation.
 * (copy & pasted & refactored from original org.netbeans.core.registry.ObjectBinding)
 */
final class ShadowBinding {
    private static final String SHADOW_EXTENSION = "shadow";//NOI18N   
    static final ObjectBinding.Reader READER = new ObjectBinding.Reader() {
        boolean canRead(FileObject fo) {
            return fo.getExt().equalsIgnoreCase(SHADOW_EXTENSION);
        }

        String getFileExtension() {
            return SHADOW_EXTENSION;
        }

        ObjectBinding getObjectBinding(BasicContext ctx, FileObject fo) {
            return new ObjectBindingImpl((ContextImpl) ctx, fo);
        }
    };

    private ShadowBinding() {
    }

    private static class ObjectBindingImpl extends ObjectBinding {
        private ContextImpl context;

        protected ObjectBindingImpl(ContextImpl context, FileObject fo) {
            super(fo);
            this.context = context;
        }

        public Object createInstance() throws IOException {
            // try to convert shadow file to ObjectRef instance
            FileObject fo = getFile();
            String originalFile = (String) fo.getAttribute("originalFile");//NOI18N
            // if the shadow points for a folder the ObjectRef will be invalid! 
            int i = originalFile.lastIndexOf('/');//NOI18N
            String contextName = "/" + originalFile.substring(0, i);//NOI18N
            String bindingName = originalFile.substring(i + 1);
            i = bindingName.lastIndexOf('.');
            if (i > 0) {
                bindingName = bindingName.substring(0, i);
            }
            return SpiUtils.createObjectRef(context.getRootContextImpl(), contextName, bindingName);
        }

        public boolean isEnabled() {
            return (getFile() != null && getFile().isValid());
        }
    }

}

