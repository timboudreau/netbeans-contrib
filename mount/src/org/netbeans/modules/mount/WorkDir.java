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
    
    private WorkDir() {}
    
    public static FileObject get() throws IOException {
        FileSystem sfs = Repository.getDefault().getDefaultFileSystem();
        FileObject mounts = sfs.getRoot().getFileObject(PATH_MOUNTS);
        if (mounts == null) {
            mounts = FileUtil.createFolder(sfs.getRoot(), PATH_MOUNTS);
        }
        return mounts;
    }
    
    // XXX general build script etc.
    
}
