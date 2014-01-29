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

import com.sun.jdi.AbsentInformationException;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.modules.debugger.jpda.JPDADebuggerImpl;
import org.netbeans.modules.dew4nb.endpoint.EndPoint;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
final class ActiveSessions {

     private static final Logger LOG = Logger.getLogger(ActiveSessions.class.getName());

    //@GuardedBy("ActiveSessions.class")
    private static ActiveSessions instance;

    private final ConcurrentMap<Integer,Data> active;
    private final AtomicInteger sequencer;

    private ActiveSessions() {
        this.active = new ConcurrentHashMap<>();
        this.sequencer = new AtomicInteger();
    }


    int createSession(
            @NonNull final WorkspaceResolver.Context context,
            @NonNull final EndPoint.Env env) {
        Parameters.notNull("context", context); //NOI18N
        Parameters.notNull("env", env); //NOI18N
        final int id = sequencer.incrementAndGet();
        final Session session = DebuggerManager.getDebuggerManager().getCurrentSession();
        if (active.putIfAbsent(id, new Data(id, context, env, session)) != null) {
            throw new IllegalStateException("Trying to reuse active session");  //NOI18N
        }
        return id;
    }

    @CheckForNull
    WorkspaceResolver.Context getContext(final int sessionId) {
        final Data data = active.get(sessionId);
        return data == null ? null : data.ctx;
    }

    @CheckForNull
    EndPoint.Env getEnv(final int sessionId) {
        final Data data = active.get(sessionId);
        return data == null ? null : data.env;
    }

    @CheckForNull
    Session getDebugSession(final int sessionId) {
        final Data data = active.get(sessionId);
        return data == null ? null : data.session;
    }


    @NonNull
    static synchronized ActiveSessions getInstance() {
        if (instance == null) {
            instance = new ActiveSessions();
        }
        return instance;
    }

    private static final class Data implements PropertyChangeListener {
        final int id;
        final WorkspaceResolver.Context ctx;
        final EndPoint.Env env;
        final Session session;
        final JPDADebugger jpda;
        volatile JPDAThread currentThread;



        private Data(
            final int id,
            @NonNull final WorkspaceResolver.Context ctx,
            @NonNull final EndPoint.Env env,
            @NonNull final Session session) {
            Parameters.notNull("ctx", ctx); //NOI18N
            Parameters.notNull("env", env); //NOI18N
            Parameters.notNull("session", session); //NOI18N
            this.id = id;
            this.ctx = ctx;
            this.env = env;
            this.session = session;
            this.jpda = this.session.lookupFirst(null, JPDADebugger.class);
            if (!(jpda instanceof JPDADebuggerImpl)) {
                throw new IllegalStateException("Wrong debugger service.");    //NOI18N
            }
            jpda.addPropertyChangeListener(this);
        }


        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final String propName = evt.getPropertyName();
            if (JPDADebugger.PROP_CURRENT_THREAD.equals(propName)) {
                if (currentThread != null) {
                    ((Customizer)currentThread).removePropertyChangeListener(this);
                }
                currentThread = jpda.getCurrentThread();
                if (currentThread != null) {
                    ((Customizer)currentThread).addPropertyChangeListener(this);
                }
            } else if (JPDAThread.PROP_SUSPENDED.equals(propName)) {
                CallStackFrame[] callStack = null;
                try {
                     callStack = currentThread.getCallStack();
                } catch (AbsentInformationException aie) {/*pass, no -g*/}
                env.sendObject(createSuspendResult(callStack));                
            }
        }

        @NonNull
        private SuspendResult createSuspendResult(@NullAllowed CallStackFrame[] callStack) {
            final SuspendResult res = new SuspendResult();
            res.setId(id);
            res.setStatus(Status.done);
            res.setType(DebugMessageType.suspended);
            if (callStack != null) {
                final WorkspaceResolver wr = WorkspaceResolver.getDefault();
                if (wr == null) {
                    throw new IllegalStateException("No workspace resolver.");  //NOI18N
                }
                final FileObject root = wr.resolveFile(ctx);
                final SourcePathProvider spp = session.lookupFirst(null, SourcePathProvider.class);
                for (CallStackFrame csf : callStack) {
                    String relativePath;
                    try {
                        relativePath = csf.getSourcePath(null).replace (File.separatorChar, '/');
                    } catch (AbsentInformationException e) {
                        relativePath = "<unknown>";
                    }
                    final String surl = spp.getURL (relativePath, true);
                    if (surl != null) {
                        try {
                           final FileObject fo = URLMapper.findFileObject(new java.net.URL(surl));
                           if (root != null && fo != null && FileUtil.isParentOf(root, fo)) {
                               relativePath = FileUtil.getRelativePath(root, fo);
                           }
                        } catch (java.net.MalformedURLException muex) {
                            LOG.log(
                                Level.WARNING,
                                "Malformed URL {0}",    //NOI18N
                                surl);
                        }
                    }
                    res.getStack().add(String.format(
                        "%s:%d",
                        relativePath,
                        csf.getLineNumber(null)));
                }
            }
            return res;
         }

    }

}
