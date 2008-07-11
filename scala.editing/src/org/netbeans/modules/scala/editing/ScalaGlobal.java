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
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;

/**
 *
 * @author dcaoyuan
 */
public class ScalaGlobal {

    private static Map<Project, Reference<Global>> projectToGlobal =
            new WeakHashMap<Project, Reference<Global>>();

    public static Global getGlobal(FileObject fo) {
        Global global = null;

        Project project = FileOwnerQuery.getOwner(fo);
        if (project != null) {
            Reference<Global> globalRef = projectToGlobal.get(project);
            if (globalRef != null) {
                global = globalRef.get();
                if (global != null) {
                    return global;
                } else {
                    projectToGlobal.remove(global);
                }
            }

        }
        
        Properties sysProps = System.getProperties();
        printProperties(sysProps);
        String scalaClzPath = (String) sysProps.get("scala.class.path");
        String scalaSrcPath = (String) sysProps.get("scala.source.path");

        final boolean onlyPresentation = false;

        final Settings settings = new Settings(null);
        settings.verbose().value_$eq(false);
        settings.classpath().tryToSet(Nil.$colon$colon("").$colon$colon("-classpath"));
        settings.bootclasspath().tryToSet(Nil.$colon$colon("").$colon$colon("-bootclasspath"));

        if (global == null) {
            global = new Global(settings) {

                @Override
                public boolean onlyPresentation() {
                    return onlyPresentation;
                }

                @Override
                public void logError(String msg, Throwable t) {
                    //Exceptions.printStackTrace(t);
                }
            };

            projectToGlobal.put(project, new WeakReference<Global>(global));
        }

        if (project != null) {
            // add project's src and out path
            FileObject prjFo = project.getProjectDirectory();
            FileObject srcFo = null;
            FileObject outFo = null;
            if (prjFo != null) {
                try {
                    srcFo = prjFo.getFileObject("src");
                    if (srcFo == null) {
                        srcFo = prjFo.createFolder("src");
                    }
                    FileObject buildFo = prjFo.getFileObject("build");
                    if (buildFo == null) {
                        buildFo = prjFo.createFolder("build");
                    }
                    FileObject classesFo = buildFo.getFileObject("classes");
                    if (classesFo == null) {
                        classesFo = buildFo.createFolder("classes");
                    }
                    outFo = prjFo.getFileObject("build/classes");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

                String srcPath = srcFo == null ? "" : FileUtil.toFile(srcFo).getAbsolutePath();
                if (outFo != null) {
                    String outFoPath = FileUtil.toFile(outFo).getAbsolutePath();
                    settings.outdir().tryToSet(Nil.$colon$colon(outFoPath).$colon$colon("-d"));
                    global.classPath().library(outFoPath, srcPath);
                }
            }

            // add boot, compiler classpath
            ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
            if (cpp != null) {
                addToGlobalClassPath(global, cpp.findClassPath(fo, ClassPath.BOOT));
                addToGlobalClassPath(global, cpp.findClassPath(fo, ClassPath.COMPILE));
            }
        }

        return global;
    }

    private static void addToGlobalClassPath(Global global, ClassPath cp) {
        String sources = "";
        for (ClassPath.Entry entry : cp.entries()) {
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
