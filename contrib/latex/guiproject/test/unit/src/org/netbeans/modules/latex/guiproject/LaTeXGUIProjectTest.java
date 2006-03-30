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

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectTest extends ProjectTestCase {
    
    public LaTeXGUIProjectTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();//LaTeXGUIProjectTest.class);
        
        //only one test is currently working:
        suite.addTest(TestSuite.createTest(LaTeXGUIProjectTest.class, "testGetLookup"));
        
        return suite;
    }

    protected void tearDown() throws java.lang.Exception {
    }

    /**
     * Test of getLookup method, of class org.netbeans.modules.latex.guiproject.LaTeXGUIProject.
     */
    public void testGetLookup() throws IOException {
        Project p = ProjectManager.getDefault().findProject(prj1Impl);
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
