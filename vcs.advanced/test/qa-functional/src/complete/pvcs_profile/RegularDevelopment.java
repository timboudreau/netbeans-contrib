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

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.netbeans.jellytools.modules.vcscore.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.*;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.properties.*;
import javax.swing.text.*;
import java.awt.Color;
import java.util.Date;

/** XTest / JUnit test class performing regular development testing on PVCS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class RegularDevelopment extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String REFRESH = "PVCS|Refresh";
    public static String RECURSIVE_REFRESH = "PVCS|Refresh Recursively";
    public static String GET = "PVCS|Get";
    public static String DIFF = "PVCS|Diff";
    public static String PUT = "PVCS|Put";
    public static String HISTORY = "PVCS|History";
    public static String UNLOCK = "PVCS|Unlock";
    public static String REMOVE_REVISION = "PVCS|Remove Revision";
    public static String LOCK = "PVCS|Lock";
    public static String VERSIONING_EXPLORER = "Versioning Explorer";
    public static String workingDirectory;
    public static String userName;
    private static final Color MODIFIED_COLOR = new Color(160, 200, 255);
    private static final Color NEW_COLOR = new Color(180, 255, 180);
    private static final Color REMOVED_COLOR = new Color(255, 160, 180);
    
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
        String exec = Utilities.isUnix() ? "sh -c \"vlog\"" : "cmd /x /c \"vlog\"";
        try { if (Runtime.getRuntime().exec(exec).waitFor() != 0 ) return suite; }
        catch (Exception e) {}
        suite.addTest(new RegularDevelopment("testCheckoutFile"));
        suite.addTest(new RegularDevelopment("testModifyFile"));
        suite.addTest(new RegularDevelopment("testViewDifferences"));
        suite.addTest(new RegularDevelopment("testCheckinFile"));
        suite.addTest(new RegularDevelopment("testViewHistory"));
        suite.addTest(new RegularDevelopment("testUnlockFile"));
        suite.addTest(new RegularDevelopment("testGetMissingFile"));
        suite.addTest(new RegularDevelopment("testRemoveRevision"));
        suite.addTest(new RegularDevelopment("testCreateOwnRevision"));
        suite.addTest(new RegularDevelopment("testCheckoutRevision"));
        suite.addTest(new RegularDevelopment("testLockFile"));
        suite.addTest(new RegularDevelopment("testCreateBranch"));
        suite.addTest(new RegularDevelopment("testVersioningExplorer"));
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
    protected void setUp() {
        JellyProperties.setDefaults();
        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
    }
    
    /** Method called after each testcase. Resets Jemmy WaitComponentTimeout.
     */
    protected void tearDown() {
        JellyProperties.setDefaults();
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
    
    /** Tries to checkout a file from PVCS project database.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckoutFile() throws Exception {
        System.out.print(".. Testing checking out a file ..");
        String workingPath = getWorkDirPath();
        workingDirectory = workingPath.substring(0, workingPath.indexOf("RegularDevelopment")) + "RepositoryCreation" + File.separator + "testCreateDatabase";
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = null;
        try { A_FileNode = new Node( filesystemNode, "A_File [Current]"); }
        catch (TimeoutExpiredException e) { captureScreen("Error: Can't find A_File [Current] node."); }
        filesystemNode.select();
        new ComboBoxProperty(new PropertySheetOperator(), "Advanced Options").setValue("False");
        new Action(VERSIONING_MENU + "|" + GET, GET).perform(A_FileNode);
        GetCommandOperator getCommand = new GetCommandOperator("A_File.java");
        getCommand.checkLockForTheCurrentUser(true);
        getCommand.checkCheckOutWritableWorkfile(true);
        getCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        String children[] = filesystemNode.getChildren();
        int count = children.length;
        boolean found = false;
        for(int i=0; i<count; i++) {
            String child = children[i];
            if (child.startsWith("A_File [Current; 1.1] (") && child.endsWith(")")) {
                found = true;
                userName = child.substring(23, child.length() - 1);
            }
        }
        if (!found) captureScreen("Error: Unable to find locked A_File [Current; 1.1] (...) file.");
        new DeleteAction().perform(A_FileNode);
        new NbDialogOperator("Confirm Object Deletion").no();
        System.out.println(". done !");
    }

    /** Tries to modify a file in PVCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testModifyFile() throws Exception {
        System.out.print(".. Testing file modification ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current; 1.1] (" + userName + ")");
        BufferedWriter writer = new BufferedWriter(new FileWriter(workingDirectory + File.separator + "Work" + File.separator + "A_File.java"));
        writer.write("/** This is testing A_File.java file.\n */\n public class Testing_File {\n     int i;\n }\n");
        writer.flush();
        writer.close();
        new OpenAction().perform(A_FileNode);
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(A_FileNode);
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        A_FileNode = new Node( filesystemNode, "A_File [Locally Modified; 1.1] (" + userName + ")");
        System.out.println(". done !");
    }
    
    /** Tries to view differences of the modified file in PVCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testViewDifferences() throws Exception {
        System.out.print(".. Testing view differences ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Locally Modified; 1.1] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + DIFF, DIFF).perform(A_FileNode);
        EditorOperator editor = new EditorOperator("Diff: A_File.java");
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

    /** Tries to check in a file into PVCS project database.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckinFile() throws Exception {
        System.out.print(".. Testing checking in a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Locally Modified; 1.1] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + PUT, PUT).perform(A_FileNode);
        PutCommandOperator putCommand = new PutCommandOperator("A_File.java");
        putCommand.setChangeDescription("Three lines have changed.");
        putCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        A_FileNode = new Node(filesystemNode, "A_File [Current]");
        File A_File = new File(workingDirectory + File.separator + "Work" + File.separator + "A_File.java");
        if (A_File.canWrite()) captureScreen("Error: A_File.java remained read-write after check in.");
        System.out.println(". done !");
    }

    /** Tries to inspect PVCS information about a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testViewHistory() throws Exception {
        System.out.print(".. Testing history information of a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        new Action(VERSIONING_MENU + "|" + HISTORY, HISTORY).perform(A_FileNode);
        VCSCommandsOutputOperator outputWindow = new VCSCommandsOutputOperator("History");
        MainWindowOperator.getDefault().waitStatusText("Command History finished.");
        outputWindow.btClose();
        Thread.sleep(5000);
        String currentContents = outputWindow.txtStandardOutput().getText();
        outputWindow.close();
        String goldenContents = "Archive:          " + workingDirectory + File.separator + "Repo" + File.separator + "archives" + File.separator + "A_File.java-arc\nWorkfile:         A_File.java";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents.");
        goldenContents = "Owner:            " + userName + "\nLast trunk rev:   1.1\nLocks:            \nGroups:           \nRev count:        2\nAttributes:\n   WRITEPROTECT\n   CHECKLOCK\n   NOEXCLUSIVELOCK\n   EXPANDKEYWORDS\n   TRANSLATE\n   NOCOMPRESSDELTA\n   NOCOMPRESSWORKIMAGE\n   GENERATEDELTA\n   COMMENTPREFIX = \" * \"\n   NEWLINE = \"\\r\\n\"\nVersion labels:\nDescription:\nAuto-generated class file.\n\n-----------------------------------\nRev 1.1\nChecked in";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents.");
        goldenContents = "Author id: " + userName + "     lines deleted/added/moved: 2/2/0\nThree lines have changed.\n-----------------------------------\nRev 1.0\nChecked in:";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents.");
        goldenContents = "Author id: " + userName + "     lines deleted/added/moved: 0/0/0\nInitial revision.\n===================================";
        if (currentContents.indexOf(goldenContents) < 0) captureScreen("Error: Incorrect history contents.");
        System.out.println(". done !");
    }

    /** Tries to give up a lock on a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testUnlockFile() throws Exception {
        System.out.print(".. Testing giving up a lock on a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node C_FileNode = new Node( filesystemNode, "test [Current]|C_File [Current; 1.1] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + UNLOCK, UNLOCK).perform(C_FileNode);
        C_FileNode = new Node( filesystemNode, "test [Current]|C_File [Current]");
        System.out.println(". done !");
    }

    /** Tries to delete a file, map it as [Missing] and check it out again.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testGetMissingFile() throws Exception {
        System.out.print(".. Testing getting of a missing file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node( filesystemNode, "test [Current]");
        Node C_FileNode = new Node( testNode, "C_File [Current]");
        new DeleteAction().perform(C_FileNode);
        new NbDialogOperator("Confirm Object Deletion").yes();
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(testNode);
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        Node C_FileJavaNode = new Node( testNode, "C_File.java [Missing]");
        Node C_FileFormNode = new Node( testNode, "C_File.form [Missing]");
        new ActionNoBlock(VERSIONING_MENU + "|" + GET, GET).perform(new Node[] {C_FileJavaNode, C_FileFormNode});
        GetCommandOperator getCommand = null;
        long oldTimeout = JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 5000);
        try { getCommand = new GetCommandOperator("C_File.java ..."); }
        catch (TimeoutExpiredException ej) {
            try { getCommand= new GetCommandOperator("C_File.form ..."); }
            catch (TimeoutExpiredException ef) {
                JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
                captureScreen("Error: Can't find Get dialog.");
            }
        }
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
        getCommand.ok();
        Thread.sleep(10000);
        C_FileNode = new Node( testNode, "C_File [Current]");
        File C_File = new File(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "C_File.java");
        if (C_File.canWrite()) captureScreen("Error: C_File.java remained read-write after check out.");
        System.out.println(". done !");
    }

    /** Tries to remove the last revision of a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRemoveRevision() throws Exception {
        System.out.print(".. Testing last revision removal of a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        new Action(VERSIONING_MENU + "|" + REMOVE_REVISION, REMOVE_REVISION).perform(D_FileNode);
        new QuestionDialogOperator("Are you sure you want to remove the last revision of the file \"D_File.java\"?").yes();
        RemoveCommandOperator removeCommand = new RemoveCommandOperator("D_File.java");
        removeCommand.ok();
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The last revision of the file \"D_File.java\" was removed successfully.");
        information.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        String status = Utilities.isWindows() ? "Locally Modified" : "Current"; // Workaround until #27634 is fixed.
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [" + status + "]");
        System.out.println(". done !");
    }

    /** Tries to create new revision of a file and assign it own number.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCreateOwnRevision() throws Exception {
        System.out.print(".. Testing own revision creation on a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        String status = Utilities.isWindows() ? "Locally Modified" : "Current"; // Workaround until #27634 is fixed.
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [" + status + "]");
        filesystemNode.select();
        new ComboBoxProperty(new PropertySheetOperator(), "Advanced Options").setValue("True");
        new Action(VERSIONING_MENU + "|" + PUT + "...", PUT + "...").perform(D_FileNode);
        PutCommandOperator putCommand = new PutCommandOperator("D_File.java");
        putCommand.setChangeDescription("Assigning own number.");
        putCommand.checkCheckTheWorkfileInAndImmediatelyOut(true);
        putCommand.setAssignARevisionNumber("2.0");
        putCommand.setAssignAVersionLabel("My_Version");
        putCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        System.out.println(". done !");
    }

    /** Tries to get specific revision of a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckoutRevision() throws Exception {
        System.out.print(".. Testing checkout of specific revision ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        new Action(VERSIONING_MENU + "|" + GET + "...", GET + "...").perform(D_FileNode);
        GetCommandOperator getCommand = new GetCommandOperator("D_File.java");
        getCommand.setSpecificRevision("2.0");
        getCommand.checkSetTheDateAndTimeOfTheFileToTheCurrentTime(true);
        getCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        if (!D_FileNode.isPresent()) captureScreen("Error: Unable to find D_File [Current]");
        File D_File = new File(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "another" + File.separator + "D_File.java");
        Date fileTime = new Date(D_File.lastModified());
        Date currentTime = new Date();
        long timeGap = currentTime.getTime() - fileTime.getTime();
        if (timeGap > 10000) captureScreen("Error: Unable to set current time during checkout.");
        System.out.println(". done !");
    }

    /** Tries to lock a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testLockFile() throws Exception {
        System.out.print(".. Testing lockout of a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        filesystemNode.select();
        new ComboBoxProperty(new PropertySheetOperator(), "Advanced Options").setValue("False");
        new Action(VERSIONING_MENU + "|" + LOCK, LOCK).perform(D_FileNode);
        Thread.sleep(10000);
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current; 2.1] (" + userName + ")");
        System.out.println(". done !");
    }

    /** Tries to create a branch on a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCreateBranch() throws Exception {
        System.out.print(".. Testing branch creation on a file ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current; 2.1] (" + userName + ")");
        filesystemNode.select();
        new ComboBoxProperty(new PropertySheetOperator(), "Advanced Options").setValue("True");
        new Action(VERSIONING_MENU + "|" + PUT + "...", PUT + "...").perform(D_FileNode);
        PutCommandOperator putCommand = new PutCommandOperator("D_File.java");
        putCommand.setChangeDescription("Starting new branch.");
        putCommand.checkCheckInTheWorkfileEvenIfUnchanged(true);
        putCommand.checkApplyALockOnCheckout(true);
        putCommand.setAssignAVersionLabel("MyBranch");
        putCommand.checkFloatLabelWithTheTipRevision(true);
        putCommand.checkStartABranch(true);
        putCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current; 2.0.1.1] (" + userName + ")");
        System.out.println(". done !");
    }

    /** Tries to invoke versioning explorer and check all revisions.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVersioningExplorer() throws Exception {
        System.out.print(".. Testing versioning explorer ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current; 2.0.1.1] (" + userName + ")");
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(D_FileNode);
        MainWindowOperator.getDefault().waitStatusText("Command REVISION_LIST finished.");
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        D_FileNode = new Node(filesystemNode, "test [Current]|another [Current]|D_File.java [Current; 2.0.1.1] (" + userName + ")");
        Thread.sleep(3000);
        new Node(D_FileNode, "2.0  Assigning own number.|2.0.1|2.0.1.0  Starting new branch.").select();
        versioningExplorer.close();
        System.out.println(". done !");
    }
}