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

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.modules.debugger.jpda.JPDADebuggerImpl;
import org.netbeans.modules.dew4nb.endpoint.BasicRequestHandler;
import org.netbeans.modules.dew4nb.endpoint.RequestHandler;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.services.javac.JavacMessageType;
import org.netbeans.modules.dew4nb.services.javac.JavacQuery;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service=RequestHandler.class)
public class ContinueHandler extends BasicRequestHandler<JavacQuery, JavacMessageType, ContinueResult> {

    public ContinueHandler() {
        super(DebugerModels.END_POINT, JavacMessageType.cont, JavacQuery.class, ContinueResult.class);
    }

    @Override
    @NonNull
    protected Status handle(@NonNull final JavacQuery request, @NonNull final ContinueResult response) {
        Parameters.notNull("request", request); //NOI18N
        Parameters.notNull("response", response);   //NOI18N
        if (request.getType() != JavacMessageType.cont) {
            throw new IllegalStateException("Invalid message type: " + request.getType());  //NOI18N
        }
        Status status = Status.not_found;
        final int sessionId = request.getOffset();
        final WorkspaceResolver.Context ctx = ActiveSessions.getInstance().getContext(sessionId);
        if (ctx != null) {
            final Session debugSession = ActiveSessions.getInstance().getDebugSession(sessionId);
            if (debugSession == null) {
                throw new IllegalStateException("No debugger session.");    //NOI18N
            }
            final JPDADebugger jpda = debugSession.lookupFirst(null, JPDADebugger.class);
            if (!(jpda instanceof JPDADebuggerImpl)) {
                throw new IllegalStateException("Wrong debugger service.");    //NOI18N
            }
            ((JPDADebuggerImpl)jpda).resume();
            status = Status.done;
        }
        return status;
    }

}
