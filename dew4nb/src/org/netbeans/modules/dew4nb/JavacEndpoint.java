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
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import net.java.html.BrwsrCtx;
import net.java.html.json.Model;
import net.java.html.json.Models;
import net.java.html.json.Property;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.openide.util.Lookup;


/** The end point one can use to communicate with Javac service. Also defines
 * the WebSocket protocol between the client and the server.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public final class JavacEndpoint {

    private static final Logger LOG = Logger.getLogger(JavacEndpoint.class.getName());
    private final Object lock = new Object();
    private final Lookup.Result<RequestHandler> result;
    //@GuardedBy("lock")
    private Map<JavacMessageType,RequestHandler> handlers;

    private JavacEndpoint() {
        this.result = Lookup.getDefault().lookupResult(RequestHandler.class);
    }
    
    public static JavacEndpoint newCompiler() {
        return new JavacEndpoint();
    }
    

    @NonNull
    public Object doCompile(String query) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(query.getBytes("UTF-8"));
        JavacQuery q = Models.parse(BrwsrCtx.findDefault(JavacQuery.class), JavacQuery.class, is);
        is.close();
        return doCompile(q);
    }

    @NonNull
    public Object doCompile(JavacQuery query) throws IOException {
        final JavacMessageType type = query.getType();
        final RequestHandler h = type == null ? null : getHandleFor(type);
        if (h != null) {
            try {
                final Object res = h.response.getDeclaredConstructor().newInstance();
                try {
                    final Method set = h.response.getDeclaredMethod("setType", JavacMessageType.class); //NOI18N
                    set.invoke(res, query.getType());
                } catch (NoSuchMethodException noSetter) {
                    LOG.log(
                        Level.WARNING,
                        "The {0} has no type setter.",  //NOI18N
                        res);
                }
                try {
                    final Method set = h.response.getDeclaredMethod("setState", String.class);  //NOI18N
                    set.invoke(res, query.getState());
                } catch (NoSuchMethodException noSetter) {
                    LOG.log(
                        Level.WARNING,
                        "The {0} has no state setter.", //NOI18N
                        res);
                }
                return h.handle(query, res) ?
                    res :
                    error(Status.runtime_error, "Unhandled request", query);    //NOI18N
            } catch (ReflectiveOperationException |
                     IllegalArgumentException ex) {
                return error(Status.runtime_error, ex.getMessage(), query);
            }
        }        
        return error(Status.not_found, null, query);
    }

    @NonNull
    public JavacFailure error (
        @NonNull final Status status,
        @NullAllowed final String message,
        @NullAllowed final JavacQuery query) {
        final JavacFailure fail = new JavacFailure();
        fail.setStatus(status);
        if (query != null) {
            fail.setType(query.getType());
            fail.setState(query.getState());
        }
        fail.setMessage(message == null ? "" : message);    //NOI18N
        return fail;
    }

    @CheckForNull
    private RequestHandler getHandleFor(@NonNull final JavacMessageType type) {
        synchronized (lock) {
            if (handlers == null) {
                handlers = new EnumMap<>(JavacMessageType.class);
                for (RequestHandler h : this.result.allInstances()) {
                    assert h.type != null;
                    assert h.request == JavacQuery.class;
                    assert h.response != null;                                        
                    handlers.put(h.type, h);
                }
            }
            return handlers.get(type);
        }
    }

    @Model(className = "JavacQuery", properties = {
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "context", type=Context.class),
        @Property(name = "java", type = String.class),
        @Property(name = "offset", type = int.class)
    })
    static final class JavacQueryModel {
    }    

    @Model(className = "JavacCompletionResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "completions", type = CompletionItem.class, array = true)
    })
    static final class JavacCompletionResultModel {
    }

    @Model(className = "JavacDiagnosticsResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "diagnostics", type = JavacDiagnostic.class, array = true)
    })
    static final class JavacDiagnosticsResultModel {
    }

    @Model(className = "JavacTypeResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "types", type = TypeDescriptor.class, array = true)
    })
    static final class JavacTypeResultModel {
    }


    @Model(className = "FileContentResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "content", type = String.class)
    })
    static final class FileContentResultModel {
    }

    @Model(className = "JavacFailure", properties = {
        @Property(name="status", type=Status.class),
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name="message", type=String.class)
    })
    static final class JavacFailureModel {
    }

    @Model(className = "Context", properties = {
        @Property(name="user", type=String.class),
        @Property(name = "workspace", type = String.class),
        @Property(name = "path", type = String.class)
    })
    static final class ContextModel {
    }


//    @Model(className = "JavacResult", properties = {
//        @Property(name = "type", type = JavacMessageType.class),
//        @Property(name = "state", type = String.class),
//        @Property(name = "status", type = String.class),
//        @Property(name = "errors", type = JavacError.class, array = true),
//        @Property(name = "classes", type = JavacClass.class, array = true),
//        @Property(name = "completions", type = CompletionItem.class, array = true)
//    })
//    static final class JavacResultModel {
//    }



    @Model(className = "JavacDiagnostic", properties = {
        @Property(name = "col", type = long.class),
        @Property(name = "line", type = long.class),
        @Property(name = "kind", type = Diagnostic.Kind.class),
        @Property(name = "msg", type = String.class)
    })
    static final class JavacDiagnosticModel {        
    }

    @Model(className = "JavacClass", properties = {
        @Property(name = "className", type = String.class),
        @Property(name = "byteCode", type = byte.class, array = true)
    })
    static final class JavacClassModel {
    }
    
    @Model(className = "CompletionItem", properties = {
        @Property(name = "text", type = String.class),
        @Property(name = "displayName", type = String.class),
        @Property(name = "extraText", type = String.class),
        @Property(name = "rightText", type = String.class),
        @Property(name = "className", type = String.class),
    })
    static final class CompletionItemModel {
    }

    @Model(className = "TypeDescriptor", properties = {
        @Property(name = "name", type = String.class),
        @Property(name = "owner", type = String.class),
        @Property(name = "context", type = Context.class),
    })
    static final class TypeDescriptorModel {
    }
    
}
