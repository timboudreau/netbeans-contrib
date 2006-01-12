/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
    
    private AntProjectHelper createEmptyProject(String projectFolder, String projectName) throws Exception {
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
        ejbMod.configFiles = Util.relativizeLocation(base, base, FileUtil.normalizeFile(conf));
        List mods = new ArrayList();
        mods.add(ejbMod);
        AntProjectHelper helper = FreeformProjectGenerator.createProject(base, base, projectName, null);
        EJBProjectGenerator.putEJBModules(helper, Util.getAuxiliaryConfiguration(helper), mods);
        EJBProjectGenerator.putServerID(helper, "GENERIC");
        
        //srcFolders
        JavaProjectGenerator.SourceFolder sf = new JavaProjectGenerator.SourceFolder();
        sf.label = "src";
        sf.type = "java";
        sf.style = "packages";
        sf.location = src.getAbsolutePath();
        ArrayList sources = new ArrayList();
        sources.add(sf);
        JavaProjectGenerator.putSourceFolders(helper, sources, null);
        List l = new ArrayList();
        String s = Util.relativizeLocation(base, base, FileUtil.normalizeFile(src));
        l.add(s);
        l.add(src.getName());
        EJBProjectGenerator.putEJBSourceFolder(helper, l);
        
        return helper;
    }
    
    public void testEJBModules() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj1", "proj-1");
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
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
        assertEquals("Classpath element is not empty.", "" , Util.findElement(module, "classpath", EJBProjectNature.NS_EJB).getTextContent());
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

}
