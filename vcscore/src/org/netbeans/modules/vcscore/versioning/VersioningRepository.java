/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.openide.util.Lookup;

import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;

/**
 *
 * @author  Martin Entlicher
 */
public final class VersioningRepository extends Object implements java.io.Serializable {
    
    private static VersioningRepository repository;
    
    private ArrayList verSystems;
    private ArrayList verSystemsCopy;
    
    private Hashtable names;

    private transient EventListenerList listenerList = new EventListenerList();

    /** property listener on systemName property of file system */
    private java.beans.PropertyChangeListener propListener = new java.beans.PropertyChangeListener () {
                /** @param ev event with changes */
                public void propertyChange (java.beans.PropertyChangeEvent ev) {
                    if (ev.getPropertyName ().equals ("systemName")) { //NOI18N
                        // assign the property to new name
                        String ov = (String)ev.getOldValue ();
                        String nv = (String)ev.getNewValue ();
                        VersioningFileSystem fs = (VersioningFileSystem)ev.getSource ();
                        // when a file system is valid then it is attached to a name
                        synchronized (this) {
                            names.remove (ov);
                            // register name of the file system
                            names.put (nv, fs);
                        }
                    }
                }
            };

    private static final long serialVersionUID = 8047724018983158285L;
    
    /** Creates new VersioningRepository */
    private VersioningRepository() {
        verSystems = new ArrayList();
        names = new Hashtable();
    }
    
    public static synchronized VersioningRepository getRepository() {
        if (repository == null) {
            initRepository();
        }
        return repository;
    }
    
    private static synchronized void initRepository() {
        if (repository != null) return;
        Lookup l = Lookup.getDefault();
        repository = (VersioningRepository) l.lookup(org.netbeans.modules.vcscore.versioning.VersioningRepository.class);
        if (repository == null) {
            repository = new VersioningRepository();
        }
    }
    
    public final void addVersioningFileSystem(VersioningFileSystem vfs) {
        boolean fireIt;
        boolean firstFSAdded = false;
        synchronized (this) {
            String systemName = vfs.getSystemName();
            if (!names.containsKey(systemName)) {
                if (verSystems.size() == 0) {
                    firstFSAdded = true;
                }
                verSystems.add(vfs);
                verSystemsCopy = new ArrayList(verSystems);
                // mark as a listener on changes in the file system
                vfs.addPropertyChangeListener (propListener);
                names.put(systemName, vfs);
                fireIt = true;
                vfs.addNotify();
            } else fireIt = false;
        }
        if (fireIt) {
            fireVerSystem(vfs, true);
        }
        if (firstFSAdded) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    VersioningExplorer.getRevisionExplorer().open();
                }
            });
        }
    }
    
    public final void removeVersioningFileSystem(VersioningFileSystem vfs) {
        boolean fireIt;
        synchronized (this) {
            String systemName = vfs.getSystemName();
            if (names.containsKey(systemName)) {
                verSystems.remove(vfs);
                verSystemsCopy = new ArrayList(verSystems);
                vfs.removePropertyChangeListener (propListener);
                names.remove(systemName);
                fireIt = true;
                vfs.removeNotify();
            } else fireIt = false;
        }
        if (fireIt) {
            fireVerSystem(vfs, false);
        }
    }
    
    public final List getVersioningFileSystems() {
        ArrayList vfsl = new ArrayList(verSystems);
        return vfsl;
    }
    
    public final synchronized VersioningFileSystem getSystem(String systemName) {
        return (VersioningFileSystem) names.get(systemName);
    }
    
    public void addRepositoryListener(VersioningRepositoryListener listener) {
        synchronized (listenerList) {
            listenerList.add(VersioningRepositoryListener.class, listener);
        }
    }
    
    public void removeRepositoryListener(VersioningRepositoryListener listener) {
        synchronized (listenerList) {
            listenerList.remove(VersioningRepositoryListener.class, listener);
        }
    }
    
    private void fireVerSystem(VersioningFileSystem vfs, boolean added) {
        VersioningRepositoryListener[] listeners;
        synchronized (listenerList) {
            listeners = (VersioningRepositoryListener[]) listenerList.getListeners(VersioningRepositoryListener.class);
        }
        VersioningRepositoryEvent ev = new VersioningRepositoryEvent(this, vfs, added);
        if (added) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].versioningSystemAdded(ev);
            }
        } else {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].versioningSystemRemoved(ev);
            }
        }
    }
    
    public String toString() {
        StringBuffer str;
        synchronized (this) {
            str = new StringBuffer("VersioningRepository: names = "+names+", names.size() = "+names.size());
            for (java.util.Iterator it = names.keySet().iterator(); it.hasNext(); ) {
                String systemName = (String) it.next();
                str.append("systemName = "+systemName+", filesystem = "+names.get(systemName));
            }
        }
        return str.toString();
    }

}
