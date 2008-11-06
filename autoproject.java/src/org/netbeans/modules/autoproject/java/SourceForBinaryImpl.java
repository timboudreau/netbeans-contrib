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

import org.netbeans.modules.autoproject.spi.Cache;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 * Maps known compilation destdirs back to sources.
 */
class SourceForBinaryImpl implements SourceForBinaryQueryImplementation {

    private static final Logger LOG = Logger.getLogger(SourceForBinaryImpl.class.getName());

    private final Project p;

    public SourceForBinaryImpl(Project p) {
        this.p = p;
    }

    public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) {
        File f = FileUtil.archiveOrDirForURL(binaryRoot);
        if (f != null) {
            return new R(f);
        } else {
            return null;
        }
    }

    private static final class R implements SourceForBinaryQuery.Result, PropertyChangeListener {

        private final String root;
        private final ChangeSupport cs = new ChangeSupport(this);

        R(File f) {
            root = f.getAbsolutePath();
            Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
        }

        public FileObject[] getRoots() {
            List<FileObject> roots = new ArrayList<FileObject>();
            for (Map.Entry<String,String> entry : Cache.pairs()) {
                String k = entry.getKey();
                List<String> translatedRoots;
                String dirs = Cache.get(root + JavaCacheConstants.JAR);
                if (dirs != null) {
                    translatedRoots = Arrays.asList(dirs.split(File.pathSeparator));
                } else {
                    translatedRoots = Collections.singletonList(root);
                }
                if (k.endsWith(JavaCacheConstants.BINARY) && translatedRoots.contains(entry.getValue())) {
                    roots.add(FileUtil.toFileObject(new File(k.substring(0, k.length() - JavaCacheConstants.BINARY.length()))));
                }
            }
            LOG.log(Level.FINE, "sources of " + root + ": " + roots);
            return roots.toArray(new FileObject[roots.size()]);
        }

        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            // XXX maybe under slimmer conditions
            cs.fireChange();
        }

    }

}
