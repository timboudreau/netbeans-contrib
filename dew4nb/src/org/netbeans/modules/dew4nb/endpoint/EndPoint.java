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
 *//*
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

package org.netbeans.modules.dew4nb.endpoint;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.java.html.json.Models;
import org.glassfish.grizzly.websockets.WebSocket;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
public abstract class EndPoint<Request, RequestKind extends Enum<RequestKind>> {

    private static final Object handlersCacheLock = new Object();
    //@GuardedBy("handlersCacheLock")
    private static Map<String,Collection<RequestHandler<?,?>>> handlersCache;
    private final static boolean measuringEnabled="true".equals(System.getProperty("tailwindmeasurementenabled"));  //NOI18N
    private final Object cacheLock = new Object();
    //@GuardedBy("cacheLock")
    private Map<RequestKind, RequestHandler<Request, RequestKind>> cache;


    private final String name;
    private final Class<Request> requestType;
    private final Class<RequestKind> requestKindType;

    protected EndPoint(
        @NonNull final String name,
        @NonNull final Class<Request> requestType,
        @NonNull final Class<RequestKind> requestKindType) {
        Parameters.notNull("name", name);   //NOI18N
        Parameters.notNull("requestType", requestType); //NOI18N
        Parameters.notNull("requestKindType", requestKindType); //NOI18N
        this.name = name;
        this.requestType = requestType;
        this.requestKindType = requestKindType;
    }

    @NonNull
    public final String getName() {
        return name;
    }

    @NonNull
    public final Status handle(WebSocket ws, String request) throws Exception {
        try (ByteArrayInputStream is = new ByteArrayInputStream(request.getBytes("UTF-8"))) {
            Request query = Models.parse(getRequestType(), is);
            final RequestHandler<Request, RequestKind> h = getHandleFor(query);
            if (h != null) {
                return h.perform(query, new Env(ws, getName()));
            } else {
                return Status.not_found;
            }
        }
    }

    @CheckForNull
    protected abstract RequestKind getRequestKind(@NonNull Request query);

    @NonNull
    private Class<Request> getRequestType() {
        return requestType;
    }

    @NonNull
    private Class<RequestKind> getRequestKindType() {
        return requestKindType;
    }

    @CheckForNull
    @SuppressWarnings("unchecked")
    private  RequestHandler<Request, RequestKind> getHandleFor(@NonNull final Request query) {
        final RequestKind kind = getRequestKind(query);
        if (kind == null) {
            return null;
        }
        synchronized (cacheLock) {
            if (cache == null) {
                cache = new EnumMap<>(getRequestKindType());
                for (RequestHandler<?,?> rh : getRequestHandlers(getName())) {
                    if (getRequestType() != rh.requestType) {
                        continue;
                    }
                    if (!getRequestKindType().isInstance(rh.requestKind)) {
                        continue;
                    }
                    cache.put(
                        (RequestKind)rh.requestKind,
                        (RequestHandler<Request, RequestKind>)rh);
                }
            }
            return cache.get(kind);
        }
    }

    @NonNull
    private static  Collection<? extends RequestHandler<?,?>> getRequestHandlers(@NonNull final String name) {
        Parameters.notNull("name", name);   //NOI18N
        synchronized (handlersCacheLock) {
            if (handlersCache == null) {
                final Map<String, Collection<RequestHandler<?,?>>> handlers = new HashMap<>();
                for (RequestHandler<?,?> rh : Lookup.getDefault().lookupAll(RequestHandler.class)) {
                    Collection<RequestHandler<?,?>> line = handlers.get(rh.endPointName);
                    if (line == null) {
                        line = new ArrayList<>();
                        handlers.put(rh.endPointName, line);
                    }
                    line.add(rh);
                }
                handlersCache = handlers;
            }
            return handlersCache.get(name);
        }
    }

    public static final class Env {

        private final WebSocket ws;
        private final String endPointName;
        private final Map<String,Object> properties;
        private volatile boolean closed;

        private Env (
                @NonNull final WebSocket ws,
                @NonNull final String endPointName) {
            Parameters.notNull("ws", ws);   //NOI18N
            Parameters.notNull("endPointName", endPointName);   //NOI18N
            this.ws = ws;
            this.endPointName = endPointName;
            this.properties = new ConcurrentHashMap<>();
        }

        public void sendObject(Object object) {
            sendMessage(object == null ?
                null :
                object.toString());
        }

        public void sendMessage(@NullAllowed final String message) {
            if (closed) {
                throw new IllegalStateException("Env already closed");  //NOI18N
            }
            if (measuringEnabled) {
                long timeinside=Utilities.getMeasuredTime(System.currentTimeMillis(), ws.hashCode());
                ws.send(message == null ? "null" :( (timeinside<0)? message : (message +"@"+timeinside) ) );    //NOI18N
            } else {
                ws.send(message == null ? "null" : ( message) );    //NOI18N
            }
        }

        public void setProperty(
                @NonNull final String propName,
                @NullAllowed final Object value) {
            Parameters.notNull("propName", propName);   //NOI18N
            if (value == null) {
                properties.remove(propName);
            } else {
                properties.put(propName, value);
            }
        }

        @CheckForNull
        public <T> T getProperty(
                @NonNull final String propName,
                @NonNull final Class<? extends T> clz) {
            Parameters.notNull("propName", propName);   //NOI18N
            Parameters.notNull("clz", clz); //NOI18N
            return clz.cast(properties.get(propName));
        }

        void close() {
            closed = true;
        }
    }
}
