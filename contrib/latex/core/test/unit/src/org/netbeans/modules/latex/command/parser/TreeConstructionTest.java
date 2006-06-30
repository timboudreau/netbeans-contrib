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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.command.parser;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.junit.NbTestCase;

import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.netbeans.modules.latex.model.command.impl.NBDocumentNodeImpl;
import org.netbeans.modules.latex.model.command.parser.CommandParser;
import org.netbeans.modules.latex.test.TestCertificate;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public class TreeConstructionTest extends NbTestCase {
    
    /** Creates a new instance of TreeConstructionPerformer */
    public TreeConstructionTest(String name) {
        super(name);
    }
    
    protected void setUp() throws IOException, SAXException, PropertyVetoException {
        System.setProperty("netbeans.test.latex.enable", "true");
        getLog().println("ErrorDetectionAndCorrectionPerformer.setUp started.");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
        
        //TODO: this should be done somehow better. How?:
        File[] roots = File.listRoots();
        
        for (int cntr = 0; cntr < roots.length; cntr++) {
            LocalFileSystem lfs = new LocalFileSystem();
            
            lfs.setRootDirectory(roots[cntr]);
            
            Repository.getDefault().addFileSystem(lfs);
        }
        
        getLog().println("ErrorDetectionAndCorrectionPerformer.setUp finished.");
    }

    
    public void testTest1() throws Exception {
        performTest("Test1.tex");
    }

    public void testFreearg1() throws Exception {
        performTest("freearg1.tex");
    }

    public void testFreearg2() throws Exception {
        performTest("freearg2.tex");
    }

    public void testFreearg3() throws Exception {
        performTest("freearg3.tex");
    }
    
    public void performTest(String testFileName) throws Exception {
        getLog().println("TreeConstruction test start.");
        
        File testFile = new File(new File(getDataDir(), "TreeConstructionTest"), testFileName);
        FileObject testFileObject = FileUtil.toFileObject(testFile);
        
        assertNotNull("The test file " + testFileName + " translated to " + testFile.getPath() + " was not found on the filesystems.", testFileObject);
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        getLog().println("Parsing:");
        DocumentNode node = new CommandParser().parse(lsi, errors);
        getLog().println("Done.");
        
        PrintWriter ref = new PrintWriter(getRef());
        
        //Print node
        getLog().println("Dumping:");
        ((NBDocumentNodeImpl) node).dump(TestCertificate.get(), ref);
        getLog().println("Done.");
        
        getLog().println("TreeConstruction test end.");
        
        ref.flush();
        
        getLog().flush();
        
        assertFile("Output does not match golden file.", getGoldenFile(), new File(getWorkDir(), this.getName() + ".ref"), new File(getWorkDir(), this.getName() + ".diff"));
    }
    
}
