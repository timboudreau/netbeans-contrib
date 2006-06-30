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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.refactoring.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.impl.LaTeXSourceImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public class FindUsagesActionTest extends NbTestCase {
    
    private FileObject dataDir;
    
    public FindUsagesActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.setProperty("netbeans.test.latex.enable", "true");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
        
        dataDir = FileUtil.toFileObject(new File(getDataDir(), "FindUsagesActionTest"));
        
        assertNotNull(dataDir);
    }

    public void testFindUsagesLabel1() throws Exception {
        performFindUsagesTest("testFindUsagesLabel1", 14, 7, "AA");
    }
    
    public void testFindUsagesLabel2() throws Exception {
        performFindUsagesTest("testFindUsagesLabel2", 5, 10, "AA");
    }
    
    public void testFindUsagesLabelMF() throws Exception {
        performFindUsagesTest("testFindUsagesLabelMF1", 14, 7, "AA");
    }
    
    public void testFindUsagesCommand1() throws Exception {
        performFindUsagesTest("testFindUsagesCommand1", 11, 4, "\\AA");
    }
    
    public void testFindUsagesCommand2() throws Exception {
        performFindUsagesTest("testFindUsagesCommand2", 3, 15, "\\AA");
    }
    
    public void testFindUsagesCommandMF() throws Exception {
        performFindUsagesTest("testFindUsagesCommandMF1", 11, 4, "\\AA");
    }
    
    public void testFindUsagesEnvironment1() throws Exception {
        performFindUsagesTest("testFindUsagesEnvironment1", 9, 9, "xxxx");
    }
    
    public void testFindUsagesEnvironment2() throws Exception {
        performFindUsagesTest("testFindUsagesEnvironment2", 2, 19, "xxxx");
    }
    
    public void testFindUsagesEnvironmentMF() throws Exception {
        performFindUsagesTest("testFindUsagesEnvironmentMF1", 10, 9, "xxxx");
    }
    
    private void performFindUsagesTest(String fileName, int line, int column, String newName) throws Exception {
        FileObject testFileObject = dataDir.getFileObject(fileName + ".tex");
        
        assertNotNull(testFileObject);
        
        TestUIDelegate del = (TestUIDelegate) TestUIDelegate.getDefault();
        
        del.clear();
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        Document doc = Utilities.getDefault().openDocument(testFileObject);
        int offset = NbDocument.findLineOffset((StyledDocument) doc, line - 1) + column - 1;
        
        del.newName = newName;
        
        ((FindUsagesAction) FindUsagesAction.get(FindUsagesAction.class)).perform(doc, offset);
        
        ProgressHandle handle = ProgressHandleFactory.createHandle("");
        
        List<? extends Node> results = del.performer.perform(handle);
        
        for (Node n : results) {
            getLog(fileName + "-usages.ref").println(n.getClass() + ":" + n.getStartingPosition().dump() + "-" + n.getEndingPosition().dump());
        }
        
        getLog(fileName + "-usages.ref").close();
        
        compareReferenceFiles(fileName + "-usages.ref", fileName + "-usages.pass", fileName + "-usages.diff");
    }
    
    private void dumpDocument(String name, Document doc) throws Exception {
        try {
            getLog(name + ".ref").print(doc.getText(0, doc.getLength() - 1));
        } finally {
            getLog(name + ".ref").close();
        }
    }
    
}
