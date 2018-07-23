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

package org.netbeans.modules.mount;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

/**
 * Reports all mount points as Java package roots.
 * @author Jesse Glick
 */
final class MountSources implements Sources, ChangeListener {
    
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    
    public MountSources() {
        MountList.DEFAULT.addChangeListener(WeakListeners.change(this, MountList.DEFAULT));
    }

    public SourceGroup[] getSourceGroups(String type) {
        if (!type.equals(Sources.TYPE_GENERIC) && !type.equals(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            return new SourceGroup[0];
        }
        List/*<SourceGroup>*/ groups = new ArrayList();
        FileObject[] roots = MountList.DEFAULT.getMounts();
        for (int i = 0; i < roots.length; i++) {
            groups.add(new Group(roots[i]));
        }
        if (type.equals(Sources.TYPE_GENERIC)) {
            groups.add(GenericSources.group(DummyProject.getInstance(), DummyProject.getInstance().getProjectDirectory(), "mount", "Mounting Work Area", null, null));
        }
        return (SourceGroup[]) groups.toArray(new SourceGroup[groups.size()]);
    }

    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fireChange() {
        ChangeListener[] ls;
        synchronized (this) {
            if (listeners.isEmpty()) {
                return;
            }
            ls = (ChangeListener[]) listeners.toArray(new ChangeListener[listeners.size()]);
        }
        ChangeEvent ev = new ChangeEvent(this);
        for (int i = 0; i < ls.length; i++) {
            ls[i].stateChanged(ev);
        }
    }
    
    public void stateChanged(ChangeEvent ev) {
        fireChange();
    }
    
    private static final class Group implements SourceGroup {
        
        private final FileObject root;
        
        public Group(FileObject root) {
            this.root = root;
        }

        public FileObject getRootFolder() {
            return root;
        }

        public String getName() {
            try {
                return root.getURL().toExternalForm();
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);
                return ""; // NOI18N
            }
        }

        public String getDisplayName() {
            return FileUtil.getFileDisplayName(root);
        }

        public Icon getIcon(boolean opened) {
            return null;
        }

        public boolean contains(FileObject file) throws IllegalArgumentException {
            return true;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {}

        public void removePropertyChangeListener(PropertyChangeListener listener) {}
        
    }
    
}
