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

/*
 * IOREditorSupport.java
 *
 * Created on November 13, 2000, 5:42 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.*;
import org.openide.cookies.*;
import org.openide.text.*;
import org.openide.filesystems.*;
import org.openide.windows.CloneableOpenSupport;
/**
 *
 * @author  tzezula
 * @version
 */
public final class IOREditorSupport extends DataEditorSupport implements OpenCookie, EditCookie, EditorCookie, PrintCookie {

    private static class Environment extends DataEditorSupport.Env {

        private SaveSupport saveCookie = null;
        
        private class SaveSupport implements SaveCookie {
            public void save () throws java.io.IOException {
                ((IOREditorSupport)findCloneableOpenSupport()).saveDocument();
                getDataObject().setModified (false);
            }
        }
        
        public Environment (IORDataObject obj) {
            super (obj);
        }
        
        protected FileObject getFile () {
            return this.getDataObject().getPrimaryFile();
        }
        
        protected FileLock takeLock () throws java.io.IOException {
            return this.getFile().lock();
        }
        
        public CloneableOpenSupport findCloneableOpenSupport () {
            return (CloneableEditorSupport) ((IORDataObject)this.getDataObject()).getCookie(EditorCookie.class);
        }
        
        public void addSaveCookie () {
            IORDataObject iorData = (IORDataObject) this.getDataObject();
            if (iorData.getCookie (SaveCookie.class) == null) {
                if (this.saveCookie == null)
                    this.saveCookie = new SaveSupport ();
                iorData.getCookieSet0().add (this.saveCookie);
                iorData.setModified (true);
            }
        }
        
        public void removeSaveCookie () {
            IORDataObject iorData = (IORDataObject) this.getDataObject();
            if (iorData.getCookie(SaveCookie.class) != null) {
                iorData.getCookieSet0().remove (this.saveCookie);
                iorData.setModified (false);
            }
        }
    }

    /** Creates new IOREditorSupport */
    public IOREditorSupport(IORDataObject dataObject) {
        super (dataObject, new Environment(dataObject));
        setMIMEType ("text/plain"); // NOI18N
    }
    
    
    protected boolean notifyModified () {
        if (!super.notifyModified())
            return false;
        ((Environment)this.env).addSaveCookie();
        return true;
    }
    
    
    protected void notifyUnmodified () {
        super.notifyUnmodified();
        ((Environment)this.env).removeSaveCookie();
    }
    
}
