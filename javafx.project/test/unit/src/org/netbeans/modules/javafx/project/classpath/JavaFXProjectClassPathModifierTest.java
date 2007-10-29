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

package org.netbeans.modules.javafx.project.classpath;

import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.javafx.project.JavaFXProjectGenerator;
import org.netbeans.modules.javafx.project.TestUtil;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryProvider;
import org.netbeans.spi.project.libraries.LibraryProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;

/**
 *
 * @author answer
 */
public class JavaFXProjectClassPathModifierTest extends NbTestCase {
    
    private FileObject scratch;
    private AntProjectHelper helper;
    private PropertyEvaluator eval;
    private FileObject src;
    private Project prj;
    
    public JavaFXProjectClassPathModifierTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        TestUtil.setLookup(new Object[] {
            new org.netbeans.modules.javafx.project.JavaFXProjectType(),
            new org.netbeans.modules.projectapi.SimpleFileOwnerQueryImplementation(),
            new TestLibraryProvider (),
        });
        this.scratch = TestUtil.makeScratchDir(this);
        FileObject projdir = scratch.createFolder("proj");  //NOI18N
        JavaFXProjectGenerator.setDefaultSourceLevel(new SpecificationVersion ("1.4"));   //NOI18N    
        this.helper = JavaFXProjectGenerator.createProject(FileUtil.toFile(projdir),"proj",null,null); //NOI18N
        this.eval = this.helper.getStandardPropertyEvaluator();
        JavaFXProjectGenerator.setDefaultSourceLevel(null);
        this.prj = FileOwnerQuery.getOwner(projdir);
        assertNotNull (this.prj);
        this.src = projdir.getFileObject("src");
        assertNotNull (this.src);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testAddRemoveRoot () throws Exception {        
        final FileObject rootFolder = this.scratch.createFolder("Root");
        final FileObject jarFile = this.scratch.createData("archive","jar");
        FileLock lck = jarFile.lock();
        try {
            ZipOutputStream jf = new ZipOutputStream (jarFile.getOutputStream(lck));            
            try {
                jf.putNextEntry(new ZipEntry("Test.properties"));
            }finally {
                jf.close();
            }
        } finally {
            lck.releaseLock();
        }
        final FileObject jarRoot = FileUtil.getArchiveRoot(jarFile);
        ProjectClassPathModifier.addRoots(new URL[] {rootFolder.getURL()}, this.src, ClassPath.COMPILE);
        String cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        String[] cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
        assertEquals(1,cpRoots.length);
        assertEquals(rootFolder,this.helper.resolveFileObject(cpRoots[0]));
        ProjectClassPathModifier.removeRoots (new URL[] {rootFolder.getURL()},this.src, ClassPath.COMPILE);
        cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
        assertEquals(0,cpRoots.length);
    }
    
    public void testAddRemoveArtifact () throws Exception {
        FileObject projdir = scratch.createFolder("libPrj");  //NOI18N
        JavaFXProjectGenerator.setDefaultSourceLevel(new SpecificationVersion ("1.4"));   //NOI18N    
        AntProjectHelper helper = JavaFXProjectGenerator.createProject(FileUtil.toFile(projdir),"libProj",null,null); //NOI18N        
        JavaFXProjectGenerator.setDefaultSourceLevel(null);
        Project libPrj = FileOwnerQuery.getOwner(projdir);
        assertNotNull (this.prj);
        AntArtifactProvider ap = (AntArtifactProvider) libPrj.getLookup().lookup(AntArtifactProvider.class);
        AntArtifact[] aas = ap.getBuildArtifacts();
        AntArtifact output = null;
        for (int i=0; i<aas.length; i++) {
            if (JavaProjectConstants.ARTIFACT_TYPE_JAR.equals(aas[i].getType())) { 
                output = aas[i];
                break;
            }
        }
        assertNotNull (output);
        ProjectClassPathModifier.addAntArtifacts(new AntArtifact[] {output}, new URI[] {output.getArtifactLocations()[0]}, this.src, ClassPath.COMPILE);
        String cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        String[] cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
        assertEquals(1,cpRoots.length);
        URI projectURI = URI.create(output.getProject().getProjectDirectory().getURL().toExternalForm());
        URI expected = projectURI.resolve(output.getArtifactLocations()[0]);
        assertEquals(expected,this.helper.resolveFile(cpRoots[0]).toURI());
        ProjectClassPathModifier.removeAntArtifacts(new AntArtifact[] {output}, new URI[] {output.getArtifactLocations()[0]},this.src, ClassPath.COMPILE);
        cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
        assertEquals(0,cpRoots.length);
    }
    
    public void testAddRemoveLibrary () throws Exception {
        LibraryProvider lp = (LibraryProvider) Lookup.getDefault().lookup(LibraryProvider.class);
        assertNotNull (lp);
        LibraryImplementation[] impls = lp.getLibraries();
        assertNotNull (impls);
        assertEquals(1,impls.length);
        FileObject libRoot = this.scratch.createFolder("libRoot");
        impls[0].setContent("classpath",Collections.singletonList(libRoot.getURL()));
        Library[] libs =LibraryManager.getDefault().getLibraries();
        assertNotNull (libs);
        assertEquals(1,libs.length);                       
        ProjectClassPathModifier.addLibraries(libs, this.src, ClassPath.COMPILE);
        String cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        String[] cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
        assertEquals(1,cpRoots.length);
        assertEquals("${libs.Test.classpath}",cpRoots[0]);    //There is no build.properties filled, the libraries are not resolved
        ProjectClassPathModifier.removeLibraries(libs,this.src, ClassPath.COMPILE);
        cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
        assertEquals(0,cpRoots.length);
    }
    
    public void testClassPathExtenderCompatibility () throws Exception {
        final FileObject rootFolder = this.scratch.createFolder("Root");
        final FileObject jarFile = this.scratch.createData("archive","jar");
        FileLock lck = jarFile.lock();
        try {
            ZipOutputStream jf = new ZipOutputStream (jarFile.getOutputStream(lck));            
            try {
                jf.putNextEntry(new ZipEntry("Test.properties"));
            }finally {
                jf.close();
            }
        } finally {
            lck.releaseLock();
        }
        final FileObject jarRoot = FileUtil.getArchiveRoot(jarFile);
        JavaFXProjectClassPathExtender extender = (JavaFXProjectClassPathExtender) this.prj.getLookup().lookup(JavaFXProjectClassPathExtender.class);
        assertNotNull (extender);
        extender.addArchiveFile(rootFolder);
//        extender.addArchiveFile(jarFile);
        String cp = this.eval.getProperty("javac.classpath");
        assertNotNull (cp);
        String[] cpRoots = PropertyUtils.tokenizePath (cp);
        assertNotNull (cpRoots);
//        assertEquals(2,cpRoots.length);
        assertEquals(1,cpRoots.length);
        assertEquals(rootFolder,this.helper.resolveFileObject(cpRoots[0]));
//        assertEquals(jarFile,this.helper.resolveFileObject(cpRoots[1]));
    }
    
    
    private static class TestLibraryProvider implements LibraryProvider {
        
        private LibraryImplementation[] libs;
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public LibraryImplementation[] getLibraries() {
            if (libs == null) {
                this.libs = new LibraryImplementation[] { new TestLibrary ("Test")};
            }
            return this.libs;
        }
        
    }    
    
    private static class TestLibrary implements LibraryImplementation {
        
        private String name;
        private List cp = Collections.EMPTY_LIST;
        private List src = Collections.EMPTY_LIST;
        private List jdoc = Collections.EMPTY_LIST;
        
        public TestLibrary (String name) {
            this.name = name;
        }
        
        public void setName(String name) {
        }

        public void setLocalizingBundle(String resourceName) {
        }

        public void setDescription(String text) {
        }

        public List getContent(String volumeType) throws IllegalArgumentException {
            if ("classpath".equals(volumeType)) {
                return this.cp; 
            }
            else if ("src".equals(volumeType)) {
                return this.src;
            }
            else if ("jdoc".equals(volumeType)) {
                return this.jdoc;
            }
            throw new IllegalArgumentException ();
        }

        public void removePropertyChangeListener(PropertyChangeListener l) {
        }

        public void addPropertyChangeListener(PropertyChangeListener l) {
        }

        public void setContent(String volumeType, List path) throws IllegalArgumentException {
            if ("classpath".equals(volumeType)) {
                this.cp = path;
            }
            else if ("src".equals(volumeType)) {
                this.src = path;
            }
            else if ("jdoc".equals(volumeType)) {
                this.jdoc = path;
            }
            else {
                throw new IllegalArgumentException ();
            }
        }

        public String getType() {
            return "javafx";
        }

        public String getName() {
            return this.name;
        }

        public String getLocalizingBundle() {
            return null;
        }

        public String getDescription() {
            return null;
        }
        
    }
    
    
}
