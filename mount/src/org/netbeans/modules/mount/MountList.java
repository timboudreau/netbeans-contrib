/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
