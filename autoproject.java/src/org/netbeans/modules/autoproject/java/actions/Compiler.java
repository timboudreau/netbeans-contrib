/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java.actions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 * Runs Java compiler on some source files.
 * Integrated with NB output window (hyperlinks etc.).
 * @author Jesse Glick
 */
public class Compiler {

    private Compiler() {}

    public static boolean compile(Collection<File> files, List<String> options) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IOException("No compiler!");
        }
        InputOutput io = IOProvider.getDefault().getIO(/*XXX I18N*/"Compiler", false);
        io.getOut().reset();
        io.getOut().print(/*XXX I18N*/"Running: javac");
        for (String o : options) {
            io.getOut().print(" " + o);
        }
        for (File f : files) {
            io.getOut().print(" " + f);
        }
        io.getOut().println("");
        DiagnosticListener<JavaFileObject> diag = new Diag(io);
        boolean ok = compiler.getTask(io.getErr(), null, diag, options, null,
                compiler.getStandardFileManager(diag, null, null).getJavaFileObjectsFromFiles(files)).
                call();
        if (ok) {
            io.getOut().println(/*XXX I18N*/"Compilation successful.");
        } else {
            io.getOut().println(/*XXX I18N*/"Compilation failed.");
        }
        io.getOut().close();
        io.getErr().close();
        return ok;
    }

    private static final class Diag implements DiagnosticListener<JavaFileObject> {

        private final InputOutput io;

        public Diag(InputOutput io) {
            this.io = io;
        }

        public void report(Diagnostic<? extends JavaFileObject> d) {
            OutputWriter w = (d.getKind() == Diagnostic.Kind.NOTE || d.getKind() == Diagnostic.Kind.OTHER) ? io.getOut() : io.getErr();
            JavaFileObject f = d.getSource();
            if (f == null) {
                w.println(d.getMessage(null));
            } else {
                boolean important = d.getKind() == Diagnostic.Kind.ERROR;
                if (important) {
                    io.select();
                }
                try {
                    String[] msgs = d.getMessage(null).split("\n");
                    for (int i = 0; i < msgs.length; i++) {
                        if (i == 0) {
                            w.println(msgs[0], new Hyperlink(d), important);
                        } else {
                            // Do not hyperlink every line, it's just ugly-looking.
                            w.println(msgs[i]);
                        }
                    }
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                }
            }
        }

        /**
         * See org.apache.tools.ant.module.run.Hyperlink for more power.
         */
        private static final class Hyperlink implements OutputListener {

            private final Diagnostic<? extends JavaFileObject> d;

            public Hyperlink(Diagnostic<? extends JavaFileObject> d) {
                this.d = d;
            }

            private EditorCookie ed() throws IOException {
                URI u = d.getSource().toUri();
                if (!"file".equals(u.getScheme())) {
                    // XXX #6450078 bug in DefaultFileManager.RegularFileObject.toUri
                    u = new File(u.toString()).toURI();
                }
                FileObject f = URLMapper.findFileObject(u.toURL());
                if (f == null) {
                    return null;
                }
                return DataObject.find(f).getCookie(EditorCookie.class);
            }

            private void show(EditorCookie ed, int kind) {
                ed.getLineSet().getOriginal((int) d.getLineNumber() - 1).show(kind, (int) d.getColumnNumber() - 1);
            }

            public void outputLineAction(OutputEvent ev) {
                try {
                    EditorCookie ed = ed();
                    if (ed == null) {
                        return;
                    }
                    if (d.getPosition() == Diagnostic.NOPOS) {
                        ed.open();
                    } else {
                        show(ed, Line.SHOW_TOFRONT);
                    }
                    URI u = d.getSource().toUri();
                    File f;
                    if ("file".equals(u.getScheme())) {
                        f = new File(u);
                    } else {
                        // XXX see above
                        f = new File(u.toString());
                    }
                    StatusDisplayer.getDefault().setStatusText(d.getMessage(null).
                            replaceFirst("^\\Q" + f + "\\E(:[0-9]+)?: ", "").
                            replaceFirst("(?s)\n.*", ""));
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                }
            }

            public void outputLineSelected(OutputEvent ev) {
                try {
                    EditorCookie ed = ed();
                    if (ed == null) {
                        return;
                    }
                    if (d.getPosition() != Diagnostic.NOPOS) {
                        show(ed, Line.SHOW_TRY_SHOW);
                    }
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                }
            }

            public void outputLineCleared(OutputEvent ev) {
                // do nothing
            }

        }

    }

}
