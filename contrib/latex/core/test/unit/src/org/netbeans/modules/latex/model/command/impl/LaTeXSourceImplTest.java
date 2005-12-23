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

package org.netbeans.modules.latex.model.command.impl;

import java.io.File;
import javax.swing.text.StyledDocument;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXSourceImplTest extends NbTestCase {
    
    private FileObject dataDir;
    
    public LaTeXSourceImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.setProperty("netbeans.test.latex.enable", "true");
        
        UnitUtilities.prepareTest(new String[0], new Object[0]);
        
        dataDir = FileUtil.toFileObject(new File(getDataDir(), "LaTeXSourceImplTest"));
        
        assertNotNull(dataDir);
    }

    public void testFindNode() throws Exception {
        FileObject testFileObject = dataDir.getFileObject("testFindNode.tex");
        
        assertNotNull(testFileObject);
        
        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
        
        LaTeXSource.Lock lock = lsi.lock(true);
        
        try {
            DataObject od = DataObject.find(testFileObject);
            EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
            StyledDocument doc = ec.openDocument();
            
            int offset;
            
            offset = NbDocument.findLineOffset(doc, 2) + 0;
            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
            offset = NbDocument.findLineOffset(doc, 4) + 0;
            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
            offset = NbDocument.findLineOffset(doc, 8) + 0;
            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
            offset = NbDocument.findLineOffset(doc, 9) + 0;
            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
        } finally {
            lsi.unlock(lock);
        }
    }
    
}
