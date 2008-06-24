/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import org.netbeans.modules.vcscore.VcsProvider;
import org.openide.filesystems.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.RequestProcessor;

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
        vfs = VersioningFileSystem.findFor(getFile());
        FileObject fo = getFile();
        VcsProvider provider = VcsProvider.getProvider(fo);
        String path = FileUtil.getRelativePath(provider.getRoot(), fo);
        return vfs.getVersions().getRevisions(path, refresh);
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
