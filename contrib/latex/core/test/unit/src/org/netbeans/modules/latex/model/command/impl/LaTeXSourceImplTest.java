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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
