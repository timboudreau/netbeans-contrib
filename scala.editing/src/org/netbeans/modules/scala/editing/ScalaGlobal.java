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
import java.util.Iterator;
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
import scala.Nil;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaGlobal {

    private final static Map<ClassPath, Reference<Global>> srcCpToGlobal =
            new WeakHashMap<ClassPath, Reference<Global>>();

    public static void reset() {
        srcCpToGlobal.clear();
    }

    /** Scala's global is not thread safed */
    public static synchronized Global getGlobal(FileObject fo) {
        Global global = null;
        boolean forTest = false;

        Project project = FileOwnerQuery.getOwner(fo);

        final Settings settings = new Settings();
        settings.verbose().value_$eq(false);
        if (project != null) {
            ClassPath srcCp = null;

            ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
            if (cpp != null) {
                srcCp = cpp.findClassPath(fo, ClassPath.SOURCE);
                StringBuilder sb = new StringBuilder();
                computeClassPath(sb, srcCp);
            }

            /** @Todo, it seems scala's Settings only support one source path, i.e. 
             * "/scalaproject/src" only, does not support "/scalaproject/src:/scalaproject/src2"
             * since we can not gaurantee the srcCp returns only one entry, we have to use
             * following guessing method:
             */
            
            // add project's src and out path
            FileObject prjDir = project.getProjectDirectory();

            FileObject srcDir = null;
            FileObject outDir = null;
            FileObject tstDir = null;
            if (prjDir != null) {
                try {
                    srcDir = prjDir.getFileObject("src");
                    if (srcDir == null) {
                        srcDir = prjDir.createFolder("src");
                    }

                    tstDir = prjDir.getFileObject("test");
                    if (tstDir == null) {
                        tstDir = prjDir.createFolder("test");
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

                if (tstDir.equals(fo) || FileUtil.isParentOf(tstDir, fo)) {
                    forTest = true;
                }

                Reference<Global> globalRef = srcCpToGlobal.get(srcCp);
                if (globalRef != null) {
                    global = globalRef.get();
                    if (global != null) {
                        return global;
                    } else {
                        srcCpToGlobal.remove(srcCp);
                    }
                }

                if (outDir != null) {
                    String srcPath = srcDir == null ? "" : FileUtil.toFile(srcDir).getAbsolutePath();
                    String tstPath = tstDir == null ? "" : FileUtil.toFile(tstDir).getAbsolutePath();
                    String sourcePath = forTest ? srcPath + File.pathSeparator + tstPath : srcPath;

                    String outPath = FileUtil.toFile(outDir).getAbsolutePath();
                    settings.sourcepath().tryToSet(Nil.$colon$colon(sourcePath).$colon$colon("-sourcepath"));
                    settings.outdir().tryToSet(Nil.$colon$colon(outPath).$colon$colon("-d"));
                }
            }

            if (global == null) {
                // add boot, compiler classpath
                if (cpp != null) {
                    ClassPath bootCp = cpp.findClassPath(fo, ClassPath.BOOT);
                    ClassPath compCp = cpp.findClassPath(fo, ClassPath.COMPILE);
                    if (bootCp == null || compCp == null) {
                        bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT);
                        compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE);
                    }

                    StringBuilder sb = new StringBuilder();
                    computeClassPath(sb, bootCp);
                    settings.bootclasspath().tryToSet(Nil.$colon$colon(sb.toString()).$colon$colon("-bootclasspath"));

                    sb.delete(0, sb.length());
                    computeClassPath(sb, compCp);
                    settings.classpath().tryToSet(Nil.$colon$colon(sb.toString()).$colon$colon("-classpath"));
                }

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

                srcCpToGlobal.put(srcCp, new WeakReference<Global>(global));
            }
        }

        return global;
    }

    /**
     * @Note: It seems that using global instance to set settings not works sometimes,
     * So it's always better to use Settings
     */
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

    private static void computeClassPath(StringBuilder sb, ClassPath cp) {
        if (cp == null) {
            return;
        }

        for (Iterator<ClassPath.Entry> itr = cp.entries().iterator(); itr.hasNext();) {
            File rootFile = null;
            try {
                FileObject entryRoot = itr.next().getRoot();
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
                sb.append(path);
                if (itr.hasNext()) {
                    sb.append(File.pathSeparator);
                }
            }
        }
    }

    public static CompilationUnit compileSource(final Global global, BatchSourceFile srcFile) {
        synchronized (global) {
            Global.Run run = global.new Run();

            scala.List srcFiles = Nil.$colon$colon(srcFile);
            try {
                run.compileSources(srcFiles);
            } catch (AssertionError ex) {
                /**@Note: avoid scala nsc's assert error, but since global's
                 * context may have been broken, we have to reset ScalaGlobal
                 * to clean this global
                 */
                ScalaGlobal.reset();
            } catch (java.lang.Error ex) {
                // avoid scala nsc's Error error
            } catch (Throwable ex) {
                // just ignore all ex
            }

            scala.Iterator units = run.units();
            while (units.hasNext()) {
                CompilationUnit unit = (CompilationUnit) units.next();
                if (unit.source() == srcFile) {
                    return unit;
                }
            }

            return null;
        }
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
