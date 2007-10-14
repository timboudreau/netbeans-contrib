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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.Node;
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
        System.setProperty("netbeans.user", getWorkDir().getAbsolutePath());
        new File(new File(getWorkDir(), "var"), "log").mkdirs();
        System.setProperty("netbeans.test.latex.enable", "true");
        
        UnitUtilities.prepareTest(new String[] {"/org/netbeans/modules/latex/resources/mf-layer.xml"}, new Object[0]);
        
        dataDir = FileUtil.toFileObject(new File(getDataDir(), "FindUsagesActionTest"));
        
        assertNotNull(dataDir);
        
        FileUtil.setMIMEType("tex", "text/x-tex");
        Main.initializeURLFactory();
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
