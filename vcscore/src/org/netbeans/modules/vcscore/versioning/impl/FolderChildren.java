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

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.filesystems.*;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.*;
import java.io.File;

import org.netbeans.api.queries.*;

import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;

/**
 * Translates folder content (FileObjects) to FileNodes
 * and FolderNodes. Its content is driven by visibility state
 * and it also monitors folder for addings/removals.
 * <p>
 * This is probably zillionth implementaion.
 *
 * @author Petr Kuzel
 */
final class FolderChildren extends Children.Keys implements FileChangeListener, ChangeListener {

    private final FileObject folder;

    private static final VisibilityFilter VISIBILITY = new VisibilityFilter();

    public FolderChildren(FileObject folder) {
        this.folder = folder;
    }

    protected void addNotify() {
        setKeys(computeKeys());
        folder.addFileChangeListener(this);
        VISIBILITY.addChangeListener(this);
    }

    private Collection computeKeys() {
        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                FileObject f1 = (FileObject) o1;
                FileObject f2 = (FileObject) o2;
                if (f1.isFolder() != f2.isFolder()) {
                    // if classes are different than the folder goes first
                    if (f1.isFolder()) {
                        return -1;
                    }
                    if (f2.isFolder()) {
                        return 1;
                    }
                }

                // otherwise compare by names
                return f1.getNameExt().compareTo(f2.getNameExt());
            }
        };
        Set keys = new TreeSet(comp);
        FileObject[] en = folder.getChildren();
        for (int i =0; i<en.length; i++) {
            if (VISIBILITY.isVisible(en[i])) {
                keys.add(en[i]);
            }
        }
        return keys;
    }

    protected void removeNotify() {
        VISIBILITY.removeChangeListener(this);
        folder.removeFileChangeListener(this);
        setKeys(Collections.EMPTY_SET);
    }

    protected Node[] createNodes(Object key) {
        FileObject fo = (FileObject) key;
        Node[] ret;
        if (fo.isData()) {
            ret = new Node[] {
                new FileNode(fo)
            };
        } else {
            ret = new Node[] {
                new FolderNode(fo)
            };
        }
        return ret;
    }

    // FileChangeListener implementation ~~~~~~~~~~~~~~~~~

    public void fileFolderCreated(FileEvent fe) {
        setKeys(computeKeys());
    }

    public void fileDataCreated(FileEvent fe) {
        setKeys(computeKeys());
    }

    public void fileChanged(FileEvent fe) {
    }

    public void fileDeleted(FileEvent fe) {
        setKeys(computeKeys());
    }

    public void fileRenamed(FileRenameEvent fe) {
        setKeys(computeKeys());
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
    }

    /** React to visibility changes */
    public void stateChanged(ChangeEvent e) {
        if (VISIBILITY.equals(e.getSource())) {
            setKeys(computeKeys());
        }
    }

    /**
     * Strategy that judges which files to hide (.cvsignore, /CVS, ..).
     */
    static final class VisibilityFilter  {

        private VisibilityQuery vquery;
        private final javax.swing.event.EventListenerList listeners = new javax.swing.event.EventListenerList();

        public VisibilityFilter() {
            vquery = VisibilityQuery.getDefault();
            vquery.addChangeListener(new ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent ev) {
                    EventListener[] els = listeners.getListeners(ChangeListener.class);
                    for (int i = 0; i < els.length; i++) {
                        ((ChangeListener) els[i]).stateChanged(ev);
                    }
                }
            });
        }

        public boolean isVisible(FileObject file) {

            VersioningFileSystem versioningFS;
            try {
                versioningFS = VersioningFileSystem.findFor(file.getFileSystem());
                if (versioningFS != null) {
                    File iofile = FileUtil.toFile(file);
                    return versioningFS.getFileFilter().accept(iofile.getParentFile(), iofile.getName());
                }
            } catch (FileStateInvalidException e) {
            }

            // fallback
            return vquery.isVisible(file);
        }

        public void addChangeListener(ChangeListener listener) {
            listeners.add(ChangeListener.class, listener);
        }

        public void removeChangeListener(ChangeListener listener) {
            listeners.remove(ChangeListener.class, listener);
        }
    }

}
