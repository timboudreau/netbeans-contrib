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

package org.netbeans.test.clazz;

import junit.textui.TestRunner;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.RepositoryTabOperator;
import org.netbeans.jellytools.nodes.FolderNode;
import org.netbeans.jellytools.nodes.JavaNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

 
public class SerNodeTest extends JellyTestCase {
    
    private static final String NAME_TEST_FILE = "ClazzTest"; //NOI18N
    private static final String SRC_PACKAGE = "org.netbeans.test"; //NOI18N
    private static final String DST_PACKAGE = "org.netbeans.test.clazz"; //NOI18N
    
    private DataObject srcFile;
    private DataFolder srcFolder;
    private String testFSName;
    
    {
        try {
            srcFolder = DataFolder.findFolder(Repository.getDefault().findResource(SRC_PACKAGE.replace('.','/')));
            testFSName = Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/')).getFileSystem().getDisplayName();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    } 
    
    /** Need to be defined because of JUnit */
    public SerNodeTest(String name) {
        super(name);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new SerNodeTest("testTree")); //NOI18N
        return suite;
    }
    
    /** Use for execution inside IDE */
    public static void main(java.lang.String[] args) {
        // run whole suite
        TestRunner.run(suite());
        // run only selected test case
        //junit.textui.TestRunner.run(new BeansTemplates("testJavaBean"));
    }
    
    /** setUp method  */
    public void setUp() {
        System.out.println("########  "+getName()+"  #######"); //NOI18N
        RepositoryTabOperator.invoke();
    }
    
    /** tearDown method */
    public void tearDown() {
        deleteSer();
    }
    
    private void createSer() {
        JavaNode srcNode = new JavaNode(testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + '|' + DST_PACKAGE.replace('.', '|'));
        dstNode.performPopupActionNoBlock(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste") + '|' + Bundle.getString("org.openide.loaders.Bundle", "CTL_Serialize"));
        new NbDialogOperator(Bundle.getString("org.openide.loaders.Bundle", "SerializeBean_Title")).ok();
        
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".ser")); //NOI18N
        
    }
    
    private void deleteSer() {
        ClazzNodeTest.delete(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".ser"); //NOI18N
    }
    
    public void testTree() {
        createSer();
        workaround();
        dumpNode(new JavaNode(testFSName + '|' + DST_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE), 3);
        compareReferenceFiles();
       
    }
    
    private void workaround() {
        String patterns = Bundle.getString("org.netbeans.modules.beans.Bundle", "Patterns"); 
        new JavaNode(testFSName + '|' + DST_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE + "|class " + NAME_TEST_FILE + "|" + patterns + "|ok").select();
    }
    
    private int tab = -1;
    
    private void writeTabs() {
        int i=0;
        while (i++<tab)
            getRef().print("  ");
    }
    
    private void dumpNode(Node node, int limit) {
        tab++;
        writeTabs();

        String text = node.getText();
        if (text.startsWith("String country") || text.startsWith("String language")){
            tab--;
            return;
        }
        
        if (isLeaf(node) || tab > limit) {
            getRef().println("<" + text + "/>");
            tab--;
            return;
        }
        
        node.expand();
        node.waitExpanded();
        String[] names = node.getChildren();

        getRef().println("<" + text + ">");
        
        for (int i=0; i<names.length; i++) {
            dumpNode(new JavaNode(node.getPath() + "|" + names[i]), limit);
        }

        writeTabs();
        
        int stop = text.indexOf(' ');
        if (stop > 0)
            text = text.substring(0,stop);
              
        getRef().println("</" + text + ">");
        
        tab--;
    }
    
    private static boolean isLeaf(Node node) {
        return ((org.openide.nodes.Node) node.getOpenideNode()).isLeaf();
    }
        
}
