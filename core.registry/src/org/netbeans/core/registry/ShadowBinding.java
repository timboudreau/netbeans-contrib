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

