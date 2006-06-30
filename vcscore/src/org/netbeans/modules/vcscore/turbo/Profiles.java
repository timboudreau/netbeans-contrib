/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;

import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.FileUtil;

import java.io.File;

/**
 * Access profile specifics data, such as possible statuses,
 * abstract Statuses to VCS statuses mapping and cache
 * file relative path.
 * <p>
 * The implementation forwards to FS that should be hopefully
 * CommandLineVCSFS that knows it's profile.
 *
 * @author Petr Kuzel
 */
final class Profiles {

    /**
     * Consults profile to get proper cache file for given folder (content).
     *
     * @param folder directory for which a cache file is searched
     * @return <code>null</code> if disk caching cannot be used.
     */
    public static File cacheForFolder(File folder) {
        VcsFileSystem fs = findVcsFileSystem(folder);
        if (fs != null) {
            String root = FileUtil.toFile(fs.getRoot()).getAbsolutePath();
            String path = folder.getAbsolutePath().substring(root.length());
            File ret = fs.getCacheFileName(folder, path);
            // assert that it does not return null for FS root subdirs
            // it's allowed to return null for files like "path/CVS/Root"
            assert path.length() == 0 || path.indexOf(File.separatorChar) != 0 || ret != null : "Root " + root + " path " + path;
            return ret;
        }
        return null;
    }

    /**
     * Locates VCSFs that cover given file.
     */
    private static VcsFileSystem findVcsFileSystem(File file) {
        String path = file.getAbsolutePath();
        FSRegistry registry = FSRegistry.getDefault();
        FSInfo[] infos = registry.getRegistered();
        for (int i = 0; i<infos.length; i++) {
            FSInfo info = infos[i];
            if (info.isControl() == false) continue;
            File root = info.getFSRoot();
            String rootPath = root.getAbsolutePath();
            if (path.startsWith(rootPath)) {
                return (VcsFileSystem) info.getFileSystem();  // assuming here that VCSFSs cannot overlap
            }
        }
        return null;
    }

    /**
     * Translates VCS specific statuc to abstract one.
     */
    public static FileStatusInfo toStatusInfo(File file, FileProperties fprops) {
        // TODO implement
        return null;
    }

}
