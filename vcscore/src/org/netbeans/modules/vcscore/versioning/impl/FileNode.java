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

import org.openide.nodes.*;
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
final class FileNode extends AbstractNode {

    public static final String PROP_STATUS = "status";
    public static final String PROP_LOCKER = "locker";
    public static final String PROP_REVISION = "revision";
    public static final String PROP_STICKY = "sticky";
    public static final String PROP_ALL_STATES = "all";

    // cached properties

    private String status = null;
    private String locker = null;
    private String revision = null;
    private String sticky = null;

    private FileStatusListener vcsFileStatusListener;

    private PropertyListenerImpl propListener;

    private final FileObject file;

    public FileNode(FileObject file) {
        super(new NumDotRevisionChildren(null));
        this.file = file;
        try {
            FileSystem fs = file.getFileSystem();
            vcsFileStatusListener = new VCSFileStatusListener();
            fs.addFileStatusListener((FileStatusListener) WeakListeners.create(FileStatusListener.class, vcsFileStatusListener, fs));
        } catch (FileStateInvalidException exc) {
            ErrorManager err = ErrorManager.getDefault();
            err.notify(ErrorManager.INFORMATIONAL, exc);
            return;
        }

        RevisionChildren children = (RevisionChildren) getChildren();
        RevisionChildren.NotificationListener childrenNotificationListener = new ChildrenNotificationListener();
        children.addNotificationListener(childrenNotificationListener);
        propListener = new PropertyListenerImpl();
        this.addPropertyChangeListener(propListener);
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

    public Cookie getCookie(Class type) {
        // mimics DataNode because some actions heavily depends on DataObject cookie existence
        if (type.isAssignableFrom(DataObject.class)) {
            try {
                return DataObject.find(file);
            } catch (DataObjectNotFoundException e) {
                // ignore, call super later on
            }
        }
        return super.getCookie(type);
    }

    public String getName() {
        return file.getNameExt();
    }

    public String getDisplayName() {
        String s;
        try {
            Set target = Collections.singleton(file);
            s = file.getFileSystem().getStatus().annotateName(file.getNameExt(), target);
        } catch (FileStateInvalidException exc) {
            s = super.getDisplayName();
        }
        return s;
    }
    
    public String getHtmlDisplayName() {
        String s;
        try {
            Set target = Collections.singleton(file);
            FileSystem.Status fsStatus = file.getFileSystem().getStatus();
            if (fsStatus instanceof FileSystem.HtmlStatus) {
                s = ((FileSystem.HtmlStatus) fsStatus).annotateNameHtml(file.getNameExt(), target);
            } else {
                s = fsStatus.annotateName(file.getNameExt(), target);
            }
        } catch (FileStateInvalidException exc) {
            s = super.getHtmlDisplayName();
        }
        return s;
    }

    public java.awt.Image getIcon (int type) {
        java.awt.Image img = super.getIcon(type);
        try {
            Set files = Collections.singleton(file);
            img = file.getFileSystem().getStatus().annotateIcon(img, type, files);
        } catch (FileStateInvalidException e) {
        }
        return img;
    }

    /**
     * Create the property sheet.
     *
     * @return the sheet
     */
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        ps.put(new PropertySupport.ReadOnly(FileNode.PROP_STATUS,
                String.class,
                NbBundle.getMessage(FileNode.class, "PROP_Status"),
                NbBundle.getMessage(FileNode.class, "HINT_Status")) {
            public Object getValue() {
                String value = getStatus();
                return (value == null) ? "" : value;
            }
        });
        ps.put(new PropertySupport.ReadOnly(FileNode.PROP_REVISION,
                String.class,
                NbBundle.getMessage(FileNode.class, "PROP_Revision"),
                NbBundle.getMessage(FileNode.class, "HINT_Revision")) {
            public Object getValue() {
                String value = getRevision();
                return (value == null) ? "" : value;
            }
        });
        ps.put(new PropertySupport.ReadOnly(FileNode.PROP_STICKY,
                String.class,
                NbBundle.getMessage(FileNode.class, "PROP_Sticky"),
                NbBundle.getMessage(FileNode.class, "HINT_Sticky")) {
            public Object getValue() {
                String value = getSticky();
                return (value == null) ? "" : value;
            }
        });
        sheet.put(ps);

        Sheet.Set expert = Sheet.createExpertSet();
        expert.put(new PropertySupport.ReadOnly(FileNode.PROP_LOCKER,
                String.class,
                NbBundle.getMessage(FileNode.class, "PROP_Locker"),
                NbBundle.getMessage(FileNode.class, "HINT_Locker")) {
            public Object getValue() {
                String value = getLocker();
                return (value == null) ? "" : value;
            }
        });
        sheet.put(expert);

        return sheet;
    }

    public boolean canCopy() {
        return false;
    }

    public boolean canCut() {
        return false;
    }

    public boolean canDestroy() {
        return false;
    }

    public boolean canRename() {
        return false;
    }

    private RevisionList getRevisionList(boolean refresh) {
        VersioningFileSystem vfs;
        try {
            vfs = VersioningFileSystem.findFor(file.getFileSystem());
            return vfs.getVersions().getRevisions(file.getPath(), refresh);
        } catch (FileStateInvalidException exc) {
            return null;
        }
    }

    private FileStatusProvider getFileStatusProvider() {
        VersioningFileSystem vfs;
        try {
            vfs = VersioningFileSystem.findFor(file.getFileSystem());
            return vfs.getFileStatusProvider();
        } catch (FileStateInvalidException exc) {
            return null;
        }
    }

    /**
     * Getter for property status.
     *
     * @return Value of property status.
     */
    public String getStatus() {
        if (status == null) {
            FileStatusProvider statusProvider = getFileStatusProvider();
            if (statusProvider == null) return null;
            status = statusProvider.getFileStatus(file.getPath());
        }
        return status;
    }

    /**
     * Getter for property locker.
     *
     * @return Value of property locker.
     */
    public String getLocker() {
        if (locker == null) {
            FileStatusProvider statusProvider = getFileStatusProvider();
            if (statusProvider == null) return null;
            locker = statusProvider.getFileLocker(file.getPath());
        }
        return locker;
    }

    /**
     * Getter for property revision.
     *
     * @return Value of property revision.
     */
    public String getRevision() {
        if (revision == null) {
            FileStatusProvider statusProvider = getFileStatusProvider();
            if (statusProvider == null) return null;
            revision = statusProvider.getFileRevision(file.getPath());
        }
        return revision;
    }

    /**
     * Getter for property sticky.
     *
     * @return Value of property sticky.
     */
    public String getSticky() {
        if (sticky == null) {
            FileStatusProvider statusProvider = getFileStatusProvider();
            if (statusProvider == null) return null;
            sticky = statusProvider.getFileSticky(file.getPath());
        }
        return sticky;
    }

    private class VCSFileStatusListener implements FileStatusListener {
        public void annotationChanged(FileStatusEvent ev) {
            if (ev.hasChanged(file)) {
                FileStatusProvider statusProvider = getFileStatusProvider();
                if (statusProvider == null) return;
                String name = file.getPath();
                String newState = statusProvider.getFileStatus(name);
                String oldState;
                if (status == null && newState != null || status != null && !status.equals(newState)) {
                    oldState = status;
                    status = newState;
                    firePropertyChange(PROP_STATUS, oldState, newState);
                }
                newState = statusProvider.getFileLocker(name);
                if (locker == null && newState != null || locker != null && !locker.equals(newState)) {
                    oldState = locker;
                    locker = newState;
                    firePropertyChange(PROP_LOCKER, oldState, newState);
                }
                newState = statusProvider.getFileRevision(name);
                if (revision == null && newState != null || revision != null && !revision.equals(newState)) {
                    oldState = revision;
                    revision = newState;
                    firePropertyChange(PROP_REVISION, oldState, newState);
                }
                newState = statusProvider.getFileSticky(name);
                if (sticky == null && newState != null || sticky != null && !sticky.equals(newState)) {
                    oldState = sticky;
                    sticky = newState;
                    firePropertyChange(PROP_STICKY, oldState, newState);
                }

                // XXX why this forward
                if (ev.isNameChange()) {
                    fireDisplayNameChange(null, null);
                } else if (ev.isIconChange()) {
                    fireIconChange();
                }
            }
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
                        rList.setFileObject(file);
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
