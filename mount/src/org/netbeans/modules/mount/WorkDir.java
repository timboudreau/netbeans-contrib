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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 * Manages the working directory.
 * @author Jesse Glick
 */
final class WorkDir {
    
    private static final String PATH_MOUNTS = "org-netbeans-modules-mount"; // NOI18N
    public static final String RELPATH_MOUNT_LIST = "mount-list"; // NOI18N
    private static final String RELPATH_BUILD_XML = "build.xml"; // NOI18N
    private static final String RELPATH_BUILD_IMPL_XML = "build-impl.xml"; // NOI18N
    private static final String RELPATH_BUILD_PROPERTIES = "build.properties"; // NOI18N
    
    private WorkDir() {}
    
    /**
     * Get the directory to be used for various purposes by this module.
     */
    public static FileObject get() throws IOException {
        FileSystem sfs = Repository.getDefault().getDefaultFileSystem();
        FileObject mounts = sfs.getRoot().getFileObject(PATH_MOUNTS);
        if (mounts == null) {
            mounts = FileUtil.createFolder(sfs.getRoot(), PATH_MOUNTS);
        }
        return mounts;
    }
    
    /**
     * Initialize the build scripts etc.
     * @return the main build.xml
     */
    public static FileObject initBuildEnvironment() throws IOException {
        FileObject dir = get();
        createFile(dir, RELPATH_BUILD_PROPERTIES, "resources/build.properties", false);
        createFile(dir, RELPATH_BUILD_IMPL_XML, "resources/build-impl.xml", true);
        return createFile(dir, RELPATH_BUILD_XML, "resources/build.xml", false);
    }
    
    private static FileObject createFile(FileObject dir, String path, String contents, boolean overwrite) throws IOException {
        FileObject f = dir.getFileObject(path);
        if (f != null && !overwrite) {
            // Already have one; leave it alone.
            return f;
        }
        if (f == null) {
            f = FileUtil.createData(dir, path);
        }
        FileLock lock = f.lock();
        try {
            OutputStream os = f.getOutputStream(lock);
            try {
                InputStream is = WorkDir.class.getResourceAsStream(contents);
                assert is != null : contents;
                try {
                    FileUtil.copy(is, os);
                } finally {
                    is.close();
                }
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
        return f;
    }
    
}
