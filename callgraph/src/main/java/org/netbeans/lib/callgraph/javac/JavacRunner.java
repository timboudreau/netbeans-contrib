/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.lib.callgraph.javac;

import org.netbeans.lib.callgraph.Listener;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTrees;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * Runs javac with our visitors to construct the usage graph for each method.
 *
 * @author Tim Boudreau
 */
public class JavacRunner {

    private final DiagnosticListener diagnostics = new Diagnostics();
    private final Iterable<File> files;
    private final Iterable<? extends File> outdir = Collections.singleton(new File(System.getProperty("java.io.tmpdir")));
    private final Listener listener;
    private final SourcesInfo info;

    public JavacRunner(SourcesInfo info, Iterable<File> files) {
        this(info, files, new NullListener());
    }

    public JavacRunner(SourcesInfo info, Iterable<File> files, Listener listener) {
        this.files = files;
        this.listener = listener == null ? new NullListener() : listener;
        this.info = info;
    }

    private Iterable<? extends JavaFileObject> javaFileObjects(StandardJavaFileManager m, Consumer<File> monitor) {
        return new JavaFileObjectIterable(files, m, monitor);
    }

    /**
     * Iterable that converts an Iterable<File> to an Iterable<JavaFileObject>
     */
    private static final class JavaFileObjectIterable implements Iterable<JavaFileObject> {

        private final Iterable<File> files;
        private final StandardJavaFileManager mgr;
        private final Consumer<File> consumer;

        public JavaFileObjectIterable(Iterable<File> files, final StandardJavaFileManager mgr, Consumer<File> consumer) {
            this.files = files;
            this.mgr = mgr;
            this.consumer = consumer;
        }

        @Override
        public Iterator<JavaFileObject> iterator() {
            final Iterator<File> fi = files.iterator();
            return new Iterator<JavaFileObject>() {

                @Override
                public boolean hasNext() {
                    return fi.hasNext();
                }

                @Override
                public JavaFileObject next() {
                    File f = fi.next();
                    consumer.accept(f);
                    return mgr.getJavaFileObjects(f).iterator().next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private List<String> options() {
        // Borrowed from NetBeans
        List<String> options = new ArrayList<>(9);
        options.add("-XDide");   // Javac runs inside the IDE
        options.add("-XDsave-parameter-names");   // Javac runs inside the IDE
        options.add("-XDsuppressAbortOnBadClassFile");   // When a class file cannot be read, produce an error type instead of failing with an exception
        options.add("-XDshouldStopPolicy=GENERATE");   // Parsing should not stop in phase where an error is found
//        options.add("-attrparseonly");
        options.add("-g:source"); // Make the compiler maintian source file info
        options.add("-g:lines"); // Make the compiler maintain line table
        options.add("-g:vars");  // Make the compiler maintain local variables table
        options.add("-XDbreakDocCommentParsingOnError=false");  // Turn off compile fails for javadoc
        options.add("-proc:none"); // Do not try to run annotation processors
        return options;
    }

    public Set<SourceElement> go(Consumer<File> monitor, AtomicReference<File> lastFile) throws IOException {
        listener.onStart();
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(),
                    Charset.forName("UTF-8"));
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, outdir);
            Iterable<? extends JavaFileObject> toCompile = javaFileObjects(fileManager, monitor);

            JavacTaskImpl task = (JavacTaskImpl) compiler.getTask(null,
                    fileManager, diagnostics, options(), null, toCompile);
            return parse(task, lastFile).allElements;
        } finally {
            listener.onFinish();
        }
    }

    private SourcesInfo parse(JavacTaskImpl task, AtomicReference<File> lastFile) throws IOException {
        listener.onStartActivity("Finding and parsing Java sources", -1);
        JavacTrees trees = JavacTrees.instance(task.getContext());
        List<CompilationUnitTree> units = new LinkedList<>();
        // We need to cache these because calling parse() twice is an error
        for (CompilationUnitTree tree : task.parse()) {
            units.add(tree);
        }
        listener.onStartActivity("Attributing Java sources", -1);
        try {
            task.analyze(); // Run attribution - the compiler flags we have set will have it not abort on unresolvable classes
        } catch (Throwable err) {
            throw new Error("Thrown parsing " + lastFile.get(), err);
        }
        try {
            try {
                listener.onStartActivity("Cataloging methods", units.size());
                // First pass, find all methods in all classes
                for (CompilationUnitTree tree : units) {
                    listener.onStep(tree.getSourceFile().getName());
                    ElementFinder elementFinder = new ElementFinder(tree, trees);
                    elementFinder.scan(tree, info);
                }
                listener.onStartActivity("Finding usages", units.size());
                // Second pass, run find usages on every method we found
                for (CompilationUnitTree tree : units) {
                    listener.onStep(tree.getSourceFile().getName());
                    UsageFinder usageFinder = new UsageFinder(tree, trees);
                    usageFinder.scan(tree, info);
                }
            } finally {
                task.finish();
            }
        } finally {
            info.close(); // discard references
        }
        return info;
    }

    private static final class Diagnostics implements DiagnosticListener {

        @Override
        public void report(Diagnostic diagnostic) {
            // Discard diagnostics - we're not actually compiling to class files,
            // so we don't care about deprecations (or even failures, since we
            // only need references from the classes to themselves).
        }
    }

    private static final class NullListener implements Listener {

        @Override
        public void onStart() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onStartActivity(String activity, int steps) {

        }

        @Override
        public void onStep(String step) {

        }
    }
}
