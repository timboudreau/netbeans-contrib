/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.modules.parsing.impl.indexing.CacheFolder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Pair;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = WorkspaceResolver.class, position = 10_000)
public class OpenProjectsWorkspaceResolver extends WorkspaceResolver {

    private static final Logger LOG = Logger.getLogger(OpenProjectsWorkspaceResolver.class.getName());

    private final Object lock = new Object();

    //@GuardedBy("lock")
    private Pair<Reference<FileObject>,Reference<FileObject>> last;
    //@GuardedBy("lock")
    private final Set<FileObject> knownRoots;

    public OpenProjectsWorkspaceResolver() {
        this.knownRoots = Collections.newSetFromMap(new WeakHashMap<FileObject,Boolean>());
    }

    @Override
    @CheckForNull
    public FileObject resolveFile(
            @NonNull final Context ctx) {
        Parameters.notNull("ctx", ctx);   //NOI18N
        final FileObject srcRoot = getSourceRoot(ctx.getWorkspace());
        if (srcRoot == null) {
            return null;
        }
        return srcRoot.getFileObject(ctx.getPath());
    }

    @Override
    @CheckForNull
    public Context resolveContext(@NonNull final FileObject file) {
        Parameters.notNull("file", file);   //NOI18N
        final FileObject srcRoot = getSourceRoot(file);
        if (srcRoot == null) {
            return null;
        }
        final String workspaceId = getWorkspaceId(srcRoot);
        if (workspaceId == null) {
            return null;
        }
        final String path = FileUtil.getRelativePath(srcRoot, file);
        return new Context(
            "", //NOI18N
            workspaceId,
            path);
    }


    @CheckForNull
    private FileObject getSourceRoot(@NonNull final FileObject file) {
        synchronized (lock) {
            if (last != null) {
                FileObject res;
                if (file.equals(last.first().get()) && (res=last.second().get()) != null) {
                    return res;
                }
            }
            for (FileObject root : knownRoots) {
                if (root.equals(file) || FileUtil.isParentOf(root, file)) {
                    return root;
                }
            }
        }
        final ClassPath cp = ClassPath.getClassPath(file, ClassPath.SOURCE);
        if (cp == null) {
            return null;
        }
        final FileObject root = cp.findOwnerRoot(file);
        if (root == null) {
            return null;
        }
        synchronized (lock) {
            last = Pair.<Reference<FileObject>,Reference<FileObject>>of(
                new WeakReference<FileObject>(file),
                new WeakReference<FileObject>(root));
            knownRoots.add(root);
        }
        return root;
    }

    @CheckForNull
    private FileObject getSourceRoot(@NonNull final String workspaceId) {
        final FileObject cacheFolder = CacheFolder.getCacheFolder();
        if (cacheFolder == null) {
            return null;
        }
        final FileObject dataFolder = cacheFolder.getFileObject(workspaceId);
        if (dataFolder == null) {
            return null;
        }
        final URL srcRootURL = CacheFolder.getSourceRootForDataFolder(dataFolder);
        return srcRootURL == null ?
            null:
            URLMapper.findFileObject(srcRootURL);
    }

    @CheckForNull
    private String getWorkspaceId(@NonNull final FileObject srcRoot) {
        try {
            FileObject dataFolder = CacheFolder.getDataFolder(srcRoot.toURL(), true);
            return dataFolder == null ?
                null :
                dataFolder.getName();
        } catch (IOException ioe) {
            LOG.warning(ioe.getMessage());
            return null;
        }
    }

}
