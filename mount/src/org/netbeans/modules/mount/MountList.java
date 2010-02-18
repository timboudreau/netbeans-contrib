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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.ChangeSupport;
import org.openide.util.NbPreferences;

// XXX listen to roots; if one is deleted, remove it from the list

/**
 * Keeps the list of mounts.
 * @author Jesse Glick
 */
final class MountList {
    
    public static final MountList DEFAULT = new MountList();
    private static final Logger LOG = Logger.getLogger(MountList.class.getName());
    
    private List<URL> mountURLs = null;
    private FileObject[] mounts = null;
    private final ChangeSupport cs = new ChangeSupport(this);
    
    private MountList() {}
    
    private synchronized void maybeLoad() {
        if (mountURLs == null) {
            load();
        }
    }
    
    public synchronized FileObject[] getMounts() {
        maybeLoad();
        if (mounts == null) {
            List<FileObject> _mounts = new ArrayList(mountURLs.size());
            for (URL u : mountURLs) {
                FileObject fo = URLMapper.findFileObject(u);
                if (fo != null) {
                    _mounts.add(fo);
                }
            }
            mounts = _mounts.toArray(new FileObject[_mounts.size()]);
        }
        assert mounts != null;
        assert !Arrays.asList(mounts).contains(null) : Arrays.asList(mounts);
        return mounts;
    }

    private Preferences prefs() {
        return NbPreferences.forModule(MountList.class);
    }

    private static final String MOUNT_LIST = "mountList";
    
    private void load() {
        mountURLs = new ArrayList<URL>();
        for (String u : prefs().get(MOUNT_LIST, "").split(" ")) {
            try {
                mountURLs.add(new URL(u));
            } catch (MalformedURLException x) {
                LOG.log(Level.INFO, null, x);
            }
        }
    }
    
    private void store() {
        assert mountURLs != null;
        StringBuilder b = new StringBuilder();
        for (URL u : mountURLs) {
            if (b.length() > 0) {
                b.append(' ');
            }
            b.append(u);
        }
        prefs().put(MOUNT_LIST, b.toString());
    }
    
    public void addArchiveOrFolder(File f) {
        URL u = FileUtil.urlForArchiveOrDir(f);
        if (u == null) {
            LOG.log(Level.WARNING, "No URL for {0}", f);
            return;
        }
        maybeLoad();
        mountURLs.add(u);
        mountURLsChanged();
    }
    
    public void remove(FileObject f) {
        maybeLoad();
        try {
            mountURLs.remove(f.getURL());
            mountURLsChanged();
        } catch (FileStateInvalidException e) {
            LOG.log(Level.INFO, null, e);
        }
    }
    
    private void mountURLsChanged() {
        synchronized (this) {
            store();
            mounts = null;
        }
        cs.fireChange();
    }
    
    public synchronized void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }
    
}
