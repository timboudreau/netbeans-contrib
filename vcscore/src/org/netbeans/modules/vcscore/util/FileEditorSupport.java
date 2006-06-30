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
