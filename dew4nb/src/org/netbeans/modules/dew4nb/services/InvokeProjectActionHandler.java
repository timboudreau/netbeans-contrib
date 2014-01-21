/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.dew4nb.Context;
import org.netbeans.modules.dew4nb.InvokeProjectActionResult;
import org.netbeans.modules.dew4nb.JavacMessageType;
import org.netbeans.modules.dew4nb.JavacQuery;
import org.netbeans.modules.dew4nb.RequestHandler;
import org.netbeans.modules.dew4nb.Status;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = RequestHandler.class)
public class InvokeProjectActionHandler extends RequestHandler<JavacQuery, InvokeProjectActionResult> {

    public InvokeProjectActionHandler() {
        super(JavacMessageType.invokeAction, JavacQuery.class, InvokeProjectActionResult.class);
    }

    @Override
    protected boolean handle(JavacQuery request, InvokeProjectActionResult response) {
        if (request.getType() != JavacMessageType.invokeAction) {
            throw new IllegalStateException(String.format(
                "Illegal message type: %s", //NOI18N
                request.getType()));
        }
        final WorkspaceResolver workspaceRes = Lookup.getDefault().lookup(WorkspaceResolver.class);
        if (workspaceRes == null) {
            throw new IllegalStateException("No WorkspaceResolver registered"); //NOI18N
        }
        final Context ctx = request.getContext();
        if (ctx == null) {
            return false;
        }
        final FileObject file = workspaceRes.resolveFile(new WorkspaceResolver.Context(
                ctx.getUser(),
                ctx.getWorkspace(),
                ctx.getPath()
            ));
        if (file != null) {
            final Project prj = FileOwnerQuery.getOwner(file);
            if (prj != null) {
                final ActionProvider ap = prj.getLookup().lookup(ActionProvider.class);
                if (ap != null) {
                    ap.invokeAction(
                        request.getJava(),
                        Lookups.fixed(file, prj));
                }
            }
        }
        response.getStdout().addAll(java.util.Arrays.asList("Hello","World"));  //NOI18N
        response.getStderr().add("Error");  //NOI18N
        response.setSuccess(true);
        response.setStatus(Status.success);
        return true;
    }

}
