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

package org.netbeans.modules.mount;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

/**
 * Reports all mount points as Java package roots.
 * @author Jesse Glick
 */
final class MountSources implements Sources, ChangeListener {
    
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    
    public MountSources() {
        MountList.DEFAULT.addChangeListener(WeakListeners.change(this, MountList.DEFAULT));
    }

    public SourceGroup[] getSourceGroups(String type) {
        if (!type.equals(Sources.TYPE_GENERIC) && !type.equals(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            return new SourceGroup[0];
        }
        List/*<SourceGroup>*/ groups = new ArrayList();
        FileObject[] roots = MountList.DEFAULT.getMounts();
        for (int i = 0; i < roots.length; i++) {
            groups.add(new Group(roots[i]));
        }
        if (type.equals(Sources.TYPE_GENERIC)) {
            groups.add(GenericSources.group(DummyProject.getInstance(), DummyProject.getInstance().getProjectDirectory(), "mount", "Mounting Work Area", null, null));
        }
        return (SourceGroup[]) groups.toArray(new SourceGroup[groups.size()]);
    }

    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fireChange() {
        ChangeListener[] ls;
        synchronized (this) {
            if (listeners.isEmpty()) {
                return;
            }
            ls = (ChangeListener[]) listeners.toArray(new ChangeListener[listeners.size()]);
        }
        ChangeEvent ev = new ChangeEvent(this);
        for (int i = 0; i < ls.length; i++) {
            ls[i].stateChanged(ev);
        }
    }
    
    public void stateChanged(ChangeEvent ev) {
        fireChange();
    }
    
    private static final class Group implements SourceGroup {
        
        private final FileObject root;
        
        public Group(FileObject root) {
            this.root = root;
        }

        public FileObject getRootFolder() {
            return root;
        }

        public String getName() {
            try {
                return root.getURL().toExternalForm();
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);
                return ""; // NOI18N
            }
        }

        public String getDisplayName() {
            return FileUtil.getFileDisplayName(root);
        }

        public Icon getIcon(boolean opened) {
            return null;
        }

        public boolean contains(FileObject file) throws IllegalArgumentException {
            return true;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {}

        public void removePropertyChangeListener(PropertyChangeListener listener) {}
        
    }
    
}
