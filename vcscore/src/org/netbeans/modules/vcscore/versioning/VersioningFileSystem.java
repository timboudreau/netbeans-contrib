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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.EventListenerList;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.search.VcsSearchTypeFileSystem;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class VersioningFileSystem extends AbstractFileSystem implements VcsSearchTypeFileSystem {

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
        
    /** It should return all possible VCS states in which the files in the filesystem
     * can reside.
     */
    public String[] getPossibleFileStatuses() {
        FileStatusProvider statusProvider = getFileStatusProvider();
        if (statusProvider != null) {
            return (String[]) statusProvider.getPossibleFileStatusesTable().values().toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    /** returns the status for a dataobject. If it matches the status 
     * the user selected in the find dialog (list of all possible states), then
     * it's found and displayed.
     */
    public String getStatus(org.openide.loaders.DataObject dObject) {
        if (dObject instanceof org.netbeans.modules.vcscore.versioning.impl.VersioningDataObject) {
            org.netbeans.modules.vcscore.versioning.impl.VersioningDataObject vdo = 
                (org.netbeans.modules.vcscore.versioning.impl.VersioningDataObject) dObject;
            return vdo.getStatus();
        } else {
            return "";
        }
    }

    /**
     * Perform refresh of status information on all children of a directory
     * @param path the directory path
     * @param recursivey whether to refresh recursively
     */
    public void statusChanged (String path, boolean recursively) {
        //D.deb("statusChanged("+path+")"); // NOI18N
        FileObject fo = findResource(path);
        if (fo == null) return;
        //D.deb("I have root = "+fo.getName()); // NOI18N
        Enumeration enum = fo.getChildren(recursively);
        HashSet hs = new HashSet();
        while(enum.hasMoreElements()) {
            fo = (FileObject) enum.nextElement();
            hs.add(fo);
            //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
        }
        Set s = Collections.synchronizedSet(hs);
        fireFileStatusChanged (new FileStatusEvent(this, s, true, true));
        //checkScheduledStates(s);
    }
    
    /**
     * Perform refresh of status information of a file
     * @param name the full file name
     */
    public void statusChanged (String name) {
        FileObject fo = findResource(name);
        //System.out.println("findResource("+name+") = "+fo);
        if (fo == null) return;
        fireFileStatusChanged (new FileStatusEvent(this, fo, true, true));
        //checkScheduledStates(Collections.singleton(fo));
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
