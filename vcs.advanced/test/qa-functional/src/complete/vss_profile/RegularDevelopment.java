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

package complete.vss_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcsgeneric.vss.*;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import javax.swing.text.*;
import java.awt.Color;

/** XTest / JUnit test class performing regular development testing on VSS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class RegularDevelopment extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String REFRESH = "VSS|Refresh";
    public static String REFRESH_RECURSIVELY = "VSS|Refresh Recursively";
    public static String CHECK_OUT = "VSS|Check Out";
    public static String CHECK_IN = "VSS|Check In";
    public static String DIFF = "VSS|Diff";
    public static String HISTORY = "VSS|History";
    public static String PROPERTIES = "VSS|Properties";
    public static String GET_LATEST_VERSION = "VSS|Get Latest Version";
    public static String UNDO_CHECK_OUT = "VSS|Undo Check Out";
    private static final Color MODIFIED_COLOR = new Color(160, 200, 255);
    private static final Color NEW_COLOR = new Color(180, 255, 180);
    private static final Color REMOVED_COLOR = new Color(255, 160, 180);
    public static String workingDirectory;
    public static String userName;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public RegularDevelopment(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return RegularDevelopment test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String zipFile = "C:\\Program Files\\Microsoft Visual Studio\\vss.zip";
        if (!new File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTest(new RegularDevelopment("testCheckoutFile"));
        suite.addTest(new RegularDevelopment("testModifyFile"));
        suite.addTest(new RegularDevelopment("testViewDifferences"));
        suite.addTest(new RegularDevelopment("testCheckinFile"));
        suite.addTest(new RegularDevelopment("testViewHistory"));
        suite.addTest(new RegularDevelopment("testGetMissingFile"));
        suite.addTest(new RegularDevelopment("testUnlockFile"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new File(workingDir).mkdirs();
        File outputFile = new File(workingDir + "/output.txt");
        outputFile.createNewFile();
        File errorFile = new File(workingDir + "/error.txt");
        errorFile.createNewFile();
        PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFile));
        PrintWriter errorWriter = new PrintWriter(new FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }

    /** Method will create a file and capture the screen.
     */
    private void captureScreen(String reason) throws Exception {
        File file;
        try {
            file = new File(getWorkDirPath() + "/dump.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch(IOException e) { throw new Exception("Error: Can't create dump file."); }
        PNGEncoder.captureScreen(file.getAbsolutePath());
        throw new Exception(reason);
    }
    
    /** Tries to checkout a file from VSS project repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckoutFile() throws Exception {
        System.out.print(".. Testing checking out a file ..");
        String workingPath = getWorkDirPath();
        workingDirectory = workingPath.substring(0, workingPath.indexOf("RegularDevelopment")) + "RepositoryCreation" + File.separator + "testCreateProjects";
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        new Action(VERSIONING_MENU + "|" + CHECK_OUT, CHECK_OUT).perform(A_FileNode);
        Thread.sleep(2000);
        String children[] = filesystemNode.getChildren();
        int count = children.length;
        boolean found = false;
        for(int i=0; i<count; i++) {
            String child = children[i];
            if (child.startsWith("A_File [Current] (") && child.endsWith(")")) {
                found = true;
                userName = child.substring(18, child.length() - 1);
            }
        }
        if (!found) captureScreen("Error: Unable to find locked A_File [Current] (...) file.");
        new DeleteAction().perform(A_FileNode);
        new NbDialogOperator("Confirm Object Deletion").no();
        System.out.println(". done !");
    }

    /** Tries to modify a file in VSS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testModifyFile() throws Exception {
        System.out.print(".. Testing file modification ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current] (" + userName + ")");
        BufferedWriter writer = new BufferedWriter(new FileWriter(workingDirectory + File.separator + "Work" + File.separator + "A_File.java"));
        writer.write("/** This is testing A_File.java file.\n */\n public class Testing_File {\n     int i;\n }\n");
        writer.flush();
        writer.close();
        new OpenAction().perform(A_FileNode);
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(A_FileNode);
        Thread.sleep(5000);
        A_FileNode = new Node( filesystemNode, "A_File [Locally Modified] (" + userName + ")");
        System.out.println(". done !");
    }

    /** Tries to view differences of the modified file in VSS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testViewDifferences() throws Exception {
        System.out.print(".. Testing view differences ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Locally Modified] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + DIFF, DIFF).perform(A_FileNode);
        TopComponentOperator editor = new TopComponentOperator(new EditorWindowOperator(), "Diff: A_File.java");
        JEditorPaneOperator headRevision = new JEditorPaneOperator(editor, 0);
        JEditorPaneOperator workingRevision = new JEditorPaneOperator(editor, 1);
        String headRevisionContents = "/** This is testing file.\n */\n\n public class Testing_File {\n\n }\n";
        String workingRevisionContents = "/** This is testing A_File.java file.\n */\n\n public class Testing_File {\n     int i;\n }\n";
        if (!headRevisionContents.equals(headRevision.getText()) | !workingRevisionContents.equals(workingRevision.getText()))
            captureScreen("Error: Incorrect diff contents.");
        StyledDocument headRevisionDocument = (StyledDocument) headRevision.getDocument();
        StyledDocument workingRevisionDocument = (StyledDocument) workingRevision.getDocument();
        Color headRevisionLine = (Color) headRevisionDocument.getLogicalStyle(1).getAttribute(StyleConstants.ColorConstants.Background);
        Color workingRevisionLine = (Color) workingRevisionDocument.getLogicalStyle(1).getAttribute(StyleConstants.ColorConstants.Background);
        if (!headRevisionLine.equals(MODIFIED_COLOR) | !workingRevisionLine.equals(MODIFIED_COLOR))
            captureScreen("Error: Incorrect color of modified line.");
        int thirdLineHeadOffset = 30;
        int thirdLineWorkingOffset = 42;
        headRevisionLine = (Color) headRevisionDocument.getLogicalStyle(thirdLineHeadOffset).getAttribute(StyleConstants.ColorConstants.Background);
        Style lineStyle = workingRevisionDocument.getLogicalStyle(thirdLineWorkingOffset);
        if (!headRevisionLine.equals(REMOVED_COLOR) | (lineStyle != null))
            captureScreen("Error: Incorrect color of removed line.");
        int fifthLineHeadOffset = 60;
        int fifthLineWorkingOffset = 72;
        lineStyle = headRevisionDocument.getLogicalStyle(fifthLineHeadOffset);
        workingRevisionLine = (Color) workingRevisionDocument.getLogicalStyle(fifthLineWorkingOffset).getAttribute(StyleConstants.ColorConstants.Background);
        if ((lineStyle != null) | !workingRevisionLine.equals(NEW_COLOR))
            captureScreen("Error: Incorrect color of new line.");
        System.out.println(". done !");
    }

    /** Tries to check in a file into VSS project repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckinFile() throws Exception {
        System.out.print(".. Testing checking in a file ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Locally Modified] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + CHECK_IN, CHECK_IN).perform(A_FileNode);
        CheckinCommandOperator checkinCommand = new CheckinCommandOperator("A_File.java");
        checkinCommand.setChangeDescription("Three lines have changed.");
        checkinCommand.ok();
        Thread.sleep(2000);
        A_FileNode = new Node(filesystemNode, "A_File [Current]");
        File A_File = new File(workingDirectory + File.separator + "Work" + File.separator + "A_File.java");
        if (A_File.canWrite()) captureScreen("Error: A_File.java remained read-write after check in.");
        System.out.println(". done !");
    }

    /** Tries to inspect VSS information about a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testViewHistory() throws Exception {
        System.out.print(".. Testing history information of a file ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        new Action(VERSIONING_MENU + "|" + HISTORY, HISTORY).perform(A_FileNode);
        MainWindowOperator.getDefault().waitStatusText("Command History finished.");
        VCSCommandsOutputOperator outputWindow = new VCSCommandsOutputOperator("History");
        outputWindow.btClose();
        Thread.sleep(5000);
        String currentContents = outputWindow.txtStandardOutput().getText();
        outputWindow.close();
        String goldenContents = "History of $/A_File.java ...\n\n*****************  Version 2   *****************\nUser: " + userName + "     Date:";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents. Golden: |" + goldenContents + "|" + currentContents + "|");
        goldenContents = "Checked in $/\nComment: Three lines have changed.\n\n*****************  Version 1   *****************\nUser: " + userName + "     Date";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents. Golden: |" + goldenContents + "|" + currentContents + "|");
        goldenContents = "Created\nComment: Generated A_File class.\n";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents. Golden: |" + goldenContents + "|" + currentContents + "|");
        new Action(VERSIONING_MENU + "|" + PROPERTIES, PROPERTIES).perform(A_FileNode);
        MainWindowOperator.getDefault().waitStatusText("Command Properties finished.");
        outputWindow = new VCSCommandsOutputOperator("Properties");
        outputWindow.btClose();
        Thread.sleep(5000);
        currentContents = outputWindow.txtStandardOutput().getText();
        outputWindow.close();
        goldenContents = "File:  $/A_File.java\nType:  Text\nSize:  91 bytes      5 lines\nStore only latest version:  No\nLatest:                        \n  Version:  2                     \n  Date:";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect properties contents. Golden: |" + goldenContents + "|" + currentContents + "|");
        goldenContents = "Comment: Generated A_File class.";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect properties contents. Golden: |" + goldenContents + "|" + currentContents + "|");
        System.out.println(". done !");
    }

    /** Tries to delete a file, map it as [Missing] and check it out again.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testGetMissingFile() throws Exception {
        System.out.print(".. Testing getting of a missing file ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node( filesystemNode, "test [Current]");
        Node C_FileNode = new Node( testNode, "C_File [Current]");
        new Action(VERSIONING_MENU + "|" + GET_LATEST_VERSION, GET_LATEST_VERSION).performMenu(C_FileNode);
        GetLatestVersionCommandOperator getLatestVersionCommand = null;
        long oldTimeout = JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 5000);
        try { getLatestVersionCommand = new GetLatestVersionCommandOperator("C_File.java ..."); }
        catch (TimeoutExpiredException ej) {
            try { getLatestVersionCommand= new GetLatestVersionCommandOperator("C_File.form ..."); }
            catch (TimeoutExpiredException ef) {
                JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
                captureScreen("Error: Can't find Get Latest Version dialog.");
            }
        }
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
        getLatestVersionCommand.checkMakeTheLocalCopyWritable(true);
        getLatestVersionCommand.ok();
        String deleteCommand = "cmd /x /c \"del /F /Q " + workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "C_File.*\"";
        if (new ExternalCommand(deleteCommand).exec() != 0) captureScreen("Error: Can't delete C_File files.");
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(testNode);
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        Node C_FileJavaNode = new Node( testNode, "C_File.java [Missing]");
        Node C_FileFormNode = new Node( testNode, "C_File.form [Missing]");
        new ActionNoBlock(VERSIONING_MENU + "|" + GET_LATEST_VERSION, GET_LATEST_VERSION).perform(new Node[] {C_FileJavaNode, C_FileFormNode});
        new GetLatestVersionCommandOperator("C_File.java ...").ok();
        Thread.sleep(2000);
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(testNode); // Until issue #27726 gets fixed.
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        C_FileNode = new Node( testNode, "C_File [Current]");
        File C_File = new File(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "C_File.java");
        if (C_File.canWrite()) captureScreen("Error: C_File.java remained read-write after default get latest version.");
        System.out.println(". done !");
    }

    /** Tries to give up a lock on a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testUnlockFile() throws Exception {
        System.out.print(".. Testing giving up a lock on a file ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node( filesystemNode, "test [Current]");
        Node B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current]");
        new Action(VERSIONING_MENU + "|" + CHECK_OUT, CHECK_OUT).perform(B_FileNode);
        Thread.sleep(2000);
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(testNode); // Until issue #27726 gets fixed.
        B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + UNDO_CHECK_OUT, UNDO_CHECK_OUT).perform(B_FileNode);
        Thread.sleep(2000);
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(testNode); // Until issue #27726 gets fixed.
        B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current]");
        File B_File = new File(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "B_File.java");
        if (B_File.canWrite()) captureScreen("Error: B_File.java remained read-write after undo check out.");
        System.out.println(". done !");
    }
}