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
package org.netbeans.modules.latex.command.parser;


import java.beans.PropertyVetoException;
import org.netbeans.junit.NbTestCase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.ParseError;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.netbeans.modules.latex.model.command.parser.CommandParser;
import org.openide.filesystems.FileObject;

import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class ErrorDetectionAndCorrectionTest extends  NbTestCase {
    
    /** Creates a new instance of ErrorDetectionAndCorrectionPerformer */
    public ErrorDetectionAndCorrectionTest(String name) {
        super(name);
    }
    
    protected void setUp() throws IOException, SAXException, PropertyVetoException {
        log("ErrorDetectionAndCorrectionPerformer.setUp started.");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
        
        //TODO: this should be done somehow better. How?:
        File[] roots = File.listRoots();
        
        for (int cntr = 0; cntr < roots.length; cntr++) {
            LocalFileSystem lfs = new LocalFileSystem();
            
            lfs.setRootDirectory(roots[cntr]);
            
            Repository.getDefault().addFileSystem(lfs);
        }
        
        log("ErrorDetectionAndCorrectionPerformer.setUp finished.");
    }
    
    public void testTest1() throws Exception {
        performTest("Test1.tex");
    }

    public void testTest2() throws Exception {
        performTest("Test2.tex");
    }
    
    public void testTest3() throws Exception {
        performTest("Test3.tex");
    }
    
    public void testTest4() throws Exception {
        performTest("Test4.tex");
    }
    
    public void testTest5() throws Exception {
        performTest("Test5.tex");
    }

    public void performTest(String testFileName) throws Exception {
//        doTest("org/netbeans/test/latex/parser/data/testfiles/ErrorDetectionAndCorrection/" + name + ".tex");
        getLog().println("ErrorDetectionAndCorrection test start.");
        
        File testFile = new File(new File(getDataDir(), "ErrorDetectionAndCorrectionTest"), testFileName);
        FileObject testFileObject = FileUtil.toFileObject(testFile);
        
        assertNotNull("The test file " + testFileName + " translated to " + testFile.getPath() + " was not found on the filesystems.", testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        DocumentNode node = new CommandParser().parse(lsi, errors);
        
        //Print errors:
        Iterator iter = errors.iterator();
        
        while (iter.hasNext()) {
            ParseError err = (ParseError) iter.next();
            SourcePosition pos = err.getPosition();
            
            getRef().println("(" + pos.getLine() + ":" + pos.getColumn() + "):" + err.getMessage());
        }
        
        getLog().println("ErrorDetectionAndCorrection test end.");
        
        getRef().flush();
        getLog().flush();
        
        assertFile("Output does not match golden file.", getGoldenFile(), new File(getWorkDir(), this.getName() + ".ref"), null);
    }
}
