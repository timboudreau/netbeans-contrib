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
package org.netbeans.modules.latex.guiproject;

import java.io.File;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.junit.NbTestCase;

import org.netbeans.modules.latex.UnitUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class UtilitiesTest extends NbTestCase {

    public UtilitiesTest(String testName) {
        super(testName);
    }

    private FileObject testDir;
    private FileObject testDirMain;
    private FileObject nbprojectMain;
    private FileObject nbprojectMainBuild;
    private FileObject nbprojectMainInternal;
    private FileObject nbprojectMainInternalBuild;
    private FileObject chapter1;
    private FileObject chapter1Chapter1tex;
    private FileObject chapter1Section1;
    private FileObject chapter1Section1Section1tex;
        
    protected void setUp() throws java.lang.Exception {
        testDir = UnitUtilities.makeScratchDir(this);
        nbprojectMain = testDir.createFolder("nbproject-main.tex");
        nbprojectMainInternal = nbprojectMain.createFolder("internal");
        chapter1 = testDir.createFolder("chapter1");
        chapter1Section1 = chapter1.createFolder("section1");
        
        testDirMain = testDir.createData("main", "tex");
        nbprojectMainBuild = nbprojectMain.createData("build", "xml");
        nbprojectMainInternalBuild = nbprojectMainInternal.createData("build", "xml");
        chapter1Chapter1tex = chapter1.createData("chapter1", "tex");
        chapter1Section1Section1tex = chapter1Section1.createData("section1", "tex");
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(UtilitiesTest.class);
        
        return suite;
    }

    private void testFile(FileObject base, FileObject toTest, String goldenResult) {
        File baseFile = FileUtil.toFile(base);
        File toTestFile = FileUtil.toFile(toTest);
        String realResult = Utilities.findShortestName(baseFile, toTestFile);
        
        log("realResult = " + realResult );
        log("goldenResult = " + goldenResult );
        assertEquals(realResult, goldenResult);
    }
    
    public void testFindShortestNameSameFile() {
        testFile(testDirMain, testDirMain, "main.tex");
    }
    
    public void testFindShortestNameOneAbove() {
        testFile(nbprojectMain, testDirMain, "../main.tex");
    }
    
    public void testFindShortestNameTwoAbove() {
        testFile(nbprojectMainInternal, testDirMain, "../../main.tex");
    }
    
    public void testFindShortestNameOneBelow() {
        testFile(testDirMain, chapter1Chapter1tex, "chapter1/chapter1.tex");
    }
    
    public void testFindShortestNameTwoBelow() {
        testFile(testDirMain, chapter1Section1Section1tex, "chapter1/section1/section1.tex");
    }
    
}
