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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
import javax.swing.text.Document;
import org.netbeans.core.startup.Main;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.ParseError;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.parser.CommandParser;
import org.openide.filesystems.FileObject;

import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.text.PositionRef;
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
        
        Main.initializeURLFactory();
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
    
    public void testNoError() throws Exception {
        performTest("NoErrorTest.tex");
    }

    public void performTest(String testFileName) throws Exception {
//        doTest("org/netbeans/test/latex/parser/data/testfiles/ErrorDetectionAndCorrection/" + name + ".tex");
        getLog().println("ErrorDetectionAndCorrection test start.");
        
        File testFile = new File(new File(getDataDir(), "ErrorDetectionAndCorrectionTest"), testFileName);
        FileObject testFileObject = FileUtil.toFileObject(testFile);
        
        assertNotNull("The test file " + testFileName + " translated to " + testFile.getPath() + " was not found on the filesystems.", testFileObject);
        
        Collection<ParseError> errors = new ArrayList<ParseError>();
        Collection<Document> documents = new ArrayList<Document>();
        
        DocumentNode node = new CommandParser().parse(testFileObject, documents, errors);
        
        //Print errors:
        Iterator iter = errors.iterator();
        
        while (iter.hasNext()) {
            ParseError err = (ParseError) iter.next();
            SourcePosition pos = err.getStart();
            
            getRef().println("(" + pos.getLine() + ":" + pos.getColumn() + "):" + err.getDisplayName());
        }
        
        getLog().println("ErrorDetectionAndCorrection test end.");
        
        getRef().flush();
        getLog().flush();
        
        assertFile("Output does not match golden file.", getGoldenFile(), new File(getWorkDir(), this.getName() + ".ref"), new File(getWorkDir(), this.getName() + ".diff"));
    }
}
