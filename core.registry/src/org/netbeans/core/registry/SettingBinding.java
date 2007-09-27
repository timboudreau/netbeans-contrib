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

import org.netbeans.core.registry.oldformats.SerialDataConvertor;
import org.netbeans.spi.registry.BasicContext;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.Serializable;

/**
 * Provides ObjectBinding.Reader and ObjectBinding.Writer for ObjectBinding implementation.
 * (copy & pasted & refactored from original org.netbeans.core.registry.ObjectBinding)
 */
final class SettingBinding {
    private static final String SETTINGS_EXTENSION = "settings";//NOI18N    
    static final ObjectBinding.Reader READER = new ReaderImpl();
    static final ObjectBinding.Writer WRITER = new WriterImpl();

    private SettingBinding() {
    }

    private static class ReaderImpl extends ObjectBinding.Reader {
        boolean canRead(FileObject fo) {
            boolean isRightExtension = fo.getExt().equalsIgnoreCase(SETTINGS_EXTENSION) || fo.getExt().equalsIgnoreCase("xml");
            Document dom = (isRightExtension) ? DocumentUtils.DocumentRef.getDOM(fo) : null;
            
            return isRightExtension && dom != null && SerialDataConvertor.isSettingsFormat(dom.getDocumentElement());
        }

        String getFileExtension() {
            return SETTINGS_EXTENSION;
        }

        ObjectBinding getObjectBinding(BasicContext ctx, FileObject fo) {
            return new ObjectBindingImpl(fo);
        }
    }

    private static class WriterImpl extends ObjectBinding.Writer {
        boolean canWrite(Object obj) {
            return (obj instanceof Serializable);
        }

        void write(FileObject fo, Object obj) throws IOException {
            SerialDataConvertor sdc = new SerialDataConvertor();
            sdc.write(fo, obj);
        }

        String getFileExtension() {
            return SETTINGS_EXTENSION;
        }
    }

    private static class ObjectBindingImpl extends ObjectBinding {
        private DocumentUtils.DocumentRef docRef;

        ObjectBindingImpl(FileObject fo) {
            super(fo);
            docRef = new DocumentUtils.DocumentRef();
        }

        public Object createInstance() throws IOException {
            Object retVal = null;
            Document d = docRef.getDocument(getFile());
            try {
                retVal = SerialDataConvertor.read(d.getDocumentElement(), getFile());
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getLocalizedMessage());
            }

            return retVal;
        }

        public boolean isEnabled() {
            String moduleName = getModuleName();
            return (moduleName == null) || StateUpdater.getDefault().isModuleEnabled(moduleName);
        }

        public Object getModuleDescriptor() {
            return new Object() {
                public boolean equals(Object obj) {
                    if (obj instanceof String) {
                        String modName = (String)obj;
                        String moduleName = getModuleName();
                        return moduleName == null ? false : moduleName.equals(modName);
                    }
                    return false;
                }
            };
        }

        private String getModuleName() {
            String moduleName = SerialDataConvertor.getModuleCodeName(docRef.getDocument(getFile()).getDocumentElement());
            return (moduleName != null) ? StateUpdater.cutOffVersion(moduleName) : null;
        }
    }

}
