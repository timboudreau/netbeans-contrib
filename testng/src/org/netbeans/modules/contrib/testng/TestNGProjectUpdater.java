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
package org.netbeans.modules.contrib.testng;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.contrib.testng.ProjectUtilities.Type;
import org.openide.filesystems.FileObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author lukas
 */
public class TestNGProjectUpdater {

    private static final Logger LOGGER = Logger.getLogger(TestNGProjectUpdater.class.getName());

    private TestNGProjectUpdater() {
    }

    public static void updateProject(FileObject fo) throws IOException {
        assert fo != null;
        Project p = FileOwnerQuery.getOwner(fo);
        ClassPath cp = ClassPath.getClassPath(fo, ClassPath.COMPILE);
        Type type = ProjectUtilities.getProjectType(p);
        FileObject ng = cp.findResource("org.testng.annotations.Test");
        if (ng == null) {
            // add library to the project
            switch (type) {
                case ANT:
                    Library nglib = LibraryManager.getDefault().getLibrary("TestNG-5.8"); //NOI18N
                    if (!ProjectClassPathModifier.addLibraries(new Library[]{nglib}, fo, ClassPath.COMPILE)) {
                        LOGGER.fine("TestNG library not added to Ant project " + p); //NOI18N
                    }
                    break;
                case MAVEN:
                    //PCMI in meven IDE doesn't add scope and classifier elements
                    FileObject pom = p.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
                    if (!addMavenDependency(pom)) {
                        LOGGER.fine("TestNG library not added to Maven project " + p); //NOI18N
                    }
                    break;
                default:
                    LOGGER.warning("TestNG library not added to " + p); //NOI18N
            }
        }
        if (Type.ANT.equals(type)) {
            BuildScriptHandler.initBuildScript(fo);
        }
    }

    private static boolean addMavenDependency(FileObject pom) {
        boolean retVal = false;
        File jar = InstalledFileLocator.getDefault().locate(
                "modules/org-netbeans-modules-maven.jar", //NOI18N
                "org.netbeans.modules.maven", false); //NOI18N
        try {
            ClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()},
                    Thread.currentThread().getContextClassLoader());
            Class c = cl.loadClass("org.netbeans.modules.maven.api.ModelUtils"); //NOI18N
            Method m = c.getDeclaredMethod("addDependency", FileObject.class, //NOI18N
                    String.class, String.class, String.class, String.class,
                    String.class, String.class, boolean.class);
            m.invoke(null, pom, "org.testng", "testng", "5.8", null, "test", "jdk15", false); //NOI18N
            retVal = true;
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return retVal;
    }
}
