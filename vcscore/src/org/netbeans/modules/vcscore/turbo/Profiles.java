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
