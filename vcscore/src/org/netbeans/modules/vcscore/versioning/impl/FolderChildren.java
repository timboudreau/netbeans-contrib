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
                FileKey f1 = (FileKey) o1;
                FileKey f2 = (FileKey) o2;
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
                FileKey key = new FileKey(en[i]);
                keys.add(key);
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
        FileKey fileKey = (FileKey) key;
        String name = fileKey.getNameExt();
        FileObject fo = folder.getFileObject(name);
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

    /**
     * FileKey is more robust that plain FileObject, it's immutable.
     * <p>
     * Warning: there can exist two FileObjects for the same
     * file and filesystem. One valid and one invalid. Due to
     * this problem current implementation delegates to
     * passed fileobject.
     */
    private static class FileKey {
//        private final String name;
//        private final boolean folder;
        private final FileObject fo;

        FileKey(FileObject fo) {
//            name = fo.getNameExt();
//            folder = fo.isFolder();
            this.fo = fo;
        }

        public String getNameExt() {
            return fo.getNameExt();
        }

        public boolean isFolder() {
            return fo.isFolder();
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FileKey)) return false;

            final FileKey fileKey = (FileKey) o;
            return fo.equals(fileKey.fo);
//            if (folder != fileKey.folder) return false;
//            if (name != null ? !name.equals(fileKey.name) : fileKey.name != null) return false;
//
//            return true;
        }

        public int hashCode() {
            return fo.hashCode();
//            int result;
//            result = (name != null ? name.hashCode() : 0);
//            result = 29 * result + (folder ? 1 : 0);
//            return result;
        }
    }


    // FileChangeListener implementation ~~~~~~~~~~~~~~~~~

    public void fileFolderCreated(FileEvent fe) {
        setKeys(computeKeys());      // XXX coming on main window activation => caused by FS refresh
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
            boolean visibleByFS  = true;
            versioningFS = VersioningFileSystem.findFor(file);
            if (versioningFS != null) {
                File iofile = FileUtil.toFile(file);
                visibleByFS = versioningFS.getFileFilter().accept(iofile.getParentFile(), iofile.getName());
            }

            // merge with visibility query results (hide .bak etc.) 
            return visibleByFS && vquery.isVisible(file);
        }

        public void addChangeListener(ChangeListener listener) {
            listeners.add(ChangeListener.class, listener);
        }

        public void removeChangeListener(ChangeListener listener) {
            listeners.remove(ChangeListener.class, listener);
        }
    }

}
