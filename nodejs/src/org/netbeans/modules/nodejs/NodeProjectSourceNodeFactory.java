/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.nodejs.NodeProjectSourceNodeFactory.Key;
import org.netbeans.modules.nodejs.NodeProjectSourceNodeFactory.KeyTypes;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tim Boudreau
 */
public class NodeProjectSourceNodeFactory implements NodeFactory, NodeList<Key>, FileChangeListener {

    private final ChangeSupport supp = new ChangeSupport(this);
    private final Project project;

    public NodeProjectSourceNodeFactory(Project p) {
        this.project = p;
    }

    public NodeProjectSourceNodeFactory() {
        this.project = null;
    }

    @Override
    public NodeList<?> createNodes(Project p) {
        return new NodeProjectSourceNodeFactory(p);
    }

    @Override
    public List<Key> keys() {
        List<Key> keys = new ArrayList<Key>();
        FileObject libFolder = project.getProjectDirectory().getFileObject("node_modules");
        for (FileObject fo : project.getProjectDirectory().getChildren()) {
            if (fo.isData()) {
                keys.add(new Key(KeyTypes.SOURCE, fo));
            } else if (fo.isFolder()) {
                if (fo.getName().equals(NodeJSProject.METADATA_DIR) || fo.equals(libFolder)) {
                    continue;
                }
                keys.add(new Key(KeyTypes.SOURCE, fo));
            }
        }
        //now add libraries
        if (libFolder != null) {
            List<Key> libFolders = new ArrayList<Key>();
            for (FileObject lib : libFolder.getChildren()) {
                if (!"node_modules".equals(lib.getName()) && !"nbproject".equals(lib.getName()) && lib.isFolder()) {
                    Key key = new Key(KeyTypes.LIBRARY, lib);
                    key.direct = true;
                    keys.add(key);
                    recurseLibraries(lib, libFolders);
                }
            }
            keys.addAll(libFolders);
        }
        return keys;
    }

    private void recurseLibraries(FileObject libFolder, List<Key> keys) {
        FileObject libs = libFolder.getFileObject("node_modules");
        if (libs != null) {
            for (FileObject fo : libFolder.getChildren()) {
                for (FileObject lib : fo.getChildren()) {
                    if (!"node_modules".equals(lib.getName()) && !"nbproject".equals(lib.getName()) && lib.isFolder()) {
                        boolean jsFound = false;
                        for (FileObject kid : lib.getChildren()) {
                            jsFound = "js".equals(kid.getExt());
                            if (jsFound) {
                                break;
                            }
                        }
                        if (jsFound) {
                            Key key = new Key(jsFound ? KeyTypes.LIBRARY : KeyTypes.SOURCE, lib);
                            key.direct = false;
                            keys.add(key);
                            recurseLibraries(lib, keys);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        supp.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        supp.removeChangeListener(l);
    }

    @Override
    public Node node(Key key) {
        switch (key.type) {
            case LIBRARY:
                return new LibraryFilterNode(key);
            case SOURCE:
                return new FilterNode(nodeFromKey(key));
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void addNotify() {
        FileUtil.addRecursiveListener(this, FileUtil.toFile(project.getProjectDirectory()));
    }

    @Override
    public void removeNotify() {
        //do nothing
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileChanged(FileEvent fe) {
        //do nothing
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
        //do nothing
    }

    static final class Key {

        private final KeyTypes type;
        private final FileObject fld;
        private boolean direct;

        public Key(KeyTypes type, FileObject fld) {
            this.type = type;
            this.fld = fld;
        }

        public String toString() {
            return type + " " + fld.getName() + (direct ? " direct" : " indirect");
        }
    }

    static enum KeyTypes {

        SOURCE,
        LIBRARY
    }

    interface LibrariesFolderFinder {

        public FileObject getLibrariesFolder();
    }

    static final Node nodeFromKey(Key key) {
        try {
            return DataObject.find(key.fld).getNodeDelegate();
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return Node.EMPTY;
        }
    }

    private static final class LibraryFilterNode extends FilterNode {

        private final Key key;

        public LibraryFilterNode(Key key) {
            this(nodeFromKey(key), key);
        }

        private LibraryFilterNode(Node original, Key key) {
            super(nodeFromKey(key), Children.create(new LibraryNodeChildren(original.getLookup().lookup(DataObject.class)), true));
            assert key.type == KeyTypes.LIBRARY;
            this.key = key;
        }

        @Override
        public Image getIcon(int type) {
            Image result = ImageUtilities.loadImage("org/netbeans/modules/nodejs/resources/libs.png");
            if (!key.direct) {
                result = ImageUtilities.createDisabledImage(result);
            }
            return result;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getHtmlDisplayName() {
            return key.direct ? super.getHtmlDisplayName()
                    : "<font color='!controlShadow'>" + super.getDisplayName() + " (&lt;-" + key.fld.getParent().getParent().getName() + ")";
        }
    }

    private static final class LibraryNodeChildren extends ChildFactory<FileObject> implements FileChangeListener {

        private final DataObject dob;

        private LibraryNodeChildren(DataObject dob) {
            this.dob = dob;

        }

        @Override
        protected boolean createKeys(List<FileObject> toPopulate) {
            for (FileObject fo : dob.getPrimaryFile().getChildren()) {
                if ("node_modules".equals(fo) && fo.isFolder()) {
                }
                toPopulate.add(fo);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(FileObject key) {
            try {
                DataObject dob = DataObject.find(key);
                return new FilterNode(dob.getNodeDelegate());
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return Node.EMPTY;
            }
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
            refresh(false);
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            refresh(false);
        }

        @Override
        public void fileChanged(FileEvent fe) {
            //do nothing
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            //do nothing
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            refresh(true);
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
            //do nothing
        }
    }
}
