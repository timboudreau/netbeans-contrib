/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.modules.java.freeform.JavaProjectGenerator;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Element;
import org.netbeans.modules.ant.freeform.FreeformProjectGenerator;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider;

/**
 * Tests for JavaProjectGenerator.
 *
 * @author David Konecny
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
        ProjectManager.getDefault().saveProject(p);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        ProjectInformation pi = ProjectUtils.getInformation(p);
        assertEquals("Project name was not set", "proj-1", pi.getName());
        /*
        EjbJarProvider eji = (EjbJarProvider) p.getLookup().lookup(EjbJarProvider.class);
        assertNotNull(eji.findEjbJar(p.getProjectDirectory()));
        System.out.println("eji" + eji);
         */
        EJBModules e = new EJBModules(p, helper, helper.getStandardPropertyEvaluator());
        assertNotNull("EJBModules not found", e);
        assertEquals(FileUtil.toFileObject(new File(conf, "ejb-jar.xml")), e.getEjbJars()[0].getDeploymentDescriptor());
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
}
