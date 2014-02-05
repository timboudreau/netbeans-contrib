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

package org.netbeans.modules.dew4nb.services.debugger;

import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.dew4nb.endpoint.AsyncRequestHandler;
import org.netbeans.modules.dew4nb.endpoint.EndPoint;
import org.netbeans.modules.dew4nb.endpoint.RequestHandler;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.spi.project.ActionProgress;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Parameters;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = RequestHandler.class)
public class ApplyCodeChangesHandler extends AsyncRequestHandler<DebugAction, DebugMessageType> {

    public ApplyCodeChangesHandler () {
        super(DebugerModels.END_POINT, DebugMessageType.apply, DebugAction.class);
    }

    @Override
    protected Status handle(@NonNull final DebugAction request, @NonNull final EndPoint.Env env) {
        Parameters.notNull("request", request); //NOI18N
        Parameters.notNull("env", env);    //NOI18N
        if (request.getType() != DebugMessageType.apply) {
            throw new IllegalArgumentException("Wrong request type: " + request.getType()); //NOI18N
        }
        final int sessionId = request.getSession();
        Status result = Status.not_found;
        WorkspaceResolver.Context ctx = ActiveSessions.getInstance().getContext(sessionId);
        if (ctx != null) {
            final WorkspaceResolver wr = WorkspaceResolver.getDefault();
            if (wr == null) {
                throw new IllegalStateException("No WorkspaceResolver");    //NOI18N
            }
            ctx = new WorkspaceResolver.Context(
                ctx.getUser(),
                ctx.getWorkspace(),
                request.getContext().getPath());
            final FileObject file = wr.resolveFile(ctx);
            if (file != null) {
                final Project prj = FileOwnerQuery.getOwner(file);
                if (prj != null) {
                    final ActionProgress progress = new ActionProgress() {
                        @Override
                        protected void started() {
                        }
                        @Override
                        public void finished(final boolean success) {
                            final ApplyCodeChangesResult res = new ApplyCodeChangesResult();                            
                            res.setType(request.getType());
                            res.setState(request.getState());
                            res.setStatus(Status.done);
                            res.setResult(success);
                            env.sendObject(res);
                        }
                    };
                    final Lookup lkp = Lookups.fixed(file, prj, progress);
                    final ActionProvider ap = prj.getLookup().lookup(ActionProvider.class);
                    if (supportsFix(ap, lkp)) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ap.invokeAction(JavaProjectConstants.COMMAND_DEBUG_FIX, lkp);
                            }
                        });
                        result = Status.accepted;
                    }                    
                }
            }
        }
        return result;
    }

    private static boolean supportsFix(
            @NullAllowed final ActionProvider ap,
            @NonNull final Lookup lkp) {
        Parameters.notNull("lkp", lkp); //NOI18N
        if (ap == null) {
            return false;
        }
        if (!Arrays.asList(ap.getSupportedActions()).contains(
                JavaProjectConstants.COMMAND_DEBUG_FIX)) {
            return false;
        }
        return ap.isActionEnabled(JavaProjectConstants.COMMAND_DEBUG_FIX, lkp);
    }
}
