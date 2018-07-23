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

package org.netbeans.modules.autoproject.web;

import org.netbeans.modules.autoproject.spi.Cache;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 * Enumerates known Java source roots.
 */
public class SourcesImpl implements Sources, PropertyChangeListener {

    public static final String SOURCES_TYPE_DOCROOT = "web-docroot"; // NOI18N
    public static final String SOURCES_TYPE_WEBINF = "web-inf"; // NOI18N
    
    private static final Logger LOG = Logger.getLogger(SourcesImpl.class.getName());

    private final Project p;
    private final ChangeSupport cs = new ChangeSupport(this);

    public SourcesImpl(Project p) {
        this.p = p;
        Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
    }

    public SourceGroup[] getSourceGroups(String type) {
        List<SourceGroup> groups = new ArrayList<SourceGroup>();
        if (type.equals(SourcesImpl.SOURCES_TYPE_WEBINF)) {
            String root = FileUtil.getFileDisplayName(p.getProjectDirectory());
            String file = Cache.get(root + WebCacheConstants.WEBINF);
            if (file != null) {
                FileObject fo = FileUtil.toFileObject(new File(file));
                // TODO: perhaps do not show WEB-INF if presented under a DOCROOT
                if (fo != null) {
                    groups.add(GenericSources.group(p, fo, "WEB-INF", file, null, null)); // NOI18N
                }
            }
        } else if (type.equals(SourcesImpl.SOURCES_TYPE_DOCROOT)) {
            String top = FileUtil.getFileDisplayName(p.getProjectDirectory());
            for (Map.Entry<String,String> entry : Cache.pairs()) {
                String k = entry.getKey();
                if (k.equals(top+WebCacheConstants.DOCROOT)) {
                    for (String piece : entry.getValue().split("[:;]")) {
                        FileObject root = FileUtil.toFileObject(FileUtil.normalizeFile(new File(piece)));
                        if (root == null) {
                            continue;
                        }
                        String path = FileUtil.getRelativePath(p.getProjectDirectory(), root);
                        if (path == null) {
                            path = piece;
                        }
                        groups.add(GenericSources.group(p, root, path, path, null, null));
                        // XXX should add file listener to root in case it gets deleted
                    }
                }
            }
        }
        return groups.toArray(new SourceGroup[groups.size()]);
    }

    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().endsWith(WebCacheConstants.DOCROOT)) {
            // XXX could be more discriminating
            cs.fireChange();
        }
    }

}
