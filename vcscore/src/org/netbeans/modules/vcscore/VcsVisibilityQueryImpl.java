/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.netbeans.spi.queries.VisibilityQueryImplementation;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;
import org.openide.util.WeakSet;

import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;

/**
 * VCS visibility query implementation based on VCS filesystems.
 * Files are visible if they are not ignored by the VCS filesystem.
 *
 * @author Martin Entlicher
 */
public class VcsVisibilityQueryImpl implements VisibilityQueryImplementation, PropertyChangeListener {
    
    private EventListenerList listeners = new EventListenerList();
    private WeakSet listenedVFSs = new WeakSet();
    
    /** Create VcsVisibilityQueryImpl */
    public VcsVisibilityQueryImpl() {}
    
    public boolean isVisible(FileObject fo) {
        VersioningFileSystem versioningFS = (VersioningFileSystem) fo.getAttribute(VersioningFileSystem.VERSIONING_NATIVE_FS);
        if (versioningFS != null) {
            File file = FileUtil.toFile(fo);
            return versioningFS.getFileFilter().accept(file.getParentFile(), file.getName());
        }
        VcsFileSystem vcsFS = (VcsFileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (vcsFS == null) return true;
        synchronized (listenedVFSs) {
            if (!listenedVFSs.contains(vcsFS)) {
                listenedVFSs.add(vcsFS);
                vcsFS.addPropertyChangeListener(WeakListeners.propertyChange(this, vcsFS));
            }
        }
        File file = FileUtil.toFile(fo);
        return vcsFS.getFileFilter().accept(file.getParentFile(), file.getName());
    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
    
    private void fireChangeEvent() {
        ChangeEvent che = new ChangeEvent(this);
        EventListener[] ls = listeners.getListeners(ChangeListener.class);
        for (int i = ls.length - 1; i >= 0; i -= 2) {
            ((ChangeListener) ls[i]).stateChanged(che);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (VcsFileSystem.PROP_FILE_FILTER.equals(evt.getPropertyName())) {
            fireChangeEvent();
        }
    }
    
}
