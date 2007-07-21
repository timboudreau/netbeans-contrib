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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.model.command.impl;

import java.io.File;
import javax.swing.text.StyledDocument;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.command.CommandNode;
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
        //XXX: move to CommandUtilities:
//        FileObject testFileObject = dataDir.getFileObject("testFindNode.tex");
//        
//        assertNotNull(testFileObject);
//        
//        LaTeXSourceImpl lsi =  new LaTeXSourceImpl(testFileObject);
//        
//        LaTeXSource.Lock lock = lsi.lock(true);
//        
//        try {
//            DataObject od = DataObject.find(testFileObject);
//            EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
//            StyledDocument doc = ec.openDocument();
//            
//            int offset;
//            
//            offset = NbDocument.findLineOffset(doc, 2) + 0;
//            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
//            offset = NbDocument.findLineOffset(doc, 4) + 0;
//            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
//            offset = NbDocument.findLineOffset(doc, 8) + 0;
//            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
//            offset = NbDocument.findLineOffset(doc, 9) + 0;
//            assertTrue(lsi.findNode(doc, offset) instanceof CommandNode);
//            
//            //no exception should be thrown when trying to find node outside the document:
////            System.err.println(lsi.findNode(doc, 1000000));
////            assertNull(lsi.findNode(doc, 1000000));
//        } finally {
//            lsi.unlock(lock);
//        }
    }
    
}
