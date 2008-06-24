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

package org.netbeans.modules.vcscore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.netbeans.spi.queries.VisibilityQueryImplementation;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;
import org.openide.util.WeakSet;

/**
 * VCS visibility query implementation based on VCS filesystems.
 * Files are visible if they are not ignored by the VCS filesystem.
 *
 * @author Martin Entlicher
 */
public class VcsVisibilityQueryImpl implements VisibilityQueryImplementation, PropertyChangeListener {
    
    private EventListenerList listeners = new EventListenerList();
    private WeakSet listenedVFSs = new WeakSet();
    
    /** Create VcsVisibilityQueryImpl */
    public VcsVisibilityQueryImpl() {}
    
    public boolean isVisible(FileObject fo) {
        VcsProvider vcsP = VcsProvider.getProvider(fo);
        if (vcsP == null) return true;
        synchronized (listenedVFSs) {
            if (!listenedVFSs.contains(vcsP)) {
                listenedVFSs.add(vcsP);
                vcsP.addPropertyChangeListener(WeakListeners.propertyChange(this, vcsP));
            }
        }
        File file = FileUtil.toFile(fo);
        return vcsP.getFileFilter().accept(file.getParentFile(), file.getName());
    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
    
    private void fireChangeEvent() {
        ChangeEvent che = new ChangeEvent(this);
        EventListener[] ls = listeners.getListeners(ChangeListener.class);
        for (int i = ls.length - 1; i >= 0; i -= 2) {
            ((ChangeListener) ls[i]).stateChanged(che);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (VcsProvider.PROP_FILE_FILTER.equals(evt.getPropertyName())) {
            fireChangeEvent();
        }
    }
    
}
