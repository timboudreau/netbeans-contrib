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
public class RenameActionTest extends NbTestCase {
    
    private FileObject dataDir;
    
    public RenameActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.setProperty("netbeans.test.latex.enable", "true");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
        
        dataDir = FileUtil.toFileObject(new File(getDataDir(), "RenameActionTest"));
        
        assertNotNull(dataDir);
    }

    public void testRenameLabel1() throws Exception {
        performRenameTest("testRenameLabel1", 14, 7, "AA");
    }
    
    public void testRenameLabel2() throws Exception {
        performRenameTest("testRenameLabel2", 5, 10, "AA");
    }
    
    public void testRenameLabelMF() throws Exception {
        performRenameTest("testRenameLabelMF1", 14, 7, "AA");
    }
    
    public void testRenameCommand1() throws Exception {
        performRenameTest("testRenameCommand1", 11, 4, "\\AA");
    }
    
    public void testRenameCommand2() throws Exception {
        performRenameTest("testRenameCommand2", 3, 15, "\\AA");
    }
    
    public void testRenameCommandMF() throws Exception {
        performRenameTest("testRenameCommandMF1", 11, 4, "\\AA");
    }
    
    private void performRenameTest(String fileName, int line, int column, String newName) throws Exception {
        FileObject testFileObject = dataDir.getFileObject(fileName + ".tex");
        
        assertNotNull(testFileObject);
        
        TestUIDelegate del = (TestUIDelegate) TestUIDelegate.getDefault();
        
        del.clear();
        
        Collection errors = new ArrayList();
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        Document doc = Utilities.getDefault().openDocument(testFileObject);
        int offset = NbDocument.findLineOffset((StyledDocument) doc, line - 1) + column - 1;
        
        del.newName = newName;
        
        ((RenameAction) RenameAction.get(RenameAction.class)).perform(doc, offset);
        
        ProgressHandle handle = ProgressHandleFactory.createHandle("");
        
        List<? extends Node> results = del.performer.perform(handle);
        
        for (Node n : results) {
            getLog(fileName + "-usages.ref").println(n.getClass() + ":" + n.getStartingPosition().dump() + "-" + n.getEndingPosition().dump());
        }
        
        getLog(fileName + "-usages.ref").close();
        
        handle = ProgressHandleFactory.createHandle("");
        
        del.refactor.refactor(results, handle);
        
        LaTeXSource.Lock lock = null;
        
        try {
            lock = lsi.lock();
            
            for (Iterator i = lsi.getDocument().getFiles().iterator(); i.hasNext(); ) {
                FileObject file = (FileObject) i.next();
                
                String name = file.getName();
                
                dumpDocument(name, Utilities.getDefault().openDocument(file));
                compareReferenceFiles(name + ".ref", name + ".pass", name + ".diff");
            }
        } finally {
            if (lock != null) {
                lsi.unlock(lock);
            }
        }
        
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
