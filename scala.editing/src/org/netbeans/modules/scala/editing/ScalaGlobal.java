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
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.util.Exceptions;
import scala.Nil$;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;
import scala.tools.nsc.ast.Trees.Tree;
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaGlobal {

    private static Map<FileObject, Reference<Global>> projectDirToGlobal =
            new WeakHashMap<FileObject, Reference<Global>>();

    public static Global getGlobal(FileObject fo) {
        Global global = null;

        Project project = FileOwnerQuery.getOwner(fo);
        FileObject prjDir = null;
        if (project != null) {
            prjDir = project.getProjectDirectory();
            if (prjDir != null) {
                Reference<Global> globalRef = projectDirToGlobal.get(prjDir);
                if (globalRef != null) {
                    global = globalRef.get();
                    if (global != null) {
                        return global;
                    } else {
                        projectDirToGlobal.remove(global);
                    }
                }
            }
        }

        final Settings settings = new Settings(null);
        settings.verbose().value_$eq(false);

        settings.classpath().tryToSet(Nil$.MODULE$.$plus("-classpath").$plus(""));
        settings.bootclasspath().tryToSet(Nil$.MODULE$.$plus("-bootclasspath").$plus(""));
        settings.sourcepath().tryToSet(Nil$.MODULE$.$plus("-sourcepath").$plus(""));

        if (global == null) {
            global = new Global(settings) {

                @Override
                public boolean onlyPresentation() {
                    return true;
                }

                @Override
                public void logError(String msg, Throwable t) {
                    //Exceptions.printStackTrace(t);
                }
            };

            projectDirToGlobal.put(prjDir, new WeakReference<Global>(global));
        }

        if (project != null) {
            // add project's src and out path

            FileObject srcDir = null;
            FileObject outDir = null;
            if (prjDir != null) {
                try {
                    srcDir = prjDir.getFileObject("src");
                    if (srcDir == null) {
                        srcDir = prjDir.createFolder("src");
                    }
                    FileObject buildFo = prjDir.getFileObject("build");
                    if (buildFo == null) {
                        buildFo = prjDir.createFolder("build");
                    }
                    FileObject classesFo = buildFo.getFileObject("classes");
                    if (classesFo == null) {
                        classesFo = buildFo.createFolder("classes");
                    }
                    outDir = prjDir.getFileObject("build/classes");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

                String srcPath = srcDir == null ? "" : FileUtil.toFile(srcDir).getAbsolutePath();
                if (outDir != null) {
                    String outPath = FileUtil.toFile(outDir).getAbsolutePath();
                    global.classPath().output(outPath, srcPath);
                }
            }

            // add boot, compiler classpath
            ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
            if (cpp != null) {
                ClassPath bootCp = cpp.findClassPath(fo, ClassPath.BOOT);
                ClassPath compileCp = cpp.findClassPath(fo, ClassPath.COMPILE);
                if (bootCp == null || compileCp == null) {
                    bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT);
                    compileCp = ClassPath.getClassPath(fo, ClassPath.COMPILE);
                }
                addToGlobalClassPath(global, bootCp);
                addToGlobalClassPath(global, compileCp);
            }
        }

        System.out.println(global.settings().scala$tools$nsc$Settings$$allsettings());
        return global;
    }

    private static void addToGlobalClassPath(Global global, ClassPath cp) {
        if (cp == null) {
            return;
        }

        for (ClassPath.Entry entry : cp.entries()) {
            String sources = "";
            File rootFile = null;
            try {
                FileObject entryRoot = entry.getRoot();
                if (entryRoot != null) {
                    FileSystem fs = entryRoot.getFileSystem();
                    if (fs instanceof JarFileSystem) {
                        rootFile = ((JarFileSystem) fs).getJarFile();
                    } else {
                        rootFile = FileUtil.toFile(entryRoot);
                    }
                }
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }

            if (rootFile != null) {
                String path = rootFile.getAbsolutePath();
                global.classPath().library(path, sources);
            }
        }
    }

    private static Map<String, ScalaTreeVisitor> fileNameToVisitor =
            new HashMap<String, ScalaTreeVisitor>();

    /** @Note used only for lib's source, which won't be changed anymore */
    public static ScalaTreeVisitor compileSource(final Global global, String fileName, char[] text, boolean refresh) {
        ScalaTreeVisitor visitor = fileNameToVisitor.get(fileName);
        if (visitor != null) {
            if (refresh) {
                fileNameToVisitor.remove(fileName);
            } else {
                return visitor;
            }
        }

        Global.Run run = global.new Run();

        scala.List srcFiles = Nil$.MODULE$;
        BatchSourceFile srcFile = new BatchSourceFile(fileName, text);
        srcFiles = srcFiles.$colon$colon(srcFile);

        try {
            run.compileSources(srcFiles);
        } catch (Exception ex) {
            // just ignore all ex
        }

        scala.Iterator units = run.units();
        while (units.hasNext()) {
            CompilationUnit unit = (CompilationUnit) units.next();
            if (unit.source() == srcFile) {
                Tree tree = unit.body();
                visitor = new ScalaTreeVisitor(tree);
                fileNameToVisitor.put(fileName, visitor);
                return visitor;
            }
        }

        return null;
    }

    private static void printProperties(Properties props) {
        System.out.println("===========================");
        Enumeration keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = (String) props.get(key);
            System.out.println(key + ": " + value);
        }
    }
}
