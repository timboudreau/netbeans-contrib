/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning;

import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import javax.swing.event.EventListenerList;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.caching.FileStatusProvider;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class VersioningFileSystem extends AbstractFileSystem {

    private static final SystemAction[] NO_ACTIONS = new SystemAction[0];
    
    //protected AbstractFileSystem.List list;
    //protected AbstractFileSystem.Info info;
    //protected FileSystem.Status status;
    //protected VersioningFileSystem.Versions versions;
    
    private transient EventListenerList listenerList = new EventListenerList();
    //private transient HashMap revisionListenerMap = new HashMap();
    
    public static interface Versions extends Serializable {
        
        public RevisionList getRevisions(String name, boolean refresh);
        
        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException;
        
    }
    
    private static final long serialVersionUID = 1437726745709092169L;

    /*
    public VersioningFileSystem() {
        list = getList();
        info = getInfo();
        change = null;
        attr = null;
    }
     */
    
    //public abstract AbstractFileSystem.List getList();
    
    //public abstract AbstractFileSystem.Info getInfo();
    
    //public abstract FileSystem.Status getStatus();
    
    public abstract VersioningFileSystem.Versions getVersions();
    
    /**
     * Create the Children for revisions.
     * @param list the container of RevisionItems or null, when the revisions
     *        are not loaded yet
     */
    //public abstract RevisionChildren createRevisionChildren(RevisionList list);
    
    /**
     * Get the IDE file system associated with this Versioning file system
     * @return the file system or null
     */
    protected FileSystem getFileSystem() {
        return null;
    }
    
    /**
     * Get the status provider. All file status information
     * is retrieved from this provider.
     * @return the status provider or <code>null<code>, when no provider
     *         is defined.
     */
    public FileStatusProvider getFileStatusProvider() {
        return null;
    }
        
    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        return NO_ACTIONS;
    }
    
    /** Finds existing file when its resource name is given.
     * It differs from {@link findResource(String)} method in the fact, that
     * <CODE>null</CODE> is returned if the resource was not created yet.
     * No new file objects are created when looking for the resource.
     * The name has the usual format for the {@link ClassLoader#getResource(String)}
     * method. So it may consist of "package1/package2/filename.ext".
     * If there is no package, it may consist only of "filename.ext".
     *
     * @param name resource name
     *
     * @return VcsFileObject that represents file with given name or
     *   <CODE>null</CODE> if the file was not created yet.
     */
    public FileObject findExistingResource (String name) {
        return findResource(name); // TODO
    }
    //public void markImportant(String name, boolean is);
    
    public final void addVcsFileStatusListener(VcsFileStatusListener listener) {
        synchronized (listenerList) {
            listenerList.add(VcsFileStatusListener.class, listener);
        }
    }
    
    public final void removeVcsFileStatusListener(VcsFileStatusListener listener) {
        synchronized (listenerList) {
            listenerList.remove(VcsFileStatusListener.class, listener);
        }
    }
    
    protected final void fireVcsFileStatusChanged(VcsFileStatusEvent ev) {
        VcsFileStatusListener[] listeners;
        synchronized (listenerList) {
            listeners = (VcsFileStatusListener[]) listenerList.getListeners(VcsFileStatusListener.class);
        }
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].vcsStatusChanged(ev);
        }
    }
    
}
