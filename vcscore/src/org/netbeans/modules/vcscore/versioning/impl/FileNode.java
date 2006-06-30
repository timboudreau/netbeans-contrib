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

package org.netbeans.modules.vcscore.versioning.impl;

import org.openide.filesystems.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.versioning.*;

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
        super(new RevisionChildren(null), file);
        //this.file = file;
        RevisionChildren children = (RevisionChildren) getChildren();
        RevisionChildren.NotificationListener childrenNotificationListener = new ChildrenNotificationListener();
        children.addNotificationListener(childrenNotificationListener);
        
        propListener = new PropertyListenerImpl();
        this.addPropertyChangeListener(propListener);
    }

    public Action[] getActions(boolean context) {
        if (Boolean.getBoolean("netbeans.vcsdebug")) {  // NOI18N
            return new SystemAction[]{
                SystemAction.get(org.openide.actions.OpenLocalExplorerAction.class),
                SystemAction.get(RefreshRevisionsAction.class),
                SystemAction.get(org.openide.actions.FileSystemAction.class),
                null,
                SystemAction.get(org.openide.actions.ToolsAction.class),
                SystemAction.get(org.openide.actions.PropertiesAction.class),
                null,
                SystemAction.get(DebugAction.class),
            };
        } else {
            return new SystemAction[]{
                SystemAction.get(org.openide.actions.OpenLocalExplorerAction.class),
                SystemAction.get(RefreshRevisionsAction.class),
                SystemAction.get(org.openide.actions.FileSystemAction.class),
                null,
                SystemAction.get(org.openide.actions.ToolsAction.class),
                SystemAction.get(org.openide.actions.PropertiesAction.class),
            };

        }
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

    public java.awt.Image getOpenedIcon (int type) {
        return this.getIcon(type);
    }
    
    private RevisionList getRevisionList(boolean refresh) {
        VersioningFileSystem vfs;
        vfs = VersioningFileSystem.findFor((FileSystem) getFile().getAttribute(VcsAttributes.VCS_NATIVE_FS));
        return vfs.getVersions().getRevisions((String) getFile().getAttribute(VcsAttributes.VCS_NATIVE_PACKAGE_NAME_EXT), refresh);
    }

    private final Object childrenRefreshingLock = new Object();

    public void refreshRevisions() {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                synchronized (childrenRefreshingLock) {
                    RevisionChildren children = (RevisionChildren) getChildren();
                    RevisionList rList = null;
                    try {
                        children.setList(null); // To have "Please Wait..." child
                        rList = getRevisionList(true);
                        if (rList == null) rList = new RevisionList();
                        rList.setFileObject(getFile());
                        String revision = getRevision();
                        if (revision != null) {
                            for (Iterator it = rList.iterator(); it.hasNext();) {
                                RevisionItem item = (RevisionItem) it.next();
                                item.setCurrent(revision.equals(item.getRevisionVCS()));
                            }
                        }
                    } finally {
                        if (rList == null) rList = new RevisionList();
                        RevisionChildren newChildren = rList.getChildrenFor(null);
                        if (!newChildren.equals(children)) {
                            setChildren(newChildren);
                            children = newChildren;
                        }
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
