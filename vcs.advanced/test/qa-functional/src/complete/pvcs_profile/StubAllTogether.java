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

package complete.pvcs_profile;

import complete.GenericStub.GenericNode;
import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.PVCSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.AddCommandOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.*;
import org.netbeans.jellytools.util.StringFilter;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.junit.NbTestSuite;
import org.openide.util.Utilities;

public class StubAllTogether extends PVCSStub {
    
    public StubAllTogether(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        complete.GenericStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        suite.addTest(new StubAllTogether("configure"));
        suite.addTest(new StubAllTogether("testCreateProject"));
        suite.addTest(new StubAllTogether("testAddSingleFile"));
        suite.addTest(new StubAllTogether("testAddMultipleFiles"));
        suite.addTest(new StubAllTogether("testAddFileWithLock"));
        suite.addTest(new StubAllTogether("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    GenericNode test, another, A_File, B_File, D_File, C_File;
    
    public void createStructure () {
        test = new GenericNode (root, "test");
        another = new GenericNode (test, "another");
        A_File = new GenericNode (root, "A_File", ".java");
        B_File = new GenericNode (test, "B_File", ".java");
        D_File = new GenericNode (another, "D_File", ".java");
//        C_File = new GenericNode (test, "C_File", new String[] { ".java", ".form" }); // bug in add command - serial execution needed
        C_File = new GenericNode (test, "C_File", ".java"); // bug in add command - serial execution needed
    }
    
    public void configure () {
        super.configure ();
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
        new Node (exp.repositoryTab().tree (), "").waitChildNotPresent(root.node ());
    }

    public void testCreateProject () {
        another.mkdirs ();
        refresh (root);
        test.waitStatus ("Local");
        createProject (test);
        another.waitStatus ("Local");
        createProject (another);
    }
    
    public void testAddSingleFile () {
        NewWizardOperator.create("Java Classes|Main", A_File.parent ().node (), A_File.name ());
        A_File.waitStatus ("Local");
        A_File.pvcsNode ().pVCSAdd ();
        AddCommandOperator addCommand = new AddCommandOperator("");
        addCommand.setWorkfileDescription("Auto-generated class file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.ok();
        A_File.waitHistory ("Add");
        A_File.waitStatus ("Current");
    }
    
    public void testAddMultipleFiles () {
        B_File.save ("B_File - Initial content");
        refresh (B_File.parent ());
        B_File.waitStatus ("Local");
        D_File.save ("B_File - Initial content");
        refresh (D_File.parent ());
        D_File.waitStatus ("Local");

        new PVCSAddAction ().perform (new Node [] {
            B_File.pvcsNode (),
            D_File.pvcsNode (),
        });
        AddCommandOperator addCommand = new AddCommandOperator("");
        addCommand.setWorkfileDescription("Auto-generated class file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.ok();
        B_File.waitHistory ("Add");
        D_File.waitHistory ("Add");
        B_File.waitStatus ("Current");
        D_File.waitStatus ("Current");
    }
    
    public void testAddFileWithLock() throws Exception {
        C_File.save ("Initial save");
//        C_File.save (1, "");
        refresh (C_File.parent ());
        C_File.waitStatus ("Local");
        C_File.pvcsNode ().pVCSAdd ();
        AddCommandOperator addCommand = new AddCommandOperator("");
        addCommand.setWorkfileDescription("Auto-generated form file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.setAssignAVersionLabel("My_Version");
        addCommand.checkKeepTheRevisionLocked(true);
        addCommand.checkFloatLabelWithTheTipRevision(true);
        addCommand.ok();
        C_File.waitHistory ("Add");
        C_File.waitStatus ("Current; 1.1");
        C_File.waitLock (true);
    }
    
}
