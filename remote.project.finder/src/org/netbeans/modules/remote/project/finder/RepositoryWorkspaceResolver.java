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

package org.netbeans.modules.remote.project.finder;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
public final class RepositoryWorkspaceResolver implements WorkspaceResolver {

    // path format users/{user}/workspaces/{workspace}/projects
    private static final Pattern LOCAL_PATH_PARSER = Pattern.compile(
        "users/([^/]+)/workspaces/([^/]+)/(.*)");   //NOI18N
    private static final String LOCAL_PATH_BUILDER =
        "users/{0}/workspaces/{1}/{2}"; //NOI18N

    public RepositoryWorkspaceResolver() {
    }

    @Override
    @CheckForNull
    public FileObject resolveFile(
        @NonNull final Context ctx) {
        Parameters.notNull("ctx", ctx); //NOI18N
        final FileObject repository = WorkSpaceUpdater.getDefault().getRepository();
        if (repository == null) {
            return null;
        }
        return repository.getFileObject(MessageFormat.format(
            LOCAL_PATH_BUILDER,
            ctx.getUser(),
            ctx.getWorkspace(),
            ctx.getPath()));
    }

    @Override
    @CheckForNull
    public Context resolveContext(@NonNull final FileObject file) {
        Parameters.notNull("file", file);   //NOI18N
        final FileObject repository = WorkSpaceUpdater.getDefault().getRepository();
        if (repository == null) {
            return null;
        }
        final String path = FileUtil.getRelativePath(repository, file);
        if (path == null) {
            return null;
        }
        final Matcher m = LOCAL_PATH_PARSER.matcher(path);
        if (!m.matches()) {
            return null;
        }
        return new Context(
            m.group(1),
            m.group(2),
            m.group(3));
    }

}
