/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import javax.swing.Icon;
import org.netbeans.modules.autoproject.spi.Cache;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.netbeans.spi.project.support.ant.PathMatcher;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 * Enumerates known Java source roots.
 */
class SourcesImpl implements Sources, PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(SourcesImpl.class.getName());

    private final Project p;
    private final ChangeSupport cs = new ChangeSupport(this);
    private SourceGroup[] javaGroups;

    public SourcesImpl(Project p) {
        this.p = p;
        Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
    }

    public synchronized SourceGroup[] getSourceGroups(String type) {
        if (type.equals(Sources.TYPE_GENERIC)) {
            return new SourceGroup[] {GenericSources.group(p, p.getProjectDirectory(),
                    "root", ProjectUtils.getInformation(p).getDisplayName(), null, null)};
        } else if (type.equals(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            if (javaGroups == null) {
                List<SourceGroup> groups = new ArrayList<SourceGroup>();
                String top = FileUtil.getFileDisplayName(p.getProjectDirectory());
                for (Map.Entry<String,String> entry : Cache.pairs()) {
                    String k = entry.getKey();
                    if (k.endsWith(JavaCacheConstants.SOURCE) && k.startsWith(top)) {
                        FileObject root = FileUtil.toFileObject(FileUtil.normalizeFile(
                                new File(k.substring(0, k.length() - JavaCacheConstants.SOURCE.length()))));
                        if (root != null && FileOwnerQuery.getOwner(root) == p) {
                            LOG.log(Level.FINE, "Found Java-type group in {0}", root);
                            groups.add(new Group(root));
                            // XXX should add file listener to root in case it gets deleted
                        }
                    }
                }
                javaGroups = groups.toArray(new SourceGroup[groups.size()]);
            }
            return javaGroups;
        } else {
            return new SourceGroup[0];
        }
    }

    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().endsWith(JavaCacheConstants.SOURCE)) {
            // XXX could be more discriminating
            synchronized (this) {
                javaGroups = null;
            }
            cs.fireChange();
        }
    }

    private final class Group implements SourceGroup, PropertyChangeListener {

        private final FileObject root;
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        Group(FileObject root) {
            this.root = root;
            Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
        }

        public FileObject getRootFolder() {
            return root;
        }

        public String getName() {
            return FileUtil.getRelativePath(p.getProjectDirectory(), root);
        }

        public String getDisplayName() {
            String n = getName();
            return n.length() > 0 ? n : "Source Packages"; // XXX I18N
        }

        public Icon getIcon(boolean opened) {
            return null;
        }

        public boolean contains(FileObject file) throws IllegalArgumentException {
                if (file == root) {
                    return true;
                }
                String path = FileUtil.getRelativePath(root, file);
                if (path == null) {
                    throw new IllegalArgumentException(file + " is not inside " + root);
                }
                if (file.isFolder()) {
                    path += "/"; // NOI18N
                }
                if (!computeIncludeExcludePatterns().matches(path, true)) {
                    return false;
                }
                if (file.isFolder() && file != p.getProjectDirectory() && ProjectManager.getDefault().isProject(file)) {
                    return false;
                }
                // XXX FOQ & SQ calls disabled for typed source roots; difficult to make fast (#97215)
                return true;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }

        private PathMatcher matcher;
        private PathMatcher computeIncludeExcludePatterns() {
            synchronized (this) {
                if (matcher != null) {
                    return matcher;
                }
            }
            File rootF = FileUtil.toFile(root);
            String includesPattern = Cache.get(rootF + JavaCacheConstants.INCLUDES);
            String excludesPattern = Cache.get(rootF + JavaCacheConstants.EXCLUDES);
            PathMatcher _matcher = new PathMatcher(includesPattern, excludesPattern, rootF);
            synchronized (this) {
                matcher = _matcher;
            }
            return _matcher;
        }
        private synchronized void resetIncludeExcludePatterns() {
            matcher = null;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String prop = evt.getPropertyName();
            if (prop.endsWith(JavaCacheConstants.INCLUDES) || prop.endsWith(JavaCacheConstants.EXCLUDES)) {
                resetIncludeExcludePatterns();
                pcs.firePropertyChange(PROP_CONTAINERSHIP, null, null);
            }
        }

    }

}
