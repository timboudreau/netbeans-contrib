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
package org.netbeans.modules.java.debugjavac.impl;

import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.util.Context.Factory;
import com.sun.tools.javac.util.Pair;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Queue;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import org.netbeans.modules.java.debugjavac.Decompiler;

/**
 *
 * @author lahvac
 */
public class DesugarDecompilerImpl implements Decompiler {

    @Override
    public Result decompile(Input input) {
        StringWriter errors = new StringWriter();
        StringWriter decompiled = new StringWriter();
        
        try {
            DiagnosticListener<JavaFileObject> errorsListener = Utilities.errorReportingDiagnosticListener(errors);
            JavaFileObject file = Utilities.sourceFileObject(input.source);
            JavacTask task = JavacTool.create().getTask(null, 
                    null,
                    errorsListener, Utilities.augmentCommandLineParameters(input), null, Arrays.asList(file));

            JavaCompilerOverride.preRegister(((JavacTaskImpl) task).getContext(), decompiled);
            task.generate();
        } catch (IOException ex) {
            ex.printStackTrace(new PrintWriter(errors));
        }
        
        return new Result(errors.toString(), decompiled.toString().trim(), "text/x-java");
    }
    
    static class JavaCompilerOverride extends JavaCompiler {
        public static void preRegister(com.sun.tools.javac.util.Context context, final StringWriter out) {
            context.put(compilerKey, new Factory<JavaCompiler>() {
                @Override public JavaCompiler make(com.sun.tools.javac.util.Context c) {
                    return new JavaCompilerOverride(out, c);
                }
            });
        }
        private final StringWriter out;

        public JavaCompilerOverride(StringWriter out, com.sun.tools.javac.util.Context context) {
            super(context);
            this.out = out;
        }
        
        @Override public void generate(Queue<Pair<Env<AttrContext>, JCClassDecl>> queue, Queue<JavaFileObject> results) {
            Pair<Env<AttrContext>, JCClassDecl> first = queue.peek();

            if (first != null) {
                if (first.fst.toplevel.getPackageName() != null) {
                    out.write("package ");
                    out.write(first.fst.toplevel.getPackageName().toString());
                    out.write(";\n\n");
                }

                boolean hasImports = false;
                
                for (Tree importCandidate : first.fst.toplevel.defs) {
                    if (importCandidate != null && importCandidate.getKind() == Kind.IMPORT) {
                        out.write("import ");
                        ImportTree importTree = (ImportTree) importCandidate;
                        if (importTree.isStatic()) {
                            out.write("static ");
                        }
                        out.write(importTree.getQualifiedIdentifier().toString());
                        out.write(";\n");
                        hasImports = true;
                    }
                }

                if (hasImports) {
                    out.write("\n");
                }

                for (Pair<Env<AttrContext>, JCClassDecl> q : queue) {
                    out.write(q.snd.toString());
                    out.write("\n");
                }
            }
        }
    }
}
