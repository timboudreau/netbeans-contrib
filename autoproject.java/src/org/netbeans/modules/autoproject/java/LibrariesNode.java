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

package org.netbeans.modules.autoproject.java;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;

// XXX should have a common API to permit sharing with j2seproject

final class LibrariesNode extends AbstractNode {

    private static final Image ICON_BADGE = ImageUtilities.loadImage("org/netbeans/modules/java/j2seproject/ui/resources/libraries-badge.png"); // NOI18N

    LibrariesNode(Project project) {
        super(new LibrariesChildren(project), Lookups.singleton(project));
    }

    @Override
    public String getDisplayName() {
        return "Libraries"; // XXX I18N
    }

    @Override
    public String getName() {
        return "libraries";
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.mergeImages(DataFolder.findFolder(Repository.getDefault().getDefaultFileSystem().getRoot()).
                getNodeDelegate().getIcon(type), ICON_BADGE, 7, 7);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.mergeImages(DataFolder.findFolder(Repository.getDefault().getDefaultFileSystem().getRoot()).
                getNodeDelegate().getOpenedIcon(type), ICON_BADGE, 7, 7);
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[0];
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    private static class LibrariesChildren extends Children.Keys<FileObject> implements PropertyChangeListener, ChangeListener {

        private static final Icon ARCHIVE_ICON =
                ImageUtilities.image2Icon(ImageUtilities.loadImage("org/netbeans/modules/java/j2seproject/ui/resources/jar.gif"));
        private final Sources sources;
        private final PropertyChangeListener cpListener = WeakListeners.propertyChange(this, null);

        private LibrariesChildren(Project prj) {
            sources = ProjectUtils.getSources(prj);
            sources.addChangeListener(WeakListeners.change(this, sources));
        }

        public void stateChanged(ChangeEvent e) {
            setKeys(getKeys());
        }

        public void propertyChange(PropertyChangeEvent evt) {
            setKeys(getKeys());
        }

        @Override
        protected void addNotify() {
            setKeys(getKeys());
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.<FileObject>emptySet());
        }

        private Collection<FileObject> getKeys() {
            Collection<FileObject> keys = new LinkedHashSet<FileObject>();
            List<FileObject> sourceRoots = new ArrayList<FileObject>();
            for (SourceGroup g : sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
                sourceRoots.add(g.getRootFolder());
            }
            for (FileObject sourceRoot : sourceRoots) {
                // XXX add in bootcp later?
                ClassPath cp = ClassPath.getClassPath(sourceRoot, ClassPath.COMPILE);
                if (cp != null) {
                    cp.removePropertyChangeListener(cpListener);
                    cp.addPropertyChangeListener(cpListener);
                    BINROOT: for (FileObject binRoot : cp.getRoots()) {
                        try {
                            for (FileObject matchingRoot : SourceForBinaryQuery.findSourceRoots(binRoot.getURL()).getRoots()) {
                                if (sourceRoots.contains(matchingRoot)) {
                                    continue BINROOT;
                                }
                            }
                        } catch (FileStateInvalidException x) {
                            assert false : x;
                        }
                        keys.add(binRoot);
                    }
                }
            }
            return keys;
        }

        protected Node[] createNodes(FileObject root) {
            // XXX could use filter nodes to add actions to Show Javadoc, Go to Source, etc.
            URL u;
            try {
                u = root.getURL();
            } catch (FileStateInvalidException fsie) {
                assert false : fsie;
                return null;
            }
            File jar = FileUtil.archiveOrDirForURL(u);
            String label = jar != null && jar.isFile() ? jar.getName() : FileUtil.getFileDisplayName(root);
            return new Node[] {PackageView.createPackageView(GenericSources.group(
                    null, root, root.toString(), label, ARCHIVE_ICON,ARCHIVE_ICON))};
        }

    }

}
