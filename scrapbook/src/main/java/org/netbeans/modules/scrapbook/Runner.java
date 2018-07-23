/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.scrapbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class Runner {

    public static void run(String clazz, ClassPath cp/*, ClassPath bootcp*/) throws Exception {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        final InputOutput io = IOProvider.getDefault().getIO("Scrapbook", false);
        //io.getOut().reset();
        DiagnosticListener<JavaFileObject> listener = new DiagnosticListener<JavaFileObject>() {
            @Override public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                io.getErr().println(diagnostic); // XXX format, hyperlink, etc.
            }
        };
        // XXX implement JavaFileManager directly for speed and robustness
        StandardJavaFileManager fm = javac.getStandardFileManager(listener, null, null);
        File d = new File(System.getProperty("java.io.tmpdir"));
        new File(d, "_.class").delete();
        final File s = new File(d, "_.java");
        Writer w = new FileWriter(s); // XXX use UTF-8
        try {
            w.write(clazz);
            w.flush();
        } finally {
            w.close();
        }
        List<String> opts = Arrays.asList("-d", d.getAbsolutePath(), "-classpath", cp.toString(ClassPath.PathConversionMode.WARN)/*, "-bootclasspath", bootcp.toString(ClassPath.PathConversionMode.WARN)*/);
        if (!javac.getTask(io.getErr(), fm, listener, opts, null, fm.getJavaFileObjects(s)).call()) {
            return;
        }
        fm.flush();
        fm.close();
        List<URL> runcp = new ArrayList<URL>();
        try {
            runcp.add(d.toURI().toURL());
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        for (ClassPath.Entry e : cp.entries()) {
            runcp.add(e.getURL());
        }
        /*
        for (ClassPath.Entry e : bootcp.entries()) {
            runcp.add(e.getURL());
        }
        */
        ClassLoader loader = new URLClassLoader(runcp.toArray(new URL[runcp.size()]), ClassLoader.getSystemClassLoader().getParent());
        Class<?> c = loader.loadClass("_");
        c.getField("_writer").set(null, io.getOut());
        c.getMethod("run").invoke(null);
    }

    public static ClassPath classpath(JEditorPane pane, String classpathType) {
        Object o = pane.getDocument().getProperty(Document.StreamDescriptionProperty);
        FileObject f;
        if (o instanceof FileObject) {
            f = (FileObject) o;
        } else if (o instanceof DataObject) {
            f = ((DataObject) o).getPrimaryFile();
        } else {
            return ClassPath.EMPTY;
        }
        Object projectU = f.getAttribute("project");
        if (!(projectU instanceof URL)) {
            return ClassPath.EMPTY;
        }
        FileObject projectD = URLMapper.findFileObject((URL) projectU);
        if (projectD == null || !projectD.isFolder()) {
            return ClassPath.EMPTY;
        }
        Project p;
        try {
            p = ProjectManager.getDefault().findProject(projectD);
        } catch (IOException x) {
            return ClassPath.EMPTY;
        }
        if (p == null) {
            return ClassPath.EMPTY;
        }
        List<ClassPath> all = new ArrayList<ClassPath>();
        for (SourceGroup g : ProjectUtils.getSources(p).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            ClassPath cp = ClassPath.getClassPath(g.getRootFolder(), classpathType);
            if (cp != null) {
                all.add(cp);
            }
        }
        return ClassPathSupport.createProxyClassPath(all.toArray(new ClassPath[all.size()]));
    }

    private Runner() {}

}
