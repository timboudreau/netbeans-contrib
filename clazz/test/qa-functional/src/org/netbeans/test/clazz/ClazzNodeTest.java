package org.netbeans.test.clazz;

import junit.textui.TestRunner;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.nodes.FolderNode;
import org.netbeans.jellytools.nodes.JavaNode;
import org.netbeans.jemmy.EventTool;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

 
public class ClazzNodeTest extends JellyTestCase {
    
    private static final String NAME_TEST_FILE = "ClazzTest";
    private static final String SRC_PACKAGE = "org.netbeans.test";
    private static final String DST_PACKAGE = "org.netbeans.test.clazz";
    
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
        suite.addTest(new ClazzNodeTest("testCopyPasteCopy"));
        suite.addTest(new ClazzNodeTest("testCopyPasteCreateLink"));
        suite.addTest(new ClazzNodeTest("testCopyPasteSerialize"));
        suite.addTest(new ClazzNodeTest("testCopyPasteDefaultInstance"));
        suite.addTest(new ClazzNodeTest("testCutPasteCopy"));
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
        System.out.println("########  "+getName()+"  #######");
    }
    
    /** tearDown method */
    public void tearDown() {
    }
    
    public void testCopyPasteCopy() {
        JavaNode srcNode = new JavaNode(testFSName + "|" + SRC_PACKAGE.replace('.', '|') + "|" + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        dstNode.pasteCopy();
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".class"));
        
        delete(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".class");
    }
    
    public void testCopyPasteCreateLink() {
        JavaNode srcNode = new JavaNode(testFSName + "|" + SRC_PACKAGE.replace('.', '|') + "|" + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        dstNode.pasteLink();
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".shadow"));
        
        delete(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".shadow");
    }
    
    public void testCopyPasteSerialize() {
        JavaNode srcNode = new JavaNode(testFSName + "|" + SRC_PACKAGE.replace('.', '|') + "|" + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        dstNode.performPopupActionNoBlock("Paste|Serialize");
        new NbDialogOperator("Instance Serialization").ok();
        
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".ser"));
        
        delete(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".ser");
    }
    
    public void testCopyPasteDefaultInstance() {
        JavaNode srcNode = new JavaNode(testFSName + "|" + SRC_PACKAGE.replace('.', '|') + "|" + NAME_TEST_FILE);
        srcNode.copy();
        
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        dstNode.performPopupActionNoBlock("Paste|Default instance");
        
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + "/" + SRC_PACKAGE.replace('.', '-') + "-" + NAME_TEST_FILE + ".instance"));
        
        delete(DST_PACKAGE.replace('.', '/') + "/" + SRC_PACKAGE.replace('.', '-') + "-" + NAME_TEST_FILE + ".instance");
    }
    
    public void testCutPasteCopy() {
        JavaNode srcNode = new JavaNode(testFSName + "|" + SRC_PACKAGE.replace('.', '|') + "|" + NAME_TEST_FILE);
        srcNode.cut();
        
        FolderNode dstNode = new FolderNode(testFSName + "|" + DST_PACKAGE.replace('.', '|'));
        dstNode.performPopupActionNoBlock("Paste");
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".class"));
        assertNull(Repository.getDefault().findResource(SRC_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".class"));
        
        delete(DST_PACKAGE.replace('.', '/') + "/" + NAME_TEST_FILE + ".class");
    }

    public static void delete(String file) {
        FileObject fileObject = Repository.getDefault().findResource(file);
        if (fileObject==null) return;
        try {
            DataObject.find(fileObject).delete();
        } catch (java.io.IOException e) {
        }
    }
}
