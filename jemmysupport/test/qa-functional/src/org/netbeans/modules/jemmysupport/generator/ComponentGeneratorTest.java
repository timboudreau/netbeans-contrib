/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

/*
 * ComponentGeneratorTest.java
 *
 * Created on July 11, 2002, 2:27 PM
 */

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.*;

import junit.framework.*;
import org.netbeans.junit.*;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.modules.jemmysupport.*;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.StringProperty;
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
        suite.addTest(new ComponentGeneratorTest("testPrepareFS"));
        suite.addTest(new ComponentGeneratorTest("testGrabFrame"));
        suite.addTest(new ComponentGeneratorTest("testVerifyFrameCode"));
        suite.addTest(new ComponentGeneratorTest("testGrabDialog"));
        suite.addTest(new ComponentGeneratorTest("testVerifyDialogCode"));
        suite.addTest(new ComponentGeneratorTest("testComponentsEditor"));
        suite.addTest(new ComponentGeneratorTest("testVerifyFrameModifiedCode"));
        suite.addTest(new ComponentGeneratorTest("testRemoveFS"));
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
    public void testPrepareFS() throws Exception {
        ExplorerOperator.invoke().repositoryTab().mountLocalDirectoryAPI(getWorkDir().getParentFile().getAbsolutePath());
        File files[] = getWorkDir().getParentFile().listFiles();
        for (int i=0; i<files.length; i++) 
            if (files[i].getName().indexOf("TestFrame")>=0 ||
                files[i].getName().indexOf("TestDialog")>=0) assertTrue(files[i].delete());
    }
    
    /** simple test case
     */
    public void testRemoveFS() throws Exception {
        File files[] = getWorkDir().getParentFile().listFiles();
        for (int i=0; i<files.length; i++) 
            if (files[i].getName().indexOf("TestFrame")>=0 ||
                files[i].getName().indexOf("TestDialog")>=0) assertTrue(files[i].delete());
        new FilesystemNode(ExplorerOperator.invoke().repositoryTab().tree(), "ComponentGeneratorTest").unmount();
    }
                
    /** simple test case
     */
    public void testGrabFrame() throws Exception {
        closeAllModal=true;
        ComponentGeneratorOperator gen = ComponentGeneratorOperator.invoke();
        gen.checkCreateScreenshot(false);
        gen.checkShowComponentsEditor(false);
        ExplorerOperator exp = ExplorerOperator.invoke();
        new Node(gen.treePackage(), "ComponentGeneratorTest").select();
        gen.start();
        gen.verifyStatus("Use CTRL-F12");
        JFrameOperator testFrame=new JFrameOperator(TestPanel.showFrame());
        try {
            Robot r=new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_F12);
            r.keyRelease(KeyEvent.VK_F12);
            r.keyRelease(KeyEvent.VK_CONTROL);
            gen.verifyStatus("Finished:");
            gen.stop();
            gen.close();
        } finally {
            testFrame.close();
        }
        FilesystemNode fsnode=new FilesystemNode(exp.repositoryTab().tree(), "ComponentGeneratorTest");
        fsnode.refreshFolder();
        new Node(fsnode, "TestFrame").select();
    }
    
    /** test case with golden file
     */
    public void testVerifyFrameCode() throws Exception {
        captureScreen=false;
        dumpScreen=false;
        File f = new File(getWorkDir().getParentFile(), "TestFrame.java");
        assertTrue(f.exists());
        BufferedReader in = new BufferedReader(new FileReader(f));
        PrintStream ref = getRef();
        String line;
        while (in.ready()) {
            line = in.readLine();
            if (!line.startsWith(" * Created on") && !line.startsWith(" * @author"))
                ref.println(line);
        }
        in.close();
        ref.close();
        compareReferenceFiles();
    }
                
    /** simple test case
     */
    public void testGrabDialog() throws Exception {
        closeAllModal=true;
        ComponentGeneratorOperator gen = ComponentGeneratorOperator.invoke();
        gen.checkCreateScreenshot(true);
        gen.checkShowComponentsEditor(false);
        ExplorerOperator exp = ExplorerOperator.invoke();
        new Node(gen.treePackage(), "ComponentGeneratorTest").select();
        gen.start();
        gen.verifyStatus("Use CTRL-F12");
        JDialogOperator testDialog=new JDialogOperator(TestPanel.showDialog());
        try {
            Robot r=new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_F12);
            r.keyRelease(KeyEvent.VK_F12);
            r.keyRelease(KeyEvent.VK_CONTROL);
            gen.verifyStatus("Finished:");
            gen.stop();
            gen.close();
        } finally {
            testDialog.close();
        }
        FilesystemNode fsnode=new FilesystemNode(exp.repositoryTab().tree(), "ComponentGeneratorTest");
        fsnode.refreshFolder();
        new Node(fsnode, "TestDialog").select();
    }
    
    /** test case with golden file
     */
    public void testVerifyDialogCode() throws Exception {
        captureScreen=false;
        dumpScreen=false;
        File f = new File(getWorkDir().getParentFile(), "TestDialog.java");
        assertTrue(f.exists());
        BufferedReader in = new BufferedReader(new FileReader(f));
        PrintStream ref = getRef();
        String line;
        while (in.ready()) {
            line = in.readLine();
            if (!line.startsWith(" * Created on") && !line.startsWith(" * @author"))
                ref.println(line);
        }
        in.close();
        ref.close();
        compareReferenceFiles();
        assertTrue(new File(getWorkDir().getParentFile(), "TestDialog.png").exists());
    }

    /** test case with golden file
     */
    public void testComponentsEditor() throws Exception {
        closeAllModal=true;
        ComponentGeneratorOperator gen = ComponentGeneratorOperator.invoke();
        gen.checkCreateScreenshot(false);
        gen.checkShowComponentsEditor(true);
        ExplorerOperator exp = ExplorerOperator.invoke();
        new Node(gen.treePackage(), "ComponentGeneratorTest").select();
        gen.start();
        gen.verifyStatus("Use CTRL-F12");
        JFrameOperator testFrame=new JFrameOperator(TestPanel.showFrame());
        try {
            Robot r=new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_F12);
            r.keyRelease(KeyEvent.VK_F12);
            r.keyRelease(KeyEvent.VK_CONTROL);
            ComponentsEditorOperator editor = new ComponentsEditorOperator();
            new Node(editor.treeComponentsTree(), "").select();
            Thread.sleep(1000);
            new StringProperty(editor.propertySheet(), "shortName").setStringValue("TestFrameModified");
            editor.verify();
            editor.ok();
            gen.verifyStatus("Finished:");
            gen.stop();
            gen.close();
        } finally {
            testFrame.close();
        }
        FilesystemNode fsnode=new FilesystemNode(exp.repositoryTab().tree(), "ComponentGeneratorTest");
        fsnode.refreshFolder();
        new Node(fsnode, "TestFrameModified").select();
    }
    
    
    /** test case with golden file
     */
    public void testVerifyFrameModifiedCode() throws Exception {
        captureScreen=false;
        dumpScreen=false;
        File f = new File(getWorkDir().getParentFile(), "TestFrameModified.java");
        assertTrue(f.exists());
        BufferedReader in = new BufferedReader(new FileReader(f));
        PrintStream ref = getRef();
        String line;
        while (in.ready()) {
            line = in.readLine();
            if (!line.startsWith(" * Created on") && !line.startsWith(" * @author"))
                ref.println(line);
        }
        in.close();
        ref.close();
        compareReferenceFiles();
    }
}
