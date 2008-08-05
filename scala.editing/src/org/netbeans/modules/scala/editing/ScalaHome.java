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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import java.util.Properties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import scala.Nil;
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;

/**
 * 
 * @todo Set scala home via installed scala platform
 * 
 * @author Caoyuan Deng
 */
public class ScalaHome {

    public static Global getGlobalForStdLib() {
        String scalaHome = getScalaHome();
        File scalaHomeDir = null;

        try {
            scalaHomeDir = new File(scalaHome);
            scalaHomeDir = scalaHomeDir.getCanonicalFile();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }

        File scalaLib = new File(scalaHomeDir, "lib"); // NOI18N
        assert scalaLib.exists();// : '"' + scalaLib.getAbsolutePath() + "\" exists (\"" + descriptor.getCmd() + "\" is not valid Scala executable?)";

        final Settings settings = new Settings();
        settings.verbose().value_$eq(false);

        settings.sourcepath().tryToSet(Nil.$colon$colon("").$colon$colon("-sourcepath"));

        String nbUserPath = System.getProperty("netbeans.user");

        //System.out.println("nbuser:" + nbUserPath);
        settings.outdir().tryToSet(Nil.$colon$colon(nbUserPath).$colon$colon("-d"));

        // add boot, compile classpath
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("sun.boot.class.path"));
        sb.append(File.pathSeparator);
        sb.append(scalaLib.getAbsolutePath() + File.separator + "scala-library.jar");
        
        //System.out.println("boot:" + sb);
        settings.bootclasspath().tryToSet(Nil.$colon$colon(sb.toString()).$colon$colon("-bootclasspath"));

        sb.delete(0, sb.length() - 1);
        sb.append(getJavaClassPath());
        sb.append(File.pathSeparator);
        sb.append(computeScalaClassPath(null, scalaLib));

        //System.out.println("comp:" + sb);
        settings.classpath().tryToSet(Nil.$colon$colon(sb.toString()).$colon$colon("-classpath"));

        Global global = new Global(settings) {

            @Override
            public boolean onlyPresentation() {
                return true;
            }

            @Override
            public void logError(String msg, Throwable t) {
                //Exceptions.printStackTrace(t);
                }
        };


        return global;
    }

    public static String getJavaHome() {
        String javaHome = System.getProperty("scala.java.home"); // NOI18N

        if (javaHome == null) {
            javaHome = System.getProperty("java.home"); // NOI18N
        }

        return javaHome;
    }

    public static String getJavaClassPath() {
        String javacClassPath = System.getProperty("java.class.path");
        return javacClassPath == null ? "" : javacClassPath;
    }

    public static String getScalaHome() {
        String scalaHome = System.getProperty("scala.home"); // NOI18N

        if (scalaHome == null) {
            scalaHome = System.getenv("SCALA_HOME"); // NOI18N
            if (scalaHome != null) {
                System.setProperty("scala.home", scalaHome);
            }
        }
        if (scalaHome != null) {
            return scalaHome;
        } else {
            System.out.println("Can not found ${SCALA_HOME}/bin/scala, the environment variable SCALA_HOME may be invalid.\nPlease set proper SCALA_HOME first!");
            return null;
        }
    }

    public static File getScala() {
        FileObject scalaFo = null;
        String scalaHome = getScalaHome();
        if (scalaHome != null) {
            File scalaHomeDir = new File(getScalaHome());
            if (scalaHomeDir.exists() && scalaHomeDir.isDirectory()) {
                try {
                    FileObject scalaHomeFo = FileUtil.createData(scalaHomeDir);
                    FileObject bin = scalaHomeFo.getFileObject("bin");             //NOI18N
                    if (Utilities.isWindows()) {
                        scalaFo = bin.getFileObject("scala", "exe");
                        if (scalaFo == null) {
                            scalaFo = bin.getFileObject("scala", "bat");
                        }
                    } else {
                        scalaFo = bin.getFileObject("scala", null);    //NOI18N
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (scalaFo != null) {
            return FileUtil.toFile(scalaFo);
        } else {
            System.out.println("Can not found ${SCALA_HOME}/bin/scala, the environment variable SCALA_HOME may be invalid.\nPlease set proper SCALA_HOME first!");
            return null;
        }
    }

    static List<URL> getSources(File scalaHome) {
        if (scalaHome != null) {
            try {
                File scalaSrc;
                scalaSrc = new File(scalaHome, "src");    //NOI18N
                if (scalaSrc.exists() && scalaSrc.canRead()) {
                    List<URL> srcUrls = new ArrayList<URL>();
                    for (File src : scalaSrc.listFiles()) {
                        /** 
                         * @Note:
                         * GSF's indexing does not support jar, zip yet 
                         */
                        if (src.getName().endsWith(".jar") || src.getName().endsWith(".zip")) { // NOI18N
                            URL url = FileUtil.getArchiveRoot(src.toURI().toURL());
                            srcUrls.add(url);
                        } else if (src.isDirectory()) { // NOI18N
                            URL url = src.toURI().toURL();
                            srcUrls.add(url);
                        }
                    }
//                    URL url = FileUtil.getArchiveRoot(scalaSrcDir.toURI().toURL());
//
//                    //Test for src folder in the src.zip on Mac
//                    if (Utilities.getOperatingSystem() == Utilities.OS_MAC) {
//                        try {
//                            FileObject fo = URLMapper.findFileObject(url);
//                            if (fo != null) {
//                                fo = fo.getFileObject("src");    //NOI18N
//                                if (fo != null) {
//                                    url = fo.getURL();
//                                }
//                            }
//                        } catch (FileStateInvalidException fileStateInvalidException) {
//                            Exceptions.printStackTrace(fileStateInvalidException);
//                        }
//                    }
                    return srcUrls;
                }
            } catch (MalformedURLException e) {
                Exceptions.printStackTrace(e);
            }
        }
        return null;
    }

    static List<URL> getJavadoc(File scalaHome) {
        if (scalaHome != null) {
            File scalaDoc = new File(scalaHome, "doc"); //NOI18N
            if (scalaDoc.isDirectory() && scalaDoc.canRead()) {
                try {
                    return Collections.singletonList(scalaDoc.toURI().toURL());
                } catch (MalformedURLException mue) {
                    Exceptions.printStackTrace(mue);
                }
            }
        }
        return null;
    }

    static String computeScalaClassPath(String extraCp, final File scalaLib) {
        StringBuilder cp = new StringBuilder();
        File[] libs = scalaLib.listFiles();

        for (File lib : libs) {
            if (lib.getName().endsWith(".jar")) { // NOI18N

                if (cp.length() > 0) {
                    cp.append(File.pathSeparatorChar);
                }

                cp.append(lib.getAbsolutePath());
            }
        }

        // Add in user-specified jars passed via SCALA_EXTRA_CLASSPATH

        if (extraCp != null && File.pathSeparatorChar != ':') {
            // Ugly hack - getClassPath has mixed together path separator chars
            // (:) and filesystem separators, e.g. I might have C:\foo:D:\bar but
            // obviously only the path separator after "foo" should be changed to ;
            StringBuilder p = new StringBuilder();
            int pathOffset = 0;
            for (int i = 0; i < extraCp.length(); i++) {
                char c = extraCp.charAt(i);
                if (c == ':' && pathOffset != 1) {
                    p.append(File.pathSeparatorChar);
                    pathOffset = 0;
                    continue;
                } else {
                    pathOffset++;
                }
                p.append(c);
            }
            extraCp = p.toString();
        }

        if (extraCp == null) {
            extraCp = System.getenv("SCALA_EXTRA_CLASSPATH"); // NOI18N
        }

        if (extraCp != null) {
            if (cp.length() > 0) {
                cp.append(File.pathSeparatorChar);
            }
            //if (File.pathSeparatorChar != ':' && extraCp.indexOf(File.pathSeparatorChar) == -1 &&
            //        extraCp.indexOf(':') != -1) {
            //    extraCp = extraCp.replace(':', File.pathSeparatorChar);
            //}
            cp.append(extraCp);
        }
        return Utilities.isWindows() ? "\"" + cp.toString() + "\"" : cp.toString(); // NOI18N
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
