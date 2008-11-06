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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.BinaryForSourceQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.queries.BinaryForSourceQueryImplementation;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.util.Exceptions;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaGlobal {

    private final static Map<Project, Reference<Global>> ProjectToGlobal =
            new WeakHashMap<Project, Reference<Global>>();
    private final static Map<Project, Reference<Global>> ProjectToGlobalForTest =
            new WeakHashMap<Project, Reference<Global>>();
    private final static Map<Project, Reference<SrcOutDirs>> ProjectToDirs =
            new WeakHashMap<Project, Reference<SrcOutDirs>>();
    private static Global GlobalForStdLid;

    private static class SrcOutDirs {

        FileObject srcDir;
        FileObject outDir;
        FileObject testSrcDir;
        FileObject testOutDir;
    }

    public static void reset() {
        ProjectToGlobal.clear();
        GlobalForStdLid = null;
    }

    /**
     * Scala's global is not thread safed
     *
     * @Todo: it seems scala's Settings only support one source path, i.e.
     * "/scalaproject/src" only, does not support "/scalaproject/src:/scalaproject/src2"
     * since we can not gaurantee the srcCp returns only one entry, we have to use
     * following guessing method:
     */
    public static synchronized Global getGlobal(FileObject fo) {
        Global global = null;

        final Project project = FileOwnerQuery.getOwner(fo);
        if (project == null) {
            // it may be a standalone file, or file in standard lib
            if (GlobalForStdLid == null) {
                GlobalForStdLid = ScalaHome.getGlobalForStdLib();
            }

            return GlobalForStdLid;
        }

        if (project != null) {
            SrcOutDirs dirs = null;
            Reference<SrcOutDirs> dirsRef = ProjectToDirs.get(project);
            if (dirsRef != null) {
                dirs = dirsRef.get();
            }

            if (dirs == null) {
                dirs = findDirsInfo(project);
                ProjectToDirs.put(project, new WeakReference<SrcOutDirs>(dirs));
            }

            // is fo under test source?
            boolean forTest = false;
            if (dirs.testSrcDir != null &&
                    (dirs.testSrcDir.equals(fo) || FileUtil.isParentOf(dirs.testSrcDir, fo))) {

                forTest = true;
            }

            // Do not use srcCp as the key, different fo under same src dir seems returning diff instance of srcCp
            Reference<Global> globalRef = forTest ? ProjectToGlobalForTest.get(project) : ProjectToGlobal.get(project);
            if (globalRef != null) {
                global = globalRef.get();
                if (global != null) {
                    return global;
                }
            }

            String srcPath = "";
            String outPath = "";
            if (forTest) {
                srcPath = dirs.testSrcDir == null ? "" : FileUtil.toFile(dirs.testSrcDir).getAbsolutePath();
                outPath = dirs.testOutDir == null ? "" : FileUtil.toFile(dirs.testOutDir).getAbsolutePath();
            } else {
                srcPath = dirs.srcDir == null ? "" : FileUtil.toFile(dirs.srcDir).getAbsolutePath();
                outPath = dirs.outDir == null ? "" : FileUtil.toFile(dirs.outDir).getAbsolutePath();
            }

            final Settings settings = new Settings();
            settings.verbose().value_$eq(false);
            settings.sourcepath().tryToSet(scala.netbeans.Wrapper$.MODULE$.scalaStringList("-sourcepath", srcPath));
            settings.outdir().tryToSet(scala.netbeans.Wrapper$.MODULE$.scalaStringList("-d", outPath));

            // add boot, compile classpath
            ClassPath bootCp = null;
            ClassPath compCp = null;
            ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
            if (cpp != null) {
                bootCp = cpp.findClassPath(fo, ClassPath.BOOT);
                compCp = cpp.findClassPath(fo, ClassPath.COMPILE);
            }


            boolean inStdLib = false;
            if (bootCp == null || compCp == null) {
                // in case of fo in standard libaray
                inStdLib = true;
                bootCp = ClassPath.getClassPath(fo, ClassPath.BOOT);
                compCp = ClassPath.getClassPath(fo, ClassPath.COMPILE);
            }

            StringBuilder sb = new StringBuilder();
            computeClassPath(sb, bootCp);
            settings.bootclasspath().tryToSet(scala.netbeans.Wrapper$.MODULE$.scalaStringList("-bootclasspath", sb.toString()));

            sb.delete(0, sb.length());
            computeClassPath(sb, compCp);
            if (forTest && !inStdLib && dirs.outDir != null) {
                sb.append(File.pathSeparator).append(dirs.outDir);
            }
            settings.classpath().tryToSet(scala.netbeans.Wrapper$.MODULE$.scalaStringList("-classpath", sb.toString()));

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

            if (forTest) {
                ProjectToGlobalForTest.put(project, new WeakReference<Global>(global));
                if (dirs.testOutDir != null) {
                    dirs.testOutDir.addFileChangeListener(new FileChangeAdapter() {

                        @Override
                        public void fileDeleted(FileEvent fe) {
                            // maybe a clean task invoked
                            ProjectToGlobalForTest.remove(project);
                            ProjectToDirs.remove(project);
                        }
                    });
                }

                if (dirs.outDir != null) {
                    // monitor outDir's changes,
                    /** @Todo should reset global for any changes under out dir, including subdirs */
                    dirs.outDir.addFileChangeListener(new FileChangeAdapter() {

                        @Override
                        public void fileDeleted(FileEvent fe) {
                            ProjectToGlobalForTest.remove(project);
                            ProjectToDirs.remove(project);
                        }
                    });
                }
            } else {
                ProjectToGlobal.put(project, new WeakReference<Global>(global));
                if (dirs.outDir != null) {
                    dirs.outDir.addFileChangeListener(new FileChangeAdapter() {

                        @Override
                        public void fileDeleted(FileEvent fe) {
                            // maybe a clean task invoked
                            ProjectToGlobal.remove(project);
                            ProjectToDirs.remove(project);
                        }
                    });
                }
            }
        }

        return global;
    }

    private static SrcOutDirs findDirsInfo(Project project) {
        SrcOutDirs dirs = new SrcOutDirs();

        SourceGroup[] sgs = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (sgs.length > 0) {
            dirs.srcDir = sgs[0].getRootFolder();
            dirs.outDir = findOutDir(project, dirs.srcDir);
            if (sgs.length > 1) {
                dirs.testSrcDir = sgs[1].getRootFolder();
                dirs.testOutDir = findOutDir(project, dirs.testSrcDir);
            }
        }

        return dirs;
    }

    private static FileObject findOutDir(Project project, FileObject srcRoot) {
        FileObject out = null;
        URL srcRootUrl = null;
        try {
            // make sure the url is in same form of BinaryForSourceQueryImplementation
            srcRootUrl = FileUtil.toFile(srcRoot).toURI().toURL();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }

        BinaryForSourceQueryImplementation query = project.getLookup().lookup(BinaryForSourceQueryImplementation.class);
        if (query != null && srcRootUrl != null) {
            BinaryForSourceQuery.Result result = query.findBinaryRoots(srcRootUrl);
            if (result != null) {
                for (URL url : result.getRoots()) {
                    if (FileUtil.isArchiveFile(url)) {
                        continue;
                    }

                    URI uri = null;
                    try {
                        uri = url.toURI();
                    } catch (URISyntaxException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    if (uri == null) {
                        continue;
                    }

                    File file = new File(uri);
                    if (file != null) {
                        if (file.isDirectory()) {
                            out = FileUtil.toFileObject(file);
                            break;
                        } else if (file.exists()) {
                            continue;
                        } else {
                            // global requires an exist out path, so we should create
                            if (file.mkdirs()) {
                                out = FileUtil.toFileObject(file);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // global requires an exist out path, so we have to create a tmp folder
        if (out == null) {
            FileObject projectDir = project.getProjectDirectory();
            if (projectDir != null && projectDir.isFolder()) {
                try {
                    String tmpClasses = "tmpClasses";
                    out = projectDir.getFileObject(tmpClasses);
                    if (out == null) {
                        out = projectDir.createFolder(tmpClasses);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        return out;
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

            scala.List srcFiles = scala.netbeans.Wrapper$.MODULE$.scalaSrcFileList(srcFile);
            try {
                run.compileSources(srcFiles);
            } catch (AssertionError ex) {
                /**@Note: avoid scala nsc's assert error. Since global's
                 * symbol table may have been broken, we have to reset ScalaGlobal
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

//    public static scala.List<BatchSourceFile> buildScalaList(BatchSourceFile f) {
////        scala.Array<BatchSourceFile> a = new scala.Array<BatchSourceFile>(1);
////        BatchSourceFile[] b = scala.Array$.MODULE$.apply(a);
////        b[0] = f;
////        return scala.List$.MODULE$.apply(a);
//    }
//
//    public static scala.List<String> buildScalaList(String e1, String e2) {
//        scala.Array<String> a = new scala.Array<String>(2);
//        String[] b = scala.Array$.MODULE$.apply(a);
//        b[0] = e1;
//        b[1] = e2;
//        return scala.List$.MODULE$.apply(a);
//    }
}
