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

package org.netbeans.modules.vcscore.versioning.impl;

import org.openide.filesystems.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.RequestProcessor;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.ErrorManager;

import org.netbeans.modules.vcscore.versioning.*;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;

import javax.swing.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Node representing one versioned file. It adds
 * actions, cookies, icon, display name and property
 * sheet all based on background file. It does not
 * support any clipboard operations.
 *
 * @author Petr Kuzel
 * @author Martin Entlicher
 */
final class FileNode extends FolderNode implements RefreshRevisionsCookie {

    private PropertyListenerImpl propListener;

    public FileNode(FileObject file) {
        super(new NumDotRevisionChildren(null), file);
        //this.file = file;
        RevisionChildren children = (RevisionChildren) getChildren();
        RevisionChildren.NotificationListener childrenNotificationListener = new ChildrenNotificationListener();
        children.addNotificationListener(childrenNotificationListener);
        
        propListener = new PropertyListenerImpl();
        this.addPropertyChangeListener(propListener);
        
        getCookieSet().add(this);
    }

    public Action[] getActions(boolean context) {
        return new SystemAction[]{
            SystemAction.get(org.openide.actions.OpenLocalExplorerAction.class),
            SystemAction.get(RefreshRevisionsAction.class),
            SystemAction.get(org.openide.actions.FileSystemAction.class),
            null,
            SystemAction.get(org.openide.actions.ToolsAction.class),
            SystemAction.get(org.openide.actions.PropertiesAction.class)
        };
    }

    public java.awt.Image getIcon (int type) {
        java.awt.Image img = getBlankIcon(type);
        try {
            Set files = Collections.singleton(getFile());
            img = getFile().getFileSystem().getStatus().annotateIcon(img, type, files);
        } catch (FileStateInvalidException e) {
        }
        return img;
    }

    private RevisionList getRevisionList(boolean refresh) {
        VersioningFileSystem vfs;
        try {
            vfs = VersioningFileSystem.findFor(getFile().getFileSystem());
            return vfs.getVersions().getRevisions(getFile().getPath(), refresh);
        } catch (FileStateInvalidException exc) {
            return null;
        }
    }

    private final Object childrenRefreshingLock = new Object();

    // XXX it should be RevisionChildren responsibility
    public void refreshRevisions() {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                synchronized (childrenRefreshingLock) {
                    RevisionChildren children = (RevisionChildren) getChildren();
                    RevisionList rList = null;
                    try {
                        children.setList(null); // To have "Please Wait..." child
                        rList = getRevisionList(true);//vsystem.getVersions().getRevisions(AbstractVcsFile.this.toString());
                        if (rList == null) rList = new NumDotRevisionList();
                        rList.setFileObject(getFile());
                        String revision = getRevision();
                        if (revision != null) {
                            for (Iterator it = rList.iterator(); it.hasNext();) {
                                RevisionItem item = (RevisionItem) it.next();
                                item.setCurrent(revision.equals(item.getRevisionVCS()));
                            }
                        }
                    } finally {
                        if (rList == null) rList = new NumDotRevisionList();
                        children.setList(rList);
                    }
                }
            }
        });
    }

    private class ChildrenNotificationListener implements RevisionChildren.NotificationListener {

        public void notifyAdded() {
            refreshRevisions();
            //addPropertyChangeListener(new RevisionChangeListener(children));
        }

        public void notifyRemoved() {
        }

    }

    // XXX not sure about original intent, it's attached to itself
    private class PropertyListenerImpl implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent ev) {
            if (PROP_REVISION.equals(ev.getPropertyName())) {
                String revision = (String) ev.getNewValue();
                RevisionList list = ((RevisionChildren) getChildren()).getList();
                if (list != null) {
                    synchronized (list) {
                        if (revision == null) {
                            for (Iterator it = list.iterator(); it.hasNext();) {
                                RevisionItem item = (RevisionItem) it.next();
                                item.setCurrent(false);
                            }
                        } else {
                            for (Iterator it = list.iterator(); it.hasNext();) {
                                RevisionItem item = (RevisionItem) it.next();
                                item.setCurrent(revision.equals(item.getRevision()));
                            }
                        }
                    }
                }
            }
        }
    }
}
