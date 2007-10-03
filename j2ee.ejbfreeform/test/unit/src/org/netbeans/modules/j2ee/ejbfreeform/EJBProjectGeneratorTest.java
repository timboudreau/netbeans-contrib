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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.java.freeform.JavaProjectGenerator;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.ant.freeform.FreeformProjectGenerator;
import org.netbeans.modules.project.ant.AntBasedProjectFactorySingleton;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Tests for EJBProjectGenerator.
 *
 * @author David Konecny, Lukas Jungmann
 */
public class EJBProjectGeneratorTest extends TestBase {
    
    private File lib1;
    private File lib2;
    private File src;
    private File test;
    private File conf;
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public EJBProjectGeneratorTest(java.lang.String testName) {
        super(testName);
    }
    
    private AntProjectHelper createEmptyProject(String projectFolder, String projectName, boolean notSoEmpty) throws Exception {
        File base = new File(getWorkDir(), projectFolder);
        base.mkdir();
        File antScript = new File(base, "build.xml");
        antScript.createNewFile();
        src = new File(base, "src");
        src.mkdir();
        conf = new File(base, "conf");
        conf.mkdir();
        new File(conf, "ejb-jar.xml").createNewFile();
        test = new File(base, "test");
        test.mkdir();
        File libs = new File(base, "libs");
        libs.mkdir();
        lib1 = new File(libs, "some.jar");
        createRealJarFile(lib1);
        lib2 = new File(libs, "some2.jar");
        createRealJarFile(lib2);
        EJBProjectGenerator.EJBModule ejbMod = new EJBProjectGenerator.EJBModule();
        ejbMod.j2eeSpecLevel = "1.4";
        ejbMod.classpath = "";
        ejbMod.configFiles = conf.getAbsolutePath();
        List mods = new ArrayList();
        mods.add(ejbMod);
        AntProjectHelper helper = FreeformProjectGenerator.createProject(base, base, projectName, null);
        EJBProjectGenerator.putEJBModules(helper, Util.getAuxiliaryConfiguration(helper), mods);
        EJBProjectGenerator.putServerID(helper, "GENERIC");
        
        putSrcRoot(helper); //workaround for issue 71363
        
        List l = new ArrayList();
        String s = conf.getAbsolutePath();
        l.add(s);
        l.add(conf.getName());
        EJBProjectGenerator.putEJBSourceFolder(helper, l);
        EJBProjectGenerator.putEJBNodeView(helper, l);
        
        if (notSoEmpty) {
            ArrayList sources = new ArrayList();
            JavaProjectGenerator.SourceFolder sf = new JavaProjectGenerator.SourceFolder();
            sf.label = "src";
            sf.type = "java";
            sf.style = "packages";
            sf.location = src.getAbsolutePath();
            sources.add(sf);
            sf = new JavaProjectGenerator.SourceFolder();
            sf.label = "test";
            sf.type = "java";
            sf.style = "packages";
            sf.location = test.getAbsolutePath();
            sources.add(sf);
            JavaProjectGenerator.putSourceFolders(helper, sources, "java");
            JavaProjectGenerator.putSourceViews(helper, sources, "packages");
            
            ArrayList compUnits = new ArrayList();
            JavaProjectGenerator.JavaCompilationUnit cu = new JavaProjectGenerator.JavaCompilationUnit();
            JavaProjectGenerator.JavaCompilationUnit.CP cp = new JavaProjectGenerator.JavaCompilationUnit.CP();
            cp.classpath = lib1.getAbsolutePath();
            cp.mode = "compile";
            cu.classpath = Collections.singletonList(cp);
            cu.sourceLevel = "1.4";
            cu.packageRoots = Collections.singletonList(src.getAbsolutePath());
            compUnits.add(cu);
            cu = new JavaProjectGenerator.JavaCompilationUnit();
            cp = new JavaProjectGenerator.JavaCompilationUnit.CP();
            cp.classpath = lib2.getAbsolutePath();
            cp.mode = "compile";
            cu.classpath = Collections.singletonList(cp);
            cu.sourceLevel = "1.4";
            cu.packageRoots = Collections.singletonList(test.getAbsolutePath());
            cu.isTests = true;
            compUnits.add(cu);
            JavaProjectGenerator.putJavaCompilationUnits(helper, Util.getAuxiliaryConfiguration(helper), compUnits);
        }
        
        return helper;
    }
    
    public void testEJBModules() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj1", "proj-1", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
//        ProjectManager.getDefault().saveProject(p);
        validate(p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        ProjectInformation pi = ProjectUtils.getInformation(p);
        assertEquals("Project name was not set", "proj-1", pi.getName());
        EjbJar e = EjbJar.getEjbJar(FileUtil.toFileObject(conf));
        assertNotNull("EjbJar not found", e);
        assertEquals("Incorrect ejb-jar.xml.", FileUtil.toFileObject(new File(conf, "ejb-jar.xml")),
                e.getDeploymentDescriptor());
        assertEquals("Incorrect J2EE spec. version.", "1.4", e.getJ2eePlatformVersion());
        Element ejb = ((AuxiliaryConfiguration) p.getLookup().lookup(AuxiliaryConfiguration.class))
        .getConfigurationFragment("ejb-data", EJBProjectNature.NS_EJB, true);
        assertNotNull(ejb);
        List/*<Element>*/ ejbModules = Util.findSubElements(ejb);
        assertEquals("One ejb-module element should be found.", 1, ejbModules.size());
        Element module = (Element) ejbModules.get(0);
        assertEquals("Classpath element is not empty.", "" , Util.findText(Util.findElement(module, "classpath", EJBProjectNature.NS_EJB)));
    }
    
    public void testSourceFolders() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj-2", "proj-2", true);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
//        ProjectManager.getDefault().saveProject(p);
        assertNotNull("Project was not created", p);
        validate(p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        ProjectInformation pi = ProjectUtils.getInformation(p);
        assertEquals("Project name was not set", "proj-2", pi.getName());
        EjbJar e = EjbJar.getEjbJar(FileUtil.toFileObject(conf));
        assertNotNull("EjbJar not found", e);
        Set srcRoots = new HashSet();
        srcRoots.add(FileUtil.toFileObject(src));
        Set testRoots = new HashSet();
        testRoots.add(FileUtil.toFileObject(test));
        //in current implementation EjbJar.getJavaSources() returns
        //all source roots (java+tests)
        FileObject[] fos = e.getJavaSources();
        Set s = new HashSet();
        for (int i = 0; i < fos.length; i++) {
            s.add(fos[i]);
        }
        assertTrue("There are missing java src roots", s.containsAll(srcRoots));
    }
    
    // create real Jar otherwise FileUtil.isArchiveFile returns false for it
    public void createRealJarFile(File f) throws Exception {
        OutputStream os = new FileOutputStream(f);
        try {
            JarOutputStream jos = new JarOutputStream(os);
            JarEntry entry = new JarEntry("foo.txt");
            jos.putNextEntry(entry);
            jos.flush();
            jos.close();
        } finally {
            os.close();
        }
    }
    
    public static void validate(Project proj) throws Exception {
        File projF = FileUtil.toFile(proj.getProjectDirectory());
        File xml = new File(new File(projF, "nbproject"), "project.xml");
        SAXParserFactory f = (SAXParserFactory)Class.forName("org.apache.xerces.jaxp.SAXParserFactoryImpl").newInstance();
        if (f == null) {
            System.err.println("Validation skipped because org.apache.xerces.jaxp.SAXParserFactoryImpl was not found on classpath");
            return;
        }
        f.setNamespaceAware(true);
        f.setValidating(true);
        SAXParser p = f.newSAXParser();
        p.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");
        p.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", getSchemas());
        try {
            p.parse(xml.toURI().toString(), new Handler());
        } catch (SAXParseException e) {
            assertTrue("Validation of XML document "+xml+" against schema failed. Details: "+
                    e.getSystemId() + ":" + e.getLineNumber() + ": " + e.getLocalizedMessage(), false);
        }
    }
    
    private static String[] getSchemas() throws Exception {
        return new String[] {
            FreeformProjectGenerator.class.getResource("resources/freeform-project-general.xsd").toExternalForm(),
            JavaProjectGenerator.class.getResource("resources/freeform-project-java.xsd").toExternalForm(),
            JavaProjectGenerator.class.getResource("resources/freeform-project-java-2.xsd").toExternalForm(),
            EJBProjectGenerator.class.getResource("resources/freeform-project-ejb.xsd").toExternalForm(),
            AntBasedProjectFactorySingleton.class.getResource("project.xsd").toExternalForm(),
        };
    }
    
    private static final class Handler extends DefaultHandler {
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    }
    
    // Issue 71363 WA
    private void putSrcRoot(AntProjectHelper helper) {
        JavaProjectGenerator.SourceFolder sf = new JavaProjectGenerator.SourceFolder();
        sf.label = "src";
        sf.type = "java";
        sf.style = "packages";
        sf.location = src.getAbsolutePath();
        ArrayList sources = new ArrayList();
        sources.add(sf);
        JavaProjectGenerator.putSourceFolders(helper, sources, null);
        JavaProjectGenerator.putSourceViews(helper, sources, null);
        ArrayList compUnits = new ArrayList();
        JavaProjectGenerator.JavaCompilationUnit cu = new JavaProjectGenerator.JavaCompilationUnit();
        JavaProjectGenerator.JavaCompilationUnit.CP cp = new JavaProjectGenerator.JavaCompilationUnit.CP();
        cp.classpath = lib1.getAbsolutePath();
        cp.mode = "compile";
        cu.classpath = Collections.singletonList(cp);
        cu.sourceLevel = "1.4";
        cu.packageRoots = Collections.singletonList(src.getAbsolutePath());
        compUnits.add(cu);
        JavaProjectGenerator.putJavaCompilationUnits(helper, Util.getAuxiliaryConfiguration(helper), compUnits);
    }
    
}
