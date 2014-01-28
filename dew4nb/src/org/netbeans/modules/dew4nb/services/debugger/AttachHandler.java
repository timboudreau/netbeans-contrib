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

import java.util.concurrent.Executor;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.dew4nb.endpoint.AsyncRequestHandler;
import org.netbeans.modules.dew4nb.endpoint.EndPoint;
import org.netbeans.modules.dew4nb.endpoint.RequestHandler;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.services.javac.Context;
import org.netbeans.modules.dew4nb.services.javac.JavacMessageType;
import org.netbeans.modules.dew4nb.services.javac.JavacQuery;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = RequestHandler.class)
public class AttachHandler extends AsyncRequestHandler<JavacQuery, JavacMessageType> {

    private static final Executor RP = new RequestProcessor(AttachHandler.class);    


    public AttachHandler() {
        super(DebugerModels.END_POINT, JavacMessageType.attach, JavacQuery.class);        
    }

    @Override
    @NonNull
    protected Status handle(@NonNull final JavacQuery request, @NonNull final EndPoint.Env env) {
        final JavacMessageType type = request.getType();
        if (type != JavacMessageType.attach) {
            throw new IllegalStateException("Wrong message type:" + type);  //NOI18N
        }
        final Context ctx = request.getContext();
        if (ctx != null) {
            throw new IllegalArgumentException("Missing context");  //NOI18N
        }
        final String state = request.getState();
        if (state == null) {
            throw new IllegalArgumentException("Missing state");       //NOI18N
        }
        final WorkspaceResolver resolver = Lookup.getDefault().lookup(WorkspaceResolver.class);
        if (resolver == null) {
            throw new IllegalStateException("No WorkspaceResolver");    //NOI18N
        }
        RP.execute(new Runnable() {
            @Override
            public void run() {                
                final WorkspaceResolver.Context serverCtx = new WorkspaceResolver.Context(
                   ctx.getUser(),
                   ctx.getWorkspace(),
                   ""   //NOI18N
                );
                Status status = Status.not_found;
                int id = -1;
                final FileObject workspace = resolver.resolveFile(serverCtx);                
                if (workspace != null) {
                    id = ActiveSessions.getInstance().createSession(serverCtx, env);
                    status = Status.done;
                }
                final AttachResult attachResult = new AttachResult();
                attachResult.setId(id);
                attachResult.setType(type);
                attachResult.setState(state);
                attachResult.setStatus(status);
                env.sendObject(attachResult);
            }
        });
        return Status.accepted;
    }
}
