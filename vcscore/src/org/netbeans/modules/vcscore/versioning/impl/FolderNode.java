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

import org.openide.ErrorManager;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.HelpCtx;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.NodeAction;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Set;
import java.util.Collections;

import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;

/**
 * Visualizes folder as much closely to FolderNode as possible
 * without need for data systems layer. It adds
 * actions, cookies, icon, display name and property
 * sheet all based on background file. It does not
 * support any clipboard operations.
 * <p>
 * TODO It adds FileObject into associated lookup that
 * should replace DataObject in cookies. Actions that
 * are driven by DataObject can work wrongly because
 * they typically operate over primary file that can differ
 * from wrapped file (e.g. diff on .form files works over
 * respective .java files).
 *
 * @author Petr Kuzel
 */
class FolderNode extends AbstractNode implements Node.Cookie {

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

    /** The file or folder */
    private final FileObject file;

    private final InstanceContent content;

    // XXX probably undeclatred dependency, copied from loaders.FolderNode
    static final String FOLDER_ICON_BASE =
        "org/openide/loaders/defaultFolder"; // NOI18N


    public FolderNode(FileObject folder) {
        this(new FolderChildren(folder), folder);
        setIconBase(FOLDER_ICON_BASE);
    }
    
    FolderNode(Children ch, FileObject file) {
        this(ch, file, new InstanceContent());
    }

    private FolderNode(Children ch, FileObject file, InstanceContent content) {
        super(ch, new AbstractLookup(content));

        // setup lookup content

        content.add(file);
        content.add(this);
        InstanceContent.Convertor lazyDataObject = new InstanceContent.Convertor() {
            public Object convert(Object obj) {
                try {
                    return DataObject.find(FolderNode.this.file);
                } catch (DataObjectNotFoundException e) {
                    // ignore, call super later on
                }
                return null;
            }
            public Class type(Object obj) {
                return (Class) obj;
            }
            public String id(Object obj) {
                return "";
            }
            public String displayName(Object obj) {
                return "";
            }
        };

        content.add(DataObject.class, lazyDataObject);
        if (file.isFolder()) {
            content.add(DataFolder.class, lazyDataObject);
        }
        this.file = file;
        this.content = content;
        init(file);
    }

    /** Allows sobclasses to customize lookup content. */
    protected final InstanceContent getLookupContent() {
        return content;
    }

    private void init(FileObject file) {
        try {
            FileSystem fs = file.getFileSystem();
            vcsFileStatusListener = new VCSFileStatusListener();
            fs.addFileStatusListener((FileStatusListener) WeakListeners.create(FileStatusListener.class, vcsFileStatusListener, fs));
        } catch (FileStateInvalidException exc) {
            ErrorManager err = ErrorManager.getDefault();
            err.notify(ErrorManager.INFORMATIONAL, exc);
            return;
        }
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
        try {
            Set target = Collections.singleton(file);
            FileSystem.Status fsStatus = file.getFileSystem().getStatus();
            if (fsStatus instanceof FileSystem.HtmlStatus) {
                return ((FileSystem.HtmlStatus) fsStatus).annotateNameHtml(file.getNameExt(), target);
            }
        } catch (FileStateInvalidException exc) {
            // null bellow
        }
        // we cannot provide HTNL status, framework will ask us for plain display name
        return null;
    }
    
    protected Image getBlankIcon(int type) {
        return super.getIcon(type);
    }

    public Image getIcon (int type) {
        Image img = null;
        if (type == BeanInfo.ICON_COLOR_16x16) {
            // search for proper folder icon installed by core/windows module
            img = (Image)UIManager.get("Nb.Explorer.Folder.icon");
        }
        if (img == null) {
            img = super.getIcon(type);
        }
        // give chance to annotate icon
        // copied from DataNode to keep the contract
        try {
            Set target = Collections.singleton(file);
            img = file.getFileSystem().
                  getStatus().annotateIcon(img, type, target);
        } catch (FileStateInvalidException e) {
            // no fs, do nothing
        }
        return img;
    }

    /** Overrides folder icon to search for icon in UIManager table for
     * BeanInfo.ICON_COLOR_16x16 type, to allow for different icons
     * across Look and Feels.
     * Keeps possibility of icon annotations.
     */
    public Image getOpenedIcon (int type) {
        Image img = null;
        if (type == BeanInfo.ICON_COLOR_16x16) {
            // search for proper folder icon installed by core/windows module
            img = (Image)UIManager.get("Nb.Explorer.Folder.openedIcon");
        }
        if (img == null) {
            img = super.getOpenedIcon(type);
        }
        // give chance to annotate icon
        // copied from DataNode to keep the contract
        try {
            Set target = Collections.singleton(file);
            img = file.getFileSystem().
            getStatus().annotateIcon(img, type, target);
        } catch (FileStateInvalidException e) {
            // no fs, do nothing
        }
        return img;
    }

    public Action[] getActions(boolean context) {
        return FolderNode.getFolderActions();
    }

    public static SystemAction[] getFolderActions() {
        return new SystemAction[] {
            //SystemAction.get(DebugAction.class),
            SystemAction.get (org.openide.actions.OpenLocalExplorerAction.class),
            SystemAction.get (org.openide.actions.FindAction.class),
            null,
            SystemAction.get (org.openide.actions.FileSystemAction.class),
            null,
            SystemAction.get (org.openide.actions.ToolsAction.class),
            SystemAction.get (org.openide.actions.PropertiesAction.class)
        };
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
    
    /**
     * Create the property sheet.
     *
     * @return the sheet
     */
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        ps.put(new PropertySupport.ReadOnly(FolderNode.PROP_STATUS,
                String.class,
                NbBundle.getMessage(FolderNode.class, "PROP_Status"),
                NbBundle.getMessage(FolderNode.class, "HINT_Status")) {
            public Object getValue() {
                String value = getStatus();
                return (value == null) ? "" : value;
            }
        });
        ps.put(new PropertySupport.ReadOnly(FolderNode.PROP_REVISION,
                String.class,
                NbBundle.getMessage(FolderNode.class, "PROP_Revision"),
                NbBundle.getMessage(FolderNode.class, "HINT_Revision")) {
            public Object getValue() {
                String value = getRevision();
                return (value == null) ? "" : value;
            }
        });
        ps.put(new PropertySupport.ReadOnly(FolderNode.PROP_STICKY,
                String.class,
                NbBundle.getMessage(FolderNode.class, "PROP_Sticky"),
                NbBundle.getMessage(FolderNode.class, "HINT_Sticky")) {
            public Object getValue() {
                String value = getSticky();
                return (value == null) ? "" : value;
            }
        });
        sheet.put(ps);

        Sheet.Set expert = Sheet.createExpertSet();
        expert.put(new PropertySupport.ReadOnly(FolderNode.PROP_LOCKER,
                String.class,
                NbBundle.getMessage(FolderNode.class, "PROP_Locker"),
                NbBundle.getMessage(FolderNode.class, "HINT_Locker")) {
            public Object getValue() {
                String value = getLocker();
                return (value == null) ? "" : value;
            }
        });
        sheet.put(expert);

        return sheet;
    }

    /** Get the file this node operates on. */
    protected FileObject getFile() {
        return file;
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
                String name = file.getPath();
                String newState;
                String oldState;
                FileStatusProvider statusProvider = null;

                    statusProvider = getFileStatusProvider();
                    if (statusProvider == null) return;

                newState = statusProvider.getFileStatus(name);
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

                // Refresh annotation and/or icon of this node
                if (ev.isNameChange()) {
                    fireDisplayNameChange(null, null);
                }
                if (ev.isIconChange()) {
                    fireIconChange();
                }
            }
        }
    }

    static class DebugAction extends NodeAction {

        public String getName() {
            return "Debug Status Cache";  // NOI18N
        }

        public HelpCtx getHelpCtx() {
            return null;
        }

        protected void performAction(Node[] activatedNodes) {
            FolderNode self = (FolderNode) activatedNodes[0].getCookie(FolderNode.class);
            self.getHtmlDisplayName(); // put breakpoint here
        }

        protected boolean enable(Node[] activatedNodes) {
            return true;
        }
    }

}
