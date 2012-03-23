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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation2;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 * Just notes 'source' attribute on &lt;javac&gt; currently.
 */
class SourceLevelQueryImpl implements SourceLevelQueryImplementation2 {

    @Override public Result getSourceLevel(FileObject fo) {
        File f = FileUtil.toFile(fo);
        while (f != null) {
            if (Cache.get(f + JavaCacheConstants.SOURCE_LEVEL) != null) {
                return new R(f + JavaCacheConstants.SOURCE_LEVEL);
            }
            f = f.getParentFile();
        }
        return null;
    }

    private static class R implements Result, PropertyChangeListener {
        private final String key;
        private final ChangeSupport cs = new ChangeSupport(this);
        @SuppressWarnings("LeakingThisInConstructor")
        R(String key) {
            this.key = key;
            Cache.addPropertyChangeListener(WeakListeners.propertyChange(this, Cache.class));
        }
        @Override public String getSourceLevel() {
            return Cache.get(key);
        }
        @Override public void addChangeListener(ChangeListener listener) {
            cs.addChangeListener(listener);
        }
        @Override public void removeChangeListener(ChangeListener listener) {
            cs.removeChangeListener(listener);
        }
        @Override public void propertyChange(PropertyChangeEvent evt) {
            if (key.equals(evt.getPropertyName())) {
                cs.fireChange();
            }
        }
    }
}
