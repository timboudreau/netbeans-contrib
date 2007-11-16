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

package org.netbeans.modules.groovy.groovyproject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.modules.groovy.groovyproject.ui.customizer.GroovyProjectProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Creates a J2SEProject from scratch according to some initial configuration.
 */
public class GroovyProjectGenerator {
    
    private GroovyProjectGenerator() {}
    
    /**
     * Create a new empty J2SE project.
     * @param dir the top-level directory (need not yet exist but if it does it must be empty)
     * @param name the name for the project
     * @return the helper object permitting it to be further customized
     * @throws IOException in case something went wrong
     */
    public static AntProjectHelper createProject(File dir, String name, String mainScript, String manifestFile) throws IOException {
        FileObject dirFO = createProjectDir (dir);
        // if manifestFile is null => it's TYPE_LIB
        AntProjectHelper h = createProject(dirFO, name, "src", mainScript, manifestFile, manifestFile == null); //NOI18N
        Project p = ProjectManager.getDefault().findProject(dirFO);
        ProjectManager.getDefault().saveProject(p);
        FileObject srcFolder = dirFO.createFolder("src"); // NOI18N
        if ( mainScript != null ) {
            createMainScript( mainScript, srcFolder );
        }
        return h;
    }

    public static AntProjectHelper createProject(final File dir, final String name,
                                                  final File sourceFolder, final String manifestFile) throws IOException {
        assert sourceFolder != null : "Source folder must be given";   //NOI18N
        final FileObject dirFO = createProjectDir (dir);
        // this constructor creates only java application type
        final AntProjectHelper h = createProject(dirFO, name, null, null, manifestFile, false);
        final GroovyProject p = (GroovyProject) ProjectManager.getDefault().findProject(dirFO);
        final ReferenceHelper refHelper = p.getReferenceHelper();
        try {
        ProjectManager.mutex().writeAccess( new Mutex.ExceptionAction () {
            public Object run() throws Exception {
                String srcReference = refHelper.createForeignFileReference(sourceFolder, GroovyProjectType.SOURCES_TYPE_GROOVY);
                EditableProperties props = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                props.put("src.dir",srcReference);          //NOI18N
                h.putProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                ProjectManager.getDefault().saveProject (p);
                return null;
            }
        });
        } catch (MutexException me ) {
            ErrorManager.getDefault().notify (me);
        }
        return h;
    }

    private static AntProjectHelper createProject(FileObject dirFO, String name,
                                                  String srcRoot, String mainScript, String manifestFile, boolean isLibrary) throws IOException {
        AntProjectHelper h = ProjectGenerator.createProject(dirFO, GroovyProjectType.TYPE);
        Element data = h.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element nameEl = doc.createElementNS(GroovyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
        nameEl.appendChild(doc.createTextNode(name));
        data.appendChild(nameEl);
        Element minant = doc.createElementNS(GroovyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "minimum-ant-version"); // NOI18N
        minant.appendChild(doc.createTextNode("1.6")); // NOI18N
        data.appendChild(minant);
        h.putPrimaryConfigurationData(data, true);
        EditableProperties ep = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        ep.setProperty("dist.dir", "dist"); // NOI18N
        ep.setComment("dist.dir", new String[] {"# " + NbBundle.getMessage(GroovyProjectGenerator.class, "COMMENT_dist.dir")}, false); // NOI18N
        ep.setProperty("dist.jar", "${dist.dir}/" + PropertyUtils.getUsablePropertyName(name) + ".jar"); // NOI18N
        ep.setProperty("javac.classpath", new String[0]); // NOI18N
        ep.setProperty("build.sysclasspath", "ignore"); // NOI18N
        ep.setComment("build.sysclasspath", new String[] {"# " + NbBundle.getMessage(GroovyProjectGenerator.class, "COMMENT_build.sysclasspath")}, false); // NOI18N
        ep.setProperty("run.classpath", new String[] { // NOI18N
            "${javac.classpath}:", // NOI18N
            "${build.classes.dir}", // NOI18N
        });
        ep.setProperty("debug.classpath", new String[] { // NOI18N
            "${run.classpath}", // NOI18N
        });
        ep.setProperty("application.args", ""); // NOI18N
        ep.setProperty("jar.compress", "false"); // NOI18N
        if (!isLibrary) {
            ep.setProperty("main.script", mainScript == null ? "" : mainScript); // NOI18N
        }
        ep.setProperty("javac.compilerargs", ""); // NOI18N
        ep.setComment("javac.compilerargs", new String[] {
            "# " + NbBundle.getMessage(GroovyProjectGenerator.class, "COMMENT_javac.compilerargs"), // NOI18N
        }, false);

        ep.setProperty("javac.source", "${default.javac.source}"); // NOI18N
        ep.setProperty("javac.target", "${default.javac.target}"); // NOI18N
        ep.setProperty("javac.deprecation", "false"); // NOI18N
        ep.setProperty("src.dir", srcRoot == null ? "" : srcRoot); // NOI18N
        ep.setProperty("build.dir", "build"); // NOI18N
        ep.setComment("build.dir", new String[] {"# " + NbBundle.getMessage(GroovyProjectGenerator.class, "COMMENT_build.dir")}, false); // NOI18N
        ep.setProperty("build.classes.dir", "${build.dir}/classes"); // NOI18N
        ep.setProperty("build.classes.excludes", "**/*.java,**/*.form,**/*.groovy"); // NOI18N
        ep.setProperty("platform.active", "default_platform"); // NOI18N

        ep.setProperty("run.jvmargs", ""); // NOI18N
        ep.setComment("run.jvmargs", new String[] {
            "# " + NbBundle.getMessage(GroovyProjectGenerator.class, "COMMENT_run.jvmargs"), // NOI18N
            "# " + NbBundle.getMessage(GroovyProjectGenerator.class, "COMMENT_run.jvmargs_2"), // NOI18N
        }, false);

        if (manifestFile != null) {
            ep.setProperty("manifest.file", manifestFile); // NOI18N
        }

        h.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        ep = h.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
        ep.setProperty("application.args", ""); // NOI18N
        ep.setProperty(GroovyProjectProperties.JAVAC_DEBUG, "true");  // NOI18N
        h.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, ep);
        return h;
    }

    private static FileObject createProjectDir (File dir) throws IOException {
        FileObject dirFO;
        if(!dir.exists()) {
            //Refresh before mkdir not to depend on window focus
            refreshFileSystem (dir);
            if (!dir.mkdirs()) {
                throw new IOException ("Can not create project folder.");   //NOI18N
            }
            refreshFileSystem (dir);
        }        
        dirFO = FileUtil.toFileObject(dir);
        assert dirFO != null : "No such dir on disk: " + dir; // NOI18N
        assert dirFO.isFolder() : "Not really a dir: " + dir; // NOI18N
        return dirFO;
    }

    private static void createMainScript( String mainScriptName, FileObject srcFolder ) throws IOException {
        
        int lastDotIdx = mainScriptName.lastIndexOf( '.' );
        String mName, pName;
        if ( lastDotIdx == -1 ) {
            mName = mainScriptName.trim();
            pName = null;
        }
        else {
            mName = mainScriptName.substring( lastDotIdx + 1 ).trim();
            pName = mainScriptName.substring( 0, lastDotIdx ).trim();
        }
        
        if ( mName.length() == 0 ) {
            return;
        }
        
        FileObject mainTemplate = Repository.getDefault().getDefaultFileSystem().findResource( "Templates/Groovy/GroovyScript.groovy" ); // NOI18N

        if ( mainTemplate == null ) {
            return; // Don't know the template
        }
                
        DataObject mt = DataObject.find( mainTemplate );
        
        FileObject pkgFolder = srcFolder;
        if ( pName != null ) {
            String fName = pName.replace( '.', '/' ); // NOI18N
            pkgFolder = FileUtil.createFolder( srcFolder, fName );        
        }
        DataFolder pDf = DataFolder.findFolder( pkgFolder );        
        mt.createFromTemplate( pDf, mName );
        
    }


    private static void refreshFileSystem (final File dir) throws FileStateInvalidException {
        File rootF = dir;
        while (rootF.getParentFile() != null) {
            rootF = rootF.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(rootF);
        assert dirFO != null : "At least disk roots must be mounted! " + rootF; // NOI18N
        dirFO.getFileSystem().refresh(false);
    }
}


