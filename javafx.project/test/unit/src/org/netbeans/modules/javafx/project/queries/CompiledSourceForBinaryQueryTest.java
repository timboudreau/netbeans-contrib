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

package org.netbeans.modules.javafx.project.queries;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.javafx.project.JavaFXProjectGenerator;
import org.netbeans.modules.javafx.project.JavaFXProjectType;
import org.netbeans.modules.javafx.project.MockLookup;
import org.netbeans.modules.javafx.project.TestUtil;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author answer
 */
public class CompiledSourceForBinaryQueryTest extends NbTestCase {
    
    private static final String PROP_BUILD_DIR = "build.dir";   //NOI18N
    
    private FileObject scratch;
    private FileObject projdir;
    private FileObject sources;
    private FileObject buildClasses;
    private ProjectManager pm;
    private Project pp;
    AntProjectHelper helper;
    
    public CompiledSourceForBinaryQueryTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        TestUtil.setLookup(new Object[] {
            new org.netbeans.modules.javafx.project.JavaFXProjectType(),
            new org.netbeans.modules.java.project.ProjectSourceForBinaryQuery(),
            new org.netbeans.modules.projectapi.SimpleFileOwnerQueryImplementation(),
        });
        Properties p = System.getProperties();
    }

    @Override
    protected void tearDown() throws Exception {
        scratch = null;
        projdir = null;
        pm = null;
        TestUtil.setLookup(Lookup.EMPTY);
        super.tearDown();
    }

    private void prepareProject () throws IOException {
        File scratchF = new File(getWorkDir(), "scratchDir");
        scratchF.mkdir();
        scratch = FileUtil.toFileObject(scratchF);
        projdir = scratch.createFolder("proj");        
        JavaFXProjectGenerator.setDefaultSourceLevel(new SpecificationVersion ("1.4"));   //NOI18N
        helper = JavaFXProjectGenerator.createProject(FileUtil.toFile(projdir),"proj",null,null);
        JavaFXProjectGenerator.setDefaultSourceLevel(null);   //NOI18N
        pm = ProjectManager.getDefault();
        pp = pm.findProject(projdir);
        sources = projdir.getFileObject("src");
        FileObject fo = projdir.createFolder("build");
        buildClasses = fo.createFolder("classes");        
    }
    
    public void testSourceForBinaryQuery() throws Exception {
        this.prepareProject();
        FileObject folder = scratch.createFolder("SomeFolder");
        SourceForBinaryQuery.Result result = SourceForBinaryQuery.findSourceRoots(folder.getURL());
        assertEquals("Non-project folder does not have any source folder", 0, result.getRoots().length);
        folder = projdir.createFolder("SomeFolderInProject");
        result = SourceForBinaryQuery.findSourceRoots(folder.getURL());
        assertEquals("Project non build folder does not have any source folder", 0, result.getRoots().length);
        result = SourceForBinaryQuery.findSourceRoots(buildClasses.getURL());
        assertEquals("Project build folder must have source folder", 1, result.getRoots().length);
        assertEquals("Project build folder must have source folder",sources,result.getRoots()[0]);
    }               
    
    
    public void testSourceForBinaryQueryListening () throws Exception {
        this.prepareProject();
        SourceForBinaryQuery.Result result = SourceForBinaryQuery.findSourceRoots(buildClasses.getURL());
        assertEquals("Project build folder must have source folder", 1, result.getRoots().length);
        assertEquals("Project build folder must have source folder",sources,result.getRoots()[0]);
        TestListener tl = new TestListener ();
        result.addChangeListener(tl);
        EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        FileObject sources2 = projdir.createFolder("src2");
        props.put ("src.dir","src2");        
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
        assertTrue (tl.wasEvent());
        assertEquals("Project build folder must have source folder", 1, result.getRoots().length);
        assertEquals("Project build folder must have source folder",sources2,result.getRoots()[0]);
    }

    public void testSourceForBinaryQueryMultipleSourceRoots () throws Exception {
        this.prepareProject();
        SourceForBinaryQuery.Result result = SourceForBinaryQuery.findSourceRoots(buildClasses.getURL());
        assertEquals("Project build folder must have source folder", 1, result.getRoots().length);
        assertEquals("Project build folder must have source folder",sources,result.getRoots()[0]);
        TestListener tl = new TestListener ();
        result.addChangeListener(tl);
        FileObject newRoot = addSourceRoot(helper,projdir,"src.other.dir","other");
        assertTrue (tl.wasEvent());
        assertEquals("Project build folder must have 2 source folders", 2, result.getRoots().length);
        assertEquals("Project build folder must have the first source folder",sources,result.getRoots()[0]);
        assertEquals("Project build folder must have the second source folder",newRoot,result.getRoots()[1]);
    }

    private static class TestListener implements ChangeListener {
        
        private boolean gotEvent;
        
        public void stateChanged(ChangeEvent changeEvent) {
            this.gotEvent = true;
        }      
        
        public void reset () {
            this.gotEvent = false;
        }
        
        public boolean wasEvent () {
            return this.gotEvent;
        }
    }
    
    private static FileObject addSourceRoot (AntProjectHelper helper, FileObject projdir,
                                            String propName, String folderName) throws Exception {
        Element data = helper.getPrimaryConfigurationData(true);
        NodeList nl = data.getElementsByTagNameNS (JavaFXProjectType.PROJECT_CONFIGURATION_NAMESPACE,"source-roots");
        assert nl.getLength() == 1;
        Element roots = (Element) nl.item(0);
        Document doc = roots.getOwnerDocument();
        Element root = doc.createElementNS(JavaFXProjectType.PROJECT_CONFIGURATION_NAMESPACE,"root");
        root.setAttribute("id", propName);
        roots.appendChild (root);
        helper.putPrimaryConfigurationData (data,true);
        EditableProperties props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
        props.put (propName,folderName);
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH,props);
        FileObject fo = projdir.getFileObject(folderName);
        if (fo==null) {
            fo = projdir.createFolder(folderName);
        }
        return fo;
    }
    
    public static void setLookup(Lookup l) {
        MockLookup.setLookup(l);
    }
        
}
