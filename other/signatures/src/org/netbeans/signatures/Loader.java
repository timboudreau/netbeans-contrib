/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.signatures;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * Loads structural information from bytecode.
 * @author Jesse Glick
 */
public abstract class Loader {
    
    private Elements elements;
    private Types types;
    private RuntimeException exception;
    private Error error;
    
    protected Loader(Collection<File> cp) {
        StringBuilder cps = new StringBuilder();
        for (File p : cp) {
            if (!p.exists()) {
                throw new IllegalArgumentException("No such classpath element " + p);
            }
            if (cps.length() > 0) {
                cps.append(File.pathSeparatorChar);
            }
            cps.append(p);
        }
        StringWriter err = new StringWriter();
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(
                err,
                nullOutputFileManager(),
                null,
                Arrays.asList("-source", "1.6", "-classpath", cps.toString()),
                null,
                Collections.singleton(dummyCompilationUnit()));
        task.setProcessors(Collections.singleton(new LoaderProcessor()));
        boolean ok = task.call();
        String errors = err.toString();
        assert ok : errors;
        assert errors.length() == 0 : errors;
        if (exception != null) {
            throw exception;
        } else if (error != null) {
            throw error;
        }
    }
    
    protected abstract void run();
    
    protected final Elements elements() {
        return elements;
    }
    
    protected final Types types() {
        return types;
    }
    
    @SupportedAnnotationTypes("*")
    @SupportedSourceVersion(SourceVersion.RELEASE_6)
    private final class LoaderProcessor extends AbstractProcessor {
        
        private boolean ran = false;
        
        LoaderProcessor() {}
        
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (!ran) {
                ran = true;
            } else {
                return true;
            }
            elements = processingEnv.getElementUtils();
            assert elements != null;
            types = processingEnv.getTypeUtils();
            assert types != null;
            try {
                run();
            } catch (ThreadDeath t) {
                throw t;
            } catch (RuntimeException t) {
                exception = t;
            } catch (Error t) {
                error = t;
            }
            elements = null;
            types = null;
            return true;
        }
        
    }
    
    public static JavaFileObject dummyCompilationUnit() {
        return new SimpleJavaFileObject(URI.create("nowhere:/Dummy.java"), JavaFileObject.Kind.SOURCE) {
            public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                return "public class Dummy {}";
            }
        };
    }
    
    public static JavaFileManager nullOutputFileManager() {
        return new ForwardingJavaFileManager<JavaFileManager>(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null)) {
            JavaFileObject devNull = new SimpleJavaFileObject(URI.create("no:/where"), JavaFileObject.Kind.OTHER) {
                public OutputStream openOutputStream() throws IOException {
                    return new ByteArrayOutputStream();
                }
                public Writer openWriter() throws IOException {
                    return new StringWriter();
                }
            };
            public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
                return devNull;
            }
            public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
                return devNull;
            }
        };
    }
    
}
