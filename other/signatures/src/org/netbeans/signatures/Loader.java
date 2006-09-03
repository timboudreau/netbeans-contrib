/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.signatures;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
    
    protected Loader(File... cp) {
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
        JavaFileObject dummyCompilationUnit = new SimpleJavaFileObject(URI.create("nowhere:/Dummy.java"), JavaFileObject.Kind.SOURCE) {
            public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                return "public class Dummy {}";
            }
        };
        StringWriter err = new StringWriter();
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(
                err,
                nullOutputFileManager(),
                null,
                Arrays.asList("-source", "1.6", "-classpath", cps.toString()),
                null,
                Collections.singleton(dummyCompilationUnit));
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
    
    public static JavaFileManager nullOutputFileManager() {
        final JavaFileObject devNull = new SimpleJavaFileObject(URI.create("no:/where"), JavaFileObject.Kind.OTHER) {
            public OutputStream openOutputStream() throws IOException {
                return new ByteArrayOutputStream();
            }
            public Writer openWriter() throws IOException {
                return new StringWriter();
            }
        };
        return new JavaFileManager() {
            JavaFileManager standard = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);
            public void close() throws IOException {
                standard.close();
            }
            public void flush() throws IOException {
                standard.flush();
            }
            public ClassLoader getClassLoader(Location location) {
                return standard.getClassLoader(location);
            }
            public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
                return standard.getFileForInput(location, packageName, relativeName);
            }
            public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
                return devNull;
            }
            public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
                return standard.getJavaFileForInput(location, className, kind);
            }
            public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
                return devNull;
            }
            public boolean handleOption(String current, Iterator<String> remaining) {
                return standard.handleOption(current, remaining);
            }
            public boolean hasLocation(Location location) {
                return standard.hasLocation(location);
            }
            public String inferBinaryName(Location location, JavaFileObject file) {
                return standard.inferBinaryName(location, file);
            }
            public boolean isSameFile(FileObject a, FileObject b) {
                return standard.isSameFile(a, b);
            }
            public int isSupportedOption(String option) {
                return standard.isSupportedOption(option);
            }
            public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
                return standard.list(location, packageName, kinds, recurse);
            }
        };
    }
    
}
