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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

// XXX listen to roots; if one is deleted, remove it from the list

/**
 * Keeps the list of mounts.
 * @author Jesse Glick
 */
final class MountList {
    
    public static final MountList DEFAULT = new MountList();
    
    private List/*<URL>*/ mountURLs = null;
    private FileObject[] mounts = null;
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    
    private MountList() {}
    
    private synchronized void maybeLoad() {
        if (mountURLs == null) {
            try {
                load();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
                mountURLs = new ArrayList();
            }
        }
    }
    
    public synchronized FileObject[] getMounts() {
        maybeLoad();
        if (mounts == null) {
            List/*<FileObject>*/ _mounts = new ArrayList(mountURLs.size());
            Iterator it = mountURLs.iterator();
            while (it.hasNext()) {
                URL u = (URL) it.next();
                FileObject fo = URLMapper.findFileObject(u);
                if (fo != null) {
                    _mounts.add(fo);
                }
            }
            mounts = (FileObject[]) _mounts.toArray(new FileObject[_mounts.size()]);
        }
        assert mounts != null;
        assert !Arrays.asList(mounts).contains(null) : Arrays.asList(mounts);
        return mounts;
    }
    
    private void load() throws IOException {
        mountURLs = new ArrayList();
        FileObject list = WorkDir.get().getFileObject(WorkDir.RELPATH_MOUNT_LIST);
        if (list != null) {
            InputStream is = list.getInputStream();
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8")); // NOI18N
                String url;
                while ((url = r.readLine()) != null) {
                    mountURLs.add(new URL(url));
                }
            } finally {
                is.close();
            }
        }
    }
    
    private void store() throws IOException {
        assert mountURLs != null;
        FileObject list = WorkDir.get().getFileObject(WorkDir.RELPATH_MOUNT_LIST);
        if (list == null) {
            list = FileUtil.createData(WorkDir.get(), WorkDir.RELPATH_MOUNT_LIST);
        }
        FileLock lock = list.lock();
        try {
            OutputStream os = list.getOutputStream(lock);
            try {
                PrintStream ps = new PrintStream(os, false, "UTF-8"); // NOI18N
                Iterator it = mountURLs.iterator();
                while (it.hasNext()) {
                    URL url = (URL) it.next();
                    ps.println(url);
                }
                ps.flush();
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    public void addArchive(File f) {
        maybeLoad();
        try {
            mountURLs.add(FileUtil.getArchiveRoot(f.toURI().toURL()));
            mountURLsChanged();
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public void addFolder(File f) {
        maybeLoad();
        try {
            mountURLs.add(f.toURI().toURL());
            mountURLsChanged();
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public void remove(FileObject f) {
        maybeLoad();
        try {
            mountURLs.remove(f.getURL());
            mountURLsChanged();
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private void mountURLsChanged() {
        synchronized (this) {
            try {
                store();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
            mounts = null;
        }
        fireChange();
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
    
}
