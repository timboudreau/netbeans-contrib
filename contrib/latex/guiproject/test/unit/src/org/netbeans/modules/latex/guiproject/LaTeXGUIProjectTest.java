/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.Collections;

import java.util.List;

import javax.swing.Action;

import javax.swing.Icon;
import junit.framework.*;



import junit.framework.*;

import org.netbeans.api.project.Project;

import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;


import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;

import org.netbeans.modules.latex.guiproject.ui.PropertiesDialogPanel;

import org.netbeans.modules.latex.model.Utilities;

import org.netbeans.modules.latex.model.command.DocumentNode;

import org.netbeans.modules.latex.model.command.LaTeXSource;

import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;

import org.netbeans.modules.latex.model.structural.Model;

import org.netbeans.modules.latex.model.structural.StructuralElement;

import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;

import org.netbeans.spi.project.ActionProvider;

import org.netbeans.spi.project.ui.CustomizerProvider;

import org.netbeans.spi.project.ui.LogicalViewProvider;

import org.netbeans.spi.project.ui.support.LogicalViews;

import org.openide.DialogDescriptor;

import org.openide.DialogDisplayer;

import org.openide.ErrorManager;

import org.openide.filesystems.FileObject;

import org.openide.filesystems.FileUtil;

import org.openide.loaders.DataObject;

import org.openide.loaders.DataObjectExistsException;

import org.openide.loaders.DataObjectNotFoundException;

import org.openide.nodes.AbstractNode;

import org.openide.nodes.Children;

import org.openide.nodes.FilterNode;

import org.openide.nodes.Node;

import org.openide.util.Lookup;

import org.openide.util.lookup.Lookups;

/**
 *
 * @author lahvac
 */
public class LaTeXGUIProjectTest extends TestCase {
    
    public LaTeXGUIProjectTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(LaTeXGUIProjectTest.class);
        return suite;
    }

    private FileObject main1;
    private FileObject main2;
    private FileObject prj1;
    private FileObject prj2;
    
    protected void setUp() throws IOException, SAXException {
        TestUtil.setLookup(new Object[] {
            new Repository(new XMLFileSystem(this.getClass().getResource("/org/netbeans/modules/latex/guiproject/resources/mf-layer.xml"))),
            new LaTeXGUIProjectFactory(),
            new LaTeXGUIProjectFactorySourceFactory(),
        }, this.getClass().getClassLoader());
        System.setErr(new PrintStream(new FileOutputStream("/tmp/unit-test-log.txt")));
        getRef().println("asdfasdf");
	getLog().println("setUp");
        getLog().flush();
//        clearWorkDir();
        
        FileObject testDir = TestUtil.makeScratchDir(this);
        File workdir = FileUtil.toFile(testDir);
        File project1 = new File(workdir, "1");
        File project2 = new File(workdir, "2");
        
        FileObject prj1Impl = CreateNewLaTeXProject.getDefault().createProject(new File(project1, "tex-project-1"), new File(project1, "main1.tex"));
        
        prj1 = prj1Impl.getParent();
        
        ProjectManager.getDefault().findProject(prj1Impl);
        
        FileObject prj2Impl = CreateNewLaTeXProject.getDefault().createProject(new File(project2, "tex-project-2"), new File(project2, "main2.tex"));
        
        prj2 = prj2Impl.getParent();
        
        ProjectManager.getDefault().findProject(prj2Impl);
        
        copyFile("data/main1.tex", main1 = prj1.getFileObject("main1", "tex"));
        copyFile("data/included1a.tex", prj1, "included1a", "tex");
        copyFile("data/included1b.tex", prj1, "included1b", "tex");
        copyFile("data/bibdatabase1a.bib", prj1, "bibdatabase1a", "bib");
        copyFile("data/bibdatabase1b.bib", prj1, "bibdatabase1b", "bib");
        copyFile("data/main2.tex", main2 = prj2.getFileObject("main2", "tex"));
        copyFile("data/included2a.tex", prj2, "included2a", "tex");
        copyFile("data/included2b.tex", prj2, "included2b", "tex");
        copyFile("data/bibdatabase2a.bib", prj2, "bibdatabase2a", "bib");
        copyFile("data/bibdatabase2b.bib", prj2, "bibdatabase2b", "bib");
        
        parseProject(prj1Impl);
        parseProject(prj2Impl);
    }
    
    protected void tearDown() throws java.lang.Exception {
    }

    /**
     * Test of getLookup method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetLookup() {
        System.out.println("testGetLookup");
        
        Project p = ProjectManager.getDefault().findProject(prj1);
        Lookup l = p.getLookup();
        
        assertNotNull("ActionProvider missing in the project lookup.", l.lookup(ActionProvider.class));
        assertNotNull("LaTeXSource missing in the project lookup.", l.lookup(LaTeXSource.class));
        //TODO: other...
    }

    /**
     * Test of getProjectDirectory method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetProjectDirectory() {

        System.out.println("testGetProjectDirectory");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getDisplayName method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetDisplayName() {

        System.out.println("testGetDisplayName");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getIcon method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetIcon() {

        System.out.println("testGetIcon");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getName method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetName() {

        System.out.println("testGetName");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getProject method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetProject() {

        System.out.println("testGetProject");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of createLogicalView method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testCreateLogicalView() {

        System.out.println("testCreateLogicalView");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of findPath method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testFindPath() {

        System.out.println("testFindPath");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getSource method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetSource() {

        System.out.println("testGetSource");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getSupportedActions method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetSupportedActions() {

        System.out.println("testGetSupportedActions");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of invokeAction method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testInvokeAction() {

        System.out.println("testInvokeAction");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of getProjectInternalDir method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetProjectInternalDir() {

        System.out.println("testGetProjectInternalDir");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of contains method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testContains() {

        System.out.println("testContains");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of isActionEnabled method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testIsActionEnabled() {

        System.out.println("testIsActionEnabled");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }

    /**
     * Test of showCustomizer method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testShowCustomizer() {

        System.out.println("testShowCustomizer");
        
        // TODO add your test code below by replacing the default call to fail.
        fail("The test case is empty.");
    }
    
    // TODO add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    
}
