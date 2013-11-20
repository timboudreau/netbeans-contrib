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
import java.util.Locale;
import java.util.logging.Logger;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import net.java.html.BrwsrCtx;
import net.java.html.json.Model;
import net.java.html.json.Models;
import net.java.html.json.Property;
import org.openide.util.Lookup;


/** The end point one can use to communicate with Javac service. Also defines
 * the WebSocket protocol between the client and the server.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public final class JavacEndpoint {
    private JavacEndpoint() {
    }
    
    public static JavacEndpoint newCompiler() {
        return new JavacEndpoint();
    }
    

    public JavacResult doCompile(String query) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(query.getBytes("UTF-8"));
        JavacQuery q = Models.parse(BrwsrCtx.findDefault(JavacQuery.class), JavacQuery.class, is);
        is.close();
        return doCompile(q);
    }
    
    public JavacResult doCompile(JavacQuery query) throws IOException {
        
        for (RequestHandler h : Lookup.getDefault().lookupAll(RequestHandler.class)) {
            if (h.request == JavacQuery.class || h.response == JavacResult.class) {
                JavacResult res = new JavacResult();
                res.setType(query.getType());
                res.setState(query.getState());
                if (h.handle(query, res)) {
                    return res;
                }
            }
        }
        JavacResult res = new JavacResult();
        res.setType(query.getType());
        res.setState(query.getState());
        res.setStatus("Nothing to do!");
        return res;
    }

    @Model(className = "JavacQuery", properties = {
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "html", type = String.class),
        @Property(name = "java", type = String.class),
        @Property(name = "offset", type = int.class)
    })
    static final class JavacQueryModel {
    }

    @Model(className = "JavacResult", properties = {
        @Property(name = "type", type = JavacMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "status", type = String.class),
        @Property(name = "errors", type = JavacError.class, array = true),
        @Property(name = "classes", type = JavacClass.class, array = true),
        @Property(name = "completions", type = CompletionItem.class, array = true)
    })
    static final class JavacResultModel {
    }

    @Model(className = "JavacError", properties = {
        @Property(name = "col", type = long.class),
        @Property(name = "line", type = long.class),
        @Property(name = "kind", type = Diagnostic.Kind.class),
        @Property(name = "msg", type = String.class)
    })
    static final class JavacErrorModel {
        static JavacError create(Diagnostic<? extends JavaFileObject> d) {
            return new JavacError(
                    d.getColumnNumber(),
                    d.getLineNumber(),
                    d.getKind(),
                    d.getMessage(Locale.ENGLISH)
            );
        }
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
    
}
