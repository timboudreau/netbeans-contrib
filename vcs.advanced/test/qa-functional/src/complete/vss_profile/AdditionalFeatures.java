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
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.modules.vcsgeneric.vss.*;
import org.netbeans.jellytools.modules.vcscore.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.*;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.properties.*;
import javax.swing.text.*;
import java.awt.Color;
import java.util.Date;

/** XTest / JUnit test class performing additional features testing on VSS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AdditionalFeatures extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String CHECK_OUT = "VSS|Check Out";
    public static String CHECK_IN = "VSS|Check In";
    public static String DIFF = "Diff";
    public static String REFRESH_REVISIONS = "Refresh Revisions";
    public static String VERSIONING_EXPLORER = "Versioning Explorer";
    public static String ADD_TO_GROUP = "Include in VCS Group|<Default Group>";
    public static String VCS_GROUPS = "VCS Groups";
    public static String workingDirectory;
    public static String userName;
    private static final Color MODIFIED_COLOR = new Color(160, 200, 255);
    private static final Color NEW_COLOR = new Color(180, 255, 180);
    private static final Color REMOVED_COLOR = new Color(255, 160, 180);
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AdditionalFeatures(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return AdditionalFeatures test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String zipFile = "C:\\Program Files\\Microsoft Visual Studio\\vss.zip";
        if (!new File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTest(new AdditionalFeatures("testViewOldRevision"));
        suite.addTest(new AdditionalFeatures("testCompareRevisions"));
        suite.addTest(new AdditionalFeatures("testAddToGroup"));
        suite.addTest(new AdditionalFeatures("testCheckinGroup"));
        suite.addTest(new AdditionalFeatures("testVerifyGroup"));
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
    
    /** Tries to view an old revision of a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testViewOldRevision() throws Exception {
        System.out.print(".. Testing view old revision ..");
        String workingPath = getWorkDirPath();
        workingDirectory = workingPath.substring(0, workingPath.indexOf("AdditionalFeatures")) + "RepositoryCreation" + File.separator + "testCreateProjects";
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(A_FileNode);
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        new Node(filesystemNode, "A_File.java [Current]|2  Three lines have changed.").select();
        new OpenAction().perform(new Node(filesystemNode, "A_File.java [Current]|1  Generated A_File class."));
        versioningExplorer.close();
        String editorContents = new EditorOperator("A_File.java 1.0").getText();
        if (!editorContents.equals("/** This is testing file.\n */\n\n public class Testing_File {\n }\n"))
            captureScreen("Error: Incorrect version of A_File was opened.");
        System.out.println(". done !");
    }

    /** Tries to compare two old revisions of a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCompareRevisions() throws Exception {
        System.out.print(".. Testing two revisions comparison ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(A_FileNode);
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        new Action(null, DIFF).perform(new Node(filesystemNode, "A_File.java [Current]|1  Generated A_File class."));
        versioningExplorer.close();
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

    /** Tries to add a file into VCS group.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddToGroup() throws Exception {
        System.out.print(".. Testing file addition to VCS group ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current]");
        new Action(VERSIONING_MENU + "|" + ADD_TO_GROUP, ADD_TO_GROUP).perform(B_FileNode);
        new Action(VERSIONING_MENU + "|" + VCS_GROUPS, null).performMenu();
        VCSGroupsFrameOperator groupsWindow = new VCSGroupsFrameOperator();
        new Node(groupsWindow.treeVCSGroupsTreeView(), "<Default Group>|B_File [Current]").select();
        groupsWindow.close();
        System.out.println(". done !");
    }

    /** Tries to do checkin from VCS group.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckinGroup() throws Exception {
        System.out.print(".. Testing checking in from VCS group ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current]");
        new Action(VERSIONING_MENU + "|" + CHECK_OUT, CHECK_OUT).perform(B_FileNode);
        Thread.sleep(2000);
        BufferedWriter writer = new BufferedWriter(new FileWriter(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "B_File.java"));
        writer.write("/** This is testing B_File.java file.\n */\n public class B_File {\n     int i = 1;\n }\n");
        writer.flush();
        writer.close();
        new Action(VERSIONING_MENU + "|" + VCS_GROUPS, null).performMenu();
        VCSGroupsFrameOperator groupsWindow = new VCSGroupsFrameOperator();
        Node defaultGroup = new Node(groupsWindow.treeVCSGroupsTreeView(), "<Default Group>");
        defaultGroup.select();
        Thread.sleep(3000);
        new TextFieldProperty(new PropertySheetOperator(), "Description").setValue("Checked in from VCS group.");
        defaultGroup.select();
        new Action(null, CHECK_IN).performPopup(defaultGroup);
        CheckinCommandOperator checkinCommand = new CheckinCommandOperator("B_File.java");
        String changeDescription = checkinCommand.getChangeDescription();
        checkinCommand.setChangeDescription("Checked in from VCS group.");
        checkinCommand.ok();
        if (!changeDescription.equals("Checked in from VCS group. \n"))
            captureScreen("Error: Group description was not propagated into checkin dialog. Required: |Checked in from VCS group. \n|" + changeDescription + "|");
        Thread.sleep(2000);
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(B_FileNode);
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        new Action(null, REFRESH_REVISIONS).perform(new Node(filesystemNode, "test [Current]|B_File.java [Current]"));
        MainWindowOperator.getDefault().waitStatusText("Command REVISION_LIST finished.");
        new Node(filesystemNode, "test [Current]|B_File.java [Current]|2  Checked in from VCS group.").select();
        versioningExplorer.close();
        System.out.println(". done !");
    }

    /** Tries to verify and correct VCS group.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVerifyGroup() throws Exception {
        System.out.print(".. Testing VCS group verification ..");
        new Action(VERSIONING_MENU + "|" + VCS_GROUPS, null).performMenu();
        VCSGroupsFrameOperator groupsWindow = new VCSGroupsFrameOperator();
        groupsWindow.verifyVCSGroup("<Default Group>");
        GroupVerificationOperator verifyDialog = new GroupVerificationOperator();
        verifyDialog.checkRemoveFilesFromGroup(true);
        if (!verifyDialog.tabNotChangedFiles().getModel().getValueAt(0, 0).equals("B_File"))
            captureScreen("Error: B_File [Current] was not taken for correction.");
        verifyDialog.correctGroup();
        Thread.sleep(2000);
        if (new Node(groupsWindow.treeVCSGroupsTreeView(), "<Default Group>").getChildren().length != 0)
            captureScreen("Error: VCS group was not corrected.");
        groupsWindow.close();
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        new UnmountFSAction().perform(filesystemNode);
        Thread.currentThread().sleep(5000);
        if (filesystemNode.isPresent()) captureScreen("Error: Unable to unmount filesystem.");
        System.out.println(". done !");
    }
}