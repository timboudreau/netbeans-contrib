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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Zezula
 */
final class SourceProvider {

    private static final Logger LOG = Logger.getLogger(SourceProvider.class.getName());

    //@GuardedBy("SourceProvider.class")
    private static SourceProvider instance;
    private final FileSystem ramFs;
    private final FileSystem tmpRamFs;

    private SourceProvider() {
        this.ramFs = FileUtil.createMemoryFileSystem();
        this.tmpRamFs = FileUtil.createMemoryFileSystem();
    }

    @NonNull
    public synchronized static SourceProvider getInstance() {
        if (instance == null) {
            instance = new SourceProvider();
        }
        return instance;
    }

    @CheckForNull
    Source getSource(
            @NullAllowed Context ctx,
            @NullAllowed String content) {
        FileObject file = null;
        ClassPath bootPath = null, compilePath = null, srcPath = null;
        if (ctx != null) {
            final WorkspaceResolver resolver = Lookup.getDefault().lookup(WorkspaceResolver.class);
            if (resolver == null) {
                LOG.warning("No WorkspaceResolver in Lookup."); //NOI18N
            } else {
                file = resolver.resolveFile(new WorkspaceResolver.Context(
                    ctx.getUser(),
                    ctx.getWorkspace(),
                    ctx.getPath()));
                if (file != null) {
                    bootPath = ClassPath.getClassPath(file, ClassPath.BOOT);
                    compilePath = ClassPath.getClassPath(file, ClassPath.COMPILE);
                    srcPath = ClassPath.getClassPath(file, ClassPath.SOURCE);
                }
            }
        }
        if (bootPath == null) {
            bootPath = JavaPlatform.getDefault().getBootstrapLibraries();
        }
        if (compilePath == null) {
            compilePath = ClassPath.EMPTY;
        }
        if (srcPath == null) {
            srcPath = ClassPath.EMPTY;
        }        
        final FileObject sourceFile = getSourceFile(srcPath, file, content);
        if (sourceFile == null) {
            return null;
        }
        final Source source = Source.create(sourceFile);
        return source;
    }

    @CheckForNull
    private FileObject getSourceFile(
        @NonNull ClassPath srcPath,
        @NullAllowed FileObject base,
        @NullAllowed String content) {
        if (content == null) {
            return base;
        } else {
            final String path = base == null ?
                null :
                srcPath.getResourceName(base,'/', true);    //NOI18N
            try {
                final FileObject fo;
                if (path == null) {
                    fo = tmpRamFs.createTempFile(
                        tmpRamFs.getRoot(),
                        "", //NOI18N
                        ".java",    //NOI18N
                        true);
                } else {
                    fo = FileUtil.createData(
                        ramFs.getRoot(),
                        path);
                }
                final FileLock lck = fo.lock();
                try (
                   final ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
                   final OutputStream out = fo.getOutputStream(lck)) {
                    FileUtil.copy(in, out);
                } finally {
                    lck.releaseLock();
                }
                return fo;
            } catch (IOException ioe) {
                return null;
            }
        }
    }

}
