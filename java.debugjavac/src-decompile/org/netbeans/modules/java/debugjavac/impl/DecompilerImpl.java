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

import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javap.Context;
import com.sun.tools.javap.JavapTask;
import com.sun.tools.javap.JavapTask.BadArgs;
import com.sun.tools.javap.JavapTask.ClassFileInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.tools.DiagnosticListener;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.netbeans.modules.java.debugjavac.Decompiler;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lahvac
 */
@ServiceProvider(service=Decompiler.class)
public class DecompilerImpl implements Decompiler {

    @Override
    public Result decompile(FileObject source) {
        String code = Source.create(source).createSnapshot().getText().toString();
        StringWriter errors = new StringWriter();
        StringWriter decompiled = new StringWriter();
        try {
            final Map<String, byte[]> bytecode = compile(source, errors, code);

            if (!bytecode.isEmpty()) {
                for (final Entry<String, byte[]> e : bytecode.entrySet()) {
                    class JavapTaskImpl extends JavapTask {
                        public Context getContext() {
                            return context;
                        }
                    }
                    JavapTaskImpl t = new JavapTaskImpl();
                    List<String> options = new ArrayList<String>();
                    options.add("-private");
                    options.add("-verbose");
                    options.add(e.getKey());
                    t.handleOptions(options.toArray(new String[0]));
                    t.getContext().put(PrintWriter.class, new PrintWriter(decompiled));
                    ClassFileInfo cfi = t.read(new SimpleJavaFileObject(URI.create("mem://mem"), Kind.CLASS) {
                        @Override public InputStream openInputStream() throws IOException {
                            return new ByteArrayInputStream(e.getValue());
                        }
                    });

                    t.write(cfi);
                }
            }
        } catch (IOException | ConstantPoolException ex) {
            ex.printStackTrace(new PrintWriter(errors));
        } catch (BadArgs ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return new Result(errors.toString(), decompiled.toString(), "text/x-java-bytecode");
    }
    
    private static Map<String, byte[]> compile(FileObject source, final StringWriter errors, final String code) throws IOException {
        DiagnosticListener<JavaFileObject> errorsListener = Utilities.errorReportingDiagnosticListener(errors);
        StandardJavaFileManager sjfm = JavacTool.create().getStandardFileManager(errorsListener, null, null);
        final Map<String, ByteArrayOutputStream> class2BAOS = new HashMap<String, ByteArrayOutputStream>();

        JavaFileManager jfm = new ForwardingJavaFileManager<JavaFileManager>(sjfm) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, javax.tools.FileObject sibling) throws IOException {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                
                class2BAOS.put(className, buffer);
                return new SimpleJavaFileObject(sibling.toUri(), kind) {
                    @Override
                    public OutputStream openOutputStream() throws IOException {
                        return buffer;
                    }
                };
            }
        };

        JavaFileObject file = Utilities.sourceFileObject(code);
        JavacTool.create().getTask(null, jfm, errorsListener, /*XXX:*/Utilities.commandLineParameters(source), null, Arrays.asList(file)).call();

        Map<String, byte[]> result = new HashMap<String, byte[]>();

        for (Map.Entry<String, ByteArrayOutputStream> e : class2BAOS.entrySet()) {
            result.put(e.getKey(), e.getValue().toByteArray());
        }

        return result;
    }
    
    @Override
    public String id() {
        return DecompilerImpl.class.getName();
    }

    @Override
    public String displayName() {
        return "javap";
    }
    
}
