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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.util.WeakListeners;

/**
 * @author Jan Lahoda
 */
public abstract class LaTeXSourceFactory {

    private List<MainFileListener> listeners;

    public LaTeXSourceFactory() {
        listeners = new ArrayList<MainFileListener>();
    }

    public synchronized void addMainFileListener(MainFileListener l) {
        listeners.add(l);
    }

    public synchronized void removeMainFileListener(MainFileListener l) {
        listeners.remove(l);
    }
    
    protected void fireMainFileEvent(Object mainFile, boolean isMainFile, boolean isKnownFile) {
        List clone;
        
        synchronized (this) {
            clone = new ArrayList<MainFileListener>(listeners);
        }
        
        MainFileEvent evt = null;
        
        for (Iterator i = clone.iterator(); i.hasNext(); ) {
            MainFileListener l = (MainFileListener) i.next();
            
            if (evt == null)
                evt = new MainFileEvent(this, mainFile, isMainFile, isKnownFile);
            
            l.mainFileChanged(evt);
        }
    }
    
    public abstract boolean supports(Object file);
    
    public abstract FileObject findMainFile(FileObject file);
    
    public abstract boolean isKnownFile(Object file);
    
    public abstract boolean isMainFile(Object file);
    
    /**Return collection of known files. This method should return collection of
     * files for which holds <code>get(file) != null</code> for this factory.
     * The implementors should use best-effort approach. If for some or all of the
     * files cannot be determined whether holds <code>get(file) != null</code>,
     * it is legal not to list them in the resulting collection.
     * 
     * @return list of all known files (<code>get(file) != null</code>). Should
     *              never return <code>null</code>.
     */
    public abstract Collection getAllKnownFiles();

    public static interface MainFileListener extends EventListener {
        
        public void mainFileChanged(MainFileEvent evt);
    }
    
    public static class MainFileEvent extends EventObject {
        
        private Object             mainFile;
        private boolean            isMainFile;
        private boolean            isKnownFile;
        
        public MainFileEvent(LaTeXSourceFactory factory, Object mainFile, boolean isMainFile, boolean isKnownFile) {
            super(factory);
            this.mainFile = mainFile;
            this.isMainFile = isMainFile;
            this.isKnownFile = isKnownFile;
        }
        
        public LaTeXSourceFactory getFactory() {
            return (LaTeXSourceFactory) getSource();
        }
        
        public Object getMainFile() {
            return mainFile;
        }
        
        public boolean isMainFile() {
            return isMainFile;
        }
        
        //TODO: currently not fired...
        public boolean isKnownFile() {
            return isKnownFile;
        }
        
    }
    
    public static MainFileListener weakMainFileListener(MainFileListener l, LaTeXSourceFactory source) {
        return WeakListeners.create(MainFileListener.class, l, source);
    }
}

