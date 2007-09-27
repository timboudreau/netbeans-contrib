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
