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

package org.netbeans.modules.vcscore.util;

import java.beans.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import org.openide.text.*;
/**
 *
 * @author  Martin Entlicher
 * @version 1.0
 * This class supports opening a disk file in the Editor as read-only.
 */
public class FileEditorSupport extends CloneableEditorSupport {

    private File file;
    private FileEnvironment env;

    /** Creates new FileEditorSupport */
    public FileEditorSupport(File file, FileEnvironment env) {
        super(env);
        env.setFileEditorSupport(this);
        this.file = file;
        this.env = env;
    }
    
    public void addCloseListener(TopComponentCloseListener listener) {
        env.addCloseListener(listener);
    }
    
    public String messageName() {
        return file.getName();
    }
    
    public String messageOpened() {
        return null;
    }
    
    public String messageOpening() {
        return null;
    }
    
    public String messageSave() {
        return "";
    }
    
    public String messageToolTip() {
        return "";
    }
    
    protected boolean canClose() {
        boolean can = super.canClose();
        if (can) {
            for(Iterator it = env.getCloseListeners().iterator(); it.hasNext(); ) {
                ((TopComponentCloseListener) it.next()).closing();
            }
        }
        return can;
    }

    public static class FileEnvironment extends Object implements CloneableEditorSupport.Env {//, Externalizable {

        private File file;
        private String mimeType;
        private ArrayList closeListeners = new ArrayList();
        private transient FileEditorSupport editorSupport = null;
        
        static final long serialVersionUID =2366777428924127835L;

        public FileEnvironment(File file, String mimeType) {
            this.file = file;
            this.mimeType = mimeType;
        }
        
        void addCloseListener(TopComponentCloseListener listener) {
            closeListeners.add(listener);
        }
        
        ArrayList getCloseListeners() {
            return closeListeners;
        }
    
        void setFileEditorSupport(FileEditorSupport editorSupport) {
            this.editorSupport = editorSupport;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public java.util.Date getTime() {
            return new java.util.Date(file.lastModified());
        }
        
        public InputStream inputStream() throws IOException {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException exc) {
                throw new IOException(exc.getMessage());
            }
            return in;
        }
        
        public OutputStream outputStream() throws IOException {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException exc) {
                throw new IOException(exc.getMessage());
            }
            return out;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }
        
        public void addVetoableChangeListener(VetoableChangeListener l) {
        }
        
        public org.openide.windows.CloneableOpenSupport findCloneableOpenSupport() {
            if (editorSupport != null) {
                return editorSupport;
            } else {
                if (file.exists())
                    return new FileEditorSupport(file, this); // create new support after deserialization when the file exists.
                else
                    return null; // do not try to create Editor after deserialization when the file does not exist.
            }
        }
        
        public boolean isModified() {
            return false;
        }
        
        public boolean isValid() {
            return editorSupport != null;
        }
        
        public void markModified() throws IOException {
            throw new IOException();
        }
        
        public void removePropertyChangeListener(PropertyChangeListener l) {
        }
        
        public void removeVetoableChangeListener(VetoableChangeListener l) {
        }
        
        public void unmarkModified() {
        }
        
    }
}
