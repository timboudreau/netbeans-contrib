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
        dstNode.performPopupActionNoBlock(Bundle.getStringTrimmed("org.openide.actions.Bundle", "Paste") + '|' + Bundle.getString("org.openide.actions.Bundle", "CTL_Serialize"));
        new NbDialogOperator(Bundle.getString("org.openide.loaders.Bundle", "SerializeBean_Title")).ok();
        
        new EventTool().waitNoEvent(1000);
        assertNotNull(Repository.getDefault().findResource(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".ser")); //NOI18N
        
    }
    
    private void deleteSer() {
        ClazzNodeTest.delete(DST_PACKAGE.replace('.', '/') + '/' + NAME_TEST_FILE + ".ser"); //NOI18N
    }
    
    public void testTree() {
        createSer();
        dumpNode(new JavaNode(testFSName + '|' + DST_PACKAGE.replace('.', '|') + '|' + NAME_TEST_FILE), 3);
        compareReferenceFiles();
       
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

        node.select();
        String[] names = node.getChildren();
        String text = node.getText();
        if (text.startsWith("String country") || text.startsWith("String language")){
            tab--;
            return;
        }
        if (names.length == 0 || tab > limit) {
            getRef().println("<" + text + "/>");
            tab--;
            return;
        }
        
        node.expand();
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
}
