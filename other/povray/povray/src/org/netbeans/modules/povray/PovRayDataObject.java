/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * PovRayDataObject.java
 *
 * Created on February 16, 2005, 2:29 PM
 */

package org.netbeans.modules.povray;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.povproject.MainFileProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.util.HelpCtx;
import org.openide.windows.CloneableOpenSupport;

/**
 * Data object representing a pov-ray .pov or .inc scene file. 
 *
 * @author Timothy Boudreau
 */
public class PovRayDataObject extends MultiDataObject {
    
    /** Creates a new instance of PovRayDataObject */
    public PovRayDataObject(FileObject file, MultiFileLoader ldr) throws DataObjectExistsException {
        super(file, ldr);
    }
    
    private WeakReference editorSupport = null;
    
    public Node.Cookie getCookie(Class clazz) {
        if (clazz == LineCookie.class || clazz == EditCookie.class || clazz == EditorCookie.class) {
            return getEditorSupport(true);
        } else {
            return super.getCookie (clazz);
        }
    }
    
    private PovEditorSupport getEditorSupport(boolean create) {
        PovEditorSupport result = null;
        if (editorSupport != null) {
            result = (PovEditorSupport) editorSupport.get();
        }
        
        if (result == null && create) {
            result = new PovEditorSupport (new EditorEnv());
            editorSupport = new WeakReference (result);
        }
        return result;
    }
    
    public Node createNodeDelegate() {
        DataNode node = new PovRayDataNode(this);
        return node;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public FileObject handleMove(DataFolder df) throws IOException {
        FileObject file = getPrimaryFile();
        Project project = FileOwnerQuery.getOwner(file);
        boolean isMainFile = false;
        MainFileProvider provider = null;
        if (project != null) {
            provider = (MainFileProvider) project.getLookup().lookup(MainFileProvider.class);
            if (provider != null) { //could be if we're for some reason in a java project
                isMainFile = provider.getMainFile().equals(file);
            }
        }
        
        FileObject result = super.handleMove(df);
        if (isMainFile) {
            Project project2 = FileOwnerQuery.getOwner(result);
            if (project2 == project && project != null) {
                provider.setMainFile(result);
            } else {
                provider.setMainFile(null);
            }
        }
        return result;
    }
    
    public FileObject handleRename(String name) throws IOException {
        FileObject file = getPrimaryFile();
        Project project = FileOwnerQuery.getOwner(file);
        boolean isMainFile = false;
        MainFileProvider provider = null;
        if (project != null) {
            provider = (MainFileProvider) project.getLookup().lookup(MainFileProvider.class);
            if (provider != null) { //could be if we're for some reason in a java project
                isMainFile = provider.getMainFile().equals(file);
            }
        }
        
        FileObject result = super.handleRename(name);
        if (isMainFile) {
            provider.setMainFile(result);
        }
        return result;
    }
    
    public class PovEditorSupport extends DataEditorSupport implements EditCookie, EditorCookie, LineCookie {
        private final PovRayDataObject.EditorEnv env;
        
        /** Creates a new instance of PovEditorSupport */
        public PovEditorSupport(EditorEnv env) {
            super (PovRayDataObject.this, env);
            this.env = env;
        }

        public PovRayDataObject.EditorEnv getEnv() {
            return env;
        }  
    }    
    
    class EditorEnv implements CloneableEditorSupport.Env {
        public InputStream inputStream () throws IOException {
            return getPrimaryFile().getInputStream();
        }

        public OutputStream outputStream () throws IOException {
            return getPrimaryFile().getOutputStream(getPrimaryFile().lock());
        }

        public Date getTime () {
            return modifiedDate != null ? modifiedDate : 
                PovRayDataObject.this.getPrimaryFile().lastModified();
        }

        public String getMimeType () {
            return "text/x-povray";
        }        
        
        public CloneableOpenSupport findCloneableOpenSupport() {
            return getEditorSupport(true);
        }
        
        private List vetoListeners = Collections.synchronizedList (new ArrayList(3));
        private List pcListeners = Collections.synchronizedList (new ArrayList(3));

        public void addPropertyChangeListener (PropertyChangeListener l) {
            pcListeners.add (l);
        }

        public void removePropertyChangeListener (PropertyChangeListener l) {
            pcListeners.remove(l);
        }

        public void addVetoableChangeListener (VetoableChangeListener l) {
            vetoListeners.add(l);
        }

        public void removeVetoableChangeListener (VetoableChangeListener l) {
            vetoListeners.remove(l);
        }
        
        private void fire(String s, Object old, Object nue) throws PropertyVetoException {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, s, old, nue);
            for (Iterator i=vetoListeners.iterator(); i.hasNext();) {
                VetoableChangeListener veto = (VetoableChangeListener) i.next();
                veto.vetoableChange(evt);
            }
            for (Iterator i=pcListeners.iterator(); i.hasNext();) {
                PropertyChangeListener l = (PropertyChangeListener) i.next();
                l.propertyChange(evt);
            }
        }

        public boolean isValid () {
            return PovRayDataObject.this.isValid();
        }
        
        public boolean isModified () {
            return modifiedDate != null;
        }

        private Date modifiedDate = null;
        public void markModified () throws java.io.IOException {
            Date oldDate = modifiedDate;
            modifiedDate = new Date();
            if (oldDate == null) {
                try {
                    fire (PROP_MODIFIED, Boolean.FALSE, Boolean.TRUE);
                } catch (PropertyVetoException veto) {
                    modifiedDate = oldDate;
                }
            }
        }

        public void unmarkModified () {
            boolean wasModified = modifiedDate != null;
            Date oldDate = modifiedDate;
            modifiedDate = null;
            if (oldDate != null) {
                try {
                    fire (PROP_MODIFIED, Boolean.TRUE, Boolean.FALSE);
                } catch (PropertyVetoException veto) {
                    modifiedDate = oldDate;
                }
            }
        }
        
        public void makeInvalid() throws PropertyVetoException {
            if (isValid()) {
                fire (PROP_VALID, Boolean.TRUE, Boolean.FALSE);
            }
        }
    }
}
