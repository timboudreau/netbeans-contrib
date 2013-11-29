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

package org.netbeans.modules.dew4nb.services;

import java.util.Collections;
import java.util.Locale;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.tools.Diagnostic;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.dew4nb.Context;
import org.netbeans.modules.dew4nb.JavacDiagnostic;
import org.netbeans.modules.dew4nb.JavacDiagnosticsResult;
import org.netbeans.modules.dew4nb.JavacMessageType;
import org.netbeans.modules.dew4nb.JavacQuery;
import org.netbeans.modules.dew4nb.RequestHandler;
import org.netbeans.modules.dew4nb.SourceProvider;
import org.netbeans.modules.dew4nb.Status;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = RequestHandler.class)
public class ErrorHandler extends RequestHandler<JavacQuery, JavacDiagnosticsResult>{

    public ErrorHandler() {
        super(JavacMessageType.checkForErrors, JavacQuery.class, JavacDiagnosticsResult.class);
    }

    @Override
    protected boolean handle(
            @NonNull final JavacQuery request,
            @NonNull final JavacDiagnosticsResult response) {
        Parameters.notNull("request", request); //NOI18N
        Parameters.notNull("response", response);  //NOI18N
        final JavacMessageType requestType = request.getType();
        if (requestType != JavacMessageType.checkForErrors) {
            throw new IllegalStateException(String.valueOf(requestType));
        }
        final String java = request.getJava();
        final Context ctx = request.getContext();
        final Source src = SourceProvider.getInstance().getSource(ctx, java);
        Status status = Status.runtime_error;
        if (src != null) {
            try {
                ParserManager.parse(
                        Collections.singleton(src),
                        new UserTask() {
                            @Override
                            public void run(@NonNull final ResultIterator resultIterator) throws Exception {
                                final Parser.Result res = resultIterator.getParserResult();
                                final CompilationController cc = CompilationController.get(res);
                                cc.toPhase(JavaSource.Phase.UP_TO_DATE);
                                final Document doc = cc.getDocument();
                                for (Diagnostic<?> d : cc.getDiagnostics()) {
                                    response.getDiagnostics().add(
                                        createJavacDiagnostic(d, doc));
                                }
                            }
                        });
                status = Status.success;
            } catch (ParseException ex) {
                //pass
            }
        }
        response.setStatus(status);
        return true;
    }

    private static JavacDiagnostic createJavacDiagnostic(Diagnostic<?> d, Document doc) {
        long line = d.getLineNumber();
        long col = d.getColumnNumber();
        long len = d.getEndPosition() - d.getStartPosition();
        if (len > 0 && doc instanceof BaseDocument) {
            try {
                line = Utilities.getLineOffset((BaseDocument) doc, (int) d.getStartPosition()) + 1;
                col = Utilities.getVisualColumn((BaseDocument) doc, (int) d.getStartPosition()) + 1;
            } catch (BadLocationException ble) {}
        }
        return new JavacDiagnostic(
                col,
                line,
                len > 0 ? len : 0,
                d.getKind(),
                d.getMessage(Locale.ENGLISH)
        );
    }

}
