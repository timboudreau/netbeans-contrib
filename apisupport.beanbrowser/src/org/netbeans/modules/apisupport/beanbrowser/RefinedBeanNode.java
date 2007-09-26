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

package org.netbeans.modules.apisupport.beanbrowser;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.swing.Action;
import org.openide.actions.EditAction;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.PrintCookie;
import org.openide.filesystems.FileObject;
import org.openide.nodes.BeanNode;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.actions.SystemAction;
import org.openide.windows.CloneableOpenSupport;

/** The variant of BeanNode to use.
 * Special behavior: supports editing FileObject beans.
 */
public class RefinedBeanNode extends BeanNode {
    
    public RefinedBeanNode(Object bean) throws IntrospectionException {
        super(bean);
        if (bean instanceof FileObject) {
            getCookieSet().add(new FileObjectEditorSupport((FileObject)bean));
        }
    }
    
    public Action[] getActions(boolean context) {
        Action[] a = super.getActions(context);
        if (getBean() instanceof FileObject) {
            Action[] a2 = new Action[a.length + 2];
            a2[0] = SystemAction.get(EditAction.class);
            System.arraycopy(a, 0, a2, 2, a.length);
            return a2;
        } else {
            return a;
        }
        // XXX should offer to open Java class implementing bean?
    }
    
    private static final class FileObjectEditorSupport extends CloneableEditorSupport implements EditCookie, CloseCookie, PrintCookie, EditorCookie.Observable {
        
        private final FileObject fo;
        
        public FileObjectEditorSupport(FileObject fo) {
            super(new FOEnv(fo));
            this.fo = fo;
        }
        
        protected String messageName() {
            return fo.getNameExt();
        }
        
        protected String messageToolTip() {
            return fo.getPath();
        }
        
        protected String messageOpened() {
            return ""; // NOI18N
        }
        protected String messageOpening() {
            return ""; // NOI18N
        }
        protected String messageSave() {
            return ""; // NOI18N
        }
        
        private static final class FOEnv implements CloneableEditorSupport.Env {
            
            private static final long serialVersionUID = 78267653L;
            
            private final FileObject fo;
            
            public FOEnv(FileObject fo) {
                this.fo = fo;
            }
            
            public void addPropertyChangeListener(PropertyChangeListener l) {}
            
            public void addVetoableChangeListener(VetoableChangeListener l) {}
            
            public CloneableOpenSupport findCloneableOpenSupport() {
                return new FileObjectEditorSupport(fo);
            }
            
            public String getMimeType() {
                return fo.getMIMEType();
            }
            
            public Date getTime() {
                return fo.lastModified();
            }
            
            public InputStream inputStream() throws IOException {
                return fo.getInputStream();
            }
            
            public boolean isValid() {
                return fo.isValid();
            }
            
            public void removePropertyChangeListener(PropertyChangeListener l) {
            }
            
            public void removeVetoableChangeListener(VetoableChangeListener l) {
            }
            
            // XXX for now, r/o
            
            public OutputStream outputStream() throws IOException {
                throw new IOException();
            }
            
            public boolean isModified() {
                return false;
            }
            
            public void markModified() throws IOException {
                throw new IOException();
            }
            
            public void unmarkModified() {
            }
            
        }
        
    }
    
}
