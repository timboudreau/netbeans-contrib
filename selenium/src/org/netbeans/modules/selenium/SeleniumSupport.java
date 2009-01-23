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
package org.netbeans.modules.selenium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Jindrich Sedek
 */
public class SeleniumSupport {

    private static final String SELENIUM_FOLDER_NAME = "selenium";          //NOI18N
    private static final String SELENIUM_LIBRARY_NAME = "Selenium";         //NOI18N
    private static final String SELENIUM_DIR_PROPERTY = "test.selenium.dir";//NOI18N

    private SeleniumSupport() {
    }

    public static boolean hasSeleniumDir(Project project){
        final FileObject projectDir = project.getProjectDirectory();
        for (FileObject fileObject : projectDir.getChildren()) {
            if (SELENIUM_FOLDER_NAME.equals(fileObject.getName())) {
                return true;
            }
        }
        return false;
    }

    public static FileObject getSelenimDir(Project project) {
        final FileObject projectDir = project.getProjectDirectory();
        for (FileObject fileObject : projectDir.getChildren()) {
            if (SELENIUM_FOLDER_NAME.equals(fileObject.getName())) {
                // the selenium source root already exists
                return fileObject;
            }
        }
        FileObject result = null;
        try {
            result = prepareProject(project);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    private static FileObject prepareProject(Project project) throws IOException {
        FileObject projectDir = project.getProjectDirectory();
        FileObject seleniumDir = addTestSourceRoot(project);
        addLibrary(seleniumDir);
        FileObject srcs = projectDir.getFileObject("src/java"); //NOI18N
        if (srcs == null) {
            srcs = projectDir.getFileObject("src");         //NOI18N
        }
        notifyProjectXMLChanges(srcs);
        return seleniumDir;
    }

    private static void notifyProjectXMLChanges(FileObject fo) throws IOException {
        assert fo != null;
        Library library = LibraryManager.getDefault().getLibrary(SELENIUM_LIBRARY_NAME); 
        ProjectClassPathModifier.addLibraries(new Library[]{library}, fo, ClassPath.COMPILE);
        ProjectClassPathModifier.removeLibraries(new Library[]{library}, fo, ClassPath.COMPILE);
    }

    private static void addLibrary(FileObject fo) throws IOException {
        assert fo != null;
        Project p = FileOwnerQuery.getOwner(fo);
        Library library = LibraryManager.getDefault().getLibrary(SELENIUM_LIBRARY_NAME); //NOI18N
        if (!ProjectClassPathModifier.addLibraries(new Library[]{library}, fo, ClassPath.COMPILE)) {
            Logger.getLogger(SeleniumSupport.class.getName()).fine("Selenium library was not added to project " + p); //NOI18N
        } else {
            Logger.getLogger(SeleniumSupport.class.getName()).fine("Selenium library was added to project " + p); //NOI18N
        }
    }

    private static FileObject addTestSourceRoot(Project project) throws IOException {
        final FileObject projectDir = project.getProjectDirectory();
        final FileObject seleniumDir = FileUtil.createFolder(projectDir, SELENIUM_FOLDER_NAME);
        ProjectManager.mutex().postWriteRequest(new Runnable() {

            public void run() {
                try {
                    FileObject projectXML = FileUtil.createData(projectDir, AntProjectHelper.PROJECT_XML_PATH);
                    InputStream projectXMLIs = projectXML.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(projectXMLIs));
                    StringWriter writer = new StringWriter();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        if (line.contains("<test-roots>")) {        //NOI18N
                            writer.write("<root id=\"test.selenium.dir\" name=\"Selenium Test Packages\"/>");   //NOI18N
                        }
                    }
                    projectXMLIs.close();

                    OutputStream projectXMLOs = projectXML.getOutputStream();
                    projectXMLOs.write(writer.getBuffer().toString().getBytes());
                    projectXMLOs.close();

                    FileObject projectProperties = FileUtil.createData(projectDir, AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    Properties props = getProjectProperties(projectDir);

                    props.put(SELENIUM_DIR_PROPERTY, seleniumDir.getName());
                    OutputStream propertiesOS = projectProperties.getOutputStream();
                    props.store(propertiesOS, null);
                    propertiesOS.close();

                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        return seleniumDir;
    }

    public static Properties getProjectProperties(FileObject projectDir) throws IOException {
        FileObject projectProperties = FileUtil.createData(projectDir, AntProjectHelper.PROJECT_PROPERTIES_PATH);
        InputStream propertiesIS = projectProperties.getInputStream();
        Properties props = new Properties();
        props.load(propertiesIS);
        propertiesIS.close();
        return props;
    }
}


