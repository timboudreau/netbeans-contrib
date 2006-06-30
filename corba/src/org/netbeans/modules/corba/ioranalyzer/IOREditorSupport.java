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
