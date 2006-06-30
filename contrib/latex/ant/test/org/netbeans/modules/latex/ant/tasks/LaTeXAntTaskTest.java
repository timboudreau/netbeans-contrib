/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ant.tasks;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXAntTaskTest extends BuildFileTest {

    /** Creates a new instance of LaTeXAntTaskTest */
    public LaTeXAntTaskTest(String name) {
        super(name);
    }

    private File buildFile = new File("test/org/netbeans/modules/latex/ant/tasks/latex.xml");

    public void setUp() {
        configureProject(buildFile.getPath());
        project.setBaseDir(buildFile.getParentFile());
    }
    
    public void testCompile() {
        File testFile = new File("test/org/netbeans/modules/latex/ant/tasks/latex.tex");
        
        assertTrue("The test file does not exist.", testFile.exists());
        
        testFile.setLastModified(System.currentTimeMillis());
        
        project.setProperty("mainfile", testFile.getAbsolutePath());
        
        executeTarget("plain-latex");
        
        String log = getFullLog();
        
        int index = 0;
        
        index = log.indexOf("LaTeXing, mainfile", index);
        
        assertFalse("The source was not LaTeXed!", index == (-1));
        
        index = log.indexOf("LaTeXing, mainfile", index);
        
        assertFalse("The source was not LaTeXed second time!", index == (-1));
        
        //TODO: Enable once parsing of the output is done:
//        index = log.indexOf("LaTeXing, mainfile", index);
//        
//        assertTrue("The source was LaTeXed third time!", index == (-1));
    }
    
    public void testAbsoluteFile() {
        File testFile = new File("test/org/netbeans/modules/latex/ant/tasks/latex.tex");
        
        assertTrue("The test file does not exist.", testFile.exists());
        
        testFile.setLastModified(System.currentTimeMillis());
        
        project.setProperty("mainfile", testFile.getAbsolutePath());
        
        try {
            executeTarget("plain-latex");
        } catch (BuildException e) {
            fail("A BuildException has been thrown: " + e.getMessage());
        }
    }
    
    public void testRelativeFile() {
        //TODO: touch the test file so it is forced to be compiled:
        project.setProperty("mainfile", "latex.tex");
        
        try {
            executeTarget("plain-latex");
        } catch (BuildException e) {
            fail("A BuildException has been thrown: " + e.getMessage());
        }
    }

    public void testNonExistingFile() {
        File testFile = new File("test/org/netbeans/modules/latex/ant/tasks/non-existing-file.tex");
        
        assertTrue("The test file exists!", !testFile.exists());
        
        project.setProperty("mainfile", testFile.getAbsolutePath());
        
        expectBuildExceptionContaining("plain-latex", "Non-existing main file does not throw the exception.", "Mainfile");
    }
}
