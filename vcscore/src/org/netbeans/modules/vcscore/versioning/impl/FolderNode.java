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

import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.AbstractNode;
import org.openide.util.actions.SystemAction;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Set;
import java.util.Collections;

/**
 * Visualizes folder as much closely to FolderNode as possible
 * without need for data systems layer. It adds
 * actions, cookies, icon, display name and property
 * sheet all based on background file. It does not
 * support any clipboard operations.
 *
 * @author Petr Kuzel
 */
final class FolderNode extends AbstractNode {

    private final FileObject folder;

    // XXX probably undeclatred dependency, copied from loaders.FolderNode
    static final String FOLDER_ICON_BASE =
        "org/openide/loaders/defaultFolder"; // NOI18N


    public FolderNode(FileObject folder) {
        super(new FolderChildren(folder));
        this.folder = folder;
        setIconBase(FOLDER_ICON_BASE);
    }

    public Cookie getCookie(Class type) {
        // mimics DataNode because some actions heavily depends on DataObject cookie existence
        if (type.isAssignableFrom(DataObject.class) || type.isAssignableFrom(DataFolder.class)) {
            try {
                return DataObject.find(folder);
            } catch (DataObjectNotFoundException e) {
                // ignore, call super later on
            }
        }
        return super.getCookie(type);
    }

    public String getName() {
        return folder.getNameExt();
    }

    public String getDisplayName() {
        String s;
        try {
            Set target = Collections.singleton(folder);
            s = folder.getFileSystem().getStatus().annotateName(folder.getNameExt(), target);
        } catch (FileStateInvalidException exc) {
            s = super.getDisplayName();
        }
        return s;
    }
    
    public String getHtmlDisplayName() {
        String s;
        try {
            Set target = Collections.singleton(folder);
            FileSystem.Status fsStatus = folder.getFileSystem().getStatus();
            if (fsStatus instanceof FileSystem.HtmlStatus) {
                s = ((FileSystem.HtmlStatus) fsStatus).annotateNameHtml(folder.getNameExt(), target);
            } else {
                s = fsStatus.annotateName(folder.getNameExt(), target);
            }
        } catch (FileStateInvalidException exc) {
            s = super.getHtmlDisplayName();
        }
        return s;
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
            Set target = Collections.singleton(folder);
            img = folder.getFileSystem().
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
            Set target = Collections.singleton(folder);
            img = folder.getFileSystem().
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


}
