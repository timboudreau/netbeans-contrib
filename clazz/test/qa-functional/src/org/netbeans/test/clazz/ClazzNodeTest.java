/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.test.clazz;

import java.io.IOException;
import junit.textui.TestRunner;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.RepositoryTabOperator;
import org.netbeans.jellytools.nodes.FolderNode;
import org.netbeans.jellytools.nodes.JavaNode;
import org.netbeans.jemmy.EventTool;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

 
public class ClazzNodeTest extends JellyTestCase {
    
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
    public ClazzNodeTest(String name) {
        super(name);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new ClazzNodeTest("testCopyPasteCopy")); //NOI18N
        suite.addTest(new ClazzNodeTest("testCopyPasteCreateLink")); //NOI18N
        suite.addTest(new ClazzNodeTest("testCopyPasteSerialize")); //NOI18N
        suite.addTest(new ClazzNodeTest("testCopyPasteDefaultInstance")); //NOI18N
        suite.addTest(new ClazzNodeTest("testCutPasteCopy")); //NOI18N
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
    }
    
    public void testCopyPasteCopy() {
        JavaNode srcNode = new JavaNode(testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + '|' + DST_PACKAGE.replace('.', '|'));
        dstNode.pasteCopy();
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".class")); //NOI18N 
        
        delete(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".class"); //NOI18N
    }
    
    public void testCopyPasteCreateLink() {
        JavaNode srcNode = new JavaNode(testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        dstNode.pasteLink();
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".shadow")); //NOI18N
        
        delete(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".shadow"); //NOI18N
    }
    
    public void testCopyPasteSerialize() {
        JavaNode srcNode = new JavaNode(testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + '|' + DST_PACKAGE.replace('.', '|'));
        dstNode.performPopupActionNoBlock(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste") + '|' + Bundle.getStringTrimmed("org.openide.loaders.Bundle", "CTL_Serialize"));
        new NbDialogOperator(Bundle.getString("org.openide.loaders.Bundle", "SerializeBean_Title")).ok();
        
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".ser")); //NOI18N
        
        delete(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".ser"); //NOI18N
    }
    
    public void testCopyPasteDefaultInstance() {
        System.out.println("YYYY ->" + testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        JavaNode srcNode = new JavaNode(testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + '|' + DST_PACKAGE.replace('.', '|'));
        dstNode.performPopupActionNoBlock(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste") + '|' + Bundle.getStringTrimmed("org.openide.loaders.Bundle", "PT_instance"));
        
        new EventTool().waitNoEvent(1000);
        System.out.println("XXX ->" + DST_PACKAGE.replace('.', '/') + '/' + SRC_PACKAGE.replace('.', '-') + '-' + NAME_TEST_FILE + ".instance"); //NOI18N
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + SRC_PACKAGE.replace('.', '-') + '-' + NAME_TEST_FILE + ".instance")); //NOI18N
        
        delete(DST_PACKAGE.replace('.', '/') + '/' + SRC_PACKAGE.replace('.', '-') + '-' + NAME_TEST_FILE + ".instance"); //NOI18N
    }
    
    public void testCutPasteCopy() {
        JavaNode srcNode = new JavaNode(testFSName + '|' + SRC_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.cut();
        new EventTool().waitNoEvent(1000);
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        System.out.println("AAAAAA");
        dstNode.performPopupAction(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste"));
        System.out.println("BBBBBB");
        srcNode.waitNotPresent();
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".class")); //NOI18N
        assertNull(Repository.getDefault().findResource(SRC_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".class")); //NOI18N
        new EventTool().waitNoEvent(1000);
        srcNode = new JavaNode(testFSName + '|' + DST_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE);
        srcNode.cut();
        new EventTool().waitNoEvent(1000);
        dstNode = new FolderNode(testFSName + "|" + SRC_PACKAGE.replace('.', '|'));
        dstNode.select();
        new EventTool().waitNoEvent(1000);
//        dstNode.performPopupActionNoBlock(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste"));
        dstNode.performPopupAction(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste"));
        srcNode.waitNotPresent();
        new EventTool().waitNoEvent(1000);
    }

    public static void delete(String file) {
        FileObject fileObject = Repository.getDefault().findResource(file);
        if (fileObject==null) return;
        try {
            DataObject.find(fileObject).delete();
        } catch (IOException e) {
        }
    }
}
