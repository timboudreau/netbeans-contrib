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

package org.netbeans.modules.vcscore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.netbeans.spi.queries.VisibilityQueryImplementation;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;
import org.openide.util.WeakSet;

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
