/*
 * ComponentGeneratorTest.java
 *
 * Created on July 11, 2002, 2:27 PM
 */

package org.netbeans.modules.jemmysupport.generator;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.*;
import org.netbeans.junit.*;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.modules.jemmysupport.ComponentGeneratorOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.drivers.input.KeyRobotDriver;
import org.netbeans.modules.jemmysupport.generator.data.TestPanel;

/** JUnit test suite with Jemmy/Jelly2 support
 *
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public class ComponentGeneratorTest extends JellyTestCase {
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public ComponentGeneratorTest(String testName) {
        super(testName);
    }
    
    /** method used for explicit testsuite definition
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new ComponentGeneratorTest("testGenerate"));
        suite.addTest(new ComponentGeneratorTest("testComponentsEditor"));
        suite.addTest(new ComponentGeneratorTest("testGeneratedCode"));
        return suite;
    }
    
    /** method called before each testcase
     */
    protected void setUp() throws IOException {
    }
    
    /** method called after each testcase
     */
    protected void tearDown() {
    }
    
    /** Use for internal test execution inside IDE
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** simple test case
     */
    public void testGenerate() throws Exception {
        ComponentGeneratorOperator gen = ComponentGeneratorOperator.invoke();
        gen.checkShowComponentsEditor(false);
        ExplorerOperator exp = ExplorerOperator.invoke();
        String fsid = exp.repositoryTab().mountLocalDirectoryAPI(getWorkDir().getAbsolutePath());
        new Node(gen.treePackage(), "ComponentGeneratorTest").select();
        gen.start();
        JFrameOperator testFrame=new JFrameOperator(TestPanel.showFrame());
        Robot r=new Robot();
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_F12);
        r.keyRelease(KeyEvent.VK_F12);
        r.keyRelease(KeyEvent.VK_CONTROL);
//        gen.verifyStatus("Finished:");
        Thread.sleep(2000);
        gen.stop();
        gen.close();
        testFrame.close();
        FilesystemNode fsnode=new FilesystemNode(exp.repositoryTab().tree(), "ComponentGeneratorTest");
        fsnode.refreshFolder();
        new Node(fsnode, "TestFrame").select();
        exp.repositoryTab().unmountFileSystemAPI(fsid);
    }
    
    /** test case with golden file
     */
    public void testComponentsEditor() {
        PrintStream ref = getRef();

        // place test case body here

        compareReferenceFiles();
    }
    
    /** test case with golden file
     */
    public void testGeneratedCode() {
        PrintStream ref = getRef();

        // place test case body here

        compareReferenceFiles();
    }
    
}
